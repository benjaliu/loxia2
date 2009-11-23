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
			select: "", // can be blank, single or multi
			images: "images/loxia"
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
		if(data && data.length && data.length >0){
			var rowlist = "";
			for(var dataItem,i=0; (dataItem = data[i]); i++){
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
			if(reloadAll){
				$tbody.html(rowlist);
			}else
				$tbody.append(rowlist);
		}
	};
	$.fn.ltInit = function(settings){
		var $t = $(this);		
		$t.ltLoadData(settings.data, false);
		$t.ltInitStyle(settings);
		$t.ltInitHeadAction(settings);
		$t.ltInitPager(settings);
	};
	
	$.fn.ltInitStyle = function(settings){
		var $t = $(this);
		var sortStatus = [];
		if(settings.sort){
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
			$(this).addClass(sortClass).addClass("col-" + i);
			$(this).html("<div class='th-col-" + i + "'>" + $(this).html() + "</div>");
			$t.find("tbody:first tr").find("td:eq(" + i + ")").addClass("col-" + i);
			$t.data("cols", ++i);
		});
		$t.find('tbody:first tr:odd').addClass("odd");
		$t.find('tbody:first tr:even').addClass("even");
			
		return this;
	}
	
	$.fn.ltInitHeadAction = function(settings){		
		var $t = $(this);
		$("thead tr:last th", $t).livequery(function(){
			$(this).hover(function(){
				$(this).addClass("hover");
			},function(){
				$(this).removeClass("hover");
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
						},
						error : function(XMLHttpRequest, textStatus, errorThrown){
							//TODO exception handling here
							var exception = {};
							exception["message"] = "Error occurs when fetching data from url:" + this.url;
							exception["cause"] = textStatus? textStatus : errorThrown;
							//console.dir(exception);
						}
				};
				if(settings.form) args["form"] = settings.form;
				//TODO add Loading here
				$.loxia.asyncXhr(settings.url, args, function(data, textStatus){
					//console.dir(data);
					if(data.exception){
						//TODO exception handling here
					}else{
						$t.ltLoadData(data.data, true);
						$("thead tr:last th", $t).removeClass("sort-asc sort-desc");
						$th.addClass("sort-" + sortOrder);
						$t.find('tbody:first tr:odd').addClass("odd");
						$t.find('tbody:first tr:even').addClass("even");
						//TODO remove Loading here							
					}
				});
			});
		});
		
		return this;
	}
	
	$.fn.ltSetPager = function(settings){
		var $t = $(this);
		if(!settings.page) return this;
		
		$t.data("pageSize", settings.pageSize);
		$t.data("currentPage", settings.currentPage);
		$t.data("pageCount", settings.pageCount);
		$t.data("itemCount", settings.itemCount);
		$t.data("pageItemCount", settings.pageItemCount);
	}
	
	$.fn.ltInitPager = function(settings){
		var $t = $(this);
		if(!settings.page) return this;

		$t.data("pageSize", settings.pageSize);
		$t.data("currentPage", settings.currentPage);
		$t.data("pageCount", settings.pageCount);
		$t.data("itemCount", settings.itemCount);
		$t.data("pageItemCount", settings.pageItemCount);


		var pager =
			'<div class="ltPager">' +
			'<div class="inner"><div class="perPage">' +
			'Per Page: <select>';
			for (i=0; i<settings.pageSizeOptions.length; i++) {
				var selected = settings.pageSize == settings.pageSizeOptions[i] ? ' selected="selected"' : '';
				pager += '<option value="'+settings.pageSizeOptions[i]+'"'+selected+'>'+settings.pageSizeOptions[i]+'</option>';
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
			
		pager += '<div class="pages">Page <input class="loxia pageInput" checkmaster="checkNumber" value="'+ settings.currentPage + '"/>' +
			'/<span class="totalPages">' + $t.data("pageCount") + '</span><input type="button" class="loxia" value="Go"/></div>';
		
		pager +=			
			'<div>' +
			'<div class="separator"></div>' +
			'<div class="imgWrap load"><img src="'+settings.images+'/loading.gif" alt="Load" title="Reload" /></div>' +
			'<div class="separator"></div>' +
			'<div class="displaying">Displaying ' + itemStart + ' to ' + itemEnd + ' of ' + $t.data("itemCount") + ' items</div>' +
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
		
		$t.ltSetPagerImages(settings, settings.currentPage, settings.pageCount);
		
		return this;
	}	
	
	$.fn.ltSetPagerImages = function(settings, page, pages) {
		$this = $(this);

		if (page == 1) {
			$this.find(".ltPager img[src$='prev.gif']").attr('src', settings.images+'/prev-disabled.gif');
			$this.find(".ltPager img[src$='first.gif']").attr('src', settings.images+'/first-disabled.gif');

			$this.find('.ltPager').find("img[src$='next-disabled.gif']").attr('src', settings.images+'/next.gif');
			$this.find('.ltPager').find("img[src$='last-disabled.gif']").attr('src', settings.images+'/last.gif');
		}

		if (page >= pages) {
			$this.find(".ltPager img[src$='next.gif']").attr('src', settings.images+'/next-disabled.gif');
			$this.find(".ltPager img[src$='last.gif']").attr('src', settings.images+'/last-disabled.gif');

			if (page != 1) {
				$this.find(".ltPager img[src$='prev-disabled.gif']").attr('src', settings.images+'/prev.gif');
				$this.find(".ltPager img[src$='first-disabled.gif']").attr('src', settings.images+'/first.gif');
			}
		}

		if (page != 1 && page != pages) {
			$this.find('.ltPager').find("img[src$='next-disabled.gif']").attr('src', settings.images+'/next.gif');
			$this.find('.ltPager').find("img[src$='last-disabled.gif']").attr('src', settings.images+'/last.gif');

			$this.find(".ltPager img[src$='prev-disabled.gif']").attr('src', settings.images+'/prev.gif');
			$this.find(".ltPager img[src$='first-disabled.gif']").attr('src', settings.images+'/first.gif');
		}
	};
})(jQuery);