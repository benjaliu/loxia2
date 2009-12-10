(function($) {
	this.LoxiaChoice = function(settings){
		this.data = settings.data;
		this.findMode = settings.findMode || "like";
		this.cachedKeys = [];
		this.cachedValues = [];
		this.currentList = [];
		this._isShown = false;
		
		var div = '<div class="loxia ui-loxiachoice" style="position: absolute; left: 0; top: 0; z-index: 10000;"></div>';
		$("body").append(div);
		this.choiceContainer = $(".ui-loxiachoice:last");
		this.choiceContainer.hide();
		
		if(settings.parentNode) this.parentNode = settings.parentNode;
		this._initData();
		
		this._init();
	};
	
	this.LoxiaChoice.prototype.isShown = function(){return this._isShown};

	this.LoxiaChoice.prototype._initData = function(){
		if(this.data){
			if($.isArray(this.data)){
				this.cachedValues = this.data;
				this.cachedKeys = null;
			}else{
				for(k in this.data){
					this.cachedKeys.push(k);
					this.cachedValues.push(this.data[k]);
				}
			}
		}
	};
	
	this.LoxiaChoice.prototype._init = function(){
		var _this = this;
		$("li", this.choiceContainer).livequery(function(){
			$(this).hover(function(){
				$("li.ui-state-active", $(this).parent()).removeClass("ui-state-active");
				$(this).addClass("ui-state-active");
			},function(){
				//do nothing
			});
			
			$(this).click(function(){
				_this.hide();
				if(_this.parentNode)
					_this.parentNode.trigger("itemselected",[[$(this).attr("key"),$(this).text()]]);
				else
					_this.choiceContainer.trigger("itemselected",[[$(this).attr("key"),$(this).text()]]);
			});
		});		
	};
	
	this.LoxiaChoice.prototype._match = function(str, searchStr){		
		var delim = str.indexOf(searchStr);
		if(delim <0) return false;
		if(this.findMode == "like") return true;
		if(this.findMode == "leftlike" && delim == 0) return true;
		if(this.findMode == "rightlike" && delim == str.length - searchStr.length) return true;
		return false;
	};
	
	this.LoxiaChoice.prototype._filter = function(searchStr){
		this.currentList = [];
		for(var i=0; i< this.cachedValues.length; i++){
			if(searchStr == undefined || searchStr == "" ||
					(searchStr && this._match(this.cachedValues[i], searchStr)))
				this.currentList.push(i);
		}
	};
	
	this.LoxiaChoice.prototype.moveTo = function(index){
		if(!this._isShown) return;
		$("li.ui-state-active", this.choiceContainer).removeClass("ui-state-active");
		if(index <0 || index >= this.currentList.length) return;
		var key = this.cachedKeys == null ?
				this.cachedValues[this.currentList[index]]:this.cachedKeys[this.currentList[index]];
		$("li[key='" + key +"']").addClass("ui-state-active");
	};
	
	this.LoxiaChoice.prototype.show = function(searchStr){
		if(this.cachedValues.length == 0) return;
		this._filter(searchStr);
		if(this.currentList.length == 0){
			if(this._isShown)
				this.hide();
			return;
		}
		var listNode = '<ul class="ui-widget-content ui-corner-all" style="list-style:none; margin: 0; padding-left: 0;">';
		for(var i=0; i< this.currentList.length; i++){
			var v = this.cachedValues[this.currentList[i]];
			var k = this.cachedKeys == null ? v : this.cachedKeys[this.currentList[i]];
			listNode += '<li style="cursor: default; margin: 0; height: 20px; border 1px solid transparent; padding-left: 0;" key="' + k + '">'+ v +'</li>';
		}
		listNode +='</ul>';
		this.choiceContainer.html(listNode);
		if(!this._isShown){
			this._isShown = true;
			var left = 0,top = 0;
			var width = this.choiceContainer.width();
			if(this.parentNode){
				var offset = this.parentNode.offset();
				left = offset.left;
				top = offset.top + this.parentNode.height() + 2;
				width = width < this.parentNode.width() ? this.parentNode.width() : width;
			}			
			var height = this.choiceContainer.height();
						
			this.choiceContainer.css({left : left + 'px', top : top + 'px', height : 0, width : width});
			this.choiceContainer.show();
			this.choiceContainer.animate({height : height},"fast");
		}
		if(searchStr)
			this.moveTo(0);
	};
	
	this.LoxiaChoice.prototype.hide = function(){
		this._isShown = false;
		this.choiceContainer.hide();
	};	
	
	var loxiaDropdown = $.extend({}, loxia.loxiaWidget, {
		container : undefined,
		arrowNode : undefined,
		valueNode : undefined,
		loxiaChoice : undefined,
		_initDomNode : function(){
			var name = this.element.attr("name");
			var value = this.element.val();
			var style = this.element.attr("style");
			var containerDiv = '<div style="overflow: hidden; -moz-box-sizing: content-box; display: inline-block; vertical-align: bottom; #display: inline; position: relative;';
			if(!$.browser.msie){
				containerDiv += style;
				this.element.attr("style","width: 99%;");
			}			
			containerDiv += '"/>';
			this.element.before(containerDiv);
			this.container = $(this.element.prev());
			this.container.css(style);
			this.container.prepend(this.element.get(0));			
			var hiddenText = '<input type="hidden" name="' + name + '"/>';
			this.element.attr("name", name + "_mapped");
			this.element.after(hiddenText);
			this.valueNode = $(this.element.next());
			this.options.lastRightValue = value;
			this._setValue(this.element.val());
			var width = this.element.width();
			var arrowDiv = '<div class="ui-state-default ui-corner-all" style="border: 0 solid; position: absolute; top: 2px; left:'+ (width - 16) +'px"><span class="ui-icon ui-icon-triangle-1-s"/></div>';
			this.container.append(arrowDiv);
			this.arrowNode = this.container.find("div");			
			
			var _this = this;
			this.arrowNode.hover(function(){
					if(_this.element.is(".ui-state-disabled") || _this.element.attr("readonly")) return;
					$(this).addClass("ui-state-active");
				},
				function(){
					if(_this.element.is(".ui-state-disabled") || _this.element.attr("readonly")) return;
					$(this).removeClass("ui-state-active");
				});
		},	
		_initEvent : function(){
			var _this = this;
			this.arrowNode.click(function(){
				if(_this.element.is(".ui-state-disabled") || _this.element.attr("readonly")) return;
				var choice = _this.loxiaChoice;
				choice.parentNode = _this.element;	
				if(choice.isShown())
					choice.hide();
				else{
					choice.show();
					var value = _this.val();
					$("li.ui-state-active",choice.choiceContainer).removeClass("ui-state-active");
					$("li[key='"+ value +"']", choice.choiceContainer).addClass("ui-state-active");
				}
			});
			
			this.element.bind("itemselected", function(event, data){
				_this.val(data[0]);
			});
			this.element.focus(function(){	
				if($(this).is(".ui-state-disabled") || _this.element.attr("readonly")) return;
				var tooltip = $(this).data("loxiatooltip");
				$(this).addClass("ui-state-active");
				
				if(_this._getData("errorMessage")){
					tooltip.show(_this._getData("errorMessage"));
				}
				if(_this._getData("select"))					
					$(this).select();
			});
			
			this.element.blur(function(){
				if($(this).is(".ui-state-disabled") || _this.element.attr("readonly")) return;
				_this.loxiaChoice.hide();
				
				var tooltip = $(this).data("loxiatooltip");
				$(this).removeClass("ui-state-active");
				tooltip.hide();
			
				var value = $(this).val();
				var index = 0;
				for(;index < _this.loxiaChoice.cachedValues.length; index++){
					if(_this.loxiaChoice.cachedValues[index] == value) break;
				}
				index = index == _this.loxiaChoice.cachedValues.length? -1 : index;
				if(!_this.editable && index <0){
					_this.setState(false, loxia.getLocaleMsg("INVALID_DATA"));
				}else if(index >=0){
					value = _this.loxiaChoice.cachedKeys == null ?
							_this.loxiaChoice.cachedValues[index]:_this.loxiaChoice.cachedKeys[index];
					_this.val(value);
				}else{
					_this.val(value);
				}				
			});
			this.element.keyup(function(event){
				if($(this).is(".ui-state-disabled") || _this.element.attr("readonly")) return;
				var item = $("li.ui-state-active",_this.loxiaChoice.choiceContainer);				
				if(event.keyCode == 40){ //down					
					if(item.get(0)){ 
						var nextItem = item.next();
						if(nextItem.get(0)){
							item.removeClass("ui-state-active");
							nextItem.addClass("ui-state-active");
						}						
					}else
						_this.loxiaChoice.moveTo(0);
				}else if(event.keyCode == 38){ //up
					if(item.get(0)){ 
						var preItem = item.prev();
						if(preItem.get(0)){
							item.removeClass("ui-state-active");
							preItem.addClass("ui-state-active");
						}
					}else
						_this.loxiaChoice.moveTo(_this.loxiaChoice.currentList.length -1);
				}else if(event.keyCode == 13){ //enter
					_this.element.trigger("itemselected",
							[[item.attr("key"),item.text()]]);
					_this.loxiaChoice.hide();					
				}else{
					_this.loxiaChoice.parentNode = _this.element;
					_this.loxiaChoice.show($(this).val());
				}
			});
			
			$(document).mousedown(function(event){
				if(_this.loxiaChoice.isShown()){
					var target = event.srcElement? event.srcElement : event.target;
					var inputs = $(target).is(".ui-loxiadropdown") ? $(target) : $(target).parents(".ui-loxiadropdown");
					var choices = $(target).is(".ui-loxiachoice") ? $(target) : $(target).parents(".ui-loxiachoice");
					
					if(inputs.length ==0 && choices.length ==0)
						_this.loxiaChoice.hide();
				}
			});
		},
		_setValue : function(value){
			this.valueNode.val(value);
			var displayValue = this._findValue(value);				
			displayValue = displayValue || value;
			return this.element.val(displayValue);
		},
		_findValue : function(value){
			if($.isArray(this.options.data))
				return value;
			return this.options.data[value];
		},
		_getValue : function(){return this.valueNode.val();},
		_init : function(){
			if(!this.element.is("input")){
				throw new Error("Wrong Dom Type for Dropdown");
			}
			
			if(this.options.editable == true && !$.isArray(this.options.data)){
				throw new Error("Wrong data for editable dropdown");
			}
			
			if(this.options.choice){
				if(loxia.isString(this.options.choice)){				
					this.loxiaChoice = document[this.options.choice];
				}else
					this.loxiaChoice = this.options.choice;
				this.options.data = this.loxiaChoice.data;
			}else{
				this.loxiaChoice = new LoxiaChoice({
					findMode: this.options.findMode,
					data : this.options.data
				});
			}
			this._initDomNode();
			
			var baseClass = "loxiadropdown";
			this._setData("baseClass", baseClass);
			this.element.data("baseClass", baseClass);
			this.element.addClass("loxia " + "ui-" + baseClass + " ui-state-default ui-corner-all");
			
			if(this.element.attr("required") == "true"){
				this._setData("required", true);
				this.element.addClass("ui-state-mandatory");
			}
			
			if(this.element.attr("disabled"))
				this.setEnable(false);
			
			if(this.element.attr("readonly"))
				this.setReadonly(true);
			
			if(this.element.attr("selectonfocus") == "true"){
				this._setData("select", true);
			}								
			
			this.element.loxiatooltip();
									
			this._initEvent();
		},
		_setValue : function(value){
			this.valueNode.val(value);			
			var displayValue = this._findValue(value);
			if(!displayValue && !this.options.editable)
				this.valueNode.val("");
			displayValue = displayValue || value;
			return this.element.val(displayValue);
		},
		setEnable : function(state){
			if(state){
				this.element.removeClass("ui-state-disabled");
				this.element.removeAttr("disabled");
				this.arrowNode.removeClass("ui-state-disabled");
			}else{
				this.element.addClass("ui-state-disabled");
				this.element.attr("disabled","disabled");
				this.arrowNode.addClass("ui-state-disabled");
			}
		}
	});
	
	$.widget("ui.loxiadropdown", loxiaDropdown); 
	$.ui.loxiadropdown.getter = ""; 
	$.ui.loxiadropdown.defaults = $.extend({}, loxia.defaults, {
		editable : false,
		findMode : "like" //"like","leftlike","rightlike"
	});	
	
})(jQuery);