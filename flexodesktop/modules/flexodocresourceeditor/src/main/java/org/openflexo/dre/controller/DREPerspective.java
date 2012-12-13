/**
 * 
 */
package org.openflexo.dre.controller;

import javax.swing.ImageIcon;
import javax.swing.JComponent;

import org.openflexo.dre.view.DREBrowserView;
import org.openflexo.dre.view.DocCenterView;
import org.openflexo.dre.view.DocFolderView;
import org.openflexo.dre.view.DocItemView;
import org.openflexo.drm.DRMObject;
import org.openflexo.drm.DocItem;
import org.openflexo.drm.DocItemFolder;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.icon.DREIconLibrary;
import org.openflexo.view.EmptyPanel;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

class DREPerspective extends FlexoPerspective {

	/**
	 * 
	 */
	private final DREController controller;

	/**
	 * @param dreController
	 *            TODO
	 * @param name
	 */
	public DREPerspective(DREController controller) {
		super("docresourceeditor_perspective");
		this.controller = controller;
		setTopLeftView(new DREBrowserView(controller));
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return DREIconLibrary.DRE_DRE_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.controller.model.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return DREIconLibrary.DRE_DRE_SELECTED_ICON;
	}

	@Override
	public DRMObject getDefaultObject(FlexoObject proposedObject) {
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoObject object) {
		return true;
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoObject object, FlexoController controller) {
		if (object instanceof DocItemFolder) {
			if (((DocItemFolder) object).isRootFolder()) {
				return new DocCenterView((DocItemFolder) object, (DREController) controller);
			} else {
				return new DocFolderView((DocItemFolder) object, (DREController) controller);
			}
		} else if (object instanceof DocItem) {

			if (this.controller.docItemView == null) {
				this.controller.docItemView = new DocItemView((DocItem) object, (DREController) controller);
			} else {
				this.controller.docItemView.setDocItem((DocItem) object);
			}
			return this.controller.docItemView;
		} else {
			return new EmptyPanel<FlexoObject>(controller, this, object);
		}
	}

	@Override
	public JComponent getHeader() {
		return this.controller.getAdditionalActionPanel();
	}
}