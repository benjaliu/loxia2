(function($) {
	var loxiaBaseTable = {
			
	};
	var loxiaBaseTableDefaults = {
		_formatName : function(str){
			return str.replace(/\./ig,"_");
		}
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
		_initHeadAction : function(){
			var $t = this.element;
			var _this = this;
			$("thead tr:last th", $t).livequery(function(){
				$(this).hover(function(){
					$(this).toggleClass("ui-state-hover");
				},function(){
					$(this).toggleClass("ui-state-hover");
				});
				
				$(this).click(function(){
					var $th = $(this);
					if($th.hasClass("sort-nosort")) return;
					var sort = $th.attr("sort");

					var sortStr = "" + sort + ",";
					var sortOrder = "";
					if($th.hasClass("sort-asc")){
						sortOrder = "desc";
					}else
						sortOrder = "asc";
					sortStr += sortOrder;
					var args  = {
							data: {
								currentPage: _this._getData("currentPage"),
								pageSize: _this._getData("pageSize"),
								sortString: sortStr
							}
					};
					if(_this._getData("form")) args["form"] = _this._getData("form");
					_this._refresh(args);
				});
			});
		},
		_refresh : function(args){
		},
		_initPager : function(){
			var _this = this;
			var $t = this.element;
			if(!this._getData("page")) return this;

			var pageSizeOptions = this._getData("pageSizeOptions");
			var pager =
				'<div class="ui-pager">' +
				'<div class="ui-pager-block" block="pagesize">' + loxia.getLocaleMsg("TABLE_PER_PAGE") +
				'<select loxiaType="select">';
				for (var i=0; i<pageSizeOptions.length; i++) {				
					pager += '<option value="'+pageSizeOptions[i]+'">'+pageSizeOptions[i]+'</option>';
				}

			var itemStart = this.options.pageSize * (this.options.currentPage - 1) + 1;
			var itemEnd = itemStart + this.options.pageItemCount - 1;
			
			pager += '</select>' + '</div>' +				
				'<div class="ui-pager-block" block="navigator">' +
				'<div class="separator"></div>' +
				'<div class="ui-state-default ui-corner-all"><span action="First" title="' + loxia.getLocaleMsg("TABLE_PAGER_FIRST") + '" class="ui-icon ui-icon-seek-first"></span></div> ' +
				'<div class="ui-state-default ui-corner-all"><span action="Previous" title="' + loxia.getLocaleMsg("TABLE_PAGER_PREVIOUS") + '" class="ui-icon ui-icon-seek-prev"></span></div> ' +
				'<div class="ui-state-default ui-corner-all"><span action="Next" title="' + loxia.getLocaleMsg("TABLE_PAGER_NEXT") + '" class="ui-icon ui-icon-seek-next"></span></div> ' +
				'<div class="ui-state-default ui-corner-all"><span action="Last" title="' + loxia.getLocaleMsg("TABLE_PAGER_LAST") + '" class="ui-icon ui-icon-seek-end"></span></div> ' +
				'<div class="separator"></div></div>';
				
			pager += '<div class="ui-pager-block" block="pagegoto"><div>' + loxia.getLocaleMsg("TABLE_PAGE") + 
				' <input loxiaType="number" min="1" max="' + this.options.pageCount + '" value="" style="width: 3em;"/>' +
				'/<span"></span></div>' + 
				'<div class="ui-state-default ui-corner-all"><span action="Goto" title="' + loxia.getLocaleMsg("TABLE_PAGER_GOTO") + '" class="ui-icon ui-icon-arrowreturnthick-1-w"></span></div></div>';
			
			pager +=			
				'<div class="ui-pager-block" block="refresh">' +
				'<div class="separator"></div>' +
				'<div class="ui-state-default ui-corner-all"><span action="Reload" title="' + loxia.getLocaleMsg("TABLE_PAGER_RELOAD") + '" class="ui-icon ui-icon-transferthick-e-w"></span></div>' +
				'<div class="separator"></div>' +
				'<div class="page-info"><span></span></div>' +
				'</div>';

			pager += '</div>';
			
			var $tbody = $t.find("tbody:last").append('<tr><td colspan="' + this.options.cols + '">'
					+ pager + "</td></tr>");
			
			$('.ui-pager .ui-state-default',$t).hover(
					function() {
						$(this).toggleClass('ui-state-active');
					},
					function() {
						$(this).toggleClass('ui-state-active');
					}
				);
			
			loxia.initContext($t.find("tbody:last tr:last"));			
			
			this._setPager();
			
			$(".ui-pager", $t).livequery(function() {
				$(".ui-pager .ui-icon", $t).unbind("click").bind("click", function() {					
					if ($(this).hasClass("disabled")) {
						return false;
					}
					var action = $(this).attr("action");
					
					var moveto = true;
					var moveToPage = _this.options.currentPage;
					switch (action) {
					case 'Next':
						moveToPage += 1;
						break;

					case 'Previous':
						moveToPage -= 1;
						break;

					case 'First':
						moveToPage = 1;
						break;

					case 'Last':
						moveToPage = $t.data("pageCount");
						break;
						
					case 'Goto':
						var $input = $(".ui-pager-block[block='pagegoto'] input", $t);
						console.log($input.loxianumber("getState"));
						if($input.loxianumber("getState") == null){
							$input.loxianumber("check");								
						}
						
						if($input.loxianumber("getState"))
							moveToPage = parseInt($input.loxianumber("val"));
						else
							moveto = false;
					}
					if(moveto){
						var settings = {
							url: _this.options.url,
							data: {
								pageSize: _this.options.pageSize,
								currentPage: moveToPage
							}
						};
						if(_this.options.sort)
							settings["sort"] = _this.options.sort;
						if(_this.options.form)
							settings["form"] = _this.options.form;		
						_this._refresh(settings);
					}
				});			
				
				$(".ui-pager-block[block='pagesize'] select", $t).unbind("valuechanged").bind("valuechanged", function() {
					var pageSize = parseInt($(this).loxiaselect("val"));
					console.log(pageSize);
					var settings = {
						url: _this.options.url,
						data: {
							pageSize: pageSize,
							currentPage: 1
						}
					};
					if(_this.options.sort)
						settings["sort"] = _this.options.sort;
					if(_this.options.form)
						settings["form"] = _this.options.form;		
					_this._refresh(settings);
				});
			});
		},
		_setPager : function(){
		},
		_init: function(){
			this.element.removeAttr("loxiaType");
			this.element.addClass("loxia ui-loxia-table");
			this._initSelector();
			this._loadData(false);
			this._initStyle();
			this._initHeadAction();
			this._initPager();
		}
	});
	$.widget("ui.loxiatable", loxiaTable); 
	$.ui.loxiatable.getter = ""; 
	$.ui.loxiatable.defaults = $.extend({},loxiaBaseTableDefaults,{
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