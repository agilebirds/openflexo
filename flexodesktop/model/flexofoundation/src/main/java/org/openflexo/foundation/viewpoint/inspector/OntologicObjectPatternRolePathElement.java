package org.openflexo.foundation.viewpoint.inspector;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.FinalBindingPathElementImpl;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.ontology.DataPropertyStatement;
import org.openflexo.foundation.ontology.IsAStatement;
import org.openflexo.foundation.ontology.ObjectPropertyStatement;
import org.openflexo.foundation.ontology.OntologyClass;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.OntologyStatement;
import org.openflexo.foundation.ontology.RestrictionStatement;
import org.openflexo.foundation.ontology.dm.URIChanged;
import org.openflexo.foundation.ontology.dm.URINameChanged;
import org.openflexo.foundation.viewpoint.ClassPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyPatternRole;
import org.openflexo.foundation.viewpoint.DataPropertyStatementPatternRole;
import org.openflexo.foundation.viewpoint.IndividualPatternRole;
import org.openflexo.foundation.viewpoint.IsAStatementPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyPatternRole;
import org.openflexo.foundation.viewpoint.ObjectPropertyStatementPatternRole;
import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;
import org.openflexo.foundation.viewpoint.PropertyPatternRole;
import org.openflexo.foundation.viewpoint.RestrictionStatementPatternRole;

public abstract class OntologicObjectPatternRolePathElement<E extends Bindable,T extends OntologyObject> extends PatternRolePathElement<E,T>
{
	private static final Logger logger = Logger.getLogger(OntologicObjectPatternRolePathElement.class.getPackage().getName());

	private FinalBindingPathElementImpl uriNameProperty;
	private FinalBindingPathElementImpl uriProperty;
	protected List<BindingPathElement> allProperties;
	
	public OntologicObjectPatternRolePathElement(OntologicObjectPatternRole aPatternRole, E container) 
	{
		super(aPatternRole,container);
		allProperties = new Vector<BindingPathElement>();
		uriNameProperty = new FinalBindingPathElementImpl<T,String>(URINameChanged.URI_NAME_KEY,TypeUtils.getBaseClass(getType()),String.class,true,"uri_name_as_supplied_in_ontology") {
			@Override
			public String getBindingValue(T target, BindingEvaluationContext context) 
			{
				return ((OntologyObject)target).getName();
			}
		    @Override
		    public void setBindingValue(String value, T target, BindingEvaluationContext context) 
		    {
		    	try {
		    		logger.info("Rename URI of object "+target+" with "+value);
					target.setName(value);
				} catch (Exception e) {
					logger.warning("Unhandled exception: "+e);
					e.printStackTrace();
				}
		    }
		};
		allProperties.add(uriNameProperty);
		uriProperty = new FinalBindingPathElementImpl<T,String>(URIChanged.URI_KEY,TypeUtils.getBaseClass(getType()),String.class,false,"uri_as_supplied_in_ontology") {
			@Override
			public String getBindingValue(T target, BindingEvaluationContext context) 
			{
				return ((OntologyObject)target).getURI();
			}
		    @Override
		    public void setBindingValue(String value, T target, BindingEvaluationContext context) 
		    {
		    	// not relevant because not settable
		    }
		};
		allProperties.add(uriProperty);
	}

	public BindingPathElement getUriNameProperty() 
	{
		return uriNameProperty;
	}

	public BindingPathElement getUriProperty() 
	{
		return uriProperty;
	}

	@Override
	public List<BindingPathElement> getAllProperties() 
	{
		return allProperties;
	}
	
	public static class OntologicClassPatternRolePathElement<E extends Bindable> extends OntologicObjectPatternRolePathElement<E,OntologyClass>
	{
		public OntologicClassPatternRolePathElement(ClassPatternRole aPatternRole, E container) 
		{
			super(aPatternRole,container);
		}
	}

	public static class OntologicIndividualPatternRolePathElement<E extends Bindable> extends OntologicObjectPatternRolePathElement<E,OntologyIndividual>
	{
		Vector<StatementPathElement> accessibleStatements;
		
		public OntologicIndividualPatternRolePathElement(IndividualPatternRole aPatternRole, E container) 
		{
			super(aPatternRole,container);
			accessibleStatements = new Vector<StatementPathElement>();
			if (aPatternRole.getOntologicType() != null) {
				for (final OntologyProperty property : aPatternRole.getOntologicType().getPropertiesTakingMySelfAsDomain()) {
					StatementPathElement propertyPathElement = null;
					if (property instanceof OntologyObjectProperty) {
						propertyPathElement = new ObjectPropertyStatementPathElement(this, (OntologyObjectProperty)property);
					}
					else if (property instanceof OntologyDataProperty) {
						propertyPathElement = new DataPropertyStatementPathElement(this, (OntologyDataProperty)property);
					}
					if (propertyPathElement != null) {
						accessibleStatements.add(propertyPathElement);
						allProperties.add(propertyPathElement);
					}
				}
			}
		}
	}

	public static abstract class OntologicPropertyPatternRolePathElement<E extends Bindable,T extends OntologyProperty> extends OntologicObjectPatternRolePathElement<E,T>
	{
		public OntologicPropertyPatternRolePathElement(PropertyPatternRole aPatternRole, E container) 
		{
			super(aPatternRole,container);
		}
	}

	public static class OntologicDataPropertyPatternRolePathElement<E extends Bindable> extends OntologicPropertyPatternRolePathElement<E,OntologyDataProperty>
	{
		public OntologicDataPropertyPatternRolePathElement(DataPropertyPatternRole aPatternRole, E container) 
		{
			super(aPatternRole,container);
		}
	}

	public static class OntologicObjectPropertyPatternRolePathElement<E extends Bindable> extends OntologicPropertyPatternRolePathElement<E,OntologyObjectProperty>
	{
		public OntologicObjectPropertyPatternRolePathElement(ObjectPropertyPatternRole aPatternRole, E container) 
		{
			super(aPatternRole,container);
		}
	}

	public static class OntologicStatementPatternRolePathElement<E extends Bindable, T extends OntologyStatement> extends PatternRolePathElement<E,T>
	{
		private FinalBindingPathElementImpl displayableRepresentation;		
		private FinalBindingPathElementImpl subject;
		protected List<BindingPathElement> allProperties;

		public OntologicStatementPatternRolePathElement(OntologicObjectPatternRole aPatternRole, E container) 
		{
			super(aPatternRole,container);
			allProperties = new Vector<BindingPathElement>();
			displayableRepresentation = new FinalBindingPathElementImpl<OntologyStatement,String>("displayableRepresentation",OntologyStatement.class,String.class,false,"string_representation_of_ontologic_statement_(read_only)") {
				@Override
				public String getBindingValue(OntologyStatement target, BindingEvaluationContext context) 
				{
					return target.getDisplayableDescription();
				}
			    @Override
			    public void setBindingValue(String value, OntologyStatement target, BindingEvaluationContext context) 
			    {
			    	// not relevant because not settable
			    }
			};
			subject = new FinalBindingPathElementImpl<OntologyStatement,OntologyObject>("subject",OntologyStatement.class,OntologyObject.class,false,"subject_of_statement") {
				@Override
				public OntologyObject getBindingValue(OntologyStatement target, BindingEvaluationContext context) 
				{
					return target.getSubject();
				}
				@Override
				public void setBindingValue(OntologyObject value,
						OntologyStatement target,
						BindingEvaluationContext context) {
			    	// not relevant because not settable
				}
			};
			allProperties.add(displayableRepresentation);
			allProperties.add(subject);
		}
		
		@Override
		public List<BindingPathElement> getAllProperties() 
		{
			return allProperties;
		}
	}

	public static class IsAStatementPatternRolePathElement<E extends Bindable> extends OntologicStatementPatternRolePathElement<E,IsAStatement>
	{
		private FinalBindingPathElementImpl<IsAStatement,OntologyObject> object;

		public IsAStatementPatternRolePathElement(IsAStatementPatternRole aPatternRole, E container) 
		{
			super(aPatternRole,container);
			object = new FinalBindingPathElementImpl<IsAStatement,OntologyObject>("object",IsAStatement.class,OntologyObject.class,false,"object_of_statement") {
				@Override
				public OntologyObject getBindingValue(IsAStatement target, BindingEvaluationContext context) 
				{
					return null;
				}
				@Override
				public void setBindingValue(OntologyObject value,
						IsAStatement target, BindingEvaluationContext context) 
				{
			    	// not relevant because not settable
				}
			};
			allProperties.add(object);
		}
		
	}

	public static class ObjectPropertyStatementPatternRolePathElement<E extends Bindable> extends OntologicStatementPatternRolePathElement<E,ObjectPropertyStatement>
	{
		private FinalBindingPathElementImpl<ObjectPropertyStatement,OntologyObjectProperty> predicate;	
		private FinalBindingPathElementImpl<ObjectPropertyStatement,OntologyObject> object;

		public ObjectPropertyStatementPatternRolePathElement(ObjectPropertyStatementPatternRole aPatternRole, E container) 
		{
			super(aPatternRole,container);
			predicate = new FinalBindingPathElementImpl<ObjectPropertyStatement,OntologyObjectProperty>("predicate",ObjectPropertyStatement.class,OntologyObjectProperty.class,false,"predicate_of_statement") {
				@Override
				public OntologyObjectProperty getBindingValue(ObjectPropertyStatement target, BindingEvaluationContext context) 
				{
					return target.getPredicate();
				}
				@Override
				public void setBindingValue(OntologyObjectProperty value, ObjectPropertyStatement target, BindingEvaluationContext context) {
			    	// not relevant because not settable
				}
			};
			object = new FinalBindingPathElementImpl<ObjectPropertyStatement,OntologyObject>("object",ObjectPropertyStatement.class,OntologyObject.class,false,"object_of_statement") {
				@Override
				public OntologyObject getBindingValue(ObjectPropertyStatement target, BindingEvaluationContext context) 
				{
					return target.getStatementObject();
				}
				@Override
				public void setBindingValue(OntologyObject value, ObjectPropertyStatement target, BindingEvaluationContext context) {
			    	// not relevant because not settable
				}
			};
			allProperties.add(predicate);
			allProperties.add(object);
		}
		
	}

	public static class DataPropertyStatementPatternRolePathElement<E extends Bindable> extends OntologicStatementPatternRolePathElement<E,DataPropertyStatement>
	{
		private FinalBindingPathElementImpl<DataPropertyStatement,OntologyDataProperty> predicate;	
		private FinalBindingPathElementImpl<DataPropertyStatement,Object> value;

		public DataPropertyStatementPatternRolePathElement(DataPropertyStatementPatternRole aPatternRole, E container) 
		{
			super(aPatternRole,container);
			predicate = new FinalBindingPathElementImpl<DataPropertyStatement,OntologyDataProperty>("predicate",DataPropertyStatement.class,OntologyDataProperty.class,false,"predicate_of_statement") {
				@Override
				public OntologyDataProperty getBindingValue(DataPropertyStatement target, BindingEvaluationContext context) 
				{
					return target.getPredicate();
				}
				@Override
				public void setBindingValue(OntologyDataProperty value, DataPropertyStatement target, BindingEvaluationContext context) {
			    	// not relevant because not settable
				}
			};
			value = new FinalBindingPathElementImpl<DataPropertyStatement,Object>("object",DataPropertyStatement.class,aPatternRole.getDataProperty().getDataType().getAccessedType(),false,"object_of_statement") {
				@Override
				public Object getBindingValue(DataPropertyStatement target, BindingEvaluationContext context) 
				{
					return target.getValue();
				}
				@Override
				public void setBindingValue(Object value, DataPropertyStatement target, BindingEvaluationContext context) {
			    	// not relevant because not settable
				}
			};
			allProperties.add(predicate);
			allProperties.add(value);
		}
		
	}

	public static class RestrictionStatementPatternRolePathElement<E extends Bindable> extends OntologicStatementPatternRolePathElement<E,RestrictionStatement>
	{
		public RestrictionStatementPatternRolePathElement(RestrictionStatementPatternRole aPatternRole, E container) 
		{
			super(aPatternRole,container);
		}
		
	}


}