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
package org.openflexo.ve.controller.action;

import java.awt.Dimension;
import java.awt.Font;
import java.awt.GridBagConstraints;
import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Icon;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.components.widget.FIBEditionPatternInstanceSelector;
import org.openflexo.components.widget.FIBIndividualSelector;
import org.openflexo.components.widget.FIBPropertySelector;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.model.BorderLayoutConstraints;
import org.openflexo.fib.model.BorderLayoutConstraints.BorderLayoutLocation;
import org.openflexo.fib.model.FIBButton;
import org.openflexo.fib.model.FIBCheckBox;
import org.openflexo.fib.model.FIBCheckboxList;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBComponent.HorizontalScrollBarPolicy;
import org.openflexo.fib.model.FIBComponent.VerticalScrollBarPolicy;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomAssignment;
import org.openflexo.fib.model.FIBDependancy;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBLabel.Align;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBNumber.NumberType;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Border;
import org.openflexo.fib.model.FIBPanel.FlowLayoutAlignment;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.GridBagLayoutConstraints;
import org.openflexo.fib.model.GridBagLayoutConstraints.AnchorType;
import org.openflexo.fib.model.GridBagLayoutConstraints.FillType;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyObjectProperty;
import org.openflexo.foundation.ontology.IFlexoOntologyStructuralProperty;
import org.openflexo.foundation.view.ModelSlotInstance;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.view.diagram.action.DropSchemeAction;
import org.openflexo.foundation.view.diagram.viewpoint.DiagramPaletteElement;
import org.openflexo.foundation.viewpoint.CheckboxParameter;
import org.openflexo.foundation.viewpoint.ClassParameter;
import org.openflexo.foundation.viewpoint.DataPropertyParameter;
import org.openflexo.foundation.viewpoint.EditionPatternInstanceParameter;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.foundation.viewpoint.FlexoObjectParameter;
import org.openflexo.foundation.viewpoint.IndividualParameter;
import org.openflexo.foundation.viewpoint.IntegerParameter;
import org.openflexo.foundation.viewpoint.ListParameter;
import org.openflexo.foundation.viewpoint.ListParameter.ListType;
import org.openflexo.foundation.viewpoint.ObjectPropertyParameter;
import org.openflexo.foundation.viewpoint.PropertyParameter;
import org.openflexo.foundation.viewpoint.TextAreaParameter;
import org.openflexo.foundation.viewpoint.TextFieldParameter;
import org.openflexo.foundation.viewpoint.URIParameter;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.FlexoFIBController;

public class ParametersRetriever /*implements BindingEvaluationContext*/{

	private static final Logger logger = Logger.getLogger(ParametersRetriever.class.getPackage().getName());

	private EditionSchemeAction<?, ?> action;

	protected DiagramPaletteElement paletteElement;

	public static boolean retrieveParameters(final EditionSchemeAction<?, ?> action, boolean skipDialogWhenPossible) {
		boolean successfullyRetrievedDefaultParameters = action.retrieveDefaultParameters();

		if (successfullyRetrievedDefaultParameters && action.getEditionScheme().getSkipConfirmationPanel() && skipDialogWhenPossible) {
			return true;
		}

		ParametersRetriever retriever = new ParametersRetriever(action);

		return retriever._retrieveParameters(action);
	}

	private ParametersRetriever(EditionSchemeAction<?, ?> action) {
		this.action = action;
	}

	public class URIPanel extends FIBPanel {
		public FIBTextField tf;
		public FIBLabel uriLabel;

		public URIPanel(final EditionSchemeParameter parameter) {
			super();
			setName(parameter.getName() + "URIPanel");
			setLayout(Layout.gridbag);
			tf = new FIBTextField();
			uriLabel = new FIBLabel("http://xxxxxx.owl");
			Font f = uriLabel.retrieveValidFont();
			if (f != null) {
				uriLabel.setFont(f.deriveFont(10f));
			}
			/*uriLabel.setData(new DataBinding('"' + action.getProject().getProjectOntology().getURI() + "#" + '"' + "+parameters."
					+ parameter.getName()) {
				@Override
				public BindingFactory getBindingFactory() {
					return parameter.getBindingFactory();
				}
			});*/
			/*uriLabel.setData(new DataBinding("data.parameters." + parameter.getName() + "+'a'") {
				@Override
				public BindingFactory getBindingFactory() {
					return parameter.getBindingFactory();
				}
			});*/
			uriLabel.setData(new DataBinding<Object>("data.project.projectOntology.URI" + "+'#'"));
			addToSubComponents(tf, new GridBagLayoutConstraints(GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE,
					GridBagConstraints.REMAINDER, 1, 1.0, 0, AnchorType.west, FillType.horizontal, 0, 0, 0, 0, 0, 0));
			addToSubComponents(uriLabel, new GridBagLayoutConstraints(GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE,
					GridBagConstraints.REMAINDER, 1, 1.0, 0, AnchorType.west, FillType.horizontal, -3, 0, 0, 0, 0, 0));
			tf.setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
		}
	}

	private FIBComponent makeWidget(final EditionSchemeParameter parameter, FIBPanel panel, int index) {
		if (parameter instanceof TextFieldParameter) {
			FIBTextField tf = new FIBTextField();
			tf.setName(parameter.getName() + "TextField");
			tf.setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
			panel.addToSubComponents(tf, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false), index);
			return tf;
		} else if (parameter instanceof URIParameter) {
			URIPanel uriPanel = new URIPanel(parameter);
			panel.addToSubComponents(uriPanel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false), index);
			return uriPanel;
		} else if (parameter instanceof TextAreaParameter) {
			FIBTextArea ta = new FIBTextArea();
			ta.setName(parameter.getName() + "TextArea");
			ta.setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
			ta.setValidateOnReturn(true); // Avoid too many ontologies manipulations
			ta.setUseScrollBar(true);
			ta.setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ta.setVerticalScrollbarPolicy(VerticalScrollBarPolicy.VERTICAL_SCROLLBAR_AS_NEEDED);
			panel.addToSubComponents(ta, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, true), index);
			return ta;
		} else if (parameter instanceof CheckboxParameter) {
			FIBCheckBox cb = new FIBCheckBox();
			cb.setName(parameter.getName() + "CheckBox");
			cb.setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
			panel.addToSubComponents(cb, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false), index);
			return cb;
		} else if (parameter instanceof IntegerParameter) {
			FIBNumber number = new FIBNumber();
			number.setName(parameter.getName() + "Number");
			number.setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
			number.setNumberType(NumberType.IntegerType);
			panel.addToSubComponents(number, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false), index);
			return number;
		} else if (parameter instanceof ListParameter) {
			ListParameter listParameter = (ListParameter) parameter;
			FIBCheckboxList cbList = new FIBCheckboxList();
			cbList.setName(parameter.getName() + "CheckboxList");
			cbList.setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
			// TODO: repair this !!!
			logger.warning("This feature is no more implemented, please repair this !!!");
			cbList.setList(new DataBinding<List<?>>("data.parameters." + parameter.getName() + "TODO"));
			if (listParameter.getListType() == ListType.ObjectProperty) {
				cbList.setIteratorClass(IFlexoOntologyObjectProperty.class);
				cbList.setFormat(new DataBinding<String>("object.name + \" (\"+object.domain.name+\")\""));
				cbList.setShowIcon(true);
				cbList.setIcon(new DataBinding<Icon>("controller.iconForObject(object)"));
				cbList.setVGap(-2);
			} else if (listParameter.getListType() == ListType.DataProperty) {
				cbList.setIteratorClass(IFlexoOntologyDataProperty.class);
				cbList.setFormat(new DataBinding<String>("object.name + \" (\"+object.domain.name+\")\""));
				cbList.setShowIcon(true);
				cbList.setIcon(new DataBinding<Icon>("controller.iconForObject(object)"));
				cbList.setVGap(-2);
			} else if (listParameter.getListType() == ListType.Property) {
				cbList.setIteratorClass(IFlexoOntologyStructuralProperty.class);
				cbList.setFormat(new DataBinding<String>("object.name + \" (\"+object.domain.name+\")\""));
				cbList.setShowIcon(true);
				cbList.setIcon(new DataBinding<Icon>("controller.iconForObject(object)"));
				cbList.setVGap(-2);
			}
			cbList.setUseScrollBar(true);
			cbList.setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			cbList.setVerticalScrollbarPolicy(VerticalScrollBarPolicy.VERTICAL_SCROLLBAR_AS_NEEDED);

			panel.addToSubComponents(cbList, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, true), index);
			return cbList;
		} else if (parameter instanceof FlexoObjectParameter) {
			FlexoObjectParameter foParameter = (FlexoObjectParameter) parameter;
			switch (foParameter.getFlexoObjectType()) {
			case Process:
				FIBCustom processSelector = new FIBCustom();
				processSelector.setComponentClass(org.openflexo.components.widget.FIBProcessSelector.class);
				processSelector.addToAssignments(new FIBCustomAssignment(processSelector, new DataBinding<Object>("component.project"),
						new DataBinding<Object>("data.project"), true));
				processSelector.setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
				panel.addToSubComponents(processSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false), index);
				return processSelector;
			case ProcessFolder:
				FIBCustom processFolderSelector = new FIBCustom();
				processFolderSelector.setComponentClass(org.openflexo.components.widget.FIBProcessFolderSelector.class);
				processFolderSelector.addToAssignments(new FIBCustomAssignment(processFolderSelector, new DataBinding<Object>(
						"component.project"), new DataBinding<Object>("data.project"), true));
				processFolderSelector.setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
				panel.addToSubComponents(processFolderSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false),
						index);
				return processFolderSelector;
			case Role:
				FIBCustom roleSelector = new FIBCustom();
				roleSelector.setComponentClass(org.openflexo.components.widget.FIBRoleSelector.class);
				roleSelector.addToAssignments(new FIBCustomAssignment(roleSelector, new DataBinding<Object>("component.project"),
						new DataBinding<Object>("data.project"), true));
				roleSelector.setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
				panel.addToSubComponents(roleSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false), index);
				return roleSelector;
			case Activity:
				FIBCustom activitySelector = new FIBCustom();
				activitySelector.setComponentClass(org.openflexo.components.widget.ActivitySelector.class);
				activitySelector.addToAssignments(new FIBCustomAssignment(activitySelector, new DataBinding<Object>("component.project"),
						new DataBinding<Object>("data.project"), true));
				activitySelector.setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
				panel.addToSubComponents(activitySelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false), index);
				return activitySelector;
			case Operation:
				FIBCustom operationSelector = new FIBCustom();
				operationSelector.setComponentClass(org.openflexo.components.widget.OperationSelector.class);
				operationSelector.addToAssignments(new FIBCustomAssignment(operationSelector, new DataBinding<Object>("component.project"),
						new DataBinding<Object>("data.project"), true));
				operationSelector.setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
				panel.addToSubComponents(operationSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false), index);
				return operationSelector;
			case Action:
				FIBCustom actionSelector = new FIBCustom();
				actionSelector.setComponentClass(org.openflexo.components.widget.ActionSelector.class);
				actionSelector.addToAssignments(new FIBCustomAssignment(actionSelector, new DataBinding<Object>("component.project"),
						new DataBinding<Object>("data.project"), true));
				actionSelector.setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
				panel.addToSubComponents(actionSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false), index);
				return actionSelector;

			default:
				break;
			}
		} else if (parameter instanceof EditionPatternInstanceParameter) {
			FIBCustom epiSelector = new FIBCustom();
			epiSelector.setComponentClass(FIBEditionPatternInstanceSelector.class);
			epiSelector.addToAssignments(new FIBCustomAssignment(epiSelector, new DataBinding<Object>("component.project"),
					new DataBinding<Object>("data.project"), true));
			epiSelector.addToAssignments(new FIBCustomAssignment(epiSelector, new DataBinding<Object>("component.view"),
					new DataBinding<Object>("data.view"), true));
			/*epiSelector.addToAssignments(new FIBCustomAssignment(epiSelector, new DataBinding<Object>("component.virtualModelInstance"),
					new DataBinding<Object>("data.virtualModelInstance"), true));*/
			epiSelector.addToAssignments(new FIBCustomAssignment(epiSelector, new DataBinding<Object>("component.viewPoint"),
					new DataBinding<Object>("data.editionPattern.viewPoint"), true));
			epiSelector.addToAssignments(new FIBCustomAssignment(epiSelector, new DataBinding<Object>("component.editionPattern"),
					new DataBinding<Object>("data.editionScheme.parameters." + parameter.getName() + ".editionPattern"), true));
			/*if (action.getVirtualModelInstance() != null) {
				ModelSlotInstance msInstance = action.getVirtualModelInstance().getModelSlotInstance(
						((IndividualParameter) parameter).getModelSlot());
				if (msInstance != null && msInstance.getModel() != null) {
					individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding(
							"component.contextOntologyURI"), new DataBinding<Object>('"' + msInstance.getModel().getURI() + '"'), true));
				} else {
					logger.warning("No model defined for model slot " + ((IndividualParameter) parameter).getModelSlot());
				}
			} else {
				logger.warning("Inconsistent data: no VirtualModelInstance for action " + action);
			}*/
			// Quick and dirty hack to configure IndividualSelector: refactor this when new binding model will be in use
			/*individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding<Object>("component.typeURI"),
					new DataBinding<Object>('"' + ((IndividualParameter) parameter)._getConceptURI() + '"'), true));
			if (StringUtils.isNotEmpty(((IndividualParameter) parameter).getRenderer())) {
				individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding<Object>(
						"component.renderer"), new DataBinding<Object>('"' + ((IndividualParameter) parameter).getRenderer() + '"'), true));
			}*/
			epiSelector.setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
			panel.addToSubComponents(epiSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false), index);
			return epiSelector;
		} else if (parameter instanceof IndividualParameter) {
			FIBCustom individualSelector = new FIBCustom();
			individualSelector.setComponentClass(FIBIndividualSelector.class);
			// Quick and dirty hack to configure ClassSelector: refactor this when new binding model will be in use
			// component.context = xxx
			/*individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding<Object>("component.project"),
					new DataBinding<Object>("data.project"), true));*/
			/*individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector,
					new DataBinding("component.contextOntologyURI"), new DataBinding('"' + parameter.getViewPoint().getViewpointOntology()
							.getURI() + '"') {
						@Override
						public BindingFactory getBindingFactory() {
							return parameter.getBindingFactory();
						}
					}, true));*/

			individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding<Object>(
					"component.informationSpace"), new DataBinding<Object>("data.project.informationSpace"), true));
			if (action.getVirtualModelInstance() != null) {
				ModelSlotInstance msInstance = action.getVirtualModelInstance().getModelSlotInstance(
						((IndividualParameter) parameter).getModelSlot());
				if (msInstance != null && msInstance.getModel() != null) {
					individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding(
							"component.contextOntologyURI"), new DataBinding<Object>('"' + msInstance.getModel().getURI() + '"'), true));
				} else {
					logger.warning("No model defined for model slot " + ((IndividualParameter) parameter).getModelSlot());
				}
			} else {
				logger.warning("Inconsistent data: no VirtualModelInstance for action " + action);
			}
			// Quick and dirty hack to configure IndividualSelector: refactor this when new binding model will be in use
			individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding<Object>("component.typeURI"),
					new DataBinding<Object>('"' + ((IndividualParameter) parameter)._getConceptURI() + '"'), true));
			if (StringUtils.isNotEmpty(((IndividualParameter) parameter).getRenderer())) {
				individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding<Object>(
						"component.renderer"), new DataBinding<Object>('"' + ((IndividualParameter) parameter).getRenderer() + '"'), true));
			}
			individualSelector.setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
			panel.addToSubComponents(individualSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false), index);
			return individualSelector;
		} else if (parameter instanceof ClassParameter) {
			ClassParameter classParameter = (ClassParameter) parameter;
			FIBCustom classSelector = new FIBCustom();
			classSelector.setComponentClass(org.openflexo.components.widget.FIBClassSelector.class);
			// Quick and dirty hack to configure ClassSelector: refactor this when new binding model will be in use
			// component.context = xxx
			/*classSelector.addToAssignments(new FIBCustomAssignment(classSelector, new DataBinding<Object>("component.project"),
					new DataBinding<Object>("data.project"), true));*/
			/*classSelector.addToAssignments(new FIBCustomAssignment(classSelector, new DataBinding("component.contextOntologyURI"),
					new DataBinding('"' + classParameter.getViewPoint().getViewpointOntology().getURI() + '"') {
						@Override
						public BindingFactory getBindingFactory() {
							return parameter.getBindingFactory();
						}
					}, true));*/
			classSelector.addToAssignments(new FIBCustomAssignment(classSelector, new DataBinding<Object>("component.informationSpace"),
					new DataBinding<Object>("data.project.informationSpace"), true));
			if (action.getVirtualModelInstance() != null) {
				ModelSlotInstance msInstance = action.getVirtualModelInstance().getModelSlotInstance(
						((IndividualParameter) parameter).getModelSlot());
				if (msInstance != null && msInstance.getModel() != null) {
					classSelector.addToAssignments(new FIBCustomAssignment(classSelector, new DataBinding("component.contextOntologyURI"),
							new DataBinding<Object>('"' + msInstance.getModel().getURI() + '"'), true));
				} else {
					logger.warning("No model defined for model slot " + ((IndividualParameter) parameter).getModelSlot());
				}
			} else {
				logger.warning("Inconsistent data: no VirtualModelInstance for action " + action);
			}
			// Quick and dirty hack to configure ClassSelector: refactor this when new binding model will be in use
			IFlexoOntologyClass conceptClass = null;
			if (classParameter.getIsDynamicConceptValue()) {
				conceptClass = classParameter.evaluateConceptValue(action);
			} else {
				conceptClass = classParameter.getConcept();
			}
			if (conceptClass != null) {
				classSelector.addToAssignments(new FIBCustomAssignment(classSelector, new DataBinding<Object>("component.rootClassURI"),
						new DataBinding<Object>('"' + conceptClass.getURI() + '"'), true));
			}
			classSelector.setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
			panel.addToSubComponents(classSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false), index);
			return classSelector;
		} else if (parameter instanceof PropertyParameter) {
			PropertyParameter propertyParameter = (PropertyParameter) parameter;
			FIBCustom propertySelector = new FIBCustom();
			propertySelector.setComponentClass(FIBPropertySelector.class);
			// Quick and dirty hack to configure FIBPropertySelector: refactor this when new binding model will be in use
			// component.context = xxx
			/*propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding<Object>("component.project"),
					new DataBinding<Object>("data.project"), true));*/
			/*propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding("component.contextOntologyURI"),
					new DataBinding('"' + propertyParameter.getViewPoint().getViewpointOntology().getURI() + '"') {
						@Override
						public BindingFactory getBindingFactory() {
							return parameter.getBindingFactory();
						}
					}, true));*/
			propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding<Object>(
					"component.informationSpace"), new DataBinding<Object>("data.project.informationSpace"), true));
			if (action.getVirtualModelInstance() != null) {
				ModelSlotInstance msInstance = action.getVirtualModelInstance().getModelSlotInstance(
						((IndividualParameter) parameter).getModelSlot());
				if (msInstance != null && msInstance.getModel() != null) {
					propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding(
							"component.contextOntologyURI"), new DataBinding<Object>('"' + msInstance.getModel().getURI() + '"'), true));
				} else {
					logger.warning("No model defined for model slot " + ((IndividualParameter) parameter).getModelSlot());
				}
			} else {
				logger.warning("Inconsistent data: no VirtualModelInstance for action " + action);
			}

			// Quick and dirty hack to configure PropertySelector: refactor this when new binding model will be in use
			IFlexoOntologyClass domainClass = null;
			if (propertyParameter.getIsDynamicDomainValue()) {
				domainClass = propertyParameter.evaluateDomainValue(action);
			} else {
				domainClass = propertyParameter.getDomain();
			}
			// System.out.println("domain class = " + domainClass + " uri=" + domainClass.getURI());
			if (domainClass != null) {
				propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding<Object>(
						"component.domainClassURI"), new DataBinding<Object>('"' + domainClass.getURI() + '"'), true));
			}

			if (propertyParameter instanceof ObjectPropertyParameter) {
				IFlexoOntologyClass rangeClass = null;
				if (propertyParameter.getIsDynamicDomainValue()) {
					rangeClass = ((ObjectPropertyParameter) propertyParameter).evaluateRangeValue(action);
				} else {
					rangeClass = ((ObjectPropertyParameter) propertyParameter).getRange();
				}
				// System.out.println("range class = " + rangeClass + " uri=" + rangeClass.getURI());
				if (rangeClass != null) {
					propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding<Object>(
							"component.rangeClassURI"), new DataBinding<Object>('"' + rangeClass.getURI() + '"'), true));
				}
			}

			if (propertyParameter instanceof ObjectPropertyParameter) {
				propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding<Object>(
						"component.selectDataProperties"), DataBinding.makeFalseBinding(), true));
			} else if (propertyParameter instanceof DataPropertyParameter) {
				propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding<Object>(
						"component.selectObjectProperties"), DataBinding.makeFalseBinding(), true));
			}

			propertySelector.setData(new DataBinding<Object>("data.parameters." + parameter.getName()));
			panel.addToSubComponents(propertySelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false), index);
			return propertySelector;
		}

		// Default
		FIBLabel unknown = new FIBLabel();
		unknown.setLabel("???");
		panel.addToSubComponents(unknown, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false), index);
		return unknown;
	}

	private FIBComponent makeFIB(final EditionSchemeAction<?, ?> action) {

		paletteElement = action instanceof DropSchemeAction ? ((DropSchemeAction) action).getPaletteElement() : null;
		final EditionScheme editionScheme = action.getEditionScheme();

		FIBPanel returned = new FIBPanel() {
			@Override
			protected void createDataBindingVariable() {
				_bindingModel.addToBindingVariables(new BindingVariable("data", action));
			}
			/*@Override
			protected void createBindingModel() {
				super.createBindingModel(action.getEditionScheme().getBindingModel());
				//_bindingModel.addToBindingVariables(new BindingVariable("parameters", new ParameterizedTypeImpl(List.class,
				//new WilcardTypeImpl(EditionSchemeParameter.class))));
				//_bindingModel.addToBindingVariables(new ResolvedEditionSchemeParameterListPathElement(action));
			}*/
		};
		returned.setBindingFactory(action.getEditionScheme().getBindingFactory());

		returned.setLayout(Layout.twocols);
		returned.setDataClass(action.getBaseClass());
		returned.setBorder(Border.empty);
		returned.setBorderTop(10);
		returned.setBorderBottom(5);
		returned.setBorderRight(10);
		returned.setBorderLeft(10);
		returned.setControllerClass(FlexoFIBController.class);

		if (editionScheme.getDefinePopupDefaultSize()) {
			returned.setMinWidth(editionScheme.getWidth());
			returned.setMinHeight(editionScheme.getHeight());
		}

		FIBLabel titleLabel = new FIBLabel();
		titleLabel.setAlign(Align.center);
		titleLabel.setLabel(FlexoLocalization.localizedForKey(editionScheme.getVirtualModel().getLocalizedDictionary(),
				editionScheme.getLabel() != null ? editionScheme.getLabel() : editionScheme.getName()));
		returned.addToSubComponents(titleLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false), 0);

		if (StringUtils.isNotEmpty(editionScheme.getDescription())) {
			FIBPanel descriptionPanel = new FIBPanel();
			descriptionPanel.setLayout(Layout.twocols);
			descriptionPanel.setBorder(Border.rounded3d);
			descriptionPanel.setLayout(Layout.border);
			descriptionPanel.setBorderTop(10);
			descriptionPanel.setBorderBottom(10);

			FIBLabel descriptionLabel = new FIBLabel();
			descriptionLabel.setAlign(Align.center);
			descriptionLabel.setLabel("<html><i>" + editionScheme.getDescription() + "</i></html>");
			descriptionPanel.addToSubComponents(descriptionLabel, new BorderLayoutConstraints(BorderLayoutLocation.center));
			returned.addToSubComponents(descriptionPanel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false), 1);
		} else {
			((TwoColsLayoutConstraints) titleLabel.getConstraints()).setInsetsBottom(10);
		}

		int index = 1;
		Hashtable<EditionSchemeParameter, FIBComponent> widgets = new Hashtable<EditionSchemeParameter, FIBComponent>();
		for (final EditionSchemeParameter parameter : editionScheme.getParameters()) {
			FIBLabel label = new FIBLabel();
			label.setLabel(parameter.getLabel());
			returned.addToSubComponents(label, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false), index++);
			FIBComponent widget = makeWidget(parameter, returned, index++);
			widgets.put(parameter, widget);
		}
		for (final EditionSchemeParameter parameter : editionScheme.getParameters()) {
			if (parameter instanceof URIParameter) {
				URIPanel uriPanel = (URIPanel) widgets.get(parameter);
				Vector<EditionSchemeParameter> dependancies = ((URIParameter) parameter).getDependancies();
				if (dependancies != null) {
					for (EditionSchemeParameter dep : dependancies) {
						FIBComponent dependingComponent = widgets.get(dep);
						uriPanel.tf.addToExplicitDependancies(new FIBDependancy(dependingComponent));
					}
				}
			}
		}

		FIBPanel buttonsPanel = new FIBPanel();

		buttonsPanel.setLayout(Layout.flow);
		buttonsPanel.setFlowAlignment(FlowLayoutAlignment.CENTER);
		buttonsPanel.setHGap(0);
		buttonsPanel.setVGap(5);
		buttonsPanel.setBorderTop(5);
		buttonsPanel.setBorder(Border.empty);
		FIBButton validateButton = new FIBButton();
		validateButton.setLabel("validate");
		validateButton.setLocalize(true);
		validateButton.setAction(new DataBinding("controller.validateAndDispose()"));
		buttonsPanel.addToSubComponents(validateButton);
		FIBButton cancelButton = new FIBButton();
		cancelButton.setLabel("cancel");
		cancelButton.setLocalize(true);
		cancelButton.setAction(new DataBinding("controller.cancelAndDispose()"));
		buttonsPanel.addToSubComponents(cancelButton);

		returned.addToSubComponents(buttonsPanel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false), index++);

		/*	try {
				logger.info("Getting this "
						+ XMLCoder.encodeObjectWithMapping(returned, FIBLibrary.getFIBMapping(), StringEncoder.getDefaultInstance()));
			} catch (InvalidObjectSpecificationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvalidModelException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (AccessorInvocationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (DuplicateSerializationIdentifierException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}*/

		return returned;
	}

	private boolean _retrieveParameters(final EditionSchemeAction<?, ?> action) {

		FIBComponent component = makeFIB(action);
		FIBDialog dialog = FIBDialog.instanciateDialog(component, action, null, true, FlexoLocalization.getMainLocalizer());
		if (!action.getEditionScheme().getDefinePopupDefaultSize()) {
			dialog.setMinimumSize(new Dimension(500, 50));
		}
		dialog.showDialog();
		return dialog.getStatus() == Status.VALIDATED;
	}

}
