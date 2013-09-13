package org.openflexo.view;

import java.awt.BorderLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.controller.model.FlexoPerspective;

public class NoModuleView<O extends FlexoModelObject> extends JPanel implements ModuleView<O> {

	private final O representedObject;
	private final FlexoPerspective perspective;
	private final FlexoController controller;

	public NoModuleView(FlexoController controller, O representedObject, String message, FlexoPerspective perspective,
			boolean localizeMessage) {
		super(new BorderLayout());
		this.controller = controller;
		this.representedObject = representedObject;
		this.perspective = perspective;
		JLabel label = new JLabel();
		label.setText(localizeMessage ? FlexoLocalization.localizedForKey(message, label) : message);
		add(label);
	}

	@Override
	public O getRepresentedObject() {
		return representedObject;
	}

	@Override
	public void deleteModuleView() {
		if (controller != null) {
			controller.removeModuleView(this);
		}
	}

	@Override
	public FlexoPerspective getPerspective() {
		return perspective;
	}

	@Override
	public void willShow() {
		// TODO Auto-generated method stub

	}

	@Override
	public void willHide() {
		// TODO Auto-generated method stub

	}

	@Override
	public boolean isAutoscrolled() {
		return false;
	}
}
