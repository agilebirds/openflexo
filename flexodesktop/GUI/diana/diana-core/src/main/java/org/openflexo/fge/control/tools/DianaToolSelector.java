package org.openflexo.fge.control.tools;

import java.beans.PropertyChangeEvent;

import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.DianaInteractiveEditor.EditorTool;
import org.openflexo.fge.control.notifications.ToolChanged;
import org.openflexo.fge.view.DianaViewFactory;

/**
 * Represents a widget allowing to choose a tool (see {@link EditorTool}
 * 
 * @author sylvain
 * 
 */
public abstract class DianaToolSelector<C, F extends DianaViewFactory<F, ? super C>> extends DianaToolImpl<C, F> {

	public DianaToolSelector(AbstractDianaEditor<?, F, ?> editor) {
		super();
		attachToEditor(editor);
	}

	/**
	 * Return the technology-specific component representing the tool selector
	 * 
	 * @return
	 */
	public abstract C getComponent();

	@Override
	public DianaInteractiveEditor<?, F, ?> getEditor() {
		return (DianaInteractiveEditor<?, F, ?>) super.getEditor();
	}

	public EditorTool getSelectedTool() {
		if (getEditor() != null) {
			return getEditor().getCurrentTool();
		}
		return EditorTool.SelectionTool;
	}

	public void selectTool(EditorTool tool) {
		if (getEditor() != null) {
			getEditor().setCurrentTool(tool);
		}
	}

	@Override
	public void attachToEditor(AbstractDianaEditor<?, F, ?> editor) {
		super.attachToEditor(editor);
		handleToolChanged();
	}

	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		if (evt.getPropertyName().equals(ToolChanged.EVENT_NAME)) {
			handleToolChanged();
		}
	}

	public abstract void handleToolChanged();

}