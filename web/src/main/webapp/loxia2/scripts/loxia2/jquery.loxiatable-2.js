(function($){
    var _rowIndex = 0;
    loxia.regional['']['TABLE_BARBTN_ADD'] = "Add New Row";
    loxia.regional['']['TABLE_BARBTN_DELETE'] = "Remove Selected Row";

    loxia.regional['']['LABEL_OK'] = "Ok";
    loxia.regional['']['LABEL_CANCEL'] = "Cancel";
    loxia.regional['']['LABEL_REFRESH'] = "Refresh";
    loxia.regional['']['LABEL_PAGE_FIRST'] = "First";
    loxia.regional['']['LABEL_PAGE_LAST'] = "Last";
    loxia.regional['']['LABEL_PAGE_PREV'] = "Prev";
    loxia.regional['']['LABEL_PAGE_NEXT'] = "Next";
    loxia.regional['']['LABEL_PAGE_GOTO'] = "GO";
    loxia.regional['']['LABEL_PAGE_INFO'] = "Total pages: {0} / Records: {1}";

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

            $('<div class="tbl-action-bar">' + bar + '</div>').prependTo("tfoot .col-0");

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
            var _this = this;
            if(this.element.attr("dataurl"))
                this.option("dataurl",this.element.attr("dataurl"));

            this.element.on("click", "thead input[type='checkbox']", function(){
                var $th = $(this).parent("th"), idx = $th.parent("tr").children("th").index($th),
                    c = $(this).is(":checked");

                $("tbody", _this.element).find("tr").each(function(){
                    if(idx == 0){
                        if(c){
                            $(this).addClass("selected");
                        }else{
                            $(this).removeClass("selected");
                        }
                    }
                    $(this).find("td.col-" + idx +" input[type='checkbox']").prop("checked",c);
                });

            });
            this.element.on("click", "tbody td.col-0 input[type='checkbox']", function(){
                if($(this).is(":checked")){
                    $(this).parentsUntil("tr").parent().addClass("selected");
                }else{
                    $(this).parentsUntil("tr").parent().removeClass("selected");
                }
            });
            this.element.on("click", "tbody td.col-0 input[type='radio']", function(){
                $(this).parentsUntil("tbody").parent().children("tr.selected").removeClass("selected");
                if($(this).is(":checked")){
                    $(this).parentsUntil("tr").parent().addClass("selected");
                }else{
                    $(this).parentsUntil("tr").parent().removeClass("selected");
                }
            });
            this.element.on("click", ".ui-loxia-nav .refresh", function(){
                _this.refresh();
            });
            this.element.on("click", ".ui-loxia-nav .home", function(){
                _this.option("currentPage", 1);
                _this.refresh();
                $(_this.element).trigger("gotopage",[this]);
            });
            this.element.on("click", ".ui-loxia-nav .prev", function(){
                var p = _this.option("currentPage");
                _this.option("currentPage", (p-1>0?p-1:1));
                _this.refresh();
                $(_this.element).trigger("gotopage",[this]);
            });
            this.element.on("click", ".ui-loxia-nav .next", function(){
                var p = _this.option("currentPage"), t = _this.option("totalPages");
                _this.option("currentPage", (p+1>t?t:p+1));
                _this.refresh();
                $(_this.element).trigger("gotopage",[this]);
            });
            this.element.on("click", ".ui-loxia-nav .end", function(){
                _this.option("currentPage", _this.option("totalPages"));
                _this.refresh();
                $(_this.element).trigger("gotopage",[this]);
            });
            this.element.on("change", ".ui-loxia-nav select.sortsel", function(){
                _this.option("sortStr", _this.option("sorts")[parseInt($(this).val())].sortStr);
                _this.refresh();
                $(_this.element).trigger("sortchanged",[this]);
            });
            this.element.on("keypress", ".ui-loxia-nav .goto", function(evt){
                if(evt && evt.keyCode == 13){
                    var n = parseInt($(this).text(),10),
                        t = _this.option("totalPages");
                    if(isNaN(n) || n < 1 || n > t){
                        //do nothing
                    }else{
                        _this.option("currentPage", n);
                        _this.refresh();
                        $(_this.element).trigger("gotopage",[this]);
                    }
                }
            });
            this.element.on("click", "th", function(){
                var idx = $(this).parent("tr").children("th").index(this),
                    s = _this.option("cols")[idx].sort;
                if(s){
                    if($(this).hasClass("sort-asc")){
                        _this.option("sortStr", loxia.isString(s)?(s + " desc"):s[1]);
                    }else{
                        _this.option("sortStr", loxia.isString(s)?(s + " asc"):s[0]);
                    }
                    _this.refresh();
                    $(_this.element).trigger("sortchanged",[this]);
                }
            });
        },
        refresh: function(url,method){
            var cols = this.option("cols");
            var dataurl = url||this.option("dataurl");
            var httpmethod = method||this.option("httpmethod");
            if(cols.length == 0 || dataurl == undefined
                || dataurl == null || dataurl.length == 0)
                this.element.html();
            else{
                var _d = {};
                if(loxia.isString(dataurl)){
                    var data = {};
                    if(this.option("page")){
                        data = $.extend(data,{
                            page:{
                                startPage:this.option("currentPage"),
                                size:this.option("size")
                            },
                            sortStr: this.option("sortStr")
                        });
                    }
                    _d = loxia.syncXhr(dataurl, data,{type: httpmethod||"GET"});
                }else{
                    _d = dataurl;
                }
                this._resetTable(_d);
            }
            $(this.element).trigger("reload",[this]);
        },

        _resetTable: function(data){
            if(data == null || data.length ==0){
                this.element.html();
                return;
            }

            var cols = this.option("cols");
            var t = "<table cellspacing='0' cellpadding='0'>";
            var thead = "<thead><tr>";
            for(var i=0; i< cols.length; i++){
                thead += "<th class='col-" + i + "'" + (cols[i].width?(" width='" + cols[i].width + "'"):"") +
                    ">" + (cols[i].label||"&nbsp;") + "<span></span></th>";
            }
            thead += "</tr></thead>";
            var tbody = "<tbody>";

            this.option("count", data.count);
            this.option("sortStr", data.sortStr);

            if(this.option("page")){
                this.option("currentPage", data.currentPage);
                this.option("totalPages", data.totalPages);
                this.option("size", data.size);
                this.option("firstPage", data.firstPage);
                this.option("lastPage", data.lastPage);

            }
            var nav = this._drawNavigator();
            if(this.option("page")){
                for(var i=0; i< data.items.length; i++)
                    tbody += this._drawLine(data.items[i], i);
                tbody += "</tbody>";
                t += thead + tbody + "</table>";

            }else{
                if(data.length){
                    for(var i=0; i< data.length; i++)
                        tbody += this._drawLine(data[i], i);
                }else{
                    tbody += this._drawLine(data, 0);
                }
                tbody += "</tbody>";
                t += thead + tbody + "</table>";
            }

            if(nav){
                var np = this.option("navpos");
                t = ((np==1||np==3)?nav:"") + t + ((np==2||np==3)?nav:"");
            }

            this.element.html(t);
            this._setNavigatorStatus();
        },

        _drawNavigator: function(){
            if((!this.option("page"))&&(this.option("sorts").length == 0)) return null;
            var t = "<div class='ui-loxia-nav'>";
            if(this.option("nav")){
                t += this.option("nav")(data,this);
            }else{
                t += "<a href='javascript:void(0);' class='refresh' title='" + loxia.getLocaleMsg("LABEL_REFRESH") + "'></a>";
                var sorts = this.option("sorts");
                if(sorts.length >0){
                    t += "<select class='sortsel'>";
                    for(var i=0; i< sorts.length; i++){
                        t += "<option value='" + i + "'>" + sorts[i].label + "</option>";
                    }
                    t += "</select>";
                }

                t += "<div class='nav-pager'>";
                t += "<a href='javascript:void(0);' class='home' title='" + loxia.getLocaleMsg("LABEL_PAGE_FIRST") + "'> << </a>";
                t += "<a href='javascript:void(0);' class='prev' title='" + loxia.getLocaleMsg("LABEL_PAGE_PREV") + "'> < </a>";
                t += "<input type='text' class='goto' value='" + this.option("currentPage") + "'/>";
                t += "<a href='javascript:void(0);' class='next' title='" + loxia.getLocaleMsg("LABEL_PAGE_NEXT") + "'> > </a>";
                t += "<a href='javascript:void(0);' class='end' title='" + loxia.getLocaleMsg("LABEL_PAGE_LAST") + "'> >> </a>";
                t += "<p>" + loxia.getLocaleMsg("LABEL_PAGE_INFO",[this.option("totalPages"),this.option("count")]) + "</p>";
                t += "</div>";
            }
            t += "</div>";
            return t;
        },

        _setNavigatorStatus: function(){
            var $sortsel = this.element.find(".ui-loxia-nav select.sortsel"),
                sortStr = this.option("sortStr");
            if($sortsel.length > 0){
                var idx = -1;
                for(var i=0; i< this.option("sorts").length; i++){
                    if(this.option("sorts")[i].sortStr == sortStr){
                        idx = i; break;
                    }
                }
                if(idx >=0){
                    $sortsel.val(""+idx);
                }else{
                    $sortsel.prepend("<option value=''>--------</option>").val("");
                }
            }

            var f = this.option("firstPage"), l = this.option("lastPage");
            $(".ui-loxia-nav a",this.element).removeClass("disabled");
            if(f){
                $(".ui-loxia-nav .home, .ui-loxia-nav .prev",this.element).addClass("disabled");
            }else{
                $(".ui-loxia-nav .end, .ui-loxia-nav .next",this.element).addClass("disabled");
            }

            if(sortStr && this.option("sorts").length ==0){
                //set sort on column
                var col = -1;
                for(var i=0; i< this.option("cols").length; i++){
                    var s = this.option("cols")[i].sort;
                    if(s){
                        var c = "";
                        if(loxia.isString(s)){
                            if(sortStr == s + " asc") {col = i; c="sort-asc";}
                            else if(sortStr == s + " desc") {col = i; c="sort-desc";}
                        }else{
                            if(sortStr == s[0]) {col = i; c="sort-asc";}
                            else if(sortStr == s[1]) {col = i; c="sort-desc";}
                        }
                        if(c) break;
                    }
                }
                if(col > 0){
                    //$("th", this.element).removeClass("sort-asc sort-desc");
                    $("th.col-" + col, this.element).addClass(c);
                }
            }
        },

        _drawLine: function(data, idx){
            var cols = this.option("cols");
            var tr = "<tr class='" + (idx%2==0?"odd":"even") + "'>";
            for(var i=0; i< cols.length; i++){
                tr += "<td class='col-" + i +" " + (cols[i].align? cols[i].align :"")+ "'" +
                    ">" + this._getData(data,cols[i],idx) + "</td>";
            }
            tr += "</tr>";
            return tr;
        },

        _getData: function(data, args,idx){
            if(args.template){
                return loxia.hitch(args.template)(data, args, idx);
            }
            if(args.name == undefined) return "";
            if(args.formatter)
                return loxia.hitch(args.formatter)(loxia.getObject(args.name,data),args.formatarg);
            else
                return loxia.getObject(args.name,data)||"";
        }

    };

    $.widget("ui.loxiasimpletable", loxiaSimpleTable);
    $.ui.loxiasimpletable.prototype.options = {page: false, navpos: 3,
        currentPage: 1, totalPages: 1, count: 1, size: 20, sorts: []};
})(jQuery);