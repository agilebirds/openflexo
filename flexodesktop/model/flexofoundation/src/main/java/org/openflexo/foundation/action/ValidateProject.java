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
package org.openflexo.foundation.action;

import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.CodeType;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.dkv.DKVValidationModel;
import org.openflexo.foundation.dm.DMValidationModel;
import org.openflexo.foundation.ie.IEValidationModel;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.ValidationFinishedNotification;
import org.openflexo.foundation.validation.ValidationInitNotification;
import org.openflexo.foundation.validation.ValidationNotification;
import org.openflexo.foundation.validation.ValidationProgressNotification;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.wkf.WKFValidationModel;
import org.openflexo.localization.FlexoLocalization;

public class ValidateProject extends FlexoAction<ValidateProject, FlexoModelObject, FlexoModelObject> {

	static final Logger logger = Logger.getLogger(ValidateProject.class.getPackage().getName());

	public static FlexoActionType<ValidateProject, FlexoModelObject, FlexoModelObject> actionType = new FlexoActionType<ValidateProject, FlexoModelObject, FlexoModelObject>(
			"validate_project") {

		/**
		 * Factory method
		 */
		@Override
		public ValidateProject makeNewAction(FlexoModelObject object, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
			return new ValidateProject(object, globalSelection, editor);
		}

		@Override
		public boolean isVisibleForSelection(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

		@Override
		public boolean isEnabledForSelection(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection) {
			return true;
		}

	};

	static {
		FlexoModelObject.addActionForClass(ValidateProject.actionType, FlexoProject.class);
	}

	ValidateProject(FlexoModelObject focusedObject, Vector<FlexoModelObject> globalSelection, FlexoEditor editor) {
		super(actionType, focusedObject, globalSelection, editor);
	}

	public FlexoProject getProject() {
		return getFocusedObject().getProject();
	}

	@Override
	protected void doAction(Object context) {
		logger.info("Validate project");
		makeFlexoProgress(FlexoLocalization.localizedForKey("check_model_consistency"), 5);
		setProgress(FlexoLocalization.localizedForKey("loading_required_resources"));

		if (getProject().getFlexoComponentLibrary(false) != null) {
			// We validate the component library model
			IEValidationModel ieValidationModel = new IEValidationModel(getProject(), CodeType.PROTOTYPE);
			ieValidationModel.addObserver(ieValidationObserver);
			ieValidationReport = getProject().getFlexoComponentLibrary().validate(ieValidationModel);
			ieValidationModel.deleteObserver(ieValidationObserver);
		}
		if (getProject().getFlexoWorkflow(false) != null) {
			// We validate the workflow model
			WKFValidationModel wkfValidationModel = new WKFValidationModel(getProject(), CodeType.PROTOTYPE);
			wkfValidationModel.addObserver(wkfValidationObserver);
			wkfValidationReport = getProject().getFlexoWorkflow().validate(wkfValidationModel);
			wkfValidationModel.deleteObserver(wkfValidationObserver);
		}
		if (getProject().getDKVModel(false) != null) {
			// We validate the dkv model
			DKVValidationModel dkvValidationModel = new DKVValidationModel(getProject(), CodeType.PROTOTYPE);
			dkvValidationModel.addObserver(dkvValidationObserver);
			dkvValidationReport = getProject().getDKVModel().validate(dkvValidationModel);
			dkvValidationModel.deleteObserver(dkvValidationObserver);
		}
		if (getProject().getDataModel(false) != null) {
			DMValidationModel dmValidationModel = new DMValidationModel(getProject(), CodeType.PROTOTYPE);
			dmValidationModel.addObserver(dmValidationObserver);
			dmValidationReport = getProject().getDataModel().validate(dmValidationModel);
			dmValidationModel.deleteObserver(dmValidationObserver);
		}
		hideFlexoProgress();
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
