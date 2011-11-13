package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.Hashtable;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.AbstractBinding.BindingEvaluationContext;
import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.PatternRole;

public class EditionPatternPathElement<E extends Bindable> implements BindingVariable<EditionPattern> {
	static final Logger logger = Logger.getLogger(EditionPatternPathElement.class.getPackage().getName());

	private E container;
	private String name;
	private EditionPattern editionPattern;
	private Hashtable<PatternRole, PatternRolePathElement> patternRoleElements;
	private Vector<PatternRolePathElement> allPatternRoleElements;

	public EditionPatternPathElement(EditionPattern anEditionPattern, E container) {
		this(null, anEditionPattern, container);
	}

	public EditionPatternPathElement(String name, EditionPattern anEditionPattern, E container) {
		this.name = name;
		this.editionPattern = anEditionPattern;
		this.container = container;
		patternRoleElements = new Hashtable<PatternRole, PatternRolePathElement>();
		allPatternRoleElements = new Vector<PatternRolePathElement>();
		if (editionPattern != null) {
			for (PatternRole pr : editionPattern.getPatternRoles()) {
				PatternRolePathElement newPathElement = null;
				newPathElement = PatternRolePathElement.makePatternRolePathElement(pr, (EditionPattern) null);
				patternRoleElements.put(pr, newPathElement);
				allPatternRoleElements.add(newPathElement);
			}
		}
	}

	public Vector<PatternRolePathElement> getAllPatternRoleElements() {
		return allPatternRoleElements;
	}

	public PatternRolePathElement getPatternRolePathElement(PatternRole pr) {
		return patternRoleElements.get(pr);
	}

	@Override
	public Class<E> getDeclaringClass() {
		if (container != null)
			return (Class<E>) container.getClass();
		return null;
	}

	@Override
	public Type getType() {
		return EditionPattern.class;
	}

	@Override
	public String getSerializationRepresentation() {
		if (name != null)
			return name;
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
	public EditionPattern getBindingValue(Object target, BindingEvaluationContext context) {
		return editionPattern;
	}

	@Override
	public void setBindingValue(EditionPattern value, Object target, BindingEvaluationContext context) {
		// Not settable
	}

	public EditionPattern getEditionPattern() {
		return editionPattern;
	}

}