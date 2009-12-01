(function($) {
	
	var loxiaInput = $.extend({}, loxia.loxiaWidget, {
		getBaseClass : function(){
			return "loxiainput";
		},
		_initInput : function(){
			this._setData("checkmaster",this.element.attr("checkmaster") || "");
			
			this.element.focus(function(){	
				var _t = $(this).data("loxiainput");
				var tooltip = $(this).data("loxiatooltip");
				$(this).addClass("ui-state-active");
				
				if(_t._getData("errorMessage")){
					tooltip.show(_t._getData("errorMessage"));
				}
				if(_t._getData("select"))					
					$(this).select();
			});
			
			this.element.blur(function(){
				var _t = $(this).data("loxiainput");
				var tooltip = $(this).data("loxiatooltip");
				$(this).removeClass("ui-state-active");
				tooltip.hide();
			
				var value = $(this).val();
				if($(this).attr("trim") == "true"){
					value = $.trim(value);	
					$(this).val(value);
				}
				_t.val(value);
			});
		},
		_init : function(){
			if(this.element.is("input") || this.element.is("textarea")){
				this.element.removeAttr("loxiaType");
				var baseClass = this.getBaseClass();
				this._setData("baseClass", baseClass);
				this.element.data("baseClass",baseClass);
				this.element.addClass("loxia " + "ui-" + baseClass + " ui-state-default ui-corner-all");
				
				if(this.element.attr("required") == "true"){
					this._setData("required", true);
					this.element.addClass("ui-state-mandatory");
				}
				
				if(this.element.attr("selectonfocus") == "true"){
					this._setData("select", true);
				}
								
				if(this.element.val())
					this._setData("lastRightValue", this.element.val());
				
				this.element.loxiatooltip();
				
				this._initInput();
			}else
				throw new exception("Wrong Dom Type for Input");
		}
	});
	
	$.widget("ui.loxiainput", loxiaInput); 
	$.ui.loxiainput.getter = loxia.loxiaGetter; 
	$.ui.loxiainput.defaults = loxia.defaults;
	
	var loxiaNumber = $.extend({}, loxiaInput, {
		getBaseClass : function(){
			return "loxianumber";
		},
		_initInput : function(){
			var checkmaster = "checkNumber";
			if(this.element.attr("checkmaster")) 
				checkmaster += ("," + this.element.attr("checkmaster"));
			this._setData("checkmaster",checkmaster);
			
			var attrs = ["decimal","min","max"];
			for(var attr,i=0; attr = attrs[i]; i++){
				if(this.element.attr(attr)){
					var val = parseInt(this.element.attr(attr),10);
					if(!isNaN(val))
						this._setData(attr,val);
				}
			}
			
			this.element.focus(function(){	
				var _t = $(this).data("loxianumber");
				var tooltip = $(this).data("loxiatooltip");
				$(this).addClass("ui-state-active");
				if(_t._getData("errorMessage")){
					tooltip.show(_t._getData("errorMessage"));
				}
				if(_t._getData("select"))
					$(this).select();
			});
			
			this.element.blur(function(){
				var _t = $(this).data("loxianumber");
				var tooltip = $(this).data("loxiatooltip");
				$(this).removeClass("ui-state-active");
				tooltip.hide();
			
				var value = $(this).val();
				if($(this).attr("trim") == "true"){
					value = $.trim(value);	
					$(this).val(value);
				}
				_t.val(value);
			});
			
		}
	});
	
	$.widget("ui.loxianumber", loxiaNumber); 
	$.ui.loxianumber.getter = loxia.loxiaGetter; 
	$.ui.loxianumber.defaults = loxia.defaults;
	
	var loxiaDate = $.extend({}, loxiaInput, {
		getBaseClass : function(){
			return "loxiadate";
		},
		_initInput : function(){
			var checkmaster = "checkDate";
			if(this.element.attr("checkmaster")) 
				checkmaster += ("," + this.element.attr("checkmaster"));
			this._setData("checkmaster",checkmaster);		
			
			this.element.data("dropdown",true);
			this.element.datepicker({changeYear: true, changeMonth: true, dateFormat: loxia.dateFormat,
				onSelect: function(dateText, inst) {
					var _t = $(this).data("loxiadate");
					_t.val(dateText);
				}
			});
			
			var dpInstConfig = $.datepicker._getFormatConfig($.datepicker._getInst(this.element.get(0)));
			if(dpInstConfig){
				var minDate,maxDate;
				if(this.element.attr("min")){
					try{
						if(this.element.attr("min") == "today"){
							minDate = new Date();
						}else
							minDate = $.datepicker.parseDate(loxia.dateFormat,this.element.attr("min"),dpInstConfig);
						this._setData("min",minDate);
						this.element.datepicker("option","minDate",minDate);
						//$.datepicker._optionDatepicker(this.element.get(0),"minDate",minDate);
					}catch(e){}				
				}
				if(this.element.attr("max")){
					try{
						if(this.element.attr("max") == "today"){
							maxDate = new Date();
						}else
							maxDate = $.datepicker.parseDate(loxia.dateFormat,this.element.attr("max"),dpInstConfig);
						this._setData("max",maxDate);
						this.element.datepicker("option","maxDate",maxDate);
						//$.datepicker._optionDatepicker(this.element.get(0),"maxDate",maxDate);
					}catch(e){}				
				}
			}
			
			this.element.focus(function(){	
				var _t = $(this).data("loxiadate");
				var tooltip = $(this).data("loxiatooltip");
				$(this).addClass("ui-state-active");
				
				if(_t._getData("errorMessage")){
					tooltip.show(_t._getData("errorMessage"));
				}
				if(_t._getData("select"))					
					$(this).select();
			});
			
			this.element.blur(function(){
				var _t = $(this).data("loxiadate");
				var tooltip = $(this).data("loxiatooltip");
				$(this).removeClass("ui-state-active");
				tooltip.hide();
			
				var value = $(this).val();
				if($(this).attr("trim") == "true"){
					value = $.trim(value);	
					$(this).val(value);
				}
				_t.val(value);
			});
		}
	});
	
	$.widget("ui.loxiadate", loxiaDate); 
	$.ui.loxiadate.getter = loxia.loxiaGetter; 
	$.ui.loxiadate.defaults = loxia.defaults;
})(jQuery);