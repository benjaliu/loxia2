(function($) {
	var loxiaDropdown = $.extend({}, loxia.loxiaWidget, {
		container : undefined,
		arrowNode : undefined,
		valueNode : undefined,
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
			
			this.arrowNode.hover(function(){$(this).addClass("ui-state-active");},
					function(){$(this).removeClass("ui-state-active");});
		},	
		_setValue : function(value){
			this.valueNode.val(value);
			var displayValue = this._findValue(value);				
			displayValue = displayValue || value;
			return this.element.val(displayValue);
		},
		_findValue : function(value){
			return "";
		},
		_getValue : function(){return this.valueNode.val();},
		_init : function(){
			if(!this.element.is("input")){
				throw new Error("Wrong Dom Type for Dropdown");
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
			
			if(this.element.attr("selectonfocus") == "true"){
				this._setData("select", true);
			}								
			
			this.element.loxiatooltip();
		},
		_setValue : function(value){
			this.valueNode.val(value);			
			var displayValue = this._findValue(value);
			if(!displayValue && !this.options.editable)
				this.valueNode.val("");
			displayValue = displayValue || value;
			return this.element.val(displayValue);
		}
	});
	
	$.widget("ui.loxiadropdown", loxiaDropdown); 
	$.ui.loxiadropdown.getter = ""; 
	$.ui.loxiadropdown.defaults = $.extend({}, loxia.defaults, {
		editable : false,
		data : {},
		findMode : "like" //"like","leftlike","rightlike","exact"
	});	
})(jQuery);