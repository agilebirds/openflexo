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
package org.openflexo.foundation.wkf;

import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.validation.ValidationReport;

/**
 * Thown after trying to move a process. Contains a validation report where all inconsistency are pointed
 * 
 * @author sguerin
 * 
 */
public class InvalidProcessReferencesException extends FlexoException {
	public FlexoProcess movedProcess;
	public ValidationReport report;

	public InvalidProcessReferencesException(FlexoProcess movedProcess, ValidationReport report) {
		this.movedProcess = movedProcess;
		this.report = report;
	}

}
