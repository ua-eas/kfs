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
package org.kuali.kfs.krad.uif;

import java.text.MessageFormat;

/**
 * Constants for CSS style strings
 */
public class CssConstants {

    public static final String DISPLAY = "display: ";

    public static class Displays {
        public static final String BLOCK = DISPLAY + "block;";
        public static final String INLINE = DISPLAY + "inline;";
        public static final String INLINE_BLOCK = DISPLAY + "inline-block;";
        public static final String NONE = DISPLAY + "none;";
    }

    public static final String TEXT_ALIGN = "text-align: ";

    public static class TextAligns {
        public static final String LEFT = TEXT_ALIGN + "left;";
        public static final String RIGHT = TEXT_ALIGN + "right;";
        public static final String CENTER = TEXT_ALIGN + "center;";
        public static final String JUSTIFY = TEXT_ALIGN + "justify;";
        public static final String INHERIT = TEXT_ALIGN + "inherit;";
    }

    public static final String VERTICAL_ALIGN = "vertical-align: ";

    public static class VerticalAligns {
        public static final String BASELINE = VERTICAL_ALIGN + "Baseline;";
        public static final String BOTTOM = VERTICAL_ALIGN + "bottom;";
        public static final String MIDDLE = VERTICAL_ALIGN + "middle;";
        public static final String TOP = VERTICAL_ALIGN + "top;";
    }

    public static class Margins {
        public static final String MARGIN_LEFT = "margin-left: {0};";
        public static final String MARGIN_RIGHT = "margin-right: {0};";
        public static final String MARGIN_TOP = "margin-top: {0};";
        public static final String MARGIN_BOTTOM = "margin-bottom: {0};";
    }

    public static class Padding {
        public static final String PADDING_LEFT = "padding-left: {0};";
        public static final String PADDING_RIGHT = "padding-right: {0};";
        public static final String PADDING_TOP = "padding-top: {0};";
        public static final String PADDING_BOTTOM = "padding-bottom: {0};";
    }

    public static final String WIDTH = "width: ";

    /**
     * Replaces parameters in the given CSS string with the corresponding
     * parameter values given
     *
     * @param style      - String with parameters to replace
     * @param parameters - One or more parameter values
     * @return String given string with filled parameters
     */
    public static final String getCssStyle(String style, String... parameters) {
        MessageFormat cssStyle = new MessageFormat(style);

        return cssStyle.format(parameters);
    }
}
