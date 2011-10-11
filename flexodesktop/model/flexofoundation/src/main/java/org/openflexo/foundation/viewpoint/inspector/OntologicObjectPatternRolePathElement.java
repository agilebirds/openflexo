package org.openflexo.foundation.viewpoint.inspector;

import java.util.List;
import java.util.Vector;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.FinalBindingPathElementImpl;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyProperty;
import org.openflexo.foundation.ontology.OntologyStatement;
import org.openflexo.foundation.viewpoint.OntologicObjectPatternRole;

public abstract class OntologicObjectPatternRolePathElement extends PatternRolePathElement
{
	public static class OntologicClassPatternRolePathElement extends OntologicObjectPatternRolePathElement
	{
		public OntologicClassPatternRolePathElement(OntologicObjectPatternRole aPatternRole) 
		{
			super(aPatternRole);
		}
	}

	public static class OntologicIndividualPatternRolePathElement extends OntologicObjectPatternRolePathElement
	{
		public OntologicIndividualPatternRolePathElement(OntologicObjectPatternRole aPatternRole) 
		{
			super(aPatternRole);
		}
	}

	public static abstract class OntologicPropertyPatternRolePathElement extends OntologicObjectPatternRolePathElement
	{
		public OntologicPropertyPatternRolePathElement(OntologicObjectPatternRole aPatternRole) 
		{
			super(aPatternRole);
		}
	}

	public static class OntologicDataPropertyPatternRolePathElement extends OntologicPropertyPatternRolePathElement
	{
		public OntologicDataPropertyPatternRolePathElement(OntologicObjectPatternRole aPatternRole) 
		{
			super(aPatternRole);
		}
	}

	public static class OntologicObjectPropertyPatternRolePathElement extends OntologicPropertyPatternRolePathElement
	{
		public OntologicObjectPropertyPatternRolePathElement(OntologicObjectPatternRole aPatternRole) 
		{
			super(aPatternRole);
		}
	}

	private FinalBindingPathElementImpl uriNameProperty;
	private FinalBindingPathElementImpl uriProperty;
	private List<BindingPathElement> allProperties;
	
	public OntologicObjectPatternRolePathElement(OntologicObjectPatternRole aPatternRole) 
	{
		super(aPatternRole);
		allProperties = new Vector<BindingPathElement>();
		uriNameProperty = new FinalBindingPathElementImpl("uriName",OntologyObject.class,String.class,true,"uri_name_as_supplied_in_ontology") {
			@Override
			public Object evaluateBinding(Object target, BindingEvaluationContext context) 
			{
				return ((OntologyObject)target).getName();
			}
		};
		allProperties.add(uriNameProperty);
		uriProperty = new FinalBindingPathElementImpl("uri",OntologyObject.class,String.class,false,"uri_as_supplied_in_ontology") {
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
	
	public static class OntologicStatementPatternRolePathElement extends PatternRolePathElement
	{
		private FinalBindingPathElementImpl displayableRepresentation;		
		private FinalBindingPathElementImpl subject;
		private FinalBindingPathElementImpl predicate;	
		private FinalBindingPathElementImpl object;
		private List<BindingPathElement> allProperties;

		public OntologicStatementPatternRolePathElement(OntologicObjectPatternRole aPatternRole) 
		{
			super(aPatternRole);
			allProperties = new Vector<BindingPathElement>();
			displayableRepresentation = new FinalBindingPathElementImpl("displayableRepresentation",OntologyStatement.class,String.class,false,"string_representation_of_ontologic_statement_(read_only)") {
				@Override
				public Object evaluateBinding(Object target, BindingEvaluationContext context) 
				{
					return ((OntologyStatement)target).getName();
				}
			};
			subject = new FinalBindingPathElementImpl("subject",OntologyStatement.class,OntologyObject.class,false,"subject_of_statement") {
				@Override
				public Object evaluateBinding(Object target, BindingEvaluationContext context) 
				{
					return ((OntologyStatement)target).getSubject();
				}
			};
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
			allProperties.add(displayableRepresentation);
			allProperties.add(subject);
			allProperties.add(predicate);
			allProperties.add(object);
		}
		
		@Override
		public List<BindingPathElement> getAllProperties() 
		{
			return allProperties;
		}
	}


}