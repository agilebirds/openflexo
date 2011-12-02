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
package org.openflexo.generator;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Vector;
import java.util.logging.Handler;
import java.util.logging.Level;
import java.util.logging.LogRecord;
import java.util.logging.Logger;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.TargetType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGSymbolicDirectory;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.cg.action.AbstractGCAction;
import org.openflexo.foundation.cg.dm.CustomTemplateRepositoryChanged;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.cg.templates.CGTemplates;
import org.openflexo.foundation.cg.templates.TemplateFileNotification;
import org.openflexo.foundation.dkv.DKVValidationModel;
import org.openflexo.foundation.dm.DMValidationModel;
import org.openflexo.foundation.ie.IEValidationModel;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.GeneratedResourceData;
import org.openflexo.foundation.rm.cg.CGRepositoryFileResource;
import org.openflexo.foundation.validation.ValidationReport;
import org.openflexo.foundation.wkf.WKFValidationModel;
import org.openflexo.generator.action.ValidateProject;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.exception.ModelValidationException;
import org.openflexo.generator.exception.PermissionDeniedException;
import org.openflexo.logging.FlexoLoggingFormatter;
import org.openflexo.toolbox.LogListener;
import org.openflexo.velocity.FlexoVelocity;

public abstract class AbstractProjectGenerator<R extends GenerationRepository> extends MetaGenerator<FlexoProject, R> {

	private R repository;
	private CGTemplates templates;
	private TemplateLocator templateLocator;
	private Vector<Generator<? extends FlexoModelObject, R>> generators;
	private Vector<Logger> loggers;
	private AbstractGCAction action;
	public Vector<LogListener> logListeners;
	private Handler logHandler;
	protected FlexoLoggingFormatter formatter = new FlexoLoggingFormatter();
	private boolean handleLogs = false;

	public AbstractProjectGenerator(FlexoProject project, R repository) throws GenerationException {
		super(null, project);
		this.projectGenerator = this;
		this.repository = repository;
		this.templates = getDefaultTemplates();
		this.templateLocator = new TemplateLocator(templates, this);
		this.generators = new Vector<Generator<? extends FlexoModelObject, R>>();
		this.loggers = new Vector<Logger>();
		this.logListeners = new Vector<LogListener>();
		this.loggers.add(FlexoVelocity.getLogger());
		if (getRootOutputDirectory() != null) {
			if (!getRootOutputDirectory().exists()) {
				getRootOutputDirectory().mkdirs();
			}
			if (!getRootOutputDirectory().canWrite()) {
				throw new PermissionDeniedException(getRootOutputDirectory(), this);
			}
		}
		if (repository != null) {
			for (CGSymbolicDirectory symbDir : repository.getSymbolicDirectories().values()) {
				// GPO: I am almost sure that the next block is not required since when writing files to the disk, dirs will be created.
				// Meanwhile, I leave this here because it is safer and is pretty much harmless (although not nicely coded)
				if (symbDir.getDirectory().getFile() != null) {
					symbDir.getDirectory().getFile().mkdirs();
					if (!symbDir.getDirectory().getFile().canWrite()) {
						throw new PermissionDeniedException(symbDir.getDirectory().getFile(), this);
					}
				}
			}
			repository.addObserver(this);
			repository.setProjectGenerator(this);
		}
	}

	public abstract CGTemplates getDefaultTemplates();

	@Override
	public FlexoProject getProject() {
		return getObject();
	}

	@Override
	public R getRepository() {
		return repository;
	}

	@Override
	public TargetType getTarget() {
		if (repository != null) {
			return getRepository().getTarget();
		}
		return getProject().getTargetType();
	}

	/**
	 * @return
	 */
	public File getRootOutputDirectory() {
		if (repository != null) {
			return repository.getDirectory();
		}
		return null;
	}

	@Override
	public Vector<CGRepositoryFileResource> refreshConcernedResources(
			Vector<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>> forResources) {
		getRepository().disableObserving();
		Vector<CGRepositoryFileResource> returned = super.refreshConcernedResources(forResources);
		for (CGFile file : getRepository().getFiles()) {
			if (file.getResource() == null) {
				file.setMarkedForDeletion(true);
			} else {
				CGRepositoryFileResource resource = file.getResource();
				if (!returned.contains(resource)) {
					file.setMarkedForDeletion(true);
				}
			}
		}
		getRepository().enableObserving();
		getRepository().refresh();
		return returned;
	}

	@Override
	public TemplateLocator getTemplateLocator() {
		return templateLocator;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if ((dataModification.propertyName() != null && (dataModification.propertyName().equals("targetType") || dataModification
				.propertyName().equals("docType")))) {
			getTemplateLocator().notifyTemplateModified();
		}
		if (dataModification instanceof TemplateFileNotification) {
			getTemplateLocator().notifyTemplateModified();
		} else if (dataModification instanceof CustomTemplateRepositoryChanged) {
			getTemplateLocator().notifyTemplateModified();
		}
		super.update(observable, dataModification);
	}

	@Override
	public final AbstractGCAction getAction() {
		return action;
	}

	public final void setAction(AbstractGCAction action) {
		this.action = action;
	}

	protected void addToGenerators(Generator<? extends FlexoModelObject, R> generator) {
		generators.add(generator);
		if (!loggers.contains(generator.getGeneratorLogger())) {
			loggers.add(generator.getGeneratorLogger());
		}
	}

	public boolean checkModelConsistency() throws ModelValidationException {
		return checkModelConsistency(null, null, null, null);
	}

	public boolean checkModelConsistency(FlexoObserver ieValidationObserver, FlexoObserver wkfValidationObserver,
			FlexoObserver dmValidationObserver, FlexoObserver dkvValidationObserver) throws ModelValidationException {
		ModelValidationException thrownException = null;

		// We validate the component library model
		IEValidationModel ieValidationModel = new IEValidationModel(getProject(), getTarget());
		if (ieValidationObserver != null) {
			ieValidationModel.addObserver(ieValidationObserver);
		}
		ValidationReport report = getProject().getFlexoComponentLibrary().validate(ieValidationModel);
		if (ieValidationObserver != null) {
			ieValidationModel.deleteObserver(ieValidationObserver);
		}

		if (getAction() instanceof ValidateProject) {
			((ValidateProject) getAction()).setIeValidationReport(report);
		}

		if (report.getErrorNb() > 0) {
			thrownException = new ModelValidationException("Component library validation failed", "component_library_is_not_valid", report);
		}

		// We validate the workflow model
		WKFValidationModel wkfValidationModel = new WKFValidationModel(getProject(), getTarget());
		if (wkfValidationObserver != null) {
			wkfValidationModel.addObserver(wkfValidationObserver);
		}
		report = getProject().getFlexoWorkflow().validate(wkfValidationModel);
		if (wkfValidationObserver != null) {
			wkfValidationModel.deleteObserver(wkfValidationObserver);
		}

		if (getAction() instanceof ValidateProject) {
			((ValidateProject) getAction()).setWkfValidationReport(report);
		}

		if (report.getErrorNb() > 0) {
			thrownException = new ModelValidationException("Workflow validation failed", "workflow_is_not_valid", report);
		}

		// We validate the dkv model
		DKVValidationModel dkvValidationModel = new DKVValidationModel(getProject(), getTarget());
		if (dkvValidationObserver != null) {
			dkvValidationModel.addObserver(dkvValidationObserver);
		}
		report = getProject().getDKVModel().validate(dkvValidationModel);
		if (dkvValidationObserver != null) {
			dkvValidationModel.deleteObserver(dkvValidationObserver);
		}

		if (getAction() instanceof ValidateProject) {
			((ValidateProject) getAction()).setDkvValidationReport(report);
		}

		if (report.getErrorNb() > 0) {
			thrownException = new ModelValidationException("DKV validation failed", "domainkeyvalue_is_not_valid", report);
		}

		DMValidationModel dmValidationModel = new DMValidationModel(getProject(), getTarget());
		if (dmValidationObserver != null) {
			dmValidationModel.addObserver(dmValidationObserver);
		}
		report = getProject().getDataModel().validate(dmValidationModel);
		if (dmValidationObserver != null) {
			dmValidationModel.deleteObserver(dmValidationObserver);
		}

		if (getAction() instanceof ValidateProject) {
			((ValidateProject) getAction()).setDmValidationReport(report);
		}

		if (report.getErrorNb() > 0) {
			thrownException = new ModelValidationException("Data model validation failed", "data_model_is_not_valid", report);
		}

		if (thrownException != null) {
			throw thrownException;
		}

		return true;
	}

	public void err(String log) {
		for (LogListener l : logListeners) {
			l.err(log);
		}
	}

	public void warn(String log) {
		for (LogListener l : logListeners) {
			l.warn(log);
		}
	}

	public void log(String log) {
		for (LogListener l : logListeners) {
			l.log(log);
		}
	}

	private Handler getLogHandler() {
		if (logHandler == null) {
			logHandler = new Handler() {
				@Override
				public void close() throws SecurityException {
				}

				@Override
				public void flush() {
					getRepository().notifyLogAdded();
				}

				@Override
				public void publish(LogRecord record) {
					if (record.getLevel().intValue() > Level.INFO.intValue()) {
						err(formatter.format(record));
					} else {
						log(formatter.format(record));
					}
				}
			};
		}
		return logHandler;
	}

	public void notifyExceptionOccured(GenerationException exception) {
		if (handleLogs) {
			StackTraceElement[] st = exception.getStackTrace();
			err("Exception occured: " + exception.getLocalizedMessage() + "\n");
			err("Details:\n" + exception.getDetails() + "\n");
			err(exception.toString() + "\n");
			for (StackTraceElement el : st) {
				err("\tat " + el + "\n");
			}
			if (exception.getTargetException() != null) {
				err("Caused by:\n");
				err(exception.getTargetException().toString() + "\n");
				for (StackTraceElement el : exception.getTargetException().getStackTrace()) {
					err("\tat " + el + "\n");
				}
				Throwable t = exception.getTargetException();
				while (t.getCause() != null) {
					t = t.getCause();
				}
				if (!exception.getTargetException().equals(t)) {
					err("Deeply caused by:\n");
					err(t.toString() + "\n");
					for (StackTraceElement el : t.getStackTrace()) {
						err("\tat " + el + "\n");
					}
				}
			}
			getLogHandler().flush();
		}
	}

	public void startHandleLogs() {
		handleLogs = true;
		for (Logger tempLogger : loggers) {
			if (tempLogger != null) {
				boolean alreadyInside = false;
				for (Handler already : tempLogger.getHandlers()) {
					if (already == getLogHandler()) {
						alreadyInside = true;
					}
				}
				if (!alreadyInside) {
					tempLogger.addHandler(getLogHandler());
				}
			}
		}
	}

	public void flushLogs() {
		getLogHandler().flush();
	}

	public void stopHandleLogs() {
		handleLogs = false;
		for (Logger tempLogger : loggers) {
			if (tempLogger != null) {
				tempLogger.removeHandler(getLogHandler());
			}
		}
	}

	public void addToLogListeners(LogListener listener) {
		logListeners.add(listener);
	}

	public void removeFromLogListeners(LogListener listener) {
		logListeners.remove(listener);
	}

	public void copyAdditionalFiles() throws IOException {
		// To overwrite if the repository needs additional files
	}

	public abstract boolean hasBeenInitialized();

	/**
	 * Override this method to force an order for the file generation. <br>
	 * Useful is a generation can alter the result of another generation.
	 * 
	 * @param resources
	 *            the resources which will be generated
	 */
	public void sortResourcesForGeneration(
			List<CGRepositoryFileResource<? extends GeneratedResourceData, IFlexoResourceGenerator, CGFile>> resources) {
		FlexoResource.sortResourcesWithDependancies(resources);
	}
}
