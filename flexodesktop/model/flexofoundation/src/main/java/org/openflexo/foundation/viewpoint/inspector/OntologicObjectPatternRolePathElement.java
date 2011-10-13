package org.openflexo.foundation.viewpoint.inspector;

import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.FinalBindingPathElementImpl;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.OntologyStatement;
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

public abstract class OntologicObjectPatternRolePathElement extends PatternRolePathElement
{
	private static final Logger logger = Logger.getLogger(OntologicObjectPatternRolePathElement.class.getPackage().getName());

	private FinalBindingPathElementImpl uriNameProperty;
	private FinalBindingPathElementImpl uriProperty;
	protected List<BindingPathElement> allProperties;
	
	public OntologicObjectPatternRolePathElement(OntologicObjectPatternRole aPatternRole) 
	{
		super(aPatternRole);
		allProperties = new Vector<BindingPathElement>();
		uriNameProperty = new FinalBindingPathElementImpl("uriName",TypeUtils.getBaseClass(getType()),String.class,true,"uri_name_as_supplied_in_ontology") {
			@Override
			public Object evaluateBinding(Object target, BindingEvaluationContext context) 
			{
				return ((OntologyObject)target).getName();
			}
		};
		allProperties.add(uriNameProperty);
		uriProperty = new FinalBindingPathElementImpl("uri",TypeUtils.getBaseClass(getType()),String.class,false,"uri_as_supplied_in_ontology") {
			@Override
			public Object evaluateBinding(Object target, BindingEvaluationContext context) 
			{
				return ((OntologyObject)target).getURI();
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
	
	public static class OntologicClassPatternRolePathElement extends OntologicObjectPatternRolePathElement
	{
		public OntologicClassPatternRolePathElement(ClassPatternRole aPatternRole) 
		{
			super(aPatternRole);
		}
	}

	public static class OntologicIndividualPatternRolePathElement extends OntologicObjectPatternRolePathElement
	{
		Vector<StatementPathElement> accessibleStatements;
		
		public OntologicIndividualPatternRolePathElement(IndividualPatternRole aPatternRole) 
		{
			super(aPatternRole);
			accessibleStatements = new Vector<StatementPathElement>();
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

	public static abstract class OntologicPropertyPatternRolePathElement extends OntologicObjectPatternRolePathElement
	{
		public OntologicPropertyPatternRolePathElement(PropertyPatternRole aPatternRole) 
		{
			super(aPatternRole);
		}
	}

	public static class OntologicDataPropertyPatternRolePathElement extends OntologicPropertyPatternRolePathElement
	{
		public OntologicDataPropertyPatternRolePathElement(DataPropertyPatternRole aPatternRole) 
		{
			super(aPatternRole);
		}
	}

	public static class OntologicObjectPropertyPatternRolePathElement extends OntologicPropertyPatternRolePathElement
	{
		public OntologicObjectPropertyPatternRolePathElement(ObjectPropertyPatternRole aPatternRole) 
		{
			super(aPatternRole);
		}
	}

	public static class OntologicStatementPatternRolePathElement extends PatternRolePathElement
	{
		private FinalBindingPathElementImpl displayableRepresentation;		
		private FinalBindingPathElementImpl subject;
		protected List<BindingPathElement> allProperties;

		public OntologicStatementPatternRolePathElement(OntologicObjectPatternRole aPatternRole) 
		{
			super(aPatternRole);
			allProperties = new Vector<BindingPathElement>();
			displayableRepresentation = new FinalBindingPathElementImpl("displayableRepresentation",OntologyStatement.class,String.class,false,"string_representation_of_ontologic_statement_(read_only)") {
				@Override
				public Object evaluateBinding(Object target, BindingEvaluationContext context) 
				{
					return ((OntologyStatement)target).getDisplayableDescription();
				}
			};
			subject = new FinalBindingPathElementImpl("subject",OntologyStatement.class,OntologyObject.class,false,"subject_of_statement") {
				@Override
				public Object evaluateBinding(Object target, BindingEvaluationContext context) 
				{
					return ((OntologyStatement)target).getSubject();
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

	public static class IsAStatementPatternRolePathElement extends OntologicStatementPatternRolePathElement
	{
		private FinalBindingPathElementImpl predicate;	
		private FinalBindingPathElementImpl object;

		public IsAStatementPatternRolePathElement(IsAStatementPatternRole aPatternRole) 
		{
			super(aPatternRole);
			predicate = new FinalBindingPathElementImpl("predicate",OntologyStatement.class,OntologyProperty.class,false,"predicate_of_statement") {
				@Override
				public Object evaluateBinding(Object target, BindingEvaluationContext context) 
				{
					return ((OntologyStatement)target).getPredicate();
				}
			};
			object = new FinalBindingPathElementImpl("object",OntologyStatement.class,Object.class,false,"object_of_statement") {
				@Override
				public Object evaluateBinding(Object target, BindingEvaluationContext context) 
				{
					// TODO
					return null;
				}
			};
			allProperties.add(predicate);
			allProperties.add(object);
		}
		
	}

	public static class ObjectPropertyStatementPatternRolePathElement extends OntologicStatementPatternRolePathElement
	{
		private FinalBindingPathElementImpl predicate;	
		private FinalBindingPathElementImpl object;

		public ObjectPropertyStatementPatternRolePathElement(ObjectPropertyStatementPatternRole aPatternRole) 
		{
			super(aPatternRole);
			predicate = new FinalBindingPathElementImpl("predicate",OntologyStatement.class,OntologyProperty.class,false,"predicate_of_statement") {
				@Override
				public Object evaluateBinding(Object target, BindingEvaluationContext context) 
				{
					return ((OntologyStatement)target).getPredicate();
				}
			};
			object = new FinalBindingPathElementImpl("object",OntologyStatement.class,Object.class,false,"object_of_statement") {
				@Override
				public Object evaluateBinding(Object target, BindingEvaluationContext context) 
				{
					// TODO
					return null;
				}
			};
			allProperties.add(predicate);
			allProperties.add(object);
		}
		
	}

	public static class DataPropertyStatementPatternRolePathElement extends OntologicStatementPatternRolePathElement
	{
		private FinalBindingPathElementImpl predicate;	
		private FinalBindingPathElementImpl object;

		public DataPropertyStatementPatternRolePathElement(DataPropertyStatementPatternRole aPatternRole) 
		{
			super(aPatternRole);
			predicate = new FinalBindingPathElementImpl("predicate",OntologyStatement.class,OntologyProperty.class,false,"predicate_of_statement") {
				@Override
				public Object evaluateBinding(Object target, BindingEvaluationContext context) 
				{
					return ((OntologyStatement)target).getPredicate();
				}
			};
			object = new FinalBindingPathElementImpl("object",OntologyStatement.class,Object.class,false,"object_of_statement") {
				@Override
				public Object evaluateBinding(Object target, BindingEvaluationContext context) 
				{
					// TODO
					return null;
				}
			};
			allProperties.add(predicate);
			allProperties.add(object);
		}
		
	}

	public static class RestrictionStatementPatternRolePathElement extends OntologicStatementPatternRolePathElement
	{
		private FinalBindingPathElementImpl predicate;	
		private FinalBindingPathElementImpl object;

		public RestrictionStatementPatternRolePathElement(RestrictionStatementPatternRole aPatternRole) 
		{
			super(aPatternRole);
			predicate = new FinalBindingPathElementImpl("predicate",OntologyStatement.class,OntologyProperty.class,false,"predicate_of_statement") {
				@Override
				public Object evaluateBinding(Object target, BindingEvaluationContext context) 
				{
					return ((OntologyStatement)target).getPredicate();
				}
			};
			object = new FinalBindingPathElementImpl("object",OntologyStatement.class,Object.class,false,"object_of_statement") {
				@Override
				public Object evaluateBinding(Object target, BindingEvaluationContext context) 
				{
					// TODO
					return null;
				}
			};
			allProperties.add(predicate);
			allProperties.add(object);
		}
		
	}


}