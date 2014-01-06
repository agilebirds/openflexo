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
package org.openflexo.foundation.ie.util;

import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.KVCFlexoObject;
import org.openflexo.kvc.ChoiceList;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Represents type of an hyperlink
 * 
 * @author sguerin
 * 
 */
public abstract class ClientSideEventType extends KVCFlexoObject implements StringConvertable, ChoiceList, Serializable {

	private static final Logger logger = Logger.getLogger(ClientSideEventType.class.getPackage().getName());

	public static final ClientSideEventType ONCLICK = new OnClickClientSideEventType();
	public static final ClientSideEventType MOUSEOUT = new MouseOutClientSideEventType();
	public static final ClientSideEventType MOUSEOVER = new MouseOverClientSideEventType();

	public static final StringEncoder.Converter<ClientSideEventType> ClientSideEventTypeConverter = new Converter<ClientSideEventType>(
			ClientSideEventType.class) {

		@Override
		public ClientSideEventType convertFromString(String value) {
			return get(value);
		}

		@Override
		public String convertToString(ClientSideEventType value) {
			return value.getName();
		}

	};

	public static class OnClickClientSideEventType extends ClientSideEventType {
		OnClickClientSideEventType() {
		}

		@Override
		public String getName() {
			return "onclick";
		}
	}

	public static class MouseOutClientSideEventType extends ClientSideEventType {
		MouseOutClientSideEventType() {
		}

		@Override
		public String getName() {
			return "onmouseout";
		}
	}

	public static class MouseOverClientSideEventType extends ClientSideEventType {
		MouseOverClientSideEventType() {
		}

		@Override
		public String getName() {
			return "onmouseover";
		}
	}

	public abstract String getName();

	public static ClientSideEventType get(String typeName) {
		for (Enumeration e = availableValues().elements(); e.hasMoreElements();) {
			ClientSideEventType temp = (ClientSideEventType) e.nextElement();
			if (temp.getName().equals(typeName)) {
				return temp;
			}
		}

		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Could not find ClientSideEventType named " + typeName);
		}
		return null;
	}

	private Vector<ClientSideEventType> _availableValues = null;

	@Override
	public Vector getAvailableValues() {
		if (_availableValues == null) {
			_availableValues = new Vector<ClientSideEventType>();
			_availableValues.add(ONCLICK);
			_availableValues.add(MOUSEOUT);
			_availableValues.add(MOUSEOVER);
		}
		return _availableValues;
	}

	@Override
	public StringEncoder.Converter getConverter() {
		return ClientSideEventTypeConverter;
	}

	public static Vector availableValues() {
		return ONCLICK.getAvailableValues();
	}

}
