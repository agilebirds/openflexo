package org.openflexo.view.controller.action;

import java.awt.event.ActionEvent;
import java.util.Vector;

import javax.swing.AbstractAction;

import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.action.FlexoAction;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.action.FlexoActionType;

public class EditionAction<A extends FlexoAction<A, T1, T2>, T1 extends FlexoModelObject, T2 extends FlexoModelObject> extends
		AbstractAction {

	private FlexoActionSource actionSource;
	private FlexoActionType<A, T1, T2> actionType;

	private T1 focusedObject;
	private Vector<T2> globalSelection;
	private FlexoEditor editor;

	public EditionAction(FlexoActionType<A, T1, T2> actionType, FlexoActionSource actionSource) {
		super();
		this.actionSource = actionSource;
		this.actionType = actionType;
	}

	public EditionAction(FlexoActionType<A, T1, T2> actionType, T1 focusedObject, Vector<T2> globalSelection, FlexoEditor editor) {
		super();
		this.actionType = actionType;
		this.focusedObject = focusedObject;
		this.globalSelection = globalSelection;
		this.editor = editor;
	}

	@Override
	public boolean isEnabled() {
		return super.isEnabled() && getEditor() != null && getEditor().isActionEnabled(actionType, focusedObject, globalSelection);
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		getEditor().performActionType(actionType, getFocusedObject(), getGlobalSelection(), e);
	}

	private FlexoEditor getEditor() {
		if (actionSource != null) {
			return actionSource.getEditor();
		}
		return editor;
	}

	public T1 getFocusedObject() {
		if (actionSource != null) {
			return (T1) actionSource.getFocusedObject();
		}
		return focusedObject;
	}

	public Vector<T2> getGlobalSelection() {
		if (actionSource != null) {
			return (Vector<T2>) actionSource.getGlobalSelection();
		}
		return globalSelection;
	}

}
