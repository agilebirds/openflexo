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
package org.openflexo.inspector.model;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.inspector.AbstractController;
import org.openflexo.inspector.DefaultInspectableObject;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLSerializable;


/**
 * Abstract class defining a model object in inspector definition
 * 
 * @author sguerin
 * 
 */
public abstract class ModelObject extends DefaultInspectableObject implements XMLSerializable
{

	private static final Logger logger = Logger.getLogger(InspectorModel.class.getPackage().getName());

	private boolean isEncoding = false;

	@Override
	public synchronized String toString()
	{
		if (!isEncoding) {
			isEncoding = true;
			try {
				String returned = XMLCoder
						.encodeObjectWithMapping(this, InspectorMapping.getInstance(), StringEncoder.getDefaultInstance());
				isEncoding = false;
				return returned;
			} catch (Exception e) {
				if (logger.isLoggable(Level.WARNING)) {
					logger.warning("Unexpected exception occured: " + e.getClass().getName());
				}
				e.printStackTrace();
			}
		}
		return super.toString();
	}

	public XMLSerializable copy(AbstractController c)
	{
		try {
			String temp = XMLCoder.encodeObjectWithMapping(this, InspectorMapping.getInstance(), StringEncoder.getDefaultInstance());
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("Clone TabModel with:\n" + temp);
			}
			return XMLDecoder.decodeObjectWithMapping(temp, InspectorMapping.getInstance(), c);
		} catch (Exception e) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Unexpected exception occured: " + e.getClass().getName());
			}
			e.printStackTrace();
		}
		return null;
	}

	@Override
	public String getInspectorName() {
		// TODO Auto-generated method stub
		return null;
	}
}
