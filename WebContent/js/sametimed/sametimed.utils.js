function createMethodReference(obj, func) {
	return function() {
		func.apply(obj, arguments);
	};
}