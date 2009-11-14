/* ==========================/ EDITOR BOX /================================== */

var EditorBoxRenderer = $.inherit(
	
	IBoxRenderer, {
	
	__constructor: function() { },
	
	renderBox: function(clientId, moduleData, documentModel) {
		var editorWrapper = $('<div />')
				.attr('id', moduleData.holderId)
				.addClass(moduleData.styleClass);
		
		var editorElmId = this.getEditorElmId(clientId);
		
		var documentEditor = new DocumentEditor(clientId, editorElmId, documentModel);		
		
		editorWrapper.append(this.__prepareButtons(editorElmId, clientId,
					createMethodReference(documentEditor, documentEditor.execBtnCommand)));
		
		editorWrapper.append(documentEditor.getEditorElm());
		editorWrapper.append(documentEditor.getPreviewElm());
				
		return editorWrapper;
	},
	
	__prepareButtons: function(elmId, clientId, cmdExecHandler) {
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
							.click(function(event) {
										return cmdExecHandler(buttonData.cmd);
									})
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
		console.log('update catched');
		$('#' + this.getEditorElmId(clientId)).attr('readonly', 'readonly');
	},
	
	afterUpdate: function(clientId) {
		// FIXME: use DocumentEditor unlock method somehow
		console.log('after-update catched');
		$('#' + this.getEditorElmId(clientId)).attr('readonly', '');
	},	
	
	getEditorElmId: function(clientId) {
		return 'editor-content-' + clientId;
	}
	
}, {  // static
	
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
		this.elmId = elmId;
		
		this.inputCompleted = true; // do user finished pressing buttons, default is true		
		this.actionsStore = [];
		
		this.documentText = this.parseDocModel(documentModel);		
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
		
		// this.unlock(); // unlock will be called after update, so editor 
				// will be locked all the time until first update is received
		
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
		if (!this.isLocked()) {
			this.inputCompleted = false;			
			var ev = this.prepareEvent(event);
			this.actionsStore.push(
					[0, ev.which, ev.ctrlKey || ev.metaKey, ev.shiftKey, 
					 	ev.cursorPos]);
			console.log('writing key-event: ', [0, ev.which, ev.ctrlKey || ev.metaKey, ev.shiftKey, 
		                				    ev.cursorPos]);
		}
	},
	
	onKeyUp: function(/*event*/) {
		this.inputCompleted = true;
	},
	
	onMouseUp: function(event) {
		if (!this.isLocked()) {
			var ev = this.prepareEvent(event);
			this.actionsStore.push(
					[1, ev.cursorPos]); // ctrlKey/metaKey?
			console.log('writing mouse-event: ', [1, ev.cursorPos]);
		}
	},	
	
	onCut: function(event) {
		if (!this.isLocked()) {
			var ev = this.prepareEvent(event);
			this.actionsStore.push(
					[2, ev.cursorPos]); // ctrlKey/metaKey?
			console.log('writing cut-event: ', [2, ev.cursorPos]);
		}
	},
	
	onPaste: function(event) {
		if (!this.isLocked()) {
			var ev = this.prepareEvent(event);
			this.actionsStore.push(
					[3, ev.cursorPos]); // ctrlKey/metaKey?
			console.log('writing paste-event: ', [3, ev.cursorPos]);
		}
	},
	
	onTimer: function() {		
		if (this.inputCompleted && (this.actionsStore.length > 0)) {
			this.lock();
			this.sendCommands(this.compileCommands(this.actionsStore));
			this.actionsStore = [];
			this.unlock();
		}
		//console.log('ontimer'); 
	},
	
	prepareEvent: function(event) {
		event.cursorPos = this.editorElm.attr('selectionStart');
		/* FIXME: implement IE way */
		return event;
	},
	
	lock: function() {
		console.log('locking');
		this.editorElm.attr('readonly', true);
	},
	
	unlock: function() {
		console.log('unlocking');
		this.editorElm.attr('readonly', false);
	},
	
	isLocked: function() {
		return this.editorElm.attr('readonly');
	},
	
	execBtnCommand: function(cmdName) {
		console.log('exec cmd: ' + cmdName);
		return this.__self.CMD_BTN_HANDLER(this.clientId, cmdName, this.elmId);
	},
	
	parseDocModel: function(docModel) {
		var docText = "";
		for (textChunkIdx in docModel) {
			var textChunk = docModel[textChunkIdx];
			docText += textChunk.text;
		}		
		return docText;
	},
	
	initWithDocument: function(holderElm, documentModel, mode) {
		//holderElm.val('');
		for (textChunkIdx in documentModel) {
			var textChunk = documentModel[textChunkIdx];
			holderElm.append(textChunk.text);
			// textChunk.id; // textChunk.style; // textChunk.size; 
			// textChunk.reserved; // textChunk.author;
		}		
	},
	
	compileCommands: function(actionsList) {
		// FIXME: implement
		console.log('compiling ', actionsList);
	},
	
	sendCommands: function(commands) {
		// FIXME: implement, send to cmdSequenceServlet
		console.log('sending ', commands);
	}
	
}, { // static
	
	COMMIT_CHECK_PERIOD: 2000,
	
	CMD_BTN_HANDLER: cmdButtonOnClick	
	
});