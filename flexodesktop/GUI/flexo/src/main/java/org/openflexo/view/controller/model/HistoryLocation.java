package org.openflexo.view.controller.model;

import org.openflexo.foundation.FlexoObject;

public class HistoryLocation extends ControllerModelObject {
	private final FlexoObject object;

	private final FlexoPerspective perspective;

	protected HistoryLocation(FlexoObject object, FlexoPerspective perspective) {
		super();
		this.object = object;
		this.perspective = perspective;
	}

	public FlexoObject getObject() {
		return object;
	}

	public FlexoPerspective getPerspective() {
		return perspective;
	}
}
