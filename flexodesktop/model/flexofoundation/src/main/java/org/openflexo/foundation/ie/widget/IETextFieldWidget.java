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
package org.openflexo.foundation.ie.widget;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Vector;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.util.IEControlOperator;
import org.openflexo.foundation.ie.util.TextFieldClass;
import org.openflexo.foundation.ie.util.TextFieldFormatType;
import org.openflexo.foundation.ie.util.TextFieldType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.toolbox.ToolBox;

/**
 * Represents a 'TextField' widget
 * 
 * @author bmangez
 */
public class IETextFieldWidget extends IEEditableTextWidget {

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	/**
     * 
     */
	public static final String TEXTFIELD_WIDGET = "textfield_widget";

	private TextFieldType _fieldType;

	private TextFieldFormatType _formatType;

	private IEControlOperator operator;

	private int _min = 0;

	private int _max = Integer.MAX_VALUE;

	private boolean isDynamic = false;

	private String values;

	private int size = 0;

	private boolean isExtensible = false;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IETextFieldWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public IETextFieldWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
	}

	@Override
	public String getDefaultInspectorName() {
		return "TextField.inspector";
	}

	public TextFieldType get_field_type() {
		TextFieldType field_type = getFieldType();
		if (field_type == null)
			field_type = TextFieldType.TEXT;
		return field_type;
	}

	public TextFieldType getFieldType() {
		return _fieldType;
	}

	public void setFieldType(TextFieldType type) {
		TextFieldType oldType = getFieldType();
		if (oldType != type) {
			_fieldType = type;
			setChanged();
			notifyObservers(new WKFAttributeDataModification("fieldType", oldType, type));
		}
	}

	public TextFieldFormatType getFormatType() {
		return _formatType;
	}

	public void setFormatType(TextFieldFormatType format) {
		_formatType = format;
		setChanged();
		notifyObservers(new IEDataModification("formatType", null, format));
	}

	public int getMax() {
		return _max;
	}

	public void setMax(int max) {
		this._max = max;
		setChanged();
		notifyObservers(new IEDataModification("max", null, new Integer(max)));
	}

	public int getMin() {
		return _min;
	}

	public void setMin(int min) {
		this._min = min;
		setChanged();
		notifyObservers(new IEDataModification("min", null, new Integer(min)));
	}

	/**
	 * @return
	 */
	public boolean getIsDynamic() {
		return isDynamic;
	}

	public void setIsDynamic(boolean value) {
		this.isDynamic = value;
		setChanged();
		notifyObservers(new IEDataModification("isDynamic", null, new Boolean(value)));
	}

	/**
	 * @return
	 */
	public String getValues() {
		return values;
	}

	public void setValues(String s) {
		values = s;
		setChanged();
		notifyObservers(new IEDataModification("values", null, values));
	}

	@Override
	public WidgetBindingDefinition getBindingValueDefinition() {
		if (getFieldType() != null && getFieldType() == TextFieldType.DATE) {
			return WidgetBindingDefinition.get(this, BINDING_VALUE, Date.class, BindingDefinitionType.GET_SET, false);
		}
		if (getFieldType() != null && getFieldType() == TextFieldType.INTEGER) {
			return WidgetBindingDefinition.get(this, BINDING_VALUE, Number.class, BindingDefinitionType.GET_SET, false);
		}
		if (getFieldType() != null && getFieldType() == TextFieldType.FLOAT) {
			return WidgetBindingDefinition.get(this, BINDING_VALUE, Float.class, BindingDefinitionType.GET_SET, false);
		}
		if (getFieldType() != null && getFieldType() == TextFieldType.DOUBLE) {
			return WidgetBindingDefinition.get(this, BINDING_VALUE, Double.class, BindingDefinitionType.GET_SET, false);
		}
		return WidgetBindingDefinition.get(this, BINDING_VALUE, String.class, BindingDefinitionType.GET_SET, false);
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is NOT a recursive method
	 * 
	 * @return a Vector of IEObject instances
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		return EMPTY_IOBJECT_VECTOR;
	}

	@Override
	public String getFullyQualifiedName() {
		return getWOComponent().getName() + "." + getName() + "(TextField)";
	}

	public IEControlOperator getOperator() {
		return operator;
	}

	public void setOperator(IEControlOperator operator) {
		this.operator = operator;
		setChanged();
		notifyObservers(new IEDataModification("operator", null, operator));
	}

	private boolean isDateOrIntegerOrFloat() {
		return getFieldType() != null
				&& (getFieldType().equals(TextFieldType.DATE) || getFieldType().equals(TextFieldType.INTEGER) || getFieldType().equals(
						TextFieldType.FLOAT));
	}

	public String getOperatorCodeStringRepresentation() {
		if (isDateOrIntegerOrFloat()) {
			if (getOperator() == null)
				return IEControlOperator.EQUAL.getSign();
			if (getOperator() == IEControlOperator.LIKE || getOperator() == IEControlOperator.CASEINSENSITIVELIKE)
				return IEControlOperator.EQUAL.getSign();
			return getOperator().getSign();
		} else {
			if (getOperator() == null)
				return IEControlOperator.CASEINSENSITIVELIKE.getSign();
			if (getOperator() == IEControlOperator.LIKE || getOperator() == IEControlOperator.CASEINSENSITIVELIKE)
				return getOperator().getSign();
			return IEControlOperator.CASEINSENSITIVELIKE.getSign();
		}
	}

	private static final Vector<TextFieldType> fieldTypeList = new Vector<TextFieldType>(Arrays.asList(TextFieldType.TEXT,
			TextFieldType.DATE, TextFieldType.INTEGER, TextFieldType.FLOAT, TextFieldType.DOUBLE));

	public Vector availableFieldType() {
		return fieldTypeList;
	}

	public int getSize() {
		return size;
	}

	public void setSize(int s) {
		size = s;
		setChanged();
	}

	public boolean getIsExtensible() {
		return getTfcssClass() != null && getTfcssClass().equals(TextFieldClass.DLEXTENSIBLE);
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return TEXTFIELD_WIDGET;
	}

	@Override
	public boolean isText() {
		return getFieldType() == null || getFieldType() == TextFieldType.TEXT;
	}

	@Override
	public boolean isDate() {
		return getFieldType() == TextFieldType.DATE;
	}

	public boolean isInteger() {
		return getFieldType() == TextFieldType.INTEGER;
	}

	public boolean isFloat() {
		return getFieldType() == TextFieldType.FLOAT;
	}

	public boolean isDouble() {
		return getFieldType() == TextFieldType.DOUBLE;
	}

	public boolean isKV() {
		return getFieldType() == TextFieldType.KEYVALUE;
	}

	public boolean isPhone() {
		return isText() && getFormatType() == TextFieldFormatType.PHONE;
	}

	public boolean isBank() {
		return isText() && getFormatType() == TextFieldFormatType.BANK_ACCOUNT;
	}

	public boolean isEmail() {
		return isText() && getFormatType() == TextFieldFormatType.EMAIL;
	}

	public boolean isAutocomplete() {
		// return isText()&&getFormatType()==TextFieldFormatType.AUTOCOMPLETE;
		return false;
	}

	public boolean isURL() {
		return isText() && getFormatType() == TextFieldFormatType.URL;
	}

	/**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithValueList#getValueList(org.openflexo.foundation.wkf.FlexoProcess)
	 */
	@Override
	public List<Object> getValueList(FlexoProcess process) {
		return parseValueListToAppropriateType(getValue(), getValues(), getFieldType(), null, process);
	}

	/**
	 * @return
	 */
	public String getTextFieldUniqueID() {
		if (getCalculatedLabel() != null)
			return ToolBox.getJavaName(getCalculatedLabel().trim()) + getFlexoID();
		return "textfield_" + getFlexoID();
	}

	/**
	 * Overrides isNumber
	 * 
	 * @see org.openflexo.foundation.ie.widget.IEEditableTextWidget#isNumber()
	 */
	@Override
	public boolean isNumber() {
		return isInteger() || isFloat() || isDouble();
	}

	public static class ExampleValueMustMatchFieldType extends ValidationRule {

		/**
		 * @param objectType
		 * @param ruleName
		 */
		public ExampleValueMustMatchFieldType() {
			super(IETextFieldWidget.class, "example_value_must_match_field_type");
		}

		/**
		 * Overrides applyValidation
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue applyValidation(Validable object) {
			IETextFieldWidget tf = (IETextFieldWidget) object;
			if (tf.get_field_type() == TextFieldType.INTEGER) {
				if (tf.getValue() != null) {
					try {
						Integer.parseInt(tf.getValue());
					} catch (NumberFormatException e) {
						Vector<FixProposal> fixes = new Vector<FixProposal>();
						fixes.add(new SetDefaultValue("0"));
						fixes.add(new SetDefaultValue());
						fixes.add(new SetFieldTypeToText());
						return new ValidationError(this, tf, "example_value_does_not_match_field_type", fixes);
					}
				}
			} else if (tf.get_field_type() == TextFieldType.FLOAT) {
				if (tf.getValue() != null) {
					try {
						Float.parseFloat(tf.getValue());
					} catch (NumberFormatException e) {
						Vector<FixProposal> fixes = new Vector<FixProposal>();
						fixes.add(new SetDefaultValue("0"));
						fixes.add(new SetDefaultValue());
						fixes.add(new SetFieldTypeToText());
						try {
							Float.parseFloat(tf.getValue().replace(',', '.'));
						} catch (NumberFormatException e1) {
							return new ValidationError(this, tf, "example_value_does_not_match_field_type", fixes);
						}
						fixes.insertElementAt(new SetDefaultValue(tf.getValue().replace(',', '.')), 0);
						return new ValidationError(this, tf, "example_value_does_not_match_field_type", fixes);
					}
				}
			} else if (tf.get_field_type() == TextFieldType.DOUBLE) {
				if (tf.getValue() != null) {
					try {
						Double.parseDouble(tf.getValue());
					} catch (NumberFormatException e) {
						Vector<FixProposal> fixes = new Vector<FixProposal>();
						fixes.add(new SetDefaultValue("0"));
						fixes.add(new SetDefaultValue());
						fixes.add(new SetFieldTypeToText());
						try {
							Double.parseDouble(tf.getValue().replace(',', '.'));
						} catch (NumberFormatException e1) {
							return new ValidationError(this, tf, "example_value_does_not_match_field_type", fixes);
						}
						fixes.insertElementAt(new SetDefaultValue(tf.getValue().replace(',', '.')), 0);
						return new ValidationError(this, tf, "example_value_does_not_match_field_type", fixes);
					}
				}
			} else if (tf.get_field_type() == TextFieldType.DATE) {
				if (tf.getValue() != null) {
					if (tf.getProject().getProjectDateFormat() != null) {
						boolean ok = false;
						try {
							Date date = new SimpleDateFormat(tf.getProject().getProjectDateFormat().getJavaPattern()).parse(tf.getValue());
							ok = date != null;
						} catch (ParseException e) {
						}
						if (!ok) {
							Vector<FixProposal> fixes = new Vector<FixProposal>();
							fixes.add(new SetDefaultValue(new SimpleDateFormat(tf.getProject().getProjectDateFormat().getJavaPattern())
									.format(new Date())));
							fixes.add(new SetDefaultValue());
							fixes.add(new SetFieldTypeToText());
							return new ValidationError(this, tf, "example_value_does_not_match_project_date_formatter", fixes);
						}

					}
				}
			}
			return null;
		}

		public static class SetFieldTypeToText extends FixProposal {

			/**
			 * @param aMessage
			 */
			public SetFieldTypeToText() {
				super("set_field_type_to_text");
			}

			/**
			 * Overrides fixAction
			 * 
			 * @see org.openflexo.foundation.validation.FixProposal#fixAction()
			 */
			@Override
			protected void fixAction() {
				((IETextFieldWidget) getObject()).setFieldType(TextFieldType.TEXT);
			}

		}

		public static class SetDefaultValue extends FixProposal {

			private String value;

			/**
			 * Constructor to set the value to <code>null</code>
			 */
			public SetDefaultValue() {
				super("reset_value");
				value = null;
			}

			/**
			 * @param aMessage
			 */
			public SetDefaultValue(String defaultValue) {
				super("set_value_to_($value)");
				value = defaultValue;
			}

			public String getValue() {
				return value;
			}

			/**
			 * Overrides fixAction
			 * 
			 * @see org.openflexo.foundation.validation.FixProposal#fixAction()
			 */
			@Override
			protected void fixAction() {
				((IETextFieldWidget) getObject()).setValue(value);
			}
		}

		/**
		 * Overrides isValidForTarget
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#isValidForTarget(TargetType)
		 */
		@Override
		public boolean isValidForTarget(TargetType targetType) {
			return targetType == CodeType.PROTOTYPE;
		}
	}

}
