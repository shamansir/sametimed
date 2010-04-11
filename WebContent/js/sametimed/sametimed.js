var cometd = $.cometd;

var SametimedModule = $.inherit({
	
	__constructor: function(id) {
		this.id = id;
		_log("module '" + this.id + "' instance created");
	}
	
});


var SametimedClient = $.inherit({
	
	__constructor: function(username, modulesIds) {
		this.connected = false;
		this.modules = {};
		this.username = username;
		_log(this.username, "> client instance is created, username is '" + this.username + "'");
	},
	
	handleConnect: function() {
		_log(this.username, "> client '" + this.username + "' connected to service");
		this.connected = true;
	},
	
	addModule: function(moduleId, module) {
		_log(this.username, '> module added: ', module);
		this.modules[moduleId] = module;
	},

	handleCommand: function(command) {
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
			cometd.subscribe(this.config.channels.mftryChannel,
					         createMethodReference(this, this._gotModule));
			
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
			if (username.length > 0) {
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
	},
	
	_gotModule: function(cometObj) {
		_log('module structure: ', cometObj.data);
		if (this.sclient) {
			this.sclient.addModule(cometObj.data.moduleId,
					               cometObj.data);
		}
	}
	
});

var sametimed = new Sametimed();