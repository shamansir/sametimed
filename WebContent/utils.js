var ERROR_BOX_ID = 'error';

/* ====== SHOW_ERROR ==== */

function showGeneralError(errorText) {
	$('#' + ERROR_BOX_ID)
		.removeClass('no-errors')
		.addClass('have-errors')
		.append($('<span />').text(errorText));
}

/* ==============================/ UTILS /=================================== */

function _log() {
	if (window.console && console && console.log) console.log.apply(console, arguments);
}

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
