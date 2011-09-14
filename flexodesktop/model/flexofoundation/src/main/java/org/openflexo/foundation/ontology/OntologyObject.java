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
package org.openflexo.foundation.ontology;

import java.util.Hashtable;
import java.util.Iterator;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.dm.DuplicateMethodSignatureException;
import org.openflexo.foundation.ontology.dm.OntologyObjectStatementsChanged;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;

import com.hp.hpl.jena.ontology.OntClass;
import com.hp.hpl.jena.ontology.OntResource;
import com.hp.hpl.jena.ontology.Restriction;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.ResourceFactory;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;

public abstract class OntologyObject extends AbstractOntologyObject implements InspectableObject {

	private static final Logger logger = Logger.getLogger(OntologyObject.class.getPackage().getName());


	private final Vector<OntologyStatement> _statements;
	private final Vector<PropertyStatement> _annotationStatements;
	private final Vector<ObjectPropertyStatement> _annotationObjectsStatements;
	private final Vector<OntologyStatement> _semanticStatements;
	
	
	public OntologyObject() 
	{
		_statements = new Vector<OntologyStatement>();
		_semanticStatements = new Vector<OntologyStatement>();
		_annotationStatements = new Vector<PropertyStatement>();
		_annotationObjectsStatements = new Vector<ObjectPropertyStatement>();
	}
	
	protected abstract void update();

	@Override
	public abstract String getURI();
	
	@Override
	public abstract FlexoOntology getFlexoOntology();
	
	public abstract OntResource getOntResource();
	
	public Resource getResource()
	{
		return getOntResource();
	}
	
	@Override
	public String getDescription()
	{
		if (getOntResource() != null) {
			return getOntResource().getComment(FlexoLocalization.getCurrentLanguage().getTag());
		}
		return null;
	}

	@Override
	public void setDescription(String aDescription)
	{
		if (getOntResource() != null) {
			getOntResource().setComment(aDescription,FlexoLocalization.getCurrentLanguage().getTag());
		}
	}

	public void updateOntologyStatements()
	{
		//TODO: optimize this (do not always recalculate)
		
		_statements.clear();
		_semanticStatements.clear();
		_annotationStatements.clear();
		_annotationObjectsStatements.clear();
		
		Hashtable<OntClass,Restriction> restrictions = new Hashtable<OntClass,Restriction>();
		
		String OWL = getFlexoOntology().getOntModel().getNsPrefixURI("owl");
		Property ON_CLASS = ResourceFactory.createProperty( OWL + "onClass" );
		Property ON_DATA_RANGE = ResourceFactory.createProperty( OWL + "onDataRange" );
		Property QUALIFIED_CARDINALITY = ResourceFactory.createProperty( OWL + "qualifiedCardinality" );
		Property MIN_QUALIFIED_CARDINALITY = ResourceFactory.createProperty( OWL + "minQualifiedCardinality" );
		Property MAX_QUALIFIED_CARDINALITY = ResourceFactory.createProperty( OWL + "maxQualifiedCardinality" );
				
		//System.out.println("***********************************************");
		
		if (this instanceof OntologyClass) {
		//	DescribeClass dc = new DescribeClass();
			// dc.describeClass(System.out, (OntClass)getOntResource());
			for (Iterator it = ((OntClass)getOntResource()).listSuperClasses(true); it.hasNext();) {
				OntClass s = (OntClass)it.next();
				if (s.isRestriction()) {
					Restriction r = s.asRestriction();
					restrictions.put(s,r);
				}
			}
		}
		
		for (StmtIterator j = getOntResource().listProperties(); j.hasNext();)
		{
			 Statement s = j.nextStatement();
			 
			 OntologyStatement newStatement = null;
			 
			 if (!s.getSubject().equals(getOntResource())) {
				 logger.warning("Inconsistant data: subject is not "+this);
			 }
			 else {
				 Property predicate = s.getPredicate();
				 if (predicate.getURI().equals(TypeStatement.TYPE_URI)) {
					 if (s.getObject() instanceof Resource) {
						 if (((Resource)s.getObject()).getURI().equals(IsClassStatement.CLASS_URI)) {
							 newStatement = new IsClassStatement(this,s);
						 }
						 else if (((Resource)s.getObject()).getURI().equals(IsObjectPropertyStatement.OBJECT_PROPERTY_URI)) {
							 newStatement = new IsObjectPropertyStatement(this,s);
						 }
						 else if (((Resource)s.getObject()).getURI().equals(IsDatatypePropertyStatement.DATATYPE_PROPERTY_URI)) {
							 newStatement = new IsDatatypePropertyStatement(this,s);
						 }
						 else {
							 newStatement = new TypeStatement(this,s);
						 }
					 }
					 else {
						 newStatement = new TypeStatement(this,s);
					 }
				 }
				 else if (predicate.getURI().equals(SubClassStatement.SUB_CLASS_URI)) {
					 if (restrictions.get(s.getObject()) != null) {
						 Restriction r = restrictions.get(s.getObject());
						 // TODO: differenciate ObjectRestrictionStatement and DataRestrictionStatement here
						 if (r.isSomeValuesFromRestriction()) {
							 newStatement = new SomeRestrictionStatement(this,s,r.asSomeValuesFromRestriction());	
						 }
						 else if (r.isAllValuesFromRestriction()) {
							 newStatement = new OnlyRestrictionStatement(this,s,r.asAllValuesFromRestriction());	
						 }
						 else if (r.getProperty(ON_CLASS) != null) {
							 if (r.getProperty(QUALIFIED_CARDINALITY) != null) {
								 newStatement = new ExactRestrictionStatement(this,s,r);	
							 }
							 else if (r.getProperty(MIN_QUALIFIED_CARDINALITY) != null) {
								 newStatement = new MinRestrictionStatement(this,s,r);	
							 }
							 else if (r.getProperty(MAX_QUALIFIED_CARDINALITY) != null) {
								 newStatement = new MaxRestrictionStatement(this,s,r);	
							 }
						 }
						 else if (r.getProperty(ON_DATA_RANGE) != null) {
							 if (r.getProperty(QUALIFIED_CARDINALITY) != null) {
								 newStatement = new ExactDataRestrictionStatement(this,s,r);	
							 }
							 else if (r.getProperty(MIN_QUALIFIED_CARDINALITY) != null) {
								 newStatement = new MinDataRestrictionStatement(this,s,r);	
							 }
							 else if (r.getProperty(MAX_QUALIFIED_CARDINALITY) != null) {
								 newStatement = new MaxDataRestrictionStatement(this,s,r);	
							 }
						 }
						 //System.out.println("*********** for "+s+" restriction "+r+" obtain "+newStatement); 
					 }
					 else {
						 newStatement = new SubClassStatement(this,s);
					 }
				 }
				 else if (predicate.getURI().equals(RangeStatement.RANGE_URI)) {
					 newStatement = new RangeStatement(this,s);
				 }
				 else if (predicate.getURI().equals(DomainStatement.DOMAIN_URI)) {
					 newStatement = new DomainStatement(this,s);
				 }
				 else if (predicate.getURI().equals(InverseOfStatement.INVERSE_OF_URI)) {
					 newStatement = new InverseOfStatement(this,s);
				 }
				 else if (predicate.getURI().equals(SubPropertyStatement.SUB_PROPERTY_URI)) {
					 newStatement = new SubPropertyStatement(this,s);
				 }
				 else {
					 OntologyObject predicateProperty = /*getProject().*/getOntologyLibrary().getOntologyObject(predicate.getURI());
					 if (predicateProperty instanceof OntologyObjectProperty) {
						 newStatement = new ObjectPropertyStatement(this,s);
					 }
					 else if (predicateProperty instanceof OntologyDataProperty) {
						 newStatement = new DataPropertyStatement(this,s);
					 }
					 else {
						 //logger.warning("Inconsistant data: unkwown property "+predicate);
					 }
				 }
			 }
			 
			 if (newStatement != null) {
				 _statements.add(newStatement);
				 if ((newStatement instanceof PropertyStatement) && ((PropertyStatement)newStatement).isAnnotationProperty()) {
					 if (((PropertyStatement)newStatement).hasLitteralValue()) {
						 _annotationStatements.add(((PropertyStatement)newStatement));
					 }
					 else if (newStatement instanceof ObjectPropertyStatement) {
						 _annotationObjectsStatements.add(((ObjectPropertyStatement)newStatement));
					 }
				 }
				 else {
					 _semanticStatements.add(newStatement);
				 }
			 }

		}
		
		 for (OntologyStatement s : _statements) {
			 //System.out.println("> "+s.toString());
		 }
		 
		setChanged();
		notifyObservers(new OntologyObjectStatementsChanged(this));

	}

	public Vector<OntologyStatement> getStatements() 
	{
		return _statements;
	}

	public Vector<OntologyStatement> getSemanticStatements() 
	{
		return _semanticStatements;
	}

	public Vector<PropertyStatement> getAnnotationStatements() 
	{
		return _annotationStatements;
	}

	public Vector<ObjectPropertyStatement> getAnnotationObjectStatements() 
	{
		return _annotationObjectsStatements;
	}

	/**
	 * Return all statement related to supplied property
	 * @param property
	 * @return
	 */
	public Vector<PropertyStatement> getPropertyStatements(OntologyProperty property)
	{
		Vector<PropertyStatement> returned = new Vector<PropertyStatement>();
		for (OntologyStatement statement : getStatements()) {
			if (statement instanceof PropertyStatement) {
				PropertyStatement s = (PropertyStatement)statement;
				if (s.getProperty() == property) {
					returned.add(s);
				}
			}
		}
		return returned;
	}
	
	/**
	 * Return all statement related to supplied property
	 * @param property
	 * @return
	 */
	public Vector<ObjectPropertyStatement> getObjectPropertyStatements(OntologyObjectProperty property)
	{
		Vector<ObjectPropertyStatement> returned = new Vector<ObjectPropertyStatement>();
		for (OntologyStatement statement : getStatements()) {
			if (statement instanceof ObjectPropertyStatement) {
				ObjectPropertyStatement s = (ObjectPropertyStatement)statement;
				if (s.getProperty() == property) {
					returned.add(s);
				}
			}
		}
		return returned;
	}
	
	/**
	 * Return first found statement related to supplied property
	 * @param property
	 * @return
	 */
	public PropertyStatement getPropertyStatement(OntologyProperty property)
	{
		Vector<PropertyStatement> returned = getPropertyStatements(property);
		if (returned.size() > 0) {
			return returned.firstElement();
		}
		return null;
	}
	
	/**
	 * Return statement related to supplied property and value
	 * @param property
	 * @return
	 */
	public PropertyStatement getPropertyStatement(OntologyProperty property, String value)
	{
		Vector<PropertyStatement> returned = getPropertyStatements(property);
		for (PropertyStatement statement : returned) {
			if (statement.hasLitteralValue()
					&& statement.getStringValue().equals(value)) {
				return statement;
			}
		}
		return null;
	}
	
	/**
	 * Return statement related to supplied property and value
	 * @param property
	 * @return
	 */
	public ObjectPropertyStatement getPropertyStatement(OntologyObjectProperty property, OntologyObject object)
	{
		Vector<ObjectPropertyStatement> returned = getObjectPropertyStatements(property);
		for (ObjectPropertyStatement statement : returned) {
			if (statement.getStatementObject() == object) {
				return statement;
			}
		}
		return null;
	}
	
	/**
	 * Return statement related to supplied property, value and language
	 * @param property
	 * @return
	 */
	public PropertyStatement getPropertyStatement(OntologyProperty property, String value, Language language)
	{
		Vector<PropertyStatement> returned = getPropertyStatements(property);
		for (PropertyStatement statement : returned) {
			if (statement.hasLitteralValue()
					&& statement.getStringValue().equals(value)
					&& statement.getLanguage().equals(language.getTag())) {
				return statement;
			}
		}
		return null;
	}
	

	/**
	 * Return first found statement related to supplied property
	 * @param property
	 * @return
	 */
	// TODO: need to handle multiple statements
	public DataPropertyStatement getDataPropertyStatement(OntologyDataProperty property)
	{
		for (OntologyStatement statement : getStatements()) {
			if ((statement instanceof DataPropertyStatement) && (((DataPropertyStatement)statement).getProperty() == property)) {
				return (DataPropertyStatement)statement;
			}
		}
		return null;
	}
	
	/**
	 * Return first found statement related to supplied property
	 * @param property
	 * @return
	 */
	// TODO: need to handle multiple statements
	public ObjectPropertyStatement getObjectPropertyStatement(OntologyObjectProperty property)
	{
		for (OntologyStatement statement : getStatements()) {
			if ((statement instanceof ObjectPropertyStatement) && (((ObjectPropertyStatement)statement).getProperty() == property)) {
				return (ObjectPropertyStatement)statement;
			}
		}
		return null;
	}
	
	/**
	 * Return first found statement related to supplied property
	 * @param property
	 * @return
	 */
	// TODO: need to handle multiple statements
	public ObjectRestrictionStatement getObjectRestrictionStatement(OntologyProperty property, OntologyClass object)
	{
		for (OntologyStatement statement : getStatements()) {
			if ((statement instanceof ObjectRestrictionStatement) 
					&& (((ObjectRestrictionStatement)statement).getProperty() == property)
					&& (((ObjectRestrictionStatement)statement).getObject() == object)) {
				return (ObjectRestrictionStatement)statement;
			}
		}
		return null;
	}
	
	/**
	 * Return first found statement related to supplied property
	 * @param property
	 * @return
	 */
	// TODO: need to handle multiple statements
	public SubClassStatement getSubClassStatement(OntologyObject father)
	{
		for (OntologyStatement statement : getStatements()) {
			if ((statement instanceof SubClassStatement) && ((SubClassStatement)statement).getParent().equals(father)) {
				return (SubClassStatement)statement;
			}
		}
		return null;
	}
	
	public abstract boolean isSuperConceptOf(OntologyObject concept);
	
	public boolean isSubConceptOf(OntologyObject concept)
	{
		return concept.isSuperConceptOf(concept);
	}
	
    public PropertyStatement createNewCommentAnnotation()
    {
    	return createNewAnnotation("http://www.w3.org/2000/01/rdf-schema#comment");
     }
    
    public PropertyStatement createNewLabelAnnotation()
    {
    	return createNewAnnotation("http://www.w3.org/2000/01/rdf-schema#label");
    }
    
    public PropertyStatement createNewSeeAlsoAnnotation()
    {
    	return createNewAnnotation("http://www.w3.org/2000/01/rdf-schema#seeAlso",this);
    }
    
    public PropertyStatement createNewIsDefinedByAnnotation()
    {
    	return createNewAnnotation("http://www.w3.org/2000/01/rdf-schema#isDefinedBy",getFlexoOntology());
    }
    
    public PropertyStatement createNewAnnotation(String propertyURI)
    {
       	OntologyProperty property = getOntologyLibrary().getProperty(propertyURI);
    	if (property != null) {
    		return addPropertyStatement(property,"label",Language.ENGLISH);
    	}
    	else {
    		logger.warning("Could not find property "+property);
    		return null;
    	}
    }
   
    public PropertyStatement createNewAnnotation(String propertyURI, OntologyObject object)
    {
    	if (object == null) {
    		logger.warning("Cannot create annotation "+propertyURI+" with null object");
    		return null;
    	}
       	OntologyProperty property = getOntologyLibrary().getProperty(propertyURI);
    	if (property instanceof OntologyObjectProperty) {
    		return addPropertyStatement((OntologyObjectProperty)property,object);
    	}
    	else {
    		logger.warning("Could not find property "+property);
    		return null;
    	}
    }
   

    public void deleteAnnotation(PropertyStatement annotation) throws DuplicateMethodSignatureException
    {
    	removePropertyStatement(annotation);
    }

    public boolean isAnnotationAddable()
    {
        return (!getIsReadOnly());
    }

   public boolean isAnnotationDeletable(PropertyStatement annotation)
    {
    	return (!getIsReadOnly());
    }

   public ObjectPropertyStatement addPropertyStatement (OntologyObjectProperty property, OntologyObject object)
   {
	   //System.out.println("Subject: "+this+" resource="+getOntResource());
	   //System.out.println("Predicate: "+property+" resource="+property.getOntProperty());
	   //System.out.println("Object: "+object+" resource="+object.getOntResource());
	   
	   getOntResource().addProperty(
			   property.getOntProperty(), 
			   object.getResource());
	   updateOntologyStatements();
	   return getPropertyStatement(property, object);
   }

   public PropertyStatement addPropertyStatement (OntologyProperty property, String value)
   {
	   getOntResource().addProperty(
			   property.getOntProperty(), 
			   value);
	   updateOntologyStatements();
	   return getPropertyStatement(property, value);
   }

   public PropertyStatement addPropertyStatement (OntologyProperty property, String value, Language language)
   {
	   //System.out.println("****** Add statement for property "+property.getName()+" value="+value+" language="+language);
	   getOntResource().addProperty(
			   property.getOntProperty(), 
			   value,
			   language.getTag());
	   updateOntologyStatements();
	   return getPropertyStatement(property, value, language);
   }

   public void removePropertyStatement (PropertyStatement statement)
   {
	   getFlexoOntology().getOntModel().remove(statement.getStatement());
	   updateOntologyStatements();
   }

   public boolean getIsReadOnly()
   {
	   return (getFlexoOntology().getIsReadOnly());
   }
 
}
