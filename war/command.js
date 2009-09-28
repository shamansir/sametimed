var CMD_EXECUTOR_URL = '/same_timed/cmd_exec';
// it must to be identical to the function, described in initRedrawJSFunc() method
// var REDRAW_JS_FUNC_NAME = 'redrawPanel'; FIXME: use constant 

function parseCommandLine(line) {
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
		return createCommandXML(commandName, arguments);		
	} else {
		return "?";
	}
}

function createCommandXML(commandName, arguments) {
	var xml = '<command><name>' + commandName + '</name>'; 
	for (argumentName in arguments) {
		var argument = arguments[argumentName];
		if (argument !== undefined) {
			xml += '<argument name="' + argumentName + '">' +
				arguments[argumentName] + "</argument>";
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

function cmdButtonOnClick(holderElementId, clientId, commandXML) {
	
}

function sendButtonOnClick(holderElementId, clientId, inputId) {
	var consoleInputElm = document.getElementById(inputId);
	if (consoleInputElm) {
		var consoleLine = consoleInputElm.value;
		if (consoleLine.length > 0) {
			var cmdXML = parseCommandLine(consoleLine);
			if (cmdXML != '?') {
				/* var redrawJSFunc = function(request, response) {
						// FIXME: it is required to pass all panels ids to rerender
						redrawPanel(holderElementId, response);
					} */
				makeRequest(CMD_EXECUTOR_URL, 'clientId=' + clientId + '&cmdXML=' + escape(cmdXML), null /*redrawJSFunc*/, true);
			} else {
				alert('console command cannot be parsed')
			}
		}
	} else {
		alert('console input element is not found by id ' + inputId);
	}
}