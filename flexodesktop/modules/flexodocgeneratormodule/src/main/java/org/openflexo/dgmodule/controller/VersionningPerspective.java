/**
 * 
 */
package org.openflexo.dgmodule.controller;

import java.util.logging.Level;

import javax.swing.ImageIcon;
import javax.swing.JPanel;

import org.openflexo.dg.file.DGLatexFile;
import org.openflexo.dg.file.DGScreenshotFile;
import org.openflexo.dgmodule.controller.browser.DGBrowser;
import org.openflexo.dgmodule.view.DGBrowserView;
import org.openflexo.dgmodule.view.DGFileHistoryModuleView;
import org.openflexo.dgmodule.view.DGFileModuleView;
import org.openflexo.dgmodule.view.DGRepositoryModuleView;
import org.openflexo.dgmodule.view.DGTemplateFileModuleView;
import org.openflexo.dgmodule.view.GeneratedDocModuleView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.DGRepository;
import org.openflexo.foundation.cg.GeneratedDoc;
import org.openflexo.foundation.cg.action.AbstractGCAction;
import org.openflexo.foundation.cg.templates.CGTemplate;
import org.openflexo.icon.DGIconLibrary;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class VersionningPerspective extends FlexoPerspective {

	private DGBrowser browser;
	private DGBrowserView dgBrowserView;

	private final DGController dgController;

	/**
	 * @param dgController
	 *            TODO
	 * @param name
	 */
	public VersionningPerspective(DGController dgController) {
		super("versionning");
		this.dgController = dgController;
		browser = new DGBrowser(dgController);
		dgBrowserView = new DGBrowserView(dgController, browser);
	}

	@Override
	public DGBrowserView getTopLeftView() {
		return dgBrowserView;
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return DGIconLibrary.DG_VP_ACTIVE_ICON;
	}

	@Override
	public JPanel getFooter() {
		return this.dgController._footer;
	}

	@Override
	public FlexoModelObject getDefaultObject(FlexoModelObject proposedObject) {
		// System.out.println("Proposed object in VersionningPerspective: " + proposedObject);
		/*if (proposedObject instanceof TOCEntry) {
			return ((TOCEntry) proposedObject).getRepository();
		} else {*/
		return this.dgController.getProject().getGeneratedDoc();
		// }
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return object instanceof GeneratedDoc || object instanceof DGRepository || object instanceof DGLatexFile
				|| object instanceof DGScreenshotFile || object instanceof CGTemplate;
	}

	@Override
	public ModuleView<? extends FlexoModelObject> createModuleViewForObject(FlexoModelObject object, FlexoController controller) {
		if (object instanceof GeneratedDoc) {
			return new GeneratedDocModuleView((GeneratedDoc) object, (DGController) controller,
					((DGController) controller).VERSIONNING_PERSPECTIVE);
		} else if (object instanceof DGRepository) {
			return new DGRepositoryModuleView((DGRepository) object, (DGController) controller,
					((DGController) controller).VERSIONNING_PERSPECTIVE);
		} else if (object instanceof CGFile) {
			return new DGFileHistoryModuleView((CGFile) object, (DGController) controller);
		} else if (object instanceof CGTemplate) {
			return new DGTemplateFileModuleView((CGTemplate) object, (DGController) controller);
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
