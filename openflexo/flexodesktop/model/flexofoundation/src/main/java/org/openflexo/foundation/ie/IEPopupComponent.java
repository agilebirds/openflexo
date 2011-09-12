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
package org.openflexo.foundation.ie;

import java.util.logging.Logger;

import org.openflexo.foundation.ie.cl.PopupComponentDefinition;
import org.openflexo.foundation.ie.util.HyperlinkType;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.xml.FlexoComponentBuilder;


/**
 * Represents a 'popup' WOComponent related to a ComponentDefinition.Popup
 * attached to an Operation Node
 * 
 * @author sguerin
 * 
 */
public final class IEPopupComponent extends IEPageComponent
{

    @SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(IEPopupComponent.class.getPackage().getName());

    /**
     * Constructor invoked during deserialization for IEPopupComponent
     * 
     * @param componentDefinition
     */
    public IEPopupComponent(FlexoComponentBuilder builder)
    {
        super(builder);
        initializeDeserialization(builder);
    }

    /**
     * Default constructor for IEPopupComponent
     * 
     * @param componentDefinition
     */
    public IEPopupComponent(PopupComponentDefinition componentDefinition, FlexoProject prj)
    {
        super(componentDefinition, prj);
    }

    @Override
	public PopupComponentDefinition getComponentDefinition()
    {
        return (PopupComponentDefinition) super.getComponentDefinition();
    }

    @Override
	public String getInspectorName()
    {
        return "PopupComponent.inspector";
    }

    @Override
	public String getFullyQualifiedName()
    {
        return "Popup:" + getName();
    }
    /**
     * Overrides getClassNameKey
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return "popup_component";
    }
    
    public static class PopupRequiresAConfirmOrCancelButtonRule extends ValidationRule
    {
        public PopupRequiresAConfirmOrCancelButtonRule()
        {
            super(IEPopupComponent.class, "usually_popups_require_confirm_or_cancel_button");
        }

        @Override
		public ValidationIssue applyValidation(final Validable object)
        {
            final IEPopupComponent popup = (IEPopupComponent) object;
            if (popup.getName().equals("WDLDateAssistant")) {return null;}
            boolean found=false;
            for(IEHyperlinkWidget o:popup.getAllButtonInterface()) {
            	if (o.getHyperlinkType()==HyperlinkType.CONFIRM) {found=true;}
            	if (o.getHyperlinkType()==HyperlinkType.CANCEL) {found=true;}
            }
            if (!found) {
                return new ValidationWarning(this, object, "usually_popups_require_confirm_or_cancel_button");
            }
            return null;
        }

    }

    
}
