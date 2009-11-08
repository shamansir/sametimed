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