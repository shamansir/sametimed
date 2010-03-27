var cometd = $.cometd;

var Sametimed = $.inherit({
	
	__constructor : function() {
		this.config = null;
		this._getConfReq = '';
	},	
	
	init: function(getConfReq) {
		
		this._getConfReq = getConfReq || 'getconf';

		// FIXME: lock screen and show user an option to cancel this request
		
		var this_ = this;
		
		$.ajax({ url: this._getConfReq,
				 async: false,
				 dataType: 'json',
				 success: function(data) {
			         this_.config = data;
		         }});
		
		// FIXME: unlock screen here		
		
		if (this.config) {
				    
			cometd.init({
			    url: this.config.cometdURL
			});		
			
			cometd.subscribe(this.config.channels.updChannel, 
							 createMethodReference(this, this.gotUpdate));		
			
			cometd.publish(this.config.channels.joinChannel, 
					       {test: 'test'});
			
			// TODO: unsubscribe and disconnect on exit
		} else {
			alert('getting configuration from server is failed, please check ' +
					'connection.');
		}
	},
	
	gotUpdate: function(cometObj) {
		if (console) console.log('update ', cometObj.data);
	}	
	
});

var sametimed = new Sametimed();