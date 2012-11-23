package org.openflexo.view.controller.model;

import org.openflexo.foundation.FlexoModelObject;

public class HistoryLocation extends ControllerModelObject {
	private final FlexoModelObject object;

	private final FlexoPerspective perspective;

	protected HistoryLocation(FlexoModelObject object, FlexoPerspective perspective) {
		super();
		this.object = object;
		this.perspective = perspective;
	}

	public FlexoModelObject getObject() {
		return object;
	}

	public FlexoPerspective getPerspective() {
		return perspective;
	}
}
