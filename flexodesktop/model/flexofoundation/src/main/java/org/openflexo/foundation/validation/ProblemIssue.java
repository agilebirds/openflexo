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

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.DeletableObject;

/**
 * Represents a validation issue requiring attention embedded in a validation report
 * 
 * @author sguerin
 * 
 */
public abstract class ProblemIssue<R extends ValidationRule<R, V>, V extends Validable> extends ValidationIssue<R, V> {

	private static final Logger logger = Logger.getLogger(ProblemIssue.class.getPackage().getName());

	private Vector<FixProposal<R, V>> _fixProposals;

	private R _validationRule;

	private Vector<Validable> _relatedValidableObjects;

	public ProblemIssue(R rule, V anObject, String aMessage) {
		super(anObject, aMessage);
		_validationRule = rule;
		_fixProposals = new Vector<FixProposal<R, V>>();
		_relatedValidableObjects = new Vector<Validable>();
	}

	public ProblemIssue(R rule, V anObject, String aMessage, FixProposal<R, V> proposal) {
		this(rule, anObject, aMessage);
		if (proposal != null) {
			addToFixProposals(proposal);
		}
	}

	public ProblemIssue(R rule, V anObject, String aMessage, Vector<FixProposal<R, V>> fixProposals) {
		this(rule, anObject, aMessage);
		if (fixProposals != null) {
			for (FixProposal<R, V> fp : fixProposals) {
				addToFixProposals(fp);
			}
		}
	}

	public void addToFixProposals(FixProposal<R, V> proposal) {
		_fixProposals.add(proposal);
		proposal.setProblemIssue(this);
	}

	public boolean isFixable() {
		return _fixProposals.size() > 0;
	}

	@Override
	public int getSize() {
		return _fixProposals.size();
	}

	@Override
	public FixProposal<R, V> getElementAt(int index) {
		if (_fixProposals == null) {
			return null;
		}
		return _fixProposals.elementAt(index);
	}

	public void revalidateAfterFixing(boolean isDeleteAction) {
		Vector<ValidationIssue> allIssuesToRemove = getValidationReport().issuesRegarding(getObject());
		for (Enumeration e = getRelatedValidableObjects().elements(); e.hasMoreElements();) {
			Validable relatedValidable = (Validable) e.nextElement();
			allIssuesToRemove.addAll(getValidationReport().issuesRegarding(relatedValidable));
		}
		if (getObject().getAllEmbeddedValidableObjects() != null) {
			for (Enumeration e = getObject().getAllEmbeddedValidableObjects().elements(); e.hasMoreElements();) {
				Validable embeddedValidable = (Validable) e.nextElement();
				allIssuesToRemove.addAll(getValidationReport().issuesRegarding(embeddedValidable));
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
			ValidationReport newReportForThisObject = getObject().validate(getValidationReport().getValidationModel());
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("Found " + newReportForThisObject.getValidationIssues().size() + " new issues for this revalidated object");
			}
			for (Enumeration e = newReportForThisObject.getValidationIssues().elements(); e.hasMoreElements();) {
				ValidationIssue newIssue = (ValidationIssue) e.nextElement();
				getValidationReport().addToValidationIssues(newIssue);
			}
		}
		for (Enumeration e = getRelatedValidableObjects().elements(); e.hasMoreElements();) {
			Validable relatedValidable = (Validable) e.nextElement();
			if (!(relatedValidable instanceof DeletableObject && ((DeletableObject) relatedValidable).isDeleted())) {
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("Revalidate related");
				}
				ValidationReport newReportForRelatedObject = relatedValidable.validate(getValidationReport().getValidationModel());
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
	}

	public R getValidationRule() {
		return _validationRule;
	}

	public Vector<Validable> getRelatedValidableObjects() {
		return _relatedValidableObjects;
	}

	public void setRelatedValidableObjects(Vector<Validable> relatedValidableObjects) {
		_relatedValidableObjects = relatedValidableObjects;
	}

	public void addToRelatedValidableObjects(Validable object) {
		_relatedValidableObjects.add(object);
	}

	public void addToRelatedValidableObjects(Vector<? extends Validable> someObjects) {
		_relatedValidableObjects.addAll(someObjects);
	}

	public void removeFromRelatedValidableObjects(Validable object) {
		_relatedValidableObjects.remove(object);
	}
}
