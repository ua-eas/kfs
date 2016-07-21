/*
 * The Kuali Financial System, a comprehensive financial management system for higher education.
 * 
 * Copyright 2005-2015 The Kuali Foundation
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
package org.kuali.kfs.krad.uif.field;

import org.apache.commons.lang.StringUtils;
import org.kuali.kfs.krad.uif.component.Component;
import org.kuali.kfs.krad.uif.view.View;

/**
 * Field that encloses an image element
 *
 * 
 */
public class ImageField extends FieldBase {
    private static final long serialVersionUID = -7994212503770623408L;

    private String source;
    private String altText;
    private String height;
    private String width;

    private boolean captionHeaderAboveImage;

    private String captionHeaderText;
    private HeaderField captionHeader;

    private String cutlineText;
    private MessageField cutline;

    public ImageField() {
        super();
    }

    public void performFinalize(View view, Object model, Component parent) {
        super.performFinalize(view, model, parent);

        if (StringUtils.isNotBlank(captionHeaderText)) {
            captionHeader.setHeaderText(captionHeaderText);
        }

        if (StringUtils.isNotBlank(cutlineText)) {
            cutline.setMessageText(cutlineText);
        }
    }

    public String getSource() {
        return this.source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public String getAltText() {
        return this.altText;
    }

    public void setAltText(String altText) {
        this.altText = altText;
    }

    public String getHeight() {
        return this.height;
    }

    public void setHeight(String height) {
        this.height = height;
    }

    public void setWidth(String width) {
        this.width = width;
    }

    public String getWidth() {
        return width;
    }

    public String getCaptionHeaderText() {
        return captionHeaderText;
    }

    public void setCaptionHeaderText(String captionHeaderText) {
        this.captionHeaderText = captionHeaderText;
    }

    public HeaderField getCaptionHeader() {
        return captionHeader;
    }

    public void setCaptionHeader(HeaderField captionHeader) {
        this.captionHeader = captionHeader;
    }

    public String getCutlineText() {
        return cutlineText;
    }

    public void setCutlineText(String cutlineText) {
        this.cutlineText = cutlineText;
    }

    public MessageField getCutline() {
        return cutline;
    }

    /**
     * A cutline is the text describing the image in detail (this is also often confusingly called a caption).
     */
    public void setCutline(MessageField cutline) {
        this.cutline = cutline;
    }

    public boolean isCaptionHeaderAboveImage() {
        return captionHeaderAboveImage;
    }

    public void setCaptionHeaderAboveImage(boolean captionHeaderAboveImage) {
        this.captionHeaderAboveImage = captionHeaderAboveImage;
    }
}
