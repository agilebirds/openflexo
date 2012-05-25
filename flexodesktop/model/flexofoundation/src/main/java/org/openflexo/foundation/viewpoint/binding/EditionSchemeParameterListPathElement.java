package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.EditionPatternParameter;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.EditionSchemeParameter;
import org.openflexo.foundation.viewpoint.ListParameter;
import org.openflexo.localization.FlexoLocalization;

public class EditionSchemeParameterListPathElement implements SimplePathElement<EditionSchemeAction<?>>,
		BindingVariable<EditionSchemeAction<?>> {
	private static final Logger logger = Logger.getLogger(EditionSchemeParameterListPathElement.class.getPackage().getName());

	private EditionScheme editionScheme;
	private EditionSchemePathElement parent;
	private Vector<BindingPathElement> allProperties;

	public EditionSchemeParameterListPathElement(EditionScheme editionScheme, EditionSchemePathElement aParent) {
		super();
		parent = aParent;
		this.editionScheme = editionScheme;
		allProperties = new Vector<BindingPathElement>();
		for (EditionSchemeParameter p : editionScheme.getParameters()) {
			if (p instanceof EditionPatternParameter) {
				allProperties.add(new EditionPatternParameterPathElement(p.getName(), ((EditionPatternParameter) p), p));
			} else {
				allProperties.add(new EditionSchemeParameterPathElement(this, p));
				if (p instanceof ListParameter) {
					allProperties.add(new ListValueForListParameterPathElement(this, (ListParameter) p));
				}
			}
		}
	}

	public Vector<BindingPathElement> getAllProperties() {
		return allProperties;
	}

	@Override
	public Class<EditionScheme> getDeclaringClass() {
		return EditionScheme.class;
	}

	@Override
	public String getSerializationRepresentation() {
		return getLabel();
	}

	@Override
	public boolean isBindingValid() {
		return true;
	}

	@Override
	public Type getType() {
		return EditionSchemeAction.class;
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
	public EditionSchemeAction<?> getBindingValue(Object target, BindingEvaluationContext context) {
		if (target instanceof EditionSchemeAction) {
			return (EditionSchemeAction<?>) target;
		}
		logger.warning("Unexpected " + target);
		return null;
	}

	@Override
	public void setBindingValue(EditionSchemeAction<?> value, Object target, BindingEvaluationContext context) {
		// Not relevant because not settable
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