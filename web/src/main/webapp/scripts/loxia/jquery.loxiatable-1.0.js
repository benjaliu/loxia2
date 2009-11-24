(function($){
	$.fn.loxiatable = function(settings){		
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
							throw new Exception("Load Data for Selector Error.");
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
	}
	
	$.fn.ltRefreshTable = function(settings){
		var $t = $(this);
		var imagePath = settings.images || $t.data("images");
		$t.find('.ltPager .load img').attr('src', imagePath+'/load.gif');
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
	}
	
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
	}
	
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
	}
	
	$.fn.ltInitPager = function(settings){
		var $t = $(this);
		if(!settings.page) return this;

		var pager =
			'<div class="ltPager">' +
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
			
		pager += '<div class="pages">Page <input class="loxia pageInput" checkmaster="checkNumber" value=""/>' +
			'/<span class="totalPages"></span><input type="button" class="loxia" value="Go"/></div>';
		
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
		
		$('.ltPager .imgWrap',$t).hover(
				function() {
					$(this).toggleClass('imgWrapHover');
				},
				function() {
					$(this).toggleClass('imgWrapHover');
				}
			);
		
		$.loxia.initLidgets($t.find("tbody:last tr:last"));			
		
		$t.ltSetPager(settings);
		
		$(".ltPager", $t).livequery(function() {
			$(".ltPager img", $t).unbind("click").bind("click", function() {
				var src = $(this).attr("src");
				if (src.match(/disabled\.gif$/)) {
					return false;
				}
				var action = $(this).attr("alt");
				
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
				}
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
			});
			
			$(".ltPager .pages .loxiaButton", $t).unbind("click").bind("click", function(){
				var $input = $(".ltPager .pages .pageInput", $t);
				if(!$input.data("state"))
					$.loxia.lidget.check($input);
				
				if($input.data("state")){
					var moveToPage = parseInt($input.val());
					
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
			
			$(".ltPager select", $t).unbind("change").bind("change", function() {
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
	}	
	
	$.fn.ltSetPagerImages = function(settings, page, pages) {
		var $t = $(this);
		var imagePath = settings.images || $t.data("images");
		
		if (page == 1) {
			$t.find(".ltPager img[src$='prev.gif']").attr('src', imagePath+'/prev-disabled.gif');
			$t.find(".ltPager img[src$='first.gif']").attr('src', imagePath+'/first-disabled.gif');

			$t.find('.ltPager').find("img[src$='next-disabled.gif']").attr('src', imagePath+'/next.gif');
			$t.find('.ltPager').find("img[src$='last-disabled.gif']").attr('src', imagePath+'/last.gif');
		}

		if (page >= pages) {
			$t.find(".ltPager img[src$='next.gif']").attr('src', imagePath+'/next-disabled.gif');
			$t.find(".ltPager img[src$='last.gif']").attr('src', imagePath+'/last-disabled.gif');

			if (page != 1) {
				$t.find(".ltPager img[src$='prev-disabled.gif']").attr('src', imagePath+'/prev.gif');
				$t.find(".ltPager img[src$='first-disabled.gif']").attr('src', imagePath+'/first.gif');
			}
		}

		if (page != 1 && page != pages) {
			$t.find('.ltPager').find("img[src$='next-disabled.gif']").attr('src', imagePath+'/next.gif');
			$t.find('.ltPager').find("img[src$='last-disabled.gif']").attr('src', imagePath+'/last.gif');

			$t.find(".ltPager img[src$='prev-disabled.gif']").attr('src', imagePath+'/prev.gif');
			$t.find(".ltPager img[src$='first-disabled.gif']").attr('src', imagePath+'/first.gif');
		}
		
		$t.find('.ltPager .load img').attr('src', imagePath+'/load.png');
	};
})(jQuery);