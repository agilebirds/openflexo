package org.openflexo.fge.control.tools;

import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JToggleButton;

import org.openflexo.fge.FGEIconLibrary;
import org.openflexo.fge.control.DianaInteractiveEditor;
import org.openflexo.fge.control.DianaInteractiveEditor.EditorTool;

@SuppressWarnings("serial")
public class ToolPanel extends JPanel {

	private final EditorToolbox editorToolbox;

	private ToolButton selectionToolButton;
	private ToolButton drawShapeToolButton;
	private ToolButton drawConnectorToolButton;
	private ToolButton drawTextToolButton;

	public ToolPanel(EditorToolbox editorToolbox) {
		super(new FlowLayout(FlowLayout.LEADING, 0, 0));
		this.editorToolbox = editorToolbox;
		selectionToolButton = new ToolButton(EditorTool.SelectionTool, FGEIconLibrary.SELECTION_TOOL_ICON,
				FGEIconLibrary.SELECTION_TOOL_SELECTED_ICON);
		drawShapeToolButton = new ToolButton(EditorTool.DrawShapeTool, FGEIconLibrary.DRAW_SHAPE_TOOL_ICON,
				FGEIconLibrary.DRAW_SHAPE_TOOL_SELECTED_ICON);
		drawConnectorToolButton = new ToolButton(EditorTool.DrawConnectorTool, FGEIconLibrary.DRAW_CONNECTOR_TOOL_ICON,
				FGEIconLibrary.DRAW_CONNECTOR_TOOL_SELECTED_ICON);
		drawTextToolButton = new ToolButton(EditorTool.DrawTextTool, FGEIconLibrary.DRAW_TEXT_TOOL_ICON,
				FGEIconLibrary.DRAW_TEXT_TOOL_SELECTED_ICON);
		add(new JLabel(FGEIconLibrary.TOOLBAR_LEFT_ICON));
		add(selectionToolButton);
		add(new JLabel(FGEIconLibrary.TOOLBAR_SPACER_ICON));
		add(drawShapeToolButton);
		add(new JLabel(FGEIconLibrary.TOOLBAR_SPACER_ICON));
		add(drawConnectorToolButton);
		add(new JLabel(FGEIconLibrary.TOOLBAR_SPACER_ICON));
		add(drawTextToolButton);
		add(new JLabel(FGEIconLibrary.TOOLBAR_RIGHT_ICON));
		updateButtons();
	}

	private void selectTool(EditorTool tool) {
		this.editorToolbox.getController().setCurrentTool(tool);
		updateButtons();
	}

	public void updateButtons() {
		selectionToolButton.setSelected(this.editorToolbox.getController().getCurrentTool() == EditorTool.SelectionTool);
		drawShapeToolButton.setSelected(this.editorToolbox.getController().getCurrentTool() == EditorTool.DrawShapeTool);
		drawConnectorToolButton.setSelected(this.editorToolbox.getController().getCurrentTool() == EditorTool.DrawConnectorTool);
		drawTextToolButton.setSelected(this.editorToolbox.getController().getCurrentTool() == EditorTool.DrawTextTool);
	}

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
}