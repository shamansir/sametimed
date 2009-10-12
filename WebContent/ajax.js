/**
 * Browser-independent AJAX call
 *
 * @param {String} locationURL an URL to call, without parameters
 * @param {String} [parameters=null] a parameters list, in the form
 *        'param1=value1&param2=value2&param3=value3'
 * @param {Function(XHMLHTTPRequest, String, Object)} [onComplete=null] a function that
 *        will be called when the response (responseText or responseXML of
 *        XHMLHTTPRequest) will be received
 * @param {Boolean} [doSynchronous=false] make a synchronous request (onComplete
 *        will /not/ be called)        
 * @param {Boolean} [doPost=false] make a POST request instead of GET        
 * @param {Object} [dataPackage=null] any object to transfer to the onComplete
 *        listener
 * @return {XHMLHTTPRequest} request object, if no exceptions occured
 */

function makeRequest(locationURL, parameters, onComplete, doPost, dataPackage) {

    var http_request = false;
    try {
        http_request = new ActiveXObject("Msxml2.XMLHTTP");
    } catch (e1) {
        try {
            http_request= new ActiveXObject("Microsoft.XMLHTTP");
        } catch (e2) {
            http_request = new XMLHttpRequest();
        }
    }

    //if (http_request.overrideMimeType) { // optional
    //  http_request.overrideMimeType('text/xml');
    //}

    if (!http_request) {
      alert('Cannot create XMLHTTP instance');
      return false;
    }

    completeListener = function() {
        if (http_request.readyState == 4) {
            if (http_request.status == 200) {
                if (onComplete) onComplete(http_request, http_request ? http_request.responseText : null, dataPackage);
            }
        }
    };

    //var salt = hex_md5(new Date().toString());
    http_request.onreadystatechange = completeListener;
    if (doPost) {
		http_request.open('POST', locationURL, true);
		http_request.setRequestHeader("Content-type", "application/x-www-form-urlencoded");
		http_request.setRequestHeader("Content-length", parameters.length);
		http_request.setRequestHeader("Connection", "close");
		http_request.send(parameters);
    } else {
    	http_request.open('GET', locationURL + (parameters ? ("?" + parameters) : ""), true);
    	//http_request.open('GET', './proxy.php?' + parameters +
                    // "&salt=" + salt, true);
    	http_request.send(null);                        
    } 
   
}