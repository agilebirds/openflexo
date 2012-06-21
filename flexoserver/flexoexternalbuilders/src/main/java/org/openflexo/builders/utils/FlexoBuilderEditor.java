package org.openflexo.builders.utils;

import java.util.EventObject;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.SwingUtilities;
import javax.swing.SwingWorker;

import org.openflexo.builders.FlexoExternalMainWithProject;
import org.openflexo.components.ProgressWindow;
import org.openflexo.dg.ProjectDocGenerator;
import org.openflexo.dg.docx.ProjectDocDocxGenerator;
import org.openflexo.dg.html.ProjectDocHTMLGenerator;
import org.openflexo.dg.latex.ProjectDocLatexGenerator;
import org.openflexo.foundation.DefaultFlexoEditor;
import org.openflexo.foundation.FlexoException;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.ValidateProject;
import org.openflexo.foundation.cg.CGRepository;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GenerationRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoProgressFactory;
import org.openflexo.generator.AbstractProjectGenerator;
import org.openflexo.generator.ProjectGenerator;
import org.openflexo.generator.action.GCAction.ProjectGeneratorFactory;
import org.openflexo.generator.exception.GenerationException;

/**
 * This class extends the DefaultFlexoEditor for the Flexo server application. So far the implementation is just a simple extension of the
 * default editor but it may be used later for additional purposes.
 * 
 * @author gpolet
 * 
 */
public class FlexoBuilderEditor extends DefaultFlexoEditor implements ProjectGeneratorFactory {

	protected static final Logger logger = Logger.getLogger(FlexoBuilderEditor.class.getPackage().getName());

	private AbstractProjectGenerator<? extends GenerationRepository> projectGenerator;

	private ProjectDocGenerator projectDocGenerator;

	private final FlexoExternalMainWithProject externalMainWithProject;

	public FlexoBuilderEditor(FlexoExternalMainWithProject externalMainWithProject, FlexoProject project) {
		super(project);
		this.externalMainWithProject = externalMainWithProject;
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

	private volatile List<FlexoAction<?, ?, ?>> todos;

	private Runnable whenDone;

	protected Exception exception;

	@Override
	public <A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> A performAction(final A action,
			EventObject event) {
		if (action.getActionType() == ValidateProject.actionType) {
			return action;
		}
		if (action.isLongRunningAction() && SwingUtilities.isEventDispatchThread()) {
			ProgressWindow.showProgressWindow(action.getLocalizedName(), 100);
			SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					try {
						action.doActionInContext();
					} catch (Exception e) {
						e.printStackTrace();
						FlexoBuilderEditor.this.exception = e;
					}
					return null;
				}

				@Override
				protected void done() {
					if (!action.isEmbedded()) {
						doNextTodo(action);
					}
				}
			};
			worker.execute();
		} else {
			try {
				action.doActionInContext();
			} catch (FlexoException e) {
				e.printStackTrace();
				FlexoBuilderEditor.this.exception = e;
			}
			if (!action.isEmbedded()) {
				doNextTodo(action);
			}
		}
		return action;
	}

	public void chainActions(List<FlexoAction<?, ?, ?>> actions, Runnable whenDone) {
		this.todos = actions;
		this.whenDone = whenDone;
		doNextTodo(null);
	}

	public <A extends FlexoAction<?, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> void doNextTodo(final A action) {
		if (action != null && (!action.hasActionExecutionSucceeded() || exception != null)) {
			externalMainWithProject.handleActionFailed(action);
		} else {
			if (todos == null && whenDone == null) {
				return;
			}
			if (todos.isEmpty()) {
				SwingUtilities.invokeLater(whenDone);
			} else {
				final FlexoAction<?, ?, ?> todo = todos.remove(0);
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						todo.doAction();
					}
				});
			}
		}
	}

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

}
