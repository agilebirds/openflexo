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
package org.openflexo.foundation.rm.cg;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.xmlcode.StringRepresentable;

public enum GenerationStatus implements StringRepresentable {
	UpToDate,
	GenerationModified,
	GenerationAdded,
	GenerationRemoved,
	OverrideScheduled,
	DiskModified,
	DiskRemoved,
	ConflictingUnMerged,
	ConflictingMarkedAsMerged,
	CodeGenerationNotAvailable,
	CodeGenerationNotSynchronized,
	GenerationError,
	NotGenerated,
	Unknown;

	public boolean isGenerationAvailable() {
		return ((this == UpToDate) || (this == GenerationModified) || (this == GenerationAdded) || (this == GenerationRemoved)
				|| (this == OverrideScheduled) || (this == DiskModified) || (this == DiskRemoved) || (this == ConflictingUnMerged) || (this == ConflictingMarkedAsMerged));
	}

	public boolean isAbnormal() {
		return ((this == CodeGenerationNotAvailable) || (this == NotGenerated) || (this == Unknown) || (this == GenerationError));
	}

	public boolean isGenerationModified() {
		return ((this == GenerationModified) || (this == GenerationAdded) || (this == GenerationRemoved) || (this == OverrideScheduled));
	}

	public boolean isDiskModified() {
		return ((this == DiskModified) || (this == DiskRemoved));
	}

	public boolean isConflicting() {
		return ((this == ConflictingUnMerged) || (this == ConflictingMarkedAsMerged));
	}

	public String getLocalizedStringRepresentation() {
		return FlexoLocalization.localizedForKey(getLocalizationKey());
	}

	public String getLocalizationKey() {
		if (this == GenerationStatus.UpToDate) {
			return "up_to_date_file";
		} else if (this == GenerationStatus.GenerationModified) {
			return "file_needs_to_be_rewritten";
		} else if (this == GenerationStatus.GenerationAdded) {
			return "file_marked_for_addition";
		} else if (this == GenerationStatus.GenerationRemoved) {
			return "file_marked_for_removal";
		} else if (this == GenerationStatus.OverrideScheduled) {
			return "override_scheduled";
		} else if (this == GenerationStatus.DiskRemoved) {
			return "file_removed_on_disk";
		} else if (this == GenerationStatus.DiskModified) {
			return "file_modified_on_disk";
		} else if (this == GenerationStatus.ConflictingMarkedAsMerged) {
			return "conflicting_file_marked_as_merged";
		} else if (this == GenerationStatus.ConflictingUnMerged) {
			return "conflicting_file";
		} else if (this == GenerationStatus.CodeGenerationNotAvailable) {
			return "code_generation_not_available";
		} else if (this == GenerationStatus.CodeGenerationNotSynchronized) {
			return "code_generation_not_synchronized";
		} else if (this == GenerationStatus.GenerationError) {
			return "generation_error";
		} else if (this == GenerationStatus.NotGenerated) {
			return "file_not_generated_yet";
		}
		return "???";
	}

	/**
	 * Overrides toString
	 * 
	 * @see java.lang.Enum#toString()
	 */
	@Override
	public String toString() {
		return getLocalizationKey();
	}
}