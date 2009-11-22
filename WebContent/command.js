/*
function escapeXML(strForXml) {
	strForXml = strForXml.replace(/&/g, "&amp;");
	strForXml = strForXml.replace(/</g, "&lt;");
	strForXml = strForXml.replace(/>/g, "&gt;");
	strForXml = strForXml.replace(/"/g, "&quot;");
	return strForXml;
}

function unescapeXML(xmlStr) {
	xmlStr = xmlStr.replace(/&amp;/g, "&");
	xmlStr = xmlStr.replace(/&lt;/g, "<");
	xmlStr = xmlStr.replace(/&gt;/g, ">");
	xmlStr = xmlStr.replace(/&quot;/g, "\"");
	return xmlStr;
}*/

function escapeQuotes(strToEscape) {
	return strToEscape.replace(/"/g, "&_quot;");
}

function unescapeQuotes(escapedStr) {
	return escapedStr.replace(/&_quot;/g, "\"");
}

var CLIENT_RECEIVER_URL = './get_client_view'; // FIXME: load from configuration
var CMD_EXECUTOR_URL = './cmd_exec'; // FIXME: load from configuration

/* ====== COMMANDS ====== */

function parseCommandLine(cmdAuthor, line, sourceDoc) {
	var line = new String(line);
	if (line.length > 0) {
		var commandName = '';
		var arguments = {};
		if (line.charAt(0) == '/') {
			var cmdText = line.substr(1); // removing slash character
			var splitted = cmdText.split(/\s+/); // splitting with space symbols 
			commandName = splitted[0];
			arguments = prepareArgumentsHash(commandName, splitted.slice(1)); // pass arguments without command as array
		} else {
			commandName = 'say';
			arguments['text'] = line;
		}
		return encodeCmd(cmdAuthor, commandName, arguments, sourceDoc);		
	} else {
		return "?";
	}
}

var defaultRegisteredCmds = {
	'new'    : { docRelated: false },
	'read'   : { docRelated: false },
	'quit'   : { docRelated: false },
	'add'    : { docRelated: false },
	'remove' : { docRelated: false },
	'connect': { docRelated: false },
	'view'   : { docRelated: false }, // false?
	'open'   : { docRelated: true }	
};

function encodeCmd(forClient, commandName, arguments, sourceDoc) {
	// command format: cmd(32 chat arg1("val1") arg2("val2"))
	var encodedCmd = commandName + '(';
	encodedCmd += forClient + ' ';
	if (!(defaultRegisteredCmds[commandName]) || defaultRegisteredCmds[commandName].docRelated) {
		encodedCmd += sourceDoc + ' ';
	}
	for (argumentName in arguments) {
		var argument = arguments[argumentName];
		if (argument !== undefined) {
			encodedCmd += argumentName + '("' +
				encodeURIComponent(escapeQuotes(arguments[argumentName])) + '") ';
		}
	}	
	return encodedCmd + ')';
}

function prepareArgumentsHash(commandName, argumentsArray) {
	// TODO: use hash with commands names as keys 
	var arguments = {};
	// no arguments
	if (commandName == "new") {
		return arguments;
	}
	if (commandName == "read") {
		return arguments;
	}
	if (commandName == "quit") {
		return arguments;
	}	
	// one argument
	if (commandName == "open") {
		arguments["entry"]  = argumentsArray[0];
		return arguments;
	}
	if (commandName == "add" || commandName == "remove" || commandName == "undo") {
		arguments["user"]   = argumentsArray[0];
		return arguments;
	}	
	if (commandName == "view") {
		arguments["mode"]   = argumentsArray[0];
		return arguments;
	}
	// three arguments
	if (commandName == "connect") {
		arguments["user"]   = argumentsArray[0];
		arguments["server"] = argumentsArray[1];
		arguments["port"]   = argumentsArray[2];
		return arguments;
	}
}

/* ====== MESSAGES ====== */

// update re: ^(\w+)\((\d+)(?:\s+(\w+))?\s+(.*)\)$
// argument re: (\w+)\(\"([^\"]*)\"\)

var MESSAGE_RE = /^(\w+)\((\d+)(?:\s+(\w+))?\s+(.*)\)$/;
var MSG_ARG_RE = /(\w+)\(\"([^\"]*)\"\)/;

function parseUpdateMessage(updateMessage) {
	// update message format: msg(32 arg1("val1") arg2("val2"))
	// model update message format: upd(32 inbox value("jsonModel"))
	//_log('parseUpdate', updateMessage);
	var resData = updateMessage.match(MESSAGE_RE);
	if (resData) {
		var msgType = resData[1];
		var ownerId = resData[2];
		if (msgType == 'upd') { // update message
			var modelAlias = resData[3];
			var argument = resData[4].match(MSG_ARG_RE);
			if (argument) {
				if (argument[1] == 'value') {
					var valueStr = argument[2];
					return { // SUCCESS!!
						clientId: ownerId,
						modelType: modelAlias,
						modelValue: $.evalJSON(unescapeQuotes(valueStr))
					};					
				} else {
					alert('Model update message can not be parsed: must have only "value" argument but found ...' + resData[4] + '...');
				}
			} else {
				alert('Failed to extract arguments from update message line: ...' + resData[4] + '...');
				return null;
			}
		} /* else {
			var arguments = resData[4];
			var argsData = MSG_ARG_RE.exec(arguments);
			if (argsData) {
				// TODO: implement
			}			
		} */
	} else {
		alert('Update message ' + updateMessage + ' can not be parsed');
		return null;
	}
}

function updateReceived(updateMessage) {
	renderUpdate(parseUpdateMessage(updateMessage));
}

/* ====== GET FULL CLIENT ====== */

function getFullClient(username, clientsHolder) {
	$.ajax({ url: CLIENT_RECEIVER_URL,
		     type: 'POST',	
		     dataType: 'json',
		     data: { 'username': username,
					 'ueq': false 		// ueq == use escaped quotes in response
			       },		   
		     success: function(waveModelObj) {
			 		  	  renderClient(waveModelObj, clientsHolder);
				      },
		     error: function(request, textStatus, error) {
				    	  showGeneralError(error + ': ' + textStatus);
				    }
		   });
	// makeRequest(CLIENT_RECEIVER_URL, 'username=' + username + '&ueq=false', addClientFunc, true);	
}

/* ====== ONCLICK ====== */

function cmdButtonOnClick(clientId, commandAlias, inputId) {
	var arguments = {};
	var documentHolder = document.getElementById(inputId);
	if (commandAlias == 'put') {
		arguments['text'] = documentHolder.value;
	}
	var encodedCmd = encodeCmd(clientId, commandAlias, arguments, 'document');
	if (encodedCmd != '?') {
		$.ajax({ url: CMD_EXECUTOR_URL,
		     type: 'POST',			     
		     dataType: 'text',
		     data: { 'clientId': clientId,
					 'cmd': /*encodeURIComponent(*/encodedCmd/*)*/
			       },
			 success: function(resultText) {
			 			//consoleInputElm.value = "";
			    	    // FIXME: parse error from status: result(ok) or result(error(\"" + errorText + "\"))
			        },			       
		     error: function(request, textStatus, error) {
			    	   // _log('send from cmd button failed', textStatus);	    	   
			    	   showGeneralError(request.status + ': ' + request.statusText + ' (' + textStatus + ')');
				    }
		   });		
		// makeRequest(CMD_EXECUTOR_URL, 'clientId=' + clientId + '&cmdXML=' + encodeURIComponent(cmdXML), null, true);
	} else {
		alert('document command cannot be parsed');
	}
	return false;
}

function sendButtonOnClick(clientId, inputId) {
	var consoleInputElm = document.getElementById(inputId);
	if (consoleInputElm) {
		var consoleLine = consoleInputElm.value;
		// FIXME: put it in the stack and clear only on successful perform of the command
		//        also, show command performing process
		consoleInputElm.value = "";
		if (consoleLine.length > 0) {
			var encodedCmd = parseCommandLine(clientId, consoleLine, 'main');
			if (encodedCmd != '?') {
				$.ajax({ url: CMD_EXECUTOR_URL,
				     type: 'POST',	
				     dataType: 'text',				     
				     data: { 'clientId': clientId,
							 'cmd': /*encodeURIComponent(*/encodedCmd/*)*/
					       }, 
					 success: function() {
					 			//consoleInputElm.value = "";
					    	    // FIXME: parse error from status: result(ok) or result(error(\"" + errorText + "\"))
					          }, 
				     error: function(request, textStatus, error) {
					    	   // _log('send button execution failed', textStatus);
					    	   showGeneralError(request.status + ': ' + request.statusText + ' (' + textStatus + ')');
						    }
				   });
				// makeRequest(CMD_EXECUTOR_URL, 'clientId=' + clientId + '&cmdXML=' + encodeURIComponent(cmdXML), null /*clearInputFunc*/, true);
			} else {
				alert('console command cannot be parsed');
			}
		}
	} else {
		alert('console input element is not found by id ' + inputId);
	}
	return false;
}

function getClientOnClick(inputId, clientsHolderId) {
	getFullClient($('#' + inputId).val(), $('#' + clientsHolderId));
	return false;
}