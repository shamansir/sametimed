var cometd = $.cometd;

var Sametimed = $.inherit({
	
	__constructor : function() {
	
	},	
	
	init: function(settings) {
		cometd.init({
		    url: settings.cometdURL
		});		
		
		// cometd.handshake(); // done in init
		
		cometd.subscribe('/w/upd', 
						 createMethodReference(this, 'gotUpdate'));
		// TODO: unsubscribe and disconnect on exit
		
		cometd.publish('/w/join', {test: 'test'});
	},
	
	gotUpdate: function(data) {
		alert('update ' + data);
	}	
	
});

var sametimed = new Sametimed();