package org.openflexo.fge.control.tools;

import java.util.Observable;

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
public abstract class DianaToolSelector<C, F extends DianaViewFactory<F, ? super C>, ME> extends DianaToolImpl<C, F, ME> {

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
		return getEditor().getCurrentTool();
	}

	public void selectTool(EditorTool tool) {
		getEditor().setCurrentTool(tool);
	}

	@Override
	public void update(Observable o, Object notification) {
		if (o == getEditor() && notification instanceof ToolChanged) {
			handleToolChanged();
		}
	}

	public abstract void handleToolChanged();

}