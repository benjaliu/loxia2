(function($) {
	var loxiaTooltip = {
		_init : function(){
			if(!$.loxiatooltip.initialized){
				$(document).find("body").append($.loxiatooltip.tpDiv);
				$.loxiatooltip.initialized = true;
				$(".loxia-tooltip").hide();
			}
		},
		_tipSide : "",
		_prepare : function(message){
			this._tipSide = "right";			
			$.loxiatooltip.setMessage(message, $(".loxia-tooltip").get(0));
			var offset = this.element.offset();
			var left = offset.left + this.element.width();
			var top = offset.top;
			var width = $(".loxia-tooltip").width();
			if(this.options.onShow){
				loxia.hitch(this,this.options.onShow)({target:this.element[0]});
			}
			if($(window).width() - offset.left <= width + 8){
				this._tipSide = "left";
				left = offset.left - width;
			}
			$(".loxia-tooltip").css({position : "absolute", top : top + 'px',
					left : left + 'px', opacity : 0});
			if(this._tipSide == "right"){
				
			}else{
				
			}
		},
		show : function(message){
			this._prepare(message);
			$(".loxia-tooltip").show();
			var left = $(".loxia-tooltip").offset().left;
			if(this._tipSide == "right"){
				left += 8;
			}else{
				left -= 8;
			}
			$(".loxia-tooltip").animate({opacity : 1, left : left}, "fast");
		},
		hide : function(){
			$(".loxia-tooltip").hide();
			if(this.options.onHide){
				loxia.hitch(this,this.options.onHide)({target:this.element[0]});
			}
		}
	}
	
	$.widget("ui.loxiatooltip", loxiaTooltip); 
	$.ui.loxiatooltip.getter = ""; 
	$.ui.loxiatooltip.defaults = {
		onShow : undefined,
		onHide : undefined
	};
	$.loxiatooltip = {
		tpDiv : '<div class="loxia loxia-tooltip ui-widget"><div class="ui-tooltip-arrow"></div>' +
			'<div class="ui-state-highlight ui-corner-all"  style="padding: .3em .7em; width: auto;"></div></div>',
		initialized : false,
		setMessage : function(message, tooltip){$(tooltip).find(".ui-state-highlight").text(message)}
	};
})(jQuery);