(function($){	
	openFrame = function(menuItem, container) {
		if(container == undefined)
			container = $("#frame-container");
		else if(loxia.isString(container)){
			container = $("#" + container);
		}else
			container = $(container);
		
		var frameId="frm-" + (new Date()).getTime().toString();
		
		$(menuItem).attr("frameId", frameId);
		container.tabs("add","#" + frameId, $(menuItem).text());		
		$("#" + frameId + " iframe").height(container.height() - $("> ul.ui-tabs-nav", container).height() - 30);
		var idx = container.find("li").length - 1;
		container.tabs("select", idx);
		container.find("li:eq(" + idx + ")").attr("frameId", frameId);
		return frameId;
	};
	
	$(document).ready(function (){
		//init default frame container
		var $tabs = $("#frame-container");		
		$tabs.tabs({
			tabTemplate: "<li><a href='#{href}'>#{label}</a> <span class='ui-icon ui-icon-close'>关闭</span></li>",
			add: function( event, ui ) {
				var frameId = $(ui.panel).attr("id");		
				var url = $("a[frameId='" + frameId + "']").attr("href");
				$(ui.panel).append("<div class='frame-control'><span class='refresh'><a href='#'>刷新</a></span>"
						+ "<span class='print'><a href='#'>打印</a></span><span class='showmsg'><a href='#'>显示信息</a></span></div>" + 
						"<iframe src='" + url + "' scrolling=auto frameborder='0'></iframe>");
				
				$("span.showmsg a", ui.panel).powerFloat({
					eventType: "click",
					target: $j("#msg")});
				/*$.ajax({
					url: url,
					success: function(data, textStatus, jqXHR){
						$(ui.panel).append(data);
						loxia.initContext(ui.panel);
					},
					error: function(jqXHR, textStatus, errorThrown){
						$(ui.panel).append("<p>系统载入失败，请联系系统管理员</p>");
					}
				});*/
			}
		});
		
		$( "#frame-container span.ui-icon-close" ).live( "click", function() {
			var $titleContainer = $tabs.find("> ul.ui-tabs-nav");
			var index = $( "li", $titleContainer ).index( $( this ).parent() );
			$tabs.tabs( "remove", index );
		});
	});
})(jQuery);

