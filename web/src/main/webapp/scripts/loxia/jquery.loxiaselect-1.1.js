(function($) {
	
	var loxiaSelect = $.extend({}, loxia.loxiaWidget, {
		readOnly: false,
		getBaseClass : function(){
			return "loxiaselect";
		},
		_initSelect : function(){
			this._setData("checkmaster",this.element.attr("checkmaster") || "");
			
			this.element.focus(function(){	
				if($(this).is(".ui-state-disabled")) return;
				var _t = $(this).data("loxiaselect"),
					tooltip = $(this).data("loxiatooltip");
				$(this).addClass("ui-state-active");
				
				if(_t._getData("errorMessage")){
					tooltip.show(_t._getData("errorMessage"));
				}
			});
			
			this.element.blur(function(){
				if($(this).is(".ui-state-disabled")) return;
				var tooltip = $(this).data("loxiatooltip");
				$(this).removeClass("ui-state-active");
				tooltip.hide();
			});
			
			this.element.change(function(){				
				var _t = $(this).data("loxiaselect"),
					tooltip = $(this).data("loxiatooltip");
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
				
				if(this.element.attr("required") == "true" || this.element.attr("required") == true){
					this._setData("required", true);
					this.element.addClass("ui-state-mandatory");
				}
				
				if(this.element.attr("disabled"))
					this.setEnable(false);
				
				if(this.element.attr("readonly")){
					this.setReadonly(true);
				}
								
				if(this.element.val() != null)
					this._setData("lastRightValue", this.element.val());
				
				this.element.loxiatooltip();
				
				this._initSelect();
			}else
				throw new Error("Wrong Dom Type for Input");
		},		
		_getDimensions: function(el){
		    var ret = {};

		    // The multiple acquisitions of the CSS styles are required to cover any border and padding the elements may have.
		    // The Ternary (parseInt(...) || 0) statements fix a bug in IE6 where it returns NaN,
		    //  which doesn't play nicely when adding to numbers...
		    ret.width = el.width()
		      + (parseInt(el.css('borderLeftWidth')) || 0)
		      + (parseInt(el.css('borderRightWidth')) || 0)
		      + (parseInt(el.css('padding-left')) || 0)
		      + (parseInt(el.css('padding-right')) || 0);
		    ret.height = el.height()
		      + (parseInt(el.css('borderTopWidth')) || 0)
		      + (parseInt(el.css('borderBottomWidth')) || 0)
		      + (parseInt(el.css('padding-bottom')) || 0)
		      + (parseInt(el.css('padding-bottom')) || 0);
		    var offsets = el.offset();
		    ret.left = offsets.left;
		    ret.top = offsets.top;

		    return ret;
		},
		setReadonly : function(state){
			var overlay = $('<div class="readonly-overlay"></div>');
			if(this.readOnly == state) return;
			this.readOnly = state;
			if(state){				
				this.element.bind('focus', this.unfocus).after(overlay);
				overlay.css($.extend({'position':'absolute',
					'z-index':'999'}, this._getDimensions(this.element)));				    
				if($.browser.version == '6.0' && $.browser.msie) this.element.css('visibility', 'hidden');
			}else{
				overlay = this.element.unbind('focus', this.unfocus).next();
				overlay.remove();
				if($.browser.version == '6.0' && $.browser.msie) this.element.css('visibility', 'visible');
			}
		},
		unfocus : function(e){$(e.currentTarget).blur();}
	});
	
	$.widget("ui.loxiaselect", loxiaSelect); 
	$.ui.loxiaselect.getter = loxia.loxiaGetter; 
	$.ui.loxiaselect.defaults = loxia.defaults;
})(jQuery);