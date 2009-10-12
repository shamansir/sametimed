var DEFAULT_CLIENTS_HOLDER_ID = 'client-views';

/*
 * waveModelStr format:
 * 
 * client = {
 * 		clientId: <int>,
 * 		info: <string>, // information line
 * 		inbox: {<int>: <inboxObj>, ...}, // waves list, inbox number to wave id string
 * 		users: [<string>, ...], // full addresses, one by one   
 * 		chat: [{author: <string>, text: <string>}, ...], // chat lines
 * 		document: [{text: <string>, style: <string>}, ...}], // document chunks
 * 		console: [<string>, ...], // console history
 * 		errors: [<string>, ...], // errors happend while using the client
 * }
 * 
 * inboxObj = {
 * 		new: <boolean>,
 * 		current: <boolean>,
 * 		id: <string>,
 * 		digest: <string>
 * }
 */

function renderClient(waveModelStr, holder) {
	var waveModelObj = JSON.parse(waveModelStr);
	if (!waveModelObj.error) {
		var clientsHolder = holder ? holder : $('#' + DEFAULT_CLIENTS_HOLDER_ID);
		clientsHolder.append(ClientRenderer.createClient(waveModelObj));
	} else {
		$('#error')
			.removeClass('no-errors')
			.addClass('have-errors')
			.append($('<span />').text(waveModelObj.error));
	}
}

function renderUpdate(updateObj) {
	ClientRenderer.renderUpdate(updateObj);
}

// use jquery.inherit plugin
var ClientRenderer = {
		
	SEND_BTN_HANDLER: 'sendButtonOnClick',
	CMD_BTN_HANDLER: 'cmdButtonOnClick',
	
	CLIENT_HOLDER_PREFIX: 'wave-client-',	
	IDS_PREFIX: 'client-',	
	MODELS_POSTFIXES: {
		info: "-infoline",
		inbox: "-inbox",
		users: "-userlist",
		chat: "-chat",
		document: "-editor",
		console: "-console",
		errors: "-errorbox"
	},
	
	EDITOR_BUTTONS: [
		{ 'bold': { id_postfix: '-bold', name: 'Bold', cmd: 'bold', text: 'B' },
		  'italic': { id_postfix: '-italic', name: 'Italic', cmd: 'italic', text: 'I' },
		  'underline': { id_postfix: '-uline', name: 'Underline', cmd: 'uline', text: 'U' } },
		{ 'left': { id_postfix: '-left', name: 'Align Teft', cmd: 'left', text: 'L' },
		  'center': { id_postfix: '-center', name: 'Center Text', cmd: 'center', text: 'C' },
		  'right': { id_postfix: '-right', name: 'Align Right', cmd: 'right', text: 'R' },
		  'justify': { id_postfix: '-jtify', name: 'Justify Text', cmd: 'jtify', text: 'J'} } 
	],
	
	DIGEST_MAX_LENGTH: 14, // in symbols
	
	// TODO: store models renderers using createMethodReference
		
	createClient: function(waveModel) {
	
		var clientId = waveModel.clientId;
		
		var clientWrapper = $('<div />')
			.attr('id', this.CLIENT_HOLDER_PREFIX + clientId)
			.addClass('wave-client');
		
		// TODO: store elements as id -> element hash
		clientWrapper.append(this.createInfoLine(clientId, waveModel.info));
		clientWrapper.append(this.createInbox(clientId, waveModel.inbox));
		clientWrapper.append(this.createUsersList(clientId, waveModel.users));
		
		if (waveModel.chat)     clientWrapper.append(this.createChat(clientId, waveModel.chat));
		if (waveModel.document) clientWrapper.append(this.createEditor(clientId, waveModel.document));
		
		clientWrapper.append(this.createConsole(clientId, waveModel.console));
		clientWrapper.append(this.createErrorBox(clientId, waveModel.errors));
		
		return clientWrapper;
	},

	createInfoLine: function(clientId, infoLineStr) {
		return $('<span />')
				.attr('id', this.getModelHolderId("info", clientId))
				.addClass('infoline')
				.text(infoLineStr);
	},
	
	createInbox: function(clientId, inboxModel) {
		var inboxWrapper = $('<ol />')
				.attr('id', this.getModelHolderId("inbox", clientId))
				.addClass('inbox');
		
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
	
	createUsersList: function(clientId, usersModel) {
		var userslistWrapper = $('<ul />')
				.attr('id', this.getModelHolderId("users", clientId))
				.addClass('userlist');
		
		if (usersModel.length == 0) {
			userslistWrapper.append($('<li />').addClass('empty'));
		}
		
		for (userId in usersModel) {
			userslistWrapper.append($('<li />')
				.append($('<span />').addClass('user').text(usersModel[userId])));
		}	
		
		return userslistWrapper;		
	},
	
	createChat: function(clientId, chatModel) {
		var chatWrapper = $('<div />')
				.attr('id', this.getModelHolderId("chat", clientId))
				.addClass('chat');
		
		for (chatLineIdx in chatModel) {
			var chatLine = chatModel[chatLineIdx];
			var chatLineElm = $('<span />').addClass('chat-line');
			chatLineElm.append($('<span />').addClass('author').text(chatLine.author));
			chatLineElm.append($('<span />').addClass('line').text(chatLine.text));
			chatWrapper.append(chatLineElm);
		}	
		
		return chatWrapper;
	},
	
	createEditor: function(clientId, documentModel) {
		var editorWrapper = $('<div />')
				.attr('id', this.getModelHolderId("document", clientId))
				.addClass('editor');
		
		var buttonsContainer = $('<ul />')
				.addClass('editor-buttons');		
		for (blockIdx in this.EDITOR_BUTTONS) {
			var blockData = this.EDITOR_BUTTONS[blockIdx];
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
							.attr('onclick', 'return ' + this.CMD_BTN_HANDLER + "(" + clientId + ",'" + buttonData.cmd + "')")
							.addClass('editor-button')
							.text(buttonData.text);
				buttonWrapper.append(button);
				buttonsBlock.append(buttonWrapper);
			}
			blockWrapper.append(buttonsBlock);
			buttonsContainer.append(blockWrapper);
		}
		editorWrapper.append(buttonsContainer);
		
		for (textChunkIdx in documentModel) {
			var textChunk = documentModel[textChunkIdx];
			editorWrapper.append($('<span />').addClass('chunk').text(textChunk.text));
			// textChunk.style;
		}	
		
		return editorWrapper;		
	},
	
	createConsole: function(clientId, consoleLines) {
		var inputElmId = 'console-input-' + clientId;
		
		var consoleWrapper = $('<form />')
				.attr('id', this.getModelHolderId("console", clientId))
				.attr('action', null)
				.attr('method', 'post')
				.addClass('console');
		
		consoleWrapper.append($('<input />')
				.attr('id', inputElmId)
				.attr('type', 'text')
				.addClass('gwt-TextBox')
				.attr('onkeydown', 'return blockEnter(event);'));
				
		consoleWrapper.append($('<input />')
				.attr('type', 'button')
				.attr('title', 'send')
				.attr('value', 'send')
				.addClass('gwt-Button')
				.attr('onclick', 'return ' + this.SEND_BTN_HANDLER +
						'(' + clientId + ',\'' + inputElmId + '\')'));
				/* .click(...)); */		
		
		return consoleWrapper;		
	},
	
	createErrorBox: function(clientId, errorsLines) {
		var errboxWrapper = $('<div />')
				.attr('id', this.getModelHolderId("errors", clientId))
				.addClass('errorbox');
		
		for (errorLineIdx in errorsLines) {
			errboxWrapper.append($('<span />').addClass('error-line').text(
					errorsLines[errorLineIdx]));
		}	
		
		return errboxWrapper;			
	},
	
	getModelHolderId: function(modelAlias, clientId) {
		return this.IDS_PREFIX + clientId + this.MODELS_POSTFIXES[modelAlias];
	},
	
	renderUpdate: function(updateModel) {
		var clientId = updateModel.clientId;
		var modelType = updateModel.modelType;
		var model = updateModel.modelValue;
		
		var modelWrapper = null;
		if (modelType == 'info') {
			modelWrapper = this.createInfoLine(clientId, model);
		} else if (modelType == 'inbox') {
			modelWrapper = this.createInbox(clientId, model);
		} else if (modelType == 'users') {
			modelWrapper = this.createUsersList(clientId, model);
		} else if (modelType == 'chat') {
			modelWrapper = this.createChat(clientId, model);
		} else if (modelType == 'document') {
			modelWrapper = this.createEditor(clientId, model);
		} else if (modelType == 'console') {
			modelWrapper = this.createConsole(clientId, model);
		} else if (modelType == 'errors') {
			modelWrapper = this.createErrorBox(clientId, model);
		}
		
		if (modelWrapper != null) {
			var holderId = this.getModelHolderId(modelType, clientId);
			$('#' + holderId).replaceWith(modelWrapper);
			if ((modelType == 'inbox') || 
				(modelType == 'users') ||
				(modelType == 'chat')) {
				modelWrapper.attr("scrollTop", modelWrapper.height());
			}
		}

	}
		
}

function blockEnter(evt) {
    evt = (evt) ? evt : event;
    var charCode = (evt.charCode) ? evt.charCode :((evt.which) ? evt.which : evt.keyCode);
    if (charCode == 13) {
        return false;
    } else {
        return true;
    }
}
