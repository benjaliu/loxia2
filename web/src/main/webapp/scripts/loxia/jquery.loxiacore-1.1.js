(function($) {
	var _global = this;
	if(typeof this["loxia"] == "undefined"){
		this.loxia = {
				SUCCESS : "success",
				ERROR : "error",				
				debug : false, //debug mode switch
				region : '', //default region info
				dateFormat: "yy-mm-dd", //default date format,
				pageLock : true, //default page locking after submitting
				onionPage : undefined,
				
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
					return {width : w, height : h};
				},
		
				isString: function(obj){
					return typeof obj == "string" || obj instanceof String;
				},
				
				upperFirstLetter: function(str){
					return str.substring(0,1).toUpperCase() + str.substring(1);
				},
				
				getLocaleMsg: function(msg, args){
					var localeMsg = this.regional[this.region][msg];
					if(localeMsg == undefined || localeMsg == null)
						localeMsg = this.regional[this.region][''];
					if(localeMsg == undefined || localeMsg == null) return msg;
					if(!args)
						return localeMsg;
					if(!$.isArray(args))
						args = [args];
					
					var params = localeMsg.match(/\{\d+\}/ig);
					if(!params || params.length == 0) return localeMsg;
					localeMsg = localeMsg.replace(/\{\d+\}/ig,"#");
					for(var i=0; i< params.length; i++){
						var index = parseInt(params[i].replace(/\{/,"").replace(/\}/,""));
						localeMsg = localeMsg.replace(/\#/,(args[index] != undefined && args[index] != null)? "" + args[index] : "");
					}
					return localeMsg;
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
				isLoxiaWidget : function(context){
					return $(context).hasClass("loxia");
				},
				init : function(settings){
					$.extend(this, settings);
					if(this.pageLock)
						if(this.onionPage)
							$(document).find("body").loxiaonionpage({layer : this.onionPage});
						else
							$(document).find("body").loxiaonionpage();
					this.initContext();
				},
				initContext : function(context){
					if(context == undefined) context = document;
					if($(context).attr("loxiaType")) this.initLoxiaWidget(context);
					else{
						$(context).find("> table[loxiaType]").each(function(){
							loxia.initLoxiaWidget(this);
						});
						$(context).find("[loxiaType]").each(function(){
							loxia.initLoxiaWidget(this);
						});
					}
				},
				initLoxiaWidget : function (context){
					switch($(context).attr("loxiaType")){
					case "button":
						$(context).loxiabutton();
						break;
					case "input":
						$(context).loxiainput();
						break;
					case "number":
						$(context).loxianumber();
						break;
					case "date":
						$(context).loxiadate();
						break;
					case "select":
						$(context).loxiaselect();
						break;
					case "button":
						break;
					case "table": 						
						var settings = $(context).attr("settings");
						settings = settings ? _global[settings] : {};
						$(context).loxiatable($.extend({},settings));
						break;
					case "edittable":
						var settings = $(context).attr("settings");
						settings = settings ? _global[settings] : {};
						$(context).loxiaedittable($.extend({},settings));
						break;						
					}
				},
				lockPage : function(){
					if(this.pageLock){
						var onion = $(document).find("body").data("loxiaonionpage");
						onion.show();
					}
				},
				unlockPage : function(){
					if(this.pageLock){
						var onion = $(document).find("body").data("loxiaonionpage");
						onion.hide();
					}
				},
				val : function(obj){
					if(obj == undefined) return null;
					if(this.isLoxiaWidget(obj)){
						var baseClass = $(obj).data("baseClass");
						if(baseClass){
							return $(obj).data(baseClass).val();
						}
					}
					if($(obj).is("input,select,textarea")) return $(obj).val();
					var firstInputItem = $(obj).find("input,select,textarea").get(0);
					if(firstInputItem){
						if(this.isLoxiaWidget(firstInputItem)){
							var baseClass = $(firstInputItem).data("baseClass");
							if(baseClass){
								return $(firstInputItem).data(baseClass).val();
							}else
								return null;
						}						
						else return $(firstInputItem).val();
					}else
						return $(obj).text();
				},
				validateForm : function(form){
					form = this.isString(form) ? $("#" + form).get(0) : form;
					var errorMsg = [];
					var fieldErrorNums = 0;
					$("input.loxia,select.loxia,textarea.loxia").
						each(function(){
							if($(this).attr("formCheck") == "false")
								return;
							if($(this).data("baseClass")){
								var baseClass = $(this).data("baseClass");
								if($(this).data(baseClass).getState() == null)
									$(this).data(baseClass).check();
								if(!$(this).data(baseClass).getState())
									fieldErrorNums ++;
							}							
						});
					
					if(fieldErrorNums > 0){
						errorMsg.push(this.getLocaleMsg("VALIDATE_FIELD_ERROR",[fieldErrorNums]));
						return errorMsg;
					}
					
					var formValidateMethod = $(form).attr("validate");
					if(!formValidateMethod){
						formValidateMethod = $(form).attr("name") + "Validate";
					}
					if(_global[formValidateMethod] && $.isFunction(_global[formValidateMethod])){
						var ret = this.hitch(_global[formValidateMethod])(form);
						if(ret != this.SUCCESS){
							errorMsg.push(this.getLocaleMsg("VALIDATE_FORM_ERROR"));
							if($.loxia.isString(ret))
								errorMsg.push(ret);
							else{
								for(var i=0; i< ret.length; i++)
									errorMsg.push(ret[i]);
							}					
						}
					}
					return errorMsg;
				},
				submitForm : function(form){
					form = this.isString(form) ? $("#" + form).get(0) : form;
					var errorMsg = this.validateForm(form);
					if(errorMsg.length == 0){
						//success
						form.submit();
					}else{
						//show errors	
						$(form).trigger("formvalidatefailed", [errorMsg]);
					}
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
		loxia.regional[''] = {
			INVALID_EMPTY_DATA : "Current Input is a mandatory one",
			INVALID_NUMBER : "Invalid Number",
			INVALID_DATE : "Invalid Date",
			DATA_EXCEED_RANGE : "Input data exceeds range",
			
			VALIDATE_FIELD_ERROR : "{0} field error(s) found.",
			VALIDATE_FORM_ERROR : "Form validation failed:",
			
			TABLE_PER_PAGE : "Per Page",
			TABLE_PAGER_FIRST : "First Page",
			TABLE_PAGER_PREVIOUS : "Previous Page",
			TABLE_PAGER_NEXT : "Next Page",
			TABLE_PAGER_LAST : "Last Page",
			TABLE_PAGER_GOTO : "Goto Page",
			TABLE_PAGER_RELOAD : "Reload",			
			TABLE_PAGE : "Page",
			TABLE_PAGE_INFO : "{0} item(s)",
			TABLE_PAGE_ERROR_OCCURS : "Error occurs at reloading.",
			TABLE_PAGE_RELOAD : "Table is refreshed.",
			TABLE_BARBTN_ADD : "Add New Line",
			TABLE_BARBTN_DELETE : "Delete Selected Lines"
		};		
		
		loxia.defaults = {
			baseClass : "",
			required: false,
			lastRightValue : undefined,
			checkmaster : "",
			state : null,
			errorMessage : null
		};
		
		loxia.loxiaWidget = {			
			_setValue : function(value){return this.element.val(value)}, //can be overridden
			_getValue : function(){return this.element.val()},
			val : function(value){
				if(value != undefined){
					this._setValue(value);
					var result = this.check();					
					return this.element;
				}else
					return this._getData("lastRightValue");
			},
			clearState : function(){
				this._setData("state", null);
				this._setData("errorMessage", null);
				this.element.removeClass("ui-state-error");
			},
			getState : function(){
				return this.options.state;
			},
			setState : function(st, msg){				
				this.clearState();
				this._setData("state", (st == true));
				if(!this._getData("state")){
					this.element.addClass("ui-state-error");
					this._setData("errorMessage", msg);
				}
			},
			check : function(){
				this.clearState();
				var value = this._getValue();
				if(this._getData("required") && value == ""){
					this.setState(false, loxia.getLocaleMsg("INVALID_EMPTY_DATA"));
					return false;
				}
				
				if(value == this.val()){
					this.setState(true);
					return true;
				}
				
				if(this._getData("checkmaster")){					
					var cms = this._getData("checkmaster").split(",");
					var executed = {};
					for(var cm,i=0; cm=cms[i]; i++){
						if(cm in executed) continue;
						executed[cm] = cm;
						var result = loxia.hitch(cm)(value,this);
						if(result.indexOf(loxia.SUCCESS) != 0){
							this.setState(false, result);
							return false;
						}
						if(result.length > 8){
							//set the formatted value
							value = result.substring(8);						
						}
					}
				}
				this.setState(true);
				this._setData("lastRightValue", value);
				this.element.trigger("valuechanged", [value]);
				return true;
			}
		};
		
		loxia.loxiaGetter = "val check getState getBaseClass";
	}	
	
	checkNumber = function(value, obj){
		var value = $.trim(value);
		if(!value) return loxia.SUCCESS;
		var prefix = value.charAt(0);
		if(prefix == "+" || prefix == "-"){
			value = value.substring(1);
		}else
			prefix = "";
		value = value.replace(/^(0(?=\d))+/,"");
		
		var decimal = obj._getData("decimal");
		var regex = new RegExp("^\\d+$");
		if(decimal){
			regex = new RegExp("^\\d+\\.?\\d{0," + decimal + "}$");
		}else{
			decimal = 0;				
		}
		if(!regex.test(value))
			return loxia.getLocaleMsg("INVALID_NUMBER");
		
		value = value.replace(/^\./,"0.");
		value = value.replace(/\.$/,".0");
		value = prefix + value;
		
		var v = parseFloat(value);
		var min = obj._getData("min");
		var max = obj._getData("max");
		if((min && v < min) || (max && v > max))
			return loxia.getLocaleMsg("DATA_EXCEED_RANGE");
		
		return loxia.SUCCESS + "^" + v.toFixed(decimal);
	}
	
	checkDate = function(value,obj){
		var config = $.datepicker._getFormatConfig($.datepicker._getInst(obj.element.get(0)));
		try{
			var currDate = $.datepicker.parseDate(loxia.dateFormat,value,config);
			var minDate = obj._getData("min");
			var maxDate = obj._getData("max");
			if((minDate && currDate < minDate) ||
					(maxDate && currDate > maxDate))
				return loxia.getLocaleMsg("DATA_EXCEED_RANGE");
		}catch(e){
			return loxia.getLocaleMsg("INVALID_DATE");
		}				
		return loxia.SUCCESS;
	}
		
	var loxiaOnionPage = {
		_init : function(){
			if(this.options.layer){
				if(loxia.isString(this.options.layer))
					this.options.layer = $("#" + this.options.layer).get(0);
			}else{
				$(document).find("body").append($.loxiaonionpage.onDiv);
				this.options.layer = $(".loxia-onion-container").get(0);
			}
			
			$(this.options.layer).hide();
		},
		
		show : function(){
			$(this.options.layer).show();
		},
		
		hide : function(){
			$(this.options.layer).hide();
		}
	};
	
	$.widget("ui.loxiaonionpage", loxiaOnionPage); 
	$.ui.loxiaonionpage.getter = ""; 
	$.ui.loxiaonionpage.defaults = {
		layer : undefined
	};
	$.loxiaonionpage = {
		onDiv : '<div class="loxia loxia-onion-container"><div class="loxia-onion"></div></div>'
	};
})(jQuery);
