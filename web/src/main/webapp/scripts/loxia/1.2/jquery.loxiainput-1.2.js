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
				
				var _this = this;
				this.element.focus(function(){	
					if(_this.option("disabled") || _this.option("readonly")) return;
					$(this).addClass("ui-loxia-active");
					
					if(_this.option("errorMessage")){
						loxia.tip(this,_this.option("errorMessage"));
					}
					if(_this.option("select"))					
						$(this).select();
					_this._focus();					
				});
				
				this.element.blur(function(){
					if(_this.option("disabled") || _this.option("readonly")) return;
					$(this).removeClass("ui-loxia-active");
					loxia.tip();
				
					var value = $(this).val();
					if(_this.option("trim")){
						value = $.trim(value);	
						$(this).val(value);
					}
					_this.val(value);
					_this._blur();
				});
			}else
				throw new Error("Wrong Dom Type for Input");
		}
	});
	
	$.widget("ui.loxiainput", loxiaInput); 
	$.ui.loxiainput.prototype.options = loxia.defaults;
	
	var loxiaNumber = $.extend({}, loxiaInput, {	
		decimal: function(value){
			if(value == undefined) return this.option("decimal");
			value = "0"||value;
			value = parseInt(value,10);
			if(isNaN(value)) throw new Error("decimal is not a valid number");
			this.option("decimal", value);
			this.state(null);
		},
	
		min: function(value){
			if(value == undefined) return this.option("min");
			if(value != undefined && value != null){
				value = loxia.isString(value)?parseFloat(value):value;
				if(isNaN(value)) throw new Error("min is not a valid number");
				this.option("min", value);
				this.state(null);				
			}
		},
		
		max: function(value){
			if(value == undefined) return this.option("max");
			if(value != undefined && value != null){
				value = loxia.isString(value)?parseFloat(value):value;
				if(isNaN(value)) throw new Error("max is not a valid number");
				this.option("max", value);
				this.state(null);				
			}
		},
		
		_initInput : function(){
			this.element.addClass("ui-loxia-number");
			if(this.option("checkmaster"))
				this.option("checkmaster","checkLoxiaNumber," + this.option("checkmaster"));
			else
				this.option("checkmaster","checkLoxiaNumber");
			var _e = this.element;
			
			var dv = parseInt(_e.attr("decimal"),10), minv = parseFloat(_e.attr("min")) ,maxv = parseFloat(_e.attr("max"));
			
			if(!isNaN(dv)) this.option("decimal",dv);
			if(!isNaN(minv)) this.option("min",minv);
			if(!isNaN(maxv)) this.option("max",maxv);
		},
	
		_focus : function(){
			this.element.addClass("ui-loxia-number-active");
		},
		
		_blur : function(){
			this.element.removeClass("ui-loxia-number-active");
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
		
		min: function(value){
			if(value == undefined) return this.option("min");
			if(value != undefined && value != null){
				value = loxia.isString(value)? this._parseDate(value):value;
				if(!value) throw new Error("min is not a valid date");
				this.option("min", value);
				this.element.datepicker("option","minDate",value);
				this.state(null);				
			}
		},
		
		max: function(value){
			if(value == undefined) return this.option("max");
			if(value != undefined && value != null){
				value = loxia.isString(value)? this._parseDate(value):value;
				if(!value) throw new Error("max is not a valid date");
				this.option("max", value);
				this.element.datepicker("option","maxDate",value);
				this.state(null);				
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
		
		parseDate : function(dateStr){
			return this._parseDate(dateStr);
		},
		
		_initInput : function(){
			if(this.option("checkmaster"))
				this.option("checkmaster","checkLoxiaDate," + this.option("checkmaster"));
			else
				this.option("checkmaster","checkLoxiaDate");
			
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