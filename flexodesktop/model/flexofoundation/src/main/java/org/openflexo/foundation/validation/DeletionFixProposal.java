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
package org.openflexo.foundation.validation;

import java.util.logging.Logger;

import org.openflexo.foundation.DeletableObject;

/**
 * Automatic deletion fix proposal
 * 
 * @author sguerin
 * 
 */
public class DeletionFixProposal<R extends ValidationRule<R, V>, V extends Validable> extends FixProposal<R, V> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(DeletionFixProposal.class.getPackage().getName());

	public DeletionFixProposal(String aMessage) {
		super(aMessage);
	}

	@Override
	public void apply() {
		fixAction();
		getProblemIssue().revalidateAfterFixing(true);
	}

	@Override
	protected void fixAction() {
		if (getObject() instanceof DeletableObject) {
			((DeletableObject) getObject()).delete();
		}
	}

	@Override
	public boolean askConfirmation() {
		return true;
	}

}
