(function($) {
	var loxiaRowIndex = 0;
	var loxiaBaseTable = {
		_formatName : function(str){
			return str.replace(/\./ig,"_");
		}
	};
	var loxiaBaseTableDefaults = {
		
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
								throw new Error("Load Data for Selector Error.");
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
			$t.find("tbody:gt(0)").addClass("ui-widget-content");
			var cols = $t.find("thead tr:last th").each(function(i){
				var sortClass = "";

				var sort = $(this).attr("sort");	
				if(sort){
					sortClass = "sort-nosort";
					if(sort == sortStatus[0]){
						if(sortStatus[1].toLowerCase() == "asc") sortClass = "sort-asc";
						else if(sortStatus[1].toLowerCase() == "desc") sortClass = "sort-desc";
					}
				}

				var className = $(this).hasClass("selector") ? "selector" : "";
				$(this).addClass(sortClass).addClass("col-" + i);
				$(this).html("<div class='th-col-" + i + "'>" + $(this).html() + "<div class='ui-sort'></div></div>");
				$t.find("tbody:first tr").find("td:eq(" + i + ")").addClass(className + " col-" + i);
			}).length;
			this._setData("cols",cols);
			$t.find('tbody:first tr:odd').addClass("odd");
			$t.find('tbody:first tr:even').addClass("even");
			
			$t.find("tbody").find("tr:last").addClass("last");

			if(this._getData("selectCols") == 1)
				$t.find("tbody:first input:checked").parents("tr").addClass("selected");
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
					var sort = $th.attr("sort");
					if(!sort) return;
					
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
			var _this = this;
			var $t = this.element;
			var $reload = $t.find('.ui-pager-block[block="refresh"]');
			$reload.find(".ui-state-default").addClass("loading");
			
			if(!this.options.cacheSelect){
				var selected = this.options.selected;
				for(key in selected){
					selected[key] = {};
				}
			}
			args = $.extend({
				error : function(XMLHttpRequest, textStatus, errorThrown){
					var exception = {};
					exception["message"] = "Error occurs when fetching data from url:" + this.url;
					exception["cause"] = textStatus? textStatus : errorThrown;
					$t.find(".ui-pager-status > div").addClass("ui-state-highlight")
						.text(loxia.getLocaleMsg("TABLE_PAGE_ERROR_OCCURS")).show()
						.animate({opacity: 1},"fast");
					
					$reload.find(".loading").removeClass("loading");
					//console.dir(exception);
				}
			},args);
			//console.dir(settings);
			var url = args.url || this.options.url;
			loxia.asyncXhr(url, args, function(data, textStatus){
				//console.dir(data);
				if(data.exception){
					$t.find(".ui-pager-status > div").addClass("ui-state-highlight")
					.text(data.exception.message).show()
					.animate({opacity: 1},"fast");
				}else{
					_this.options.data = data;					
					_this.refresh(true);

					if(data.sort){						
						var sortStatus = [];

						var sortlist = data.sort.split(",");
						sortStatus.push(sortlist[0]);
						sortStatus.push(sortlist[1]||"asc");

						$t.find("thead tr:last th").each(function(i){
							if(data.sort){
								$(this).removeClass("sort-asc sort-desc sort-nosort");
								var sort = $(this).attr("sort");
								if(sort){
									if(sort == sortStatus[0]){
										$(this).addClass("sort-" + sortStatus[1]);
									}else
										$(this).addClass("sort-nosort");
								}
							}

							var className = $(this).hasClass("selector") ? "selector" : "";
							$t.find("tbody:first tr").find("td:eq(" + i + ")").addClass(className + " col-" + i);
						});
					}

					$t.find('tbody:first tr:odd').addClass("odd");
					$t.find('tbody:first tr:even').addClass("even");
					$t.find('tbody:first tr:last').addClass("last");

					if(_this.options.selectCols == 1)
						$t.find("tbody:first input:checked").parents("tr").addClass("selected");

					if(_this.options.page)
						_this._setPager();	
					$t.find(".ui-pager-status > div").removeClass("ui-state-highlight")
					.text(loxia.getLocaleMsg("TABLE_PAGE_RELOAD")).show()
					.animate({opacity: 1},"fast");
				}
				$reload.find(".loading").removeClass("loading");
			});
		},
		_initPager : function(){
			var _this = this;
			var $t = this.element;
			if(!this._getData("page") || 
					((!this.options.alwaysShowPage) && this.options.pageCount <=1)) return;

			var pageSizeOptions = this._getData("pageSizeOptions");
			var pager =
				'<div class="ui-pager">' +
				'<div class="ui-pager-block" block="pagesize">' + loxia.getLocaleMsg("TABLE_PER_PAGE") +
				'<select loxiaType="select" formCheck="false">';
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
				' <input loxiaType="number" formCheck="false" min="1" max="' + this.options.pageCount + '" value="" style="width: 3em;"/>' +
				'/<span"></span></div>' + 
				'<div class="ui-state-default ui-corner-all"><span action="Goto" title="' + loxia.getLocaleMsg("TABLE_PAGER_GOTO") + '" class="ui-icon ui-icon-arrowreturnthick-1-w"></span></div></div>';
			
			pager +=			
				'<div class="ui-pager-block" block="refresh">' +
				'<div class="separator"></div>' +
				'<div class="ui-state-default ui-corner-all"><span action="Reload" title="' + loxia.getLocaleMsg("TABLE_PAGER_RELOAD") + '" class="ui-icon ui-icon-transferthick-e-w"></span></div>' +
				'<div class="separator"></div>' +
				'<div class="page-info"><span></span></div>' +
				'</div>';
			pager += '<div class="ui-pager-status"><div class="ui-state-default ui-corner-all"></div></div>';
			pager += '</div>';
			
			var $tbody = $t.find("tbody:last").append('<tr class="last"><td colspan="' + this.options.cols + '">'
					+ pager + "</td></tr>");
			
			$('.ui-pager .ui-state-default',$t).hover(
					function() {
						if(!$(this).hasClass("ui-state-disabled") &&
								!$(this).hasClass("loading"))
							$(this).addClass('ui-state-hover');
					},
					function() {
						if(!$(this).hasClass("ui-state-disabled") &&
								!$(this).hasClass("loading"))
							$(this).removeClass('ui-state-hover');
					}
				);
			
			loxia.initContext($t.find("tbody:last tr:last"));			
			
			this._setPager();
			
			$(".ui-pager", $t).livequery(function() {
				$(".ui-pager .ui-icon", $t).unbind("click").bind("click", function() {	
					var $p = $(this).parent();
					if ($p.hasClass("ui-state-disabled") ||
							$p.hasClass("loading")) {
						return false;
					}
					if ($p.hasClass("ui-state-hover"))
						$p.removeClass("ui-state-hover");
					
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
			var $t = this.element;
			$(".ui-pager-block[block='pagesize'] select",$t)
				.data("loxiaselect").val("" + this.options.pageSize);
			$(".ui-pager-block[block='pagegoto'] input",$t)
			.data("loxianumber").val("" + this.options.currentPage);
			$(".ui-pager-block[block='pagegoto'] span").text(this.options.pageCount);
			$(".ui-pager-block .page-info span").text(loxia.getLocaleMsg("TABLE_PAGE_INFO",[this.options.itemCount]));
			
			var currentPage = this.options.currentPage;
			var pageCount = this.options.pageCount;
			$t.find(".ui-pager .ui-state-disabled").removeClass("ui-state-disabled");
			if (currentPage == 1) {								
				$t.find(".ui-pager .ui-icon-seek-first").parent().addClass("ui-state-disabled");
				$t.find(".ui-pager .ui-icon-seek-prev").parent().addClass("ui-state-disabled");				
			}

			if (currentPage >= pageCount) {
				$t.find(".ui-pager .ui-icon-seek-next").parent().addClass("ui-state-disabled");
				$t.find(".ui-pager .ui-icon-seek-end").parent().addClass("ui-state-disabled");				
			}
			$t.find(".ui-pager-status > div").hide().css({opacity:0});
		},
		_init: function(){
			this.element.removeAttr("loxiaType");
			this.element.addClass("loxia ui-loxia-table");
			this._initSelector();
			this._loadData(false);
			this._initStyle();
			this._initHeadAction();
			this._initPager();
			
			var _this = this;
			$("tbody:first .selector input", this.element).livequery(function(){
				$(this).click(function(){
					if(_this.options.selectCols == 1)
						$(this).parents("tr").toggleClass("selected");
					var selected = _this.options.selected["col_" + $(this).parents("td").get(0).cellIndex];
					var value = _this._formatName($(this).val());
					if($(this).is(":checked")){
						if($(this).is("input[type='radio']")){
							if(value in selected){
								delete selected[value];
								$(this).attr("checked",false);
							}else{
								for(key in selected)
									delete selected[key];
								selected[value] = value;
							}
						}else{
							selected[value] = value;
						}
					}else{
						delete selected[value];
					}
					//console.dir($t.data("selected"));
					_this.element.trigger("selectchanged",[[$(this),selected]]);
				});
			});

			$("thead .selector input",this.element).click(function(){
				var i = $(this).parents("th").get(0).cellIndex;
				var needStyle = (_this.options.selectCols == 1);
				var selected = _this.options.selected["col_" + i];
				var checked = $(this).is(":checked");

				var changed = false;
				_this.element.find("tbody:first tr").find("td:eq(" + i + ") input").each(function(){
					var value = _this._formatName($(this).val());
					if($(this).is(":checked") != checked){
						changed = true;
						$(this).attr("checked", checked);

						if(checked)
							selected[value] = value;
						else
							delete selected[value];

						if(needStyle)
							$(this).parents("tr").toggleClass("selected");
					}
				});

				if(changed)
					_this.element.trigger("selectchanged",[[$(this),selected]]);
			});
		}
	});
	$.widget("ui.loxiatable", loxiaTable); 
	$.ui.loxiatable.getter = ""; 
	$.ui.loxiatable.defaults = $.extend({},loxiaBaseTableDefaults,{
		sort: "",
		page: false,
		alwaysShowPage: false,
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
	
	var loxiaEditTable = $.extend({}, loxiaBaseTable, {
		setAddable : function(flag){
			this.options.addable = flag;
			var $btn = this.element.find(".ui-bar .ui-icon[action='Add']").parent();
			$btn.removeClass("ui-state-disabled");
			if(!flag)
				$btn.addClass("ui-state-disabled");
		},
		setDeletable : function(flag){
			this.options.deletable = flag;
			var $btn = this.element.find(".ui-bar .ui-icon[action='Delete']").parent();
			$btn.removeClass("ui-state-disabled");
			if(!flag)
				$btn.addClass("ui-state-disabled");
		},
		_initExecBar : function(){
			var $t = this.element;
			
			var bar =
				'<div class="ui-bar">';
			bar += '<div class="ui-bar-block" block="operator">' +
			'<div class="separator"></div>' +
			'<div class="ui-state-default ui-corner-all' + (this.options.addable?'':' ui-state-disabled') + '"><span action="Add" title="' + loxia.getLocaleMsg("TABLE_BARBTN_ADD") + '" class="ui-icon ui-icon-plusthick"></span></div> ' +
			'<div class="ui-state-default ui-corner-all' + (this.options.deletable?'':' ui-state-disabled') + '"><span action="Delete" title="' + loxia.getLocaleMsg("TABLE_BARBTN_DELETE") + '" class="ui-icon ui-icon-minusthick"></span></div> ' +
			'<div class="separator"></div></div>';
			
			bar += '</div>';
			var $tbody = $t.find("tbody:last").append('<tr class="last"><td colspan="' + this.options.cols + '">'
					+ bar + "</td></tr>");

			var _this = this;
			
			$('.ui-bar .ui-state-default',$t).hover(
					function() {
						if(!$(this).hasClass("ui-state-disabled"))
							$(this).addClass('ui-state-hover');
					},
					function() {
						if(!$(this).hasClass("ui-state-disabled"))
							$(this).removeClass('ui-state-hover');
					}
				);
			$(".ui-bar", $t).livequery(function() {
				$(".ui-bar .ui-icon", $t).unbind("click").bind("click", function() {
					var $p = $(this).parent();
					if ($p.hasClass("ui-state-disabled")) {
						return false;
					}
					switch($(this).attr("action")){
					case "Add" :
						_this.appendRow();										
						break;
					case "Delete":
						_this.deleteRow();
						break;
					}
					_this._calculateFoot();	
					
					$t.find('tbody:first tr:odd').removeClass("even").addClass("odd");
					$t.find('tbody:first tr:even').removeClass("odd").addClass("even");
				});
			});
		},
		_init: function(){
			this.element.removeAttr("loxiaType");
			this.element.addClass("loxia ui-loxia-table");
			
			var _this = this;
			var $t = this.element;
			if($("tbody", $t).length != 2)
				throw new Error("Current table need at least and only 2 tbodies.");

			$t.find("thead tr").each(function(i){					
				$(this).addClass("ui-widget-header");
				$(this).find("th").addClass("ui-state-default");
			});
			$t.find("tbody:gt(0)").addClass("ui-widget-content");
			
			var formulas = [];
			var cols = $t.find("thead tr:last th").each(function(i){
				$(this).html("<div class='th-col-" + i + "'>" + $(this).html() + "</div>");
				$t.find("tbody tr").find("td:eq(" + i + ")").addClass(" col-" + i);				

				var formula = $(this).attr("formula") || "";
				formulas.push(formula);

				$(this).hover(function(){
					$(this).toggleClass("ui-state-hover");
				},function(){
					$(this).toggleClass("ui-state-hover");
				});
			}).length;
			
			this.options.cols = cols;

			$t.find("tfoot tr").each(function(){
				var index = 0;
				$(this).find("td").each(function(){
					var colspan = parseInt($(this).attr("colspan")||"1");
					$(this).addClass("col-" + index);
					$(this).data("col", index);
					index += colspan;
				});
			});
			$t.find("tfoot tr:last").addClass("last");
			this.options.formulas = formulas;

			$t.find("tbody .col-0 input[type='checkbox']").each(function(){
				$(this).parents("td").addClass("selector");
			});

			var $tptTbody = $("tbody:last", $t);
			var templateStr = $tptTbody.html();
			this.options.template = templateStr;
			$("tr",$tptTbody).remove();

			loxia.initContext($t.find("tbody:first"));

			for(var i=0; i< this.options.append; i++)
				this.appendRow();

			this._calculateRow();
			this._calculateFoot();

			$t.find('tbody:first tr:odd').removeClass("even").addClass("odd");
			$t.find('tbody:first tr:even').removeClass("odd").addClass("even");

			this._initExecBar();

			$("thead tr:last", $t).find(".th-col-0 input[type='checkbox']").bind("click", function(){
				if($(this).is(":checked")){
					$("tbody:first .col-0 input[checked='false']", $t).each(function(){
						$(this).attr("checked", true);
						$(this).parents("tr").addClass("selected");
					});
				}else{
					$("tbody:first .col-0 input:checked", $t).each(function(){
						$(this).attr("checked", false);
						$(this).parents("tr").removeClass("selected");
					});
				}
			});
			
			$("tbody:first tr", $t).livequery(function(){
				var $tr = $(this);
				$("input,select,textarea", $tr).each(function(){
					if($(this).parents("td").is(".selector")){
						$(this).unbind("click").bind("click", function(){
							$tr.removeClass("select");
							if($(this).is(":checked"))
								$tr.addClass("selected");
							else
								$tr.removeClass("selected");
						});
					}else if(loxia.isLoxiaWidget(this))
						$(this).unbind("valuechanged").bind("valuechanged", function(event, data){
							$t.trigger("rowchanged",[[$tr.get(0),this]]);
						});
					else if($(this).is("select"))
						$(this).unbind("change").bind("change", function(event, data){
							$t.trigger("rowchanged",[[$tr.get(0),this]]);
						});
					else
						$(this).unbind("blur").bind("blur", function(event, data){
							$t.trigger("rowchanged",[[$tr.get(0),this]]);
						});
				});
			});
			
			$t.bind("rowchanged", function(event, data){
				var row = data[0];
				if(row)
					_this._calculateRow(row);
				_this._calculateFoot();
			});
		},		
		appendRow : function(){
			var $t = this.element;
			var rowIndex = "" + (--loxiaRowIndex);
			var row = this.options.template.replace(/\(#\)/ig, "(" + rowIndex + ")");
			$t.find("tbody:first").append(row);
			loxia.initContext($t.find("tbody:first tr:last"));
			$t.trigger("rowappended", [$t.find("tbody:first tr:last")]);
		},
		deleteRow : function(){
			var $t = this.element;			
			if($("tbody:first .col-0 :checked", $t).length > 0){
				$t.find("tbody:first tr:has(.col-0 input:checked)").remove();
				$t.trigger("rowdeleted");
			}
		},
		_calculateRow : function(context){
			var $t = $(this);
			context = context || $t.find("tbody:first");
			var $rows = $(context);
			if(!$rows.is("tr"))
				$rows = $rows.find("tr");

			var calCols = [];
			var formulas = this.options.formulas;
			for(var i=0; i< this.options.cols; i++)
				if(formulas[i]) calCols.push(i);

			if(calCols.length >0){
				for(var i=0; i< calCols.length; i++){
					var formula = formulas[calCols[i]];
					var decimal = 0;
					var delim = formula.indexOf(":");
					if(delim > 0){
						decimal = parseInt(formula.substring(delim + 1));
						formula = formula.substring(0, delim);
					}

					var params = formula.match(/\$\d+/ig);
					formula = formula.replace(/\$\d+/ig,"#");

					$rows.each(function(){
						var f = "" + formula;
						for(var j=0; j< params.length; j++){
							var cellIndex = parseInt(params[j].replace(/\$/,""));
							var p = loxia.val($(this).find("td:eq(" + cellIndex + ")").get(0));
							p = (p == null || $.trim(p) == "") ? 0 : p;
							f = f.replace(/\#/,p);
						}
						var value = eval(f);
						value = value? value.toFixed(decimal): "";
						$(this).find("td:eq(" + calCols[i] + ")").text(value);
					});
				}
			}
		},
		_calculateFoot : function(){
			var $t = this.element;
			$t.find("tfoot td[decimal]").each(function(){
				var decimal = parseInt($(this).attr("decimal"));
				var result = 0;
				$t.find("tbody:first tr").find("td:eq(" + $(this).data("col") + ")").each(function(){
					var value = parseFloat(loxia.val($(this).get(0)));
					value = isNaN(value) ? 0 : value;
					result += value == null ? 0 : value;
				})
				$(this).text(result.toFixed(decimal));
			});
			$t.trigger("calculated");
		}
	});
	$.widget("ui.loxiaedittable", loxiaEditTable); 
	$.ui.loxiaedittable.getter = ""; 
	$.ui.loxiaedittable.defaults = $.extend({},loxiaBaseTableDefaults,{
		addable: true,
		deletable: true,
		append: 0
	});
})(jQuery);