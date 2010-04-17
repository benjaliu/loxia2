(function($) {
	var loxiaInput = $.extend({}, loxia.loxiaWidget, {

		_initInput : function(){},
		_focus : function(){},
		_blur : function(){},
		_create : function(){
			$.Widget.prototype._create.apply( this, arguments );
			
			if(this.element.is("input") || this.element.is("textarea")){
				this._initWidget();
				this.option("trim", (this.element.attr("trim") === "true"));
		
				this._initInput();
				
				this.element.focus(function(){	
					var input = $(this).data($(this).data("loxiaType"));
					if(input.option("disabled") || input.option("readonly")) return;
					$(this).addClass("ui-state-active");
					
					if(input.option("errorMessage")){
						loxia.tip(this,input.option("errorMessage"));
					}
					if(input.option("select"))					
						$(this).select();
					input._focus();					
				});
				
				this.element.blur(function(){
					var input = $(this).data($(this).data("loxiaType"));
					if(input.option("disabled") || input.option("readonly")) return;
					$(this).removeClass("ui-state-active");
					loxia.tip();
				
					var value = $(this).val();
					if(input.option("trim")){
						value = $.trim(value);	
						$(this).val(value);
					}
					input.val(value);
					input._blur();
				});
			}else
				throw new Error("Wrong Dom Type for Input");
		}
	});
	
	$.widget("ui.loxiainput", loxiaInput); 
	$.ui.loxiainput.prototype.options = loxia.defaults;
	
	var loxiaNumber = $.extend({}, loxiaInput, {
		_initInput : function(){
			if(this.option("checkmaster"))
				this.option("checkmaster","checkNumber," + this.option("checkmaster"));
			else
				this.option("checkmaster","checkNumber");
			var _e = this.element;
			$.each(["decimal","min","max"],function(i,v){
				var value = parseInt(_e.attr(v),10);
				if(!isNaN(value)) _e.data("loxianumber").option(v,value);
			});
			_e.css({"text-align":"right","padding-right":"5px"});
		},
	
		_focus : function(){
			this.element.css({"text-align":"left"});
		},
		
		_blur : function(){
			this.element.css({"text-align":"right","padding-right":"5px"});
		}
	});	
	
	$.widget("ui.loxianumber", loxiaNumber); 
	$.ui.loxianumber.prototype.options = loxia.defaults;
	
	var loxiaDate = $.extend({}, loxiaInput, {
		_setOption: function( key, value ) {
			$.Widget.prototype._setOption.apply( this, arguments );				
	
			if(key === "readonly"){
				if(value){
					this.element.attr("readonly","readonly");
					this.element.datepicker("disable");
				}else{
					this.element.removeAttr("readonly");
					this.element.datepicker("enable");
				}
			}else if(key === "disabled"){
				if(value){
					this.element.attr("disabled","disabled");
					this.element.datepicker("disable");
				}else{
					this.element.removeAttr("disabled");
					this.element.datepicker("enable");
				}
			}
		},
		
		_parseDate : function(dateStr){
			if(dateStr){
				if(dateStr === "today")	return new Date();
				try{
					return $.datepicker.parseDate(loxia.dateFormat,dateStr);
				}catch(e){}
			}
			return null;
		},
		
		_initInput : function(){
			if(this.option("checkmaster"))
				this.option("checkmaster","checkDate," + this.option("checkmaster"));
			else
				this.option("checkmaster","checkDate");
			
			var minDate = this._parseDate(this.element.attr("min")),
				maxDate = this._parseDate(this.element.attr("max")),
				dpSettings = {changeYear: true, changeMonth: true, dateFormat: loxia.dateFormat,
					showMonthAfterYear : true,
					minDate : minDate === null ? undefined : minDate,
					maxDate : maxDate === null ? undefined : maxDate,
					onSelect: function(dateText, inst) {
							var _t = $(this).data("loxiadate");
							_t.val(dateText);
						}
				};
			if(minDate != null) this.option("min", minDate);
			if(maxDate != null) this.option("max", maxDate);
			
			this.element.datepicker(dpSettings);
		}
	});
	
	$.widget("ui.loxiadate", loxiaDate); 
	$.ui.loxiadate.prototype.options = loxia.defaults;
})(jQuery);