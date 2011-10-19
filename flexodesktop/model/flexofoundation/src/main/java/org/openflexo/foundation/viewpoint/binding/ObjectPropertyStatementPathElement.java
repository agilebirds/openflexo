package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.SimpleBindingPathElementImpl;
import org.openflexo.antar.binding.TypeUtils;
import org.openflexo.foundation.ontology.ObjectPropertyStatement;
import org.openflexo.foundation.ontology.OntologyIndividual;
import org.openflexo.foundation.ontology.OntologyObject;
import org.openflexo.foundation.ontology.OntologyObjectProperty;
import org.openflexo.foundation.ontology.PropertyStatement;
import org.openflexo.foundation.ontology.dm.URIChanged;
import org.openflexo.foundation.ontology.dm.URINameChanged;

public class ObjectPropertyStatementPathElement extends StatementPathElement<OntologyObject>
{
	private static final Logger logger = Logger.getLogger(ObjectPropertyStatementPathElement.class.getPackage().getName());

	private SimpleBindingPathElementImpl<String> uriNameProperty;
	private SimpleBindingPathElementImpl<String> uriProperty;
	
	private OntologyObjectProperty ontologyProperty;
	
	public ObjectPropertyStatementPathElement(BindingPathElement aParent, OntologyObjectProperty anOntologyProperty) 
	{
		super(aParent);
		ontologyProperty = anOntologyProperty;
		uriNameProperty = new SimpleBindingPathElementImpl<String>(URINameChanged.URI_NAME_KEY,TypeUtils.getBaseClass(getType()),String.class,true,"uri_name_as_supplied_in_ontology") {
			@Override
			public String getBindingValue(Object target, BindingEvaluationContext context) 
			{
				if (target instanceof OntologyObject) {
					return ((OntologyObject) target).getName();
				}
				else {
					logger.warning("Unexpected: "+target);
					return null;
				}
			}
			@Override
			public void setBindingValue(String value, Object target, BindingEvaluationContext context) {
				if (target instanceof OntologyObject) {
					try {
						logger.info("Rename URI of object "+target+" with "+value);
						((OntologyObject)target).setName(value);
					} catch (Exception e) {
						logger.warning("Unhandled exception: "+e);
						e.printStackTrace();
					}
				}
				else {
					logger.warning("Unexpected: "+target);
				}
			}
		};
		allProperties.add(uriNameProperty);
		uriProperty = new SimpleBindingPathElementImpl<String>(URIChanged.URI_KEY,TypeUtils.getBaseClass(getType()),String.class,false,"uri_as_supplied_in_ontology") {
			@Override
			public String getBindingValue(Object target, BindingEvaluationContext context) 
			{
				if (target instanceof OntologyObject) {
				return ((OntologyObject) target).getURI();
				}
				else {
					logger.warning("Unexpected: "+target);
					return null;
				}
			}
		    @Override
		    public void setBindingValue(String value, Object target, BindingEvaluationContext context) 
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
	public OntologyObject getBindingValue(Object target, BindingEvaluationContext context) 
	{
		if (target instanceof OntologyIndividual) {
			OntologyIndividual individual = (OntologyIndividual)target;
			PropertyStatement statement = individual.getPropertyStatement(ontologyProperty);
			if (statement == null) 
				return null;
			if (statement instanceof ObjectPropertyStatement) {
				return ((ObjectPropertyStatement) statement).getStatementObject();
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
	public void setBindingValue(OntologyObject value, Object target, BindingEvaluationContext context) 
	{
		logger.warning("Implement me");
	}

}