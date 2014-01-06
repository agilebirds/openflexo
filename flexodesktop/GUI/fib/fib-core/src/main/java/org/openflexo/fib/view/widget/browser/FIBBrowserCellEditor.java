/**
 * 
 */
package org.openflexo.fib.view.widget.browser;

import java.awt.Component;
import java.awt.event.MouseEvent;
import java.util.EventObject;

import javax.swing.Icon;
import javax.swing.JTree;
import javax.swing.tree.DefaultTreeCellEditor;
import javax.swing.tree.TreePath;

import org.openflexo.fib.view.widget.browser.FIBBrowserModel.BrowserCell;

public class FIBBrowserCellEditor extends DefaultTreeCellEditor {

	public FIBBrowserCellEditor(JTree tree, FIBBrowserCellRenderer renderer) {
		super(tree, renderer);
	}

	@Override
	public Component getTreeCellEditorComponent(JTree arg0, Object element, boolean _selected, boolean expanded, boolean leaf, int row) {
		String editingName = ((BrowserCell) element).getBrowserElementType().getEditableLabelFor(
				((BrowserCell) element).getRepresentedObject());
		Icon customIcon = getRenderer().getIcon(((BrowserCell) element).getRepresentedObject());
		if (customIcon != null) {
			getRenderer().setClosedIcon(customIcon);
			getRenderer().setOpenIcon(customIcon);
			getRenderer().setLeafIcon(customIcon);
		}
		Component returned = super.getTreeCellEditorComponent(arg0, editingName, _selected, expanded, leaf, row);
		return returned;
	}

	@Override
	public boolean isCellEditable(EventObject event) {
		if (super.isCellEditable(event)) {
			if (event != null) {
				if (event.getSource() instanceof JTree) {
					setTree((JTree) event.getSource());
					if (event instanceof MouseEvent) {
						TreePath path = tree.getPathForLocation(((MouseEvent) event).getX(), ((MouseEvent) event).getY());
						if (path != null) {
							BrowserCell cell = (BrowserCell) path.getLastPathComponent();
							return cell.getBrowserElementType().isLabelEditable();
						}
					}
				}
			}
		}
		return false;
	}

	protected FIBBrowserCellRenderer getRenderer() {
		return (FIBBrowserCellRenderer) renderer;
	}

}