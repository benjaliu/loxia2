(function($) {
	var loxiaButton = {
		_init : function(){
			if(this.element.is("input[type='button']") || this.element.is("button")){
				this.element.removeAttr("loxiaType");
				this.options.type = this.element.attr("buttonType") || this.options.type;
				this.element.addClass("loxia loxia-button ui-state-default ui-corner-all");
				
				this.element.bind("click", function(e){
					e.stopPropagation();
					var _t = $(this).data("loxiabutton");
					switch(_t.options.type){
					case "submit" :
						loxia.lockPage();
						loxia.submitForm($(this).parents("form").get(0));
						break;
					case "anchor" :
						var href = $(this).attr("href");
						var target = $(this).attr("target");
						if(target == "_blank"){
						}else{
							loxia.lockPage();
						}
					case "pop" :
					case "close" :
						if($.browser.msie){
							window.top.close();
						}else
							window.close();
						break;
					}
					
				});
				
				this.element.hover(function(){
					$(this).toggleClass("ui-state-hover");
				},function(){
					$(this).toggleClass("ui-state-hover");
				});
			}else
				throw new exception("Wrong DOM Type for Button.");
		}
	};	
	$.widget("ui.loxiabutton", loxiaButton); 
	$.ui.loxiabutton.getter = ""; 
	$.ui.loxiabutton.defaults = {
		type : "button"
	};	
})(jQuery);