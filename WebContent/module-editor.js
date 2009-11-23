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
 		  'justify': { id_postfix: '-jtify', name: 'Justify Text', cmd: 'jtify', text: 'J'} }/*,
 		{ 'put': { id_postfix:'-put', name: 'Put Text', cmd: 'put', text:'Put'} } */ ]	
	
});

/* =======================/ DOCUMENT HANDLER /=============================== */

var DocumentEditor = $.inherit({
	
	__constructor: function(clientId, elmId, documentModel) {
		this.clientId = clientId;
		this.elmId = elmId;
		
		this.inputCompleted = true; // do user finished pressing buttons, default is true		
		this.actionsStore = [];
		
		// construct elements
		this.documentText = this.parseDocModel(documentModel);		
		this.editorElm = this.createEditor(elmId, documentModel); // jQuery object
		this.previewElm = this.createPreview(elmId + '-result', documentModel); // jQuery object
		
		this.lock();
		
		this.editorElm.attr('client', clientId);
		this.previewElm.attr('client', clientId);
		
		// init elements with document content
		this.initWithDocument(this.editorElm, documentModel, 'wiki');
		this.initWithDocument(this.previewElm, documentModel, 'styled');
		
		// macros are used to record user actions, which are
		// sent periodically to server to apply document changes
		// FIXME: init with document state
		this.macrosState = {
				charKeysStack: [],
				inputMode: 0, // 0 - put, 1 - delete
				lastCursorPos: 0,
				startCursorPos: 0,
				deleteStopPos: 0
			};
		
		// assign events
		this.editorElm.keydown(createMethodReference(this, this.onKeyDown));
		this.editorElm.keyup(createMethodReference(this, this.onKeyUp));
		this.editorElm.keypress(createMethodReference(this, this.onKeyPress));		
		
		this.editorElm.bind('cut', createMethodReference(this, this.onCut));
		this.editorElm.bind('paste', createMethodReference(this, this.onPaste));
		this.editorElm.bind('undo', createMethodReference(this, this.onUndo)); // FIXME: undo is not handled!
		
		// handler will send the recorded actions (compiled in actions) with the check period
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
					[0, ev.cursorPos, ev.which, ev.charCode, (ev.ctrlKey || ev.metaKey) | (ev.altKey << 1), ev.selEnd]);
			//_log(event);			
			_log('writing key-event: ', [0, ev.cursorPos, ev.which, ev.charCode, (ev.ctrlKey || ev.metaKey) | (ev.altKey << 1), 
			            					    ev.selEnd]);
			//this.unlock();
		}
	},	
	
	onKeyUp: function(event) {
		this.inputCompleted = true;		
	},
	
	onCut: function(event) {
		if (!this.isLocked()) {
			//this.lock();
			var ev = this.prepareEvent(event);
			this.actionsStore.push(
					[1, ev.cursorPos, ev.selEnd]); // ctrlKey/metaKey?
			_log('writing cut-event: ', [1, ev.cursorPos, ev.selEnd]);
			//this.unlock();
		}
	},
	
	onPaste: function(event) {
		if (!this.isLocked()) {
			//this.lock();
			var ev = this.prepareEvent(event);
			var ctrl = this;
			var edElm = this.editorElm.get(0); // FIXME: docSize is getting not from prepared event 
			var cursorPos = ev.cursorPos;
			var preSize = ev.docSize;
			setTimeout(function() {
					var val = edElm.value;
					var curSize = val.length;
					var text = val.substr(cursorPos, curSize - preSize);
					ctrl.actionsStore.push(
							[2, cursorPos, text, text.length]); 
					_log('writing paste-event: ', [2, cursorPos, text, text.length]);
			}, 1);
			//this.unlock();
		}
	},
	
	onUndo: function(event) {
		if (!this.isLocked()) {
			var ev = this.prepareEvent(event);
			this.actionsStore.push([3, ev.cursorPos]);
			_log('writing undo-event: ', [3, ev.cursorPos]);
		}
	},
	
	onTimer: function() {
		// FIXME: handle pushing in specified position
		// FIXME: handle del/undo commands (at least del)		
		// FIXME: turn off updates for document and put the cursor in editor after all updates
		// FIXME: really ask for document updates and unlock only when they are received
		// FIXME: updates even for closed and for all clients are sent. 		
		if (this.inputCompleted && (this.actionsStore.length > 0)) {
			this.lock();
			this.sendCommands(this.compileCommands(this.actionsStore));
			this.actionsStore = [];
			this.askUpdates(); // FIXME: unlock only when commands results and updates received
			this.unlock();
			this.editorElm.focus();
		}
		//_log('ontimer'); 
	},
	
	// each event is prepared (decorated) before further processing
	prepareEvent: function(event) { 
		var editor = this.editorElm.get(0);
		event.cursorPos = editor.selectionStart; /* FIXME: implement IE way */
		event.selEnd = editor.selectionEnd; /* FIXME: implement IE way */
		event.docSize = editor.value.length;
		event.charCode = event.charCode || event.keyCode;// editor.value[event.cursorPos];
		return event;
	},
	
	execBtnCommand: function(cmdName) {
		_log('exec cmd: ' + cmdName);
		return this.__self.CMD_BTN_HANDLER(this.clientId, cmdName, this.elmId, this.__self.DOCUMENT_ALIAS);
	},		
	
	lock: function() {
		_log('locking');
		this.editorElm.attr('readonly', true);
	},
	
	unlock: function() {
		_log('unlocking');
		this.editorElm.attr('readonly', false);
	},
	
	isLocked: function() {
		return this.editorElm.attr('readonly');
	},	
	
	prepareUpdate: function() {
		this.lock();
	},
	
	afterUpdate: function() {
		this.unlock();
	},
	
	askUpdates: function() {
		// FIXME: implement, disable automatic updates on server for editor wavelet 
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
		var ms = this.macrosState;
		// function for using current macrosState to push the command
		var pushCurrentCommand = function() {
				if ((ms.inputMode == 0) && ms.charKeysStack && (ms.charKeysStack.length > 0)) {					
					commands.push({ name: 'put', 
									arguments: { chars: (String.fromCharCode.apply(null, ms.charKeysStack)), 
						                         pos: ms.startCursorPos } });
					ms.startCursorPos = ms.lastCursorPos = (ms.lastCursorPos + 1);
					ms.charKeysStack = [];
				}
				if ((ms.inputMode == 1) && ((ms.deleteStopPos - ms.startCursorPos) > 0)) {
					commands.push({ name: 'del', 
								    arguments: { start: ms.startCursorPos, 
						                         len: ms.deleteStopPos - ms.startCursorPos } });
					ms.startCursorPos = ms.deleteStopPos;
				}
			};
 /* for each recorded action */
		for (actionIdx in actionsList) {
			var action = actionsList[actionIdx];
			//               0  1             2          3            4                                              5 
			// key-event:   [0, ev.cursorPos, ev.which,  ev.charCode, (ev.ctrlKey || ev.metaKey) | (ev.altKey << 1), ev.selEnd]
			// // mouse-event: [-, ev.cursorPos]
			// cut-event:   [1, ev.cursorPos, ev.selEnd]
			// paste-event: [2, ev.cursorPos, text,      textLen]
			// undo-event:  [3, ev.cursorPos]
			switch(action[0]) {
 /* if current action is keypress action */
 /*action:key*/ case 0: { // when some key pressed ...
						// TODO: use action array, not variables, if they are used only ones
						var cursorPos = action[1];					
						var which = action[2];
						var charCode = action[3];
						var funcKey = action[4];
						var selEnd = action[5];
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
   /* if !func key */	if (!funcKey) { // if no functional key was pressed 
	   /* if letter key */	if ((which === undefined) || (which >= 32)) { // if it is some letter, NOT any of del/backspace/shift...
								// and if we are inserting and it is inserted just after the previous letter // FIXME: handle Enter
			   /* 1-by-1 */		if ((ms.inputMode == 0) && (cursorPos == (ms.lastCursorPos + 1))) { 
									ms.charKeysStack.push(charCode); // ... then write this character to the stack									
			   /* else */		} else { // if we are deleted something before or inserting position is changed
									// ...then save the state of the previous performed actions in command
									pushCurrentCommand();
									// ...and save the new state
									ms.startCursorPos = ms.deleteStopPos = ms.lastCursorPos = cursorPos;
									ms.charKeysStack = [charCode];
								}
								ms.inputMode = 0; // user is putting chars now
	   /* or Del key */     } else if ((which == 0) && (charCode == 46)) { // Del key
			   /* block */      if (selEnd != cursorPos) { // if user deletes the selected block of letters
									// if user entered chars or deleted chars before - save his actions
									pushCurrentCommand();
									var delStartPos = (selEnd > cursorPos) ? cursorPos : selEnd;
									var delEndPos   = (selEnd > cursorPos) ? selEnd : cursorPos;
									// send deletion command
									commands.push({ name: 'del', 
													arguments: { start: delStartPos, 
										                         len: delEndPos - delStartPos } });
									// ...and save the new state
									ms.startCursorPos = ms.deleteStopPos = ms.lastCursorPos = cursorPos;									
								// if user just continues to delete sequence of letters 
			   /* 1-by-1 */		} else if ((ms.inputMode == 1) && (cursorPos == ms.lastCursorPos)) {
									// startCursorPos is the same as before
									ms.deleteStopPos++; // and delete-stop pos increases
			   /* else */		} else {
									// if user entered chars or deleted chars before - save his actions
									pushCurrentCommand();
									// ... and save the new state
									ms.startCursorPos = ms.lastCursorPos = cursorPos;
									ms.deleteStopPos = (cursorPos + 1);
								}
								ms.inputMode = 1; // user is deleting characters now
	   /* or Bksp key */    } else if (which == 8) { // Backspace key
			   /* block */		if (selEnd != cursorPos) { // if user deletes the selected block of letters
									// if user entered chars or deleted chars before - save his actions
									pushCurrentCommand();
									var delStartPos = (selEnd > cursorPos) ? cursorPos : selEnd;
									var delEndPos   = (selEnd > cursorPos) ? selEnd : cursorPos;
									// send deletion command
									commands.push({ name: 'del', 
													arguments: { start: delStartPos, 
										                         len: delEndPos - delStartPos } });
									// ...and save the new state
									ms.startCursorPos = ms.deleteStopPos = ms.lastCursorPos = cursorPos;								
								// if user just continues to delete sequence of letters
			   /* 1-by-1 */     } else if ((ms.inputMode == 1) && (cursorPos == ms.startCursorPos)) {
									// deleteStopPos is the same as before
									ms.startCursorPos = (ms.startCursorPos > 0) ? (ms.startCursorPos - 1) : ms.startCursorPos;
			   /* else */       } else {
									// if user entered chars or deleted chars before - save his actions
									pushCurrentCommand();
									// ... and save the new state
									ms.startCursorPos = ms.lastCursorPos = (cursorPos > 0) ? (cursorPos - 1) : cursorPos;
									ms.deleteStopPos = cursorPos; 
								}
								ms.inputMode = 1; // user is deleting characters now								
							}
   /* or if Ctrl */     } else if (1 & funcKey) { // if Ctrl is pressed
			  /* +Bksp */   if (which == 8) { // Ctrl + Backspace
				  				// if user entered chars or deleted chars before - save his actions
								pushCurrentCommand();
								var delStartPos = (selEnd > cursorPos) ? cursorPos : selEnd;
								var delEndPos   = (selEnd > cursorPos) ? selEnd : cursorPos;
								// send deletion command
								commands.push({ name: 'del', 
									            arguments: { start: delStartPos, 
									                         len: delEndPos - delStartPos } });
			  /* +Del */    } else if ((which == 0) && (charCode == 46)) { // Ctrl + Del
				  				// if user entered chars or deleted chars before - save his actions
								pushCurrentCommand();
								if (selEnd != cursorPos) { // if block selected
									var delStartPos = (selEnd > cursorPos) ? cursorPos : selEnd;
									var delEndPos   = (selEnd > cursorPos) ? selEnd : cursorPos;
									// send deletion command
									commands.push({ name: 'del', 
										            arguments: { start: delStartPos, 
										                         len: delEndPos - delStartPos } });									
								} else {
									// just send deletion-to-the-end-of-text command 									
									commands.push({ name: 'del', 
										            arguments: { start: cursorPos, 
										                         len: -1 } }); // -1 means delete to the end
								}
							} else if (which == 122) {  // FIXME: undo using right-click/menu is not handled!
								commands.push({ name: 'undo' });
							}
			  				// ...and save the new state
			  				ms.startCursorPos = ms.deleteStopPos = ms.lastCursorPos = cursorPos;
						}
						/* if (((which >= 34)  && (which <= 40)) { not required, if user places cursor back then 
							pushCurrentCommand();
							ms.startCursorPos = ms.deleteStopPos = ms.lastCursorPos = cursorPos;
						} */
						ms.lastCursorPos = cursorPos; // save last cursor pos
					} break;
				/* case 1: { // mouse-event
					    // may be not required, because cursor pos is passed with each event
						// ms.lastCursorPos = action[1];
					} break; */
 /* if current action is cut action */
 /*action:cut*/ case 1: { // cut-event
						// if user entered chars or deleted chars before - save his actions
						pushCurrentCommand();
						var cursorPos = action[1];
						var selEnd = action[2];
						var delStartPos = (selEnd > cursorPos) ? cursorPos : selEnd;
						var delEndPos   = (selEnd > cursorPos) ? selEnd : cursorPos;
						// send deletion command
						commands.push({ name: 'del', 
							            arguments: { start: delStartPos, 
							                         len: delEndPos - delStartPos } });
						// ...and save the new state
						ms.startCursorPos = ms.deleteStopPos = ms.lastCursorPos = cursorPos;
					} break;
 /* if current action is paste action */
 /*action:pst*/ case 2: { // paste-event
						// if user entered chars or deleted chars before - save his actions
						pushCurrentCommand();
						var cursorPos = action[1]; 					
						commands.push({ name: 'put', 
							            arguments: { chars: action[2], 
							                         pos: cursorPos } }); // action[2] is text
						// save new state
						ms.charKeysStack = [];
						ms.inputMode = 0;						
						ms.startCursorPos = ms.deleteStopPos = ms.lastCursorPos = (cursorPos + action[3]); // action[3] is textLen
					} break;
 /*action:undo*/case 3: { // undo-event
	 					commands.push({ name: 'undo' });
	 					ms.startCursorPos = ms.deleteStopPos = ms.lastCursorPos = action[1]; // action[1] is cursorPos
 					} break; 
			}			
		}
		// push the modifications if some happened
		pushCurrentCommand();
		return commands;
	},
	
	sendCommands: function(commands) {
		_log('sending ', commands);
		this.__self.CMD_SEQENCE_SENDER(this.clientId, commands, this.__self.DOCUMENT_ALIAS);
	}
	
}, { // static
	
	DOCUMENT_ALIAS: 'document',
	
	COMMIT_CHECK_PERIOD: 2000,
	
	CMD_BTN_HANDLER: cmdButtonOnClick,
	CMD_SEQENCE_SENDER: sendCommandsSequence	
	
});