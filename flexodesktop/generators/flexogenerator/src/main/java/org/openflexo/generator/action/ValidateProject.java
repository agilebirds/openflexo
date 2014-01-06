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
package org.openflexo.generator.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.validation.ValidationFinishedNotification;
import org.openflexo.foundation.validation.ValidationInitNotification;
import org.openflexo.foundation.validation.ValidationNotification;
import org.openflexo.foundation.validation.ValidationProgressNotification;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.exception.ModelValidationException;
import org.openflexo.localization.FlexoLocalization;

public class ValidateProject extends GCAction<ValidateProject, GenerationRepository> {

	static final Logger logger = Logger.getLogger(ValidateProject.class.getPackage().getName());

	public static FlexoActionType<ValidateProject, GenerationRepository, CGObject> actionType = new FlexoActionType<ValidateProject, GenerationRepository, CGObject>(
			"validate_project", GENERATE_MENU, VALIDATION_GROUP, FlexoActionType.NORMAL_ACTION_TYPE) {

		/**
		 * Factory method
		 */
		@Override
		public ValidateProject makeNewAction(GenerationRepository object, Vector<CGObject> globalSelection, FlexoEditor editor) {
			return new ValidateProject(object, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(GenerationRepository focusedObject, Vector<CGObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(GenerationRepository focusedObject, Vector<CGObject> globalSelection) {
			return focusedObject.isConnected();
		}

	};

	static {
		TestModelObject.addActionForClass(ValidateProject.actionType, GenerationRepository.class);
	}

	@Override
	public boolean isLongRunningAction() {
		return true;
	}

	ValidateProject(GenerationRepository focusedObject, Vector<CGObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	@Override
	protected void doAction(Object context) throws ModelValidationException {
		logger.info("Validate project");

		AbstractProjectGenerator<? extends GenerationRepository> pg = getProjectGenerator();
		pg.setAction(this);

		getRepository();
		makeFlexoProgress(FlexoLocalization.localizedForKey("check_model_consistency"), 5);

		setProgress(FlexoLocalization.localizedForKey("loading_required_resources"));
		try {
			getProjectGenerator().checkModelConsistency(ieValidationObserver, wkfValidationObserver, dmValidationObserver,
					dkvValidationObserver);
		} catch (ModelValidationException e) {
			throw e;
		} finally {
			hideFlexoProgress();
		}
	}

	private ValidationReport ieValidationReport = null;
	private ValidationReport wkfValidationReport = null;
	private ValidationReport dkvValidationReport = null;
	private ValidationReport dmValidationReport = null;

	public boolean isProjectValid() {
		return getErrorsNb() == 0;
	}

	public int getErrorsNb() {
		int errorsNb = 0;
		if (ieValidationReport != null) {
			errorsNb += ieValidationReport.getErrorNb();
		}
		if (wkfValidationReport != null) {
			errorsNb += wkfValidationReport.getErrorNb();
		}
		if (dkvValidationReport != null) {
			errorsNb += dkvValidationReport.getErrorNb();
		}
		if (dmValidationReport != null) {
			errorsNb += dmValidationReport.getErrorNb();
		}
		return errorsNb;
	}

	private FlexoObserver ieValidationObserver = new FlexoObserver() {
		@Override
		public void update(FlexoObservable observable, DataModification dataModification) {
			if (dataModification instanceof ValidationNotification) {
				if (dataModification instanceof ValidationInitNotification) {
					ValidationInitNotification initNotification = (ValidationInitNotification) dataModification;
					setProgress(FlexoLocalization.localizedForKey("validating_ie_model"));
					resetSecondaryProgress(initNotification.getNbOfObjectToValidate());
					logger.info("validating_ie_model " + initNotification.getNbOfObjectToValidate());
				} else if (dataModification instanceof ValidationProgressNotification) {
					ValidationProgressNotification progressNotification = (ValidationProgressNotification) dataModification;
					setSecondaryProgress(FlexoLocalization.localizedForKey("validating") + " "
							+ progressNotification.getValidatedObject().getFullyQualifiedName());
				}
			} else if (dataModification instanceof ValidationFinishedNotification) {
				// Nothing
			}
		}
	};

	private FlexoObserver wkfValidationObserver = new FlexoObserver() {
		@Override
		public void update(FlexoObservable observable, DataModification dataModification) {
			if (dataModification instanceof ValidationNotification) {
				if (dataModification instanceof ValidationInitNotification) {
					ValidationInitNotification initNotification = (ValidationInitNotification) dataModification;
					setProgress(FlexoLocalization.localizedForKey("validating_wkf_model"));
					resetSecondaryProgress(initNotification.getNbOfObjectToValidate());
					logger.info("validating_wkf_model " + initNotification.getNbOfObjectToValidate());
				} else if (dataModification instanceof ValidationProgressNotification) {
					ValidationProgressNotification progressNotification = (ValidationProgressNotification) dataModification;
					setSecondaryProgress(FlexoLocalization.localizedForKey("validating") + " "
							+ progressNotification.getValidatedObject().getFullyQualifiedName());
				} else if (dataModification instanceof ValidationFinishedNotification) {
					// Nothing
				}

			}
		}
	};

	private FlexoObserver dkvValidationObserver = new FlexoObserver() {
		@Override
		public void update(FlexoObservable observable, DataModification dataModification) {
			if (dataModification instanceof ValidationNotification) {
				if (dataModification instanceof ValidationInitNotification) {
					ValidationInitNotification initNotification = (ValidationInitNotification) dataModification;
					setProgress(FlexoLocalization.localizedForKey("validating_dkv_model"));
					resetSecondaryProgress(initNotification.getNbOfObjectToValidate());
					logger.info("validating_dkv_model " + initNotification.getNbOfObjectToValidate());
				} else if (dataModification instanceof ValidationProgressNotification) {
					ValidationProgressNotification progressNotification = (ValidationProgressNotification) dataModification;
					setSecondaryProgress(FlexoLocalization.localizedForKey("validating") + " "
							+ progressNotification.getValidatedObject().getFullyQualifiedName());
				} else if (dataModification instanceof ValidationFinishedNotification) {
					// Nothing
				}

			}
		}
	};

	private FlexoObserver dmValidationObserver = new FlexoObserver() {
		@Override
		public void update(FlexoObservable observable, DataModification dataModification) {
			if (dataModification instanceof ValidationNotification) {
				if (dataModification instanceof ValidationInitNotification) {
					ValidationInitNotification initNotification = (ValidationInitNotification) dataModification;
					setProgress(FlexoLocalization.localizedForKey("validating_dm_model"));
					resetSecondaryProgress(initNotification.getNbOfObjectToValidate());
					logger.info("validating_dm_model " + initNotification.getNbOfObjectToValidate());
				} else if (dataModification instanceof ValidationProgressNotification) {
					ValidationProgressNotification progressNotification = (ValidationProgressNotification) dataModification;
					setSecondaryProgress(FlexoLocalization.localizedForKey("validating") + " "
							+ progressNotification.getValidatedObject().getFullyQualifiedName());
				} else if (dataModification instanceof ValidationFinishedNotification) {
					// Nothing
				}

			}
		}
	};

	public ValidationReport getDmValidationReport() {
		return dmValidationReport;
	}

	public void setDmValidationReport(ValidationReport dmValidationReport) {
		this.dmValidationReport = dmValidationReport;
	}

	public ValidationReport getIeValidationReport() {
		return ieValidationReport;
	}

	public void setIeValidationReport(ValidationReport ieValidationReport) {
		this.ieValidationReport = ieValidationReport;
	}

	public ValidationReport getWkfValidationReport() {
		return wkfValidationReport;
	}

	public void setWkfValidationReport(ValidationReport wkfValidationReport) {
		this.wkfValidationReport = wkfValidationReport;
	}

	public ValidationReport getDkvValidationReport() {
		return dkvValidationReport;
	}

	public void setDkvValidationReport(ValidationReport dkvValidationReport) {
		this.dkvValidationReport = dkvValidationReport;
	}

	public String readableValidationErrors() {
		StringBuffer bf = new StringBuffer();
		if (getWkfValidationReport() != null) {
			bf.append(getWkfValidationReport().errorAsString());
		}
		if (getIeValidationReport() != null) {
			bf.append(getIeValidationReport().errorAsString());
		}
		if (getDkvValidationReport() != null) {
			bf.append(getDkvValidationReport().errorAsString());
		}
		if (getDmValidationReport() != null) {
			bf.append(getDmValidationReport().errorAsString());
		}
		return bf.toString();
	}
}
