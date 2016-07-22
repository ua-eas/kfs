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
package org.kuali.kfs.krad.uif.widget;

import org.kuali.kfs.krad.uif.component.ClientSideState;

/**
 * Decorates a group with collapse/expand functionality
 * 
 * 
 */
public class Disclosure extends WidgetBase {
    private static final long serialVersionUID = 1238789480161901850L;

    private String collapseImageSrc;
    private String expandImageSrc;

    private int animationSpeed;

    @ClientSideState
    private boolean defaultOpen;

    private boolean renderImage;

    public Disclosure() {
        super();

        defaultOpen = true;
        renderImage = true;
    }

    /**
     * Path to the images that should be displayed to collapse the group
     * 
     * @return String image path
     */
    public String getCollapseImageSrc() {
        return this.collapseImageSrc;
    }

    /**
     * Setter for the collapse image path
     * 
     * @param collapseImageSrc
     */
    public void setCollapseImageSrc(String collapseImageSrc) {
        this.collapseImageSrc = collapseImageSrc;
    }

    /**
     * Path to the images that should be displayed to expand the group
     * 
     * @return String image path
     */
    public String getExpandImageSrc() {
        return this.expandImageSrc;
    }

    /**
     * Setter for the expand image path
     * 
     * @param collapseImageSrc
     */
    public void setExpandImageSrc(String expandImageSrc) {
        this.expandImageSrc = expandImageSrc;
    }

    /**
     * Gives the speed for the open/close animation, a smaller int will result
     * in a faster animation
     * 
     * @return int animation speed
     */
    public int getAnimationSpeed() {
        return this.animationSpeed;
    }

    /**
     * Setter for the open/close animation speed
     * 
     * @param animationSpeed
     */
    public void setAnimationSpeed(int animationSpeed) {
        this.animationSpeed = animationSpeed;
    }

    /**
     * Indicates whether the group should be initially open
     * 
     * @return boolean true if group should be initially open, false if it
     *         should be closed
     */
    public boolean isDefaultOpen() {
        return this.defaultOpen;
    }

    /**
     * Setter for the default open indicator
     * 
     * @param defaultOpen
     */
    public void setDefaultOpen(boolean defaultOpen) {
        this.defaultOpen = defaultOpen;
    }

    /**
     * Indicates whether the expand/collapse image should be rendered for the closure, if set to false only
     * the group title will be clickable
     *
     * @return boolean true to render the expand/colapse image false to not
     */
    public boolean isRenderImage() {
        return renderImage;
    }

    /**
     * Setter for the render expand/collapse image indicator
     *
     * @param renderImage
     */
    public void setRenderImage(boolean renderImage) {
        this.renderImage = renderImage;
    }
}
