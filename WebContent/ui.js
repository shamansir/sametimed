var DEFAULT_CLIENTS_HOLDER_ID = 'client-views';

/*
 * waveModelStr format:
 * 
 * client = {
 * 		clientId: <int>,
 * 		info: <string>, // information line
 * 		inbox: {<int>: <inboxObj>, ...}, // waves list, inbox number to wave id string
 * 		users: [<string>, ...], // full addresses, one by one   
 * 		console: [<string>, ...], // console history
 * 		errors: [<string>, ...], // errors happend while using the client
 * 
 *      // optional modules 
 * 		chat: [{author: <string>, text: <string>}, ...], // chat lines
 * 		document: 
 * 			[{id: <int>, text: <string>, style: <string>, author: <string>,  
 *            reserved: boolean, size: <int>}, ...}], // document chunks
 * }
 * 
 * inboxObj = {
 * 		new: <boolean>,
 * 		current: <boolean>,
 * 		id: <string>,
 * 		digest: <string>
 * }
 */

/* =====================/ RENDERING FUNCTIONS /============================== */

function renderClient(waveModelObj, holder) {
	//var waveModelObj = JSON.parse(waveModelStr);
	if (!waveModelObj.error) {
		var clientsHolder = holder ? holder : $('#' + DEFAULT_CLIENTS_HOLDER_ID);
		clientsHolder.append(clientRenderer.createClient(waveModelObj));
	} else {
		showGeneralError(waveModelObj.error);
	}
}

function renderUpdate(updateObj) {
	if (updateObj) {
		clientRenderer.renderUpdate(updateObj);
	} else {
		showGeneralError('received null update');
	}
}

/* ==========================/ IBOXRENDER /================================== */

var IBoxRenderer = $.inherit({
	
	renderBox: function(clientId, moduleData, chatModel) {},
	
	doAutoScroll: function() { return false; },
	
	prepareUpdate: function(clientId) {},
	
	beforeUpdate: function(clientId) {},
	
	afterUpdate: function(clientId) {}
		
});

/* ========================/ CLIENT RENDERER /=============================== */

var ClientRenderer = $.inherit({
			
	__constructor: function(configuration) {
		this.modules = configuration.modules;		
		
		// preparing blocks aliases and blocks data object to use on render
		this.blocksAliases = [];
		this.blocksData = {}; // object is unsorted hash and array
						      // is strictly sorted, that's why references
						      // to blocks data are stored in two different
							  // variables: one for quick access and one
		                      // for keeping order
		
		for (blockDataId in this.__self.BLOCKS) {
			var blockData = this.__self.BLOCKS[blockDataId];
			this.blocksAliases.push(blockData.alias);
			this.blocksData[blockData.alias] = blockData;
		}
		
		// preparing render order array 
		this.renderOrder = [];
		
		var modulesRenderOrder = {}; // map: { <block-before-modules-alias> : 
										  //   [<module-alias>, <module-alias>, ...] }
		
		for (moduleId in this.modules) {
			var renderAfter = this.modules[moduleId].renderAfter;
			if (!renderAfter) renderAfter = this.blocksAliases[this.blocksAliases.length - 1]; 
				// if no block specified to render after, then render after the last block 
			if (!modulesRenderOrder[renderAfter]) modulesRenderOrder[renderAfter] = [];
			modulesRenderOrder[renderAfter].push(moduleId);
		}			
		
		for (blockAliasId in this.blocksAliases) {
			var blockAlias = this.blocksAliases[blockAliasId]; 
			this.renderOrder.push(blockAlias); // render block itself
			// render modules after the block
			for (moduleAliasId in modulesRenderOrder[blockAlias]) {
				this.renderOrder.push(modulesRenderOrder[blockAlias][moduleAliasId]);
			}
		}		
	},
	
	createClient: function(waveModel) {
	
		var clientId = waveModel.clientId;
		
		var clientWrapper = $('<div />')
			.attr('id', this.__self.CLIENT_HOLDER_PREFIX + clientId)
			.addClass(this.__self.CLIENT_CLASS);
				
		for (modelAliasId in this.renderOrder) {
			
			var modelAlias = this.renderOrder[modelAliasId];
		
			var model = waveModel[modelAlias];
			var blockData = this.blocksData[modelAlias];
			
			if (blockData) { // if it a model for inner block...
				// ...render it by itself
				clientWrapper.append(
						this[blockData.renderer](clientId, blockData, model));
						// ATTENTION: calling a rendering method here
				blockAutoScroll = blockData.autoScroll;
			} else if (this.modules[modelAlias]) { // if this is a module model
				var moduleData = this.modules[modelAlias];
				var moduleRenderer = moduleData.renderer;
				// ...render it with module's renderer
				clientWrapper.append(
					moduleRenderer.renderBox(clientId, 
						                     this.prepareModuleData(clientId, modelAlias, moduleData), 
						                     model));
				blockAutoScroll = moduleRenderer.doAutoScroll();
			}			
		}
		
		return clientWrapper;
	},

	createInfoLine: function(clientId, blockData, infoLineStr) {
		return $('<span />')
				.attr('id', this.getModelHolderId(blockData.alias, clientId))
				.addClass(blockData.styleClass)
				.text(infoLineStr);
	},
	
	createInbox: function(clientId, blockData, inboxModel) {
		var inboxWrapper = $('<ol />')
				.attr('id', this.getModelHolderId(blockData.alias, clientId))
				.addClass(blockData.styleClass);
		
		if (inboxModel.length == 0) {
			inboxWrapper.append($('<li />').addClass('empty'));
		}
		
		for (inboxId in inboxModel) {
			var entryData = inboxModel[inboxId];
			var liElm = $('<li />')
					.append($('<span />').addClass('inbox-id').text(inboxId))
					.append($('<span />').addClass('inbox-wave-id').text(
														entryData.id))
			 	    .append($('<span />').addClass('inbox-wave-digest').text(
													 entryData.digest.substr(this.DIGEST_MAX_LENGTH)));
			if (entryData.unread)  liElm.addClass('inbox-unread');
			if (entryData.current) liElm.addClass('inbox-current');
			inboxWrapper.append(liElm);
		}	
		
		return inboxWrapper;		
	},
	
	createUsersList: function(clientId, blockData, usersModel) {
		var userslistWrapper = $('<ul />')
				.attr('id', this.getModelHolderId(blockData.alias, clientId))
				.addClass(blockData.styleClass);
		
		if (usersModel.length == 0) {
			userslistWrapper.append($('<li />').addClass('empty'));
		}
		
		for (userId in usersModel) {
			userslistWrapper.append($('<li />')
				.append($('<span />').addClass('user').text(usersModel[userId])));
		}	
		
		return userslistWrapper;		
	},
		
	createConsole: function(clientId, blockData, consoleLines) {
		var inputElmId = 'console-input-' + clientId;
		
		var consoleWrapper = $('<form />')
				.attr('id', this.getModelHolderId(blockData.alias, clientId))
				.attr('action', null)
				.attr('method', 'post')
				.addClass(blockData.styleClass);
		
		consoleWrapper.append($('<input />')
				.attr('id', inputElmId)
				.attr('type', 'text')
				.attr('onkeydown', 'return blockEnter(event);'));
				
		consoleWrapper.append($('<input />')
				.attr('type', 'button')
				.attr('title', 'send')
				.attr('value', 'send')
				.attr('onclick', 'return ' + this.__self.CONSOLE_BTN_HANDLER +
						'(' + clientId + ',\'' + inputElmId + '\')'));
				/* .click(...)); */		
		
		return consoleWrapper;		
	},
	
	createErrorBox: function(clientId, blockData, errorsLines) {
		var errboxWrapper = $('<div />')
				.attr('id', this.getModelHolderId(blockData.alias, clientId))
				.addClass(blockData.styleClass);
		
		for (errorLineIdx in errorsLines) {
			errboxWrapper.append($('<span />').addClass('error-line').text(
					errorsLines[errorLineIdx]));
		}	
		
		return errboxWrapper;			
	},
	
	getModelHolderId: function(modelAlias, clientId) {
		return this.__self.IDS_PREFIX + clientId + this.blocksData[modelAlias].postfix;
	},
	
	prepareModuleData: function(clientId, modelAlias, moduleData) {
		moduleData.holderId = this.__self.IDS_PREFIX + clientId + '-' + modelAlias;
		if (!moduleData.styleClass) moduleData.styleClass = modelAlias;
		return moduleData;
	},
	
	renderUpdate: function(updateModel) {
		var clientId = updateModel.clientId;
		var modelType = updateModel.modelType;
		var model = updateModel.modelValue;
		var blockData = this.blocksData[modelType];		
		
		var modelWrapper = null;
		var blockAutoScroll = false;
		var holderId = null;
		
		var prepareUpdateCallback = null;
		var beforeUpdateCallback = null;
		var afterUpdateCallback = null;
		
		if (blockData) { // if it is the inner block to update
			holderId = this.getModelHolderId(modelType, clientId); 
			modelWrapper = this[blockData.renderer](clientId, blockData, model);
			 	           // ATTENTION: calling a rendering method here			
			blockAutoScroll = blockData.autoScroll;
		} else if (this.modules[modelType]) { // if it is the module to update
			var moduleData = this.prepareModuleData(clientId, modelType, this.modules[modelType]);
			var moduleRenderer = moduleData.renderer;			
			holderId = moduleData.holderId;
			
			prepareUpdateCallback = createMethodReference(moduleRenderer, moduleRenderer.prepareUpdate);
			beforeUpdateCallback  = createMethodReference(moduleRenderer, moduleRenderer.beforeUpdate);
			afterUpdateCallback   = createMethodReference(moduleRenderer, moduleRenderer.afterUpdate);
			
			if (prepareUpdateCallback) prepareUpdateCallback(clientId);
			
			modelWrapper = moduleRenderer.renderBox(clientId, moduleData, model);
			blockAutoScroll = moduleRenderer.doAutoScroll();
		}
		
		if (modelWrapper != null) {
			if (beforeUpdateCallback) beforeUpdateCallback(clientId); 
			$('#' + holderId).replaceWith(modelWrapper);
			if (blockAutoScroll) {
				modelWrapper.attr("scrollTop", modelWrapper.height());
			}
			if (afterUpdateCallback) afterUpdateCallback(clientId);
		}		

	}
		
}, { // static
	
	CLIENT_CLASS: 'wave-client',
	
	CONSOLE_BTN_HANDLER: 'sendButtonOnClick',
	
	BLOCKS: [{ alias: 'info',
		       styleClass: 'infoline',
		       postfix: '-infoline',
		       renderer: 'createInfoLine',
		       autoScroll: false
		     }, 
		     { alias: 'inbox',
			   styleClass: 'inbox',
			   postfix: '-inbox',
			   renderer: 'createInbox',
			   autoScroll: true
			 }, 
			 { alias: 'users',
			   styleClass: 'userlist',
			   postfix: '-userlist',
			   renderer: 'createUsersList',
			   autoScroll: true
			 },
			 { alias: 'console',
			   styleClass: 'console',
			   postfix: '-console',
			   renderer: 'createConsole',
			   autoScroll: false
			 },			 
			 { alias: 'errors',
			   styleClass: 'errorbox',
			   postfix: '-errorbox',
			   renderer: 'createErrorBox',
			   autoScroll: true
			 }],
	
	CLIENT_HOLDER_PREFIX: 'wave-client-',	
	IDS_PREFIX: 'client-',
	
	DIGEST_MAX_LENGTH: 14 // in symbols
	
});
