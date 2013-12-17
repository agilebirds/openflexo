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
package org.openflexo.foundation.dkv;

import java.util.Collection;
import java.util.logging.Logger;

import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.dkv.dm.DKVDataModification;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationError;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.xml.FlexoDKVModelBuilder;
import org.openflexo.inspector.InspectableObject;
import org.openflexo.logging.FlexoLogger;

/**
 * @author gpolet
 * 
 */
public class Language extends DKVObject implements InspectableObject {

	@SuppressWarnings("unused")
	private static final Logger logger = FlexoLogger.getLogger(Language.class.getPackage().toString());

	private boolean isMain = false;

	private String isoCode;

	public Language(FlexoDKVModelBuilder builder) {
		this(builder.dkvModel);
		initializeDeserialization(builder);
	}

	public Language(DKVModel dkvModel) {
		super(dkvModel);
	}

	/**
     * 
     */
	public Language(DKVModel dl, String name) {
		this(dl);
		this.name = name;
	}

	/**
     * 
     */
	public Language(DKVModel dl, String name, boolean isMain) {
		this(dl);
		this.name = name;
		this.isMain = isMain;
	}

	/**
	 * Overrides getFullyQualifiedName
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getFullyQualifiedName()
	 */
	@Override
	public String getFullyQualifiedName() {
		return "LANGUAGE." + name;
	}

	/**
	 * Overrides getInspectorName
	 * 
	 * @see org.openflexo.inspector.InspectableObject#getInspectorName()
	 */
	@Override
	public String getInspectorName() {
		return Inspectors.IE.LANGUAGE_INSPECTOR;
	}

	public boolean getIsMain() {
		return isMain;
	}

	public void setIsMain(boolean b) {
		Boolean old = new Boolean(isMain);
		isMain = b;
		if (b) {
			for (Language l : getDkvModel().getLanguages()) {
				if (l != this) {
					l.setIsMain(false);
				}
			}
		}
		setChanged();
		notifyObservers(new DKVDataModification("isMain", old, new Boolean(b)));
	}

	public String getIsoCode() {
		return isoCode;
	}

	public void setIsoCode(String isoCode) {
		String old = this.isoCode;
		this.isoCode = isoCode;
		setChanged();
		notifyObservers(new DKVDataModification("isoCode", old, isoCode));
	}

	/**
	 * @throws UnauthorizedActionException
	 * 
	 */
	@Override
	public boolean delete() throws UnauthorizedActionException {
		if (getDkvModel().getLanguages().size() > 1) {
			getDkvModel().removeFromLanguage(this);
			super.delete();
			this.deleteObservers();
			return true;
		} else {
			throw new UnauthorizedActionException("There must always be at least one language in any application");
		}

	}

	@Override
	public void undelete() {
		super.undelete();
		getDkvModel().addToLanguages(this);
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.AgileBirdsObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return "dkv_language";
	}

	/**
	 * Overrides isDeleteAble
	 * 
	 * @see org.openflexo.foundation.dkv.DKVObject#isDeleteAble()
	 */
	@Override
	public boolean isDeleteAble() {
		return true;
	}

	public static class IsoCodeMustBeDefined extends ValidationRule {
		public IsoCodeMustBeDefined() {
			super(Language.class, "isocode_must_be_defined");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final Language language = (Language) object;
			if (language.getIsoCode() == null) {
				ValidationError error = new ValidationError(this, object, "isocode_must_be_defined");
				return error;
			}
			return null;
		}

	}

	public static class IsoCodeMustBe2CharLength extends ValidationRule {
		public IsoCodeMustBe2CharLength() {
			super(Language.class, "isocode_must_be_2_char_length");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final Language language = (Language) object;
			if (language.getIsoCode() != null && language.getIsoCode().length() != 2) {
				ValidationError error = new ValidationError(this, object, "isocode_must_be_2_char_length");
				return error;
			}
			return null;
		}

	}

	public static class IsoCodeMustBeUnique extends ValidationRule {
		public IsoCodeMustBeUnique() {
			super(Language.class, "isocode_must_unique");
		}

		@Override
		public ValidationIssue applyValidation(final Validable object) {
			final Language language = (Language) object;
			if (language.getIsoCode() != null && !language.isoCodeIsUnique()) {
				ValidationError error = new ValidationError(this, object, "isocode_must_unique");
				return error;
			}
			return null;
		}

	}

	@Override
	public Collection<? extends Validable> getEmbeddedValidableObjects() {
		return null;
	}

	/**
	 * @return wheter the isoCode of this Language is unique or not (case insensitive)
	 */
	public boolean isoCodeIsUnique() {
		if (getIsoCode() == null || getIsoCode().trim().length() == 0) {
			return false;
		}
		for (Language lg : getDkvModel().getLanguages()) {
			if (getIsoCode().equalsIgnoreCase(lg.getIsoCode()) && lg != this) {
				return false;
			}
		}
		return true;
	}
}
