package org.openflexo.rest.client;

import javax.swing.ImageIcon;

import org.openflexo.fib.FIBLibrary;
import org.openflexo.fib.controller.FIBController;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.FIBView;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.ImageIconResource;
import org.openflexo.view.DefaultModuleView;
import org.openflexo.view.ModuleView;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.FlexoFIBController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class ServerRestClientPerspective extends FlexoPerspective {

	private static final ImageIcon IMAGE_ICON = new ImageIconResource("Icons/GUI/ServerPerspective.png");

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(ServerRestClientPerspective.class
			.getPackage().getName());

	public ServerRestClientPerspective() {
		super("server_client_perspective");
	}

	@Override
	public ImageIcon getActiveIcon() {
		return IMAGE_ICON;
	}

	@Override
	public ModuleView<?> createModuleViewForObject(FlexoModelObject object, FlexoController controller) {
		if (object instanceof FlexoProject) {
			ServerRestClientModel model = new ServerRestClientModel(controller, object.getProject());
			FIBComponent component = FIBLibrary.instance().retrieveFIBComponent(ServerRestClientModel.FIB_FILE);
			FIBController fibController = FIBController.instanciateController(component, FlexoLocalization.getMainLocalizer());
			if (fibController instanceof FlexoFIBController) {
				((FlexoFIBController) fibController).setFlexoController(controller);
			} else if (component.getControllerClass() != null) {
				logger.warning("Controller for component " + component + " is not an instanceof FlexoFIBController");
			}
			FIBView<?, ?> view = fibController.buildView(component);
			fibController.setDataObject(model);
			return new DefaultModuleView<FlexoProject>(controller, (FlexoProject) object, view, this);
		} else {
			return null;
		}
	}

	@Override
	public boolean hasModuleViewForObject(FlexoModelObject object) {
		return object instanceof FlexoProject;
	}

	@Override
	public FlexoModelObject getDefaultObject(FlexoModelObject proposedObject) {
		return proposedObject.getProject();
	}

}
