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
package org.openflexo.inspector;

import java.util.HashSet;
import java.util.Hashtable;
import java.util.Set;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.components.widget.FIBIndividualSelector;
import org.openflexo.components.widget.FIBPropertySelector;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.FIBCheckBox;
import org.openflexo.fib.model.FIBCustom;
import org.openflexo.fib.model.FIBCustom.FIBCustomAssignment;
import org.openflexo.fib.model.FIBLabel;
import org.openflexo.fib.model.FIBNumber;
import org.openflexo.fib.model.FIBNumber.NumberType;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBTab;
import org.openflexo.fib.model.FIBTabPanel;
import org.openflexo.fib.model.FIBTextArea;
import org.openflexo.fib.model.FIBTextField;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints.TwoColsLayoutLocation;
import org.openflexo.foundation.FlexoProjectObject;
import org.openflexo.foundation.ontology.IFlexoOntologyClass;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.LocalizedDictionary;
import org.openflexo.foundation.viewpoint.binding.EditionPatternInstanceBindingVariable;
import org.openflexo.foundation.viewpoint.inspector.CheckboxInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.ClassInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.DataPropertyInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.FlexoObjectInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.IndividualInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.InspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.IntegerInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.ObjectPropertyInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.PropertyInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.TextAreaInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.TextFieldInspectorEntry;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.StringUtils;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.Cloner;
import org.openflexo.xmlcode.DuplicateSerializationIdentifierException;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;

/**
 * Represent a FIBComponent used as an inspector for a particular class instance
 * 
 * @author sylvain
 * 
 */
public class FIBInspector extends FIBPanel {

	static final Logger logger = Logger.getLogger(FIBInspector.class.getPackage().getName());

	private boolean superInspectorWereAppened = false;

	private FIBInspector superInspector;

	private Vector<EditionPattern> currentEditionPatterns = new Vector<EditionPattern>();
	private Hashtable<EditionPattern, FIBTab> tabsForEP = new Hashtable<EditionPattern, FIBTab>();

	public FIBInspector getSuperInspector() {
		return superInspector;
	}

	protected void appendSuperInspectors(ModuleInspectorController inspectorController) {
		if (getDataType() == null) {
			return;
		}
		if (getDataType() instanceof Class) {
			FIBInspector superInspector = inspectorController.inspectorForClass(((Class) getDataType()).getSuperclass());
			if (superInspector != null) {
				superInspector.appendSuperInspectors(inspectorController);
				this.superInspector = superInspector;
				appendSuperInspector(superInspector);
			}
		}
	}

	@Override
	public String toString() {
		return "Inspector[" + getDataType() + "]";
	}

	protected void appendSuperInspector(FIBInspector superInspector) {
		if (!superInspectorWereAppened) {
			// logger.info("> Append " + superInspector + " to " + this);
			/*try {
				System.out.println("Clone container:\n"+XMLCoder.encodeObjectWithMapping(superInspector, FIBLibrary.getFIBMapping(),StringEncoder.getDefaultInstance()));
				System.out.println("Found this:\n"+XMLCoder.encodeObjectWithMapping((XMLSerializable)Cloner.cloneObjectWithMapping(superInspector, FIBLibrary.getFIBMapping()), FIBLibrary.getFIBMapping(),StringEncoder.getDefaultInstance()));
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
			append((FIBPanel) Cloner.cloneObjectWithMapping(superInspector, FIBLibrary.getFIBMapping()));
			superInspectorWereAppened = true;
			// logger.info("< Appened " + superInspector + " to " + this);
		}
	}

	public FIBTabPanel getTabPanel() {
		return (FIBTabPanel) getSubComponents().firstElement();
	}

	public String getXMLRepresentation() {
		try {
			return XMLCoder.encodeObjectWithMapping(this, FIBLibrary.getFIBMapping(), StringEncoder.getDefaultInstance());
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
		}
		return "Error ???";
	}

	private boolean ensureCreationOfTabForEP(EditionPattern ep) {
		FIBTab returned = tabsForEP.get(ep);
		if (returned == null) {
			// System.out.println("Creating FIBTab for " + ep);
			returned = makeFIBTab(ep);
			tabsForEP.put(ep, returned);
			getTabPanel().addToSubComponents(returned, null, 0);
			return true;
		}
		return false;
	}

	/**
	 * This method looks after object's EditionPattern references to know if we need to structurally change inspector by adding or removing
	 * tabs, which all correspond to one and only one EditionPattern
	 * 
	 * Note: only object providing support as primary role are handled here
	 * 
	 * @param object
	 * @return a boolean indicating if a new tab was created
	 */
	protected boolean updateEditionPatternReferences(FlexoProjectObject object) {

		boolean returned = false;

		currentEditionPatterns.clear();

		Set<EditionPattern> editionPatternsToDisplay = new HashSet<EditionPattern>();

		for (EditionPattern ep : tabsForEP.keySet()) {
			if (object.getEditionPatternInstance(ep) == null) {
				tabsForEP.get(ep).setVisible(DataBinding.makeFalseBinding());
			}
		}

		if (object.getEditionPatternReferences() != null) {
			for (FlexoModelObjectReference<EditionPatternInstance> ref : object.getEditionPatternReferences()) {
				EditionPatternInstance epi = ref.getObject();
				editionPatternsToDisplay.add(epi.getEditionPattern());
				if (ensureCreationOfTabForEP(epi.getEditionPattern())) {
					returned = true;
				}
				FIBTab tab = tabsForEP.get(epi.getEditionPattern());
				tab.setVisible(DataBinding.makeTrueBinding());
				currentEditionPatterns.add(epi.getEditionPattern());
			}
			updateBindingModel();
		}

		/*for (FIBComponent c : getTabPanel().getSubComponents()) {
			System.out.println("> Tab: " + c + " visible=" + c.getVisible());
			if (StringUtils.isNotEmpty(c.getVisible().toString())) {
				FIBWidget w = (FIBWidget) ((FIBContainer) c).getSubComponents().get(1);
				try {
					logger.info("Getting this "
							+ XMLCoder.encodeObjectWithMapping(w, FIBLibrary.getFIBMapping(), StringEncoder.getDefaultInstance()));
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
				}
				System.out.println("data=" + w.getData());
				BindingValue v = (BindingValue) w.getData().getBinding();
				System.out.println("bv=" + v);
				System.out.println("0:" + v.getBindingPathElementAtIndex(0) + " of " + v.getBindingPathElementAtIndex(0).getClass());
				System.out.println("1:" + v.getBindingPathElementAtIndex(1) + " of " + v.getBindingPathElementAtIndex(1).getClass());
			}
		}*/

		return returned;
	}

	@Override
	protected void createBindingModel() {
		super.createBindingModel();
		for (int i = 0; i < currentEditionPatterns.size(); i++) {
			EditionPattern ep = currentEditionPatterns.get(i);
			_bindingModel.addToBindingVariables(new EditionPatternInstanceBindingVariable(ep, i));
		}
	}

	private FIBWidget makeWidget(final InspectorEntry entry, FIBTab newTab) {
		if (entry instanceof TextFieldInspectorEntry) {
			FIBTextField tf = new FIBTextField();
			tf.setValidateOnReturn(true); // Avoid to many ontologies manipulations
			newTab.addToSubComponents(tf, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
			return tf;
		} else if (entry instanceof TextAreaInspectorEntry) {
			FIBTextArea ta = new FIBTextArea();
			ta.setValidateOnReturn(true); // Avoid to many ontologies manipulations
			ta.setUseScrollBar(true);
			ta.setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ta.setVerticalScrollbarPolicy(VerticalScrollBarPolicy.VERTICAL_SCROLLBAR_AS_NEEDED);
			newTab.addToSubComponents(ta, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, true));
			return ta;
		} else if (entry instanceof CheckboxInspectorEntry) {
			FIBCheckBox cb = new FIBCheckBox();
			newTab.addToSubComponents(cb, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
			return cb;
		} else if (entry instanceof IntegerInspectorEntry) {
			FIBNumber number = new FIBNumber();
			number.setNumberType(NumberType.IntegerType);
			newTab.addToSubComponents(number, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
			return number;
		} else if (entry instanceof IndividualInspectorEntry) {
			IndividualInspectorEntry individualEntry = (IndividualInspectorEntry) entry;
			FIBCustom individualSelector = new FIBCustom();
			individualSelector.setComponentClass(FIBIndividualSelector.class);
			// Quick and dirty hack to configure ClassSelector: refactor this when new binding model will be in use
			// component.context = xxx
			individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding("component.project"),
					new DataBinding("data.project"), true));
			/*individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector,
					new DataBinding("component.contextOntologyURI"), new DataBinding('"' + individualEntry.getViewPoint()
							.getViewpointOntology().getURI() + '"') {
						@Override
						public BindingFactory getBindingFactory() {
							return entry.getBindingFactory();
						}
					}, true));*/
			// Quick and dirty hack to configure IndividualSelector: refactor this when new binding model will be in use
			IFlexoOntologyClass conceptClass = null;
			if (individualEntry.getIsDynamicConceptValue()) {
				// conceptClass = classEntry.evaluateConceptValue(action);
				// TODO: implement proper scheme with new binding support
				logger.warning("Please implement me !!!!!!!!!");
			} else {
				conceptClass = individualEntry.getConcept();
			}
			if (conceptClass != null) {
				individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding("component.typeURI"),
						new DataBinding('"' + conceptClass.getURI() + '"'), true));
			}
			if (StringUtils.isNotEmpty(individualEntry.getRenderer())) {
				individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding("component.renderer"),
						new DataBinding('"' + individualEntry.getRenderer() + '"'), true));
			}

			newTab.addToSubComponents(individualSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
			return individualSelector;
		} else if (entry instanceof ClassInspectorEntry) {
			ClassInspectorEntry classEntry = (ClassInspectorEntry) entry;
			FIBCustom classSelector = new FIBCustom();
			classSelector.setComponentClass(org.openflexo.components.widget.FIBClassSelector.class);
			// Quick and dirty hack to configure ClassSelector: refactor this when new binding model will be in use
			// component.context = xxx
			classSelector.addToAssignments(new FIBCustomAssignment(classSelector, new DataBinding<Object>("component.project"),
					new DataBinding<Object>("data.project"), true));
			/*classSelector.addToAssignments(new FIBCustomAssignment(classSelector, new DataBinding("component.contextOntologyURI"),
					new DataBinding('"' + classEntry.getViewPoint().getViewpointOntology().getURI() + '"') {
						@Override
						public BindingFactory getBindingFactory() {
							return entry.getBindingFactory();
						}
					}, true));*/
			// Quick and dirty hack to configure ClassSelector: refactor this when new binding model will be in use
			IFlexoOntologyClass conceptClass = null;
			if (classEntry.getIsDynamicConceptValue()) {
				// conceptClass = classEntry.evaluateConceptValue(action);
				// TODO: implement proper scheme with new binding support
				logger.warning("Please implement me !!!!!!!!!");
			} else {
				conceptClass = classEntry.getConcept();
			}
			if (conceptClass != null) {
				classSelector.addToAssignments(new FIBCustomAssignment(classSelector, new DataBinding<Object>("component.rootClassURI"),
						new DataBinding<Object>('"' + conceptClass.getURI() + '"'), true));
			}
			newTab.addToSubComponents(classSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
			return classSelector;
		} else if (entry instanceof PropertyInspectorEntry) {
			PropertyInspectorEntry propertyEntry = (PropertyInspectorEntry) entry;
			FIBCustom propertySelector = new FIBCustom();
			propertySelector.setComponentClass(FIBPropertySelector.class);
			// Quick and dirty hack to configure FIBPropertySelector: refactor this when new binding model will be in use
			// component.context = xxx
			propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding<Object>("component.project"),
					new DataBinding<Object>("data.project"), true));
			/*propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding("component.contextOntologyURI"),
					new DataBinding('"' + propertyEntry.getViewPoint().getViewpointOntology().getURI() + '"') {
						@Override
						public BindingFactory getBindingFactory() {
							return entry.getBindingFactory();
						}
					}, true));*/

			// Quick and dirty hack to configure FIBPropertySelector: refactor this when new binding model will be in use
			IFlexoOntologyClass domainClass = null;
			if (propertyEntry.getIsDynamicDomainValue()) {
				// domainClass = propertyEntry.evaluateDomainValue(action);
				// TODO: implement proper scheme with new binding support
				logger.warning("Please implement me !!!!!!!!!");
			} else {
				domainClass = propertyEntry.getDomain();
			}
			if (domainClass != null) {
				propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding<Object>(
						"component.domainClassURI"), new DataBinding<Object>('"' + domainClass.getURI() + '"'), true));
			}
			if (propertyEntry instanceof ObjectPropertyInspectorEntry) {
				IFlexoOntologyClass rangeClass = null;
				if (propertyEntry.getIsDynamicDomainValue()) {
					// domainClass = propertyEntry.evaluateDomainValue(action);
					// TODO: implement proper scheme with new binding support
					logger.warning("Please implement me !!!!!!!!!");
				} else {
					rangeClass = ((ObjectPropertyInspectorEntry) propertyEntry).getRange();
				}
				if (rangeClass != null) {
					propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding<Object>(
							"component.rangeClassURI"), new DataBinding<Object>('"' + rangeClass.getURI() + '"'), true));
				}
			}
			if (propertyEntry instanceof ObjectPropertyInspectorEntry) {
				propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding<Object>(
						"component.selectDataProperties"), new DataBinding<Object>("false"), true));
			} else if (propertyEntry instanceof DataPropertyInspectorEntry) {
				propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding<Object>(
						"component.selectObjectProperties"), new DataBinding<Object>("false"), true));
			}

			// Quick and dirty hack to configure PropertySelector: refactor this when new binding model will be in use
			/*propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding("component.domainClassURI"),
					new DataBinding('"' + ((PropertyInspectorEntry) entry)._getDomainURI() + '"') {
						@Override
						public BindingFactory getBindingFactory() {
							return entry.getBindingFactory();
						}
					}, true));*/
			newTab.addToSubComponents(propertySelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
			return propertySelector;
		} else if (entry instanceof FlexoObjectInspectorEntry) {
			FlexoObjectInspectorEntry foEntry = (FlexoObjectInspectorEntry) entry;
			switch (foEntry.getFlexoObjectType()) {
			case Process:
				FIBCustom processSelector = new FIBCustom();
				processSelector.setComponentClass(org.openflexo.components.widget.FIBProcessSelector.class);
				processSelector.addToAssignments(new FIBCustomAssignment(processSelector, new DataBinding("component.project"),
						new DataBinding("data.project"), true));
				newTab.addToSubComponents(processSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
				return processSelector;
			case ProcessFolder:
				FIBCustom processFolderSelector = new FIBCustom();
				processFolderSelector.setComponentClass(org.openflexo.components.widget.FIBProcessFolderSelector.class);
				processFolderSelector.addToAssignments(new FIBCustomAssignment(processFolderSelector, new DataBinding("component.project"),
						new DataBinding("data.project"), true));
				newTab.addToSubComponents(processFolderSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
				return processFolderSelector;
			case Role:
				FIBCustom roleSelector = new FIBCustom();
				roleSelector.setComponentClass(org.openflexo.components.widget.FIBRoleSelector.class);
				roleSelector.addToAssignments(new FIBCustomAssignment(roleSelector, new DataBinding("component.project"), new DataBinding(
						"data.project"), true));
				newTab.addToSubComponents(roleSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
				return roleSelector;
			case Activity:
				FIBCustom activitySelector = new FIBCustom();
				activitySelector.setComponentClass(org.openflexo.components.widget.ActivitySelector.class);
				activitySelector.addToAssignments(new FIBCustomAssignment(activitySelector, new DataBinding("component.project"),
						new DataBinding("data.project"), true));
				newTab.addToSubComponents(activitySelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
				return activitySelector;
			case Operation:
				FIBCustom operationSelector = new FIBCustom();
				operationSelector.setComponentClass(org.openflexo.components.widget.OperationSelector.class);
				operationSelector.addToAssignments(new FIBCustomAssignment(operationSelector, new DataBinding("component.project"),
						new DataBinding("data.project"), true));
				newTab.addToSubComponents(operationSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
				return operationSelector;
			case Action:
				FIBCustom actionSelector = new FIBCustom();
				actionSelector.setComponentClass(org.openflexo.components.widget.ActionSelector.class);
				actionSelector.addToAssignments(new FIBCustomAssignment(actionSelector, new DataBinding("component.project"),
						new DataBinding("data.project"), true));
				newTab.addToSubComponents(actionSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
				return actionSelector;

			default:
				break;
			}
		}

		FIBLabel unknown = new FIBLabel();
		unknown.setLabel("???");
		newTab.addToSubComponents(unknown, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false));
		return unknown;

	}

	private FIBTab makeFIBTab(EditionPattern ep) {
		// logger.info("makeFIBTab " + refIndex + " for " + ep);
		String epIdentifier = getEditionPatternIdentifier(ep);
		FIBTab newTab = createFIBTabForEditionPattern(ep);
		appendInspectorEntries(ep, epIdentifier, newTab);
		newTab.finalizeDeserialization();
		return newTab;
	}

	protected void appendInspectorEntries(EditionPattern ep, String epIdentifier, FIBTab newTab) {
		if (ep.getParentEditionPattern() != null) {
			appendInspectorEntries(ep.getParentEditionPattern(), epIdentifier, newTab);
		}
		LocalizedDictionary localizedDictionary = ep.getViewPoint().getLocalizedDictionary();
		for (final InspectorEntry entry : ep.getInspector().getEntries()) {
			FIBLabel label = new FIBLabel();
			String entryLabel = localizedDictionary.getLocalizedForKeyAndLanguage(entry.getLabel(), FlexoLocalization.getCurrentLanguage());
			if (entryLabel == null) {
				entryLabel = entry.getLabel();
			}
			label.setLabel(entryLabel);
			newTab.addToSubComponents(label, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false));
			FIBWidget widget = makeWidget(entry, newTab);
			widget.setBindingFactory(entry.getBindingFactory());
			widget.setData(new DataBinding<Object>(epIdentifier + "." + entry.getData().toString()));
			widget.setReadOnly(entry.getIsReadOnly());
		}
	}

	protected FIBTab createFIBTabForEditionPattern(EditionPattern ep) {
		String epIdentifier = getEditionPatternIdentifier(ep);
		FIBTab newTab = new FIBTab();
		newTab.setTitle(ep.getInspector().getInspectorTitle());
		newTab.setLayout(Layout.twocols);
		// newTab.setDataClass(EditionPatternInstance.class);
		// newTab.setData(new DataBinding("data.editionPatternReferences.get["+refIndex+"].editionPatternInstance"));
		// newTab.setData(new DataBinding("data.editionPatternReferences.firstElement.editionPatternInstance"));
		newTab.setName(epIdentifier + "Panel");
		return newTab;
	}

	protected String getEditionPatternIdentifier(EditionPattern ep) {
		return "data.getEditionPatternInstance(\"" + ep.getName() + "\")";
		// return ep.getViewPoint().getName() + "_" + ep.getName() + "_" + refIndex;
	}

}
