package org.openflexo.fge.swing.control.tools;

import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JToggleButton;

import org.openflexo.fge.FGEIconLibrary;
import org.openflexo.fge.control.DianaInteractiveEditor.DrawConnectorToolOption;
import org.openflexo.fge.control.DianaInteractiveEditor.DrawCustomShapeToolOption;
import org.openflexo.fge.control.DianaInteractiveEditor.DrawShapeToolOption;
import org.openflexo.fge.control.DianaInteractiveEditor.EditorTool;
import org.openflexo.fge.control.DianaInteractiveEditor.EditorToolOption;
import org.openflexo.fge.control.tools.DianaToolSelector;
import org.openflexo.fge.swing.JDianaInteractiveEditor;
import org.openflexo.fge.swing.SwingViewFactory;

/**
 * SWING implementation of the {@link DianaToolSelector}
 * 
 * @author sylvain
 * 
 */
public class JDianaToolSelector extends DianaToolSelector<JPanel, SwingViewFactory> {

	private static final Logger logger = Logger.getLogger(JDianaToolSelector.class.getPackage().getName());

	private ToolButton selectionToolButton;
	private ToolButton drawShapeToolButton;
	private ToolButton drawCustomShapeToolButton;
	private ToolButton drawConnectorToolButton;
	private ToolButton drawTextToolButton;

	private JPanel component;

	private boolean isInitialized = false;

	public JDianaToolSelector(JDianaInteractiveEditor<?> editor) {
		super(editor);
		component = new JPanel();
		component.setLayout(new FlowLayout(FlowLayout.LEADING, 0, 0));

		selectionToolButton = new ToolButton(EditorTool.SelectionTool, null);
		drawShapeToolButton = new ToolButton(EditorTool.DrawShapeTool, editor != null ? editor.getDrawShapeToolOption() : null);
		drawCustomShapeToolButton = new ToolButton(EditorTool.DrawCustomShapeTool, editor != null ? editor.getDrawCustomShapeToolOption()
				: null);
		drawConnectorToolButton = new ToolButton(EditorTool.DrawConnectorTool, editor != null ? editor.getDrawConnectorToolOption() : null);
		drawTextToolButton = new ToolButton(EditorTool.DrawTextTool, null);
		component.add(new JLabel(FGEIconLibrary.TOOLBAR_LEFT_ICON));
		component.add(selectionToolButton);
		component.add(new JLabel(FGEIconLibrary.TOOLBAR_SPACER_ICON));
		component.add(drawShapeToolButton);
		component.add(new JLabel(FGEIconLibrary.TOOLBAR_SPACER_ICON));
		component.add(drawCustomShapeToolButton);
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

	@Override
	public void handleToolOptionChanged() {
		updateButtons();
	}

	private void updateButtons() {
		if (isInitialized) {
			selectionToolButton.update();
			drawShapeToolButton.update();
			drawCustomShapeToolButton.update();
			drawConnectorToolButton.update();
			drawTextToolButton.update();
		}
	}

	@SuppressWarnings("serial")
	public class ToolButton extends JToggleButton {
		private EditorTool representedTool;

		public ToolButton(EditorTool aRepresentedTool, final EditorToolOption toolOption) {
			super();
			this.representedTool = aRepresentedTool;
			setBorder(BorderFactory.createEmptyBorder());
			addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					if (e.getPoint().x > getWidth() - 8 && e.getPoint().y > getHeight() - 8 && representedTool.getOptions() != null) {
						JPopupMenu contextualMenu = new JPopupMenu();
						for (final EditorToolOption option : representedTool.getOptions()) {
							JMenuItem menuItem = new JMenuItem(option.name(), getSelectedIconFor(option));
							menuItem.addActionListener(new ActionListener() {
								@Override
								public void actionPerformed(ActionEvent e) {
									selectTool(representedTool);
									setOption(option);
								}

							});
							contextualMenu.add(menuItem);
						}
						contextualMenu.show((Component) e.getSource(), e.getPoint().x, e.getPoint().y);
					}
					if (isSelected()) {
						selectTool(representedTool);
					} else {
						selectTool(EditorTool.SelectionTool);
					}
				}
			});

			update();

		}

		public void update() {
			setSelected(getSelectedTool() == representedTool);
			if (getOption() != null) {
				setIcon(getIconFor(getOption()));
				setPressedIcon(getSelectedIconFor(getOption()));
				setSelectedIcon(getSelectedIconFor(getOption()));
			} else {
				setIcon(getIconFor(representedTool));
				setPressedIcon(getSelectedIconFor(representedTool));
				setSelectedIcon(getSelectedIconFor(representedTool));
			}
		}

		@Override
		public void setSelected(boolean b) {
			if (isSelected() != b) {
				super.setSelected(b);
			}
		}

		public EditorToolOption getOption() {
			if (getEditor() != null) {
				switch (representedTool) {
				case SelectionTool:
					return null;
				case DrawShapeTool:
					return getEditor().getDrawShapeToolOption();
				case DrawCustomShapeTool:
					return getEditor().getDrawCustomShapeToolOption();
				case DrawConnectorTool:
					return getEditor().getDrawConnectorToolOption();
				case DrawTextTool:
					return null;
				default:
					logger.warning("Unexpected tool: " + representedTool);
				}
			}
			return null;
		}

		public void setOption(EditorToolOption option) {
			if (getEditor() != null) {
				System.out.println("Sets option with " + option);

				if (option instanceof DrawShapeToolOption) {
					getEditor().setDrawShapeToolOption((DrawShapeToolOption) option);
				} else if (option instanceof DrawCustomShapeToolOption) {
					getEditor().setDrawCustomShapeToolOption((DrawCustomShapeToolOption) option);
				} else if (option instanceof DrawConnectorToolOption) {
					getEditor().setDrawConnectorToolOption((DrawConnectorToolOption) option);
				}
			}
		}

	}

	@Override
	public SwingViewFactory getDianaFactory() {
		return SwingViewFactory.INSTANCE;
	}

	public Icon getIconFor(EditorTool tool) {
		switch (tool) {
		case SelectionTool:
			return FGEIconLibrary.SELECTION_TOOL_ICON;
		case DrawShapeTool:
			return FGEIconLibrary.DRAW_RECTANGLE_TOOL_ICON;
		case DrawCustomShapeTool:
			return FGEIconLibrary.DRAW_CUSTOM_POLYGON_TOOL_ICON;
		case DrawConnectorTool:
			return FGEIconLibrary.DRAW_LINE_TOOL_ICON;
		case DrawTextTool:
			return FGEIconLibrary.DRAW_TEXT_TOOL_ICON;
		default:
			logger.warning("Unexpected tool: " + tool);
			return null;
		}

	}

	public Icon getSelectedIconFor(EditorTool tool) {
		switch (tool) {
		case SelectionTool:
			return FGEIconLibrary.SELECTION_TOOL_SELECTED_ICON;
		case DrawShapeTool:
			return FGEIconLibrary.DRAW_RECTANGLE_TOOL_SELECTED_ICON;
		case DrawCustomShapeTool:
			return FGEIconLibrary.DRAW_CUSTOM_POLYGON_TOOL_SELECTED_ICON;
		case DrawConnectorTool:
			return FGEIconLibrary.DRAW_LINE_TOOL_SELECTED_ICON;
		case DrawTextTool:
			return FGEIconLibrary.DRAW_TEXT_TOOL_SELECTED_ICON;
		default:
			logger.warning("Unexpected tool: " + tool);
			return null;
		}

	}

	public Icon getIconFor(EditorToolOption option) {
		if (option instanceof DrawShapeToolOption) {
			switch ((DrawShapeToolOption) option) {
			case DrawRectangle:
				return FGEIconLibrary.DRAW_RECTANGLE_TOOL_ICON;
			case DrawOval:
				return FGEIconLibrary.DRAW_OVAL_TOOL_ICON;
			default:
				logger.warning("Unexpected option: " + option);
				return FGEIconLibrary.DRAW_RECTANGLE_TOOL_ICON;
			}
		} else if (option instanceof DrawCustomShapeToolOption) {
			switch ((DrawCustomShapeToolOption) option) {
			case DrawPolygon:
				return FGEIconLibrary.DRAW_CUSTOM_POLYGON_TOOL_ICON;
			case DrawClosedCurve:
				return FGEIconLibrary.DRAW_CLOSED_CURVE_TOOL_ICON;
			case DrawComplexShape:
				return FGEIconLibrary.DRAW_CLOSED_CURVE_TOOL_ICON;
			default:
				logger.warning("Unexpected option: " + option);
				return FGEIconLibrary.DRAW_CLOSED_CURVE_TOOL_ICON;
			}
		} else if (option instanceof DrawConnectorToolOption) {
			switch ((DrawConnectorToolOption) option) {
			case DrawLine:
				return FGEIconLibrary.DRAW_LINE_TOOL_ICON;
			case DrawCurve:
				return FGEIconLibrary.DRAW_CURVE_TOOL_ICON;
			case DrawRectPolylin:
				return FGEIconLibrary.DRAW_RECT_POLYLIN_TOOL_ICON;
			case DrawCurvedPolylin:
				return FGEIconLibrary.DRAW_CURVED_POLYLIN_TOOL_ICON;
			default:
				logger.warning("Unexpected option: " + option);
				return FGEIconLibrary.DRAW_LINE_TOOL_ICON;
			}
		}
		logger.warning("Unexpected option: " + option);
		return null;
	}

	public Icon getSelectedIconFor(EditorToolOption option) {
		if (option instanceof DrawShapeToolOption) {
			switch ((DrawShapeToolOption) option) {
			case DrawRectangle:
				return FGEIconLibrary.DRAW_RECTANGLE_TOOL_SELECTED_ICON;
			case DrawOval:
				return FGEIconLibrary.DRAW_OVAL_TOOL_SELECTED_ICON;
			default:
				logger.warning("Unexpected option: " + option);
				return FGEIconLibrary.DRAW_RECTANGLE_TOOL_SELECTED_ICON;
			}
		} else if (option instanceof DrawCustomShapeToolOption) {
			switch ((DrawCustomShapeToolOption) option) {
			case DrawPolygon:
				return FGEIconLibrary.DRAW_CUSTOM_POLYGON_TOOL_SELECTED_ICON;
			case DrawClosedCurve:
				return FGEIconLibrary.DRAW_CLOSED_CURVE_TOOL_SELECTED_ICON;
			case DrawComplexShape:
				return FGEIconLibrary.DRAW_CLOSED_CURVE_TOOL_SELECTED_ICON;
			default:
				logger.warning("Unexpected option: " + option);
				return FGEIconLibrary.DRAW_CLOSED_CURVE_TOOL_SELECTED_ICON;
			}
		} else if (option instanceof DrawConnectorToolOption) {
			switch ((DrawConnectorToolOption) option) {
			case DrawLine:
				return FGEIconLibrary.DRAW_LINE_TOOL_SELECTED_ICON;
			case DrawCurve:
				return FGEIconLibrary.DRAW_CURVE_TOOL_SELECTED_ICON;
			case DrawRectPolylin:
				return FGEIconLibrary.DRAW_RECT_POLYLIN_TOOL_SELECTED_ICON;
			case DrawCurvedPolylin:
				return FGEIconLibrary.DRAW_CURVED_POLYLIN_TOOL_SELECTED_ICON;
			default:
				logger.warning("Unexpected option: " + option);
				return FGEIconLibrary.DRAW_LINE_TOOL_SELECTED_ICON;
			}
		}
		logger.warning("Unexpected option: " + option);
		return null;
	}

}