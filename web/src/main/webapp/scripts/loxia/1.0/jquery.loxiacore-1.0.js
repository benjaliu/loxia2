(function($) {
	var _global = this;
	$.loxia = {
		SUCCESS : "success",
		ERROR : "error",
		defaultConfig: {debug: false,
			dateFormat: "yy-mm-dd",
			tooltipContainers : [
       			  "<table class='loxiatip loxiatipUp' cellspacing='0' cellpadding='0'><tbody><tr><td class='tip-topleft'></td><td class='tip-top'></td><td class='tip-topright'></td></tr><tr><td class='tip-left'></td><td class='tip-content'></td><td class='tip-right'></td></tr><tr><td class='tip-bottomleft'></td><td><table class='tip-bottom' cellspacing='0' cellpadding='0'><tr><th></th><td><div></div></td><th></th></tr></table></td><td class='tip-bottomright'></td></tr></tbody></table>", //up
       			  "<table class='loxiatip loxiatipDown' cellspacing='0' cellpadding='0'><tbody><tr><td class='tip-topleft'></td><td><table class='tip-top' cellspacing='0' cellpadding='0'><tr><th></th><td><div></div></td><th></th></tr></table></td><td class='tip-topright'></td></tr><tr><td class='tip-left'></td><td class='tip-content'></td><td class='tip-right'></td></tr><tr><td class='tip-bottomleft'></td><td class='tip-bottom'></td><td class='tip-bottomright'></td></tr></tbody></table>", //down
       			  "<table class='loxiatip loxiatipLeft' cellspacing='0' cellpadding='0'><tbody><tr><td class='tip-topleft'></td><td class='tip-top'></td><td class='tip-topright'></td></tr><tr><td class='tip-left'></td><td class='tip-content'></td><td class='tip-right-tail'><div class='tip-right'></div><div class='tip-right-tail'></div><div class='tip-right'></div></td></tr><tr><td class='tip-bottomleft'></td><td class='tip-bottom'></td><td class='tip-bottomright'></td></tr></tbody></table>", //left
       			  "<table class='loxiatip loxiatipRight' cellspacing='0' cellpadding='0'><tbody><tr><td class='tip-topleft'></td><td class='tip-top'></td><td class='tip-topright'></td></tr><tr><td class='tip-left-tail'><div class='tip-left'></div><div class='tip-left-tail'></div><div class='tip-left'></div></td><td class='tip-content'></td><td class='tip-right'></td></tr><tr><td class='tip-bottomleft'></td><td class='tip-bottom'></td><td class='tip-bottomright'></td></tr></tbody></table>" //right
       			]
		},
		init: function(settings){
			$.extend(this.defaultConfig,settings);
			
			//init tooltip
			if(!this.defaultConfig.tooltipContainers ||
					this.defaultConfig.tooltipContainers.length != 4)
				throw new Error("Error configuration for tooltips");
			for(var c,i=0;c=this.defaultConfig.tooltipContainers[i];i++){
				$(c).appendTo("body");
			}
			$(".loxiatip").hide();					
		},
		initLidgets: function(context){
			if(context == undefined)
				$('button.lidget,select.lidget,input.lidget,textarea.lidget').lidget();
			else if($(context).hasClass("lidget"))
				$(context).lidget();
			else
				$('button.lidget,select.lidget,input.lidget,textarea.lidget', context).lidget();
		},
		isString: function(obj){
			return typeof obj == "string" || obj instanceof String;
		},
		getViewport : function(){
			var viewportwidth;
			var viewportheight;
			 
			// the more standards compliant browsers (mozilla/netscape/opera/IE7) use window.innerWidth and window.innerHeight			 
			if (typeof window.innerWidth != 'undefined')
			{
			     viewportwidth = window.innerWidth,
			     viewportheight = window.innerHeight
			}			 
			// IE6 in standards compliant mode (i.e. with a valid doctype as the first line in the document)
			else if (typeof document.documentElement != 'undefined'
			    && typeof document.documentElement.clientWidth !=
			    'undefined' && document.documentElement.clientWidth != 0)
			{
			      viewportwidth = document.documentElement.clientWidth,
			      viewportheight = document.documentElement.clientHeight
			}			 
			// older versions of IE			 
			else
			{
			      viewportwidth = document.getElementsByTagName('body')[0].clientWidth,
			      viewportheight = document.getElementsByTagName('body')[0].clientHeight
			}
			return [viewportwidth,viewportheight];
		},
		upperFirstLetter: function(str){
			return str.substring(0,1).toUpperCase() + str.substring(1);
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
		val : function(obj){
			if(obj == undefined) return null;
			if($(obj).is(".loxia")) return this.lidget.val(obj);
			if($(obj).is("input,select,textarea")) return $(obj).val();
			var firstInputItem = $(obj).find("input,select,textarea").get(0);
			if(firstInputItem){
				if($(firstInputItem).is(".loxia")) return this.lidget.val(firstInputItem);
				else return $(firstInputItem).val();
			}else
				return $(obj).text();
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
					if($(this).data("state") != undefined){
						if(!$(this).data("state")) fieldErrorNums ++;
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
			UP : "Up", DOWN : "Down", LEFT : "Left", RIGHT : "Right",			
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
			},
			showTooltip: function(obj, msg){
				var tooltipHolder = [200,50];
				var	position = [];
				var objPos = this.getPosition(obj);
				var tipPos = [objPos[0],objPos[1]];
				var offset = [0,0];
				var relatePos = [objPos[0] - $(document).scrollLeft(),
				                 objPos[1] - $(document).scrollTop()];
				var viewPos = $.loxia.getViewport();
				
				var posLeft = (viewPos[0] - relatePos[0] - obj.offsetWidth) > tooltipHolder[0];
				var posTop = (viewPos[1] - relatePos[1] - obj.offsetHeight) > tooltipHolder[1];
				
				var direction = this.RIGHT;
				if(!posLeft){
					if(posTop)
						direction = this.DOWN;
					else{
						direction = 
							(relatePos[0] - tooltipHolder[0] >= relatePos[1] - tooltipHolder[1])?
									this.LEFT : this.UP;
					}
				}
				
				var tooltip = $(".loxiatip" + direction);
				if(msg.indexOf("**") ==0){
					$(".tip-content",tooltip).html($("#" + msg.substring(2)).html());
				}else{
					$(".tip-content",tooltip).text(msg);					
				}
				tooltip.attr("show", "true");
				
				if(direction == this.RIGHT){
					tipPos[0] = tipPos[0] + obj.offsetWidth;
					tipPos[1] = tipPos[1] - (tooltip.height() - obj.offsetHeight)/2;
					offset[0] = -10;
				}else if(direction == this.LEFT){
					tipPos[0] = tipPos[0] - tooltip.width();
					tipPos[1] = tipPos[1] - (tooltip.height() - obj.offsetHeight)/2;
					offset[0] = 10;
				}else if(direction == this.DOWN){
					tipPos[1] = tipPos[1] + obj.offsetHeight + 2;
					offset[1] = -10;
				}else{
					tipPos[1] = tipPos[1] - tooltip.height - 2;
					offset[1] = 10;
				}
				tipPos[0] = tipPos[0] < 0 ? 0 : tipPos[0];
				tipPos[1] = tipPos[1] < 0 ? 0 : tipPos[1];
				tooltip.css({
						'position': 'absolute',
						'left': (tipPos[0] + offset[0]) + 'px',
						'top': (tipPos[1] + offset[1]) + 'px',
						'opacity': 0
					});
				tooltip.show();
				tooltip.animate({opacity: 1, left: tipPos[0], top: tipPos[1]},"fast");
				
			},
			hideTooltip: function(obj){
				$(".loxiatip[show='true']").attr("show","").hide();
			}
		},
		lidget : {
			clearState : function(obj){
				if(!$(obj).hasClass("loxia")) return;
				$(obj).removeClass($(obj).attr("baseClass") + "Error");
				$(obj).removeData("state");
				$(obj).removeData("errorMsg");
			},
			
			setState : function(obj, state, msg){
				if(!$(obj).hasClass("loxia")) return;
				state = state == true ? true : false;
				$(obj).removeClass($(obj).attr("baseClass") + "Error");
				$(obj).data("state",state);
				if(! state){
					$(obj).data("errorMsg",msg);
					$(obj).addClass($(obj).attr("baseClass") + "Error");
				}
			},
			
			val: function(obj){
				if(!$(obj).hasClass("loxia")) return null;
				return $(obj).data("lastRightValue");
			},
			
			check : function(obj){
				if(!$(obj).hasClass("loxia")) return true;
				this.clearState(obj);
				
				var value = $(obj).val();
				var baseClass = $(obj).attr("baseClass");
				var required = ($(obj).attr("required") == "true");
				if(required && value == ""){
					this.setState(obj, false, "Mandatory Field");
					return false;
				}else if(value == $(obj).data("lastRightValue")){
					this.setState(obj, true);
					return true;
				}else{
					var checkmasters = $(obj).attr("checkmaster");
					if(checkmasters){
						var cms = checkmasters.split(",");
						for(var cm,i=0; cm=cms[i]; i++){
							var result = $.loxia.hitch(cm)(value,obj);
							if(result.indexOf($.loxia.SUCCESS) != 0){
								this.setState(obj, false, result);
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
				this.setState(obj, true);
				$(obj).data("lastRightValue", value);
				$(obj).trigger("valueChangedEvent",[value]);
				return true;
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

	$.fn.lidget = function(){
		this.each(function(){			
			$(this).removeClass("lidget").addClass("loxia");
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
				$(this).data("lastRightValue", $(this).val());
			}
			
			if($(this).hasClass("datepicker")){
				$(this).datepick({dateFormat: $.loxia.defaultConfig.dateFormat});
			}
			
			$(this).focus(function(){				
				if(!isButton && !isCheckBox && !isRadio){
					$(this).addClass(baseClass + "Focused");
					var msg = $(this).data("errorMsg");
					if(msg)
						$.loxia.html.showTooltip(this,msg);
				}
			});
			
			if($(this).is("select")){
				$(this).change(function(){
					$(this).removeClass(baseClass + "Focused");
					$.loxia.html.hideTooltip(this);
				
					var value = $(this).val();
					if($(this).attr("trim") == "true"){
						value = $.trim(value);
						$(this).val(value);
					}
					
					//do check
					_l.lidget.check(this);	
				});
			}else{
				$(this).blur(function(){
					if(!isButton && !isCheckBox && !isRadio){
						$(this).removeClass(baseClass + "Focused");
						$.loxia.html.hideTooltip(this);
					
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
			}
		});
				
		return this;
	};
 	
	checkNumber = function(){
		return _l.SUCCESS;
	};
	checkDate = function(){
		return _l.SUCCESS;
	}; 
 })(jQuery);