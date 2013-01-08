package org.openflexo.view.controller.model;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.FlexoProjectObject;

public class Location extends ControllerModelObject {
	private final FlexoObject object;

	private final FlexoPerspective perspective;

	private final FlexoEditor editor;

	public Location(FlexoEditor context, FlexoObject object, FlexoPerspective perspective) {
		super();
		this.editor = context;
		this.object = object;
		this.perspective = perspective;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + (editor == null ? 0 : editor.hashCode());
		result = prime * result + (object == null ? 0 : object.hashCode());
		result = prime * result + (perspective == null ? 0 : perspective.hashCode());
		return result;
	}

	@Override
	public String toString() {
		return (object != null ? object : "No object")
				+ " - "
				+ (perspective != null ? perspective.getName() : " No perspective" + " - "
						+ (editor != null ? editor.getProject() : "No project"));
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		Location other = (Location) obj;
		if (editor == null) {
			if (other.editor != null) {
				return false;
			}
		} else if (!editor.equals(other.editor)) {
			return false;
		}
		if (object == null) {
			if (other.object != null) {
				return false;
			}
		} else if (!object.equals(other.object)) {
			return false;
		}
		if (perspective == null) {
			if (other.perspective != null) {
				return false;
			}
		} else if (!perspective.equals(other.perspective)) {
			return false;
		}
		return true;
	}

	public FlexoObject getObject() {
		return object;
	}

	public FlexoPerspective getPerspective() {
		return perspective;
	}

	public FlexoEditor getEditor() {
		return editor;
	}

	public boolean isEditable() {
		if (getObject() instanceof FlexoProjectObject) {
			return ((FlexoProjectObject) getObject()).getProject() == getEditor().getProject();
		}
		return true;
	}

}
