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
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.components.widget.FIBIndividualSelector;
import org.openflexo.components.widget.FIBPropertySelector;
import org.openflexo.fib.controller.FIBController.Status;
import org.openflexo.fib.controller.FIBDialog;
import org.openflexo.fib.model.BorderLayoutConstraints;
import org.openflexo.fib.model.BorderLayoutConstraints.BorderLayoutLocation;
import org.openflexo.fib.model.DataBinding;
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
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.view.action.DropSchemeAction;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.CheckboxParameter;
import org.openflexo.foundation.viewpoint.ClassParameter;
import org.openflexo.foundation.viewpoint.DataPropertyParameter;
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
import org.openflexo.foundation.viewpoint.ViewPointPaletteElement;
import org.openflexo.foundation.viewpoint.binding.EditionSchemeParameterListPathElement;
import org.openflexo.foundation.viewpoint.binding.ListValueForListParameterPathElement;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.view.controller.FlexoFIBController;

public class ParametersRetriever /*implements BindingEvaluationContext*/{

	private static final Logger logger = Logger.getLogger(ParametersRetriever.class.getPackage().getName());

	private EditionSchemeAction<?> action;

	protected ViewPointPaletteElement paletteElement;

	public static boolean retrieveParameters(final EditionSchemeAction<?> action, boolean skipDialogWhenPossible) {
		boolean successfullyRetrievedDefaultParameters = action.retrieveDefaultParameters();

		if (successfullyRetrievedDefaultParameters && action.getEditionScheme().getSkipConfirmationPanel() && skipDialogWhenPossible) {
			return true;
		}

		ParametersRetriever retriever = new ParametersRetriever(action);

		return retriever._retrieveParameters2(action);
	}

	private ParametersRetriever(EditionSchemeAction<?> action) {
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
			uriLabel.setFont(f.deriveFont(10f));
			/*uriLabel.setData(new DataBinding('"' + action.getProject().getProjectOntology().getURI() + "#" + '"' + "+parameters."
					+ parameter.getName()) {
				@Override
				public BindingFactory getBindingFactory() {
					return parameter.getBindingFactory();
				}
			});*/
			/*uriLabel.setData(new DataBinding("parameters." + parameter.getName() + "+'a'") {
				@Override
				public BindingFactory getBindingFactory() {
					return parameter.getBindingFactory();
				}
			});*/
			uriLabel.setData(new DataBinding("data.project.projectOntology.URI" + "+'#'") {
				@Override
				public BindingFactory getBindingFactory() {
					return parameter.getBindingFactory();
				}
			});
			addToSubComponents(tf, new GridBagLayoutConstraints(0, GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE,
					GridBagConstraints.REMAINDER, 1, 1.0, 0, AnchorType.west, FillType.horizontal, 0, 0, 0, 0, 0, 0));
			addToSubComponents(uriLabel, new GridBagLayoutConstraints(1, GridBagConstraints.RELATIVE, GridBagConstraints.RELATIVE,
					GridBagConstraints.REMAINDER, 1, 1.0, 0, AnchorType.west, FillType.horizontal, -3, 0, 0, 0, 0, 0));
			tf.setData(new DataBinding("parameters." + parameter.getName()) {
				@Override
				public BindingFactory getBindingFactory() {
					return parameter.getBindingFactory();
				}
			});
		}
	}

	private FIBComponent makeWidget(final EditionSchemeParameter parameter, FIBPanel panel, int index) {
		if (parameter instanceof TextFieldParameter) {
			FIBTextField tf = new FIBTextField();
			tf.setName(parameter.getName() + "TextField");
			tf.setData(new DataBinding("parameters." + parameter.getName()) {
				@Override
				public BindingFactory getBindingFactory() {
					return parameter.getBindingFactory();
				}
			});
			panel.addToSubComponents(tf, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
			return tf;
		} else if (parameter instanceof URIParameter) {
			URIPanel uriPanel = new URIPanel(parameter);
			panel.addToSubComponents(uriPanel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
			return uriPanel;
		} else if (parameter instanceof TextAreaParameter) {
			FIBTextArea ta = new FIBTextArea();
			ta.setName(parameter.getName() + "TextArea");
			ta.setData(new DataBinding("parameters." + parameter.getName()) {
				@Override
				public BindingFactory getBindingFactory() {
					return parameter.getBindingFactory();
				}
			});
			ta.setValidateOnReturn(true); // Avoid too many ontologies manipulations
			ta.setUseScrollBar(true);
			ta.setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ta.setVerticalScrollbarPolicy(VerticalScrollBarPolicy.VERTICAL_SCROLLBAR_AS_NEEDED);
			panel.addToSubComponents(ta, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, true, index));
			return ta;
		} else if (parameter instanceof CheckboxParameter) {
			FIBCheckBox cb = new FIBCheckBox();
			cb.setName(parameter.getName() + "CheckBox");
			cb.setData(new DataBinding("parameters." + parameter.getName()) {
				@Override
				public BindingFactory getBindingFactory() {
					return parameter.getBindingFactory();
				}
			});
			panel.addToSubComponents(cb, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
			return cb;
		} else if (parameter instanceof IntegerParameter) {
			FIBNumber number = new FIBNumber();
			number.setName(parameter.getName() + "Number");
			number.setData(new DataBinding("parameters." + parameter.getName()) {
				@Override
				public BindingFactory getBindingFactory() {
					return parameter.getBindingFactory();
				}
			});
			number.setNumberType(NumberType.IntegerType);
			panel.addToSubComponents(number, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
			return number;
		} else if (parameter instanceof ListParameter) {
			ListParameter listParameter = (ListParameter) parameter;
			FIBCheckboxList cbList = new FIBCheckboxList();
			cbList.setName(parameter.getName() + "CheckboxList");
			cbList.setData(new DataBinding("parameters." + parameter.getName()) {
				@Override
				public BindingFactory getBindingFactory() {
					return parameter.getBindingFactory();
				}
			});
			cbList.setList(new DataBinding("parameters." + parameter.getName() + ListValueForListParameterPathElement.SUFFIX) {
				@Override
				public BindingFactory getBindingFactory() {
					return parameter.getBindingFactory();
				}
			});
			if (listParameter.getListType() == ListType.ObjectProperty) {
				cbList.setIteratorClass(OntologyObjectProperty.class);
				cbList.setFormat(new DataBinding("object.name + \" (\"+object.domain.name+\")\""));
				cbList.setShowIcon(true);
				cbList.setIcon(new DataBinding("controller.iconForObject(object)"));
				cbList.setVGap(-2);
			} else if (listParameter.getListType() == ListType.DataProperty) {
				cbList.setIteratorClass(OntologyDataProperty.class);
				cbList.setFormat(new DataBinding("object.name + \" (\"+object.domain.name+\")\""));
				cbList.setShowIcon(true);
				cbList.setIcon(new DataBinding("controller.iconForObject(object)"));
				cbList.setVGap(-2);
			} else if (listParameter.getListType() == ListType.Property) {
				cbList.setIteratorClass(OntologyProperty.class);
				cbList.setFormat(new DataBinding("object.name + \" (\"+object.domain.name+\")\""));
				cbList.setShowIcon(true);
				cbList.setIcon(new DataBinding("controller.iconForObject(object)"));
				cbList.setVGap(-2);
			}
			cbList.setUseScrollBar(true);
			cbList.setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			cbList.setVerticalScrollbarPolicy(VerticalScrollBarPolicy.VERTICAL_SCROLLBAR_AS_NEEDED);

			panel.addToSubComponents(cbList, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, true, index));
			return cbList;
		} else if (parameter instanceof FlexoObjectParameter) {
			FlexoObjectParameter foParameter = (FlexoObjectParameter) parameter;
			switch (foParameter.getFlexoObjectType()) {
			case Process:
				FIBCustom processSelector = new FIBCustom();
				processSelector.setComponentClass(org.openflexo.components.widget.FIBProcessSelector.class);
				processSelector.addToAssignments(new FIBCustomAssignment(processSelector, new DataBinding("component.project"),
						new DataBinding("data.project"), true));
				processSelector.setData(new DataBinding("parameters." + parameter.getName()) {
					@Override
					public BindingFactory getBindingFactory() {
						return parameter.getBindingFactory();
					}
				});
				panel.addToSubComponents(processSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
				return processSelector;
			case ProcessFolder:
				FIBCustom processFolderSelector = new FIBCustom();
				processFolderSelector.setComponentClass(org.openflexo.components.widget.FIBProcessFolderSelector.class);
				processFolderSelector.addToAssignments(new FIBCustomAssignment(processFolderSelector, new DataBinding("component.project"),
						new DataBinding("data.project"), true));
				processFolderSelector.setData(new DataBinding("parameters." + parameter.getName()) {
					@Override
					public BindingFactory getBindingFactory() {
						return parameter.getBindingFactory();
					}
				});
				panel.addToSubComponents(processFolderSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false,
						index));
				return processFolderSelector;
			case Role:
				FIBCustom roleSelector = new FIBCustom();
				roleSelector.setComponentClass(org.openflexo.components.widget.FIBRoleSelector.class);
				roleSelector.addToAssignments(new FIBCustomAssignment(roleSelector, new DataBinding("component.project"), new DataBinding(
						"data.project"), true));
				roleSelector.setData(new DataBinding("parameters." + parameter.getName()) {
					@Override
					public BindingFactory getBindingFactory() {
						return parameter.getBindingFactory();
					}
				});
				panel.addToSubComponents(roleSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
				return roleSelector;
			case Activity:
				FIBCustom activitySelector = new FIBCustom();
				activitySelector.setComponentClass(org.openflexo.components.widget.ActivitySelector.class);
				activitySelector.addToAssignments(new FIBCustomAssignment(activitySelector, new DataBinding("component.project"),
						new DataBinding("data.project"), true));
				activitySelector.setData(new DataBinding("parameters." + parameter.getName()) {
					@Override
					public BindingFactory getBindingFactory() {
						return parameter.getBindingFactory();
					}
				});
				panel.addToSubComponents(activitySelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
				return activitySelector;
			case Operation:
				FIBCustom operationSelector = new FIBCustom();
				operationSelector.setComponentClass(org.openflexo.components.widget.OperationSelector.class);
				operationSelector.addToAssignments(new FIBCustomAssignment(operationSelector, new DataBinding("component.project"),
						new DataBinding("data.project"), true));
				operationSelector.setData(new DataBinding("parameters." + parameter.getName()) {
					@Override
					public BindingFactory getBindingFactory() {
						return parameter.getBindingFactory();
					}
				});
				panel.addToSubComponents(operationSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
				return operationSelector;
			case Action:
				FIBCustom actionSelector = new FIBCustom();
				actionSelector.setComponentClass(org.openflexo.components.widget.ActionSelector.class);
				actionSelector.addToAssignments(new FIBCustomAssignment(actionSelector, new DataBinding("component.project"),
						new DataBinding("data.project"), true));
				actionSelector.setData(new DataBinding("parameters." + parameter.getName()) {
					@Override
					public BindingFactory getBindingFactory() {
						return parameter.getBindingFactory();
					}
				});
				panel.addToSubComponents(actionSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
				return actionSelector;

			default:
				break;
			}
		} else if (parameter instanceof IndividualParameter) {
			FIBCustom individualSelector = new FIBCustom();
			individualSelector.setComponentClass(FIBIndividualSelector.class);
			// Quick and dirty hack to configure ClassSelector: refactor this when new binding model will be in use
			// component.context = xxx
			individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding("component.project"),
					new DataBinding("data.project"), true));
			/*individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector,
					new DataBinding("component.contextOntologyURI"), new DataBinding('"' + parameter.getViewPoint().getViewpointOntology()
							.getURI() + '"') {
						@Override
						public BindingFactory getBindingFactory() {
							return parameter.getBindingFactory();
						}
					}, true));*/
			// Quick and dirty hack to configure IndividualSelector: refactor this when new binding model will be in use
			individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding("component.typeURI"),
					new DataBinding('"' + ((IndividualParameter) parameter)._getConceptURI() + '"') {
						@Override
						public BindingFactory getBindingFactory() {
							return parameter.getBindingFactory();
						}
					}, true));
			if (StringUtils.isNotEmpty(((IndividualParameter) parameter).getRenderer())) {
				individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding("component.renderer"),
						new DataBinding('"' + ((IndividualParameter) parameter).getRenderer() + '"') {
							@Override
							public BindingFactory getBindingFactory() {
								return parameter.getBindingFactory();
							}
						}, true));
			}
			individualSelector.setData(new DataBinding("parameters." + parameter.getName()) {
				@Override
				public BindingFactory getBindingFactory() {
					return parameter.getBindingFactory();
				}
			});
			panel.addToSubComponents(individualSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
			return individualSelector;
		} else if (parameter instanceof ClassParameter) {
			ClassParameter classParameter = (ClassParameter) parameter;
			FIBCustom classSelector = new FIBCustom();
			classSelector.setComponentClass(org.openflexo.components.widget.FIBClassSelector.class);
			// Quick and dirty hack to configure ClassSelector: refactor this when new binding model will be in use
			// component.context = xxx
			classSelector.addToAssignments(new FIBCustomAssignment(classSelector, new DataBinding("component.project"), new DataBinding(
					"data.project"), true));
			/*classSelector.addToAssignments(new FIBCustomAssignment(classSelector, new DataBinding("component.contextOntologyURI"),
					new DataBinding('"' + classParameter.getViewPoint().getViewpointOntology().getURI() + '"') {
						@Override
						public BindingFactory getBindingFactory() {
							return parameter.getBindingFactory();
						}
					}, true));*/
			// Quick and dirty hack to configure ClassSelector: refactor this when new binding model will be in use
			OntologyClass conceptClass = null;
			if (classParameter.getIsDynamicConceptValue()) {
				conceptClass = classParameter.evaluateConceptValue(action);
			} else {
				conceptClass = classParameter.getConcept();
			}
			if (conceptClass != null) {
				classSelector.addToAssignments(new FIBCustomAssignment(classSelector, new DataBinding("component.rootClassURI"),
						new DataBinding('"' + conceptClass.getURI() + '"') {
							@Override
							public BindingFactory getBindingFactory() {
								return parameter.getBindingFactory();
							}
						}, true));
			}
			classSelector.setData(new DataBinding("parameters." + parameter.getName()) {
				@Override
				public BindingFactory getBindingFactory() {
					return parameter.getBindingFactory();
				}
			});
			panel.addToSubComponents(classSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
			return classSelector;
		} else if (parameter instanceof PropertyParameter) {
			PropertyParameter propertyParameter = (PropertyParameter) parameter;
			FIBCustom propertySelector = new FIBCustom();
			propertySelector.setComponentClass(FIBPropertySelector.class);
			// Quick and dirty hack to configure FIBPropertySelector: refactor this when new binding model will be in use
			// component.context = xxx
			propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding("component.project"),
					new DataBinding("data.project"), true));
			/*propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding("component.contextOntologyURI"),
					new DataBinding('"' + propertyParameter.getViewPoint().getViewpointOntology().getURI() + '"') {
						@Override
						public BindingFactory getBindingFactory() {
							return parameter.getBindingFactory();
						}
					}, true));*/

			// Quick and dirty hack to configure PropertySelector: refactor this when new binding model will be in use
			OntologyClass domainClass = null;
			if (propertyParameter.getIsDynamicDomainValue()) {
				domainClass = propertyParameter.evaluateDomainValue(action);
			} else {
				domainClass = propertyParameter.getDomain();
			}
			// System.out.println("domain class = " + domainClass + " uri=" + domainClass.getURI());
			if (domainClass != null) {
				propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding("component.domainClassURI"),
						new DataBinding('"' + domainClass.getURI() + '"') {
							@Override
							public BindingFactory getBindingFactory() {
								return parameter.getBindingFactory();
							}
						}, true));
			}

			if (propertyParameter instanceof ObjectPropertyParameter) {
				OntologyClass rangeClass = null;
				if (propertyParameter.getIsDynamicDomainValue()) {
					rangeClass = ((ObjectPropertyParameter) propertyParameter).evaluateRangeValue(action);
				} else {
					rangeClass = ((ObjectPropertyParameter) propertyParameter).getRange();
				}
				// System.out.println("range class = " + rangeClass + " uri=" + rangeClass.getURI());
				if (rangeClass != null) {
					propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding("component.rangeClassURI"),
							new DataBinding('"' + rangeClass.getURI() + '"') {
								@Override
								public BindingFactory getBindingFactory() {
									return parameter.getBindingFactory();
								}
							}, true));
				}
			}

			if (propertyParameter instanceof ObjectPropertyParameter) {
				propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding(
						"component.selectDataProperties"), new DataBinding("false"), true));
			} else if (propertyParameter instanceof DataPropertyParameter) {
				propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding(
						"component.selectObjectProperties"), new DataBinding("false"), true));
			}

			propertySelector.setData(new DataBinding("parameters." + parameter.getName()) {
				@Override
				public BindingFactory getBindingFactory() {
					return parameter.getBindingFactory();
				}
			});
			panel.addToSubComponents(propertySelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
			return propertySelector;
		}

		// Default
		FIBLabel unknown = new FIBLabel();
		unknown.setLabel("???");
		panel.addToSubComponents(unknown, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
		return unknown;
	}

	private FIBComponent makeFIB(final EditionSchemeAction<?> action) {

		paletteElement = action instanceof DropSchemeAction ? ((DropSchemeAction) action).getPaletteElement() : null;
		final EditionScheme editionScheme = action.getEditionScheme();

		FIBPanel returned = new FIBPanel() {
			@Override
			protected void createBindingModel() {
				super.createBindingModel();
				_bindingModel.addToBindingVariables(new ResolvedEditionSchemeParameterListPathElement(action));
			}
		};
		returned.setLayout(Layout.twocols);
		returned.setDataClass(EditionSchemeAction.class);
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

		Font f = returned.retrieveValidFont();
		returned.setFont(f.deriveFont(11f));

		FIBLabel titleLabel = new FIBLabel();
		titleLabel.setFont(titleLabel.retrieveValidFont().deriveFont(Font.BOLD, 13f));
		titleLabel.setAlign(Align.center);
		titleLabel.setLabel(editionScheme.getLabel());
		returned.addToSubComponents(titleLabel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false, 0));

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
			returned.addToSubComponents(descriptionPanel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false, 0));
		} else {
			((TwoColsLayoutConstraints) titleLabel.getConstraints()).setInsetsBottom(10);
		}

		int index = 1;
		Hashtable<EditionSchemeParameter, FIBComponent> widgets = new Hashtable<EditionSchemeParameter, FIBComponent>();
		for (final EditionSchemeParameter parameter : editionScheme.getParameters()) {
			FIBLabel label = new FIBLabel();
			label.setLabel(parameter.getLabel());
			returned.addToSubComponents(label, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false, index++));
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
		buttonsPanel.setFont(f.deriveFont(13f));
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

		returned.addToSubComponents(buttonsPanel, new TwoColsLayoutConstraints(TwoColsLayoutLocation.center, true, false, index++));

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

	private boolean _retrieveParameters2(final EditionSchemeAction<?> action) {

		FIBComponent component = makeFIB(action);
		FIBDialog dialog = FIBDialog.instanciateDialog(component, action, null, true, FlexoLocalization.getMainLocalizer());
		if (!action.getEditionScheme().getDefinePopupDefaultSize()) {
			dialog.setMinimumSize(new Dimension(500, 50));
		}
		dialog.showDialog();
		return dialog.getStatus() == Status.VALIDATED;
	}

	/*	private boolean _retrieveParameters(final EditionSchemeAction<?> action) {
			paletteElement = (action instanceof DropSchemeAction ? ((DropSchemeAction) action).getPaletteElement() : null);
			EditionScheme editionScheme = action.getEditionScheme();

			Vector<ParameterDefinition> parameters = new Vector<ParameterDefinition>();

			uriParametersList = new Vector<URIParameter>();
			paramHash = new Hashtable<EditionSchemeParameter, ParameterDefinition>();

			String description = editionScheme.getDescription();
			if (description == null) {
				description = editionScheme.getEditionPattern().getDescription();
			}

			parameters.add(new InfoLabelParameter("infoLabel", "description", description));

			for (final EditionSchemeParameter parameter : editionScheme.getParameters()) {
				ParameterDefinition param = null;
				Object defaultValue = parameter.getDefaultValue(action, this);
				if (parameter instanceof org.openflexo.foundation.viewpoint.URIParameter) {
					param = new URIParameter((org.openflexo.foundation.viewpoint.URIParameter) parameter, action);
					uriParametersList.add((URIParameter) param);
					if (defaultValue != null) {
						action.getParameterValues().put(parameter.getName(), defaultValue);
					}
				} else if (parameter instanceof org.openflexo.foundation.viewpoint.TextFieldParameter) {
					param = new TextFieldParameter(parameter.getName(), parameter.getLabel(), (String) defaultValue, 40) {
						@Override
						public void setValue(String value) {
							super.setValue(value);
							action.getParameterValues().put(parameter.getName(), value);
							for (URIParameter p : uriParametersList) {
								if (p.getDependancyParameters() != null && p.getDependancyParameters().contains(this)) {
									p.updateURIName();
								}
							}
						}
					};
					if (defaultValue != null) {
						action.getParameterValues().put(parameter.getName(), defaultValue);
					}
				} else if (parameter.getWidget() == WidgetType.LOCALIZED_TEXT_FIELD) {
					LocalizedString ls = new LocalizedString((String) defaultValue, Language.ENGLISH);
					param = new LocalizedTextFieldParameter(parameter.getName(), parameter.getLabel(), ls, 40) {
						@Override
						public void setValue(LocalizedString value) {
							super.setValue(value);
							action.getParameterValues().put(parameter.getName(), value);
						}
					};
					action.getParameterValues().put(parameter.getName(), ls);
				} else if (parameter instanceof org.openflexo.foundation.viewpoint.TextAreaParameter) {
					param = new TextAreaParameter(parameter.getName(), parameter.getLabel(), (String) defaultValue, 40, 5) {
						@Override
						public void setValue(String value) {
							super.setValue(value);
							action.getParameterValues().put(parameter.getName(), value);
						}
					};
					if (defaultValue != null) {
						action.getParameterValues().put(parameter.getName(), defaultValue);
					}
				} else if (parameter instanceof org.openflexo.foundation.viewpoint.IntegerParameter) {
					param = new IntegerParameter(parameter.getName(), parameter.getLabel(), ((Number) defaultValue).intValue()) {
						@Override
						public void setValue(Integer value) {
							super.setValue(value);
							action.getParameterValues().put(parameter.getName(), value);
							for (URIParameter p : uriParametersList) {
								if (p.getDependancyParameters() != null && p.getDependancyParameters().contains(this)) {
									p.updateURIName();
								}
							}
						}
					};
					if (defaultValue != null) {
						action.getParameterValues().put(parameter.getName(), defaultValue);
					}
				} else if (parameter instanceof org.openflexo.foundation.viewpoint.CheckboxParameter) {
					param = new CheckboxParameter(parameter.getName(), parameter.getLabel(), (Boolean) defaultValue) {
						@Override
						public void setValue(Boolean value) {
							super.setValue(value);
							action.getParameterValues().put(parameter.getName(), value);
						}
					};
					if (defaultValue != null) {
						action.getParameterValues().put(parameter.getName(), defaultValue);
					}
				} else if (parameter instanceof DropDownParameter) {
					param = new StaticDropDownParameter<String>(parameter.getName(), parameter.getLabel(),
							((DropDownParameter) parameter).getValueList(), parameter.getDefaultValue().toString()) {
						@Override
						public void setValue(String value) {
							super.setValue(value);
							action.getParameterValues().put(parameter.getName(), value);
						}
					};
					if (defaultValue != null) {
						action.getParameterValues().put(parameter.getName(), defaultValue);
					}
				} else if (parameter instanceof IndividualParameter) {
					OntologyClass ontologyClass = ((IndividualParameter) parameter).getConcept();
					OntologyIndividual defaultIndividual = null;
					if (ontologyClass != null && ontologyClass.getIndividuals().size() > 0) {
						defaultIndividual = ontologyClass.getIndividuals().firstElement();
					}
					param = new OntologyIndividualParameter(parameter.getName(), parameter.getLabel(), ontologyClass, defaultIndividual) {
						@Override
						public void setValue(OntologyIndividual value) {
							super.setValue(value);
							logger.info("Set as parameter " + parameter.getName() + " value=" + value);
							action.getParameterValues().put(parameter.getName(), value);
						};
					};
					if (defaultIndividual != null) {
						logger.info("Set as parameter " + parameter.getName() + " value=" + defaultIndividual);
						action.getParameterValues().put(parameter.getName(), defaultIndividual);
					}
				} else if (parameter instanceof FlexoObjectParameter) {
					if (((FlexoObjectParameter) parameter).getFlexoObjectType() == FlexoObjectType.Role) {
						param = new RoleParameter(parameter.getName(), parameter.getLabel(), null) {
							@Override
							public void setValue(Role value) {
								super.setValue(value);
								System.out.println("Set as parameter " + parameter.getName() + " value=" + value);
								action.getParameterValues().put(parameter.getName(), value);
							};
						};

					}
				}

				if (param != null) {
					parameters.add(param);
					paramHash.put(parameter, param);
				}
			}

			for (URIParameter p : uriParametersList) {
				p.buildDependancies();
				p.updateURIName();
			}

			AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(action.getProject(), editionScheme.getEditionPattern()
					.getName(), "", // No need for extra title
					new ValidationCondition() {
						@Override
						public boolean isValid(ParametersModel model) {
							for (URIParameter param : uriParametersList) {
								if (param._parameter.evaluateCondition(action)) {
									if (StringUtils.isEmpty(param.getValue())) {
										setErrorMessage(FlexoLocalization.localizedForKey("declared_uri_must_be_specified_please_enter_uri"));
										return false;
									}
									if (action.getProject().getProjectOntologyLibrary()
											.isDuplicatedURI(action.getProject().getProjectOntology().getURI(), param.getValue())) {
										setErrorMessage(FlexoLocalization
												.localizedForKey("declared_uri_must_be_unique_please_choose_an_other_uri"));
										return false;
									} else if (!action.getProject().getProjectOntologyLibrary()
											.testValidURI(action.getProject().getProjectOntology().getURI(), param.getValue())) {
										setErrorMessage(FlexoLocalization
												.localizedForKey("declared_uri_is_not_well_formed_please_choose_an_other_uri"));
										return false;
									}
								}
							}
							return true;
						}
					}, parameters);

			if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
				return true;
			} else {
				return false;
			}

		}*/

	/*public class URIParameter extends TextFieldAndLabelParameter {

		private org.openflexo.foundation.viewpoint.URIParameter _parameter;
		private EditionSchemeAction<?> _action;
		private Expression baseExpression;
		private Vector<ParameterDefinition> dependancyParameters;

		public URIParameter(org.openflexo.foundation.viewpoint.URIParameter parameter, EditionSchemeAction action) {
			super(parameter.getName(), parameter.getLabel(), "<not set yet>", 40);
			_parameter = parameter;
			_action = action;
			buildDependancies();
		}

		protected void buildDependancies() {
			if (_parameter.getBaseURI() != null && _parameter.getBaseURI().isValid()) {
				DefaultExpressionParser parser = new DefaultExpressionParser();
				try {
					baseExpression = parser.parse(_parameter.getBaseURI().toString());
					dependancyParameters = new Vector<ParameterDefinition>();
					Vector<EditionSchemeParameter> depends = extractDepends(_parameter.getBaseURI().toString(), _action);
					for (EditionSchemeParameter p : depends) {
						if (p != null) {
							dependancyParameters.add(paramHash.get(p));
							// System.out.println("Param URI "+parameter+" depends of "+paramHash.get(p)+" p="+p);
						}
					}
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}

		@Override
		public void setValue(String value) {
			super.setValue(value);
			if (value != null) {
				_action.getParameterValues().put(_parameter.getName(), value);
			}
			// System.out.println("Param URI "+_parameter+" (name="+_parameter.getName()+" takes value "+value);
		}

		public Vector<ParameterDefinition> getDependancyParameters() {
			return dependancyParameters;
		}

		public void updateURIName() {
			String baseProposal = getURIName();
			String proposal = baseProposal;
			if (!StringUtils.isEmpty(baseProposal)) {
				Integer i = null;
				while (_action.getProject().getProjectOntologyLibrary()
						.isDuplicatedURI(_action.getProject().getProjectOntology().getURI(), proposal)) {
					if (i == null) {
						i = 1;
					} else {
						i++;
					}
					proposal = baseProposal + i;
				}
			}
			setValue(proposal);
		}

		@Override
		public String getAdditionalLabel() {
			return getURI();
		}

		public String getURI() {
			return _action.getProject().getProjectOntology().makeURI(getURIName());
		}

		public String getURIName() {
			if (baseExpression == null) {
				return (String) _parameter.getDefaultValue(_action, ParametersRetriever.this);
			}
			Hashtable<String, Object> paramValues = new Hashtable<String, Object>();
			for (String s : _action.getParameterValues().keySet()) {
				String value = _action.getParameterValues().get(s).toString();
				paramValues.put(s, JavaUtils.getClassName(value));
			}
			return evaluateExpression(paramValues);
		}

		private String evaluateExpression(final Hashtable<String, Object> parameterValues) {
			if (baseExpression == null) {
				return "";
			}
			try {
				Expression evaluation = baseExpression.evaluate(parameterValues);
				if (evaluation instanceof StringConstant) {
					return ((StringConstant) evaluation).getValue();
				}
				logger.warning("Inconsistant data: expected StringConstant, found " + evaluation.getClass().getName());
			} catch (TypeMismatchException e) {
				e.printStackTrace();
			}
			return "";
		}

	}

	private String getDepends(String anExpression, final EditionSchemeAction action) {
		String depends = "";
		boolean isFirst = true;
		for (final EditionSchemeParameter parameter : extractDepends(anExpression, action)) {
			depends += (isFirst ? "" : ",") + parameter.getName();
			isFirst = false;
		}
		return depends;
	}

	Vector<EditionSchemeParameter> extractDepends(String anExpression, final EditionSchemeAction action) {
		EditionScheme editionScheme = action.getEditionScheme();
		Vector<EditionSchemeParameter> returned = new Vector<EditionSchemeParameter>();
		try {
			System.out.println("Expression: " + anExpression);
			Vector<Variable> variables = Expression.extractVariables(anExpression);
			for (Variable v : variables) {
				returned.add(editionScheme.getParameter(v.getName()));
				System.out.println("depends: " + v.getName() + " param: " + editionScheme.getParameter(v.getName()));
			}
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (TypeMismatchException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return returned;
	}
	*/
	/*@Override
	public Object getValue(BindingVariable variable) {
		if (variable instanceof EditionSchemeParameterPathElement) {
			System.out.println("OK, on me demande la valeur pour " + variable);
			return action.getParameterValue(((EditionSchemeParameterPathElement) variable).getParameter());

			// return paramHash.get(((EditionSchemeParameterPathElement) variable).getParameter()).getValue();
		}
		logger.warning("Unexpected " + variable);
		return null;
	}*/

	protected class ResolvedEditionSchemeParameterListPathElement extends EditionSchemeParameterListPathElement {
		public ResolvedEditionSchemeParameterListPathElement(EditionSchemeAction action) {
			super(action.getEditionScheme(), null);
		}

		@Override
		public EditionSchemeAction<?> getBindingValue(Object target, BindingEvaluationContext context) {
			return action;
		}
	}

}
