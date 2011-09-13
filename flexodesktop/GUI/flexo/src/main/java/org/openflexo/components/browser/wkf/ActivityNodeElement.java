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

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.wkf.node.ActivityNode;
import org.openflexo.foundation.wkf.node.SelfExecutableNode;
import org.openflexo.icon.WKFIconLibrary;


/**
 * Browser element representing an Activity Node
 *
 * @author sguerin
 *
 */
public class ActivityNodeElement extends AbstractActivityNodeElement
{

    public ActivityNodeElement(ActivityNode node, ProjectBrowser browser, BrowserElement parent)
    {
        super(node, BrowserElementType.ACTIVITY_NODE, browser,parent);
    }

    protected ActivityNode getActivityNode()
    {
        return (ActivityNode) getObject();
    }

    @Override
	public Icon getIcon()
    {
        if (getActivityNode() instanceof SelfExecutableNode) {
            return decorateIcon(WKFIconLibrary.SELF_EXECUTABLE_ICON);
        } else if (getActivityNode().isBeginNode()) {
            return WKFIconLibrary.BEGIN_ACTIVITY_ICON;
        } else if (getActivityNode().isEndNode()) {
            return WKFIconLibrary.END_ACTIVITY_ICON;
        } else {
            return super.getIcon();
        }
    }

}
