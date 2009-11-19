(function($){
	$.fn.loxiatable = function(settings){
		settings = $.extend({},{
			sort: "",			
			page: false,
			pageSize: 20,
			pageSizeOptions: ["10", "15", "20", "25", "30", "40", "50", "100"],
			currentPage : 1,
			pageCount: 0,
			data: [],
			form: "",
			url: ""
		},settings);
		this.each(function(){
			$(this).addClass("loxiatable");
			$(this).ltInit(settings);
		});
		return this;
	};
	$.fn.ltLoadData = function(data, reloadAll){
		var $t = $(this);
		var $tbody = $t.find("tbody:first");
		var propList = [];
		$t.find("thead tr:last th").each(function(){
			propList.push($(this).attr("property"));
		});
		if(reloadAll){
			$tbody.html("");
		}
		if(data && data.length && data.length >0){
			var rowlist = "";
			for(var dataItem,i=0; (dataItem = data[i]); i++){
				console.log(dataItem);
				var row = "<tr>";
				for(var p,j=0;p=propList[j];j++){
					var value = $.loxia.getObject(p,dataItem);
					value = (value == undefined || value == null ||
							($.loxia.isString(value) && !value)) ? "&nbsp;" : value;
					row += "<td>" + value + "</td>";					
				}
				row += "</tr>";
				rowlist += row;
			}
			console.log(rowlist);
			$tbody.append(rowlist);
		}
	};
	$.fn.ltInit = function(settings){
		var $t = $(this);		
		$t.ltLoadData(settings.data, false);
		$t.ltInitStyle(settings);
		$t.ltInitSort(settings);
		$t.ltInitPager(settings);
	};
	
	$.fn.ltInitStyle = function(settings){
		var $t = $(this);
		var sortStatus = {};
		if(settings.sort){
			var sortlist = settings.sort.split(",");
			//TODO
		}
		$t.find("thead tr:last th").each(function(i){
			var sort = $(this).attr("sort");
			//TODO
			var sortClass = "";
			if(!sort) sortClass = "nosort";
			else if(sort.toLowerCase().indexOf("|desc") > 0) sortClass = "sort-desc";
			else if(sort.toLowerCase().indexOf("|asc") > 0) sortClass = "sort-asc";
			$(this).addClass(sortClass).addClass("col-" + i);
			$(this).html("<div class='th-col-" + i + "'>" + $(this).html() + "</div>");
			$t.find("tbody:first tr").find("td:eq(" + i + ")").addClass("col-" + i);
			++i;
		});
		
		$t.find('tbody:first tr:odd').addClass("odd");
		$t.find('tbody:first tr:even').addClass("even");
			
		return this;
	}
	
	$.fn.ltInitSort = function(settings){
		if(!settings.sort) return this;
		console.log("initiate sort");
		$("thead tr:last th", $t).livequery(function(){
			$(this).hover(function(){
				$(this).addClass("hover");
			},function(){
				$(this).removeClass("hover");
			});
			
			$(this).click(function(){
				
			});
		});
		
		return this;
	}
	
	$.fn.ltInitPager = function(settings){
	}
})(jQuery);