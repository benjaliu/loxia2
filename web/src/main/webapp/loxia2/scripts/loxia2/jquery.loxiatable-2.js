(function($){
	var _rowIndex = 0;
	loxia.regional['']['TABLE_BARBTN_ADD'] = "Add New Row";
	loxia.regional['']['TABLE_BARBTN_DELETE'] = "Remove Selected Row";
	var loxiaEditTable = {
		_formatName : function(str){
			return str.replace(/\./ig,"_");
		},
		
		_initTable: function(){
			var $t = this.element.find("table");
			$('<div class="tbl-container"></div>').prependTo(this.element);
			$("div.tbl-container", this.element).append($t);
			$t.attr("cellpadding",0).attr("cellspacing",0);
			var formulas = [];
			var cols = $t.find("thead tr:last").find("th").each(function(i){
				$(this).html("<div class='col-" + i + "'>" + $(this).html() + "</div>");
				$t.find("tbody tr").find("td:eq(" + i + ")").addClass("col-" + i);
				formulas.push($(this).attr("formula")||"");
				
				$(this).hover(function(){
					$(this).addClass("ui-state-hover");
				},function(){
					$(this).removeClass("ui-state-hover");
				});
			}).length;
			
			$("tbody", $t).find("td.col-0:has(input[type='checkbox'])").css({"text-align":"center"});
								
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
			
			//$t.find("tbody tr:last").addClass("last");
			//$t.find("tfoot tr:last").addClass("last");
			
			$("tbody:first .col-0 input:checked", $t).each(function(){
				$(this).parentsUntil("tr").parent().addClass("ui-state-highlight");
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
			
			$("thead th",$t).addClass("ui-state-default");
			$("thead tr", $t).find("th:eq(0) input[type='checkbox']").bind("click", function(){				
				if($(this).is(":checked")){
					$("tbody:first .col-0 input:not(:checked)", $t).each(function(){
						$(this).attr("checked", true);
						$(this).parentsUntil("tr").parent().addClass("ui-state-highlight");
					});
				}else{
					$("tbody:first .col-0 input:checked", $t).each(function(){
						$(this).attr("checked", false);
						$(this).parentsUntil("tr").parent().removeClass("ui-state-highlight");
					});
				}
			});
			//line selector
            $("tbody:first", $t).on("click", "td.col-0 input[type='checkbox']", function(){
                if($(this).is(":checked")){
                    $(this).parentsUntil("tr").parent().addClass("ui-state-highlight");
                }else{
                    $(this).parentsUntil("tr").parent().removeClass("ui-state-highlight");
                }
            });
            $("tbody:first", $t).on("mouseover mouseout", "tr", function(event) {
                if (event.type == 'mouseover') {
                    $(this).addClass("ui-state-hover");
                } else {
                    $(this).removeClass("ui-state-hover");
                }
            });
		},
		_create: function(){
			$.Widget.prototype._create.apply( this, arguments );
			var $t = this.element;
			if($("tbody", $t).length != 2)
				throw new Error("Current table need at least and only 2 tbodies.");
			$t.removeAttr("loxiaType").addClass("ui-loxia-table").addClass("ui-corner-all");
			
			this._initTable();
		},
		_adjustBodyStyle: function(){
			var $t = this.element.find("table");
			$("tbody:first tr:odd", $t).removeClass("even").addClass("odd");
			$("tbody:first tr:even", $t).removeClass("odd").addClass("even");
		},
		_initActionBar: function(){
			var bar = '', $t = this.element.find("table"), opstr = $t.attr("operation")||"";			
			if(opstr.indexOf("add") >=0)
				bar += '<button type="button" icons="ui-icon-plusthick" loxiaType="button" action="add" title="' + loxia.getLocaleMsg("TABLE_BARBTN_ADD") + '">' + loxia.getLocaleMsg("TABLE_BARBTN_ADD") + '</button>';
			if(opstr.indexOf("delete") >=0)
				bar += '<button type="button" icons="ui-icon-minusthick" loxiaType="button" action="delete" title="' + loxia.getLocaleMsg("TABLE_BARBTN_DELETE") + '">' + loxia.getLocaleMsg("TABLE_BARBTN_DELETE") + '</button>';
			
			$('<div class="tbl-action-bar">' + bar + '</div>').prependTo(this.element);
			
			var _this = this;
			var $actionbar = this.element.find("div.tbl-action-bar");
			loxia.initContext($actionbar[0]);
            $actionbar.on("click", "button[action='add']", function(){_this.appendRow();});
            $actionbar.on("click", "button[action='delete']", function(){_this.deleteRow();});
		},
		_initRowAction: function(row){
			var $t = this.element.find("table");
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
						$t.trigger("rowchanged",[[row,this]]);
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
			var $t = this.element.find("table");
			$t.find("tfoot td[decimal]").each(function(){
				var decimal = parseInt($(this).attr("decimal")), result = 0;
				$t.find("tbody:first tr").find("td:eq(" + $(this).data("col") + ")").each(function(){
					var value = parseFloat(loxia.val($(this).get(0)));
					value = isNaN(value) ? 0 : value;
					result += value == null ? 0 : value;
				})
				$(this).text(result.toFixed(decimal));
			});
			$t.trigger("calculated",[this]);
		},
		appendRow: function(){
			var $t = this.element.find("table"),
				rowIndex = "" + (--_rowIndex),
				row = this.option("template").replace(/\(#\)/ig, "(" + rowIndex + ")");
			$t.find("tbody:first").append(row);
			var $tr = $t.find("tbody:first tr:last");
			loxia.initContext($tr);
			var index = $tr.parents("tbody").find("tr").index($tr[0]);
			$tr.addClass(index%2 == 0 ? "even":"odd");
			this._initRowAction($tr.get(0));
			
			
			$t.trigger("rowappended", [[$tr[0],this]]);
		},
		deleteRow: function(){
			var $t = this.element.find("table")
			$t.find("tbody:first tr.ui-state-highlight").remove();
			$("thead tr", $t).find("th:eq(0) input[type='checkbox']").attr("checked",false);
			this._adjustBodyStyle();
			$t.trigger("rowdeleted",[this]);
		}
	};
	
	$.widget("ui.loxiaedittable", loxiaEditTable); 
	$.ui.loxiadate.prototype.options = {};

    var loxiaSimpleTable = {
        _create: function(){
            $.Widget.prototype._create.apply( this, arguments );
            this.element.addClass("ui-loxia-simple-table");
            if(this.element.attr("dataurl"))
                this.option("dataurl",this.element.attr("dataurl"));
        },
        refresh: function(url,data,method){
            var cols = this.option("cols");
            var dataurl = url||this.option("dataurl");
            if(cols.length == 0 || dataurl == undefined
                || dataurl == null || dataurl.length == 0)
                this.element.html();
            else{
                var _d = {};
                if(loxia.isString(dataurl)){
                    _d = loxia.syncXhr(dataurl, data,{type: method||"GET"});
                }else{
                    _d = url;
                }
                this._resetTable(_d);
            }
        },

        _resetTable: function(data){
            if(data == null || data.length ==0){
                this.element.html();
                return;
            }

            var cols = this.option("cols");
            var t = "<table>";
            var thead = "<thead><tr>";
            for(var i=0; i< cols.length; i++){
                thead += "<th" + (cols[i].width?(" width='" + cols[i].width + "'"):"") +
                    ">" + cols[i].label||"&nbsp;" + "</th>"
            }
            thead += "</tr></thead>";
            var tbody = "<tbody>";
            if(data.length){
                for(var i=0; i< data.length; i++)
                    tbody += this._drawLine(data[i], i);
            }else{
                tbody += this._drawLine(data, 0);
            }
            tbody += "</tbody>";
            t += thead + tbody + "</table>";
            this.element.html(t);
        },

        _drawLine: function(data, idx){
            var cols = this.option("cols");
            var tr = "<tr class='" + (idx%2==0?"odd":"even") + "'>";
            for(var i=0; i< cols.length; i++){
                tr += "<td" +
                    (cols[i].align?" class='" + cols[i].align + "'":"") +
                    ">" + this._getData(data,cols[i]) + "</td>";
            }
            tr += "</tr>";
            return tr;
        },

        _getData: function(data, args){
            if(args.name == undefined) return "";
            return data[args.name]||"";
        }

    };

    $.widget("ui.loxiasimpletable", loxiaSimpleTable);
    $.ui.loxiasimpletable.prototype.options = {};
})(jQuery);