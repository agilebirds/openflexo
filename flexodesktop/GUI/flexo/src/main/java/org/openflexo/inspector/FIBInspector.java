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

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.model.DataBinding;
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
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.ontology.EditionPatternReference;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.binding.EditionPatternInstancePathElement;
import org.openflexo.foundation.viewpoint.inspector.CheckboxInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.ClassInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.EditionPatternInspector;
import org.openflexo.foundation.viewpoint.inspector.FlexoObjectInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.IndividualInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.InspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.IntegerInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.PropertyInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.TextAreaInspectorEntry;
import org.openflexo.foundation.viewpoint.inspector.TextFieldInspectorEntry;
import org.openflexo.xmlcode.AccessorInvocationException;
import org.openflexo.xmlcode.Cloner;
import org.openflexo.xmlcode.DuplicateSerializationIdentifierException;
import org.openflexo.xmlcode.InvalidModelException;
import org.openflexo.xmlcode.InvalidObjectSpecificationException;
import org.openflexo.xmlcode.StringEncoder;
import org.openflexo.xmlcode.XMLCoder;

public class FIBInspector extends FIBPanel {

	static final Logger logger = Logger.getLogger(FIBInspector.class.getPackage().getName());

	private boolean superInspectorWereAppened = false;

	private FIBInspector superInspector;

	public FIBInspector getSuperInspector() {
		return superInspector;
	}

	protected void appendSuperInspectors(MainInspectorController controller) {
		if (getDataType() == null) {
			return;
		}
		if (getDataType() instanceof Class) {
			FIBInspector superInspector = controller.inspectorForClass(((Class) getDataType()).getSuperclass());
			if (superInspector != null) {
				superInspector.appendSuperInspectors(controller);
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
			// System.out.println("Append "+superInspector+" to "+this);
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

	/**
	 * This method looks after object's EditionPattern references to know if we need to structurally change inspector by adding or removing
	 * tabs, which all correspond to one and only one EditionPattern
	 * 
	 * Note: only object providing support as primary role are handled here
	 * 
	 * @param object
	 * @return
	 */
	protected boolean updateEditionPatternReferences(FlexoModelObject object) {
		boolean needsChanges = false;

		if (object.getEditionPatternReferences() == null) {
			needsChanges = (currentEditionPatterns.size() > 0);
		} else {
			/*System.out.println("*********** Object " + object);
			System.out.println("References: " + object.getEditionPatternReferences().size());
			for (EditionPatternReference ref : object.getEditionPatternReferences()) {
				System.out.println(">>>>>>>>> Reference \n" + ref.getEditionPatternInstance().debug());
			}*/
			if (currentEditionPatterns.size() != object.getEditionPatternReferences().size()) {
				needsChanges = true;
			} else {
				for (int i = 0; i < currentEditionPatterns.size(); i++) {
					if (currentEditionPatterns.get(i) != object.getEditionPatternReferences().get(i).getEditionPattern()) {
						needsChanges = true;
						break;
					}
				}
			}
		}

		/*if (object.providesSupportAsPrimaryRole() != previousObjectWasProvidingSupportAsPrimaryRole) {
			needsChanges = true;
		}*/

		if (!needsChanges) {
			// No changes detected, i can return
			return false;
		}

		for (FIBTab tab : tabsForEP.values()) {
			getTabPanel().removeFromSubComponents(tab);
		}

		currentEditionPatterns.clear();
		tabsForEP.clear();

		// if (object.providesSupportAsPrimaryRole()) {
		if (object.getEditionPatternReferences() != null) {
			for (int refIndex = 0; refIndex < object.getEditionPatternReferences().size(); refIndex++) {
				EditionPatternReference ref = object.getEditionPatternReferences().get(refIndex);
				if (ref == null) {
					logger.warning("Cannot find reference for EditionPattern refIndex=" + refIndex + ". Please investigate...");
				} else {
					if (ref.getEditionPattern() == null) {
						logger.warning("Found reference for null EditionPattern refIndex=" + refIndex + ". Please investigate...");
					} else {
						EditionPatternInspector inspector = ref.getEditionPattern().getInspector();
						FIBTab newTab = makeFIBTab(ref.getEditionPattern(), refIndex);
						currentEditionPatterns.add(ref.getEditionPattern());
						tabsForEP.put(ref.getEditionPattern(), newTab);
						getTabPanel().addToSubComponents(newTab);
					}
				}
			}
			updateBindingModel();
		}
		// }

		// previousObjectWasProvidingSupportAsPrimaryRole = object.providesSupportAsPrimaryRole();

		return true;

	}

	// private boolean previousObjectWasProvidingSupportAsPrimaryRole = false;

	@Override
	protected void createBindingModel() {
		super.createBindingModel();
		for (int i = 0; i < currentEditionPatterns.size(); i++) {
			EditionPattern ep = currentEditionPatterns.get(i);
			_bindingModel.addToBindingVariables(new EditionPatternInstancePathElement(ep, i, getDataClass()));
		}
	}

	private FIBWidget makeWidget(final InspectorEntry entry, FIBTab newTab, int index) {
		if (entry instanceof TextFieldInspectorEntry) {
			FIBTextField tf = new FIBTextField();
			tf.validateOnReturn = true; // Avoid to many ontologies manipulations
			newTab.addToSubComponents(tf, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
			return tf;
		} else if (entry instanceof TextAreaInspectorEntry) {
			FIBTextArea ta = new FIBTextArea();
			ta.validateOnReturn = true; // Avoid to many ontologies manipulations
			ta.setUseScrollBar(true);
			ta.setHorizontalScrollbarPolicy(HorizontalScrollBarPolicy.HORIZONTAL_SCROLLBAR_AS_NEEDED);
			ta.setVerticalScrollbarPolicy(VerticalScrollBarPolicy.VERTICAL_SCROLLBAR_AS_NEEDED);
			newTab.addToSubComponents(ta, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, true, index));
			return ta;
		} else if (entry instanceof CheckboxInspectorEntry) {
			FIBCheckBox cb = new FIBCheckBox();
			newTab.addToSubComponents(cb, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
			return cb;
		} else if (entry instanceof IntegerInspectorEntry) {
			FIBNumber number = new FIBNumber();
			number.setNumberType(NumberType.IntegerType);
			newTab.addToSubComponents(number, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
			return number;
		} else if (entry instanceof IndividualInspectorEntry) {
			FIBCustom individualSelector = new FIBCustom();
			individualSelector.setComponentClass(org.openflexo.components.widget.OntologyIndividualSelector.class);
			individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding("component.project"),
					new DataBinding("data.project"), true));
			// Quick and dirty hack to configure IndividualSelector: refactor this when new binding model will be in use
			individualSelector.addToAssignments(new FIBCustomAssignment(individualSelector, new DataBinding("component.ontologyClassURI"),
					new DataBinding('"' + ((IndividualInspectorEntry) entry)._getConceptURI() + '"') {
						@Override
						public BindingFactory getBindingFactory() {
							return entry.getBindingFactory();
						}
					}, true));
			newTab.addToSubComponents(individualSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
			return individualSelector;
		} else if (entry instanceof ClassInspectorEntry) {
			FIBCustom classSelector = new FIBCustom();
			classSelector.setComponentClass(org.openflexo.components.widget.OntologyClassSelector.class);
			classSelector.addToAssignments(new FIBCustomAssignment(classSelector, new DataBinding("component.project"), new DataBinding(
					"data.project"), true));
			// Quick and dirty hack to configure ClassSelector: refactor this when new binding model will be in use
			classSelector.addToAssignments(new FIBCustomAssignment(classSelector, new DataBinding("component.parentClassURI"),
					new DataBinding('"' + ((ClassInspectorEntry) entry)._getConceptURI() + '"') {
						@Override
						public BindingFactory getBindingFactory() {
							return entry.getBindingFactory();
						}
					}, true));
			newTab.addToSubComponents(classSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
			return classSelector;
		} else if (entry instanceof PropertyInspectorEntry) {
			FIBCustom propertySelector = new FIBCustom();
			propertySelector.setComponentClass(org.openflexo.components.widget.OntologyPropertySelector.class);
			propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding("component.project"),
					new DataBinding("data.project"), true));
			// Quick and dirty hack to configure PropertySelector: refactor this when new binding model will be in use
			propertySelector.addToAssignments(new FIBCustomAssignment(propertySelector, new DataBinding("component.domainClassURI"),
					new DataBinding('"' + ((PropertyInspectorEntry) entry)._getDomainURI() + '"') {
						@Override
						public BindingFactory getBindingFactory() {
							return entry.getBindingFactory();
						}
					}, true));
			newTab.addToSubComponents(propertySelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
			return propertySelector;
		} else if (entry instanceof FlexoObjectInspectorEntry) {
			FlexoObjectInspectorEntry foEntry = (FlexoObjectInspectorEntry) entry;
			switch (foEntry.getFlexoObjectType()) {
			case Process:
				FIBCustom processSelector = new FIBCustom();
				processSelector.setComponentClass(org.openflexo.components.widget.FIBProcessSelector.class);
				processSelector.addToAssignments(new FIBCustomAssignment(processSelector, new DataBinding("component.project"),
						new DataBinding("data.project"), true));
				newTab.addToSubComponents(processSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
				return processSelector;
			case Role:
				FIBCustom roleSelector = new FIBCustom();
				roleSelector.setComponentClass(org.openflexo.components.widget.FIBRoleSelector.class);
				roleSelector.addToAssignments(new FIBCustomAssignment(roleSelector, new DataBinding("component.project"), new DataBinding(
						"data.project"), true));
				newTab.addToSubComponents(roleSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
				return roleSelector;
			case Activity:
				FIBCustom activitySelector = new FIBCustom();
				activitySelector.setComponentClass(org.openflexo.components.widget.ActivitySelector.class);
				activitySelector.addToAssignments(new FIBCustomAssignment(activitySelector, new DataBinding("component.project"),
						new DataBinding("data.project"), true));
				newTab.addToSubComponents(activitySelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
				return activitySelector;
			case Operation:
				FIBCustom operationSelector = new FIBCustom();
				operationSelector.setComponentClass(org.openflexo.components.widget.OperationSelector.class);
				operationSelector.addToAssignments(new FIBCustomAssignment(operationSelector, new DataBinding("component.project"),
						new DataBinding("data.project"), true));
				newTab.addToSubComponents(operationSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
				return operationSelector;
			case Action:
				FIBCustom actionSelector = new FIBCustom();
				actionSelector.setComponentClass(org.openflexo.components.widget.ActionSelector.class);
				actionSelector.addToAssignments(new FIBCustomAssignment(actionSelector, new DataBinding("component.project"),
						new DataBinding("data.project"), true));
				newTab.addToSubComponents(actionSelector, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
				return actionSelector;

			default:
				break;
			}
		}

		FIBLabel unknown = new FIBLabel();
		unknown.setLabel("???");
		newTab.addToSubComponents(unknown, new TwoColsLayoutConstraints(TwoColsLayoutLocation.right, true, false, index));
		return unknown;

	}

	private FIBTab makeFIBTab(EditionPattern ep, int refIndex) {
		FIBTab newTab = new FIBTab();
		newTab.setTitle(ep.getInspector().getInspectorTitle());
		newTab.setIndex(-1);
		newTab.setLayout(Layout.twocols);
		// newTab.setDataClass(EditionPatternInstance.class);
		// newTab.setData(new DataBinding("data.editionPatternReferences.get["+refIndex+"].editionPatternInstance"));
		// newTab.setData(new DataBinding("data.editionPatternReferences.firstElement.editionPatternInstance"));
		String epIdentifier = ep.getCalc().getName() + "_" + ep.getName() + "_" + refIndex;
		newTab.setName(epIdentifier + "Panel");
		int index = 0;
		for (final InspectorEntry entry : ep.getInspector().getEntries()) {
			FIBLabel label = new FIBLabel();
			label.setLabel(entry.getLabel());
			newTab.addToSubComponents(label, new TwoColsLayoutConstraints(TwoColsLayoutLocation.left, false, false, index++));
			FIBWidget widget = makeWidget(entry, newTab, index++);
			widget.setData(new DataBinding(epIdentifier + "." + entry.getData().toString()) {
				@Override
				public BindingFactory getBindingFactory() {
					return entry.getBindingFactory();
				}
			});
			widget.setReadOnly(entry.getIsReadOnly());

		}
		return newTab;
	}

	private Vector<EditionPattern> currentEditionPatterns = new Vector<EditionPattern>();
	private Hashtable<EditionPattern, FIBTab> tabsForEP = new Hashtable<EditionPattern, FIBTab>();
}
