(function($) {
	
	var loxiaSelect = $.extend({}, loxia.loxiaWidget, {
		getBaseClass : function(){
			return "loxiaselect";
		},
		_initSelect : function(){
			this._setData("checkmaster",this.element.attr("checkmaster") || "");
			
			this.element.focus(function(){	
				var _t = $(this).data("loxiaselect");
				var tooltip = $(this).data("loxiatooltip");
				$(this).addClass("ui-state-active");
				
				if(_t._getData("errorMessage")){
					tooltip.show(_t._getData("errorMessage"));
				}
			});
			
			this.element.blur(function(){
				var tooltip = $(this).data("loxiatooltip");
				$(this).removeClass("ui-state-active");
				tooltip.hide();
			});
			
			this.element.change(function(){
				var _t = $(this).data("loxiaselect");
				var tooltip = $(this).data("loxiatooltip");
				$(this).removeClass("ui-state-active");
				tooltip.hide();
			
				var value = $(this).val();
				_t.val(value);
			});
		},
		_init : function(){
			if(this.element.is("select")){
				this.element.removeAttr("loxiaType");
				var baseClass = this.getBaseClass();
				this.element.data("baseClass", baseClass);
				this._setData("baseClass", baseClass);
				this.element.addClass("loxia " + "ui-" + baseClass + " ui-state-default ui-corner-all");
				
				if(this.element.attr("required") == "true"){
					this._setData("required", true);
					this.element.addClass("ui-state-mandatory");
				}
								
				if(this.element.val() != null)
					this._setData("lastRightValue", this.element.val());
				
				this.element.loxiatooltip();
				
				this._initSelect();
			}else
				throw new exception("Wrong Dom Type for Input");
		}
	});
	
	$.widget("ui.loxiaselect", loxiaSelect); 
	$.ui.loxiaselect.getter = loxia.loxiaGetter; 
	$.ui.loxiaselect.defaults = loxia.defaults;
})(jQuery);