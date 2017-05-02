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
	$.fn.selectMenuItem = function(options){
		return this.each(function(){
			options = options || {};
			//default setting
			options = $.extend({
				selectPage: ""
			}, options);

			if(options.selectPage){
				var current = $(this).find("a[name='" + options.selectPage + "']");
				if(current){
					current.addClass("current");
				}
			}
		});
	}

	$.fn.navMenu = function(options){
		return this.each(function(){
			options = options || {};
			//default setting
			options = $.extend({
				parent_div: "viewlayout_div",
				nav_div: "viewnavigation_div",
				defaultSelectFirst: true,
				currentPage: "",
				animate: false,
				slideout: true,
				pad_out: 25,
				pad_in: 18
			}, options);

			//element id strings
			var id = $(this).parent().attr('id');
			var list_elements = "#" + id + " li";
			var link_elements = list_elements + " a";

			//Styling
			$(this).parent().addClass("navigation-block");
			$("#" + options.parent_div).addClass("navigation-parent-div");
			$("#" + options.nav_div).addClass("navigation-div");
			if (options.animate) {
				//Animated menu
				$("li", this).addClass("animated-element");
				$(this).addClass("animated-navigation");
			}
			else{
				//Plain menu
				$("li", this).addClass("basic-element");
				$(this).addClass("basic-navigation");
			}

			if(options.slideout){
				$(this).before("<a id='collapseLink' class='collapseLink' alt='Close Navigation'>Collapse Navigation</a>");
				$(".navigation-block").after("<a id='controlbtn' class='slideLink' alt='Close Navigation'><<</a>");
			}

			if(options.defaultSelectFirst && !options.currentPage){
				$(link_elements).first().addClass("current");
			}

			if(options.currentPage){
				var current = $(this).find("a[name='" + options.currentPage + "']");
				if(current){
					current.addClass("current");
				}
			}

			//Handlers and animation
			$(document).ready(function()
			{
				if(options.animate){

					$(link_elements).each(function(i)
					{
						$(this).click(
						function()
						{
						$("li.animated-element a").removeClass("current");
						$(this).addClass("current");
						});

						/*$(this).hover(
						function()
						{
							if (!$(this).is(':animated')) {
								$(this).animate({
									paddingLeft: options.pad_out
								}, 150);
							}
						},
						function()
						{
								$(this).animate({
									paddingLeft: options.pad_in
								}, 150);
						});

						$(this).focus(
						function()
						{
							$(this).animate({ paddingLeft: options.pad_out }, 150);
						});

						$(this).blur(
						function()
						{
							$(this).animate({ paddingLeft: options.pad_in }, 150);
						});  */
					});
				}
				else{
					$(link_elements).each(function(i){
						$(this).click(
							function()
							{
								$("li.basic-element a").removeClass("current");
								$(this).addClass("current");
							});
					});
				}

				if(options.slideout){
					//Slideout animation
					$("a#controlbtn", this).click(function(e) {
			            e.preventDefault();
			            var slidepx = $(".navigation-block").width();
			            if (!$("#" + options.parent_div).is(':animated')) {
			                if ($(this).hasClass('closed')) {
			                    $(this).removeClass('closed').html('<<');
			                    margin = "+=" + slidepx;
			                } else {
			                    $(this).addClass('closed').html('>>');
			                    margin = "-=" + slidepx;
			                }
			                $("#" + options.parent_div).animate({marginLeft: margin}, "slow");
			            }
			        });

					$("a#collapseLink", this).click(function(e) {
			            e.preventDefault();
			            var slidepx = $(".navigation-block").width();
			            if (!$("#" + options.parent_div).is(':animated')) {
			                if ($("a#controlbtn").hasClass('closed')) {
			                    $("a#controlbtn").removeClass('closed').html('<<');
			                    margin = "+=" + slidepx;
			                } else {
			                    $("a#controlbtn").addClass('closed').html('>>');
			                    margin = "-=" + slidepx;
			                }
			                $("#" + options.parent_div).animate({marginLeft: margin}, "slow");
			            }
			        });
				}
			});
		});
	}
})(jQuery);