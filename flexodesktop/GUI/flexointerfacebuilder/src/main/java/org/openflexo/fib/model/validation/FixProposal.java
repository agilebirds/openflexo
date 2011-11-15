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

import java.util.logging.Logger;

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.kvc.KVCObject;
import org.openflexo.localization.FlexoLocalization;

/**
 * Abstract automatic fix proposal for a validation issue
 * 
 * @author sguerin
 * 
 */
public abstract class FixProposal<R extends ValidationRule<R, C>, C extends FIBModelObject> extends KVCObject  {

	@SuppressWarnings("unused")
	private static final Logger logger = Logger.getLogger(FixProposal.class.getPackage().getName());

	private String _message;

	private String _localizedMessage;

	private ProblemIssue<R, C> _issue;

	public FixProposal(String aMessage) {
		super();
		_message = aMessage;
	}

	public FixProposal(String aMessage, boolean localizedMessage) {
		this(aMessage);
		if (localizedMessage) {
			_localizedMessage = aMessage;
		}
	}

	public String getMessage() {
		return _message;
	}

	public String getLocalizedMessage() {
		if ((_localizedMessage == null) && (getProblemIssue() != null) && (getObject() != null)) {
			_localizedMessage = FlexoLocalization.localizedForKeyWithParams(_message, this);
		}
		return _localizedMessage;
	}

	public void setMessage(String message) {
		this._message = message;
	}

	public C getObject() {
		return getProblemIssue().getObject();
	}

	public void apply() {
		apply(true);
	}

	public void apply(boolean revalidateAfterFixing) {
		fixAction();
		if (revalidateAfterFixing) {
			getProblemIssue().revalidateAfterFixing(false);
		}
	}

	protected abstract void fixAction();

	public void setProblemIssue(ProblemIssue<R, C> issue) {
		_issue = issue;
	}

	public ProblemIssue<R, C> getProblemIssue() {
		return _issue;
	}

	public boolean askConfirmation() {
		return false;
	}

}
