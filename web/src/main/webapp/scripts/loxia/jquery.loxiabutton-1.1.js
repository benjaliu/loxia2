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
							loxia.openPage(loxia.getTimeUrl(loxia.encodeUrl(href)));
						}else{
							loxia.lockPage();
							window.location = loxia.getTimeUrl(loxia.encodeUrl(href));
						}
						break;
					case "pop" :
						var href = $(this).attr("href");
						var param = "";
						for(k in this.options.data){
							param += '&' + k + '=' + this.options.data[k];							
						}
						href += (/\?/.test(href) ? '&':'?') + param.substring(1);
						
						var oTarget = null;
						if($(this).attr("popfor"))
							oTarget = $(this).prev($(this).attr("popfor"));
						else
							oTarget = $(this).prev();
						
						var oWin = loxia.openPage(href);
						if(!oWin.opener) oWin.opener = self;
						oWin.dialogTarget = oTarget;
						break;
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
		type : "button",
		data : {}
	};	
})(jQuery);