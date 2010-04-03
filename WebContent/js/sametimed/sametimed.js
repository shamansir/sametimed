var cometd = $.cometd;

/* var SametimedClient = $.inherit({
	
	__constructor: function(id) {
		this.id = id;
		this.joined = false;
	}
	
}); */

var Wavelet = $.inherit({
	
	__constructor: function() {
		this.modules = {};
	},

	preExecuteCmd: function(command) {
	},
	
	executeUpdate: function(update) {
	}
	
});

var Sametimed = $.inherit({
	
	__constructor: function() {
		this.config = null;
		this.sclients = {};
		this.connected = false;
		this.joined = false;
		this._getConfReq = '';
	},	
	
	init: function(getConfReq) {
		
		this._getConfReq = getConfReq || 'getconf';

		// FIXME: lock screen and show user an option to cancel this request 
		//        or set timeout
		
		var this_ = this;
		
		$.ajax({ url: this._getConfReq,
				 async: false,
				 dataType: 'json',
				 success: function(data) {
			         this_.config = data;
		         }});
		
		// FIXME: unlock screen here		
		
		if (this.config) {
			
			_log('config: ', this.config);
				    
			cometd.init({
			    url: this.config.cometdURL
			});
			
			this.connected = true;
			
			cometd.subscribe(this.config.channels.updChannel,					
							 createMethodReference(this, this.gotUpdate));
			
			$('#sametimed-login-submit').click(
							createMethodReference(this, this._onJoinBtnClick));
			
			// TODO: unsubscribe and disconnect on exit
		} else {
			alert('getting configuration from server is failed, please check ' +
					'connection.');
		}
	},
	
	gotUpdate: function(cometObj) {
		_log('update ', cometObj.data);
		/* if (this.sclients[id]) {
			_log();
		} */
	},
	
	_onJoinBtnClick: function() {
		if (this.connected) {
			var username = $('#sametimed-login').val();
			_log('trying to connect as ', username);
			cometd.publish(this.config.channels.joinChannel, 
				                      { 'username': username });
		} else {
			alert('not connected, join is not performed');
			_log('not connected, join is not performed');
		}	
	}
	
});

var sametimed = new Sametimed();