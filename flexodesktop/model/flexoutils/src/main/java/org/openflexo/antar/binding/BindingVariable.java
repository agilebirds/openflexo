package org.openflexo.antar.binding;

import java.beans.PropertyChangeSupport;
import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;
import org.openflexo.toolbox.HasPropertyChangeSupport;
import org.openflexo.toolbox.ToolBox;

public class BindingVariable implements BindingPathElement, SettableBindingPathElement, HasPropertyChangeSupport {

	private static final Logger logger = Logger.getLogger(BindingVariable.class.getPackage().getName());

	private String variableName;
	private Type type;
	private boolean settable = false;
	private PropertyChangeSupport pcSupport;

	public static final String VARIABLE_NAME = "variableName";
	public static final String TYPE = "type";

	public BindingVariable(String variableName, Type type) {
		super();
		this.variableName = variableName;
		this.type = type;
		pcSupport = new PropertyChangeSupport(this);
	}

	public BindingVariable(String variableName, Type type, boolean settable) {
		this(variableName, type);
		setSettable(settable);
	}

	@Override
	public Type getType() {
		return type;
	}

	public void setType(Type type) {
		if (type != null && !type.equals(this.type)) {
			Type oldType = this.type;
			this.type = type;
			pcSupport.firePropertyChange(TYPE, oldType, type);
		}
	}

	public String getVariableName() {
		return variableName;
	}

	public void setVariableName(String aVariableName) {
		if (aVariableName != null && !aVariableName.equals(variableName)) {
			String oldVariableName = variableName;
			this.variableName = aVariableName;
			pcSupport.firePropertyChange(VARIABLE_NAME, oldVariableName, variableName);
		}
	}

	@Override
	public String toString() {
		return getVariableName() + "/" + TypeUtils.simpleRepresentation(getType());
	}

	@Override
	public String getSerializationRepresentation() {
		return variableName;
	}

	@Override
	public String getLabel() {
		return getVariableName();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		String returned = "<html>";
		String resultingTypeAsString;
		if (getType() != null) {
			resultingTypeAsString = TypeUtils.simpleRepresentation(getType());
			resultingTypeAsString = ToolBox.replaceStringByStringInString("<", "&LT;", resultingTypeAsString);
			resultingTypeAsString = ToolBox.replaceStringByStringInString(">", "&GT;", resultingTypeAsString);
		} else {
			resultingTypeAsString = "???";
		}
		returned += "<p><b>" + resultingTypeAsString + " " + getVariableName() + "</b></p>";
		// returned +=
		// "<p><i>"+(bv.getDescription()!=null?bv.getDescription():FlexoLocalization.localizedForKey("no_description"))+"</i></p>";
		returned += "</html>";
		return returned;
	}

	@Override
	public boolean isSettable() {
		return settable;
	}

	public void setSettable(boolean settable) {
		this.settable = settable;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj instanceof BindingVariable) {
			return getVariableName().equals(((BindingVariable) obj).getVariableName()) && getType() != null
					&& getType().equals(((BindingVariable) obj).getType());
		}
		return super.equals(obj);
	}

	@Override
	public int hashCode() {
		return (toString()).hashCode();
	}

	@Override
	public Object getBindingValue(Object owner, BindingEvaluationContext context) {
		return context.getValue(this);
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) throws TypeMismatchException,
			NullReferenceException {
		if (isSettable() && context instanceof SettableBindingEvaluationContext) {
			((SettableBindingEvaluationContext) context).setValue(value, this);
		}
	}

	@Override
	public BindingPathElement getParent() {
		return null;
	}

	@Override
	public PropertyChangeSupport getPropertyChangeSupport() {
		return pcSupport;
	}

	@Override
	public String getDeletedProperty() {
		return null;
	}

}
