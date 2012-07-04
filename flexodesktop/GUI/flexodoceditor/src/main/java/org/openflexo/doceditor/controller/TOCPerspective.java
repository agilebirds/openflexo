/**
 * 
 */
package org.openflexo.doceditor.controller;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.ImageIcon;
import javax.swing.JComponent;
import javax.swing.JLabel;

import org.openflexo.FlexoCst;
import org.openflexo.doceditor.controller.browser.FIBTOCBrowser;
import org.openflexo.doceditor.view.DERepositoryModuleView;
import org.openflexo.doceditor.view.DETOCEntryModuleView;
import org.openflexo.doceditor.view.TOCDataView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.toc.TOCData;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.icon.DEIconLibrary;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;

/**
 * This perspective is used to represent all table of contents defined in the scope of current project<br>
 * At the left is presented a browser allowing to browse into the TOC, at the middle are the TOCEntry represented and edited.
 * 
 * @author sylvain
 * 
 */
public class TOCPerspective extends FlexoPerspective<FlexoModelObject> {

	protected static final Logger logger = Logger.getLogger(TOCPerspective.class.getPackage().getName());

	private FIBTOCBrowser tocBrowser;

	private JLabel infoLabel;
	private final DEController deController;

	/**
	 * @param deController
	 */
	public TOCPerspective(DEController deController) {
		super("table_of_contents");
		this.deController = deController;
		tocBrowser = new FIBTOCBrowser(deController.getProject().getTOCData(), deController);
		infoLabel = new JLabel("Table of contents perspective");
		infoLabel.setFont(FlexoCst.SMALL_FONT);
	}

	@Override
	public boolean isAlwaysVisible() {
		return true;
	}

	@Override
	public boolean doesPerspectiveControlLeftView() {
		return true;
	}

	@Override
	public FIBTOCBrowser getLeftView() {
		return tocBrowser;
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return DEIconLibrary.DE_TOC_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return DEIconLibrary.DE_TOC_SELECTED_ICON;
	}

	@Override
	public JComponent getFooter() {
		return infoLabel;
	}

	@Override
	public FlexoModelObject getDefaultObject(FlexoModelObject proposedObject) {
		System.out.println("Proposed object in TOCPerspective: " + proposedObject);
		if (proposedObject instanceof TOCEntry) {
			return ((TOCEntry) proposedObject).getRepository();
		} else if (this.deController.getProject().getTOCData().getRepositories().size() > 0) {
			return this.deController.getProject().getTOCData().getRepositories().firstElement();
		} else {
			return this.deController.getProject().getTOCData();
		}
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return ((object instanceof TOCEntry) || (object instanceof TOCRepository) || (object instanceof TOCData));
	}

	@Override
	public ModuleView<? extends FlexoModelObject> createModuleViewForObject(FlexoModelObject object, FlexoController controller) {
		if (object instanceof TOCRepository) {
			return new DERepositoryModuleView((TOCRepository) object, (DEController) controller, this);
		} else if (object instanceof TOCData) {
			return new TOCDataView((TOCData) object, (DEController) controller);
		} else if (object instanceof TOCEntry) {
			return new DETOCEntryModuleView((TOCEntry) object, (DEController) controller, this);
		}
		if (logger.isLoggable(Level.INFO)) {
			logger.info("No module view for object: " + object + " and perspective: " + this);
		}
		return null;
	}

	@Override
	public void notifyModuleViewDisplayed(ModuleView<?> moduleView) {
		super.notifyModuleViewDisplayed(moduleView);
	}

}