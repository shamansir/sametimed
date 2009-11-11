/* ==============================/ UTILS /=================================== */

function blockEnter(evt) {
    evt = (evt) ? evt : event;
    var charCode = (evt.charCode) ? evt.charCode :((evt.which) ? evt.which : evt.keyCode);
    if (charCode == 13) {
        return false;
    } else {
        return true;
    }
}

function createMethodReference(obj, func) {
	return function() {
		func.apply(obj, arguments);
	};
}
