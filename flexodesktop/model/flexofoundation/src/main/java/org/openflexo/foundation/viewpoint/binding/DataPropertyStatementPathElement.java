package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.foundation.ontology.DataPropertyStatement;
import org.openflexo.foundation.ontology.OntologyDataProperty;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.PropertyStatement;

public class DataPropertyStatementPathElement extends StatementPathElement<Object>
{
	private static final Logger logger = Logger.getLogger(DataPropertyStatementPathElement.class.getPackage().getName());

	private OntologyDataProperty ontologyProperty;
	
	public DataPropertyStatementPathElement(BindingPathElement aParent, OntologyDataProperty anOntologyProperty) 
	{
		super(aParent);
		ontologyProperty = anOntologyProperty;
	}

	@Override
	public List<BindingPathElement> getAllProperties() 
	{
		return allProperties;
	}

	@Override
	public Type getType() 
	{
		if (ontologyProperty != null && ontologyProperty.getDataType() != null)
			return ontologyProperty.getDataType().getAccessedType();
		return Object.class;
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
	public Object getBindingValue(OntologyObject target, BindingEvaluationContext context) 
	{
		if (target instanceof OntologyIndividual) {
			OntologyIndividual individual = (OntologyIndividual)target;
			PropertyStatement statement = individual.getPropertyStatement(ontologyProperty);
			if (statement == null) 
				return null;
			if (statement instanceof DataPropertyStatement) {
				return ((DataPropertyStatement) statement).getValue();
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
	
	@Override
	public void setBindingValue(Object value, OntologyObject target, BindingEvaluationContext context) 
	{
		logger.warning("Attempt to process setBindingValue with "+value);
		if (target instanceof OntologyIndividual) {
			OntologyIndividual individual = (OntologyIndividual)target;
			PropertyStatement statement = individual.getPropertyStatement(ontologyProperty);
			if (statement == null) {
				individual.addDataPropertyStatement(ontologyProperty, value);
			}
			if (statement instanceof DataPropertyStatement) {
				((DataPropertyStatement) statement).setValue(value);
			}
			else {
				logger.warning("Unexpected statement "+statement+" while evaluateBinding()");
			}
		}
		else {
			logger.warning("Unexpected target "+target+" while evaluateBinding()");
		}
	}

}