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

import org.openflexo.icon.WSEIconLibrary;
import org.openflexo.localization.FlexoLocalization;


import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementType;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.foundation.wkf.ws.AbstractMessageDefinition;

/**
 * Browser element representing a MessageDefinition
 *
 * @author sguerin
 *
 */
public class MessageDefinitionElement extends BrowserElement
{

    protected static final ImageIcon SMALL_OUT_MESSAGE_DEFINITION_LEFT_ICON = WSEIconLibrary.WS_OUT_MESSAGE_LEFT_ICON;

    protected static final ImageIcon SMALL_IN_MESSAGE_DEFINITION_LEFT_ICON = WSEIconLibrary.WS_IN_MESSAGE_LEFT_ICON;
    protected static final ImageIcon SMALL_FAULT_MESSAGE_DEFINITION_LEFT_ICON = WSEIconLibrary.WS_FAULT_MESSAGE__LEFT_ICON;


    public MessageDefinitionElement(AbstractMessageDefinition message, ProjectBrowser browser, BrowserElement parent)
    {
        super(message, BrowserElementType.MESSAGE_DEFINITION, browser, parent);
    }

    @Override
	protected void buildChildrenVector()
    {
    }

    @Override
	public String getName()
    {
    		// use this method when name will be defined on a message definition
    	    if(getMessageDefinition().getName()!=null) return getMessageDefinition().getName();
    	    if(getMessageDefinition().isInputMessageDefinition())return FlexoLocalization.localizedForKey("input_message_definition");
    	    else if (getMessageDefinition().isOutputMessageDefinition())return FlexoLocalization.localizedForKey("output_message_definition");
    	    else if (getMessageDefinition().isFaultMessageDefinition())return FlexoLocalization.localizedForKey("fault_message_definition");
    	    else return super.getName();
    }

    protected AbstractMessageDefinition getMessageDefinition()
    {
        return (AbstractMessageDefinition) getObject();
    }


    @Override
	public Icon getIcon()
    {
 		if(getMessageDefinition().isInputMessageDefinition()) return SMALL_IN_MESSAGE_DEFINITION_LEFT_ICON;
		else if (getMessageDefinition().isOutputMessageDefinition()) return SMALL_OUT_MESSAGE_DEFINITION_LEFT_ICON;
		else if (getMessageDefinition().isFaultMessageDefinition()) return SMALL_FAULT_MESSAGE_DEFINITION_LEFT_ICON;
		return super.getIcon();
    }


}
