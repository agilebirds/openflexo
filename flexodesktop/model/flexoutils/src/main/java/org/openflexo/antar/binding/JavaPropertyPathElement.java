package org.openflexo.antar.binding;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.toolbox.ToolBox;
import org.openflexo.xmlcode.KeyValueCoder;
import org.openflexo.xmlcode.KeyValueDecoder;

/**
 * Modelize a Java simple get/set access through a property<br>
 * The path element may be settable or not.
 * 
 * @author sylvain
 * 
 */
public class JavaPropertyPathElement extends SimplePathElement {

	private KeyValueProperty keyValueProperty;

	public JavaPropertyPathElement(BindingPathElement parent, String propertyName, Type type) {
		super(parent, propertyName, type);
		keyValueProperty = KeyValueLibrary.getKeyValueProperty(parent.getType(), propertyName);
	}

	public JavaPropertyPathElement(BindingPathElement parent, KeyValueProperty property) {
		super(parent, property.getName(), property.getType());
		keyValueProperty = property;
	}

	@Override
	public Type getType() {
		if (keyValueProperty != null) {
			return TypeUtils.makeInstantiatedType(keyValueProperty.getType(), getParent().getType());
		}
		return super.getType();
	}

	@Override
	public String getLabel() {
		return getPropertyName();
	}

	@Override
	public boolean isSettable() {
		return keyValueProperty.isSettable();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		String returned = "<html>";
		String resultingTypeAsString;
		if (resultingType != null) {
			resultingTypeAsString = TypeUtils.simpleRepresentation(resultingType);
			resultingTypeAsString = ToolBox.replaceStringByStringInString("<", "&LT;", resultingTypeAsString);
			resultingTypeAsString = ToolBox.replaceStringByStringInString(">", "&GT;", resultingTypeAsString);
		} else {
			resultingTypeAsString = "???";
		}
		returned += "<p><b>" + resultingTypeAsString + " " + getPropertyName() + "</b></p>";
		// returned +=
		// "<p><i>"+(property.getDescription()!=null?property.getDescription():FlexoLocalization.localizedForKey("no_description"))+"</i></p>";
		returned += "</html>";
		return returned;
	}

	@Override
	public Object getBindingValue(Object target, BindingEvaluationContext context) {
		return KeyValueDecoder.objectForKey(target, getPropertyName());
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) {
		KeyValueCoder.setObjectForKey(target, value, getPropertyName());
	}
}
