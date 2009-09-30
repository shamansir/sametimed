var CLIENTS_HOLDER_ID = 'client-views';

/*
 * waveModelStr format:
 * 
 * client = {
 * 		clientId: <int>,
 * 		info: <string>, // information line
 * 		inbox: {<int>: <string>, ...}, // waves list, inbox number to wave id string
 * 		users: [<string>, ...], // full addresses, one by one   
 * 		chat: [{author: <string>, text: <string>}, ...], // chat lines
 * 		document: [{text: <string>, style: <string>}, ...}], // document chunks
 * 		console: [<string>, ...], // console history
 * 		errors: [<string>, ...], // errors happend while using the client
 * }
 */

function renderClient(waveModelStr) {
	var waveModelObj = JSON.parse(waveModelStr);
	$('#' + CLIENTS_HOLDER_ID).append(
			ClientRenderer.createClient(waveModelObj)
		);	
}

// use jquery.inherit plugin
var ClientRenderer = {
		
	SEND_BTN_HANDLER: 'sendButtonOnClick',
	CMD_BTN_HANDLER: 'cmdButtonOnClick',
		
	createClient: function(waveModel) {
	
		var clientId = waveModel.clientId;
		
		var clientWrapper = $('<div />')
			.attr('id', 'wave-client-' + clientId)
			.addClass('wave-client');
		
		// TODO: store elements as id -> element hash
		clientWrapper.append(this.createInfoLine(clientId, waveModel.info));
		clientWrapper.append(this.createInbox(clientId, waveModel.inbox));
		clientWrapper.append(this.createUsersList(clientId, waveModel.users));
		clientWrapper.append(this.createChat(clientId, waveModel.chat));
		clientWrapper.append(this.createEditor(clientId, waveModel.document));
		clientWrapper.append(this.createConsole(clientId, waveModel.console));
		clientWrapper.append(this.createErrorBox(clientId, waveModel.errors));
		
		return clientWrapper;
	},

	createInfoLine: function(clientId, infoLineStr) {
		return $('<span />')
				.attr('id', 'client-' + clientId + '-infoline')
				.addClass('infoline')
				.text(infoLineStr);
	},
	
	createInbox: function(clientId, inboxModel) {
		var inboxWrapper = $('<ol />')
				.attr('id', 'client-' + clientId + '-inbox')
				.addClass('inbox');
		
		if (inboxModel.length == 0) {
			inboxWrapper.append($('<li />').addClass('empty'));
		}
		
		for (inboxId in inboxModel) {
			inboxWrapper.append($('<li />')
					.append($('<span />').addClass('inbox-id').text(inboxId))
					.append($('<span />').addClass('inbox-wave-id').text(
							inboxModel[inboxId])));
		}	
		
		return inboxWrapper;		
	},
	
	createUsersList: function(clientId, usersModel) {
		var userslistWrapper = $('<ul />')
				.attr('id', 'client-' + clientId + '-userlist')
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
				.attr('id', 'client-' + clientId + '-chat')
				.addClass('chat');
		
		for (chatLineIdx in chatModel) {
			var chatLine = chatModel[chatLineIdx];
			chatWrapper.append($('<span />').addClass('author').text(chatLine.author));
			chatWrapper.append($('<span />').addClass('line').text(chatLine.text));
		}	
		
		return chatWrapper;
	},
	
	createEditor: function(clientId, documentModel) {
		var editorWrapper = $('<div />')
				.attr('id', 'client-' + clientId + '-editor')
				.addClass('editor');
		
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
				.attr('id', 'client-' + clientId + '-console')
				.attr('action', null)
				.attr('method', 'post')
				.addClass('console');
		
		consoleWrapper.append($('<input />')
				.attr('id', inputElmId)
				.attr('type', 'text')
				.addClass('gwt-TextBox'));
				
		consoleWrapper.append($('<input />')
				.attr('type', 'button')
				.attr('title', 'send')
				.attr('value', 'send')
				.addClass('gwt-Button')
				.attr('onclick', this.SEND_BTN_HANDLER +
						'(' + clientId + ',\'' + inputElmId + '\')'));
				/* .click(...)); */		
		
		return consoleWrapper;		
	},
	
	createErrorBox: function(clientId, errorsLines) {
		var errboxWrapper = $('<div />')
				.attr('id', 'client-' + clientId + '-errorbox')
				.addClass('errorbox');
		
		for (errorLineIdx in errorsLines) {
			errboxWrapper.append($('<span />').addClass('error-line').text(
					errorsLines[errorLineIdx]));
		}	
		
		return errboxWrapper;			
	}	
		
}

