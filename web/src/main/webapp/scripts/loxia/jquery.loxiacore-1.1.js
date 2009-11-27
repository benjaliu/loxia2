(function($) {
	var _global = this;
	if(typeof this["loxia"] == "undefined"){
		this.loxia = {
				SUCCESS : "success",
				ERROR : "error",				
				debug : false, //debug mode switch
				region : '', //default region info
				dateFormat: "yy-mm-dd", //default date format
				
				getViewport : function(){
					var w, h;					 
					// the more standards compliant browsers (mozilla/netscape/opera/IE7) use window.innerWidth and window.innerHeight			 
					if (typeof window.innerWidth != 'undefined'){
					     w = window.innerWidth, h = window.innerHeight;
					}			 
					// IE6 in standards compliant mode (i.e. with a valid doctype as the first line in the document)
					else if (typeof document.documentElement != 'undefined'
					    && typeof document.documentElement.clientWidth != 'undefined' && document.documentElement.clientWidth != 0){
					      w = document.documentElement.clientWidth, h = document.documentElement.clientHeight;
					}			 						 
					else { // older versions of IE		
					      w = document.getElementsByTagName('body')[0].clientWidth, h = document.getElementsByTagName('body')[0].clientHeight;
					}
					return [w,h];
				},
		
				isString: function(obj){
					return typeof obj == "string" || obj instanceof String;
				},
				
				upperFirstLetter: function(str){
					return str.substring(0,1).toUpperCase() + str.substring(1);
				},
				
				getLocaleMsg: function(msg, args){
					if(!args)
						return this.regional[this.region][msg];
					if(!$.isArray(args))
						args = [args];
					
					msg = this.regional[this.region][msg];
					var params = msg.match(/\{\d+\}/ig);
					if(!params || params.length == 0) return msg;
					msg = msg.replace(/\{\d+\}/ig,"#");
					
					for(var i=0; i< params.length; i++){
						var index = parseInt(params[i].replace(/\{/,"").replace(/\}/,""));
						msg = msg.replace(/\#/,args[index]? args[index] : "");
					}
					return msg;
				},
				
				getObject: function(propName, context){
					context = context || _global;
					var parts = propName.split(".");			
					for(var i=0, pn; context &&(pn = parts[i]); i++){
						context = (pn in context ? context[pn] : undefined);
					}
					return context;
				},
				setObject: function(propName, value, context){
					context = context || _global;
					var parts = propName.split(".");	
					var p = parts.pop();
					for(var i=0, pn; context &&(pn = parts[i]); i++){
						context = (pn in context ? context[pn] : context[pn]={});
					}
					return (context && p ? (context[p]=value) : undefined);
				},
				hitch : function(scope, method){
					if(!method){
						method = scope;
						scope = null;
					}
					if(this.isString(method)){
						scope = scope || _global;
						if(!scope[method]){ throw(['hitch: scope["', method, '"] is null (scope="', scope, '")'].join('')); }
						return function(){ return scope[method].apply(scope, arguments || []); }; // Function
					}
					return !scope ? method : function(){ return method.apply(scope, arguments || []); };
				},
				_setValue : function(obj, name, value){
					if(value == null) return;
					var val = obj[name];
					if(this.isString(val)){
						obj[name] = [val, value];
					}else if($.isArray(val)){
						obj[name].push(value);
					}else{
						obj[name] = value;
					}
				},
				_fieldValue : function(domNode){
					var ret = null;
					var type = (domNode.type||"").toLowerCase();
					if(domNode.name && type && !domNode.disabled){
						if(type == "radio" || type == "checkbox"){
								if(domNode.checked){ ret = domNode.value }
						}else if(domNode.multiple){
							ret = [];
							$("option",domNode).each(function(){
								if(this.selected)
									ret.push(this.value);
							});
						}else{
							ret = domNode.value;
						}
					}
					return ret;
				},
				_formToObj : function (form){
					if(!form) return {};
					form = this.isString(form) ? $("#" + form).get(0) : form;
					var ret = {};
					var exclude = "file|submit|image|reset|button|";
					var _this = this;
					$(form.elements).each(function(){
						var name = this.name;
						var type = (this.type||"").toLowerCase();
						if(name && type && exclude.indexOf(type) == -1 && !this.disabled){
							_this._setValue(ret, name, _this._fieldValue(this));
						}
					});
					return ret;
				},
				asyncXhr : function(url, args, callback){
					var options = {};
					if(arguments.length == 1)
						options = url;
					else{
						options = args;
						options["url"] = url;
						if(callback)
							options["success"] = callback;
					}
					//set default type to "json"
					options.dataType = options.dataType || "json";
					//init data if not set
					options.data = $.extend({},options.data, this._formToObj(options.form));				

					//console.dir(options);
					$.ajax(options);
				},
				asyncXhrGet : function(url, args, callback){
					if(args) args["type"] = "GET";
					else url["type"] = "GET";
					this.asyncXhr(url, args, callback);
				},
				asyncXhrPost : function(url, args, callback){
					if(args) args["type"] = "POST";
					else url["type"] = "POST";
					this.asyncXhr(url, args, callback);
				},
				syncXhr : function(url, args){
					var _data;
					var addiOpts = {
						async : false,
						success : function(data, textStatus){
							_data = data;
						},
						error : function(XMLHttpRequest, textStatus, errorThrown){
							_data = {};
							var exception = {};
							exception["message"] = "Error occurs when fetching data from url:" + this.url;
							exception["cause"] = textStatus? textStatus : errorThrown;
							_data["exception"] = exception;
						}
					}
					if(args){
						$.extend(args, addiOpts);
					}else
						$.extend(url, addiOpts);
					this.asyncXhr(url, args);
					//console.dir(_data);
					return _data;
				},
				syncXhrGet : function(url, args){
					if(args) args["type"] = "GET";
					else url["type"] = "GET";
					return this.syncXhr(url, args);
				},
				syncXhrPost : function(url, args){
					if(args) args["type"] = "POST";
					else url["type"] = "POST";
					return this.syncXhr(url, args);
				},
				log : function(msg){
					if(!this.debug) return;
					if(_global.console){
						_global.console.log(msg);
					}else
						alert(msg.outerHTML ? "Dom Object: " + msg.outerHTML : msg);
				}
		};
		
		loxia.regional = [];
		logix.regional[''] = {
			INVALID_NUMBER : "Invalid Number",
			INVALID_DATE : "Invalid Date"
		};
		
		checkNumber = function(value, obj){
			var value = $.trim(value);
			if(!value) return loxia.SUCCESS;
			var prefix = value.charAt(0);
			if(prefix == "+" || prefix == "-"){
				value = value.substring(1);
			}else
				prefix = "";
			value = value.replace(/^(0(?=\d))+/,"");
			
			var decimal = $(obj).data("decimal");
			var regex = new RegExp("^\\d+$");
			if(decimal){
				regex = new RegExp("^\\d+\\?\\d{0," + decimal + "}$");
			}else{
				decimal = 0;				
			}
			
			if(!regex.test(value))
				return loxia.ERROR + "^" + loxia.getLocaleMsg("INVALID_NUMBER");
			
			value = value.replace(/^\./,"0.");
			value = value.replace(/\.$/,".0");
			value = prefix + value;
		}
	}
})(jQuery);
