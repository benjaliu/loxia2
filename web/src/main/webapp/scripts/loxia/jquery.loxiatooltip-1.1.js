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
			var viewport = loxia.getViewport();
			var offset = this.element.offset();
			var left = offset.left + this.element.width();
			var top = offset.top;
			var width = $(".loxia-tooltip").width();
			var height = $(".loxia-tooltip").height();
			if(this.options.onShow){
				loxia.hitch(this,this.options.onShow)({target:this.element[0]});
			}
			if(viewport.width - left <= width + 8){
				this._tipSide = "left";
				left = offset.left - width;
			}
			if(this._tipSide == "left" && offset.left < width + 8){
				left = offset.left;
				if(this.element.data("dropdown")){
					this._tipSide = "up";
					top -= height;
				}else{
					this._tipSide = "down";
					top += height;
				}
			}
			$(".loxia-tooltip").css({position : "absolute", top : top + 'px',
					left : left + 'px', opacity : 0});
			var $tpArrow = $(".loxia-tooltip .ui-tooltip-arrow");
			$tpArrow.removeClass("ui-tooltip-arrow-left ui-tooltip-arrow-right ui-tooltip-arrow-up ui-tooltip-arrow-down");
			if(this._tipSide == "right"){
				left += 8;
				$tpArrow.addClass("ui-tooltip-arrow-left");
			}else if(this._tipSide == "left"){
				left -= 8;
				$tpArrow.addClass("ui-tooltip-arrow-right");
			}else if(this._tipSide == "down"){
				top += 8;
				$tpArrow.addClass("ui-tooltip-arrow-up");
			}else{
				top -= 8;
				$tpArrow.addClass("ui-tooltip-arrow-down");
			}
		},
		show : function(message){
			this._prepare(message);
			$(".loxia-tooltip").show();
			var offset = $(".loxia-tooltip").offset();
			var left = offset.left;
			var top = offset.top;
			if(this._tipSide == "right"){
				left += 8;
			}else if(this._tipSide == "left"){
				left -= 8;
			}else if(this._tipSide == "down"){
				top += 8;
			}else
				top -= 8;
			$(".loxia-tooltip").animate({opacity : 1, left : left, top : top}, "fast");
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