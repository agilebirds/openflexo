/**
 * 
 */
package org.openflexo.wse.controller;

import java.util.logging.Level;

import javax.swing.ImageIcon;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.DMPackage;
import org.openflexo.foundation.dm.DMRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.wkf.ws.ServiceInterface;
import org.openflexo.foundation.wkf.ws.ServiceMessageDefinition;
import org.openflexo.foundation.wkf.ws.ServiceOperation;
import org.openflexo.foundation.ws.ExternalWSFolder;
import org.openflexo.foundation.ws.FlexoWSLibrary;
import org.openflexo.foundation.ws.InternalWSFolder;
import org.openflexo.foundation.ws.WSPortTypeFolder;
import org.openflexo.foundation.ws.WSRepositoryFolder;
import org.openflexo.foundation.ws.WSService;
import org.openflexo.icon.WSEIconLibrary;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.wse.view.WSEDMEntityView;
import org.openflexo.wse.view.WSEDMPackageView;
import org.openflexo.wse.view.WSEDMRepositoryView;
import org.openflexo.wse.view.WSEExternalWSFolderView;
import org.openflexo.wse.view.WSEInternalWSFolderView;
import org.openflexo.wse.view.WSELibraryView;
import org.openflexo.wse.view.WSEMessageView;
import org.openflexo.wse.view.WSEPortTypeFolderView;
import org.openflexo.wse.view.WSEPortTypeView;
import org.openflexo.wse.view.WSERepositoryFolderView;
import org.openflexo.wse.view.WSEServiceOperationView;
import org.openflexo.wse.view.WSEServiceView;

public class WSEPerspective extends FlexoPerspective<FlexoModelObject> {

	/**
	 * @param name
	 */
	public WSEPerspective() {
		super("webserviceeditor_perspective");
	}

	/**
	 * Overrides getIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getActiveIcon()
	 */
	@Override
	public ImageIcon getActiveIcon() {
		return WSEIconLibrary.WSE_WSEP_ACTIVE_ICON;
	}

	/**
	 * Overrides getSelectedIcon
	 * 
	 * @see org.openflexo.view.FlexoPerspective#getSelectedIcon()
	 */
	@Override
	public ImageIcon getSelectedIcon() {
		return WSEIconLibrary.WSE_WSEP_SELECTED_ICON;
	}

	@Override
	public FlexoModelObject getDefaultObject(FlexoModelObject proposedObject) {
		if (hasModuleViewForObject(proposedObject)) {
			return proposedObject;
		}
		return null;
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return true;
	}

	@Override
	public ModuleView<? extends FlexoModelObject> createModuleViewForObject(FlexoModelObject object, FlexoController controller) {
		if (object instanceof FlexoProject) {
			return new WSELibraryView(((FlexoProject) object).getFlexoWSLibrary(), (WSEController) controller);
		} else if (object instanceof FlexoWSLibrary) {
			return new WSELibraryView((FlexoWSLibrary) object, (WSEController) controller);
		} else if (object instanceof ExternalWSFolder) {
			return new WSEExternalWSFolderView((ExternalWSFolder) object, (WSEController) controller);
		} else if (object instanceof InternalWSFolder) {
			return new WSEInternalWSFolderView((InternalWSFolder) object, (WSEController) controller);
		} else if (object instanceof WSService) {
			return new WSEServiceView((WSService) object, (WSEController) controller);
		} else if (object instanceof WSPortTypeFolder) {
			return new WSEPortTypeFolderView((WSPortTypeFolder) object, (WSEController) controller);
		} else if (object instanceof WSRepositoryFolder) {
			return new WSERepositoryFolderView((WSRepositoryFolder) object, (WSEController) controller);
		} else if (object instanceof DMPackage) {
			return new WSEDMPackageView((DMPackage) object, (WSEController) controller);
		} else if (object instanceof DMRepository) {
			return new WSEDMRepositoryView((DMRepository) object, (WSEController) controller);
		} else if (object instanceof DMEntity) {
			return new WSEDMEntityView((DMEntity) object, (WSEController) controller);
		} else if (object instanceof ServiceInterface) {
			return new WSEPortTypeView((ServiceInterface) object, (WSEController) controller);
		} else if (object instanceof ServiceOperation) {
			return new WSEServiceOperationView((ServiceOperation) object, (WSEController) controller);
		} else if (object instanceof ServiceMessageDefinition) {
			return new WSEMessageView((ServiceMessageDefinition) object, (WSEController) controller);
		} else {
			if (WSEController.logger.isLoggable(Level.FINE)) {
				WSEController.logger.fine("Cannot create view for a " + object.getClass().getName());
			}
			return null;
			// return new WSEViewExample(object, this);
		}
	}

}