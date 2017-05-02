/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 *
 * Copyright 2005-2016 The Kuali Foundation
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as
 * published by the Free Software Foundation, either version 3 of the
 * License, or (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 *
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
(function($) {
	$.fn.selectTab = function(options){
		return this.each(function(){
			options = options || {};
			//default setting
			options = $.extend({
				selectPage: ""
			}, options);

			if(options.selectPage){
				var currentTab = $(this).find("a[name='" + options.selectPage + "']");
				if(currentTab){
					currentTab.parent().addClass("ui-state-active");
				}
			}
		});
	}

	$.fn.tabMenu = function(options){
		return this.each(function(){
			options = options || {};
			//default setting
			options = $.extend({
				defaultSelectFirst: true,
				currentPage: ""
			}, options);

			//element id strings
			var id = $(this).parent().attr('id');
			var list_elements = "#" + id + " li";
			var link_elements = list_elements + " a";

			//Styling
			$(this).parent().addClass("ui-tabs tab-navigation-block");
			$(this).addClass("ui-helper-reset ui-helper-clearfix tabMenu");
			$(list_elements).addClass("ui-state-default ui-corner-top");
			if(options.currentPage){
				var currentTab = $(this).find("a[name='" + options.currentPage + "']");
				if(currentTab){
					currentTab.closest("li").addClass("ui-state-active");
				}
			}
			//Handlers and animation
			$(document).ready(function()
			{
					$(link_elements).each(function(i)
					{
						if(i == 0 && options.defaultSelectFirst && !options.currentPage){
							$(this).closest("li").addClass("ui-state-active");
						}
						$(this).focus(
						function()
						{
							$(this).closest("li").addClass("ui-state-focus");
						});

                        $(this).click(
						function()
						{
							$(link_elements).each(function(){$(this).closest("li").removeClass("ui-state-active")});
							$(this).closest("li").addClass("ui-state-active");
						});

						$(this).hover(
						function()
						{
							$(this).closest("li").addClass("ui-state-hover");
						},
						function()
						{
							$(this).closest("li").removeClass("ui-state-hover");
						});



						$(this).blur(
						function()
						{
							$(this).closest("li").removeClass("ui-state-focus");
						});
					});
			});
		});
	}
})(jQuery);