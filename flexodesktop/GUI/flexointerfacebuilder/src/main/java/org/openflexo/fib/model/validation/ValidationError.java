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
package org.openflexo.fib.model.validation;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBModelObject;

/**
 * Represents a validation issue requiring attention embedded in a validation report. An error signify that the project launching may lead
 * to abnormal behaviour.
 * 
 * @author sguerin
 * 
 */
public class ValidationError<R extends ValidationRule<R, C>, C extends FIBModelObject> extends ProblemIssue<R, C> {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(ValidationError.class.getPackage().getName());

	public ValidationError(R rule, C anObject, String aMessage) {
		super(rule, anObject, aMessage);
	}

	public ValidationError(R rule, C anObject, String aMessage, FixProposal<R, C> proposal) {
		super(rule, anObject, aMessage, proposal);
	}

	public ValidationError(R rule, C anObject, String aMessage, Vector<FixProposal<R, C>> fixProposals) {
		super(rule, anObject, aMessage, fixProposals);
	}

	@Override
	public String toString() {
		return "VALIDATION / ERROR:   " + getLocalizedMessage();
	}

}
