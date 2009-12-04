(function($) {
	var loxiaButton = {
		_init : function(){
			if(this.element.is("input[type='button']") || this.element.is("button")){
				this.element.removeAttr("loxiaType");
				this.options.type = this.element.attr("buttonType");
				this.element.addClass("loxia loxia-button ui-state-default ui-corner-all");
				
				this.element.bind("click", function(e){
					var _t = $(this).data("loxiabutton");
					switch(_t.options.type){
					case "submit" :
						loxia.submitForm($(this).parents("form").get(0));
						break;
					case "anchor" :
					case "pop" :
					case "close" :
						console.log("inner actions");
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