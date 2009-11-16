(function($) {
	var _global = this;
	$.loxia = {
		SUCCESS : "success",
		ERROR : "error",
		defaultConfig: {debug: false},
		init: function(settings){
			$.extend(this.defaultConfig,settings);
			
			//init tooltip
			$('body').append('<div class="loxiaTooltip"></div>');
			
			this.initLidgets();
						
		},
		initLidgets: function(context){
			if(context == undefined)
				$('button.loxia,select.loxia,input.loxia,textarea.loxia').lidget();
			else
				$('button.loxia,select.loxia,input.loxia,textarea.loxia', context).lidget();
		},
		isString: function(obj){
			return typeof obj == "string" || obj instanceof String;
		},
		upperFirstLetter: function(str){
			return str.substring(0,1).toUpperCase() + str.substring(1);
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
			options.data = options.data || {};
			if(options.form){
				$j.extend(options.data, this._formToObj(options.form));				
			}
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
		validateForm : function(form){
			form = this.isString(form) ? $("#" + form).get(0) : form;
			var errorMsg = [];
			var fieldErrorNums = 0;
			$(".loxia[baseType='Input'],.loxia[baseType='Select'],.loxia[baseType='Textarea']").
				each(function(){
					if($(this).attr("state")){
						if("true" != $(this).attr("state")) fieldErrorNums ++;
					}else{
						var chkflg = $.loxia.lidget.check(this);
						if(!chkflg) fieldErrorNums ++;
					}
				});
			
			if(fieldErrorNums > 0){
				errorMsg.push("" + fieldErrorNums + " field error(s) found.");
				return errorMsg;
			}
			
			var formValidateMethod = $(form).attr("validate");
			if(!formValidateMethod){
				formValidateMethod = $(form).attr("name") + "Validate";
			}
			if(_global[formValidateMethod] && $.isFunction(_global[formValidateMethod])){
				var ret = $.loxia.hitch(_global[formValidateMethod])(form);
				if(ret != $.loxia.SUCCESS){
					errorMsg.push("Form validate error.");
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
				$(form).trigger("submitFailureEvent", [errorMsg]);
			}
		},
		html : {
			getPosition: function(obj) {
			    var curleft = 0;
			      var curtop = 0;
			      if (obj.offsetParent) {
			            do {
			                  curleft += obj.offsetLeft;
			                  curtop += obj.offsetTop;
			            } while (obj = obj.offsetParent);
			      }
			      return [curleft,curtop];
			}			
		},
		lidget : {
			clearState : function(obj){
				if(!$(obj).hasClass("loxia")) return;
				$(obj).removeClass($(obj).attr("baseClass") + "Error");
				$(obj).attr("state","");
				$(obj).attr("errorMsg","");
			},
			
			setState : function(obj, state, msg){
				if(!$(obj).hasClass("loxia")) return;
				state = state == true ? true : false;
				$(obj).removeClass($(obj).attr("baseClass") + "Error");
				$(obj).attr("state",state);
				if(! state){
					$(obj).attr("errorMsg",msg);
					$(obj).addClass($(obj).attr("baseClass") + "Error");
				}
			},
			
			check : function(obj){
				if(!$(obj).hasClass("loxia")) return true;
				this.clearState(obj);
				
				var value = $(obj).val();
				var baseClass = $(obj).attr("baseClass");
				var required = ($(obj).attr("required") == "true");
				if(required && value == ""){
					$(obj).addClass(baseClass + "Error");
					$(obj).attr("state","false");
					$(obj).attr("errorMsg","Mandatory Field");
					return false;
				}else if(value == $(obj).attr("lastRightValue")){
					$(obj).attr("state","true");
					return true;
				}else{
					var checkmasters = $(obj).attr("checkmaster");
					if(checkmasters){
						var cms = checkmasters.split(",");
						for(var cm,i=0; cm=cms[i]; i++){
							var result = $.loxia.hitch(cm)(value,obj);
							if(result.indexOf($.loxia.SUCCESS) != 0){
								$(obj).addClass(baseClass + "Error");
								$(obj).attr("state","false");
								$(obj).attr("errorMsg", result);
								return false;
							}
							if(result.length > 8){
								//set the formatted value
								value = result.substring(8);
								$(obj).val(value);
							}
						}
					}
				}
				if(!$(obj).attr("state")){
					$(obj).attr("state","true");
					$(obj).attr("lastRightValue", value);
					return true;
				}
				return false;
			}
		}
	};
	var _l = $.loxia;
	
	$.log = function(msg){
		if(!_l.defaultConfig.debug) return;
		if(_global.console){
			_global.console.log(msg);
		}else
			alert(msg.outerHTML ? msg.outerHTML : msg);
	};
	$.fn.log = function(msg){
		$.log(msg);
	    return this;
	};
	
	
	$.tooltip = {
		//will be implemented more complex
		defaultSettings: {},
		show : function(obj,msg,settings){
			settings = $.extend({},this.defaultSettings,settings);
			
			var position = _l.html.getPosition(obj);
			var toolTip = $('.loxiaTooltip');			
			toolTip.css({left : (position[0] + obj.offsetWidth + 2)+'px', 
	            	top : position[1]+'px'});
			toolTip.html(msg);
			toolTip.fadeIn("fast");
		},
		hide : function(obj){
			$(".loxiaTooltip").hide();
		}
	};

	$.fn.lidget = function(){
		this.each(function(){			
			var type = _l.upperFirstLetter($.trim(this.tagName.toLowerCase()));
			var isButton = ((type == "Input") && $(this).attr("type").toLowerCase() == "button")
							|| type == "Button";
			var isCheckBox = ((type == "Input") && $(this).attr("type").toLowerCase() == "checkbox");
			var isRadio = ((type == "Input") && $(this).attr("type").toLowerCase() == "radio");
			
			var baseType = type;
			if(isButton) baseType = "Button";
			else if(isCheckBox) baseType="CheckBox";
			else if(isRadio) baseType = "Radio";
			var baseClass = "loxia" + baseType;
			
			$(this).addClass(baseClass);
			$(this).attr("baseClass", baseClass);
			$(this).attr("baseType", baseType);
			
			if($(this).attr("required") == "true"){
				$(this).addClass(baseClass + "Required");
			}
			
			if($(this).val()){
				$(this).attr("lastRightValue", $(this).val());
			}
			
			$(this).focus(function(){				
				if(!isButton && !isCheckBox && !isRadio){
					$(this).addClass(baseClass + "Focused");
					var msg = $(this).attr("errorMsg");
					if(msg)
						$.tooltip.show(this,msg);
				}
			});
			
			$(this).blur(function(){
				if(!isButton && !isCheckBox && !isRadio){
					$(this).removeClass(baseClass + "Focused");
					$.tooltip.hide(this);
				
					var value = $(this).val();
					if($(this).attr("trim") == "true"){
						value = $.trim(value);
						$(this).val(value);
					}
					
					//do check
					if(_l.lidget.check(this)){						
						//format
						if($(this).attr("formatter")){
							value = $(this).val();
							value = _l.hitch($(this).attr("formatter"))(value);
							$(this).val(value);
						}	
					}					
				}
			});
		});
		return this;
	};
 	
	checkNumber = function(){
		return _l.SUCCESS + "^" + "12345";
	};
	checkDate = function(){
		return _l.SUCCESS;
	}; 
 })(jQuery);

(function($){
	$(document).ready(function(){
		$.loxia.init({debug:true});
	});
})(jQuery);