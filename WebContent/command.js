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
}

var CLIENT_RECEIVER_URL = './get_client_view'; 
var CMD_EXECUTOR_URL = './cmd_exec'; 

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
		return createCommandXML(cmdAuthor, commandName, arguments, sourceDoc);		
	} else {
		return "?";
	}
}

function createCommandXML(forClient, commandName, arguments, sourceDoc) {
	// FIXME: rewrite with JQuery
	var xml = '<command><name>' + commandName + '</name>';
	xml += '<owner-id>' + forClient + '</owner-id>';
	if ((commandName == 'say') || (commandName == 'undo')
		                       || (commandName == 'put')) {
		xml += '<for-document>' + sourceDoc + '</for-document>';
	}
	for (argumentName in arguments) {
		var argument = arguments[argumentName];
		if (argument !== undefined) {
			xml += '<argument name="' + argumentName + '">' +
				escapeXML(arguments[argumentName]) + "</argument>";
		}
	}
	xml += '</command>';
	return xml;
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

function parseUpdateMessage(updateMessage) {
	var msgRoot = $(updateMessage);
	var msgType = msgRoot.find('name').text();
	var ownerId = msgRoot.find('owner-id').text();
	var typeStr = msgRoot.find('argument[name=alias]').text();
	var valueStr = msgRoot.find('argument[name=value]').text();	
	return {
			clientId: ownerId,
			modelType: typeStr,
			modelValue: JSON.parse(unescapeXML(valueStr))
		};
}

function updateReceived(updateMessageXML) {	
	renderUpdate(parseUpdateMessage(updateMessageXML));
}

/* ====== GET FULL CLIENT ====== */

function getFullClient(username, clientsHolder) {
	var addClientFunc = function(response, responseText) {
			if (responseText) {
				renderClient(responseText, clientsHolder);
			}
		};
	makeRequest(CLIENT_RECEIVER_URL, 'username=' + username + '&ueq=false', addClientFunc, true);	
}

/* ====== ONCLICK ====== */

function cmdButtonOnClick(clientId, commandAlias, inputId) {
	var arguments = {};
	var documentHolder = document.getElementById(inputId);
	if (commandAlias == 'put') {
		arguments['text'] = documentHolder.value;
	}
	var cmdXML = createCommandXML(clientId, commandAlias, arguments, 'document');
	if (cmdXML != '?') {
		makeRequest(CMD_EXECUTOR_URL, 'clientId=' + clientId + '&cmdXML=' + encodeURIComponent(cmdXML), null, true);
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
			var cmdXML = parseCommandLine(clientId, consoleLine, 'main');
			if (cmdXML != '?') {
				/* var clearInputFunc = function(request, response) {
					consoleInputElm.value = "";
				}; */
				makeRequest(CMD_EXECUTOR_URL, 'clientId=' + clientId + '&cmdXML=' + encodeURIComponent(cmdXML), null /*clearInputFunc*/, true);
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