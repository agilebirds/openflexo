/**
 * 
 */
package org.openflexo.dgmodule.controller;

import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.openflexo.dg.file.DGScreenshotFile;
import org.openflexo.dgmodule.view.DGFileModuleView;
import org.openflexo.dgmodule.view.DGRepositoryModuleView;
import org.openflexo.dgmodule.view.DGTemplateFileModuleView;
import org.openflexo.dgmodule.view.GeneratedDocModuleView;
import org.openflexo.doceditor.controller.DEController;
import org.openflexo.doceditor.view.DETOCEntryModuleView;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProjectObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GeneratedDoc;
import org.openflexo.foundation.cg.action.AbstractGCAction;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.icon.DGIconLibrary;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class DocGeneratorPerspective extends FlexoPerspective {

	private final DGController dgController;

	/**
	 * @param dgController
	 *            TODO
	 * @param name
	 */
	public DocGeneratorPerspective(DGController dgController) {
		super("doc_generation");
		this.dgController = dgController;
		setTopLeftView(dgController.getDgBrowserView());
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return DGIconLibrary.DG_DGP_ACTIVE_ICON;
	}

	@Override
	public JPanel getFooter() {
		return this.dgController._footer;
	}

	@Override
	public FlexoObject getDefaultObject(FlexoObject proposedObject) {
		if (proposedObject instanceof FlexoProjectObject) {
			if (((FlexoProjectObject) proposedObject).getProject().getGeneratedDoc().getGeneratedRepositories().size() > 0) {
				return ((FlexoProjectObject) proposedObject).getProject().getGeneratedDoc().getGeneratedRepositories().get(0);
			} else {
				return ((FlexoProjectObject) proposedObject).getProject().getGeneratedDoc();
			}
		}
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoObject object) {
		return object instanceof GeneratedDoc || object instanceof DGRepository || object instanceof CGFile
				|| object instanceof DGScreenshotFile || object instanceof CGTemplate || object instanceof TOCEntry;
	}

	@Override
	public ModuleView<? extends FlexoObject> createModuleViewForObject(FlexoObject object, FlexoController controller) {
		if (object instanceof GeneratedDoc) {
			return new GeneratedDocModuleView((GeneratedDoc) object, (DGController) controller,
					((DGController) controller).DOCUMENTATION_GENERATOR_PERSPECTIVE);
		} else if (object instanceof DGRepository) {
			return new DGRepositoryModuleView((DGRepository) object, (DGController) controller,
					((DGController) controller).DOCUMENTATION_GENERATOR_PERSPECTIVE);
		} else if (object instanceof CGFile) {
			return new DGFileModuleView((CGFile) object, (DGController) controller);
		} else if (object instanceof CGTemplate) {
			return new DGTemplateFileModuleView((CGTemplate) object, (DGController) controller);
		} else if (object instanceof TOCEntry) {
			return new DETOCEntryModuleView((TOCEntry) object, (DEController) controller, this);
		}
		if (DGController.logger.isLoggable(Level.INFO)) {
			DGController.logger.info("No module view for object: " + object + " and perspective: " + this);
		}
		return null;
	}

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		if (moduleView.getRepresentedObject() instanceof CGObject) {
			this.dgController._lastEditedCGRepository = AbstractGCAction.repositoryForObject((CGObject) moduleView.getRepresentedObject());
		}
		this.dgController.refreshFooter();
		if (moduleView instanceof DGFileModuleView) {
			((DGFileModuleView) moduleView).refresh();
		}
	}

}
