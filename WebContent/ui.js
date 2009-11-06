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
		$('#error')
			.removeClass('no-errors')
			.addClass('have-errors')
			.append($('<span />').text(waveModelObj.error));
	}
}

function renderUpdate(updateObj) {
	clientRenderer.renderUpdate(updateObj);
}

/* ==============================/ UTILS /=================================== */

function blockEnter(evt) {
    evt = (evt) ? evt : event;
    var charCode = (evt.charCode) ? evt.charCode :((evt.which) ? evt.which : evt.keyCode);
    if (charCode == 13) {
        return false;
    } else {
        return true;
    }
}

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
		if (blockData) {
			holderId = this.getModelHolderId(modelType, clientId); 
			modelWrapper = this[blockData.renderer](clientId, blockData, model);
			 	           // ATTENTION: calling a rendering method here			
			blockAutoScroll = blockData.autoScroll;
		} else if (this.modules[modelType]) {
			var moduleData = this.prepareModuleData(clientId, modelType, this.modules[modelType]);
			var moduleRenderer = moduleData.renderer;			
			holderId = moduleData.holderId;	
			modelWrapper = moduleRenderer.renderBox(clientId, moduleData, model);
			blockAutoScroll = moduleRenderer.doAutoScroll();
		}
		
		if (modelWrapper != null) {
			$('#' + holderId).replaceWith(modelWrapper);
			if (blockAutoScroll) {
				modelWrapper.attr("scrollTop", modelWrapper.height());
			}
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


var IBoxRenderer = $.inherit({
	
	renderBox: function(clientId, moduleData, chatModel) {}
		
});

/* ============================/ CHAT BOX /================================== */

var ChatBoxRenderer = $.inherit(
	
	IBoxRenderer, {
	
	__constructor: function() {},
	
	renderBox: function(clientId, moduleData, chatModel) {
		var chatWrapper = $('<div />')
				.attr('id', moduleData.holderId)
				.addClass(moduleData.styleClass);
		
		for (chatLineIdx in chatModel) {
			var chatLine = chatModel[chatLineIdx];
			var chatLineElm = $('<span />').addClass('chat-line');
			chatLineElm.append($('<span />').addClass('author').text(chatLine.author));
			chatLineElm.append($('<span />').addClass('line').text(chatLine.text));
			chatWrapper.append(chatLineElm);
		}	
		
		return chatWrapper;
	},
	
	doAutoScroll: function() { return true; }
	
});

/* ==========================/ EDITOR BOX /================================== */

var EditorBoxRenderer = $.inherit(
		
	
	IBoxRenderer, {
	
	__constructor: function() {},
	
	renderBox: function(clientId, moduleData, documentModel) {
		var editorWrapper = $('<div />')
				.attr('id', moduleData.holderId)
				.addClass(moduleData.styleClass);
		
		var editorElmId = 'editor-content-' + clientId;
		
		// FIXME: do not draw buttons every time
		var buttonsContainer = $('<ul />')
				.addClass('editor-buttons');		
		for (blockIdx in this.__self.EDITOR_BUTTONS) {
			var blockData = this.__self.EDITOR_BUTTONS[blockIdx];
			var blockWrapper = $('<li />');
			var buttonsBlock = $('<ul />')
					.addClass('editor-buttons-block');
			for (buttonAlias in blockData) {
				var buttonData = blockData[buttonAlias];
				var buttonWrapper = $('<li />');
				var button = $('<a />')
							.attr('id', 'editor-' + clientId + '-button' + buttonData.id_postfix)
							.attr('href', '#')
							.attr('title', buttonData.name)
							.attr('onclick', 'return ' + this.__self.CMD_BTN_HANDLER + "(" + clientId + ",'" + buttonData.cmd + "','" + editorElmId + "')")
							.addClass('editor-button')
							.text(buttonData.text);
				buttonWrapper.append(button);
				buttonsBlock.append(buttonWrapper);
			}
			blockWrapper.append(buttonsBlock);
			buttonsContainer.append(blockWrapper);
		}
		editorWrapper.append(buttonsContainer);
		
		var wikiEditingArea =  $('<textarea />')
				.addClass('editor-wiki-area')
				.attr('id', editorElmId);
		// new EditorController(); attach documentModel + clientId
		for (textChunkIdx in documentModel) {
			var textChunk = documentModel[textChunkIdx];
			wikiEditingArea.append(textChunk.text);			
			// textChunk.id; // textChunk.style; // textChunk.size; 
			// textChunk.reserved; // textChunk.author;
		}
		wikiEditingArea.keydown(onEditorKeyDownFunc);
		wikiEditingArea.keyup(onEditorKeyUpFunc);
		editorWrapper.append(wikiEditingArea);
		
		var documentTextArea =  $('<div />')
			.addClass('editor-document')
			.attr('id', editorElmId);
		// new EditorController(); attach documentModel + clientId
		for (textChunkIdx in documentModel) {
			var textChunk = documentModel[textChunkIdx];
			wikiEditingArea.append(textChunk.text);
			// textChunk.style;
		}
		editorWrapper.append(documentTextArea);		
		
		/*
		for (textChunkIdx in documentModel) {
			var textChunk = documentModel[textChunkIdx];
			editorWrapper.append($('<span />').addClass('chunk').text(textChunk.text));
			// textChunk.style;
		} */
		
		return editorWrapper;		
	},
	
	doAutoScroll: function() { return false; }
	
}, {  // static
	
	CMD_BTN_HANDLER: 'cmdButtonOnClick',
	
	EDITOR_BUTTONS: [
 		{ 'bold': { id_postfix: '-bold', name: 'Bold', cmd: 'bold', text: 'B' },
 		  'italic': { id_postfix: '-italic', name: 'Italic', cmd: 'italic', text: 'I' },
 		  'underline': { id_postfix: '-uline', name: 'Underline', cmd: 'uline', text: 'U' } },
 		{ 'left': { id_postfix: '-left', name: 'Align Left', cmd: 'left', text: 'L' },
 		  'center': { id_postfix: '-center', name: 'Center Text', cmd: 'center', text: 'C' },
 		  'right': { id_postfix: '-right', name: 'Align Right', cmd: 'right', text: 'R' },
 		  'justify': { id_postfix: '-jtify', name: 'Justify Text', cmd: 'jtify', text: 'J'} },
 		{ 'put': { id_postfix:'-put', name: 'Put Text', cmd: 'put', text:'Put'} } ]	
	
});