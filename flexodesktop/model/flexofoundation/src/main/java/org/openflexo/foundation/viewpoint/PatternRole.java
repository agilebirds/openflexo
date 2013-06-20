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

import java.lang.reflect.Type;
import java.util.Collection;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.toolbox.StringUtils;

/**
 * A PatternRole is a structural element of an EditionPattern, which plays a role in this edition pattern
 * 
 * @author sylvain
 * 
 */
public abstract class PatternRole<T> extends EditionPatternObject {

	// private static final Logger logger = Logger.getLogger(PatternRole.class.getPackage().getName());

	private EditionPattern _pattern;

	private ModelSlot<?> modelSlot;

	public PatternRole(VirtualModel.VirtualModelBuilder builder) {
		super(builder);
	}

	@Override
	public String getURI() {
		return getEditionPattern().getURI() + "." + getPatternRoleName();
	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return null;
	}

	public ModelSlot<?> getModelSlot() {
		return modelSlot;
	}

	public void setModelSlot(ModelSlot<?> modelSlot) {
		this.modelSlot = modelSlot;
		setChanged();
		notifyObservers(new DataModification("modelSlot", null, modelSlot));
	}

	public void setEditionPattern(EditionPattern pattern) {
		_pattern = pattern;
	}

	@Override
	public EditionPattern getEditionPattern() {
		return _pattern;
	}

	@Override
	public VirtualModel<?> getVirtualModel() {
		if (getEditionPattern() != null) {
			return getEditionPattern().getVirtualModel();
		}
		return null;
	}

	public String getPatternRoleName() {
		return getName();
	}

	public void setPatternRoleName(String patternRoleName) {
		setName(patternRoleName);
	}

	@Override
	public String toString() {
		return getClass().getSimpleName()
				+ ":"
				+ getPatternRoleName()
				+ "[container="
				+ (getEditionPattern() != null ? getEditionPattern().getName() + "/"
						+ (getEditionPattern().getVirtualModel() != null ? getEditionPattern().getVirtualModel().getName() : "null")
						: "null") + "][" + Integer.toHexString(hashCode()) + "]";
	}

	public abstract Type getType();

	public abstract String getPreciseType();

	public void finalizePatternRoleDeserialization() {
	}

	@Override
	public final BindingModel getBindingModel() {
		return getEditionPattern().getBindingModel();
	}

	public abstract boolean getIsPrimaryRole();

	public abstract void setIsPrimaryRole(boolean isPrimary);

	public abstract boolean defaultBehaviourIsToBeDeleted();

	public abstract ActorReference<T> makeActorReference(T object, EditionPatternInstance epi);

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
