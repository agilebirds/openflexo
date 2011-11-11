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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.List;
import java.util.Vector;

import org.apache.commons.lang.StringUtils;
import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.bindings.WidgetBindingDefinition;
import org.openflexo.foundation.bindings.BindingDefinition.BindingDefinitionType;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.operator.RepetitionOperator;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.ParameteredFixProposal;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoComponentBuilder;

/**
 * @author gpolet Created on 12 sept. 2005
 */
public class IERadioButtonWidget extends IEControlWidget implements Serializable, IEWidgetWithValueList, IEWidgetWithMainBinding {

	/**
	 *
	 */
	public static final String RADIO_BUTTON_WIDGET = "radio_button_widget";

	public static final String BINDING_ISCHECKED_NAME = "isChecked";

	public static final String BUTTON_LABEL = "buttonLabel";

	public static final String ATTRIB_DESCRIPTION_NAME = "description";

	public static final String ATTRIB_DEFAULTVALUE_NAME = "value";

	public static final String GROUP_NAME = "groupName";

	protected boolean _value = false;

	private String groupName;

	private boolean _submitForm = false;

	private String labelAlignement;

	private boolean displayLabel = true;

	private String buttonLabel;

	private boolean useOneNameForAllRadios = false;

	protected BindingValue _bindingChecked; // Could be considered as a BindingValue because defined as GET_SET

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IERadioButtonWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public IERadioButtonWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
	}

	@Override
	public String getDefaultInspectorName() {
		return "RadioButton.inspector";
	}

	// ==========================================================================
	// ============================= XMLSerialize
	// ===============================
	// ==========================================================================

	@Override
	public void performOnDeleteOperations() {
		if (_woComponent != null && getGroupName() != null) {
			_woComponent.getRadioButtonManager().unRegisterButton(this, getGroupName());
		}
		super.performOnDeleteOperations();
	}

	// ==========================================================================
	// ============================= Variables
	// ==================================
	// ==========================================================================

	public WidgetBindingDefinition getBindingCheckedDefinition() {
		return WidgetBindingDefinition.get(this, "bindingChecked", Boolean.TYPE, BindingDefinitionType.GET_SET, true);
	}

	public BindingValue getBindingChecked() {
		if (isBeingCloned()) {
			return null;
		}
		return _bindingChecked;
	}

	public void setBindingChecked(BindingValue bindingChecked) {
		_bindingChecked = bindingChecked;
		setChanged();
		if (_bindingChecked != null) {
			_bindingChecked.setOwner(this);
			_bindingChecked.setBindingDefinition(getBindingCheckedDefinition());
		}
		setChanged();
		notifyObservers(new IEDataModification("bindingChecked", null, _bindingChecked));
	}

	public String getButtonLabel() {
		if (buttonLabel == null) {
			buttonLabel = getLabel();
		}
		return buttonLabel;
	}

	public void setButtonLabel(String buttonLabel) {
		String old = this.buttonLabel;
		this.buttonLabel = buttonLabel;
		if (!isDeserializing()) {
			setChanged();
			notifyModification(BUTTON_LABEL, old, buttonLabel);
		}
	}

	public boolean getValue() {
		return _value;
	}

	/*
	 * (non-Javadoc)
	 *
	 * @see org.openflexo.foundation.ie.widget.IEWidget#setWOComponent(org.openflexo.foundation.ie.IEWOComponent)
	 */
	@Override
	public void setWOComponent(IEWOComponent woComponent) {
		if (noWOChange(woComponent)) {
			return;
		}
		if (this._woComponent != woComponent && _woComponent != null && getGroupName() != null) {
			_woComponent.getRadioButtonManager().unRegisterButton(this, this.getGroupName());
		}
		super.setWOComponent(woComponent);// This call is very important because it will update the WOComponent components cache
		if (woComponent != null && getGroupName() != null) {
			woComponent.getRadioButtonManager().registerButton(this, getGroupName());
		}
		setChanged();
	}

	public void setValue(boolean value) {
		if (value != this._value && value && getGroupName() != null && !isDeserializing() && getWOComponent() != null) {
			HashSet<IERadioButtonWidget> v = getWOComponent().getRadioButtonManager().getButtons(this.groupName);
			if (v == null) {
				v = getWOComponent().getRadioButtonManager().registerButton(this, groupName);
			}
			Iterator<IERadioButtonWidget> i = v.iterator();
			while (i.hasNext()) {
				IERadioButtonWidget element = i.next();
				if (element != this) {
					element.setValue(false);
				}
			}
		}
		this._value = value;
		if (!isDeserializing()) {
			setChanged();
			notifyObservers(new DataModification(DataModification.ATTRIBUTE, ATTRIB_DEFAULTVALUE_NAME, null, null));
		}
	}

	public boolean getSubmitForm() {
		return _submitForm;
	}

	public void setSubmitForm(boolean aBoolean) {
		_submitForm = aBoolean;
		setChanged();
		notifyObservers(new IEDataModification("submitForm", null, new Boolean(_submitForm)));
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

	/*
	 * (non-Javadoc)
	 *
	 * @see org.openflexo.foundation.FlexoModelObject#getFullyQualifiedName()
	 */
	@Override
	public String getFullyQualifiedName() {
		return "RadioButton";
	}

	/**
	 * @return Returns the groupName.
	 */
	public String getGroupName() {
		if (groupName == null) {
			groupName = "groupName";
		}
		return groupName;
	}

	/**
	 * @param groupName
	 *            The groupName to set.
	 */
	public void setGroupName(String groupName) {
		if (this.groupName != null && _woComponent != null) {
			getWOComponent().getRadioButtonManager().unRegisterButton(this, this.groupName);
		}
		this.groupName = groupName;
		if (groupName != null && _woComponent != null) {
			getWOComponent().getRadioButtonManager().registerButton(this, this.groupName);
		}
		setChanged();
		notifyObservers(new IEDataModification("groupName", null, groupName));
	}

	public boolean getLabelAlign() {
		return labelAlignement != null && labelAlignement.equals("Left");
	}

	public void setLabelAlign(boolean b) {
		String old = labelAlignement;
		if (b) {
			labelAlignement = "Left";
		} else {
			labelAlignement = "Right";
		}
		setChanged();
		notifyObservers(new IEDataModification("labelAlign", old, labelAlignement));
	}

	public String getLabelAlignement() {
		return labelAlignement;
	}

	public void setLabelAlignement(String labelAlignement) {
		String old = this.labelAlignement;
		this.labelAlignement = labelAlignement;
		setChanged();
		notifyObservers(new IEDataModification("labelAlign", old, labelAlignement));
	}

	@Override
	protected Hashtable<String, String> getLocalizableProperties(Hashtable<String, String> props) {
		if (!StringUtils.isEmpty(getLabel())) {
			props.put("displayLabel", getLabel());
		}
		return super.getLocalizableProperties(props);
	}

	public boolean getDisplayLabel() {
		return displayLabel;
	}

	public void setDisplayLabel(boolean displayLabel) {
		Boolean old = new Boolean(this.displayLabel);
		this.displayLabel = displayLabel;
		setChanged();
		notifyObservers(new IEDataModification("displayLabel", old, new Boolean(displayLabel)));
	}

	public boolean getUseOneNameForAllRadios() {
		return useOneNameForAllRadios;
	}

	public void setUseOneNameForAllRadios(boolean useOneNameForAllRadios) {
		this.useOneNameForAllRadios = useOneNameForAllRadios;
		setChanged();
		notifyObservers(new IEDataModification("useOneNameForAllRadios", null, new Boolean(useOneNameForAllRadios)));
	}

	/**
	 * Returns all the buttons of the group to which this radiobutton belongs, including himself.
	 * 
	 * @return all the buttons of the group to which this radiobutton belongs, including himself.
	 */
	public HashSet<IERadioButtonWidget> getGroupButtons() {
		return getWOComponent().getRadioButtonManager().getButtons(getGroupName());
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return RADIO_BUTTON_WIDGET;
	}

	public boolean hasSmallestFlexoID() {
		HashSet<IERadioButtonWidget> v = getWOComponent().getRadioButtonManager().getButtons(this.groupName);
		if (v == null) {
			v = getWOComponent().getRadioButtonManager().registerButton(this, groupName);
		}
		long smallestID = -1;
		Iterator<IERadioButtonWidget> i = v.iterator();
		while (i.hasNext()) {
			IERadioButtonWidget radio = i.next();
			if (radio.getFlexoID() > smallestID) {
				smallestID = radio.getFlexoID();
			}
		}
		return smallestID == getFlexoID();
	}

	public static class RadioButtonReloadOnChange extends ValidationRule<RadioButtonReloadOnChange, IERadioButtonWidget> {

		public RadioButtonReloadOnChange() {
			super(IERadioButtonWidget.class, "radio_button_reload_on_change");
		}

		@Override
		public ValidationIssue<RadioButtonReloadOnChange, IERadioButtonWidget> applyValidation(IERadioButtonWidget radio) {
			if (!radio.getSubmitForm()) {
				return new ValidationWarning<RadioButtonReloadOnChange, IERadioButtonWidget>(this, radio, "radio_button_reload_on_change",
						new SetReloadOnChange());
			}
			return null;
		}

		@Override
		public boolean isValidForTarget(TargetType targetType) {
			return targetType == CodeType.PROTOTYPE;
		}

	}

	public static class SetReloadOnChange extends FixProposal<RadioButtonReloadOnChange, IERadioButtonWidget> {

		public SetReloadOnChange() {
			super("set_radio_button_to_reload_on_value_change");
		}

		@Override
		protected void fixAction() {
			getObject().setSubmitForm(true);
		}

	}

	public static class RadioButtonMustBeAllInTheSameRepetition extends
			ValidationRule<RadioButtonMustBeAllInTheSameRepetition, IERadioButtonWidget> {

		public RadioButtonMustBeAllInTheSameRepetition() {
			super(IERadioButtonWidget.class, "radio_buttons_must_be_all_in_the_same_repetition");
		}

		@Override
		public ValidationIssue<RadioButtonMustBeAllInTheSameRepetition, IERadioButtonWidget> applyValidation(IERadioButtonWidget object) {
			if (object.isInRepetition()) {
				RepetitionOperator rep = object.getHTMLListDescriptor().getRepetitionOperator();
				for (IERadioButtonWidget r : object.getRadios()) {
					if (!r.isInRepetition() || r.getHTMLListDescriptor().getRepetitionOperator() != rep) {
						return new ValidationError<RadioButtonMustBeAllInTheSameRepetition, IERadioButtonWidget>(this, object,
								"their_are_radios_in_the_same_group_that_are_not_in_the_same_repetition", new ChangeRadioButtonName(object
										.getWOComponent().getRadioButtonManager().getUnusedGroupName(object.getGroupName())));
					}

				}
			}
			return null;
		}

		public static class ChangeRadioButtonName extends
				ParameteredFixProposal<RadioButtonMustBeAllInTheSameRepetition, IERadioButtonWidget> {

			public ChangeRadioButtonName(String proposal) {
				super("change_radio_button_group_name", new ParameterDefinition<?>[] { new TextFieldParameter("button_group",
						"enter_new_group_name", proposal) });
			}

			@Override
			protected void fixAction() {
				String s = (String) getValueForParameter("button_group");
				if (s != null && s.trim().length() != 0) {
					getObject().setGroupName(s);
				}
			}

		}
	}

	/**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithValueList#getValueList()
	 */
	@Override
	public List<Object> getValueList() {
		return getValueList(null);
	}

	/**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithValueList#getValueList(org.openflexo.foundation.wkf.FlexoProcess)
	 */
	@Override
	public List<Object> getValueList(FlexoProcess process) {
		List<Object> result = new ArrayList<Object>();
		result.add(getValue());
		return result;
	}

	/**
	 * @see org.openflexo.foundation.ie.widget.IEWidgetWithMainBinding#getMainBinding()
	 */
	@Override
	public AbstractBinding getMainBinding() {
		return getBindingChecked();
	}
}
