/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Container;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.FocusTraversalPolicy;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.IllegalComponentStateException;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.Window;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentEvent;
import java.awt.event.ComponentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.util.EventObject;
import java.util.List;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.FocusManager;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.toolbox.ToolBox;

/**
 * Abstract widget allowing to edit a complex object with a popup
 * 
 * @author sguerin
 * 
 */
public abstract class CustomPopup<T> extends JPanel implements ActionListener, MouseListener {

	protected static final Logger logger = Logger.getLogger(CustomPopup.class.getPackage().getName());

	public static final CustomPopupConfiguration configuration = new CustomPopupConfiguration();

	private static final int BULLETS = 3;
	private static final int BULLET_SPACING = 2;
	private static final int BULLET_SIZE = 3;

	public T _editedObject;

	protected JButton _downButton;

	public JComponent _frontComponent;

	private final List<ApplyCancelListener> applyCancelListener;

	private int posX;

	private int posY;

	public CustomJPopupMenu _popup;

	private ResizablePanel _customPanel;

	public interface ApplyCancelListener {
		public void fireApplyPerformed();

		public void fireCancelPerformed();
	}

	@Override
	public void setEnabled(boolean enabled) {
		super.setEnabled(enabled);
		if (!enabled && popupIsShown()) {
			closePopup();
		}
		if (_frontComponent != null) {
			_frontComponent.setEnabled(enabled);
		}
		if (_downButton != null) {
			_downButton.setEnabled(enabled);
		}
	}

	protected abstract JComponent buildFrontComponent();

	public CustomPopup(T editedObject) {
		super();
		_editedObject = editedObject;
		setLayout(new BorderLayout());
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			_downButton = new JButton(UtilsIconLibrary.CUSTOM_POPUP_BUTTON);
		} else {
			_downButton = new JButton(UtilsIconLibrary.CUSTOM_POPUP_DOWN);
			_downButton.setDisabledIcon(UtilsIconLibrary.CUSTOM_POPUP_DOWN_DISABLED);
		}
		_downButton.addActionListener(this);
		add(_downButton, BorderLayout.WEST);
		/*Border border = getDownButtonBorder();
		if (border != null) {
			_downButton.setBorder(border);
		}*/
		setOpaque(false);
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			setBorder(BorderFactory.createEmptyBorder(3, 5, 3, 5));
		}
		_downButton.setBorder(BorderFactory.createEmptyBorder());
		_frontComponent = buildFrontComponent();
		add(_frontComponent, BorderLayout.CENTER);

		applyCancelListener = new Vector<ApplyCancelListener>();
		setFocusTraversalPolicy(new FocusTraversalPolicy() {

			@Override
			public Component getComponentAfter(Container arg0, Component arg1) {
				return null;
			}

			@Override
			public Component getComponentBefore(Container arg0, Component arg1) {
				return null;
			}

			@Override
			public Component getDefaultComponent(Container arg0) {
				return _frontComponent;
			}

			@Override
			public Component getFirstComponent(Container arg0) {
				return _frontComponent;
			}

			@Override
			public Component getLastComponent(Container arg0) {
				return _frontComponent;
			}

		});
	}

	@Override
	public void setBackground(Color bg) {
		super.setBackground(bg);
		if (_frontComponent != null) {
			_frontComponent.setBackground(bg);
		}
	}

	@Override
	public void setForeground(Color fg) {
		super.setForeground(fg);
		if (_frontComponent != null) {
			_frontComponent.setForeground(fg);
		}
	}

	public JComponent getFrontComponent() {
		return _frontComponent;
	}

	@Override
	public void setFont(Font aFont) {
		super.setFont(aFont);
		if (_frontComponent != null) {
			_frontComponent.setFont(aFont);
		}
	}

	public static abstract class ResizablePanel extends JPanel {
		public abstract Dimension getDefaultSize();

	}

	protected void deleteCustomPanel() {
		_customPanel = null;
	}

	public JComponent getDownButton() {
		return _downButton;
	}

	public ResizablePanel getCustomPanel() {
		if (_customPanel == null) {
			_customPanel = createCustomPanel(getEditedObject());
			_customPanel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
		}
		return _customPanel;
	}

	protected abstract ResizablePanel createCustomPanel(T editedObject);

	private void makePopup() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("makePopup()");
		}
		Point p = new Point();
		try {
			p = this.getLocationOnScreen();
		} catch (IllegalComponentStateException e) {
			e.printStackTrace();
			p = getLocation();
		}
		// Dimension dim = Toolkit.getDefaultToolkit().getScreenSize();
		// Avoid popup behind dock
		// dim.height = dim.height - 50;
		posX = p.x;/* +getWidth()-getCustomPanel().getWidth(); */
		posY = p.y + getHeight() - 1;
		int newWidth = getCustomPanel().getDefaultSize().width;
		int newHeight = getCustomPanel().getDefaultSize().height;
		/*
		 * if (posX + getCustomPanel().getDefaultSize().width > dim.width) { posX = dim.width - getCustomPanel().getDefaultSize().width -
		 * 20; } if (posY + getCustomPanel().getDefaultSize().height > dim.height) { newHeight = dim.height - posY; }
		 */
		getCustomPanel().setPreferredSize(new Dimension(newWidth, newHeight));
		// _customPopup = popupFactory.getPopup(this, getCustomPanel(), posX,
		// posY);
		_popup = new CustomJPopupMenu(this);
		Rectangle popupRectangle = new Rectangle(posX, posY, newWidth, newHeight);
		Rectangle thisRectangle = new Rectangle();
		thisRectangle.setLocation(p);
		thisRectangle.setSize(getSize());

		Rectangle union = thisRectangle.union(popupRectangle);
		popupLiveArea = union;
	}

	protected class CustomJPopupMenu extends JDialog {

		protected class ParentPopupMoveListener implements ComponentListener {
			private Point parentPosition;

			public ParentPopupMoveListener() {
				if (CustomJPopupMenu.this.getOwner().isVisible()) {
					parentPosition = CustomJPopupMenu.this.getOwner().getLocationOnScreen();
				}
			}

			@Override
			public void componentHidden(ComponentEvent e) {
			}

			@Override
			public void componentMoved(ComponentEvent e) {
				updatePopupLocation();
			}

			@Override
			public void componentResized(ComponentEvent e) {
				updatePopupLocation();
			}

			@Override
			public void componentShown(ComponentEvent e) {
				parentPosition = CustomJPopupMenu.this.getOwner().getLocationOnScreen();
			}

			private void updatePopupLocation() {
				Point newPosition = getLocation();
				newPosition.x += CustomJPopupMenu.this.getOwner().getLocationOnScreen().x - parentPosition.x;
				newPosition.y += CustomJPopupMenu.this.getOwner().getLocationOnScreen().y - parentPosition.y;
				CustomJPopupMenu.this.setLocation(newPosition);
				parentPosition = CustomJPopupMenu.this.getOwner().getLocationOnScreen();
			}
		}

		protected List<CustomJPopupMenu> _childs;

		protected boolean _popupIsShown = false;

		private ParentPopupMoveListener parentListener;

		public CustomJPopupMenu(CustomPopup<?> invoker) {
			super((Window) SwingUtilities.getAncestorOfClass(Window.class, invoker));
			_childs = new Vector<CustomJPopupMenu>();
			parentListener = new ParentPopupMoveListener();
			setUndecorated(true);
			getRootPane().setBorder(BorderFactory.createLineBorder(Color.BLACK));
			getContentPane().add(invoker.getCustomPanel());

			CustomJPopupMenu parentPopupMenu = getParentPopupMenu();
			if (parentPopupMenu != null) {
				parentPopupMenu._childs.add(this);
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("This popup is " + this.hashCode() + " Parent popup is "
						+ (parentPopupMenu == null ? "null" : parentPopupMenu.hashCode()));
				// logger.info("Made new popup: "+Integer.toHexString(hashCode())+(getParentPopupMenu()!=null?" with parent: "+Integer.toHexString(getParentPopupMenu().hashCode()):""));
			}
		}

		private void registerParentListener() {
			if (getOwner() != null) {
				getOwner().addComponentListener(parentListener);
			}
		}

		private void unregisterParentListener() {
			if (getOwner() != null) {
				getOwner().removeComponentListener(parentListener);
			}
		}

		protected CustomPopup<?> getCustomPopup() {
			return CustomPopup.this;
		}

		public boolean isChildOf(Window w) {
			return w instanceof CustomPopup.CustomJPopupMenu && ((CustomPopup.CustomJPopupMenu) w).isParentOf(this);
		}

		public boolean isParentOf(Window w) {
			return w != null && (w.getOwner() == this || isParentOf(w.getOwner()));
		}

		@Override
		public void paint(Graphics g) {
			super.paint(g);
			int y = getSize().height - BULLET_SPACING - 5;
			for (int i = 0; i < 3; i++) {
				for (int j = i; j < 3; j++) {
					int x = getSize().width - BULLETS * BULLET_SIZE - (BULLETS - 1) * BULLET_SPACING + j * (BULLET_SIZE + BULLET_SPACING)
							- 5;
					g.setColor(Color.LIGHT_GRAY);
					g.fillRect(x, y, BULLET_SIZE, BULLET_SIZE);
				}
				y -= BULLET_SIZE + BULLET_SPACING;
			}
		}

		@Override
		public void setVisible(boolean aBoolean) {
			// logger.info((aBoolean?"Show popup":"Hide popup")+" "+Integer.toHexString(hashCode()));

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("setVisible " + aBoolean + " for " + this.hashCode());
			}
			if (aBoolean) {
				_requestVisibility = true;
			} else {
				for (CustomJPopupMenu child : _childs) {
					child.setVisible(false);
				}
				for (CustomJPopupMenu child : _childs) {
					if (child.requestVisibility()) {
						if (logger.isLoggable(Level.FINE)) {
							logger.fine("setVisible " + aBoolean + " forget it");
						}
						return;
					}
				}
			}
			super.setVisible(aBoolean);
			_popupIsShown = aBoolean;
			if (aBoolean) {
				registerParentListener();
				_requestVisibility = false;
			} else {
				unregisterParentListener();
			}
		}

		private boolean _requestVisibility;

		public boolean requestVisibility() {
			return _requestVisibility;
		}

		public CustomJPopupMenu getParentPopupMenu() {
			if (getOwner() instanceof CustomPopup.CustomJPopupMenu) {
				return (CustomJPopupMenu) getOwner();
			}
			return null;
		}

	}

	private Rectangle popupLiveArea;

	// This actionListener handles the next/prev month buttons.
	@Override
	public void actionPerformed(ActionEvent e) {
		onEvent(e);
	}

	private void onEvent(EventObject e) {
		if (e.getSource() == _downButton) {
			if (!popupIsShown()) {
				openPopup();
			} else {
				closePopup();
			}
		} else {
			additionalActions();
		}
	}

	private boolean closersAdded = false;

	// Override this to add functionality on down button click
	public void additionalActions() {
	}

	private MyWindowAdapter inspectorWindowListener;

	private class MyWindowAdapter extends WindowAdapter {
		private final Window parentWindow;

		public MyWindowAdapter(Window parent) {
			this.parentWindow = parent;
		}

		@Override
		public void windowDeactivated(final WindowEvent e) {
			// If this window is deactivated, it means that another window has been activated
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					if (_popup == null) {
						return;
					}
					Window oppositeWindow = e.getOppositeWindow();
					if (!(oppositeWindow instanceof CustomPopup.CustomJPopupMenu) && oppositeWindow != null
							&& oppositeWindow.getOwner() instanceof CustomPopup.CustomJPopupMenu) {
						oppositeWindow = oppositeWindow.getOwner();
					}
					if (oppositeWindow != _popup) {
						if (_popup.isChildOf(oppositeWindow)) {
							CustomPopup.CustomJPopupMenu w = _popup;
							while (w != null && w != oppositeWindow) {
								w.getCustomPopup().pointerLeavesPopup();
								w = w.getParentPopupMenu();
							}
						} else if (oppositeWindow != parentWindow || FocusManager.getCurrentManager().getFocusOwner() != null
								&& !_frontComponent.hasFocus()) {
							// This test is used to detect the case of the lost of focus is performed
							// Because a child popup gained the focus: in this case, nothing should be performed
							if (!(oppositeWindow instanceof CustomPopup.CustomJPopupMenu)
									|| !((CustomPopup.CustomJPopupMenu) oppositeWindow).isChildOf(_popup)) {
								pointerLeavesPopup();
							}
						}
					}
				}
			});
		}

		public void startListening() {
			_popup.addWindowListener(this);
			parentWindow.addWindowListener(this);
		}

		public void stopListening() {
			_popup.removeWindowListener(this);
			parentWindow.removeWindowListener(this);
		}
	}

	/**
	 * Add mouse listeners to each component of the root container c, except this button, and the calendar popup, because mouse clicks in
	 * them are not supposed to close the popup.
	 * 
	 * @param c
	 *            the root container
	 */
	private void addPopupClosers(Container c) {
		if (c == getWindow(this) && c != null) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("addPopupClosers");
			}
			inspectorWindowListener = new MyWindowAdapter((Window) c);
			// inspectorWindowListener.startListening();
		}
		if (c != this && c != _customPanel && c != null) {
			c.addMouseListener(this);
			for (int i = 0; i < c.getComponents().length; i++) {
				addPopupClosers((Container) c.getComponents()[i]);
			}
		}
	}

	/**
	 * Removes mouse listeners to each component of the root container c, except this button, and the calendar popup, because mouse clicks
	 * in them are not supposed to close the popup.
	 * 
	 * @param c
	 *            the root container
	 */
	private void removePopupClosers(Container c) {
		if (c == getWindow(this)) {
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("removePopupClosers");
			}
			if (inspectorWindowListener != null) {
				inspectorWindowListener.stopListening();
			}
			inspectorWindowListener = null;
		}
		if (c != this && c != _customPanel && c != null) {
			c.removeMouseListener(this);
			for (int i = 0; i < c.getComponents().length; i++) {
				removePopupClosers((Container) c.getComponents()[i]);
			}
		}
	}

	/**
	 * Copied directly from BasicPopupMenuUI - PK 06-08-04
	 * 
	 * @param c
	 *            componenet of which we want to find the owning window
	 * @return the window that is contins after plenty of leves the component c
	 */
	private Window getWindow(Component c) {
		return SwingUtilities.getWindowAncestor(c);
	}

	protected void deletePopup() {
		// _customPopup = null;
		_popup = null;
		_customPanel = null;
	}

	public boolean popupIsShown() {
		if (_popup != null) {
			return _popup._popupIsShown;
		}
		return false;
	}

	protected void openPopup() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("openPopup()");
		}
		makePopup();

		if (!closersAdded) // only do this once.
		{
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("CALLED addPopupClosers on " + getWindow(this));
			}
			addPopupClosers(getWindow(this));
			closersAdded = true;
		}
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (!_frontComponent.hasFocus()) {
					_frontComponent.grabFocus();
				}
				SwingUtilities.invokeLater(new Runnable() {

					@Override
					public void run() {
						if (inspectorWindowListener != null) {
							inspectorWindowListener.startListening();
						}

					}
				});
			}
		});
		if (isShowing()) {
			Point p = this.getLocationOnScreen();
			Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
			// Multiple screen management
			if (GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length > 1) {
				screenSize = new Dimension(0, 0);
				boolean found = false;
				for (int i = 0; !found && i < GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices().length; i++) {
					GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getScreenDevices()[i];
					screenSize.width = Math.max(screenSize.width, gd.getDefaultConfiguration().getBounds().x
							+ gd.getDefaultConfiguration().getBounds().width);
					screenSize.height = Math.max(screenSize.height, gd.getDefaultConfiguration().getBounds().y
							+ gd.getDefaultConfiguration().getBounds().height);
					if (gd.getDefaultConfiguration().getBounds().contains(p)) {
						found = true;
					}
				}
			}
			Point position = new Point(p.x, p.y + getHeight());
			if (position.x + getCustomPanel().getDefaultSize().width > screenSize.width) {
				position.x = screenSize.width - getCustomPanel().getDefaultSize().width;
			}
			if (position.y + getCustomPanel().getDefaultSize().height > screenSize.height) {
				position.y = screenSize.height - getCustomPanel().getDefaultSize().height;
			}
			_popup.setLocation(position);
			_popup.pack();
			_popup.setVisible(true);
			if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
				_downButton.setIcon(UtilsIconLibrary.CUSTOM_POPUP_OPEN_BUTTON);
			}

			MouseAdapter mouseListener = new MouseAdapter() {

				private Point previous;

				@Override
				public void mousePressed(MouseEvent e) {
					if (getResizeRectangle().contains(e.getPoint())) {
						previous = e.getLocationOnScreen();
					}
				}

				@Override
				public void mouseDragged(MouseEvent e) {
					if (previous != null) {
						Dimension size = getCustomPanel().getSize();
						Dimension aDimension = new Dimension(size.width + e.getLocationOnScreen().x - previous.x, size.height
								+ e.getLocationOnScreen().y - previous.y);
						getCustomPanel().setPreferredSize(aDimension);
						_popup.pack();
						previous = e.getLocationOnScreen();
					}
				}

				@Override
				public void mouseMoved(MouseEvent e) {
					if (_popup == null) {
						return;
					}
					if (getResizeRectangle().contains(e.getPoint())) {
						_popup.setCursor(Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR));
					} else {
						_popup.setCursor(Cursor.getDefaultCursor());
					}
				}

				@Override
				public void mouseReleased(MouseEvent e) {
					super.mouseReleased(e);
					previous = null;
				}

				public Rectangle getResizeRectangle() {
					if (_popup == null) {
						return new Rectangle();
					}
					Rectangle r = _popup.getBounds();
					int size = 3 * (BULLET_SIZE + BULLET_SPACING);
					r.x = r.width - size;
					r.y = r.height - size;
					r.width = size;
					r.height = size;
					return r;
				}
			};
			_popup.addMouseListener(mouseListener);
			_popup.addMouseMotionListener(mouseListener);
		} else {
			logger.warning("Illegal component state: component is not showing on screen");
			// _popup.show(this, 0, 0);
		}
		// _customPopup.show();
		// _popupIsShown = true;
	}

	public void closePopup(boolean notifyObjectChanged) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("closePopup()");
		}
		if (_popup == null) {
			return;
		}
		_popup.setVisible(false);
		if (ToolBox.getPLATFORM() != ToolBox.MACOS) {
			_downButton.setIcon(UtilsIconLibrary.CUSTOM_POPUP_BUTTON);
		}
		if (notifyObjectChanged) {
			fireEditedObjectChanged();
		}
		if (closersAdded) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("CALLED removePopupClosers on " + getWindow(this));
			}
			removePopupClosers(getWindow(this));
			closersAdded = false;
		}
	}

	public void closePopup() {
		closePopup(true);
	}

	public T getEditedObject() {
		return _editedObject;
	}

	// WARNING: this method uses the equals(Object) method to see if a change is required or not. Therefore, if the edited object type
	// overrides the equals method, some objects that are different may not be swapped and cause very unpredictable behaviour. The
	// workaround for this is to clone the value when setting on the model. See bug 1004363
	public void setEditedObject(T object) {
		if (_editedObject == null || !_editedObject.equals(object)) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("CustomPopup setEditedObject: " + object);
			}
			_editedObject = object;
			fireEditedObjectChanged();
		}
	}

	public void setRevertValue(T oldValue) {
		// Not implemented here, implement in sub-classes
	}

	public void fireEditedObjectChanged() {
		updateCustomPanel(getEditedObject());
	}

	public abstract void updateCustomPanel(T editedObject);

	@Override
	public void mouseEntered(MouseEvent e) {
		if (e.getSource() instanceof Component && configuration.getCloseWhenPointerLeavesPopup()) {
			Component leftComponent = (Component) e.getSource();
			while (leftComponent != null) {
				if (leftComponent == CustomPopup.this) {
					return;
				}
				leftComponent = leftComponent.getParent();
			}
			leftComponent = (Component) e.getSource();
			Point p = new Point(e.getPoint());
			SwingUtilities.convertPointToScreen(p, leftComponent);
			if (_customPanel != null) {
				if (!getCustomPanel().isAncestorOf(leftComponent) && !popupLiveArea.contains(p)) {
					pointerLeavesPopup();
				}
			}
		}
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getSource() instanceof Component && !configuration.getCloseWhenPointerLeavesPopup()) {
			Component leftComponent = (Component) e.getSource();
			while (leftComponent != null) {
				if (leftComponent == CustomPopup.this) {
					return;
				}
				leftComponent = leftComponent.getParent();
			}
			leftComponent = (Component) e.getSource();
			Point p = new Point(e.getPoint());
			SwingUtilities.convertPointToScreen(p, leftComponent);
			if (_customPanel != null) {
				if (!getCustomPanel().isAncestorOf(leftComponent) && !popupLiveArea.contains(p)) {
					pointerLeavesPopup();
				}
			}
		}
		onEvent(e);
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// interface
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// interface
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// interface
	}

	protected void pointerLeavesPopup() {
		closePopup();
	}

	public void addApplyCancelListener(ApplyCancelListener l) {
		applyCancelListener.add(l);
	}

	public void removeApplyCancelListener(ApplyCancelListener l) {
		applyCancelListener.remove(l);
	}

	public void apply() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("apply()");
		}
		notifyApplyPerformed();
	}

	public void notifyApplyPerformed() {
		for (ApplyCancelListener l : applyCancelListener) {
			l.fireApplyPerformed();
		}
	}

	public void cancel() {
		for (ApplyCancelListener l : applyCancelListener) {
			l.fireCancelPerformed();
		}
	}

	public static class CustomPopupConfiguration {
		private boolean closeWhenPointerLeavesPopup = false;

		public boolean getCloseWhenPointerLeavesPopup() {
			return closeWhenPointerLeavesPopup;
		}

		public void setCloseWhenPointerLeavesPopup(boolean closeWhenPointerLeavesPopup) {
			this.closeWhenPointerLeavesPopup = closeWhenPointerLeavesPopup;
		}
	}

	public String localizedForKey(String aKey) {
		return aKey;
	}
}
