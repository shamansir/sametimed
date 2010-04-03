function _log() {
	if (window.console && console && console.log) console.log.apply(console, arguments);
}

function createMethodReference(obj, func) {
	return function() {
		func.apply(obj, arguments);
	};
}