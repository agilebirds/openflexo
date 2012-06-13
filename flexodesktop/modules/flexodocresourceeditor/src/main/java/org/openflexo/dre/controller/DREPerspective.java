/**
 * 
 */
package org.openflexo.dre.controller;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.openflexo.dre.view.DocCenterView;
import org.openflexo.dre.view.DocFolderView;
import org.openflexo.dre.view.DocItemView;
import org.openflexo.drm.DRMObject;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemFolder;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.icon.DREIconLibrary;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;

class DREPerspective extends FlexoPerspective {

	/**
	 * 
	 */
	private final DREController dreController;

	/**
	 * @param dreController
	 *            TODO
	 * @param name
	 */
	public DREPerspective(DREController dreController) {
		super("docresourceeditor_perspective");
		this.dreController = dreController;
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return DREIconLibrary.DRE_DRE_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return DREIconLibrary.DRE_DRE_SELECTED_ICON;
	}

	@Override
	public DRMObject getDefaultObject(FlexoModelObject proposedObject) {
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return true;
	}

	@Override
	public ModuleView<? extends DRMObject> createModuleViewForObject(DRMObject object, FlexoController controller) {
		if (object instanceof DocItemFolder) {
			if (((DocItemFolder) object).isRootFolder()) {
				return new DocCenterView((DocItemFolder) object, (DREController) controller);
			} else {
				return new DocFolderView((DocItemFolder) object, (DREController) controller);
			}
		} else if (object instanceof DocItem) {

			if (this.dreController.docItemView == null) {
				this.dreController.docItemView = new DocItemView((DocItem) object, (DREController) controller);
			} else {
				this.dreController.docItemView.setDocItem((DocItem) object);
			}
			return this.dreController.docItemView;
		} else {
			return new EmptyPanel<DRMObject>(controller, this, object);
		}
	}

	@Override
	public JComponent getHeader() {
		return this.dreController.getAdditionalActionPanel();
	}
}