package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;

public class EditionPatternPathElement<E extends Bindable> implements BindingVariable<EditionPatternInstance>,
		SimplePathElement<EditionPatternInstance> {
	static final Logger logger = Logger.getLogger(EditionPatternPathElement.class.getPackage().getName());

	private E container;
	private String name;
	private EditionPattern editionPattern;
	private Hashtable<PatternRole, BindingPathElement> elements;
	private Vector<BindingPathElement> allElements;

	public EditionPatternPathElement(EditionPattern anEditionPattern, E container) {
		this(null, anEditionPattern, container);
	}

	public EditionPatternPathElement(String name, EditionPattern anEditionPattern, E container) {
		this.name = name;
		this.editionPattern = anEditionPattern;
		this.container = container;
		elements = new Hashtable<PatternRole, BindingPathElement>();
		allElements = new Vector<BindingPathElement>();
		if (editionPattern != null) {
			for (PatternRole pr : editionPattern.getPatternRoles()) {
				BindingPathElement<?> newPathElement = null;
				newPathElement = PatternRolePathElement.makePatternRolePathElement(pr, anEditionPattern);
				if (newPathElement != null) {
					elements.put(pr, newPathElement);
					allElements.add(newPathElement);
				}
			}
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
		return editionPattern;
	}

	@Override
	public String getSerializationRepresentation() {
		if (name != null) {
			return name;
		}
		return editionPattern.getName();
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
		return editionPattern.getDescription();
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
	public EditionPatternInstance getBindingValue(Object target, BindingEvaluationContext context) {
		logger.warning("What to return as " + getVariableName() + " with a " + target + " ? "
				+ (target != null ? "(" + target.getClass().getSimpleName() + ")" : ""));
		return null;
	}

	@Override
	public void setBindingValue(EditionPatternInstance value, Object target, BindingEvaluationContext context) {
		// Not settable
	}

	public EditionPattern getEditionPattern() {
		return editionPattern;
	}

}