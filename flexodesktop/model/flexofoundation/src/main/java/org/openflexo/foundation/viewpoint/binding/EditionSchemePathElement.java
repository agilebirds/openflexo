package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.viewpoint.EditionScheme;
import org.openflexo.foundation.viewpoint.PatternRole;

public class EditionSchemePathElement<E extends Bindable> implements BindingVariable<EditionScheme> {
	static final Logger logger = Logger.getLogger(EditionSchemePathElement.class.getPackage().getName());

	private E container;
	private EditionScheme editionScheme;

	private EditionSchemeParameterListPathElement parametersElement;
	private Hashtable<PatternRole, BindingPathElement> elements;
	private Vector<BindingPathElement> allElements;

	public EditionSchemePathElement(EditionScheme anEditionScheme, E container) {
		this.editionScheme = anEditionScheme;
		this.container = container;
		elements = new Hashtable<PatternRole, BindingPathElement>();
		allElements = new Vector<BindingPathElement>();
		parametersElement = new EditionSchemeParameterListPathElement(editionScheme, this);
		for (PatternRole pr : anEditionScheme.getEditionPattern().getPatternRoles()) {
			BindingPathElement<?> newPathElement = PatternRolePathElement.makePatternRolePathElement(pr, editionScheme);
			elements.put(pr, newPathElement);
			allElements.add(newPathElement);
		}
	}

	public Vector<BindingPathElement> getAllElements() {
		return allElements;
	}

	public BindingPathElement getPathElement(PatternRole pr) {
		return elements.get(pr);
	}

	@Override
	public Class<E> getDeclaringClass() {
		if (container != null) {
			return (Class<E>) container.getClass();
		}
		return null;
	}

	@Override
	public Type getType() {
		return EditionScheme.class;
	}

	@Override
	public String getSerializationRepresentation() {
		return editionScheme.getName();
	}

	@Override
	public boolean isBindingValid() {
		return true;
	}

	@Override
	public String getLabel() {
		return getSerializationRepresentation();
	}

	@Override
	public String getTooltipText(Type resultingType) {
		return editionScheme.getDescription();
	}

	@Override
	public boolean isSettable() {
		return false;
	}

	@Override
	public E getContainer() {
		return container;
	}

	@Override
	public String getVariableName() {
		return getSerializationRepresentation();
	}

	@Override
	public EditionScheme getBindingValue(Object target, BindingEvaluationContext context) {
		return editionScheme;
	}

	@Override
	public void setBindingValue(EditionScheme value, Object target, BindingEvaluationContext context) {
		// Not settable
	}

	public EditionScheme getEditionScheme() {
		return editionScheme;
	}

}