/**
 * 
 */
package org.openflexo.fib.view.widget.browser;

import java.awt.Component;
import java.awt.Font;

import javax.swing.Icon;
import javax.swing.JLabel;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellRenderer;

import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.fib.view.widget.browser.FIBBrowserModel.BrowserCell;

public class FIBBrowserCellRenderer extends DefaultTreeCellRenderer {

	private final FIBBrowserWidget widget;

	public FIBBrowserCellRenderer(FIBBrowserWidget widget) {
		super();
		this.widget = widget;
		if (widget.getBrowser().getTextSelectionColor() != null) {
			setTextSelectionColor(widget.getBrowser().getTextSelectionColor());
		}
		if (widget.getBrowser().getTextNonSelectionColor() != null) {
			setTextNonSelectionColor(widget.getBrowser().getTextNonSelectionColor());
		}
		if (widget.getBrowser().getBackgroundSelectionColor() != null) {
			setBackgroundSelectionColor(widget.getBrowser().getBackgroundSelectionColor());
		}
		if (widget.getBrowser().getBackgroundNonSelectionColor() != null) {
			setBackgroundNonSelectionColor(widget.getBrowser().getBackgroundNonSelectionColor());
		}
		if (widget.getBrowser().getBorderSelectionColor() != null) {
			setBorderSelectionColor(widget.getBrowser().getBorderSelectionColor());
		}
		if (widget.getFont() != null) {
			setFont(widget.getFont());
		}
	}

	/**
	 * 
	 * Returns the cell renderer.
	 * 
	 * @param table
	 *            the <code>JTable</code>
	 * @param value
	 *            the value to assign to the cell at <code>[row, column]</code>
	 * @param isSelected
	 *            true if cell is selected
	 * @param hasFocus
	 *            true if cell has focus
	 * @param row
	 *            the row of the cell to render
	 * @param column
	 *            the column of the cell to render
	 * @return the default table cell renderer
	 */
	@Override
	public Component getTreeCellRendererComponent(JTree tree, Object value, boolean sel, boolean expanded, boolean leaf, int row,
			boolean hasFocus) {
		if (value instanceof BrowserCell) {
			Object representedObject = ((BrowserCell) value).getRepresentedObject();

			if (sel) {
				if (widget.isLastFocusedSelectable()) {
					if (widget.getBrowser().getTextSelectionColor() != null) {
						setTextSelectionColor(widget.getBrowser().getTextSelectionColor());
					}
					if (widget.getBrowser().getBackgroundSelectionColor() != null) {
						setBackgroundSelectionColor(widget.getBrowser().getBackgroundSelectionColor());
					}
				} else {
					if (widget.getBrowser().getTextNonSelectionColor() != null) {
						setTextSelectionColor(widget.getBrowser().getTextNonSelectionColor());
					}
					if (widget.getBrowser().getBackgroundSecondarySelectionColor() != null) {
						setBackgroundSelectionColor(widget.getBrowser().getBackgroundSecondarySelectionColor());
					}
				}
			}

			JLabel returned = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);
			if (widget.isEnabled()) {
				if (isEnabled(representedObject)) {
					if (widget.getBrowser().getTextNonSelectionColor() != null) {
						setTextNonSelectionColor(widget.getBrowser().getTextNonSelectionColor());
					}
				} else {
					setEnabled(false);
				}
			}

			Font font = getFont(representedObject);
			if (font != null) {
				returned.setFont(font);
			}
			returned.setText(getLabel(representedObject));
			returned.setIcon(getIcon(representedObject));
			returned.setToolTipText(getTooltip(representedObject));
			return returned;

		}

		return super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

	}

	/*  protected Color getCellBackground(JTree tree, boolean isSelected, boolean hasFocus)
	{
	  	if (isSelected) {
	  		if (widget.isLastFocusedSelectable()) return MAIN_SELECTION_COLOR;
	  		else return SECONDARY_SELECTION_COLOR;
	  	}
	  	return tree.getBackground();
	  }*/

	private FIBBrowserElementType getElementType(Object object) {
		return widget.getBrowserModel().elementTypeForClass(object.getClass());
	}

	protected String getLabel(Object object) {
		FIBBrowserElementType elementType = getElementType(object);
		if (elementType != null) {
			return elementType.getLabelFor(object);
		}
		return object.toString();
	}

	protected Icon getIcon(Object object) {
		FIBBrowserElementType elementType = getElementType(object);
		if (elementType != null) {
			return elementType.getIconFor(object);
		}
		return null;
	}

	protected String getTooltip(Object object) {
		FIBBrowserElementType elementType = getElementType(object);
		if (elementType != null) {
			return elementType.getTooltipFor(object);
		}
		return object.toString();
	}

	protected boolean isEnabled(Object object) {
		FIBBrowserElementType elementType = getElementType(object);
		if (elementType != null) {
			// System.out.println("Object "+object+" isEnabled="+elementType.isEnabled(object));
			return elementType.isEnabled(object);
		}
		return true;
	}

	protected Font getFont(Object object) {
		FIBBrowserElementType elementType = getElementType(object);
		if (elementType != null) {
			return elementType.getFont(object);
		}
		return widget.getFont();
	}

	/**
	 * Overrides updateUI
	 * 
	 * @see javax.swing.JLabel#updateUI()
	 */
	/* @Override
	 public void updateUI()
	 {
	     super.updateUI();
	     // Fix for TreeCellRenderer
	     setLeafIcon(UIManager.getIcon("Tree.leafIcon"));
	     setClosedIcon(UIManager.getIcon("Tree.closedIcon"));
	     setOpenIcon(UIManager.getIcon("Tree.openIcon"));

	     setTextSelectionColor(UIManager.getColor("Tree.selectionForeground"));
	     setTextNonSelectionColor(UIManager.getColor("Tree.textForeground"));
	     setBackgroundSelectionColor(UIManager.getColor("Tree.selectionBackground"));
	     setBackgroundNonSelectionColor(UIManager.getColor("Tree.textBackground"));
	     setBorderSelectionColor(UIManager.getColor("Tree.selectionBorderColor"));
	 }*/

}