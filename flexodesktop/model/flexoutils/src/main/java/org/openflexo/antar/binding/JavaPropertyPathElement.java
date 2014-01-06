package org.openflexo.antar.binding;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.kvc.KeyValueLibrary;
import org.openflexo.kvc.KeyValueProperty;
import org.openflexo.toolbox.ToolBox;

/**
 * Modelize a Java simple get/set access through a property<br>
 * The path element may be settable or not.
 * 
 * @author sylvain
 * 
 */
public class JavaPropertyPathElement extends SimplePathElement {

	private static final Logger logger = Logger.getLogger(DataBinding.class.getPackage().getName());

	private final KeyValueProperty keyValueProperty;

	public JavaPropertyPathElement(BindingPathElement parent, String propertyName) {
		super(parent, propertyName, Object.class);
		keyValueProperty = KeyValueLibrary.getKeyValueProperty(parent.getType(), propertyName);
		if (keyValueProperty != null) {
			setType(keyValueProperty.getType());
		} else {
			logger.warning("cannot find property " + propertyName + " for " + parent + " which type is " + parent.getType());
		}
	}

	public JavaPropertyPathElement(BindingPathElement parent, KeyValueProperty property) {
		super(parent, property.getName(), property.getType());
		keyValueProperty = property;
	}

	@Override
	public Type getType() {
		// IMPORTANT:
		// If declared type is a CustomType, don't try to instanciate the one from the keyValueProperty
		// which can also shadow a more specialized type encoded by a CustomType
		if (!(super.getType() instanceof CustomType) && keyValueProperty != null) {
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
		return keyValueProperty != null && keyValueProperty.isSettable();
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
		Object obj = keyValueProperty.getObjectValue(target);
		return obj;
	}

	@Override
	public void setBindingValue(Object value, Object target, BindingEvaluationContext context) {
		keyValueProperty.setObjectValue(value, target);
	}

	@Override
	public String toString() {
		return "JavaProperty " + getParent().getType() + "#" + getPropertyName();
	}
}
