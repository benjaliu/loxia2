(function($){
	$.fn.loxiaTable = function(settings){		
		settings = $.extend({},{
			sort: "",			
			page: false,
			pageSize: 20,
			itemCount: 0,
			pageItemCount: 0,
			pageSizeOptions: ["10", "15", "20", "25", "30", "40", "50", "100"],
			currentPage : 1,
			pageCount: 1,
			data: [],
			form: "",
			url: "",
			cacheSelect: false,
			selected: {},
			images: "images/loxia"
		},settings);
		this.each(function(){
			$(this).addClass("loxiatable");
			$(this).ltInit(settings);
		});
		return this;
	};
	
	$.fn.ltInitSelector = function(settings){
		var $t = $(this);
		var selectors = [];
		var selected = settings.selected;
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
		
		$t.data("cacheSelect", settings.cacheSelect);
		$t.data("selectCols", sels);
		$t.data("selectors", selectors);
		$t.data("selected", selected);
		return this;
	};
	
	$.fn.ltLoadData = function(data, reloadAll){
		var $t = $(this);
		var $tbody = $t.find("tbody:first");
		var propList = [];
		
		$t.find("thead tr:last th").each(function(){
			propList.push($(this).attr("property"));
		});
		
		var selectors = $t.data("selectors");
		if(data && data.length && data.length >0){
			var rowlist = "";
			for(var dataItem,i=0; (dataItem = data[i]); i++){
				var row = "<tr>";
				for(var p,j=0;p=propList[j];j++){
					var value = $.loxia.getObject(p,dataItem);					
					if(selectors[j]){
						var selected = $t.data("selected")["col_"+j];						
						value = (value == undefined || value == null ||
								($.loxia.isString(value) && !value)) ? "" : "" + value;
						value = value.replace(/\./ig,"_");
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
								($.loxia.isString(value) && !value)) ? "&nbsp;" : value;
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
		$t.trigger("dataReloadedEvent",[data]);
	};
	$.fn.ltInit = function(settings){
		var $t = $(this);	
		
		if(settings.url)
			$t.data("url", settings.url);
		if(settings.form)
			$t.data("form", settings.form);
		if(settings.images)
			$t.data("images", settings.images);
		
		$t.ltInitSelector(settings);
		$t.ltLoadData(settings.data, false);
		$t.ltInitStyle(settings);
		$t.ltInitHeadAction(settings);
		$t.ltInitPager(settings);
		
		$("tbody:first .selector input", $t).livequery(function(){
			$(this).click(function(){
				if($t.data("selectCols") == 1)
					$(this).parents("tr").toggleClass("selected");
				var selected = $t.data("selected")["col_" + $(this).parents("td").get(0).cellIndex];
				var value = $(this).val().replace(/\./ig,"_");
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
				$t.trigger("selectChangedEvent",[[$(this),selected]]);
			});
		});
		
		$("thead .selector input",$t).click(function(){
			var i = $(this).parents("th").get(0).cellIndex;
			var needStyle = ($t.data("selectCols") == 1);
			var selected = $t.data("selected")["col_" + i];
			var checked = $(this).is(":checked");
			
			var changed = false;
			$t.find("tbody:first tr").find("td:eq(" + i + ") input").each(function(){
				var value = $(this).val().replace(/\./ig,"_");
				console.log($(this).is(":checked"));
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
				$t.trigger("selectChangedEvent",[[$(this),selected]]);
		});
	};
	
	$.fn.ltInitStyle = function(settings){
		var $t = $(this);
		var sortStatus = [];
		if(settings.sort){
			$t.data("sort", settings.sort);
			var sortlist = settings.sort.split(",");
			sortStatus.push(sortlist[0]);
			sortStatus.push(sortlist[1]||"asc");
		}
		$t.find("thead tr:last th").each(function(i){
			if(settings.sort){
				var sort = $(this).attr("sort");
				var sortClass = "";
				if(!sort) sortClass = "nosort";
				else if(sort == sortStatus[0]){
					if(sortStatus[1].toLowerCase() == "asc") sortClass = "sort-asc";
					else if(sortStatus[1].toLowerCase() == "desc") sortClass = "sort-desc";
				}		
			}
			var className = $(this).hasClass("selector") ? "selector" : "";
			$(this).addClass(sortClass).addClass("col-" + i);
			$(this).html("<div class='th-col-" + i + "'>" + $(this).html() + "</div>");
			$t.find("tbody:first tr").find("td:eq(" + i + ")").addClass(className + " col-" + i);
			$t.data("cols", ++i);
		});
		$t.find('tbody:first tr:odd').addClass("odd");
		$t.find('tbody:first tr:even').addClass("even");
			
		if($t.data("selectCols") == 1)
			$t.find("tbody:first input:checked").parents("tr").addClass("selected");
		return this;
	};
	
	$.fn.ltRefreshTable = function(settings){
		var $t = $(this);
		var imagePath = settings.images || $t.data("images");
		$t.find('.ltToolbar .load img').attr('src', imagePath+'/load.gif');
		if(!$t.data("cacheSelect")){
			var selected = $t.data("selected");
			for(key in selected){
				selected[key] = {};
			}
		}
		settings = $.extend({
			error : function(XMLHttpRequest, textStatus, errorThrown){
			//TODO exception handling here
			var exception = {};
			exception["message"] = "Error occurs when fetching data from url:" + this.url;
			exception["cause"] = textStatus? textStatus : errorThrown;
			//console.dir(exception);
			}
		},settings);
		//console.dir(settings);
		var url = settings.url || $t.data("url");
		$.loxia.asyncXhr(url, settings, function(data, textStatus){
			//console.dir(data);
			if(data.exception){
				//TODO exception handling here
			}else{
				$t.ltLoadData(data.data, true);
				
				if(data.sort){
					$t.data("sort",data.sort);
					var sortStatus = [];

					var sortlist = data.sort.split(",");
					sortStatus.push(sortlist[0]);
					sortStatus.push(sortlist[1]||"asc");
					
					$t.find("thead tr:last th").each(function(i){
						if(data.sort){
							$(this).removeClass("sort-asc sort-desc");
							var sort = $(this).attr("sort");
							if(sort == sortStatus[0]){
								$(this).addClass("sort-" + sortStatus[1]);
							}		
						}		
						
						var className = $(this).hasClass("selector") ? "selector" : "";
						$t.find("tbody:first tr").find("td:eq(" + i + ")").addClass(className + " col-" + i);
						$t.data("cols", ++i);
					});
				}

				$t.find('tbody:first tr:odd').addClass("odd");
				$t.find('tbody:first tr:even').addClass("even");
				
				if($t.data("selectCols") == 1)
					$t.find("tbody:first input:checked").parents("tr").addClass("selected");
				
				if(data.page)
					$t.ltSetPager(data);
				//TODO remove Loading here							
			}
		});
	};
	
	$.fn.ltInitHeadAction = function(settings){		
		var $t = $(this);
		$("thead tr:last th", $t).livequery(function(){
			$(this).hover(function(){
				$(this).toggleClass("hover");
			},function(){
				$(this).toggleClass("hover");
			});
			
			$(this).click(function(){
				var $th = $(this);
				if($th.hasClass("nosort")) return;
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
							currentPage: settings.currentPage,
							pageSize: settings.pageSize,
							sortString: sortStr
						}
				};
				if(settings.form) args["form"] = settings.form;
				//TODO add Loading here
				$t.ltRefreshTable(args);
			});
		});
		
		return this;
	};
	
	$.fn.ltSetPager = function(settings){
		var $t = $(this);
		if(!settings.page) return this;
		
		$t.data("page", true);
		$t.data("pageSize", settings.pageSize);
		$t.data("currentPage", settings.currentPage);
		$t.data("pageCount", settings.pageCount);
		$t.data("itemCount", settings.itemCount);
		$t.data("pageItemCount", settings.pageItemCount);
		
		$(".perPage select",$t).val("" + settings.pageSize);
		$(".pages input.pageInput").val(settings.currentPage);
		$(".pages .totalPages").text(settings.pageCount);
		$(".displaying .totalCount").text(settings.itemCount);
		
		$t.ltSetPagerImages(settings, settings.currentPage, settings.pageCount);
	};
	
	$.fn.ltInitPager = function(settings){
		var $t = $(this);
		if(!settings.page) return this;

		var pager =
			'<div class="ltToolbar">' +
			'<div class="inner"><div class="perPage">' +
			'Per Page: <select>';
			for (i=0; i<settings.pageSizeOptions.length; i++) {				
				pager += '<option value="'+settings.pageSizeOptions[i]+'">'+settings.pageSizeOptions[i]+'</option>';
			}

		var itemStart = settings.pageSize * (settings.currentPage - 1) + 1;
		var itemEnd = itemStart + settings.pageItemCount - 1;
		
		pager += '</select>' + '</div>' +
			'<div class="separator"></div>' +
			'<div class="pager">' +
			'<div class="imgWrap" style="margin-right: 0px;"><img src="'+settings.images+'/first.gif" alt="First" title="First Page" /></div> ' +
			'<div class="imgWrap"><img src="'+settings.images+'/prev.gif" alt="Previous" title="Previous" /></div> ' +
			'<div class="imgWrap"><img src="'+settings.images+'/next.gif" alt="Next" title="Next" /></div> ' +
			'<div class="imgWrap"><img src="'+settings.images+'/last.gif" alt="Last" title="Last Page" /></div>' +
			'<div class="separator"></div></div>';
			
		pager += '<div class="pages">Page <input class="lidget pageInput" checkmaster="checkNumber" value=""/>' +
			'/<span class="totalPages"></span></div>' +
			'<div class="pager"><div class="imgWrap"><img src="'+settings.images+'/goto.gif" alt="Goto" title="Go to Page" /></div></div>';
		
		pager +=			
			'<div>' +
			'<div class="separator"></div>' +
			'<div class="imgWrap load"><img src="'+settings.images+'/loading.gif" alt="Load" title="Reload" /></div>' +
			'<div class="separator"></div>' +
			'<div class="displaying"><span class="totalCount"></span> items</div>' +
			'</div>';

/*			if (opts.toggle) {
				pager = pager + '<div class="' + opts.toggleHolder + '"></div>';
			}*/

		pager += '</div>' +
			'</div>';
		
		var $tbody = $t.find("tbody:last").append('<tr><td colspan="' + $t.data("cols")+ '">'
				+ pager + "</td></tr>");
		
		$('.ltToolbar .imgWrap',$t).hover(
				function() {
					$(this).toggleClass('imgWrapHover');
				},
				function() {
					$(this).toggleClass('imgWrapHover');
				}
			);
		
		$.loxia.initLidgets($t.find("tbody:last tr:last"));			
		
		$t.ltSetPager(settings);
		
		$(".ltToolbar", $t).livequery(function() {
			$(".ltToolbar img", $t).unbind("click").bind("click", function() {
				var src = $(this).attr("src");
				if (src.match(/disabled\.gif$/)) {
					return false;
				}
				var action = $(this).attr("alt");
				
				var moveto = true;
				var moveToPage = $t.data("currentPage");
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
					var $input = $(".ltToolbar .pages .pageInput", $t);
					if(!$input.data("state"))
						$.loxia.lidget.check($input);
					
					if($input.data("state"))
						moveToPage = parseInt($input.val());
					else
						moveto = false;
				}
				if(moveto){
					var settings = {
						url: $t.data("url"),
						data: {
							pageSize: $t.data("pageSize"),
							currentPage: moveToPage
						}
					};
					if($t.data("sort"))
						settings["sort"] = $t.data("sort");
					if($t.data("form"))
						settings["form"] = $t.data("form");
					//TODO add Loading here				
					$t.ltRefreshTable(settings);
				}
			});			
			
			$(".ltToolbar select", $t).unbind("change").bind("change", function() {
				var pageSize = parseInt($(this).val());
				
				var settings = {
					url: $t.data("url"),
					data: {
						pageSize: pageSize,
						currentPage: 1
					}
				};
				if($t.data("sort"))
					settings["sort"] = $t.data("sort");
				if($t.data("form"))
					settings["form"] = $t.data("form");
				//TODO add Loading here				
				$t.ltRefreshTable(settings);
			});
		});
		return this;
	};	
	
	$.fn.ltSetPagerImages = function(settings, page, pages) {
		var $t = $(this);
		var imagePath = settings.images || $t.data("images");
		
		if (page == 1) {
			$t.find(".ltToolbar img[src$='prev.gif']").attr('src', imagePath+'/prev-disabled.gif');
			$t.find(".ltToolbar img[src$='first.gif']").attr('src', imagePath+'/first-disabled.gif');

			$t.find('.ltToolbar').find("img[src$='next-disabled.gif']").attr('src', imagePath+'/next.gif');
			$t.find('.ltToolbar').find("img[src$='last-disabled.gif']").attr('src', imagePath+'/last.gif');
		}

		if (page >= pages) {
			$t.find(".ltToolbar img[src$='next.gif']").attr('src', imagePath+'/next-disabled.gif');
			$t.find(".ltToolbar img[src$='last.gif']").attr('src', imagePath+'/last-disabled.gif');

			if (page != 1) {
				$t.find(".ltToolbar img[src$='prev-disabled.gif']").attr('src', imagePath+'/prev.gif');
				$t.find(".ltToolbar img[src$='first-disabled.gif']").attr('src', imagePath+'/first.gif');
			}
		}

		if (page != 1 && page != pages) {
			$t.find('.ltToolbar').find("img[src$='next-disabled.gif']").attr('src', imagePath+'/next.gif');
			$t.find('.ltToolbar').find("img[src$='last-disabled.gif']").attr('src', imagePath+'/last.gif');

			$t.find(".ltToolbar img[src$='prev-disabled.gif']").attr('src', imagePath+'/prev.gif');
			$t.find(".ltToolbar img[src$='first-disabled.gif']").attr('src', imagePath+'/first.gif');
		}
		
		$t.find('.ltToolbar .load img').attr('src', imagePath+'/load.png');
	};
})(jQuery);

(function($){
	var ltRowIndex = 0;
	$.fn.loxiaDynTable = function(settings){		
		settings = $.extend({},{	
			canAdd: true,
			canDelete: true,
			append: 0,
			images: "images/loxia"
		},settings);
		this.each(function(){
			$(this).addClass("loxiatable editable");
			$(this).ltDynInit(settings);
			
			$(this).bind("rowChangedEvent", function(event, data){
				var $row = data[0];
				if($row)
					$(this).ltCalculateRow($row);
				$(this).ltCalculateFoot(settings);
			});
		});
				
		return this;
	};
	
	$.fn.ltDynInit = function(settings){
		var $t = $(this);
		if($("tbody", $t).length != 2)
			throw new exception("Current table need at least and only 2 tbodies.");
		
		var formulas = [];
		$t.find("thead tr:last th").each(function(i){
			$(this).addClass("nosort col-" + i);
			$(this).html("<div class='th-col-" + i + "'>" + $(this).html() + "</div>");
			$t.find("tbody tr").find("td:eq(" + i + ")").addClass(" col-" + i);
			$t.data("cols", ++i);
			
			var formula = $(this).attr("formula") || "";
			formulas.push(formula);
			
			$(this).hover(function(){
				$(this).toggleClass("hover");
			},function(){
				$(this).toggleClass("hover");
			});
		});
		
		$t.find("tfoot tr").each(function(){
			var index = 0;
			$(this).find("td").each(function(){
				var colspan = parseInt($(this).attr("colspan")||"1");
				$(this).addClass("col-" + index);
				$(this).data("col", index);
				index += colspan;
			});
		})
		$t.data("formulas", formulas);

		$t.find("tbody .col-0 input[type='checkbox']").each(function(){
			$(this).parents("td").addClass("selector");
		});
				
		var $tptTbody = $("tbody:last", $t);
		var templateStr = $tptTbody.html();
		$t.data("template", templateStr);
		$("tr",$tptTbody).remove();
		
		$.loxia.initLidgets($t.find("tbody:first"));
		
		for(var i=0; i< settings.append; i++)
			$t.ltAppendRow(settings);
		
		$t.ltCalculateRow();
		$t.ltCalculateFoot(settings);
		
		$t.ltRefreshStyle(settings);
		
		$t.ltSetExecBar(settings);
		
		$("tbody:first tr", $t).livequery(function(){
			var $tr = $(this);
			$("input,select,textarea", $tr).each(function(){
				if($(this).parents("td").is(".selector")) return;
				if($(this).is(".loxia")) 
					$(this).unbind("valueChangedEvent").bind("valueChangedEvent", function(event, data){
						$t.trigger("rowChangedEvent",[[$tr,$(this)]]);
					});
				else if($(this).is("select"))
					$(this).unbind("change").bind("change", function(event, data){
						$t.trigger("rowChangedEvent",[[$tr,$(this)]]);
					});
				else
					$(this).unbind("blur").bind("blur", function(event, data){
						$t.trigger("rowChangedEvent",[[$tr,$(this)]]);
					});
			});
		});
		return this;
	};
	
	$.fn.ltRefreshStyle = function(settings){
		var $t = $(this);		
		$t.find('tbody:first tr:odd').removeClass("even").addClass("odd");
		$t.find('tbody:first tr:even').removeClass("odd").addClass("even");
			
		return this;
	};
	
	$.fn.ltSetExecBar = function(settings){
		var $t = $(this);
		
		var disabled = " disabled";
		var bar =
			'<div class="ltToolbar">' +
			'<div class="inner">' +
			'<div class="manipulation">';
		//if(settings.canAdd)
			bar += '<input type="button" class="loxia add" value="Add" title="Add New Row"' + (settings.canAdd?"":disabled) + '/>';
		//if(settings.canDelete)
			bar += '<input type="button" class="loxia delete" value="Delete" title="Delete Selected Rows"' + (settings.canDelete?"":disabled) + '/>';
		
		bar += '<div class="separator"></div></div></div></div>';
		
		var $tbody = $t.find("tbody:last").append('<tr><td colspan="' + $t.data("cols")+ '">'
				+ bar + "</td></tr>");
		
		$(".ltToolbar", $t).livequery(function() {
			$(".ltToolbar .add", $t).unbind("click").bind("click", function() {
				$t.ltAppendRow(settings);
				$t.ltCalculateFoot(settings);
				$t.ltRefreshStyle(settings);
			});
			
			$(".ltToolbar .delete", $t).unbind("click").bind("click", function() {
				$t.ltDeleteRows(settings);			
				$t.ltCalculateFoot(settings);
				$t.ltRefreshStyle(settings);
			});
		});
		return this;
	}
	
	$.fn.ltAppendRow = function(settings){
		var $t = $(this);	
		var rowIndex = "" + (--ltRowIndex);
		var row = $t.data("template").replace(/\(#\)/ig, "(" + rowIndex + ")");
		$t.find("tbody:first").append(row);
		$.loxia.initLidgets($t.find("tbody:first tr:last"));
		$t.trigger("RowAppendedEvent", [$t.find("tbody:first tr:last")]);
		return this;
	};
	
	$.fn.ltDeleteRows = function(settings){
		var $t = $(this);
		if($("tbody:first .col-0 :checked", $t).length > 0){
			$t.find("tbody:first tr:has(.col-0 input:checked)").remove();
			$t.trigger("RowDeletedEvent");
		}
		return this;
	}
	
	$.fn.ltCalculateRow = function(context){
		var $t = $(this);
		context = context || $t.find("tbody:first");
		var $rows = $(context);
		if(!$rows.is("tr"))
			$rows = $rows.find("tr");
		
		var calCols = [];
		var formulas = $t.data("formulas");
		for(var i=0; i< $t.data("cols"); i++)
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
						var p = $.loxia.val($(this).find("td:eq(" + cellIndex + ")").get(0));
						p = p == null ? 0 : p;
						f = f.replace(/\#/,p);
					}
					var value = eval(f);
					value = value? value.toFixed(decimal): "";
					$(this).find("td:eq(" + calCols[i] + ")").text(value);
				});
			}			
		}
	}
	
	$.fn.ltCalculateFoot = function(settings){
		var $t = $(this);
		$t.find("tfoot td[decimal]").each(function(){
			var decimal = parseInt($(this).attr("decimal"));
			var result = 0;
			$t.find("tbody:first tr").find("td:eq(" + $(this).data("col") + ")").each(function(){
				var value = parseFloat($.loxia.val($(this).get(0)));
				value = isNaN(value) ? 0 : value;
				result += value == null ? 0 : value;
			})
			$(this).text(result.toFixed(decimal));
		});
		$t.trigger("tableCalculatedEvent");
		return this;
	}
})(jQuery);