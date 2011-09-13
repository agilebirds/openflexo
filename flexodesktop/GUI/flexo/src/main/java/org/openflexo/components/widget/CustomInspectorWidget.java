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

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.inspector.model.PropertyModel;
import org.openflexo.inspector.widget.CustomWidget;


/**
 * Abstract FLEXO custom inspector widget
 *
 * @author sguerin
 *
 */
public abstract class CustomInspectorWidget<T> extends CustomWidget<T>
{

    public CustomInspectorWidget(PropertyModel model, AbstractController controller)
    {
        super(model,controller);
    }

    public FlexoModelObject getInspectedObject()
    {
        if (getModel() instanceof FlexoModelObject) {
            return (FlexoModelObject) getModel();
        }
        return null;
    }

    @Override
	public void setModel(InspectableObject value)
    {
        if (value instanceof FlexoModelObject) {
            setProject(((FlexoModelObject) value).getProject());
        }
        super.setModel(value);
    }

    public void setProject(FlexoProject aProject)
    {
        // does nothing: should be overriden in subclasses if needed
    }

    /**
     * Update the model given the actual state of the widget
     */
    @Override
	public synchronized void updateModelFromWidget()
    {
        notifyApply();
    }

    /**
     * Override when required (generally close popup)
     */
    @Override
    public void fireEditingCanceled()
    {
    }

    /**
     * Override when required (generally close popup)
     */
    @Override
    public void fireEditingStopped()
    {
    }

    @Override
    public boolean disableTerminateEditOnFocusLost() {
    	return true;
    }

}
