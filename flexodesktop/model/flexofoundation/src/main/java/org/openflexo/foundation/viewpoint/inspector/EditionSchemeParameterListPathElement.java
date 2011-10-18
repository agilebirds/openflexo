package org.openflexo.foundation.viewpoint.inspector;

import java.lang.reflect.Type;
import java.util.List;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.localization.FlexoLocalization;

public class EditionSchemeParameterListPathElement implements SimplePathElement<EditionScheme,List<?>>,BindingVariable<EditionScheme,List<?>>
{
	private static final Logger logger = Logger.getLogger(EditionSchemeParameterListPathElement.class.getPackage().getName());

	private EditionScheme editionScheme;
	private EditionSchemePathElement parent;
	private Vector<EditionSchemeParameterPathElement> allProperties;
	
	public EditionSchemeParameterListPathElement(EditionScheme editionScheme, EditionSchemePathElement aParent) 
	{
		super();
		parent = aParent;
		this.editionScheme = editionScheme;
		allProperties = new Vector<EditionSchemeParameterPathElement>();
		for (EditionSchemeParameter p : editionScheme.getParameters()) {
			allProperties.add(new EditionSchemeParameterPathElement(this, p));
		}
	}

	public Vector<EditionSchemeParameterPathElement> getAllProperties() {
		return allProperties;
	}
	
	@Override
	public Class<EditionScheme> getDeclaringClass() 
	{
		return EditionScheme.class;
	}

	@Override
	public String getSerializationRepresentation() 
	{
		return getLabel();
	}

	@Override
	public boolean isBindingValid() 
	{
		return true;
	}

	@Override
	public Type getType() 
	{
		return List.class;
	}

	@Override
	public String getLabel() {
		return "parameters";
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return FlexoLocalization.localizedForKey("instanciation_parameters_of_edition_scheme");
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	@Override
	public List<?> getBindingValue(EditionScheme target,
			BindingEvaluationContext context) {
		logger.warning("Que dois-je renvoyer pour "+target);
		return null;
	}

	@Override
	public void setBindingValue(List<?> value, EditionScheme target,
			BindingEvaluationContext context) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public EditionScheme getContainer() {
		return editionScheme;
	}

	@Override
	public String getVariableName() {
		return getLabel();
	}

	
}