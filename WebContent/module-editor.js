/* ==========================/ EDITOR BOX /================================== */

var EditorBoxRenderer = $.inherit(
	
	IBoxRenderer, {
	
	__constructor: function() { 		
		this.docEditors = {};
	},
	
	renderBox: function(clientId, moduleData, documentModel) {
		var editorWrapper = $('<div />')
				.attr('id', moduleData.holderId)
				.addClass(moduleData.styleClass);
		
		var editorElmId = this.getEditorElmId(clientId);
		
		var documentEditor = new DocumentEditor(clientId, editorElmId, documentModel);
		this.docEditors[clientId] = documentEditor;
		
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
		_log('update catched');
		this.docEditors[clientId].prepareUpdate();
	},
	
	afterUpdate: function(clientId) {
		_log('after-update catched');
		this.docEditors[clientId].afterUpdate();
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
		this.editorElm.keypress(createMethodReference(this, this.onKeyPress));
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
		if (!this.isLocked()) this.inputCompleted = false;
	},
	
	onKeyPress: function(event) {
		if (!this.isLocked()) {
			//this.lock();
			var ev = this.prepareEvent(event);
			this.actionsStore.push(
					[0, ev.cursorPos, ev.which, ev.charCode, ev.ctrlKey || ev.metaKey || ev.altKey, ev.docSize]);
			_log(event);			
			_log('writing key-event: ', [0, ev.cursorPos, ev.which, ev.charCode, ev.ctrlKey || ev.metaKey || ev.altKey, 
			            					    ev.altKey, ev.docSize]);
			//this.unlock();
		}
	},	
	
	onKeyUp: function(event) {
		this.inputCompleted = true;		
	},
	
	onMouseUp: function(event) {
		if (!this.isLocked()) {
			//this.lock();
			var ev = this.prepareEvent(event);			
			this.actionsStore.push(
					[1, ev.cursorPos, ev.docSize]); // ctrlKey/metaKey?
			_log('writing mouse-event: ', [1, ev.cursorPos, ev.docSize]);
			//this.unlock();
		}
	},	
	
	onCut: function(event) {
		if (!this.isLocked()) {
			//this.lock();
			var ev = this.prepareEvent(event);
			this.actionsStore.push(
					[2, ev.cursorPos, ev.selEnd, ev.docSize]); // ctrlKey/metaKey?
			_log('writing cut-event: ', [2, ev.cursorPos, ev.selEnd, ev.docSize]);
			//this.unlock();
		}
	},
	
	onPaste: function(event) {
		if (!this.isLocked()) {
			//this.lock();
			var ev = this.prepareEvent(event);
			// save document length on last event and compare with new length 
			// to get paste size
			this.actionsStore.push(
					[3, ev.cursorPos, ev.docSize]); // ctrlKey/metaKey?
			_log('writing paste-event: ', [3, ev.cursorPos, ev.docSize]);
			//this.unlock();
		}
	},
	
	onTimer: function() {		
		if (this.inputCompleted && (this.actionsStore.length > 0)) {
			this.lock();
			this.sendCommands(this.compileCommands(this.actionsStore));
			this.actionsStore = [];
			this.askUpdates(); // FIXME: unlock only when commands results and updates received
			this.unlock();
		}
		//_log('ontimer'); 
	},
	
	prepareEvent: function(event) {
		var editor = this.editorElm.get(0);
		event.cursorPos = editor.selectionStart; /* FIXME: implement IE way */
		event.selEnd = editor.selectionEnd; /* FIXME: implement IE way */
		event.docSize = editor.value.length;
		event.charCode = event.charCode || event.keyCode;// editor.value[event.cursorPos];
		return event;
	},
	
	lock: function() {
		_log('locking');
		this.editorElm.attr('readonly', true);
	},
	
	unlock: function() {
		_log('unlocking');
		this.editorElm.attr('readonly', false);
	},
	
	prepareUpdate: function() {
		this.lock();
	},
	
	afterUpdate: function() {
		this.unlock();
	},
	
	isLocked: function() {
		return this.editorElm.attr('readonly');
	},
	
	askUpdates: function() {
		// FIXME: implement, disable automatic updates on server for editor wavelet 
	},
	
	execBtnCommand: function(cmdName) {
		_log('exec cmd: ' + cmdName);
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
		var commands = [];
		_log('compiling ', actionsList);		
		// all variables here must be instance properties
		var charKeysStack = [];
		var inputMode = 0; // 0 - put, 1 - delete, 2 - replace
		var lastCursorPos = 0;
		var startCursorPos = 0;
		var deleteStopPos = 0;
		var lastDocSize = 0; // FIXME: init on documentLoad
		// FIXME: not all events are handled (or document state changes while they are handled)
		//        when they are performed very fast
		var pushCurrentCommand = function() {
				if ((inputMode == 0) && charKeysStack && (charKeysStack.length > 0)) 
					commands.push({mode: 'put', chars: (String.fromCharCode.apply(null, charKeysStack)), pos: startCursorPos}); 
				if ((inputMode == 1) && ((deleteStopPos - startCursorPos) > 0)) 
					commands.push({mode: 'del', start: startCursorPos, end: deleteStopPos});
			};
		for (actionIdx in actionsList) {
			var action = actionsList[actionIdx];
			//               0  1             2          3            4                                      5 
			// key-event:   [0, ev.cursorPos, ev.which,  ev.charCode, ev.ctrlKey || ev.metaKey || ev.altKey, ev.docSize]
			// mouse-event: [1, ev.cursorPos, ev.docSize]
			// cut-event:   [2, ev.cursorPos, ev.selEnd, ev.docSize]
			// paste-event: [3, ev.cursorPos, ev.docSize]
			switch(action[0]) {
				case 0: { // when some key pressed ...
						var cursorPos = action[1];					
						var which = action[2];
						var charCode = action[3];
						var funcKey = action[4];
						//    0     1     2     3     4     5     6     7     8     9    
						// 00 ----- ----- ----- ----- ----- ----- ----- ----- [BS]- [TAB]
						// 01 [NL]- ----- ----- [BR]- ----- ----- [SFT] [CTR] [ALT] [BRK]
						// 02 [CAP] ----- ----- ----- ----- ----- ----- [ESC] ----- -----
						// 03 ----- ----- [SPC] [PUNCTUATION-PUNCTUATION-PUNCTUATION-PUNC
						// 04 TUATION-PUNCTUATION-PUNCTUATION-PUNCTUATION]--- [NUMBERS-NU
						// 05 MBERS-NUMBERS-NUMBERS-NUMBERS-NUMBERS-NUMBERS]- [PUNCTUATIO
						// 06 N-PUNCTUATION-PUNCTUATION]--- [LETTERS-LETTERS-LETTERS-LETT
						// 07 ERS-LETTERS-LETTERS-LETTERS-LETTERS-LETTERS-LETTERS-LETTERS
						// 08 -LETTERS-LETTERS-LETTERS-LETTERS-LETTERS-LETTERS-LETTERS-LE
						// 09 TTER] [PUNCTUATION-PUNCTUATION-PUNCTUATI] [LETTERS-LETTERS-
						// 10 LETTERS-LETTERS-LETTERS-LETTERS-LETTERS-LETTERS-LETTERS-LET
						// 11 TERS-LETTERS]---- [PUNCTUATION-PUNCTUATI] ----- ----- -----
						if (!funcKey) { // if no functional key is pressed
							if ((which === undefined) || (which >= 32)) { // if it is some letter, NOT any of del/backspace/shift...
								// and if we are inserting and it is inserted just after the previous letter // FIXME: handle Enter
								if ((inputMode == 0) && (cursorPos == (lastCursorPos + 1))) { 
									charKeysStack.push(charCode); // ... then write this character to the stack									
								} else { // if we are deleted something before or inserting position is changed
									// ...then save the state of the previous performed actions in command
									pushCurrentCommand();
									// ...and save the new state
									startCursorPos = deleteStopPos = lastCursorPos = cursorPos;
									charKeysStack = [charCode];
								}
								inputMode = 0; // user is putting chars now
							} else if (which == 46) { // Del key								
								// if user continues to delete 
								if ((inputMode == 1) && (cursorPos == lastCursorPos)) {
									// startCursorPos is the same as before
									deleteStopPos++; // and delete-stop pos increases
								} else {
									// if user entered chars or deleted chars before - save his actions
									pushCurrentCommand();
									// ... and save the new state
									startCursorPos = astCursorPos = cursorPos;
									deleteStopPos = cursorPos + 1;
								}
								inputMode = 1; // user is deleting characters now
							}
						}
						if (((which >= 34)  && (which <= 40)) ||   // Arrows keys (37-40) or PgUp(33)/PgDn(34)/End(35)/Home(36), or
							( which == 8)/* || (which == 9)   ||*/ // or Backspace or (Tab) // FIXME: Backspace/Del with text selected deletes it 
						  /*( which == 45)*/|| (which == 46)     // or (Insert) or Del // Insert is not switching ins/del mode in textarea, and Tab changes to other input,
							                                    // so there is no need to handle them (shift+ins handled by paste event)
							) {
							pushCurrentCommand();
							startCursorPos = deleteStopPos = lastCursorPos = cursorPos;
						}
						lastCursorPos = cursorPos; // save last cursor pos
						lastDocSize = action[5];
					} break;
				case 1: { // mouse-event
					    lastCursorPos = action[1];
					    lastDocSize = action[2];
					} break;
				case 2: { // cut-event
						// FIXME: implement
					} break;
				case 3: { // paste-event
						// FIXME: implement
					} break;		
			}			
		}
		// push the modifications if some happened
		pushCurrentCommand();
		return commands;
	},
	
	sendCommands: function(commands) {
		// FIXME: implement, send to cmdSequenceServlet
		_log('sending ', commands);
	}
	
}, { // static
	
	COMMIT_CHECK_PERIOD: 2000,
	
	CMD_BTN_HANDLER: cmdButtonOnClick	
	
});