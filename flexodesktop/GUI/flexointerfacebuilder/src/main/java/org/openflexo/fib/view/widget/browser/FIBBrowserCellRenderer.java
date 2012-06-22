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

import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.widget.FIBBrowserWidget;
import org.openflexo.fib.view.widget.browser.FIBBrowserModel.BrowserCell;

public class FIBBrowserCellRenderer extends DefaultTreeCellRenderer {

	private final FIBBrowserWidget widget;

	public FIBBrowserCellRenderer(FIBBrowserWidget widget) {
		super();
		this.widget = widget;
		setTextSelectionColor(widget.getBrowser().getTextSelectionColor());
		setTextNonSelectionColor(widget.getBrowser().getTextNonSelectionColor());
		setBackgroundSelectionColor(widget.getBrowser().getBackgroundSelectionColor());
		setBackgroundNonSelectionColor(widget.getBrowser().getBackgroundNonSelectionColor());
		setBorderSelectionColor(widget.getBrowser().getBorderSelectionColor());
		setFont(widget.getFont());
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
					setTextSelectionColor(widget.getBrowser().getTextSelectionColor());
					setBackgroundSelectionColor(widget.getBrowser().getBackgroundSelectionColor());
				} else {
					setTextSelectionColor(widget.getBrowser().getTextNonSelectionColor());
					setBackgroundSelectionColor(widget.getBrowser().getBackgroundSecondarySelectionColor());
				}
			}

			if (!widget.isEnabled()) {
				setTextNonSelectionColor(FIBComponent.DISABLED_COLOR);
			} else {
				if (isEnabled(representedObject)) {
					setTextNonSelectionColor(widget.getBrowser().getTextNonSelectionColor());
				} else {
					setTextNonSelectionColor(FIBComponent.DISABLED_COLOR);
				}
			}

			JLabel returned = (JLabel) super.getTreeCellRendererComponent(tree, value, sel, expanded, leaf, row, hasFocus);

			returned.setFont(getFont(representedObject));
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