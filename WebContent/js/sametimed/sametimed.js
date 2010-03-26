var cometd = $.cometd;

var Sametimed = $.inherit({
	
	__constructor : function() {
	
	},	
	
	init: function(settings) {

		// TODO: ask for config from the server with synchronous request
		
		cometd.init({
		    url: settings.cometdURL
		});		
		
		cometd.subscribe('/w/upd', 
						 createMethodReference(this, this.gotUpdate));
		// TODO: unsubscribe and disconnect on exit
		
		cometd.publish('/w/join', {test: 'test'});
	},
	
	gotUpdate: function(data) {
		if (console) console.log('update ', data);
	}	
	
});

var sametimed = new Sametimed();