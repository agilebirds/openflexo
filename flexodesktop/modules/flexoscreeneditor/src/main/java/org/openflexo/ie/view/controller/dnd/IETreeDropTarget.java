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
package org.openflexo.ie.view.controller.dnd;

import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.dnd.TreeDropTarget;
import org.openflexo.components.browser.view.BrowserView.FlexoJTree;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ie.action.MoveIEElement;
import org.openflexo.foundation.ie.widget.IEWidget;


/**
 * 
 * @author gpolet
 *
 */
public class IETreeDropTarget extends TreeDropTarget {

	public IETreeDropTarget(FlexoJTree tree, ProjectBrowser browser) {
		super(tree, browser);
	}

	@Override
	public boolean targetAcceptsSource(BrowserElement target,
			BrowserElement source) {
		if (target==null||source==null)
			return false;
		FlexoModelObject targetObject = target.getObject();
		FlexoModelObject sourceObject = source.getObject();
		if (targetObject instanceof IEWidget && sourceObject instanceof IEWidget)
			return IEDTListener.isValidDropTargetContainer((IEWidget)targetObject, (IEWidget) sourceObject);
		return false;
	}
	
	@Override
	public boolean handleDrop(BrowserElement moved, BrowserElement destination) {
		FlexoModelObject targetObject = destination.getObject();
		FlexoModelObject sourceObject = moved.getObject();
		if (!(targetObject instanceof IEWidget) || !(sourceObject instanceof IEWidget))
			return false;
		if (!targetAcceptsSource(destination, moved))
			return false;
        MoveIEElement moveAction = MoveIEElement.actionType.makeNewAction((IEWidget)targetObject, null, _browser
                .getEditor());
        moveAction.setMovedWidget((IEWidget) sourceObject);
        moveAction.doAction();
        return moveAction.hasActionExecutionSucceeded();
	}
}
