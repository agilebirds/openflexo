/**
 * 
 */
package org.openflexo.doceditor.controller;

import java.util.logging.Level;

import javax.swing.ImageIcon;

import org.openflexo.doceditor.view.DERepositoryModuleView;
import org.openflexo.doceditor.view.DETOCDataModuleView;
import org.openflexo.doceditor.view.DETOCEntryModuleView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.toc.TOCData;
import org.openflexo.foundation.toc.TOCEntry;
import org.openflexo.foundation.toc.TOCObject;
import org.openflexo.foundation.toc.TOCRepository;
import org.openflexo.icon.DEIconLibrary;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;

public class DocEditorPerspective extends FlexoPerspective {

	/**
	 * @param name
	 */
	public DocEditorPerspective() {
		super("doc_editor");
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return DEIconLibrary.DE_DE_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return DEIconLibrary.DE_DE_SELECTED_ICON;
	}

	@Override
	public boolean isAlwaysVisible() {
		return true;
	}

	@Override
	public TOCObject getDefaultObject(FlexoModelObject proposedObject) {
		if ((proposedObject instanceof TOCObject) && hasModuleViewForObject(proposedObject)) {
			return (TOCObject) proposedObject;
		}

		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return ((object instanceof TOCEntry) || (object instanceof TOCRepository) || (object instanceof TOCData));
	}

	@Override
	public ModuleView<? extends TOCObject> createModuleViewForObject(TOCObject object, FlexoController controller) {
		if (object instanceof TOCRepository) {
			return new DERepositoryModuleView((TOCRepository) object, (DEController) controller, this);
		} else if (object instanceof TOCData) {
			return new DETOCDataModuleView((TOCData) object, (DEController) controller);
		} else if (object instanceof TOCEntry) {
			return new DETOCEntryModuleView((TOCEntry) object, (DEController) controller, this);
		}
		if (DEController.logger.isLoggable(Level.INFO)) {
			DEController.logger.info("No module view for object: " + object + " and perspective: " + this);
		}
		return null;
	}

}