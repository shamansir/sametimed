/* ==========================/ EDITOR BOX /================================== */

var EditorBoxRenderer = $.inherit(		
	
	IBoxRenderer, {
	
	__constructor: function() { },
	
	renderBox: function(clientId, moduleData, documentModel) {
		var editorWrapper = $('<div />')
				.attr('id', moduleData.holderId)
				.addClass(moduleData.styleClass);
		
		var editorElmId = this.getEditorElmId(clientId);
		
		editorWrapper.append(this.__prepareButtons(editorElmId, clientId));
		
		var documentEditor = new DocumentEditor(clientId, editorElmId, documentModel);				
		
		editorWrapper.append(documentEditor.getEditorElm());
		editorWrapper.append(documentEditor.getPreviewElm());
				
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
							.attr('onclick', 'return ' + 
									this.__self.CMD_BTN_HANDLER + 
										"(" + clientId + ",'" 
										    + buttonData.cmd + "','" 
										    + elmId + "')")
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
	
	prepareUpdate: function(clientId) {
		// FIXME: use DocumentEditor lock method somehow
		$('#' + this.getEditorElmId(clientId)).attr('readonly', 'readonly');
	},
	
	afterUpdate: function(clientId) {
		// FIXME: use DocumentEditor unlock method somehow
		$('#' + this.getEditorElmId(clientId)).attr('readonly', '');
	},	
	
	getEditorElmId: function(clientId) {
		return 'editor-content-' + clientId;
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

/* =======================/ DOCUMENT HANDLER /=============================== */

var DocumentEditor = $.inherit({
	
	__constructor: function(clientId, elmId, documentModel) {
		this.clientId = clientId;
		this.documentModel = documentModel;
				
		this.editorElm = this.createEditor(elmId, documentModel); // jQuery object
		this.previewElm = this.createPreview(elmId + '-result', documentModel); // jQuery object
		
		this.lock();
		
		this.editorElm.attr('client', clientId);
		this.previewElm.attr('client', clientId);
		
		this.initWithDocument(this.editorElm, documentModel, 'wiki');
		this.initWithDocument(this.previewElm, documentModel, 'styled');
		
		this.editorElm.keydown(createMethodReference(this, this.onKeyDown));
		this.editorElm.keyup(createMethodReference(this, this.onKeyUp));
		//this.editorElm.change(createMethodReference(this, this.onChange));
		this.editorElm.mouseup(createMethodReference(this, this.onMouseUp));
		
		this.editorElm.bind('cut', createMethodReference(this, this.onCut));
		this.editorElm.bind('paste', createMethodReference(this, this.onPaste));
		
		var timerEventHandler = createMethodReference(this, this.onTimer); 
		
		setInterval(function() { timerEventHandler(); }, this.__self.COMMIT_CHECK_PERIOD);
		
		// this.unlock(); // unlock will be called after update
		
	},
	
	createEditor: function(elmId, documentModel) {
		return $('<textarea />')
			.addClass('editor-wiki-area')
			.attr('id', elmId);
	},
	
	createPreview: function(elmId, documentModel) {
		return $('<div />')
			.addClass('editor-document')
			.attr('id', elmId);
	},	
	
	getEditorElm: function() {
		return this.editorElm;
	},
	
	getPreviewElm: function() {
		return this.previewElm;
	},	

	onKeyDown: function(event) {
		var ev = event.originalEvent;
		console.log('keydown:', ev.which);
	},
	
	onKeyUp: function(event) {
		var ev = event.originalEvent;
	    console.log('keyup:', ev.which);
	},
	
	onMouseUp: function(event) {
		var ev = event.originalEvent;
		console.log('mouseup:', ev);
	},	
	
	onCut: function(event) {
		var ev = event.originalEvent;
		console.log('cut:', ev);
	},
	
	onPaste: function(event) {
		var ev = event.originalEvent;
		console.log('paste:', ev);
	},	
	
	onTimer: function() {
		//console.log('ontimer'); 
	},
	
	lock: function() {
		this.editorElm.attr('readonly', 'readonly');
	},
	
	unlock: function() {
		this.editorElm.attr('readonly', '');
	},	
	
	initWithDocument: function(holderElm, documentModel, mode) {
		//holderElm.val('');
		for (textChunkIdx in documentModel) {
			var textChunk = documentModel[textChunkIdx];
			holderElm.append(textChunk.text);
			// textChunk.id; // textChunk.style; // textChunk.size; 
			// textChunk.reserved; // textChunk.author;
		}		
	}
	
}, { // static
	
	COMMIT_CHECK_PERIOD: 500
	
});