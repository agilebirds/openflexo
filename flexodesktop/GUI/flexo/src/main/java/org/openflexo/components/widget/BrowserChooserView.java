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
package org.openflexo.components.widget;

import java.awt.Dimension;

import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.view.BrowserView;
import org.openflexo.components.widget.AbstractSelectorPanel.AbstractSelectorPanelOwner;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;


/**
 * View related to a ProjectBrowser, allowing to select an object with a
 * browsing perspective
 *
 * @author sguerin
 */
public abstract class BrowserChooserView extends BrowserView
{

    private AbstractSelectorPanelOwner<?> _owner;

    public BrowserChooserView(ProjectBrowser browser, AbstractSelectorPanelOwner<?> owner, FlexoEditor editor)
    {
        super(browser, null, editor);
        _owner = owner;
        setMinimumSize(getDefaultSize());
        validate();
    }

    public Dimension getDefaultSize()
    {
        return new Dimension(280, 350);
    }

    @Override
	public void treeSingleClick(FlexoModelObject object)
    {
        if (_owner.isSelectable(object)) {
            objectWasSelected(object);
        }
    }

    @Override
	public void treeDoubleClick(FlexoModelObject object)
    {
        if (object!=null && _owner.isSelectable(object)) {
           objectWasDefinitelySelected(object);
        }
    }

    public abstract void objectWasSelected(FlexoModelObject object);

    public abstract void objectWasDefinitelySelected(FlexoModelObject object);
}
