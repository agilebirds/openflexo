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


    public FIBBrowserCellEditor(JTree tree, FIBBrowserCellRenderer renderer)
    {
        super(tree, renderer);
    }

    @Override
	public Component getTreeCellEditorComponent(JTree arg0, Object element, boolean _selected, boolean expanded, boolean leaf, int row)
    {
        String editingName = ((BrowserCell)element).getBrowserElementType().getEditableLabelFor(((BrowserCell)element).getRepresentedObject());
        Icon customIcon = getRenderer().getIcon(((BrowserCell)element).getRepresentedObject());
        if (customIcon != null) {
        	getRenderer().setClosedIcon(customIcon);
        	getRenderer().setOpenIcon(customIcon);
        	getRenderer().setLeafIcon(customIcon);
         }
        Component returned = super.getTreeCellEditorComponent(arg0, editingName, _selected, expanded, leaf, row);
        return returned;
    }

    @Override
	public boolean isCellEditable(EventObject event)
    {
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

    protected FIBBrowserCellRenderer getRenderer()
    {
        return (FIBBrowserCellRenderer)renderer;
    }
    
    /**
     * Creates the container to manage placement of
     * <code>editingComponent</code>.
     */
  /*  @Override
	protected Container createContainer()
    {
        return new CustomEditorContainer();
    }*/

   /* protected Component getEditingComponent()
    {
        return editingComponent;
    }

    protected int getOffset()
    {
        return offset;
    }

    
*/
    /**
     * Container responsible for placing the <code>editingComponent</code>.
     */
   /* public class CustomEditorContainer extends Container
    {
          public CustomEditorContainer()
        {
            setLayout(null);
        }

       @Override
		public void paint(Graphics g)
        {
            Dimension size = getSize();

            // Then the icon.
            if (_customIcon != null) {
                int yLoc = Math.max(0, (getSize().height - _customIcon.getIconHeight()) / 2);

                _customIcon.paintIcon(this, g, 0, yLoc);
            }

            // Border selection color
            Color background = getBorderSelectionColor();
            if (background != null) {
                g.setColor(background);
                g.drawRect(0, 0, size.width - 1, size.height - 1);
            }
            super.paint(g);
        }

        @Override
		public void doLayout()
        {
            if (getEditingComponent() != null) {
                Dimension cSize = getSize();

                getEditingComponent().getPreferredSize();
                getEditingComponent().setLocation(getOffset(), 0);
                getEditingComponent().setBounds(getOffset(), 0, cSize.width - getOffset(), cSize.height);
            }
        }

  	public Dimension getPreferredSize()
        {
            if (getEditingComponent() != null) {
                Dimension pSize = getEditingComponent().getPreferredSize();

                pSize.width += getOffset() + 5;

                Dimension rSize = (getRenderer() != null) ? getRenderer().getPreferredSize() : null;

                if (rSize != null)
                    pSize.height = Math.max(pSize.height, rSize.height);
                if (_customIcon != null)
                    pSize.height = Math.max(pSize.height, _customIcon.getIconHeight());

                // Make sure width is at least 100.
                pSize.width = Math.max(pSize.width, 100);
                return pSize;
            }
            return new Dimension(0, 0);
        }
    }
*/
}