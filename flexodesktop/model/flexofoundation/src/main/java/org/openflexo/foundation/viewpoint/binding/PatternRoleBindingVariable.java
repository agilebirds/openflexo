package org.openflexo.foundation.viewpoint.binding;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.foundation.viewpoint.PatternRole;

public class PatternRoleBindingVariable extends BindingVariable {
	static final Logger logger = Logger.getLogger(PatternRoleBindingVariable.class.getPackage().getName());

	private PatternRole<?> patternRole;

	public PatternRoleBindingVariable(PatternRole<?> patternRole) {
		super(patternRole.getName(), patternRole.getType(), true);
		this.patternRole = patternRole;
	}

	@Override
	public Type getType() {
		return getPatternRole().getType();
	}

	public PatternRole getPatternRole() {
		return patternRole;
	}
}