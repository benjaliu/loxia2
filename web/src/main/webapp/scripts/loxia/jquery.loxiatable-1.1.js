(function($) {
	var loxiaBaseTable = {
			
	};
	var loxiaBaseTableDefaults = {
		_formatName : function(str){
			return str.replace(/\./ig,"_");
		},
	};
	
	var loxiaTable = $.extend({}, loxiaBaseTable, {
		_initSelector: function(){
			var $t = this.element;
			var selectors = [];
			var selected = this._getData("selected");
			
			var sels = 0;
			$t.find("thead tr:last th").each(function(){
				var s = "";
				var chkbox = $(this).find("input[type='checkbox']");
				var radbox = $(this).find("input[type='radio']");
				var name = $(this).attr("name") || $(this).attr("property");
				if(chkbox.get(0)) s = "+" + name;
				else if(radbox.get(0)) {
					radbox.replaceWith("&nbsp;");
					s = "-" + name;
				}

				selectors.push(s);
				if(s){
					sels ++;
					selected["col_"+ (selectors.length-1)] = $.extend({},selected["col_"+ (selectors.length-1)]);
					$(this).addClass("selector");
				}
			});
			
			this._setData("selectCols", sels);
			this._setData("selectors", selectors);
		},
		
		_loadData: function(reloadAll){
			var $t = this.element;
			var $tbody = $t.find("tbody:first");
			var propList = [];

			$t.find("thead tr:last th").each(function(){
				propList.push($(this).attr("property"));
			});

			var selectors = this._getData("selectors");
			var selectCache = this._getData("selected");
			var data = this._getData("data");
			if(data && data.length && data.length >0){
				var rowlist = "";
				for(var dataItem,i=0; (dataItem = data[i]); i++){
					var row = "<tr>";
					for(var p,j=0;p=propList[j];j++){
						var value = loxia.getObject(p,dataItem);
						if(selectors[j]){
							var selected = selectCache["col_"+j];
							value = (value == undefined || value == null ||
									(loxia.isString(value) && !value)) ? "" : "" + value;
							value = this._formatName(value);
							var strSelected = (value in selected)? " checked" : "";
							var name = selectors[j].substring(1);
							if(selectors[j].charAt(0) == '+'){
								row += "<td><input type='checkbox'" + strSelected + " name='" + name + "' value='" + value + "'/></td>";
							}else if(selectors[j].charAt(0) == '-'){
								row += "<td><input type='radio'" + strSelected + " name='" + name + "' value='" + value + "'/></td>";
							}else
								throw new exception("Load Data for Selector Error.");
						}else{
							value = (value == undefined || value == null ||
									(loxia.isString(value) && !value)) ? "&nbsp;" : value;
							row += "<td>" + value + "</td>";
						}
					}
					row += "</tr>";
					rowlist += row;
				}
				if(reloadAll){
					$tbody.html(rowlist);
				}else
					$tbody.append(rowlist);
			}
			$t.trigger("dataloaded",[data]);
		},
		_initStyle : function(){
			var $t = this.element;
			var sortStatus = [];
			var currentSort = this._getData("sort");
			if(currentSort){
				var sortlist = currentSort.split(",");
				sortStatus.push(sortlist[0]);
				sortStatus.push(sortlist[1]||"asc");
			}
			$t.find("thead tr").each(function(i){					
				$(this).addClass("ui-widget-header");
				$(this).find("th").addClass("ui-state-default");
				if(i==0){
					$(this).find("th:first").addClass("ui-corner-tl");
					$(this).find("th:last").addClass("ui-corner-tr");
				}
			});
			$t.find("tbody").addClass("ui-widget-content");
			var cols = $t.find("thead tr:last th").each(function(i){
				var sortClass = "";
				if(currentSort){
					var sort = $(this).attr("sort");					
					if(!sort) sortClass = "sort-nosort";
					else if(sort == sortStatus[0]){
						if(sortStatus[1].toLowerCase() == "asc") sortClass = "sort-asc";
						else if(sortStatus[1].toLowerCase() == "desc") sortClass = "sort-desc";
					}
				}
				var className = $(this).hasClass("selector") ? "selector ui-loxia-table-cell" : "ui-loxia-table-cell";
				$(this).addClass(sortClass).addClass("col-" + i);
				$(this).html("<div class='th-col-" + i + "'>" + $(this).html() + "</div>");
				$t.find("tbody:first tr").find("td:eq(" + i + ")").addClass(className + " col-" + i);
			}).length;
			this._setData("cols",cols);
			$t.find('tbody:first tr:odd').addClass("ui-loxia-table-row odd");
			$t.find('tbody:first tr:even').addClass("ui-loxia-table-row even");

			if(this._getData("selectCols") == 1)
				$t.find("tbody:first input:checked").parents("tr").addClass("ui-loxia-table-row-select");
			return this;
		},
		_init: function(){
			this.element.removeAttr("loxiaType");
			this.element.addClass("loxia ui-loxia-table");
			this._initSelector();
			this._loadData(false);
			this._initStyle();
		}
	});
	$.widget("ui.loxiatable", loxiaTable); 
	$.ui.loxiaselect.getter = ""; 
	$.ui.loxiaselect.defaults = $.extend({},loxiaBaseTableDefaults,{
		sort: "",
		page: false,
		pageSize: 20,
		pageSizeOptions: ["10", "15", "20", "25", "30", "40", "50", "100"],
		currentPage: 1,
		pageCount: 1,
		itemCount: 0,
		pageItemCount : 0,
		data: [],
		form: "",
		url: "",
		cacheSelect: false,
		selected: {}
	});
})(jQuery);