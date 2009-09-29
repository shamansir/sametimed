/*
 * waveModelStr format:
 * 
 * client = {
 * 		clientId: <int>,
 * 		info: <string>, // information line
 * 		inbox: {<int>: <string>, ...}, // waves list, inbox number to wave id string
 * 		users: [<string>, ...], // full addresses, one by one   
 * 		chat: [{author: <string>, text: <string>}, ...], // chat lines
 * 		document: [{text: <string>, style: <string>}, ...}], // document chunks
 * 		console: [<string>, ...], // console history
 * 		errors: [<string>, ...], // errors happend while using the client
 * }
 */

function renderClient(waveModelStr) {
	/*
	if (window.console) {
		console.log(waveModelStr);
	} else {
		alert(waveModelStr);
	} */
}