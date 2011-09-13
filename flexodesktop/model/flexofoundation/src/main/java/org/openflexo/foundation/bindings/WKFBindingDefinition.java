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
package org.openflexo.foundation.bindings;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.wkf.WKFObject;
import org.openflexo.inspector.InspectableObject;


/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class WKFBindingDefinition extends BindingDefinition implements InspectableObject
{

    @SuppressWarnings("hiding")
	private static final Logger logger = Logger.getLogger(WKFBindingDefinition.class.getPackage().getName());

    public WKFBindingDefinition(String variableName, DMType type, WKFObject object, BindingDefinitionType bindingType, boolean mandatory)
    {
        super(variableName, type, object, bindingType, mandatory);
    }

    public static WKFBindingDefinition get(WKFObject wkfObject, String name, DMType type, BindingDefinitionType bindingType, boolean mandatory)
    {
    	if (wkfObject.getProject()!=null)
    		return wkfObject.getProject().getWKFObjectBindingDefinition(wkfObject, name, type, bindingType, mandatory);
    	else {
    		if (logger.isLoggable(Level.WARNING))
				logger.warning("WKFObject "+wkfObject+" has no project");
    		return null;
    	}
    }

    public static WKFBindingDefinition get(WKFObject wkfObject, String name, Class typeClass, BindingDefinitionType bindingType, boolean mandatory)
    {
    	if (wkfObject.getProject()!=null)
    		return get(wkfObject,name,DMType.makeResolvedDMType(typeClass, wkfObject.getProject()),bindingType,mandatory);
    	else {
    		if (logger.isLoggable(Level.WARNING))
				logger.warning("WKFObject "+wkfObject+" has no project");
    		return null;
    	}
    }

}
