/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.components.browser.wkf;

import javax.swing.Icon;
import javax.swing.ImageIcon;

import org.openflexo.icon.IconFactory;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.IconMarker;
import org.openflexo.localization.FlexoLocalization;


import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.wkf.edge.FlexoPostCondition;
import org.openflexo.foundation.wkf.edge.MessageEdge;
import org.openflexo.foundation.wkf.node.IFOperator;

/**
 * Browser element representing a FlexoPostCondition
 *
 * @author sguerin
 *
 */
public class PostConditionElement extends BrowserElement
{

	private static final IconMarker POSITIVE_MARKER = IconLibrary.POSITIVE_MARKER.clone(1, 4);
	private static final IconMarker NEGATIVE_MARKER = IconLibrary.NEGATIVE_MARKER.clone(1, 4);

    public PostConditionElement(FlexoPostCondition postCondition, ProjectBrowser browser, BrowserElement parent)
    {
        super(postCondition, BrowserElementType.POSTCONDITION, browser, parent);
    }

    @Override
    public Icon getIcon() {
    	Icon icon = super.getIcon();
    	if (icon instanceof ImageIcon) {
	    	if (isPositive())
	    		return IconFactory.getImageIcon((ImageIcon)icon, POSITIVE_MARKER);
	    	else if (isNegative())
	    		return IconFactory.getImageIcon((ImageIcon)icon, NEGATIVE_MARKER);
    	}
    	return icon;
    }

    private boolean isPositive() {
    	return getPostCondition().getStartNode() instanceof IFOperator && getPostCondition().isPositiveEvaluation();
    }

    private boolean isNegative() {
    	return getPostCondition().getStartNode() instanceof IFOperator && !getPostCondition().isPositiveEvaluation();
    }

    @Override
	protected void buildChildrenVector()
    {
        if (getPostCondition() instanceof MessageEdge) {
        	MessageEdge messageEdge = (MessageEdge)getPostCondition();
        	if (messageEdge.getInputMessage() != null) {
        		addToChilds(messageEdge.getInputMessage());
        	}
        	if (messageEdge.getOutputMessage() != null) addToChilds(messageEdge.getOutputMessage());
        }
    }

    @Override
	public String getName()
    {
        if ((getPostCondition().getName() != null) && (!(getPostCondition().getName().trim().equals("")))) {
            return getPostCondition().getName();
        } else {
            return FlexoLocalization.localizedForKey(getPostCondition().getClassNameKey());
        }
    }

    protected FlexoPostCondition getPostCondition()
    {
        return (FlexoPostCondition) getObject();
    }

}
