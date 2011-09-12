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
package org.openflexo.foundation.ontology.action;

import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.NameChanged;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.ontology.DuplicateURIException;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.ontology.ObjectPropertyStatement;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.RestrictionStatement;
import org.openflexo.foundation.ontology.SubClassStatement;
import org.openflexo.foundation.ontology.RestrictionStatement.RestrictionType;
import org.openflexo.foundation.ontology.calc.AddClass;
import org.openflexo.foundation.ontology.calc.AddIndividual;
import org.openflexo.foundation.ontology.calc.AddIsAProperty;
import org.openflexo.foundation.ontology.calc.AddObjectProperty;
import org.openflexo.foundation.ontology.calc.AddRestriction;
import org.openflexo.foundation.ontology.calc.DataPropertyAssertion;
import org.openflexo.foundation.ontology.calc.DeclarePatternRole;
import org.openflexo.foundation.ontology.calc.EditionAction;
import org.openflexo.foundation.ontology.calc.EditionPattern;
import org.openflexo.foundation.ontology.calc.EditionScheme;
import org.openflexo.foundation.ontology.calc.GoToAction;
import org.openflexo.foundation.ontology.calc.ObjectPropertyAssertion;
import org.openflexo.foundation.ontology.calc.PatternRole;
import org.openflexo.foundation.ontology.calc.ShapePatternRole;
import org.openflexo.foundation.ontology.shema.OEConnector;
import org.openflexo.foundation.ontology.shema.OEShape;
import org.openflexo.foundation.ontology.shema.OEShema;
import org.openflexo.foundation.ontology.shema.OEShemaObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.inspector.LocalizedString;
import org.openflexo.toolbox.StringUtils;

import com.hp.hpl.jena.ontology.DatatypeProperty;
import com.hp.hpl.jena.ontology.Individual;
import com.hp.hpl.jena.ontology.ObjectProperty;
import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntModel;

public abstract class EditionSchemeAction<A extends EditionSchemeAction<?>> 
extends FlexoAction<A,FlexoModelObject,FlexoModelObject>
{

	private static final Logger logger = Logger.getLogger(EditionSchemeAction.class.getPackage().getName());

	protected Hashtable<String,Object> parameterValues;

	public EditionSchemeAction(
			FlexoActionType<A, FlexoModelObject, FlexoModelObject> actionType,
			FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection,
			FlexoEditor editor)
	{
		super(actionType, focusedObject, globalSelection, editor);
		parameterValues = new Hashtable<String,Object>();
	}

	public FlexoProject getProject()
	{
		if (getFocusedObject() != null) return getFocusedObject().getProject();
		return null;
	}

	public EditionPattern getEditionPattern()
	{
		if (getEditionScheme() != null) return getEditionScheme().getEditionPattern();
		return null;
	}

	public Hashtable<String,Object> getParameterValues() 
	{
		return parameterValues;
	}

	public abstract EditionScheme getEditionScheme();

	public abstract EditionPatternInstance getEditionPatternInstance();

	protected abstract OEShema retrieveOEShema();
	
	protected abstract Object getOverridenGraphicalRepresentation(); 

	protected void applyEditionActions()
	{
		Hashtable<EditionAction,FlexoModelObject> performedActions = new Hashtable<EditionAction, FlexoModelObject>();
		
		
		// Perform actions
		for (EditionAction action : getEditionScheme().getActions()) {
			if (action.evaluateCondition(parameterValues)) {
				if (action instanceof org.openflexo.foundation.ontology.calc.AddShape) {
					logger.info("Add shape with patternRole="+action.getPatternRole());
					OEShape newShape = performAddShape((org.openflexo.foundation.ontology.calc.AddShape)action);
					if (newShape != null) {
						getEditionPatternInstance().setObjectForPatternRole(newShape, action.getPatternRole());
						performedActions.put(action,newShape);
					}
				}
				else if (action instanceof org.openflexo.foundation.ontology.calc.AddConnector) {
					logger.info("Add connector with patternRole="+action.getPatternRole());
					OEConnector newConnector = performAddConnector((org.openflexo.foundation.ontology.calc.AddConnector)action);
					if (newConnector != null) {
						getEditionPatternInstance().setObjectForPatternRole(newConnector, action.getPatternRole());
						performedActions.put(action,newConnector);
					}
				}
				else if (action instanceof AddIndividual) {
					logger.info("Add individual with patternRole="+action.getPatternRole());
					OntologyIndividual newIndividual = performAddIndividual((AddIndividual)action);
					if (newIndividual != null) {
						getEditionPatternInstance().setObjectForPatternRole(newIndividual, action.getPatternRole());
						performedActions.put(action,newIndividual);
					}
				}
				else if (action instanceof AddClass) {
					logger.info("Add class with patternRole="+action.getPatternRole());
					OntologyClass newClass = performAddClass((AddClass)action);
					if (newClass != null) {
						getEditionPatternInstance().setObjectForPatternRole(newClass, action.getPatternRole());
						performedActions.put(action,newClass);
					}
				}
				else if (action instanceof AddObjectProperty) {
					logger.info("Add object property with patternRole="+action.getPatternRole());
					ObjectPropertyStatement statement = performAddObjectProperty((org.openflexo.foundation.ontology.calc.AddObjectProperty)action);
					if (statement != null) {
						getEditionPatternInstance().setObjectForPatternRole(statement, action.getPatternRole());
						performedActions.put(action,statement);
					}
					else {
						logger.warning("Could not perform AddObjectProperty for role "+action.getPatternRole());
					}
				}
				else if (action instanceof AddIsAProperty) {
					logger.info("Add isA property with patternRole="+action.getPatternRole());
					SubClassStatement statement = performAddIsAProperty((AddIsAProperty)action);
					if (statement != null) {
						getEditionPatternInstance().setObjectForPatternRole(statement, action.getPatternRole());
						performedActions.put(action,statement);
					}
				}
				else if (action instanceof AddRestriction) {
					logger.info("Add restriction with patternRole="+action.getPatternRole());
					RestrictionStatement statement = performAddRestriction((AddRestriction)action);
					if (statement != null) {
						getEditionPatternInstance().setObjectForPatternRole(statement, action.getPatternRole());
						performedActions.put(action,statement);
					}
				}
				else if (action instanceof DeclarePatternRole) {
					logger.info("Declare object with patternRole="+action.getPatternRole());
					FlexoModelObject declaredObject = performDeclarePatternRole((DeclarePatternRole)action);
					logger.info("Found declared object: "+declaredObject);
					if (declaredObject != null) {
						getEditionPatternInstance().setObjectForPatternRole(declaredObject, action.getPatternRole());
						performedActions.put(action,declaredObject);
					}
				}
				else if (action instanceof org.openflexo.foundation.ontology.calc.AddShema) {
					logger.info("Add shema with patternRole="+action.getPatternRole());
					OEShema newShema = performAddShema((org.openflexo.foundation.ontology.calc.AddShema)action);
					if (newShema != null) {
						getEditionPatternInstance().setObjectForPatternRole(newShema, action.getPatternRole());
						performedActions.put(action,newShema);
					}
				}
			}
		}

		// All object are now created, finalize relationships between them
		for (EditionAction action : performedActions.keySet()) {
			if (action instanceof AddIndividual) {
				finalizePerformAddIndividual((AddIndividual)action,(OntologyIndividual)performedActions.get(action));
			}
			else if (action instanceof AddClass) {
				finalizePerformAddClass((AddClass)action,(OntologyClass)performedActions.get(action));
			}
			else if (action instanceof AddObjectProperty) {
				finalizePerformAddObjectProperty((AddObjectProperty)action,(ObjectPropertyStatement)performedActions.get(action));
			}
			else if (action instanceof AddIsAProperty) {
				finalizePerformAddIsAProperty((AddIsAProperty)action,(SubClassStatement)performedActions.get(action));
			}
			else if (action instanceof AddRestriction) {
				finalizePerformAddRestriction((AddRestriction)action,(RestrictionStatement)performedActions.get(action));
			}
			else if (action instanceof DeclarePatternRole) {
				FlexoModelObject declaredObject = performDeclarePatternRole((DeclarePatternRole)action);
				if (declaredObject != null) {
					getEditionPatternInstance().setObjectForPatternRole(declaredObject, action.getPatternRole());
					performedActions.put(action,declaredObject);
				}
				finalizePerformDeclarePatternRole((DeclarePatternRole)action);
			}
		}

		// Finalize shape creation at the end to be sure labels are now correctely bound
		for (EditionAction action : performedActions.keySet()) {
			if (action instanceof org.openflexo.foundation.ontology.calc.AddShape) {
				finalizePerformAddShape((org.openflexo.foundation.ontology.calc.AddShape)action,(OEShape)performedActions.get(action));
			}
			if (action instanceof org.openflexo.foundation.ontology.calc.AddConnector) {
				finalizePerformAddConnector((org.openflexo.foundation.ontology.calc.AddConnector)action,(OEConnector)performedActions.get(action));
			}
			if (action instanceof org.openflexo.foundation.ontology.calc.AddShema) {
				finalizePerformAddShema((org.openflexo.foundation.ontology.calc.AddShema)action,(OEShema)performedActions.get(action));
			}
		}
		
		// At this end, look after GoToAction
		for (EditionAction action : getEditionScheme().getActions()) {
			if (action.evaluateCondition(parameterValues)) {
				if (action instanceof GoToAction) {
					performGoToAction((GoToAction)action);
				}
			}
		}
	}

	protected OEShape performAddShape(org.openflexo.foundation.ontology.calc.AddShape action)
	{
		OEShape newShape = new OEShape(retrieveOEShema());
		
		// If an overriden graphical representation is defined, use it
		if (getOverridenGraphicalRepresentation() != null) 
			newShape.setGraphicalRepresentation(getOverridenGraphicalRepresentation());
		
		// Otherwise take the default one defined in Pattern Role
		else if (action.getPatternRole().getGraphicalRepresentation() != null) 
			newShape.setGraphicalRepresentation(action.getPatternRole().getGraphicalRepresentation());
		
		// Register reference
		newShape.registerEditionPatternReference(getEditionPatternInstance(), action.getPatternRole());		
		
		//logger.info("container="+action.getContainer());
		
		OEShemaObject container = action.getContainer(this);
		
		container.addToChilds(newShape);   
		logger.info("Added shape "+newShape+" under "+container);
		return newShape;
	}

	protected OEShape finalizePerformAddShape(org.openflexo.foundation.ontology.calc.AddShape action, OEShape newShape)
	{
		// We need to renotify here because if label is bound to a semantic. In this case, 
		// while beeing created, shape didn't have sufficient data to retrieve a label
		// which was null. Now, we are sure that the shape is bound, and label can be 
		// retrieved. That's why we notify this.
		newShape.setChanged();
		newShape.notifyObservers(new NameChanged(null,newShape.getName()));
		return newShape;
	}
	
	protected OntologyIndividual performAddIndividual(AddIndividual action)
	{
		OntologyClass father = action.getOntologyClass();
		//OntologyObject father = action.getOntologyObject(getProject());
		//System.out.println("Individual name param = "+action.getIndividualNameParameter());
		String individualName = (String)getParameterValues().get(action.getIndividualNameParameter().getName());
		//System.out.println("individualName="+individualName);
		OntologyIndividual newIndividual;
		try {
			newIndividual = getProject().getProjectOntology().createOntologyIndividual(individualName,father);
			logger.info("Added individual "+newIndividual.getName()+" as "+father);
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

	protected OntologyIndividual finalizePerformAddIndividual(AddIndividual action, OntologyIndividual newIndividual)
	{
		for (DataPropertyAssertion dataPropertyAssertion : action.getDataAssertions()) {
			if (dataPropertyAssertion.evaluateCondition(parameterValues)) {
				//System.out.println("dataPropertyAssertion="+dataPropertyAssertion);
				//System.out.println("dataPropertyAssertion.getDataPropertyURI()="+dataPropertyAssertion.getDataPropertyURI());
				OntologyProperty property = dataPropertyAssertion.getOntologyProperty();
				//System.out.println("property="+property);
				Object value = getParameterValues().get(dataPropertyAssertion.getValueParameter().getName());
				if (value instanceof String) {
					newIndividual.getOntResource().addProperty(
							property.getOntProperty(), 
							(String)value);
				}
				else if (value instanceof LocalizedString) {
					if (!StringUtils.isEmpty(((LocalizedString)value).string)) {
						newIndividual.getOntResource().addProperty(
								property.getOntProperty(), 
								((LocalizedString)value).string,
								((LocalizedString)value).language.getTag());
					}
				}
				else if (value instanceof Double) {
						newIndividual.getOntResource().addLiteral(property.getOntProperty(),((Double)value).doubleValue());
				}
				else if (value instanceof Float) {
					newIndividual.getOntResource().addLiteral(property.getOntProperty(),((Float)value).floatValue());
				}
				else if (value instanceof Long) {
					newIndividual.getOntResource().addLiteral(property.getOntProperty(),((Long)value).longValue());
				}
				else if (value instanceof Integer) {
					newIndividual.getOntResource().addLiteral(property.getOntProperty(),((Integer)value).longValue());
				}
				else if (value instanceof Short) {
					newIndividual.getOntResource().addLiteral(property.getOntProperty(),((Short)value).longValue());
				}
				else if (value instanceof Boolean) {
					newIndividual.getOntResource().addLiteral(property.getOntProperty(),((Boolean)value).booleanValue());
				}
				else if (value != null) {
					logger.warning("Unexpected "+value+" of "+value.getClass());
				}
				else {
					// If value is null, just ignore
				}
			}
		}
		for (ObjectPropertyAssertion objectPropertyAssertion : action.getObjectAssertions()) {
			if (objectPropertyAssertion.evaluateCondition(parameterValues)) {
				OntologyProperty property = objectPropertyAssertion.getOntologyProperty();
				OntologyObject assertionObject = objectPropertyAssertion.getAssertionObject(this);					
				//logger.info("Now handle object propertyt assertion "+property);
				//logger.info("assertionObject="+assertionObject);
				/*OntologyObject assertionObject = null;
					Object value = null;
					if (objectPropertyAssertion.getObject() != null) value = getParameterValues().get(objectPropertyAssertion.getObject());
					if (value instanceof OntologyObject) assertionObject = (OntologyObject)value;
					if (assertionObject == null && getParent() instanceof OEShape) 
						assertionObject = objectPropertyAssertion.getAssertionObject((OEShape)getParent(),editionPatternInstance);*/
				if (assertionObject != null) {
					newIndividual.getOntResource().addProperty(property.getOntProperty(), assertionObject.getOntResource());
				}
			}
		}
		newIndividual.updateOntologyStatements();
		return newIndividual;
	}

	protected OntologyClass performAddClass(AddClass action)
	{
		OntologyClass father = action.getOntologyClass();
		String newClassName = (String)getParameterValues().get(action.getNewClassNameParameter().getName());
		OntologyClass newClass = null;
		try {
			newClass = getProject().getProjectOntology().createOntologyClass(newClassName, father);
			logger.info("Added class "+newClass.getName()+" as "+father);
		} catch (DuplicateURIException e) {
			e.printStackTrace();
		}		
		return newClass;
	}

	protected OntologyClass finalizePerformAddClass(AddClass action, OntologyClass newClass)
	{
		return newClass;
	}

	protected ObjectPropertyStatement performAddObjectProperty(AddObjectProperty action)
	{
		OntologyObjectProperty property = (OntologyObjectProperty)action.getObjectProperty();
		OntologyObject subject = action.getPropertySubject(this);
		OntologyObject object = action.getPropertyObject(this);
		if (property == null) return null;
		if (subject == null) return null;
		if (object == null) return null;
		subject.getOntResource().addProperty(property.getOntProperty(), object.getOntResource());
		subject.updateOntologyStatements();
		return subject.getObjectPropertyStatement(property);
	}

	protected ObjectPropertyStatement finalizePerformAddObjectProperty(AddObjectProperty action,
			ObjectPropertyStatement objectPropertyStatement) 
	{
		return objectPropertyStatement;
	}
	
	protected FlexoModelObject performDeclarePatternRole(DeclarePatternRole action)
	{
		return (FlexoModelObject)action.getDeclaredObject(this);
	}

	protected FlexoModelObject finalizePerformDeclarePatternRole(DeclarePatternRole action)
	{
		FlexoModelObject object = (FlexoModelObject)action.getDeclaredObject(this);
		if (!(object instanceof OntologyObject)) {
			object.registerEditionPatternReference(getEditionPatternInstance(), action.getPatternRole());		
		}
		return object;
	}
	
	protected OEConnector performAddConnector(org.openflexo.foundation.ontology.calc.AddConnector action)
	{
		OEShape fromShape = action.getFromShape(this);
		OEShape toShape = action.getToShape(this);
		OEConnector newConnector = new OEConnector(fromShape.getShema(),fromShape,toShape);
		OEShemaObject parent = OEShemaObject.getFirstCommonAncestor(fromShape, toShape);
		if (parent == null) throw new IllegalArgumentException("No common ancestor");

		// If an overriden graphical representation is defined, use it
		if (getOverridenGraphicalRepresentation() != null) 
			newConnector.setGraphicalRepresentation(getOverridenGraphicalRepresentation());
		
		// Otherwise take the default one defined in Pattern Role
		else if (action.getPatternRole().getGraphicalRepresentation() != null) 
			newConnector.setGraphicalRepresentation(action.getPatternRole().getGraphicalRepresentation());

		
		parent.addToChilds(newConnector);
		
		// Register reference
		newConnector.registerEditionPatternReference(getEditionPatternInstance(), action.getPatternRole());		
		
		logger.info("Added connector "+newConnector+" under "+parent);
		return newConnector;
	}

	protected OEConnector finalizePerformAddConnector(org.openflexo.foundation.ontology.calc.AddConnector action, OEConnector newConnector)
	{
		// We need to renotify here because if label is bound to a semantic. In this case, 
		// while beeing created, shape didn't have sufficient data to retrieve a label
		// which was null. Now, we are sure that the shape is bound, and label can be 
		// retrieved. That's why we notify this.
		newConnector.setChanged();
		newConnector.notifyObservers(new NameChanged(null,newConnector.getName()));
		return newConnector;
	}

	protected SubClassStatement performAddIsAProperty(AddIsAProperty action)
	{
		OntologyObject subject = action.getPropertySubject(this);
		OntologyObject father = action.getPropertyFather(this);
		if (father instanceof OntologyClass) {
			if (subject instanceof OntologyClass) {
				((OntologyClass)subject).getOntResource().addSuperClass(((OntologyClass)father).getOntResource());
			}
			else if (subject instanceof OntologyIndividual) {
				((OntologyIndividual)subject).getOntResource().addOntClass(((OntologyClass)father).getOntResource());
			}
			subject.updateOntologyStatements();
			return subject.getSubClassStatement(father);
		}
		return null;
	}

	protected SubClassStatement finalizePerformAddIsAProperty(AddIsAProperty action,
			SubClassStatement subClassStatement) 
	{
		return subClassStatement;
	}


	protected RestrictionStatement performAddRestriction(AddRestriction action)
	{
		//System.out.println("Add restriction");
		
		OntologyProperty property = action.getObjectProperty();
		OntologyObject subject = action.getPropertySubject(this);
		OntologyObject object = action.getPropertyObject(this);
		
		//System.out.println("property="+property+" "+property.getURI());
		//System.out.println("subject="+subject+" "+subject.getURI());
		//System.out.println("object="+object+" "+object.getURI());
		//System.out.println("restrictionType="+getParameterValues().get(action.getRestrictionType()));
		//System.out.println("cardinality="+getParameterValues().get(action.getCardinality()));

		if (subject instanceof OntologyClass && object instanceof OntologyClass) {

			RestrictionType restrictionType = RestrictionType.valueOf((String)getParameterValues().get(action.getRestrictionTypeParameter().getName()));
			int cardinality = (Integer)getParameterValues().get(action.getCardinalityParameter().getName());
			if (restrictionType == null) restrictionType = RestrictionType.Some;

			RestrictionStatement restriction = getProject().getProjectOntology().createRestriction(
					(OntologyClass)subject, property, restrictionType, cardinality, (OntologyClass)object);

			return restriction;
		}

		return null;
	}

	protected RestrictionStatement finalizePerformAddRestriction(AddRestriction action,
			RestrictionStatement restrictionStatement) 
	{
		return restrictionStatement;
	}

	protected OEShema performAddShema(org.openflexo.foundation.ontology.calc.AddShema action)
	{
		OEShema initialShema = retrieveOEShema();
		AddShema addShemaAction = AddShema.actionType.makeNewEmbeddedAction(initialShema.getShemaDefinition().getFolder(), null, this);
		addShemaAction.newShemaName = action.getShemaName(this);
		addShemaAction.calc = initialShema.getCalc();
		addShemaAction.setFolder(initialShema.getShemaDefinition().getFolder());
		addShemaAction.doAction();
		if (addShemaAction.hasActionExecutionSucceeded()) {
			OEShema newShema = addShemaAction.getNewShema().getShema();
			ShapePatternRole shapePatternRole = action.retrieveShapePatternRole();
			if (shapePatternRole == null) {
				logger.warning("Sorry, shape pattern role is undefined");
				return newShema;
			}
			logger.info("Shape pattern role: "+shapePatternRole);
			EditionPatternInstance newEditionPatternInstance = getProject().makeNewEditionPatternInstance(getEditionPattern());
			OEShape newShape = new OEShape(newShema);
			if (getEditionPatternInstance().getPatternActor(shapePatternRole) instanceof OEShape) {
				OEShape primaryShape = (OEShape)getEditionPatternInstance().getPatternActor(shapePatternRole);
				newShape.setGraphicalRepresentation(primaryShape.getGraphicalRepresentation());
			}
			else if (shapePatternRole.getGraphicalRepresentation() != null) {
				newShape.setGraphicalRepresentation(shapePatternRole.getGraphicalRepresentation());
			}
			// Register reference
			newShape.registerEditionPatternReference(newEditionPatternInstance,shapePatternRole);		
			newShema.addToChilds(newShape);   
			newEditionPatternInstance.setObjectForPatternRole(newShape,shapePatternRole);
			// Duplicates all other pattern roles
			for (PatternRole role : getEditionPattern().getPatternRoles()) {
				if (role != action.getPatternRole() && role != shapePatternRole) {
					FlexoModelObject patternActor = getEditionPatternInstance().getPatternActor(role);
					logger.info("Duplicate pattern actor for role "+role+" value="+patternActor);
					newEditionPatternInstance.setObjectForPatternRole(patternActor,role);
					if (!(patternActor instanceof OntologyObject)) {
						patternActor.registerEditionPatternReference(newEditionPatternInstance,role);		
					}
				}
			}
			return newShema;
		}
		return null;
	}

	protected OEShema finalizePerformAddShema(org.openflexo.foundation.ontology.calc.AddShema action, OEShema newShema)
	{
		return newShema;
	}
	
	protected void performGoToAction(org.openflexo.foundation.ontology.calc.GoToAction action)
	{
		FlexoModelObject target = action.getTargetObject(this);
		logger.info("Focus and select object "+target);
		getEditor().focusOn(target);
	}


	private void pourNePasOublier() 
	{
		String FLEXO_CONCEPTS_URI = "http://www.agilebirds.com/flexo/ontologies/FlexoConceptsOntology.owl";
		String FLEXO_MODEL_OBJECT = FLEXO_CONCEPTS_URI+"#FlexoModelObject";
		String LINKED_TO_MODEL_PROPERTY = FLEXO_CONCEPTS_URI+"#linkedToModel";
		String CLASS_NAME_PROPERTY = FLEXO_CONCEPTS_URI+"#className";
		String FLEXO_ID_PROPERTY = FLEXO_CONCEPTS_URI+"#flexoID";
		String RESOURCE_NAME_PROPERTY = FLEXO_CONCEPTS_URI+"#resourceName";

		String BOT_URI = "http://www.agilebirds.com/flexo/ontologies/OrganizationTree/BasicOrganizationTree.owl";
		String COMPANY_NAME = BOT_URI+"#companyName";

		String BOT_EDITOR_URI = "http://www.agilebirds.com/flexo/ontologies/Calcs/BasicOrganizationTreeEditor.owl";
		String BOT_COMPANY = BOT_EDITOR_URI+"#BOTCompany";

		OntModel ontModel = getProject().getProjectOntology().getOntModel();

		OntClass fooClass = ontModel.createClass(getProject().getProjectOntology().getOntologyURI()+"#"+"foo");
		OntClass foo2Class = ontModel.createClass(getProject().getProjectOntology().getOntologyURI()+"#"+"foo2");
		foo2Class.addComment("Test de commentaire", "FR");
		foo2Class.addComment("Comment test", "EN");
		foo2Class.addSuperClass(fooClass);

		FlexoProcess process = getProject().getWorkflow().getRootFlexoProcess();
		OntClass flexoModelObject = ontModel.getOntClass(FLEXO_MODEL_OBJECT);
		ObjectProperty linkedToModelProperty = ontModel.getObjectProperty(LINKED_TO_MODEL_PROPERTY);
		DatatypeProperty classNameProperty = ontModel.getDatatypeProperty(CLASS_NAME_PROPERTY);
		DatatypeProperty flexoIDProperty = ontModel.getDatatypeProperty(FLEXO_ID_PROPERTY);
		DatatypeProperty resourceNameProperty = ontModel.getDatatypeProperty(RESOURCE_NAME_PROPERTY);

		Individual myRootFlexoProcess = ontModel.createIndividual(getProject().getProjectOntology().getURI()+"#MyRootProcess", flexoModelObject);
		myRootFlexoProcess.addProperty(classNameProperty,process.getClass().getName());
		myRootFlexoProcess.addProperty(flexoIDProperty,process.getSerializationIdentifier());
		myRootFlexoProcess.addProperty(resourceNameProperty,process.getFlexoResource().getFullyQualifiedName());

		OntClass botCompany = ontModel.getOntClass(BOT_COMPANY);
		DatatypeProperty companyNameProperty = ontModel.getDatatypeProperty(COMPANY_NAME);
		Individual agileBirdsCompany = ontModel.createIndividual(getProject().getProjectOntology().getURI()+"#AgileBirds", botCompany);
		agileBirdsCompany.addProperty(companyNameProperty,"Agile Birds S.A.");

		agileBirdsCompany.addProperty(linkedToModelProperty, myRootFlexoProcess);
	}

}
