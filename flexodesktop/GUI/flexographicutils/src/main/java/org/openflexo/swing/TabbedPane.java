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
import java.awt.GradientPaint;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.geom.Point2D;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.UIManager;
import javax.swing.border.Border;

import org.openflexo.icon.UtilsIconLibrary;

public class TabbedPane<J extends JComponent> {

	public static interface TabHeaderRenderer<J extends JComponent> {
		public Icon getTabHeaderIcon(J tab);

		public String getTabHeaderTitle(J tab);

		public String getTabHeaderTooltip(J tab);
	}

	public static interface TabListener<J extends JComponent> {
		public void tabSelected(@Nullable J tab);

		public void tabClosed(@Nonnull J tab);
	}

	private static final Color TRANSPARENT = new Color(1.0f, 1.0f, 1.0f, 0.3f);
	private static final Color LIGHT_BLUE = new Color(206, 231, 255, 255);

	private class TabHeaders extends JPanel implements ActionListener {

		private class TabHeader extends JPanel implements ActionListener, MouseListener {
			private class TabHeaderBorder implements Border {

				private static final int ROUNDED_CORNER_SIZE = 8;

				@Override
				public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
					((Graphics2D) g).setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
					g.setColor(Color.LIGHT_GRAY);
					g.drawLine(0, ROUNDED_CORNER_SIZE - 3, 0, height);
					g.drawLine(width - 1, ROUNDED_CORNER_SIZE - 3, width - 1, height);
					g.drawLine(ROUNDED_CORNER_SIZE - 3, 0, width - ROUNDED_CORNER_SIZE + 3, 0);
					g.drawArc(0, 0, ROUNDED_CORNER_SIZE, ROUNDED_CORNER_SIZE, 90, 90);
					g.drawArc(width - ROUNDED_CORNER_SIZE - 1, 0, ROUNDED_CORNER_SIZE, ROUNDED_CORNER_SIZE, 0, 90);
					if (selectedTab != tab) {
						g.drawLine(0, height - 1, width, height - 1);
					}
				}

				@Override
				public Insets getBorderInsets(Component c) {
					return new Insets(5, ROUNDED_CORNER_SIZE + 1, 5, ROUNDED_CORNER_SIZE + 1);
				}

				@Override
				public boolean isBorderOpaque() {
					return false;
				}

			}

			private final J tab;

			private JLabel title;
			private JButton close;

			private Color defaultBackground;

			private Color defaultForeground;

			private boolean hovered;

			public TabHeader(J tab) {
				super(new BorderLayout());
				setOpaque(false);
				this.tab = tab;
				add(title = new JLabel());
				title.setOpaque(false);
				title.setBorder(null);
				addMouseListener(this);
				title.addMouseListener(this);
				add(close = new JButton(UtilsIconLibrary.CLOSE_ICON), BorderLayout.EAST);
				close.setContentAreaFilled(false);
				close.setOpaque(false);
				close.setBorderPainted(false);
				close.setRolloverIcon(UtilsIconLibrary.CLOSE_HOVER_ICON);
				close.addActionListener(this);
				close.setFocusable(false);
				close.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 0));
				close.setPreferredSize(new Dimension(close.getIcon().getIconWidth() + close.getInsets().left + close.getInsets().right,
						close.getIcon().getIconHeight() + close.getInsets().top + close.getInsets().bottom));
				setBorder(new TabHeaderBorder());
				refresh();
			}

			public void refresh() {
				if (tabHeaderRenderer != null) {
					title.setIcon(tabHeaderRenderer.getTabHeaderIcon(tab));
					title.setText(tabHeaderRenderer.getTabHeaderTitle(tab));
					title.setToolTipText(tabHeaderRenderer.getTabHeaderTooltip(tab));
				} else {
					title.setIcon(null);
					title.setText(tab.getName());
					title.setToolTipText(tab.getToolTipText());
				}
				// setSize(getPreferredSize());
				TabHeaders.this.revalidate();
			}

			@Override
			protected void paintComponent(Graphics g) {
				if (tab == selectedTab) {
					((Graphics2D) g).setPaint(new GradientPaint(new Point2D.Double(0, 0), TRANSPARENT, new Point2D.Double(0, getHeight()),
							LIGHT_BLUE));
					g.fillRect(0, 0, getWidth(), getHeight());
				}
				super.paintComponent(g);
			}

			@Override
			protected void paintBorder(Graphics g) {
				if (getParent() == TabHeaders.this) {
					super.paintBorder(g);
				}
			}

			@Override
			public void actionPerformed(ActionEvent e) {
				if (e.getSource() == close) {
					TabbedPane.this.removeTab(tab);
				}
			}

			public void delete() {
				if (getParent() != null) {
					super.getParent().remove(this);
				}
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getSource() != close) {
					dropHoveringEffect();
					TabbedPane.this.selectTab(tab);
				}
			}

			@Override
			public void mousePressed(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseReleased(MouseEvent e) {
				// TODO Auto-generated method stub

			}

			@Override
			public void mouseEntered(MouseEvent e) {
				if (getParent() == extraTabsPopup) {
					hovered = true;
					setOpaque(true);
					defaultBackground = getBackground();
					defaultForeground = title.getForeground();
					setBackground(UIManager.getColor("List.selectionBackground"));
					title.setForeground(UIManager.getColor("List.selectionForeground"));
					setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
					repaint();
				}
			}

			@Override
			public void mouseExited(MouseEvent e) {
				dropHoveringEffect();
			}

			public void dropHoveringEffect() {
				if (hovered) {
					setOpaque(false);
					setBackground(defaultBackground);
					title.setForeground(defaultForeground);
					defaultBackground = null;
					defaultForeground = null;
					setCursor(Cursor.getDefaultCursor());
					repaint();
					hovered = false;
				}
			}

		}

		private Map<J, TabHeader> headerComponents = new HashMap<J, TabHeader>();

		private JButton extraTabs;
		private JPopupMenu extraTabsPopup;

		public TabHeaders() {
			setOpaque(false);
			extraTabs = new BarButton(UtilsIconLibrary.ARROW_DOWN);
			extraTabs.setSize(new Dimension(extraTabs.getIcon().getIconWidth(), extraTabs.getIcon().getIconHeight()));
			extraTabs.addActionListener(this);
			extraTabsPopup = new JPopupMenu();
			extraTabsPopup.setInvoker(extraTabs);
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == extraTabs) {
				if (extraTabsPopup != null) {
					if (extraTabsPopup.isVisible()) {
						extraTabsPopup.setVisible(false);
					} else {
						extraTabsPopup.setVisible(true);
						extraTabsPopup.show(extraTabs, extraTabs.getWidth() - extraTabsPopup.getWidth(), extraTabs.getHeight());
					}
				}
			}
		}

		@Override
		public void doLayout() {
			extraTabsPopup.removeAll();
			boolean moveToPopup = false;
			if (tabs.size() > 0) {
				TabHeader selectedHeader = headerComponents.get(selectedTab);

				boolean selectedHeaderDone = false;
				int x = 0;
				int availableWidth = getWidth();
				for (int i = 0; i < tabs.size(); i++) {
					J tab = tabs.get(i);
					TabHeader tabHeader = headerComponents.get(tab);
					if (!moveToPopup) {
						if (!selectedHeaderDone) {
							if (tab != selectedTab && selectedTab != null) {
								if (i + 2 == tabs.size()) { // in this case, we only need to put the current tab and the selected tab
									moveToPopup = availableWidth - (tabHeader.getWidth() + selectedHeader.getWidth()) < 0;
								} else {
									moveToPopup = availableWidth
											- (tabHeader.getWidth() + selectedHeader.getWidth() + extraTabs.getWidth()) < 0;
								}
							}
							if (moveToPopup) {
								// There is not enough room to put the current tab header, the selected header and the extraTabs button
								if (selectedHeader.getParent() != this) {
									add(selectedHeader);
								}
								tabs.remove(selectedTab);
								tabs.add(i, selectedTab);
								i++;
								selectedHeader.setLocation(x, 0);
								selectedHeaderDone = true;
							}
						} else {
							if (i + 1 == tabs.size()) {
								moveToPopup = availableWidth - tabHeader.getWidth() < 0;
							} else {
								moveToPopup = availableWidth - (tabHeader.getWidth() + extraTabs.getWidth()) < 0;
							}
						}
					}
					if (moveToPopup) {
						if (extraTabs.getParent() != this) {
							add(extraTabs);
						}
						extraTabs.setSize(extraTabs.getWidth(), getHeight());
						extraTabs.setLocation(getWidth() - extraTabs.getWidth(), 0);
						extraTabsPopup.add(tabHeader);
						extraTabsPopup.revalidate();
					} else {
						if (tabHeader.getParent() != this) {
							add(tabHeader);
						}
						tabHeader.setBounds(x, 0, tabHeader.getPreferredSize().width, getHeight());
						x += tabHeader.getWidth();
					}
					availableWidth = getWidth() - x;
					selectedHeaderDone |= tab == selectedTab;
				}
			}
			if (!moveToPopup) {
				if (extraTabs.getParent() == this) {
					remove(extraTabs);
				}
			}
		}

		public void selectTab(J tab) {
			revalidate();
			repaint();
		}

		public void refresh() {
			for (Entry<J, TabHeader> e : headerComponents.entrySet()) {
				e.getValue().refresh();
			}
		}

		public void addTab(J tab) {
			headerComponents.put(tab, new TabHeader(tab));
			doLayout();
		}

		public void removeTab(J tab) {
			TabHeader tabHeader = headerComponents.remove(tab);
			if (tabHeader != null) {
				Container parent = tabHeader.getParent();
				tabHeader.delete();
				if (parent == this) {
					revalidate();
					repaint();
				} else if (parent == extraTabsPopup) {
					extraTabsPopup.revalidate();
					extraTabsPopup.pack();
				}
			}
		}

	}

	private TabHeaderRenderer<J> tabHeaderRenderer;
	private List<TabListener<J>> tabListeners;

	private TabHeaders tabHeaders;
	private JPanel tabBody;

	private boolean useTabBody = true;

	private List<J> tabs;
	private J selectedTab;

	public TabbedPane() {
		tabs = new ArrayList<J>();
		tabListeners = new ArrayList<TabbedPane.TabListener<J>>();
		tabHeaders = new TabHeaders();
		tabBody = new JPanel(new BorderLayout());
	}

	public TabbedPane(TabHeaderRenderer<J> tabHeaderRenderer) {
		this();
		this.tabHeaderRenderer = tabHeaderRenderer;
	}

	public TabHeaders getTabHeaders() {
		return tabHeaders;
	}

	public JPanel getTabBody() {
		return tabBody;
	}

	public boolean isUseTabBody() {
		return useTabBody;
	}

	public void setUseTabBody(boolean useTabBody) {
		this.useTabBody = useTabBody;
	}

	public TabHeaderRenderer<J> getTabHeaderRenderer() {
		return tabHeaderRenderer;
	}

	public void setTabHeaderRenderer(TabHeaderRenderer<J> tabHeaderRenderer) {
		this.tabHeaderRenderer = tabHeaderRenderer;
	}

	public void addToTabListeners(TabListener<J> listener) {
		tabListeners.add(listener);
	}

	public void removeToTabListeners(TabListener<J> listener) {
		tabListeners.remove(listener);
	}

	public J getSelectedTab() {
		return selectedTab;
	}

	public boolean containsTab(J tab) {
		return tabs.contains(tab);
	}

	public void addTab(J tab) {
		if (!tabs.contains(tab)) {
			tabs.add(tab);
			tabHeaders.addTab(tab);
			if (selectedTab == null) {
				selectTab(tab);
			}
		}
	}

	public void removeTab(J tab) {
		int indexOf = tabs.indexOf(tab);
		if (tabs.remove(tab)) {
			if (selectedTab == tab) {
				// TODO: Handle removal of selected tab
				if (tabs.size() > 0) {
					if (indexOf >= tabs.size()) {
						selectTab(tabs.get(tabs.size() - 1));
					} else {
						selectTab(tabs.get(indexOf));
					}
				} else {
					selectTab(null);
				}
			}
			tabHeaders.removeTab(tab);
			fireTabClosed(tab);
		}
	}

	public void selectTab(J tab) {
		if (selectedTab == tab) {
			return;
		}
		if (tab == null && tabs.size() > 0) {
			throw new NullPointerException("Cannot select null tab");
		}
		if (tab != null && !tabs.contains(tab)) {
			throw new IllegalArgumentException("Tab must be added to the content pane first.");
		}
		if (selectedTab != null) {
			tabBody.remove(selectedTab);
		}
		selectedTab = tab;
		if (useTabBody) {
			tabBody.add(tab, 0);
			tabBody.revalidate();
			tabBody.repaint();
		}
		tabHeaders.selectTab(tab);
		fireTabSelected(tab);
	}

	public void refreshTabHeaders() {
		tabHeaders.refresh();
	}

	protected void fireTabClosed(J tab) {
		for (TabListener<J> tabListener : tabListeners) {
			tabListener.tabClosed(tab);
		}
	}

	protected void fireTabSelected(J tab) {
		for (TabListener<J> tabListener : tabListeners) {
			tabListener.tabSelected(tab);
		}
	}

	public static void main(String[] args) {
		JFrame frame = new JFrame("Test tabbed panes");
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		TabbedPane<JLabel> tabbedPane = new TabbedPane<JLabel>();
		tabbedPane.setTabHeaderRenderer(new TabHeaderRenderer<JLabel>() {

			@Override
			public String getTabHeaderTooltip(JLabel tab) {
				return "Some tooltip for " + tab.getText();
			}

			@Override
			public String getTabHeaderTitle(JLabel tab) {
				return tab.getText();
			}

			@Override
			public Icon getTabHeaderIcon(JLabel tab) {
				try {
					return new ImageIcon(new URL("http://cdn1.iconfinder.com/data/icons/CuteMonstersPNG/16/blue_monster.png"));
				} catch (MalformedURLException e) {
					e.printStackTrace();
					return null;
				}
			}
		});
		for (int i = 0; i < 20; i++) {
			tabbedPane.addTab(new JLabel("Some label " + (i + 1)));
		}
		tabbedPane.getTabHeaders().doLayout();
		frame.add(tabbedPane.getTabHeaders(), BorderLayout.NORTH);
		frame.add(tabbedPane.getTabBody());
		frame.setSize(800, 600);
		frame.setVisible(true);
	}
}
