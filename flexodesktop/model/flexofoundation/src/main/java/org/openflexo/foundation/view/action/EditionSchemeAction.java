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
package org.openflexo.foundation.view.action;

import java.util.Hashtable;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.fge.GraphicalRepresentation;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ontology.DataPropertyStatement;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.ontology.ObjectPropertyStatement;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.RestrictionStatement;
import org.openflexo.foundation.ontology.RestrictionStatement.RestrictionType;
import org.openflexo.foundation.ontology.SubClassStatement;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.view.View;
import org.openflexo.foundation.view.ViewConnector;
import org.openflexo.foundation.view.ViewElement;
import org.openflexo.foundation.view.ViewObject;
import org.openflexo.foundation.view.ViewShape;
import org.openflexo.foundation.viewpoint.AddClass;
import org.openflexo.foundation.viewpoint.AddDataPropertyStatement;
import org.openflexo.foundation.viewpoint.AddEditionPattern.AddEditionPatternParameter;
import org.openflexo.foundation.viewpoint.AddIndividual;
import org.openflexo.foundation.viewpoint.AddIsAStatement;
import org.openflexo.foundation.viewpoint.AddObjectPropertyStatement;
import org.openflexo.foundation.viewpoint.AddRestrictionStatement;
import org.openflexo.foundation.viewpoint.DataPropertyAssertion;
import org.openflexo.foundation.viewpoint.DeclarePatternRole;
import org.openflexo.foundation.viewpoint.EditionAction;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.foundation.viewpoint.GraphicalAction;
import org.openflexo.foundation.viewpoint.GraphicalElementPatternRole;
import org.openflexo.foundation.viewpoint.ListParameter;
import org.openflexo.foundation.viewpoint.ObjectPropertyAssertion;
import org.openflexo.foundation.viewpoint.URIParameter;
import org.openflexo.foundation.viewpoint.binding.EditionPatternPathElement;
import org.openflexo.foundation.viewpoint.binding.EditionSchemeParameterListPathElement;
import org.openflexo.foundation.viewpoint.binding.EditionSchemeParameterPathElement;
import org.openflexo.foundation.viewpoint.binding.GraphicalElementPathElement.ViewPathElement;
import org.openflexo.foundation.viewpoint.binding.PatternRolePathElement;
import org.openflexo.toolbox.StringUtils;

public abstract class EditionSchemeAction<A extends EditionSchemeAction<?>> extends FlexoAction<A, FlexoModelObject, FlexoModelObject>
		implements BindingEvaluationContext /*, BindingPathElement<Object>*/{

	private static final Logger logger = Logger.getLogger(EditionSchemeAction.class.getPackage().getName());

	protected Hashtable<EditionSchemeParameter, Object> parameterValues;
	protected Hashtable<ListParameter, List> parameterListValues;

	public boolean escapeParameterRetrievingWhenValid = true;

	public EditionSchemeAction(FlexoActionType<A, FlexoModelObject, FlexoModelObject> actionType, FlexoModelObject focusedObject,
			Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
		parameterValues = new Hashtable<EditionSchemeParameter, Object>();
		parameterListValues = new Hashtable<ListParameter, List>();
	}

	/**
	 * Compute and store default parameters, and return a flag indicating if all parameters declared as "mandatory" could be successfully
	 * filled
	 * 
	 * @return
	 */
	// TODO: we must order this if dependancies are not resolved using basic sequence
	public boolean retrieveDefaultParameters() {
		boolean returned = true;
		EditionScheme editionScheme = getEditionScheme();
		for (final EditionSchemeParameter parameter : editionScheme.getParameters()) {
			Object defaultValue = parameter.getDefaultValue(this);
			if (defaultValue != null && !(parameter instanceof URIParameter)) {
				parameterValues.put(parameter, defaultValue);
			}
			if (parameter instanceof ListParameter) {
				List list = (List) ((ListParameter) parameter).getList(this);
				parameterListValues.put((ListParameter) parameter, list);
			}
			if (!parameter.isValid(this, defaultValue)) {
				logger.info("Parameter " + parameter + " is not valid for value " + defaultValue);
				returned = false;
			}
		}
		return returned;
	}

	public FlexoProject getProject() {
		if (getFocusedObject() != null) {
			return getFocusedObject().getProject();
		}
		return null;
	}

	public EditionPattern getEditionPattern() {
		if (getEditionScheme() != null) {
			return getEditionScheme().getEditionPattern();
		}
		return null;
	}

	/*public String getStringParameter(String parameterName) {
		System.out.println("OK, on me demande le parametre " + parameterName + ", je retourne " + parameterValues.get(parameterName));
		return (String) parameterValues.get(parameterName);
	}

	public String getURIParameter(String parameterName) {
		System.out.println("OK, on me demande l'uri " + parameterName + ", je retourned " + parameterValues.get(parameterName));
		return (String) parameterValues.get(parameterName);
	}*/

	/*public Hashtable<EditionSchemeParameter, Object> getParameterValues() {
		return parameterValues;
	}*/

	public Object getParameterValue(EditionSchemeParameter parameter) {
		/*System.out.println("On me demande la valeur du parametre " + parameter.getName() + " a priori c'est "
				+ parameterValues.get(parameter));*/
		if (parameter instanceof URIParameter) {
			if (parameterValues.get(parameter) == null
					|| (parameterValues.get(parameter) instanceof String && StringUtils.isEmpty((String) parameterValues.get(parameter)))) {
				return ((URIParameter) parameter).getDefaultValue(this);
			}
		}
		return parameterValues.get(parameter);
	}

	public void setParameterValue(EditionSchemeParameter parameter, Object value) {
		// System.out.println("setParameterValue " + value + " for parameter " + parameter.getName());
		parameterValues.put(parameter, value);
		/*for (EditionSchemeParameter p : getEditionScheme().getParameters()) {
			if (p instanceof URIParameter) {
				// System.out.println("Hop, je recalcule l'uri, ici");
			}
		}*/
	}

	public List getParameterListValue(ListParameter parameter) {
		/*System.out.println("On me demande la valeur du parametre " + parameter.getName() + " a priori c'est "
				+ parameterValues.get(parameter));*/
		return parameterListValues.get(parameter);
	}

	public abstract EditionScheme getEditionScheme();

	public abstract EditionPatternInstance getEditionPatternInstance();

	protected abstract View retrieveOEShema();

	protected void applyEditionActions() {
		Hashtable<EditionAction, FlexoModelObject> performedActions = new Hashtable<EditionAction, FlexoModelObject>();

		// Perform actions
		for (EditionAction action : getEditionScheme().getActions()) {
			if (action.evaluateCondition(this)) {
				if (action instanceof org.openflexo.foundation.viewpoint.AddShape) {
					logger.info("Add shape " + action);
					ViewShape newShape = performAddShape((org.openflexo.foundation.viewpoint.AddShape) action);
					if (newShape != null) {
						getEditionPatternInstance().setObjectForPatternRole(newShape,
								((org.openflexo.foundation.viewpoint.AddShape) action).getPatternRole());
						performedActions.put(action, newShape);
					}
				} else if (action instanceof org.openflexo.foundation.viewpoint.AddConnector) {
					logger.info("Add connector " + action);
					ViewConnector newConnector = performAddConnector((org.openflexo.foundation.viewpoint.AddConnector) action);
					if (newConnector != null) {
						getEditionPatternInstance().setObjectForPatternRole(newConnector,
								((org.openflexo.foundation.viewpoint.AddConnector) action).getPatternRole());
						performedActions.put(action, newConnector);
					}
				} else if (action instanceof AddIndividual) {
					logger.info("Add individual " + action);
					OntologyIndividual newIndividual = performAddIndividual((AddIndividual) action);
					if (newIndividual != null) {
						getEditionPatternInstance().setObjectForPatternRole(newIndividual, ((AddIndividual) action).getPatternRole());
						performedActions.put(action, newIndividual);
					}
				} else if (action instanceof AddClass) {
					logger.info("Add class " + action);
					OntologyClass newClass = performAddClass((AddClass) action);
					if (newClass != null) {
						getEditionPatternInstance().setObjectForPatternRole(newClass, ((AddClass) action).getPatternRole());
						performedActions.put(action, newClass);
					}
				} else if (action instanceof AddObjectPropertyStatement) {
					logger.info("Add object property " + action);
					ObjectPropertyStatement statement = performAddObjectPropertyStatement((org.openflexo.foundation.viewpoint.AddObjectPropertyStatement) action);
					if (statement != null) {
						getEditionPatternInstance().setObjectForPatternRole(statement,
								((AddObjectPropertyStatement) action).getPatternRole());
						performedActions.put(action, statement);
					} else {
						logger.warning("Could not perform AddObjectPropertyStatement for action " + action);
					}
				} else if (action instanceof AddDataPropertyStatement) {
					logger.info("Add data property " + action);
					DataPropertyStatement statement = performAddDataPropertyStatement((org.openflexo.foundation.viewpoint.AddDataPropertyStatement) action);
					if (statement != null) {
						getEditionPatternInstance()
								.setObjectForPatternRole(statement, ((AddDataPropertyStatement) action).getPatternRole());
						performedActions.put(action, statement);
					} else {
						logger.warning("Could not perform AddDataPropertyStatement for action " + action);
					}
				} else if (action instanceof AddIsAStatement) {
					logger.info("Add isA property " + action);
					SubClassStatement statement = performAddIsAProperty((AddIsAStatement) action);
					if (statement != null) {
						getEditionPatternInstance().setObjectForPatternRole(statement, ((AddIsAStatement) action).getPatternRole());
						performedActions.put(action, statement);
					}
				} else if (action instanceof AddRestrictionStatement) {
					logger.info("Add restriction " + action);
					RestrictionStatement statement = performAddRestriction((AddRestrictionStatement) action);
					if (statement != null) {
						getEditionPatternInstance().setObjectForPatternRole(statement, ((AddRestrictionStatement) action).getPatternRole());
						performedActions.put(action, statement);
					}
				} else if (action instanceof DeclarePatternRole) {
					logger.info("Declare object " + action);
					FlexoModelObject declaredObject = null;
					try {
						declaredObject = performDeclarePatternRole((DeclarePatternRole) action);
					} catch (ClassCastException e) {
						logger.warning("ClassCastException: found declared object: " + e.getMessage());
					}
					logger.info("Found declared object: " + declaredObject);
					if (declaredObject != null) {
						getEditionPatternInstance().setObjectForPatternRole(declaredObject, ((DeclarePatternRole) action).getPatternRole());
						performedActions.put(action, declaredObject);
					}
				} else if (action instanceof org.openflexo.foundation.viewpoint.AddDiagram) {
					logger.info("Add shema " + action);
					View newShema = performAddDiagram((org.openflexo.foundation.viewpoint.AddDiagram) action);
					if (newShema != null) {
						getEditionPatternInstance().setObjectForPatternRole(newShema,
								((org.openflexo.foundation.viewpoint.AddDiagram) action).getPatternRole());
						performedActions.put(action, newShema);
					}
				} else if (action instanceof org.openflexo.foundation.viewpoint.AddEditionPattern) {
					logger.info("Add EditionPattern " + action + " EP="
							+ ((org.openflexo.foundation.viewpoint.AddEditionPattern) action).getEditionPatternType());
					EditionPatternInstance newEP = performAddEditionPattern((org.openflexo.foundation.viewpoint.AddEditionPattern) action);
					if (newEP != null && ((org.openflexo.foundation.viewpoint.AddEditionPattern) action).getPatternRole() != null) {
						logger.warning("EditionPatternInstance not declared as FlexoModelObject !!!");
						// getEditionPatternInstance().setObjectForPatternRole(newEP, action.getPatternRole());
						// performedActions.put(action, newEP);
					}
				}
			}
		}

		// All object are now created, finalize relationships between them
		for (EditionAction action : performedActions.keySet()) {
			if (action instanceof AddIndividual) {
				finalizePerformAddIndividual((AddIndividual) action, (OntologyIndividual) performedActions.get(action));
			} else if (action instanceof AddClass) {
				finalizePerformAddClass((AddClass) action, (OntologyClass) performedActions.get(action));
			} else if (action instanceof AddObjectPropertyStatement) {
				finalizePerformAddObjectPropertyStatement((AddObjectPropertyStatement) action,
						(ObjectPropertyStatement) performedActions.get(action));
			} else if (action instanceof AddDataPropertyStatement) {
				finalizePerformAddDataPropertyStatement((AddDataPropertyStatement) action,
						(DataPropertyStatement) performedActions.get(action));
			} else if (action instanceof AddIsAStatement) {
				finalizePerformAddIsAProperty((AddIsAStatement) action, (SubClassStatement) performedActions.get(action));
			} else if (action instanceof AddRestrictionStatement) {
				finalizePerformAddRestriction((AddRestrictionStatement) action, (RestrictionStatement) performedActions.get(action));
			} else if (action instanceof DeclarePatternRole) {
				FlexoModelObject declaredObject = performDeclarePatternRole((DeclarePatternRole) action);
				if (declaredObject != null) {
					getEditionPatternInstance().setObjectForPatternRole(declaredObject, ((DeclarePatternRole) action).getPatternRole());
					performedActions.put(action, declaredObject);
				}
				finalizePerformDeclarePatternRole((DeclarePatternRole) action);
			}
		}

		// Finalize shape creation at the end to be sure labels are now correctely bound
		for (EditionAction action : performedActions.keySet()) {
			if (action instanceof org.openflexo.foundation.viewpoint.AddShape) {
				finalizePerformAddShape((org.openflexo.foundation.viewpoint.AddShape) action, (ViewShape) performedActions.get(action));
			}
			if (action instanceof org.openflexo.foundation.viewpoint.AddConnector) {
				finalizePerformAddConnector((org.openflexo.foundation.viewpoint.AddConnector) action,
						(ViewConnector) performedActions.get(action));
			}
			if (action instanceof org.openflexo.foundation.viewpoint.AddDiagram) {
				finalizePerformAddDiagram((org.openflexo.foundation.viewpoint.AddDiagram) action, (View) performedActions.get(action));
			}
		}

		// Now perform "normal actions"
		for (EditionAction action : getEditionScheme().getActions()) {
			if (action.evaluateCondition(this)) {
				if (action instanceof GraphicalAction) {
					performGraphicalAction((GraphicalAction) action);
				}
			}
		}

	}

	protected ViewShape performAddShape(org.openflexo.foundation.viewpoint.AddShape action) {
		ViewShape newShape = new ViewShape(retrieveOEShema());

		// If an overriden graphical representation is defined, use it
		if (getOverridingGraphicalRepresentation(action.getPatternRole()) != null) {
			newShape.setGraphicalRepresentation(getOverridingGraphicalRepresentation(action.getPatternRole()));
		} else if (action.getPatternRole().getGraphicalRepresentation() != null) {
			newShape.setGraphicalRepresentation(action.getPatternRole().getGraphicalRepresentation());
		}

		// Register reference
		newShape.registerEditionPatternReference(getEditionPatternInstance(), action.getPatternRole());

		ViewObject container = action.getContainer(this);

		if (container == null) {
			logger.warning("When adding shape, cannot find container for action " + action.getPatternRole() + " container="
					+ action.getContainer());
			return null;
		}

		container.addToChilds(newShape);
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Added shape " + newShape + " under " + container);
		}
		return newShape;
	}

	protected ViewShape finalizePerformAddShape(org.openflexo.foundation.viewpoint.AddShape action, ViewShape newShape) {
		// Be sure that the newly created shape is updated
		newShape.update();
		return newShape;
	}

	protected OntologyIndividual performAddIndividual(AddIndividual action) {
		OntologyClass father = action.getOntologyClass();
		// OntologyObject father = action.getOntologyObject(getProject());
		// System.out.println("Individual name param = "+action.getIndividualNameParameter());
		// String individualName = (String)getParameterValues().get(action.getIndividualNameParameter().getName());
		String individualName = (String) action.getIndividualName().getBindingValue(this);
		// System.out.println("individualName="+individualName);
		OntologyIndividual newIndividual;
		try {
			newIndividual = getProject().getProjectOntology().createOntologyIndividual(individualName, father);
			logger.info("********* Added individual " + newIndividual.getName() + " as " + father);
			/*OntologyClass uneAutreClasses = getProject().getProjectOntology().createOntologyClass("UneClasseCommeCa", (OntologyClass)father);
			OntologyIndividual unAutreIndividual = getProject().getProjectOntology().createOntologyIndividual("UnAutreIndividual", (OntologyClass)father);
			OntologyObjectProperty objProp = getProject().getOntologyLibrary().getObjectProperty(OntologyLibrary.FLEXO_CONCEPT_ONTOLOGY_URI+"#"+"inRelationWith");
			returned.getOntResource().addProperty(objProp.getOntProperty(), uneAutreClasses.getOntResource());
			returned.getOntResource().addProperty(objProp.getOntProperty(), unAutreIndividual.getOntResource());*/

			return newIndividual;
		} catch (DuplicateURIException e) {
			e.printStackTrace();
			return null;
		}
	}

	protected OntologyIndividual finalizePerformAddIndividual(AddIndividual action, OntologyIndividual newIndividual) {
		for (DataPropertyAssertion dataPropertyAssertion : action.getDataAssertions()) {
			if (dataPropertyAssertion.evaluateCondition(this)) {
				logger.info("DataPropertyAssertion=" + dataPropertyAssertion);
				OntologyProperty property = dataPropertyAssertion.getOntologyProperty();
				logger.info("Property=" + property);
				Object value = dataPropertyAssertion.getValue(this);
				newIndividual.addLiteral(property, value);
			}
		}
		for (ObjectPropertyAssertion objectPropertyAssertion : action.getObjectAssertions()) {
			if (objectPropertyAssertion.evaluateCondition(this)) {
				// logger.info("ObjectPropertyAssertion="+objectPropertyAssertion);
				OntologyProperty property = objectPropertyAssertion.getOntologyProperty();
				// logger.info("Property="+property);
				if (property instanceof OntologyObjectProperty) {
					if (((OntologyObjectProperty) property).isLiteralRange()) {
						Object value = objectPropertyAssertion.getValue(this);
						newIndividual.addLiteral(property, value);
					} else {
						OntologyObject assertionObject = objectPropertyAssertion.getAssertionObject(this);
						if (assertionObject != null) {
							newIndividual.getOntResource().addProperty(property.getOntProperty(), assertionObject.getOntResource());
						}
					}
				}
				OntologyObject assertionObject = objectPropertyAssertion.getAssertionObject(this);
				// logger.info("assertionObject="+assertionObject);
				/*OntologyObject assertionObject = null;
					Object value = null;
					if (objectPropertyAssertion.getObject() != null) value = getParameterValues().get(objectPropertyAssertion.getObject());
					if (value instanceof OntologyObject) assertionObject = (OntologyObject)value;
					if (assertionObject == null && getParent() instanceof OEShape) 
						assertionObject = objectPropertyAssertion.getAssertionObject((OEShape)getParent(),editionPatternInstance);*/
				if (assertionObject != null) {
					newIndividual.getOntResource().addProperty(property.getOntProperty(), assertionObject.getOntResource());
				} else {
					// logger.info("assertion objet is null");
				}
			}
		}
		newIndividual.updateOntologyStatements();

		// Register reference
		newIndividual.registerEditionPatternReference(getEditionPatternInstance(), action.getPatternRole());

		return newIndividual;
	}

	protected OntologyClass performAddClass(AddClass action) {
		OntologyClass father = action.getOntologyClass();
		String newClassName = (String) action.getClassName().getBindingValue(this);
		OntologyClass newClass = null;
		try {
			logger.info("Adding class " + newClassName + " as " + father);
			newClass = getProject().getProjectOntology().createOntologyClass(newClassName, father);
			logger.info("Added class " + newClass.getName() + " as " + father);
		} catch (DuplicateURIException e) {
			e.printStackTrace();
		}
		return newClass;
	}

	protected OntologyClass finalizePerformAddClass(AddClass action, OntologyClass newClass) {

		// Register reference
		newClass.registerEditionPatternReference(getEditionPatternInstance(), action.getPatternRole());

		return newClass;
	}

	protected ObjectPropertyStatement performAddObjectPropertyStatement(AddObjectPropertyStatement action) {
		OntologyObjectProperty property = (OntologyObjectProperty) action.getObjectProperty();
		OntologyObject subject = action.getPropertySubject(this);
		OntologyObject object = action.getPropertyObject(this);
		if (property == null) {
			return null;
		}
		if (subject == null) {
			return null;
		}
		if (object == null) {
			return null;
		}
		return subject.addPropertyStatement(property, object);
	}

	protected ObjectPropertyStatement finalizePerformAddObjectPropertyStatement(AddObjectPropertyStatement action,
			ObjectPropertyStatement newObjectPropertyStatement) {
		return newObjectPropertyStatement;
	}

	protected DataPropertyStatement performAddDataPropertyStatement(AddDataPropertyStatement action) {
		OntologyDataProperty property = (OntologyDataProperty) action.getDataProperty();
		OntologyObject subject = action.getPropertySubject(this);
		Object value = action.getValue(this);
		if (property == null) {
			return null;
		}
		if (subject == null) {
			return null;
		}
		if (value == null) {
			return null;
		}
		return (DataPropertyStatement) subject.addLiteral(property, value);
	}

	protected DataPropertyStatement finalizePerformAddDataPropertyStatement(AddDataPropertyStatement action,
			DataPropertyStatement newObjectPropertyStatement) {
		return newObjectPropertyStatement;
	}

	protected FlexoModelObject performDeclarePatternRole(DeclarePatternRole action) {
		return (FlexoModelObject) action.getDeclaredObject(this);
	}

	protected FlexoModelObject finalizePerformDeclarePatternRole(DeclarePatternRole action) {
		FlexoModelObject object = (FlexoModelObject) action.getDeclaredObject(this);

		// Register reference
		object.registerEditionPatternReference(getEditionPatternInstance(), action.getPatternRole());

		return object;
	}

	protected ViewConnector performAddConnector(org.openflexo.foundation.viewpoint.AddConnector action) {

		ViewShape fromShape = action.getFromShape(this);
		ViewShape toShape = action.getToShape(this);
		ViewConnector newConnector = new ViewConnector(fromShape.getShema(), fromShape, toShape);
		ViewObject parent = ViewObject.getFirstCommonAncestor(fromShape, toShape);
		if (parent == null) {
			throw new IllegalArgumentException("No common ancestor");
		}

		// If an overriden graphical representation is defined, use it
		if (getOverridingGraphicalRepresentation(action.getPatternRole()) != null) {
			newConnector.setGraphicalRepresentation(getOverridingGraphicalRepresentation(action.getPatternRole()));
		} else if (action.getPatternRole().getGraphicalRepresentation() != null) {
			newConnector.setGraphicalRepresentation(action.getPatternRole().getGraphicalRepresentation());
		}

		parent.addToChilds(newConnector);

		// Register reference
		newConnector.registerEditionPatternReference(getEditionPatternInstance(), action.getPatternRole());

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Added connector " + newConnector + " under " + parent);
		}
		return newConnector;
	}

	protected ViewConnector finalizePerformAddConnector(org.openflexo.foundation.viewpoint.AddConnector action, ViewConnector newConnector) {
		// Be sure that the newly created shape is updated
		newConnector.update();
		return newConnector;
	}

	protected SubClassStatement performAddIsAProperty(AddIsAStatement action) {
		OntologyObject subject = action.getPropertySubject(this);
		OntologyObject father = action.getPropertyFather(this);
		if (father instanceof OntologyClass) {
			if (subject instanceof OntologyClass) {
				((OntologyClass) subject).getOntResource().addSuperClass(((OntologyClass) father).getOntResource());
			} else if (subject instanceof OntologyIndividual) {
				((OntologyIndividual) subject).getOntResource().addOntClass(((OntologyClass) father).getOntResource());
			}
			subject.updateOntologyStatements();
			return subject.getSubClassStatement(father);
		}
		return null;
	}

	protected SubClassStatement finalizePerformAddIsAProperty(AddIsAStatement action, SubClassStatement subClassStatement) {
		return subClassStatement;
	}

	protected RestrictionStatement performAddRestriction(AddRestrictionStatement action) {
		// System.out.println("Add restriction");

		OntologyProperty property = action.getObjectProperty();
		OntologyObject subject = action.getPropertySubject(this);
		OntologyObject object = action.getPropertyObject(this);

		// System.out.println("property="+property+" "+property.getURI());
		// System.out.println("subject="+subject+" "+subject.getURI());
		// System.out.println("object="+object+" "+object.getURI());
		// System.out.println("restrictionType="+getParameterValues().get(action.getRestrictionType()));
		// System.out.println("cardinality="+getParameterValues().get(action.getCardinality()));

		if (subject instanceof OntologyClass && object instanceof OntologyClass) {
			RestrictionType restrictionType = action.getRestrictionType(this);
			int cardinality = action.getCardinality(this);
			RestrictionStatement restriction = getProject().getProjectOntology().createRestriction((OntologyClass) subject, property,
					restrictionType, cardinality, (OntologyClass) object);

			return restriction;
		}

		return null;
	}

	protected RestrictionStatement finalizePerformAddRestriction(AddRestrictionStatement action, RestrictionStatement restrictionStatement) {
		return restrictionStatement;
	}

	protected View performAddDiagram(org.openflexo.foundation.viewpoint.AddDiagram action) {
		View initialShema = retrieveOEShema();
		AddView addDiagramAction = AddView.actionType.makeNewEmbeddedAction(initialShema.getShemaDefinition().getFolder(), null, this);
		addDiagramAction.newViewName = action.getDiagramName(this);
		addDiagramAction.viewpoint = action.getPatternRole().getViewpoint();
		addDiagramAction.setFolder(initialShema.getShemaDefinition().getFolder());
		addDiagramAction.skipChoosePopup = true;
		addDiagramAction.doAction();
		if (addDiagramAction.hasActionExecutionSucceeded() && addDiagramAction.getNewDiagram() != null) {
			View newShema = addDiagramAction.getNewDiagram().getShema();
			/*ShapePatternRole shapePatternRole = action.getShapePatternRole();
			if (shapePatternRole == null) {
				logger.warning("Sorry, shape pattern role is undefined");
				return newShema;
			}
			// logger.info("Shape pattern role: " + shapePatternRole);
			EditionPatternInstance newEditionPatternInstance = getProject().makeNewEditionPatternInstance(getEditionPattern());
			ViewShape newShape = new ViewShape(newShema);
			if (getEditionPatternInstance().getPatternActor(shapePatternRole) instanceof ViewShape) {
				ViewShape primaryShape = (ViewShape) getEditionPatternInstance().getPatternActor(shapePatternRole);
				newShape.setGraphicalRepresentation(primaryShape.getGraphicalRepresentation());
			} else if (shapePatternRole.getGraphicalRepresentation() != null) {
				newShape.setGraphicalRepresentation(shapePatternRole.getGraphicalRepresentation());
			}
			// Register reference
			newShape.registerEditionPatternReference(newEditionPatternInstance, shapePatternRole);
			newShema.addToChilds(newShape);
			newEditionPatternInstance.setObjectForPatternRole(newShape, shapePatternRole);
			// Duplicates all other pattern roles
			for (PatternRole role : getEditionPattern().getPatternRoles()) {
				if (role != action.getPatternRole() && role != shapePatternRole) {
					FlexoModelObject patternActor = getEditionPatternInstance().getPatternActor(role);
					logger.info("Duplicate pattern actor for role " + role + " value=" + patternActor);
					newEditionPatternInstance.setObjectForPatternRole(patternActor, role);
					patternActor.registerEditionPatternReference(newEditionPatternInstance, role);
				}
			}*/

			return newShema;
		}
		return null;
	}

	protected View finalizePerformAddDiagram(org.openflexo.foundation.viewpoint.AddDiagram action, View newShema) {
		return newShema;
	}

	protected EditionPatternInstance performAddEditionPattern(org.openflexo.foundation.viewpoint.AddEditionPattern action) {
		logger.info("Perform performAddEditionPattern " + action);
		View view = action.getView(this);
		logger.info("View: " + view);
		CreationSchemeAction creationSchemeAction = CreationSchemeAction.actionType.makeNewEmbeddedAction(view, null, this);
		creationSchemeAction.setCreationScheme(action.getCreationScheme());
		for (AddEditionPatternParameter p : action.getParameters()) {
			EditionSchemeParameter param = p.getParam();
			Object value = p.evaluateParameterValue(this);
			logger.info("For parameter " + param + " value is " + value);
			if (value != null) {
				creationSchemeAction.setParameterValue(p.getParam(), p.evaluateParameterValue(this));
			}
		}
		creationSchemeAction.doAction();
		if (creationSchemeAction.hasActionExecutionSucceeded()) {
			logger.info("Successfully performed performAddEditionPattern " + action);
			return creationSchemeAction.getEditionPatternInstance();
		}
		return null;
	}

	protected void performGraphicalAction(org.openflexo.foundation.viewpoint.GraphicalAction action) {
		logger.info("Perform graphical action " + action);
		ViewElement graphicalElement = action.getSubject(this);
		logger.info("Element is " + graphicalElement);
		logger.info("Feature is " + action.getGraphicalFeature());
		logger.info("Value is " + action.getValue().getBindingValue(this));
		action.getGraphicalFeature().applyToGraphicalRepresentation(
				(GraphicalRepresentation<?>) graphicalElement.getGraphicalRepresentation(), action.getValue().getBindingValue(this));
	}

	@Override
	public Object getValue(BindingVariable variable) {
		if (variable instanceof EditionSchemeParameterListPathElement) {
			return this;
		} else if (variable instanceof EditionSchemeParameterPathElement) {
			return getParameterValue(((EditionSchemeParameterPathElement) variable).getParameter());
		} else if (variable instanceof ViewPathElement) {
			if (variable.getVariableName().equals(EditionScheme.TOP_LEVEL)) {
				return retrieveOEShema();
			}
		} else if (variable instanceof PatternRolePathElement) {
			return getEditionPatternInstance().getPatternActor(((PatternRolePathElement) variable).getPatternRole());
		} else if (variable instanceof EditionPatternPathElement) {
			if (variable.getVariableName().equals(EditionScheme.THIS)) {
				return getEditionPatternInstance();
			}
		}

		logger.warning("Unexpected variable requested in EditionSchemeAction " + variable);
		return null;
	}

	public Object getOverridingGraphicalRepresentation(GraphicalElementPatternRole patternRole) {
		// return overridenGraphicalRepresentations.get(patternRole);
		// TODO temporary desactivate overriden GR
		return null;
	}

}
