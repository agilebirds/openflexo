package org.openflexo.foundation.viewpoint.inspector;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.FinalBindingPathElementImpl;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.ontology.ObjectPropertyStatement;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.PropertyStatement;

public class ObjectPropertyStatementPathElement extends StatementPathElement
{
	private static final Logger logger = Logger.getLogger(ObjectPropertyStatementPathElement.class.getPackage().getName());

	private FinalBindingPathElementImpl uriNameProperty;
	private FinalBindingPathElementImpl uriProperty;
	
	private OntologyObjectProperty ontologyProperty;
	
	public ObjectPropertyStatementPathElement(BindingPathElement aParent, OntologyObjectProperty anOntologyProperty) 
	{
		super(aParent);
		ontologyProperty = anOntologyProperty;
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
	public Type getType() 
	{
		return OntologyIndividual.class;
	}

	@Override
	public String getLabel() 
	{
		return ontologyProperty.getName();
	}

	@Override
	public String getTooltipText(Type resultingType) 
	{
		return ontologyProperty.getDisplayableDescription();
	}

	@Override
	public boolean isSettable() 
	{
		return true;
	}

	@Override
	public OntologyIndividual evaluateBinding(Object target, BindingEvaluationContext context) 
	{
		if (target instanceof OntologyIndividual) {
			OntologyIndividual individual = (OntologyIndividual)target;
			PropertyStatement statement = individual.getPropertyStatement(ontologyProperty);
			if (statement == null) 
				return null;
			if (statement instanceof ObjectPropertyStatement) {
				OntologyObject returned = ((ObjectPropertyStatement) statement).getStatementObject();
				if (returned instanceof OntologyIndividual) 
					return (OntologyIndividual)returned;
				else if (returned == null) 
					return null;
				else {
					logger.warning("Unexpected value "+returned+" while evaluateBinding()");
					return null;
				}
			}
			else {
				logger.warning("Unexpected statement "+statement+" while evaluateBinding()");
				return null;
			}
		}
		else {
			logger.warning("Unexpected target "+target+" while evaluateBinding()");
			return null;
		}
	}
	

}