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
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProjectObject;
import org.openflexo.foundation.FlexoProject;
import org.openflexo.foundation.toc.TOCData;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.icon.DEIconLibrary;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

/**
 * This perspective is used to represent all table of contents defined in the scope of current project<br>
 * At the left is presented a browser allowing to browse into the TOC, at the middle are the TOCEntry represented and edited.
 * 
 * @author sylvain
 * 
 */
public class TOCPerspective extends FlexoPerspective {

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
		tocBrowser = new FIBTOCBrowser(deController);
		infoLabel = new JLabel("Table of contents perspective");
		infoLabel.setFont(FlexoCst.SMALL_FONT);
		setTopLeftView(tocBrowser);
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

	@Override
	public JComponent getFooter() {
		return infoLabel;
	}

	@Override
	public FlexoModelObject getDefaultObject(FlexoObject proposedObject) {
		if (proposedObject instanceof TOCEntry) {
			return ((TOCEntry) proposedObject).getRepository();
		}
		if (proposedObject instanceof FlexoProjectObject) {
			FlexoProject project = ((FlexoProjectObject) proposedObject).getProject();
			if (project != null) {
				if (project.getTOCData().getRepositories().size() > 0) {
					return project.getTOCData().getRepositories().firstElement();
				} else {
					return project.getTOCData();
				}
			}
		}
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoObject object) {
		return object instanceof TOCEntry || object instanceof TOCRepository || object instanceof TOCData;
	}

	@Override
	public ModuleView<? extends FlexoModelObject> createModuleViewForObject(FlexoObject object, FlexoController controller) {
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

	public void setProject(FlexoProject project) {
		tocBrowser.setDataObject(project != null ? project.getTOCData() : null);
	}

}
