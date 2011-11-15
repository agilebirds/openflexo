package org.openflexo.fib.editor.controller;

import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.Hashtable;
import java.util.logging.Logger;

import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPopupMenu;
import javax.swing.SwingUtilities;

import org.openflexo.fib.editor.controller.EditorAction.ActionAvailability;
import org.openflexo.fib.editor.controller.EditorAction.ActionPerformer;
import org.openflexo.fib.model.BorderLayoutConstraints;
import org.openflexo.fib.model.BorderLayoutConstraints.BorderLayoutLocation;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.model.FIBContainer;
import org.openflexo.fib.model.FIBModelObject;
import org.openflexo.fib.model.FIBPanel;
import org.openflexo.fib.model.FIBPanel.Border;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.logging.FlexoLogger;

public class ContextualMenu
{
	private static final Logger logger = FlexoLogger.getLogger(ContextualMenu.class.getPackage().getName());

	private FIBEditorController editorController;
	private Hashtable<EditorAction,PopupMenuItem> actions;
	private JPopupMenu menu;

	public ContextualMenu(FIBEditorController anEditorController)
	{
		this.editorController = anEditorController;
		actions = new Hashtable<EditorAction, PopupMenuItem>();
		menu = new JPopupMenu();

		addToActions(new EditorAction("Inspect", null, new ActionPerformer() {
			@Override
			public FIBModelObject performAction(FIBModelObject object) {
				editorController.getEditor().getInspector().setVisible(true);
				return object;
			}
		}, new ActionAvailability() {
			@Override
			public boolean isAvailableFor(FIBModelObject object) {
				return object != null;
			}
		}));
		addToActions(new EditorAction("Delete", FIBEditorIconLibrary.DELETE_ICON, new ActionPerformer() {
			@Override
			public FIBModelObject performAction(FIBModelObject object) {
				FIBComponent parent = ((FIBComponent)object).getParent();
				boolean deleteIt = JOptionPane.showConfirmDialog(editorController.getEditor().getFrame(),
						object+": really delete this component (undoable operation) ?", "information",
						JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.INFORMATION_MESSAGE) == JOptionPane.YES_OPTION;
				if (deleteIt) {
					logger.info("Removing object "+object);
					object.delete();
				}
				return parent;
			}
		}, new ActionAvailability() {
			@Override
			public boolean isAvailableFor(FIBModelObject object) {
				return object instanceof FIBComponent;
			}
		}));

		addToActions(new EditorAction("Wrap with panel", null, new ActionPerformer() {
			@Override
			public FIBModelObject performAction(FIBModelObject object) {
				FIBComponent component = (FIBComponent)object;
				FIBContainer parent = component.getParent();
				parent.removeFromSubComponents(component);
				FIBPanel newPanel = new FIBPanel();
				newPanel.setLayout(Layout.border);
				newPanel.setBorder(Border.titled);
				parent.addToSubComponents(newPanel, component.getConstraints());
				newPanel.addToSubComponents(component,new BorderLayoutConstraints(BorderLayoutLocation.center));
				return parent;
			}
		}, new ActionAvailability() {
			@Override
			public boolean isAvailableFor(FIBModelObject object) {
				return object instanceof FIBComponent
						&& ((FIBComponent)object).getParent() != null;
			}
		}));
	}

	public void addToActions(EditorAction action)
	{
		PopupMenuItem newMenuItem = new PopupMenuItem(action);
		menu.add(newMenuItem);
		actions.put(action,newMenuItem);
	}

	public void displayPopupMenu(FIBModelObject object,Component invoker, MouseEvent e)
	{
		for (EditorAction action : actions.keySet()) {
			PopupMenuItem menuItem = actions.get(action);
			menuItem.setObject(object);
		}
		menu.show(invoker, e.getPoint().x, e.getPoint().y);
	}

	class PopupMenuItem extends JMenuItem
	{
		private FIBModelObject object;
		private EditorAction action;

		public PopupMenuItem(EditorAction anAction)
		{
			super(anAction.getActionName(),anAction.getActionIcon());
			this.action = anAction;
			addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					FIBModelObject selectThis = action.getPerformer().performAction(object);
					if (selectThis instanceof FIBComponent) {
						editorController.setSelectedObject((FIBComponent)selectThis);
					}
				}
			});
		}

		public FIBModelObject getObject()
		{
			return object;
		}

		public void setObject(FIBModelObject object)
		{
			this.object = object;
			setVisible(action.getAvailability().isAvailableFor(object));
		}

	}



}
