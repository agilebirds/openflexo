package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.ontology.EditionPatternInstance;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;

public class EditionPatternInstancePathElement<E extends Bindable> implements BindingVariable<EditionPatternInstance> {
	static final Logger logger = Logger.getLogger(EditionPatternInstancePathElement.class.getPackage().getName());

	private EditionPattern editionPattern;
	private int index;
	private Hashtable<PatternRole, BindingPathElement> elements;
	private Vector<BindingPathElement> allElements;
	private Class<E> declaringClass;

	public EditionPatternInstancePathElement(EditionPattern anEditionPattern, int index, Class<E> declaringClass) {
		this.editionPattern = anEditionPattern;
		this.declaringClass = declaringClass;
		this.index = index;
		elements = new Hashtable<PatternRole, BindingPathElement>();
		allElements = new Vector<BindingPathElement>();
		for (PatternRole pr : editionPattern.getPatternRoles()) {
			BindingPathElement<?> newPathElement = null;
			newPathElement = PatternRolePathElement.makePatternRolePathElement(pr, anEditionPattern);
			elements.put(pr, newPathElement);
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
		return declaringClass;
	}

	@Override
	public Type getType() {
		return EditionPatternInstance.class;
	}

	@Override
	public String getSerializationRepresentation() {
		return editionPattern.getViewPoint().getName() + "_" + editionPattern.getName() + "_" + index;
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
		// return patternRole.getEditionPattern();
		return null;
	}

	@Override
	public String getVariableName() {
		return getSerializationRepresentation();
	}

	@Override
	public EditionPatternInstance getBindingValue(Object target, BindingEvaluationContext context) {
		if (target != null) {
			logger.info("TODO: evaluateBinding EditionPatternPathElement with target=" + target + " context=" + context);
		}
		return null;
	}

	@Override
	public void setBindingValue(EditionPatternInstance value, Object target, BindingEvaluationContext context) {
		// Not settable
	}

	public EditionPattern getEditionPattern() {
		return editionPattern;
	}

	public int getIndex() {
		return index;
	}
}