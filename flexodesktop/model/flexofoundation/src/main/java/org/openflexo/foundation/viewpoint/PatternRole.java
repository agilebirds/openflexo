/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.viewpoint;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.viewpoint.ViewPoint.ViewPointBuilder;
import org.openflexo.toolbox.StringUtils;

/**
 * A PatternRole is an element of an EditionPattern, which play a role in this edition pattern
 * 
 * @author sylvain
 * 
 */
public abstract class PatternRole extends EditionPatternObject {

	public static enum PatternRoleType {
		Shape,
		Connector,
		Individual,
		Class,
		Property,
		ObjectProperty,
		DataProperty,
		IsAStatement,
		ObjectPropertyStatement,
		DataPropertyStatement,
		RestrictionStatement,
		FlexoModelObject,
		Diagram,
		EditionPattern,
		Primitive
	}

	private EditionPattern _pattern;
	private String patternRoleName;
	private String description;

	public PatternRole(ViewPointBuilder builder) {
		super(builder);
	}

	public void setEditionPattern(EditionPattern pattern) {
		_pattern = pattern;
	}

	@Override
	public EditionPattern getEditionPattern() {
		return _pattern;
	}

	@Override
	public String getDescription() {
		return description;
	}

	@Override
	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public ViewPoint getViewPoint() {
		if (getEditionPattern() != null) {
			return getEditionPattern().getViewPoint();
		}
		return null;
	}

	@Override
	public String getName() {
		return getPatternRoleName();
	}

	public String getPatternRoleName() {
		return patternRoleName;
	}

	public void setPatternRoleName(String patternRoleName) {
		this.patternRoleName = patternRoleName;
		/*String oldValue = patternRoleName;
		if (patternRoleName != null && !patternRoleName.equals(oldValue)) {
			this.patternRoleName = patternRoleName;
			setChanged();
			notifyObservers(new NameChanged(oldValue, patternRoleName));
		}*/
	}

	@Override
	public String getInspectorName() {
		if (getType() == PatternRoleType.Shape) {
			return Inspectors.VPM.SHAPE_PATTERN_ROLE_INSPECTOR;
		} else if (getType() == PatternRoleType.Connector) {
			return Inspectors.VPM.CONNECTOR_PATTERN_ROLE_INSPECTOR;
		} else {
			return Inspectors.VPM.PATTERN_ROLE_INSPECTOR;
		}
	}

	@Override
	public String toString() {
		return getPatternRoleName();
	}

	public abstract PatternRoleType getType();

	public abstract String getPreciseType();

	public void finalizePatternRoleDeserialization() {
	}

	public abstract Class<?> getAccessedClass();

	@Override
	public final BindingModel getBindingModel() {
		return getEditionPattern().getBindingModel();
	}

	public abstract boolean getIsPrimaryRole();

	public abstract void setIsPrimaryRole(boolean isPrimary);

	// @Override
	// public abstract String getLanguageRepresentation();

	public static class PatternRoleMustHaveAName extends ValidationRule<PatternRoleMustHaveAName, PatternRole> {
		public PatternRoleMustHaveAName() {
			super(PatternRole.class, "pattern_role_must_have_a_name");
		}

		@Override
		public ValidationIssue<PatternRoleMustHaveAName, PatternRole> applyValidation(PatternRole patternRole) {
			if (StringUtils.isEmpty(patternRole.getPatternRoleName())) {
				return new ValidationError<PatternRoleMustHaveAName, PatternRole>(this, patternRole, "pattern_role_has_no_name");
			}
			return null;
		}
	}
}
