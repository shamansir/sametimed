/* ==========================/ EDITOR BOX /================================== */

var EditorBoxRenderer = $.inherit(		
	
	IBoxRenderer, {
	
	__constructor: function() { },
	
	renderBox: function(clientId, moduleData, documentModel) {
		var editorWrapper = $('<div />')
				.attr('id', moduleData.holderId)
				.addClass(moduleData.styleClass);
		
		var editorElmId = 'editor-content-' + clientId;
		
		editorWrapper.append(this.__prepareButtons(editorElmId, clientId));
		editorWrapper.append(this.__prepareWikiEditor(editorElmId, documentModel));
		editorWrapper.append(this.__preparePreviewArea(editorElmId + '-result', documentModel));
				
		return editorWrapper;
	},
	
	__prepareButtons: function(elmId, clientId) {
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
							.attr('onclick', 'return ' + this.__self.CMD_BTN_HANDLER + "(" + clientId + ",'" + buttonData.cmd + "','" + elmId + "')")
							.addClass('editor-button')
							.text(buttonData.text);
				buttonWrapper.append(button);
				buttonsBlock.append(buttonWrapper);
			}
			blockWrapper.append(buttonsBlock);
			buttonsContainer.append(blockWrapper);
		}		
		return buttonsContainer;
	},
	
	__prepareWikiEditor: function(emlId, documentModel) {
		var wikiEditingArea =  $('<textarea />')
				.addClass('editor-wiki-area')
				.attr('id', emlId);
		// new EditorController(); attach documentModel + clientId
		for (textChunkIdx in documentModel) {
			var textChunk = documentModel[textChunkIdx];
			wikiEditingArea.append(textChunk.text);			
			// textChunk.id; // textChunk.style; // textChunk.size; 
			// textChunk.reserved; // textChunk.author;
		}
		wikiEditingArea.keydown(createMethodReference(this, this.editorOnKeyDown));
		wikiEditingArea.keyup(createMethodReference(this, this.editorOnKeyUp));
		return wikiEditingArea;
	},
	
	__preparePreviewArea: function(emlId, documentModel) {
		var previewArea =  $('<div />')
			.addClass('editor-document')
			.attr('id', emlId + '-result');
		// new EditorController(); attach documentModel + clientId
		for (textChunkIdx in documentModel) {
			var textChunk = documentModel[textChunkIdx];
			previewArea.append(textChunk.text);
			// textChunk.style;
		}
		return previewArea;
	},
	
	editorOnKeyDown: function(event) {
		// console.log(event);
	},
	
	editorOnKeyUp: function(event) {
		// console.log(event);
	}	
	
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