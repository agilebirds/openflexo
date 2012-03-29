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

import java.text.Collator;
import java.util.Collections;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.StringTokenizer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoListModel;
import org.openflexo.localization.FlexoLocalization;

/**
 * Used to store and manage a set of ValidationRule
 * 
 * @author sguerin
 * 
 */
public abstract class ValidationModel extends FlexoListModel {

	private static final Logger logger = Logger.getLogger(ValidationModel.class.getPackage().getName());

	private Hashtable<Class, Vector<ValidationRule>> _rules;

	private Hashtable<Class, ValidationRuleSet> _inheritedRules;

	private boolean recomputeInheritedRules = true;

	private FlexoProject _project;
	private TargetType _targetType;

	public ValidationModel(FlexoProject project, TargetType targetType) {
		super();
		_targetType = targetType;
		_project = project;
		_rules = new Hashtable<Class, Vector<ValidationRule>>();
		_inheritedRules = new Hashtable<Class, ValidationRuleSet>();
	}

	@Override
	public String getDeletedProperty() {
		// TODO Auto-generated method stub
		return null;
	}

	/**
	 * Validate supplied Validable object by returning boolean indicating if validation throw errors (warnings are not considered as invalid
	 * model).
	 * 
	 * @param object
	 * @return
	 */
	public boolean isValid(Validable object) {
		return validate(object).getErrorNb() == 0;
	}

	/**
	 * Validate supplied Validable object by building and returning a new ValidationReport object.
	 * 
	 * @param object
	 * @return a newly created ValidationReport object
	 */
	public ValidationReport validate(Validable object) {
		ValidationReport returned = new ValidationReport(this, object);
		if (validate(object, returned)) {
			returned.addToValidationIssues(new InformationIssue(object, "consistency_check_ok"));
		}
		return returned;
	}

	/**
	 * Validate supplied Validable object by appending ValidationIssues object to supplied ValidationReport. Return true if no validation
	 * issues were found, false otherwise
	 * 
	 * @param object
	 * @param report
	 */
	public final <V extends Validable> boolean validate(V object, ValidationReport report) {
		int addedIssues = 0;

		// Get all the objects to validate
		Vector<? extends Validable> allEmbeddedValidableObjects = object.getAllEmbeddedValidableObjects();

		// Remove duplicated objects
		Vector<Validable> objectsToValidate = new Vector<Validable>();
		for (Enumeration<? extends Validable> en = allEmbeddedValidableObjects.elements(); en.hasMoreElements();) {
			Validable next = en.nextElement();
			if (!objectsToValidate.contains(next)) {
				objectsToValidate.add(next);
			}
		}

		// Compute validation steps and notify validation initialization
		int validationStepToNotify = 0;
		for (Enumeration<Validable> en = objectsToValidate.elements(); en.hasMoreElements();) {
			Validable next = en.nextElement();
			if (shouldNotifyValidation(next)) {
				validationStepToNotify++;
			}
		}
		setChanged();
		notifyObservers(new ValidationInitNotification(object, validationStepToNotify));

		// Perform the validation
		for (Enumeration<Validable> en = objectsToValidate.elements(); en.hasMoreElements();) {
			Validable next = en.nextElement();
			if (shouldNotifyValidation(next)) {
				setChanged();
				notifyObservers(new ValidationProgressNotification(object, next));
			}

			addedIssues += performValidation(next, report);

			/*ValidationRuleSet rules = getValidationRulesForObjectType(next.getClass());
			if (logger.isLoggable(Level.FINE))
				logger.fine("Validating " + next.getFullyQualifiedName()+" "+next.toString());
			
			if (shouldNotifyValidationRules()) {
			setChanged();
			notifyObservers(new ValidationSecondaryInitNotification(next, rules.getSize()));
			}

			for (Enumeration en2 = rules.elements(); en2.hasMoreElements();) {
			   ValidationRule rule = (ValidationRule) en2.nextElement();
			   if (logger.isLoggable(Level.FINE))
			   	logger.fine("Applying rule " + rule.getLocalizedName());

			   if (shouldNotifyValidationRules()) {
			   	setChanged();
			   	notifyObservers(new ValidationSecondaryProgressNotification(next, rule));
			   }

			   if (performRuleValidation(rule, next, report)) addedIssues++;
			}*/

		}

		// Notify validation is finished
		setChanged();
		notifyObservers(new ValidationFinishedNotification(object));

		return addedIssues == 0;
	}

	private <V extends Validable> int performValidation(V next, ValidationReport report) {
		int addedIssues = 0;

		ValidationRuleSet rules = getValidationRulesForObjectType(next.getClass());
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Validating " + next.getFullyQualifiedName() + " " + next.toString());
		}

		if (shouldNotifyValidationRules()) {
			setChanged();
			notifyObservers(new ValidationSecondaryInitNotification(next, rules.getSize()));
		}

		for (Enumeration<ValidationRule> en = rules.elements(); en.hasMoreElements();) {
			ValidationRule rule = en.nextElement();
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Applying rule " + rule.getLocalizedName());
			}

			if (shouldNotifyValidationRules()) {
				setChanged();
				notifyObservers(new ValidationSecondaryProgressNotification(next, rule));
			}

			if (performRuleValidation(rule, next, report)) {
				addedIssues++;
			}
		}

		return addedIssues;
	}

	private <R extends ValidationRule<R, V>, V extends Validable> boolean performRuleValidation(R rule, V next, ValidationReport report) {
		ValidationIssue<R, V> issue = rule.getIsEnabled() ? rule.applyValidation(next) : null;
		if (issue != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Adding issue " + issue);
			}
			issue.setCause(rule);
			report.addToValidationIssues(issue);
			if (fixAutomaticallyIfOneFixProposal()) {
				if (issue instanceof ProblemIssue && ((ProblemIssue<R, V>) issue).getSize() == 1) {
					if (logger.isLoggable(Level.INFO)) {
						logger.info("Fixing automatically...");
					}
					((ProblemIssue<R, V>) issue).getElementAt(0).apply(false);
					report.addToValidationIssues(new InformationIssue<R, V>(next, FlexoLocalization.localizedForKey("fixed_automatically:")
							+ " " + issue.getLocalizedMessage() + " : "
							+ ((ProblemIssue<R, V>) issue).getElementAt(0).getLocalizedMessage(), false));
				} else if (issue instanceof CompoundIssue) {
					for (ValidationIssue<R, V> containedIssue : ((CompoundIssue<R, V>) issue).getContainedIssues()) {
						if (containedIssue instanceof ProblemIssue && containedIssue.getSize() == 1) {
							report.addToValidationIssues(containedIssue);
							if (logger.isLoggable(Level.INFO)) {
								logger.info("Fixing automatically...");
							}
							((ProblemIssue<R, V>) containedIssue).getElementAt(0).apply(false);
							report.addToValidationIssues(new InformationIssue<R, V>(containedIssue.getObject(), FlexoLocalization
									.localizedForKey("fixed_automatically:")
									+ " "
									+ containedIssue.getLocalizedMessage()
									+ " : "
									+ ((ProblemIssue<R, V>) containedIssue).getElementAt(0).getLocalizedMessage(), false));
						}
					}
				}
			}
			return true;
		}
		return false;
	}

	/**
	 * Return a boolean indicating if validation of supplied object must be notified
	 * 
	 * @param next
	 * @return a boolean
	 */
	protected abstract boolean shouldNotifyValidation(Validable next);

	/**
	 * Return a boolean indicating if validation of each rule must be notified
	 * 
	 * @param next
	 * @return a boolean
	 */
	protected boolean shouldNotifyValidationRules() {
		return false;
	}

	public abstract boolean fixAutomaticallyIfOneFixProposal();

	/**
	 * Internally used to build ValidationModel
	 * 
	 * @param validationRule
	 * @param objectType
	 */
	protected void registerRule(ValidationRule validationRule) {
		Class objectType = validationRule.getObjectType();
		if (_rules.get(objectType) != null) {
			Vector<ValidationRule> existingRules = _rules.get(objectType);
			existingRules.add(validationRule);
		} else {
			Vector<ValidationRule> newVector = new Vector<ValidationRule>();
			newVector.add(validationRule);
			_rules.put(objectType, newVector);
		}
		recomputeInheritedRules = true;
	}

	protected void unregisterRule(ValidationRule validationRule) {
		Class objectType = validationRule.getObjectType();
		if (_rules.get(objectType) != null) {
			Vector<ValidationRule> existingRules = _rules.get(objectType);
			existingRules.remove(validationRule);
		}
		recomputeInheritedRules = true;
	}

	/**
	 * Return all known ValidationRule object known for supplied object type (supports basic class inheritance scheme)
	 * 
	 * @param objectType
	 * @return
	 */
	protected ValidationRuleSet getValidationRulesForObjectType(Class objectType) {
		if (recomputeInheritedRules) {
			recomputeInheritedRules(_filter);
		}
		if (_inheritedRules.get(objectType) != null) {
			return _inheritedRules.get(objectType);
		} else {
			Class current = objectType;
			while (current.getSuperclass() != null) {
				if (_inheritedRules.get(current.getSuperclass()) != null) {
					ValidationRuleSet newRuleSet = new ValidationRuleSet(objectType, _inheritedRules.get(current.getSuperclass())
							.getRules());
					_inheritedRules.put(objectType, newRuleSet);
					return newRuleSet;
				}
				current = current.getSuperclass();
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Could not find validation rules for type " + objectType.getName());
			}
			return new ValidationRuleSet(objectType);
		}
	}

	/**
	 * Internally used to maintain association between classes and a related set of ValidationRule objects.
	 */
	private void recomputeInheritedRules(ValidationRuleFilter filter) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("BEGIN recomputeInheritedRules()");
		}
		for (Enumeration e1 = _rules.keys(); e1.hasMoreElements();) {
			Class type1 = (Class) e1.nextElement();
			Vector rulesForType = _rules.get(type1);
			ValidationRuleSet ruleSet = new ValidationRuleSet(type1, rulesForType);
			if (_project != null) {
				for (Enumeration e2 = rulesForType.elements(); e2.hasMoreElements();) {
					ValidationRule rule = (ValidationRule) e2.nextElement();
					if (!rule.isValidForTarget(getTargetType()) || filter != null && !filter.accept(rule)) {
						ruleSet.removeRule(rule);
					}
				}
			}
			_inheritedRules.put(type1, ruleSet);
		}
		for (Enumeration<Class> e1 = _rules.keys(); e1.hasMoreElements();) {
			Class type1 = e1.nextElement();
			for (Enumeration<Class> e2 = _rules.keys(); e2.hasMoreElements();) {
				Class type2 = e2.nextElement();
				if (type1.isAssignableFrom(type2) && type1 != type2) {
					// type1 superclass for type2
					ValidationRuleSet superRules = _inheritedRules.get(type1);
					ValidationRuleSet childRules = _inheritedRules.get(type2);
					for (Enumeration e3 = superRules.elements(); e3.hasMoreElements();) {
						childRules.addRule((ValidationRule) e3.nextElement());
					}
				}
			}
		}
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("END recomputeInheritedRules()");
		}
		_keys = buildAllKeys();
		Collections.sort(_keys, new ClassComparator());
		recomputeInheritedRules = false;
	}

	private class ClassComparator implements Comparator<Class> {
		private Collator collator;

		ClassComparator() {
			collator = Collator.getInstance();
		}

		@Override
		public int compare(Class o1, Class o2) {
			String className1 = null;
			String className2 = null;
			StringTokenizer st1 = new StringTokenizer(o1.getName(), ".");
			while (st1.hasMoreTokens()) {
				className1 = st1.nextToken();
			}
			StringTokenizer st2 = new StringTokenizer(o2.getName(), ".");
			while (st2.hasMoreTokens()) {
				className2 = st2.nextToken();
			}
			if (className1 != null && className2 != null) {
				return collator.compare(className1, className2);
			}
			return 0;
		}

	}

	private Vector<Class> _keys;

	public Vector<Class> buildAllKeys() {
		Vector<Class> returned = new Vector<Class>();
		for (Enumeration e1 = _inheritedRules.elements(); e1.hasMoreElements();) {
			ValidationRuleSet next = (ValidationRuleSet) e1.nextElement();
			Class nextKey = next.getType();
			returned.add(nextKey);
		}
		return returned;
	}

	public void update() {
		recomputeInheritedRules(null);
	}

	private ValidationRuleFilter _filter = null;

	public void update(ValidationRuleFilter filter) {
		_filter = filter;
		recomputeInheritedRules(filter);
	}

	/**
	 * Implements
	 * 
	 * @see javax.swing.ListModel#getSize()
	 * @see javax.swing.ListModel#getSize()
	 */
	@Override
	public int getSize() {
		return _keys.size();
	}

	/**
	 * Implements
	 * 
	 * @see javax.swing.ListModel#getElementAt(int)
	 * @see javax.swing.ListModel#getElementAt(int)
	 */
	@Override
	public Object getElementAt(int index) {
		return _inheritedRules.get(_keys.elementAt(index));
	}

	public TargetType getTargetType() {
		return _targetType;
	}

	public void setTargetType(CodeType targetType) {
		_targetType = targetType;
	}

}
