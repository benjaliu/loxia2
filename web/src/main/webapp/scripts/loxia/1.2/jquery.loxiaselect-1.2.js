(function($) {
	var loxiaSelect = $.extend({}, loxia.loxiaWidget, {
		_setOption: function( key, value ) {
			$.Widget.prototype._setOption.apply( this, arguments );				
			
			var overlay = this.element.next();
			overlay = overlay.is("div.readonly-overlay") ? overlay : null;
			if(key === "readonly"){
				if(value){
					this.element.attr("readonly","readonly");
										
					if(overlay === null){
						this.element.bind('focus', this.unfocus);
						overlay = $('<div class="readonly-overlay"></div>');
						this.element.after(overlay);
						overlay.css($.extend({'position':'absolute',
							'z-index':'999'}, this._getDimensions(this.element)));				    
						if($.browser.version == '6.0' && $.browser.msie) this.element.css('visibility', 'hidden');
					}					
				}else{
					this.element.removeAttr("readonly");
					if(overlay != null){
						this.element.unbind('focus', this.unfocus);
						overlay.remove();
						if($.browser.version == '6.0' && $.browser.msie) this.element.css('visibility', 'visible');
					}
				}
			}else if(key === "disabled"){
				if(value){
					this.element.attr("disabled","disabled");
				}else{
					this.element.removeAttr("disabled");
				}
			}
		},
		
		_initSelect : function(){
			var _this = this;
			this.element.focus(function(){	
				if(_this.option("disabled") || _this.option("readonly")) return;
				$(this).addClass("ui-loxia-active");
				if(_this.option("errorMessage")){
					loxia.tip(this,_this.option("errorMessage"));
				}
			});
			
			this.element.blur(function(){
				$(this).removeClass("ui-loxia-active");
				loxia.tip();
			});
		
			this.element.change(function(){			
				_this.val($(this).val());
				if(_this.state()){
					loxia.tip();
				}else if($(this).is("ui-loxia-active"))
					loxia.tip(this,_this.option("errorMessage"));
			});
		},
		_create : function(){
			$.Widget.prototype._create.apply( this, arguments );
						
			if(this.element.is("select")){
				this._initWidget();				
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
		
		unfocus : function(e){$(e.currentTarget).blur();}
	});
	
	$.widget("ui.loxiaselect", loxiaSelect); 
	$.ui.loxiaselect.prototype.options = loxia.defaults;
})(jQuery);