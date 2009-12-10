(function($) {
	var _global = this;
	var loxiaDialog = $.extend({},$.ui.dialog.prototype,{
		_createButtons: function(buttons) {
			var self = this,
				hasButtons = false,
				uiDialogButtonPane = $('<div></div>')
					.addClass(
						'ui-dialog-buttonpane ' +
						'ui-widget-content ' +
						'ui-helper-clearfix'
					);
	
			// if we already have a button pane, remove it
			this.uiDialog.find('.ui-dialog-buttonpane').remove();
	
			var buttonList = "";
			var funcs = {};
			for(var i=0,b; b=buttons[i];i++){
				if(loxia.isString(b)) buttonList += b;
				else{
					var buttonType = b.buttonType || "button";
					buttonList += '<button loxiaType="button" buttonType="' + buttonType + '"';
					if(b.id)
						buttonList += ' id = "' + b.id +'"';
					if(b.target)
						buttonList += ' target="'+ b.target +'"';
					if(b.href)
						buttonList += ' href="'+ b.href +'"';
					if(b.disabled)
						buttonList += ' disabled="disabled"';
					if(b.func)
						funcs["" + i] = loxia.isString(b.func) ? _global[b.func] : b.func;
					buttonList += '>' + b.value + '</button>';
				}
			}
			uiDialogButtonPane.append(buttonList);
			uiDialogButtonPane.appendTo(this.uiDialog);
			loxia.initContext(uiDialogButtonPane.get(0));
			for(var k in funcs){
				uiDialogButtonPane.find("button:eq(" + k + ")").bind("click", funcs[k]);
			}
		}
	});
	
	$.widget("ui.loxiadialog", loxiaDialog); 
		
	/* Extended from ui dialog 1.7.2*/
	$.extend($.ui.loxiadialog, {
		version: "1.1",
		defaults: {
			autoOpen: true,
			bgiframe: false,
			buttons: [],
			closeOnEscape: true,
			closeText: 'close',
			dialogClass: '',
			draggable: true,
			hide: null,
			height: 'auto',
			maxHeight: false,
			maxWidth: false,
			minHeight: 150,
			minWidth: 150,
			modal: false,
			position: 'center',
			resizable: true,
			show: null,
			stack: true,
			title: '',
			width: 300,
			zIndex: 1000
		},

		getter: 'isOpen',

		uuid: 0,
		maxZ: 0,

		getTitleId: function($el) {
			return 'ui-dialog-title-' + ($el.attr('id') || ++this.uuid);
		},

		overlay: function(dialog) {
			this.$el = $.ui.loxiadialog.overlay.create(dialog);
		}
	});

	$.extend($.ui.loxiadialog.overlay, {
		instances: [],
		maxZ: 0,
		events: $.map('focus,mousedown,mouseup,keydown,keypress,click'.split(','),
			function(event) { return event + '.dialog-overlay'; }).join(' '),
		create: function(dialog) {
			if (this.instances.length === 0) {
				// prevent use of anchors and inputs
				// we use a setTimeout in case the overlay is created from an
				// event that we're going to be cancelling (see #2804)
				setTimeout(function() {
					// handle $(el).dialog().dialog('close') (see #4065)
					if ($.ui.dialog.overlay.instances.length) {
						$(document).bind($.ui.dialog.overlay.events, function(event) {
							var dialogZ = $(event.target).parents('.ui-dialog').css('zIndex') || 0;
							return (dialogZ > $.ui.dialog.overlay.maxZ);
						});
					}
				}, 1);

				// allow closing by pressing the escape key
				$(document).bind('keydown.dialog-overlay', function(event) {
					(dialog.options.closeOnEscape && event.keyCode
							&& event.keyCode == $.ui.keyCode.ESCAPE && dialog.close(event));
				});

				// handle window resize
				$(window).bind('resize.dialog-overlay', $.ui.dialog.overlay.resize);
			}

			var $el = $('<div></div>').appendTo(document.body)
				.addClass('ui-widget-overlay').css({
					width: this.width(),
					height: this.height()
				});

			(dialog.options.bgiframe && $.fn.bgiframe && $el.bgiframe());

			this.instances.push($el);
			return $el;
		},

		destroy: function($el) {
			this.instances.splice($.inArray(this.instances, $el), 1);

			if (this.instances.length === 0) {
				$([document, window]).unbind('.dialog-overlay');
			}

			$el.remove();

			// adjust the maxZ to allow other modal dialogs to continue to work (see #4309)
			var maxZ = 0;
			$.each(this.instances, function() {
				maxZ = Math.max(maxZ, this.css('z-index'));
			});
			this.maxZ = maxZ;
		},

		height: function() {
			// handle IE 6
			if ($.browser.msie && $.browser.version < 7) {
				var scrollHeight = Math.max(
					document.documentElement.scrollHeight,
					document.body.scrollHeight
				);
				var offsetHeight = Math.max(
					document.documentElement.offsetHeight,
					document.body.offsetHeight
				);

				if (scrollHeight < offsetHeight) {
					return $(window).height() + 'px';
				} else {
					return scrollHeight + 'px';
				}
			// handle "good" browsers
			} else {
				return $(document).height() + 'px';
			}
		},

		width: function() {
			// handle IE 6
			if ($.browser.msie && $.browser.version < 7) {
				var scrollWidth = Math.max(
					document.documentElement.scrollWidth,
					document.body.scrollWidth
				);
				var offsetWidth = Math.max(
					document.documentElement.offsetWidth,
					document.body.offsetWidth
				);

				if (scrollWidth < offsetWidth) {
					return $(window).width() + 'px';
				} else {
					return scrollWidth + 'px';
				}
			// handle "good" browsers
			} else {
				return $(document).width() + 'px';
			}
		},

		resize: function() {
			/* If the dialog is draggable and the user drags it past the
			 * right edge of the window, the document becomes wider so we
			 * need to stretch the overlay. If the user then drags the
			 * dialog back to the left, the document will become narrower,
			 * so we need to shrink the overlay to the appropriate size.
			 * This is handled by shrinking the overlay before setting it
			 * to the full document size.
			 */
			var $overlays = $([]);
			$.each($.ui.dialog.overlay.instances, function() {
				$overlays = $overlays.add(this);
			});

			$overlays.css({
				width: 0,
				height: 0
			}).css({
				width: $.ui.dialog.overlay.width(),
				height: $.ui.dialog.overlay.height()
			});
		}
	});

	$.extend($.ui.loxiadialog.overlay.prototype, {
		destroy: function() {
			$.ui.dialog.overlay.destroy(this.$el);
		}
	});	
})(jQuery);