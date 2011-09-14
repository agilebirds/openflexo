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
package org.openflexo.foundation.ie.widget;

import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.xml.FlexoComponentBuilder;

public class IESequenceButton extends IESequence<IButton> implements IButton
{

    public IESequenceButton(FlexoComponentBuilder builder)
    {
        this(builder.woComponent, null, builder.getProject());
        initializeDeserialization(builder);
    }

    public IESequenceButton(IEWOComponent woComponent, IEObject parent, FlexoProject prj)
    {
        super(woComponent, parent, prj);
    }

    /**
     * Overrides isSubsequence
     * 
     * @see org.openflexo.foundation.ie.widget.IESequence#isSubsequence()
     */
    @Override
    public boolean isSubsequence()
    {
        return getParent() instanceof IESequenceButton;
    }

}
