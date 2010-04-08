var cometd = $.cometd;

var SametimedModule = $.inherit({
	
	__constructor: function(id) {
		this.id = id;
		_log("module '" + this.id + "' instance created");
	}
	
});


var SametimedClient = $.inherit({
	
	__constructor: function(username, modulesData) {
		this._modulesData = modulesData;	
		this.connected = false;
		this.modules = {};
		this.username = username;
		_log("client instance is created, username is '" + username + "'");
		_log('modules data received: ', this._modulesData);
	},
	
	handleConnect: function() {
		_log('client connected');
		this.connected = true;
		if (this._modulesData.size > 0) _log('preparing modules'); 
		for (moduleId in this._modulesData) {
			// FIXME: instantiate using classes from their js-files
			this.modules[moduleId] = new SametimedModule(this._modulesData[moduleId]); 
		}
	},	

	preExecuteCmd: function(command) {
	},
	
	executeUpdate: function(update) {
	}
	
});

var Sametimed = $.inherit({
	
	__constructor: function() {
		this.config = null;
		this.sclient = null;
		this._srvAcessible = false;		
		this._getConfReq = '';
		_log('Sametimed instance created');
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
			
			cometd.subscribe(this.config.channels.updChannel,					
							 createMethodReference(this, this._gotUpdate));
			cometd.subscribe(this.config.channels.cfrmChannel,					
					 		 createMethodReference(this, this._gotConfirm));			
			
			$('#sametimed-login-submit').click(
							createMethodReference(this, this._onJoinBtnClick));
			
			this._srvAcessible = true; 
			
			_log('Sametimed instance initialized');
			
			// TODO: unsubscribe and disconnect on exit
		} else {
			alert('getting configuration from server is failed, please check ' +
					'connection.');
		}
	},
		
	_onJoinBtnClick: function() {
		if (this._srvAcessible) {
			var username = $('#sametimed-login').val();
			_log("trying to connect as user '", username, "'");
			if (!this.sclient || !this.sclient.connected) {
				this.sclient = new SametimedClient(username, this.config.modules); 			
				cometd.publish(this.config.channels.joinChannel, { 'username': username });
				// FIXME: disable login field and button				
			} else {
				alert('already connected. reload page to reconnect with ' +
				      'different user');
				_log('already connected');
			}	
		} else {
			alert('not connected, join is not performed');
			_log('not connected, join is not performed');
		}	
	},
	
	_gotUpdate: function(cometObj) {
		_log('update ', cometObj.data);
		if (this.sclient) {
			this.sclient.executeUpdate(cometObj.data);
		}
	},
	
	_gotConfirm: function(cometObj) {
		_log('join confirmation status: ', cometObj.data);
		if (this.sclient) {
			if ((cometObj.data.status == 'ok') 
			    && (cometObj.data.username == this.sclient.username)) {
				
				this.sclient.handleConnect(); 
			} // handle 'passed' status?
		} 
	}
	
});

var sametimed = new Sametimed();