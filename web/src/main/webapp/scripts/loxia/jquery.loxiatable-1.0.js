(function($){
	$.fn.loxiatable = function(settings){
		settings = $.extend({},{
			sort: false,
			page: false,
			pageSize: 20,
			pageSizeOptions: ["10", "15", "20", "25", "30", "40", "50", "100"],
			currentPage : 1,
			pageCount: 0,
			data: [],
			url: ""
		},settings);
		this.each(function(){
			
		});
		return this;
	};
	$.fn.ltInit = function(){
		var $t = $(this);
		$t.find("thead tr:last th").each(function(i){
			$(this).addClass("col-" + i);
			$(this).html("<div class='th-col-'" + i + ">" + $(this).html() + "</div>");
			++i;
		});
	};
})(jQuery);