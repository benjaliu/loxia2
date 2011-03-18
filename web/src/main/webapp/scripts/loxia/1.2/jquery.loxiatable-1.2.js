(function($){
	var _rowIndex = 0;
	loxia.regional['']['TABLE_BARBTN_ADD'] = "Add New Row";
	loxia.regional['']['TABLE_BARBTN_DELETE'] = "Remove Selected Row";
	var loxiaEditTable = {
		_formatName : function(str){
			return str.replace(/\./ig,"_");
		},
		
		_initTable: function(){
			var $t = this.element;
			var formulas = [];
			var cols = $t.find("thead tr:last").find("th").each(function(i){
				$(this).html("<div class='col-" + i + "'>" + $(this).html() + "</div>");
				$t.find("tbody:first tr,tbody:eq(1) tr").find("td:eq(" + i + ")").addClass("col-" + i);
				formulas.push($(this).attr("formula")||"");
				
				$(this).hover(function(){
					$(this).addClass("hover");
				},function(){
					$(this).removeClass("hover");
				});
			}).length;
						
			$t.find("tfoot tr").each(function(){
				var index = 0;
				$(this).find("td").each(function(){
					var colspan = parseInt($(this).attr("colspan")||"1",10);
					$(this).addClass("col-" + index);
					$(this).data("col", index);
					index += colspan;
				});
			});
			
			this.option("cols",cols);
			this.option("formulas",formulas);
			
			this.option("template",$("tbody:eq(1)", $t).html());
			$("tbody:eq(1)", $t).find("tr").remove();
			
			loxia.initContext($t.find("tbody:first"));
			
			var _this = this;
			$("tbody:first tr", $t).each(function(){
				_this._initRowAction(this);
			});
			
			if($t.attr("append")){
				var n = parseInt($t.attr("append"),10);
				if(!isNaN(n)){
					for(var i=0;i<n;i++)
						this.appendRow();
				}
			}
			
			$t.find("tbody tr:last").addClass("last");
			$t.find("tfoot tr:last").addClass("last");
			
			$("tbody:first .col-0 input:checked", $t).each(function(){
				$(this).parents("tr").addClass("selected");
			});
			
			this._adjustBodyStyle();
			
			this._initActionBar();
			
			this._calculateRow();
			this._calculateFoot();
			//event handler
			//head selector
			$t.bind("rowchanged", function(event, data){
				var row = data[0];
				if(row)
					_this._calculateRow(row);
				_this._calculateFoot();
			});
			$t.bind("rowappended", function(event, data){
				_this._calculateFoot();
			});
			$t.bind("rowdeleted", function(event, data){
				_this._calculateFoot();
			});
			
			$("thead tr", $t).find("th:eq(0) input[type='checkbox']").bind("click", function(){				
				if($(this).is(":checked")){
					$("tbody:first .col-0 input:not(:checked)", $t).each(function(){
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
			//line selector
			$("tbody:first").find("td.col-0 input[type='checkbox']").live("click", function(){
				if($(this).is(":checked")){
					$(this).parents("tr").addClass("selected");
				}else{
					$(this).parents("tr").removeClass("selected");
				}
			});
		},
		_create: function(){
			$.Widget.prototype._create.apply( this, arguments );
			var $t = this.element;
			if($("tbody", $t).length != 3)
				throw new Error("Current table need at least and only 3 tbodies.");
			$t.removeAttr("loxiaType").addClass("ui-loxia-table");
			
			this._initTable();
		},
		_adjustBodyStyle: function(){
			$("tbody:first tr:odd", this.element).removeClass("even").addClass("odd");
			$("tbody:first tr:even", this.element).removeClass("odd").addClass("even");
		},
		_initActionBar: function(){
			var bar = '', $t = this.element, opstr = $t.attr("operation")||"";			
			if(opstr.indexOf("add") >=0)
				bar += '<button type="button" loxiaType="button" action="add" title="' + loxia.getLocaleMsg("TABLE_BARBTN_ADD") + '">' + loxia.getLocaleMsg("TABLE_BARBTN_ADD") + '</button>';
			if(opstr.indexOf("delete") >=0)
				bar += '<button type="button" loxiaType="button" action="delete" title="' + loxia.getLocaleMsg("TABLE_BARBTN_DELETE") + '">' + loxia.getLocaleMsg("TABLE_BARBTN_DELETE") + '</button>';
			
			var $td = $t.find("tbody:last").find("td:first");
			if($td.get(0))
				$(bar).appendTo($td);
			else
				$t.find("tbody:last").append('<tr class="last"><td colspan="' + this.option("cols") + '">'
					+ bar + '</td></tr>');
			
			var _this = this;
			$t.find("tbody:last button[action='add']").live("click", function(){
				_this.appendRow();
			});
			$t.find("tbody:last button[action='delete']").live("click", function(){
				_this.deleteRow();
			});
		},
		_initRowAction: function(row){
			var $t = this.element;
			var $tr = $(row);
			$("input,select,textarea", $tr).each(function(){
				if($(this).is(":checkbox") || $(this).is(":radio") || $(this).is(":hidden")) return;
				if(loxia.isWidget(this)){
					$(this).bind("valuechanged", function(event, data){
						$t.trigger("rowchanged",[[row,this]]);
					});
				}else if($(this).is("select")){
					$(this).bind("change", function(event,data){
						$t.trigger("rowchanged",[[row,this]]);
					});
				}else{
					$(this).bind("blur", function(event,data){
						$t.trigger("blur",[[row,this]]);
					});
				}
			});
		},
		_calculateRow: function(rows){
			var $rows = rows ? $(rows): $("tbody:first tr", this.element);
			
			var calCols = [], formulas = this.options.formulas;
			for(var i=0; i< this.options.cols; i++)
				if(formulas[i]) calCols.push(i);

			if(calCols.length >0){
				for(var i=0; i< calCols.length; i++){
					var formula = formulas[calCols[i]], decimal = 0;
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
						value = (value != null)? value.toFixed(decimal): "";
						$(this).find("td:eq(" + calCols[i] + ")").text(value);
					});
				}
			}
		},
		_calculateFoot: function(){
			var $t = this.element;
			$t.find("tfoot td[decimal]").each(function(){
				var decimal = parseInt($(this).attr("decimal")), result = 0;
				$t.find("tbody:first tr").find("td:eq(" + $(this).data("col") + ")").each(function(){
					var value = parseFloat(loxia.val($(this).get(0)));
					value = isNaN(value) ? 0 : value;
					result += value == null ? 0 : value;
				})
				$(this).text(result.toFixed(decimal));
			});
			$t.trigger("calculated");
		},
		appendRow: function(){
			var $t = this.element,
				rowIndex = "" + (--_rowIndex),
				row = this.option("template").replace(/\(#\)/ig, "(" + rowIndex + ")");
			$t.find("tbody:first").append(row);
			var $tr = $t.find("tbody:first tr:last");
			loxia.initContext($tr);
			
			this._initRowAction($tr.get(0));
			
			$t.trigger("rowappended", [$tr]);
		},
		deleteRow: function(){
			$(this.element).find("tbody:first tr.selected").remove();
			$(this.element).trigger("rowdeleted");
		}
	};
	
	$.widget("ui.loxiaedittable", loxiaEditTable); 
	$.ui.loxiadate.prototype.options = {};
})(jQuery);