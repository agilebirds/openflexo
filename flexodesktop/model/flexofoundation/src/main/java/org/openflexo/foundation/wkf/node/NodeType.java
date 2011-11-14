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
package org.openflexo.foundation.wkf.node;

import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoObject;
import org.openflexo.xmlcode.StringConvertable;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * Convenient class used to represent type of FlexoNode (NORMAL/BEGIN/END/PSEUDO_BEGIN/PSEUDO_END
 * 
 * @author sguerin
 */
public abstract class NodeType extends FlexoObject implements StringConvertable, Serializable {

	private static final Logger logger = Logger.getLogger(NodeType.class.getPackage().getName());

	public static final NodeType NORMAL = new NormalNodeType();

	public static final NodeType BEGIN = new BeginNodeType();

	public static final NodeType END = new EndNodeType();

	public static final StringEncoder.Converter<NodeType> nodeTypeConverter = new Converter<NodeType>(NodeType.class) {

		@Override
		public NodeType convertFromString(String value) {
			return get(value);
		}

		@Override
		public String convertToString(NodeType value) {
			return value.getName();
		}

	};

	static class NormalNodeType extends NodeType implements Serializable {
		@Override
		public String getName() {
			return "NORMAL";
		}
	}

	static class BeginNodeType extends NodeType implements Serializable {
		@Override
		public String getName() {
			return "BEGIN";
		}
	}

	static class EndNodeType extends NodeType implements Serializable {
		@Override
		public String getName() {
			return "END";
		}
	}

	public abstract String getName();

	public static NodeType get(String typeName) {
		if (NORMAL.getName().equals(typeName)) {
			return NORMAL;
		} else if (BEGIN.getName().equals(typeName)) {
			return BEGIN;
		} else if (END.getName().equals(typeName)) {
			return END;
		} else {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Could not find NodeType named " + typeName);
			}
			return null;
		}
	}

	@Override
	public StringEncoder.Converter getConverter() {
		return nodeTypeConverter;
	}

}
