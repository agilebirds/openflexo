package org.openflexo.builders.utils;

import java.awt.event.ActionEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.dg.ProjectDocGenerator;
import org.openflexo.dg.docx.ProjectDocDocxGenerator;
import org.openflexo.dg.html.ProjectDocHTMLGenerator;
import org.openflexo.dg.latex.ProjectDocLatexGenerator;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionEnableCondition;
import org.openflexo.foundation.action.FlexoActionFinalizer;
import org.openflexo.foundation.action.FlexoActionInitializer;
import org.openflexo.foundation.action.FlexoActionType;
import org.openflexo.foundation.action.FlexoExceptionHandler;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoProgressFactory;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.action.GCAction.ProjectGeneratorFactory;
import org.openflexo.generator.action.ValidateProject;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.view.controller.InteractiveFlexoEditor;

/**
 * This class extends the DefaultFlexoEditor for the Flexo server application. So far the implementation is just a simple extension of the
 * default editor but it may be used later for additional purposes.
 * 
 * @author gpolet
 * 
 */
public class FlexoBuilderEditor extends InteractiveFlexoEditor implements ProjectGeneratorFactory {

	protected static final Logger logger = Logger.getLogger(FlexoBuilderEditor.class.getPackage().getName());

	private AbstractProjectGenerator<? extends GenerationRepository> projectGenerator;

	private ProjectDocGenerator projectDocGenerator;

	public FlexoBuilderEditor(FlexoProject project) {
		super(project);
	}

	public FlexoBuilderEditor() {
		this(null);
	}

	@Override
	public AbstractProjectGenerator<? extends GenerationRepository> generatorForRepository(GenerationRepository repository) {
		if (repository instanceof CGRepository) {
			if (projectGenerator == null) {
				try {
					projectGenerator = new ProjectGenerator(this.getProject(), (CGRepository) repository);
				} catch (GenerationException e) {
					logger.log(Level.SEVERE, "Error while generating code.", e);
				}
			}
			return projectGenerator;
		} else if (repository instanceof DGRepository) {
			if (projectDocGenerator == null) {
				try {

					switch (repository.getFormat()) {
					case LATEX:
						projectDocGenerator = new ProjectDocLatexGenerator(this.getProject(), (DGRepository) repository);
						break;
					case HTML:
						projectDocGenerator = new ProjectDocHTMLGenerator(this.getProject(), (DGRepository) repository);
						break;
					case DOCX:
						projectDocGenerator = new ProjectDocDocxGenerator(this.getProject(), (DGRepository) repository);
						break;
					default:
						logger.severe("Invalid format in repository to generate documentation. Format: " + repository.getFormat());
					}
				} catch (GenerationException e) {
					logger.log(Level.SEVERE, "Error while generating documentation.", e);
				}
			}
			return projectDocGenerator;
		}
		return null;
	}

	private FlexoProgressFactory flexoProgressFactory;

	@Override
	public FlexoProgressFactory getFlexoProgressFactory() {
		return flexoProgressFactory;
	}

	public void setFactory(FlexoProgressFactory flexoProgressFactory) {
		this.flexoProgressFactory = flexoProgressFactory;
	}

	@Override
	public boolean performResourceScanning() {
		return false;
	}

	@Override
	public FlexoActionEnableCondition getEnableConditionFor(FlexoActionType actionType) {
		return null;
	}

	@Override
	public FlexoActionInitializer getInitializerFor(FlexoActionType actionType) {
		FlexoActionType _localActionType = actionType;
		if (_localActionType.equals(ValidateProject.actionType)) {
			return new FlexoActionInitializer<FlexoAction>() {

				@Override
				public boolean run(ActionEvent event, FlexoAction action) {
					return false;
				}

			};
		}
		return null;
	}

	@Override
	public FlexoActionFinalizer getFinalizerFor(FlexoActionType actionType) {
		return null;
	}

	@Override
	public FlexoExceptionHandler getExceptionHandlerFor(FlexoActionType actionType) {
		return null;
	}

	@Override
	public boolean isAutoSaveEnabledByDefault() {
		return false;
	}

}
