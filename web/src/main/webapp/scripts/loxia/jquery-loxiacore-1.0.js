(function($) {
	var _globle = this;
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
				scope = scope || _globle;
				if(!scope[method]){ throw(['hitch: scope["', method, '"] is null (scope="', scope, '")'].join('')); }
				return function(){ return scope[method].apply(scope, arguments || []); }; // Function
			}
			return !scope ? method : function(){ return method.apply(scope, arguments || []); };
		},
		_setValue() : function(obj, name, value){
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
		},
		_formToObj : function (form){
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
		asyncXhr : function(options){
			//set default type to "json"
			options.dataType = options.dataType || "json";
			$.ajax(options);
		},
		asyncXhrGet : function(options){
		},
		asyncXhrPost : function(options){
		},
		syncXhr : function(options){
		},
		syncXhrGet : function(options){
		},
		syncXhrPost : function(options){
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
				$(obj).removeClass($(obj).attr("baseClass") + "Error");
				$(obj).attr("state","");
				$(obj).attr("errorMsg","");
			},
			
			setState : function(obj, state, msg){
				state = state == true ? true : false;
				$(obj).removeClass($(obj).attr("baseClass") + "Error");
				$(obj).attr("state",state);
				if(! state){
					$(obj).attr("errorMsg",msg);
					$(obj).addClass($(obj).attr("baseClass") + "Error");
				}
			}
		}
	};
	var _l = $.loxia;
	
	$.log = function(msg){
		if(!_l.defaultConfig.debug) return;
		if(_globle.console){
			_globle.console.log(msg);
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
				$.log(this);
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
				
					//do check					
					_l.lidget.clearState(this);
					
					var value = $(this).val();
					if($(this).attr("trim") == "true"){
						value = $.trim(value);
						$(this).val(value);
					}
					var required = ($(this).attr("required") == "true");
					if(required && value == ""){
						$(this).addClass(baseClass + "Error");
						$(this).attr("state","false");
						$(this).attr("errorMsg","Mandatory Field");
					}else if(value == $(this).attr("lastRightValue")){
						$(this).attr("state","true");
					}else{
						var checkmasters = $(this).attr("checkmaster");
						if(checkmasters){
							var cms = checkmasters.split(",");
							for(var cm,i=0; cm=cms[i]; i++){
								var result = _l.hitch(cm)(value,this);
								if(result.indexOf(_l.SUCCESS) != 0){
									$(this).addClass(baseClass + "Error");
									$(this).attr("state","false");
									$(this).attr("errorMsg", result);
									break;
								}
								if(result.length > 8){
									//set the formatted value
									value = result.substring(8);
									$(this).val(value);
								}
							}
						}
					}
					if(!$(this).attr("state")){
						$(this).attr("state","true");
						$(this).attr("lastRightValue", value);
					}
					
					if($(this).attr("state") == "true"){
						//format
						if($(this).attr("formatter")){
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
		return "date format error";
	}; 
 })(jQuery);

(function($){
	$(document).ready(function(){
		$.loxia.init({debug:true});
	});
})(jQuery);