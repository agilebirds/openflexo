package org.openflexo.fge;

import java.lang.reflect.Type;

import org.openflexo.inspector.DefaultInspectableObject;
import org.openflexo.xmlcode.XMLSerializable;

public class GRVariable extends DefaultInspectableObject implements XMLSerializable {

	private String name;
	private String value;
	private GRVariableType type;

	public static enum GRVariableType {
		String {
			@Override
			public Type getType() {
				return String.class;
			}
		},
		Integer {
			@Override
			public Type getType() {
				return Integer.class;
			}
		},
		Double {
			@Override
			public Type getType() {
				return Double.class;
			}
		};

		public abstract Type getType();
	}

	public GRVariable() {
	}

	public GRVariable(String name, GRVariableType type, String value) {
		super();
		this.name = name;
		this.type = type;
		this.value = value;
	}

	@Override
	public String getInspectorName() {
		return null;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public GRVariableType getType() {
		return type;
	}

	public void setType(GRVariableType type) {
		this.type = type;
	}

}
