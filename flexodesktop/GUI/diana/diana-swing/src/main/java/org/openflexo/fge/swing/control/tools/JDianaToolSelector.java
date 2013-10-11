package org.openflexo.fge.swing.control.tools;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.openflexo.fge.FGEIconLibrary;
import org.openflexo.fge.control.AbstractDianaEditor;
import org.openflexo.fge.control.DianaInteractiveEditor.EditorTool;
import org.openflexo.fge.control.tools.DianaToolSelector;
import org.openflexo.fge.swing.SwingViewFactory;

/**
 * SWING implementation of the {@link DianaToolSelector}
 * 
 * @author sylvain
 * 
 */
public class JDianaToolSelector extends DianaToolSelector<JPanel, SwingViewFactory> {

	private ToolButton selectionToolButton;
	private ToolButton drawShapeToolButton;
	private ToolButton drawConnectorToolButton;
	private ToolButton drawTextToolButton;

	private JPanel component;

	private boolean isInitialized = false;

	public JDianaToolSelector(AbstractDianaEditor<?, SwingViewFactory, ?> editor) {
		super(editor);
		component = new JPanel();
		component.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));

		selectionToolButton = new ToolButton(EditorTool.SelectionTool, FGEIconLibrary.SELECTION_TOOL_ICON,
				FGEIconLibrary.SELECTION_TOOL_SELECTED_ICON);
		drawShapeToolButton = new ToolButton(EditorTool.DrawShapeTool, FGEIconLibrary.DRAW_SHAPE_TOOL_ICON,
				FGEIconLibrary.DRAW_SHAPE_TOOL_SELECTED_ICON);
		drawConnectorToolButton = new ToolButton(EditorTool.DrawConnectorTool, FGEIconLibrary.DRAW_CONNECTOR_TOOL_ICON,
				FGEIconLibrary.DRAW_CONNECTOR_TOOL_SELECTED_ICON);
		drawTextToolButton = new ToolButton(EditorTool.DrawTextTool, FGEIconLibrary.DRAW_TEXT_TOOL_ICON,
				FGEIconLibrary.DRAW_TEXT_TOOL_SELECTED_ICON);
		component.add(new JLabel(FGEIconLibrary.TOOLBAR_LEFT_ICON));
		component.add(selectionToolButton);
		component.add(new JLabel(FGEIconLibrary.TOOLBAR_SPACER_ICON));
		component.add(drawShapeToolButton);
		component.add(new JLabel(FGEIconLibrary.TOOLBAR_SPACER_ICON));
		component.add(drawConnectorToolButton);
		component.add(new JLabel(FGEIconLibrary.TOOLBAR_SPACER_ICON));
		component.add(drawTextToolButton);
		component.add(new JLabel(FGEIconLibrary.TOOLBAR_RIGHT_ICON));
		isInitialized = true;
		updateButtons();
	}

	@Override
	public JPanel getComponent() {
		return component;
	}

	@Override
	public void handleToolChanged() {
		updateButtons();
	}

	private void updateButtons() {
		if (isInitialized) {
			selectionToolButton.setSelected(getSelectedTool() == EditorTool.SelectionTool);
			drawShapeToolButton.setSelected(getSelectedTool() == EditorTool.DrawShapeTool);
			drawConnectorToolButton.setSelected(getSelectedTool() == EditorTool.DrawConnectorTool);
			drawTextToolButton.setSelected(getSelectedTool() == EditorTool.DrawTextTool);
		}
	}

	@SuppressWarnings("serial")
	public class ToolButton extends JToggleButton {
		// private final EditorTool tool;

		public ToolButton(final EditorTool tool, Icon icon, Icon selectedIcon) {
			super();
			// this.tool = tool;
			setIcon(icon);
			setPressedIcon(selectedIcon);
			setSelectedIcon(selectedIcon);
			setBorder(BorderFactory.createEmptyBorder());
			addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					if (isSelected()) {
						selectTool(tool);
					}
				}
			});
		}

		@Override
		public void setSelected(boolean b) {
			if (isSelected() != b) {
				super.setSelected(b);
			}
		}
	}

	@Override
	public SwingViewFactory getDianaFactory() {
		return SwingViewFactory.INSTANCE;
	}

}