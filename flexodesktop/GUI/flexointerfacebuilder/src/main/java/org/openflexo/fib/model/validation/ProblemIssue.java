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

import java.util.Enumeration;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fib.model.FIBModelObject;

/**
 * Represents a validation issue requiring attention embedded in a validation report
 * 
 * @author sguerin
 * 
 */
public abstract class ProblemIssue<R extends ValidationRule<R, C>, C extends FIBModelObject> extends ValidationIssue<R, C> {

	private static final Logger logger = Logger.getLogger(ProblemIssue.class.getPackage().getName());

	private Vector<FixProposal<R, C>> _fixProposals;

	private R _validationRule;

	private Vector<FIBModelObject> _relatedValidableObjects;

	public ProblemIssue(R rule, C anObject, String aMessage) {
		super(anObject, aMessage);
		_validationRule = rule;
		_fixProposals = new Vector<FixProposal<R, C>>();
		_relatedValidableObjects = new Vector<FIBModelObject>();
	}

	public ProblemIssue(R rule, C anObject, String aMessage, FixProposal<R, C> proposal) {
		this(rule, anObject, aMessage);
		if (proposal != null) {
			addToFixProposals(proposal);
		}
	}

	public ProblemIssue(R rule, C anObject, String aMessage, Vector<FixProposal<R, C>> fixProposals) {
		this(rule, anObject, aMessage);
		if (fixProposals != null) {
			for (FixProposal<R, C> fp : fixProposals) {
				addToFixProposals(fp);
			}
		}
	}

	public ProblemIssue(R rule, C anObject, String aMessage, FixProposal<R, C>... fixProposals) {
		this(rule, anObject, aMessage);
		if (fixProposals != null) {
			for (FixProposal<R, C> fp : fixProposals) {
				addToFixProposals(fp);
			}
		}
	}

	public void addToFixProposals(FixProposal<R, C> proposal) {
		_fixProposals.add(proposal);
		proposal.setProblemIssue(this);
	}

	public boolean isFixable() {
		return _fixProposals.size() > 0;
	}

	public void revalidateAfterFixing(boolean isDeleteAction) {
		Vector<ValidationIssue> allIssuesToRemove = getValidationReport().issuesRegarding(getObject());
		for (Enumeration e = getRelatedValidableObjects().elements(); e.hasMoreElements();) {
			FIBModelObject relatedValidable = (FIBModelObject) e.nextElement();
			allIssuesToRemove.addAll(getValidationReport().issuesRegarding(relatedValidable));
		}
		if (getObject().getEmbeddedObjects() != null) {
			for (FIBModelObject embedded : getObject().getEmbeddedObjects()) {
				allIssuesToRemove.addAll(getValidationReport().issuesRegarding(embedded));
			}
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Remove related issues");
		}
		getValidationReport().removeFromValidationIssues(allIssuesToRemove);
		if (!isDeleteAction) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Revalidate");
			}
			ValidationReport newReportForThisObject = getObject().validate();
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("Found " + newReportForThisObject.getValidationIssues().size() + " new issues for this revalidated object");
			}
			for (Enumeration e = newReportForThisObject.getValidationIssues().elements(); e.hasMoreElements();) {
				ValidationIssue newIssue = (ValidationIssue) e.nextElement();
				getValidationReport().addToValidationIssues(newIssue);
			}
		}
		for (Enumeration e = getRelatedValidableObjects().elements(); e.hasMoreElements();) {
			C relatedValidable = (C) e.nextElement();
			ValidationReport newReportForRelatedObject = relatedValidable.validate();
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("Found " + newReportForRelatedObject.getValidationIssues().size()
						+ " new issues for this revalidated related object");
			}
			for (Enumeration e2 = newReportForRelatedObject.getValidationIssues().elements(); e2.hasMoreElements();) {
				ValidationIssue newIssue = (ValidationIssue) e2.nextElement();
				getValidationReport().addToValidationIssues(newIssue);
			}
		}
	}

	public R getValidationRule() {
		return _validationRule;
	}

	public Vector<FIBModelObject> getRelatedValidableObjects() {
		return _relatedValidableObjects;
	}

	public void setRelatedValidableObjects(Vector<FIBModelObject> relatedValidableObjects) {
		_relatedValidableObjects = relatedValidableObjects;
	}

	public void addToRelatedValidableObjects(FIBModelObject object) {
		_relatedValidableObjects.add(object);
	}

	public void addToRelatedValidableObjects(List<? extends FIBModelObject> someObjects) {
		_relatedValidableObjects.addAll(someObjects);
	}

	public void removeFromRelatedValidableObjects(FIBModelObject object) {
		_relatedValidableObjects.remove(object);
	}

	@Override
	public boolean isProblemIssue() {
		return true;
	}

	public Vector<FixProposal<R, C>> getFixProposal() {
		return _fixProposals;
	}
}
