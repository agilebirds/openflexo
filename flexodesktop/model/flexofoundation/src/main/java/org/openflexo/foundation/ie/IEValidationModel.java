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
package org.openflexo.foundation.ie;

import java.util.logging.Logger;

import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.bindings.RequiredBindingValidationRule;
import org.openflexo.foundation.ie.cl.ComponentDefinition;
import org.openflexo.foundation.ie.cl.FlexoComponentFolder;
import org.openflexo.foundation.ie.menu.FlexoItemMenu;
import org.openflexo.foundation.ie.operator.ConditionalOperator;
import org.openflexo.foundation.ie.operator.RepetitionOperator;
import org.openflexo.foundation.ie.widget.IEAbstractListWidget;
import org.openflexo.foundation.ie.widget.IEButtonWidget;
import org.openflexo.foundation.ie.widget.IECheckBoxWidget;
import org.openflexo.foundation.ie.widget.IEEditableTextWidget;
import org.openflexo.foundation.ie.widget.IEHeaderWidget;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IELabelWidget;
import org.openflexo.foundation.ie.widget.IERadioButtonWidget;
import org.openflexo.foundation.ie.widget.IEStringWidget;
import org.openflexo.foundation.ie.widget.IETextFieldWidget;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationModel;

/**
 * Please comment this class
 * 
 * @author sguerin
 * 
 */
public class IEValidationModel extends ValidationModel {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(IEValidationModel.class.getPackage().getName());

	public IEValidationModel(FlexoProject project) {
		this(project, project.getTargetType());
	}

	public IEValidationModel(FlexoProject project, TargetType targetType) {
		super(project, targetType);
		registerRule(new FlexoComponentFolder.RootFolderMustHaveAPrefix());
		registerRule(new IEHyperlinkWidget.HyperlinkUrlMustHaveAnUrl());

		registerRule(new RequiredBindingValidationRule<IEEditableTextWidget>(IEEditableTextWidget.class, "bindingValue",
				"bindingValueDefinition"));
		registerRule(new RequiredBindingValidationRule<IEStringWidget>(IEStringWidget.class, "bindingValue", "bindingValueDefinition"));
		registerRule(new RequiredBindingValidationRule<RepetitionOperator>(RepetitionOperator.class, "bindingItem", "bindingItemDefinition"));
		registerRule(new IEAbstractListWidget.DropdownMustDefineABindingList());
		registerRule(new RequiredBindingValidationRule<IECheckBoxWidget>(IECheckBoxWidget.class, "bindingChecked",
				"bindingCheckedDefinition"));
		registerRule(new RequiredBindingValidationRule<ConditionalOperator>(ConditionalOperator.class, "bindingConditional",
				"bindingConditionalDefinition"));
		registerRule(new IEPopupComponent.PopupRequiresAConfirmOrCancelButtonRule());

		registerRule(new ComponentInstance.DefinedBindingsMustBeValid());
		registerRule(new ComponentInstance.MandatoryBindingsMustHaveAValue());

		registerRule(new IEAbstractListWidget.DropdownMustDefineABindingSelection());
		registerRule(new IEAbstractListWidget.DropDownWithKeyValueMustDefineADomain());
		registerRule(new IELabelWidget.LabelDontHaveDoubleDoubleDot());
		registerRule(new IELabelWidget.LabelHaveAtLeastOneDoubleDot());
		registerRule(new IELabelWidget.NoWhiteSpaceBeforeDoubleDot());
		registerRule(new IEButtonWidget.DateAssistantPopupMustBeLinkedWithADateTextField());
		registerRule(new IEButtonWidget.SearchButtonMustBeOfTypeSearch());
		registerRule(new IEButtonWidget.EmailButtonMustBeOfTypeEmail());
		registerRule(new FlexoItemMenu.RootItemMustBeBound());
		registerRule(new FlexoItemMenu.MenuMustDefineAnOperation());
		registerRule(new IEWOComponent.ComponentCannotHaveTwoListWithSameName());
		registerRule(new RepetitionOperator.ListMustHaveAName());
		registerRule(new RepetitionOperator.RawRowRepetitionCanNotContainEditableField());
		registerRule(new IETextFieldWidget.ExampleValueMustMatchFieldType());
		registerRule(new ComponentDefinition.BindingsMustDefineType());
		registerRule(new IEHeaderWidget.HeaderMustBeInATableContainingARepetition());
		registerRule(new IERadioButtonWidget.RadioButtonMustBeAllInTheSameRepetition());
		registerRule(new IERadioButtonWidget.RadioButtonReloadOnChange());
		registerRule(new IECheckBoxWidget.RadioButtonReloadOnChange());
		// Notify that the validation model is complete and that inheritance
		// computation could be performed
		update();
	}

	/**
	 * Return a boolean indicating if validation of supplied object must be notified
	 * 
	 * @param next
	 * @return a boolean
	 */
	@Override
	protected boolean shouldNotifyValidation(Validable next) {
		return next instanceof IEWOComponent;
	}

	/**
	 * Overrides fixAutomaticallyIfOneFixProposal
	 * 
	 * @see org.openflexo.foundation.validation.ValidationModel#fixAutomaticallyIfOneFixProposal()
	 */
	@Override
	public boolean fixAutomaticallyIfOneFixProposal() {
		return false;
	}
}
