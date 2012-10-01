package org.openflexo.swing;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.CubicCurve2D;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.border.Border;

import org.openflexo.icon.UtilsIconLibrary;

public class TabbedPane<J extends JComponent> {

	public static interface TabHeaderRenderer<J extends JComponent> {
		public Icon getTabHeaderIcon(J tab);

		public String getTabHeaderTitle(J tab);

		public String getTabeHeaderTooltip(J tab);
	}

	public static interface TabListener<J extends JComponent> {
		public void tabSelected(J tab);

		public void tabClosed(J tab);
	}

	private class TabHeaders extends JPanel implements ActionListener {

		private class TabHeaderBorder implements Border {

			private static final int ROUNDED_CORNER_SIZE = 5;

			@Override
			public void paintBorder(Component c, Graphics g, int x, int y, int width, int height) {
				g.setColor(Color.BLACK);
				g.drawLine(0, ROUNDED_CORNER_SIZE, 0, height);
				g.drawArc(ROUNDED_CORNER_SIZE, ROUNDED_CORNER_SIZE, ROUNDED_CORNER_SIZE, ROUNDED_CORNER_SIZE, 90, 180);
				CubicCurve2D.Double curve = new CubicCurve2D.Double(width - 4 * ROUNDED_CORNER_SIZE, 0, width - 2 * ROUNDED_CORNER_SIZE, 0,
						width - 2 * ROUNDED_CORNER_SIZE, height, width, height);
				((Graphics2D) g).draw(curve);
				g.drawLine(ROUNDED_CORNER_SIZE, 0, width - 4 * ROUNDED_CORNER_SIZE, 0);
			}

			@Override
			public Insets getBorderInsets(Component c) {
				return new Insets(ROUNDED_CORNER_SIZE, ROUNDED_CORNER_SIZE, 0, 4 * ROUNDED_CORNER_SIZE);
			}

			@Override
			public boolean isBorderOpaque() {
				return false;
			}

		}

		private class TabHeader extends JPanel implements ActionListener {

			private final J tab;

			private JLabel title;
			private JButton close;

			public TabHeader(J tab) {
				super(new BorderLayout());
				this.tab = tab;
				add(title = new JLabel());
				title.setOpaque(false);
				title.setBorder(null);
				add(close = new JButton(UtilsIconLibrary.CLOSE_ICON), BorderLayout.EAST);
				close.setRolloverIcon(UtilsIconLibrary.CLOSE_HOVER_ICON);
				close.addActionListener(this);
				setBorder(new TabHeaderBorder());
				refresh();
			}

			public void refresh() {
				if (tabHeaderRenderer != null) {
					title.setIcon(tabHeaderRenderer.getTabHeaderIcon(tab));
					title.setText(tabHeaderRenderer.getTabHeaderTitle(tab));
					title.setToolTipText(tabHeaderRenderer.getTabeHeaderTooltip(tab));
				} else {
					title.setIcon(null);
					title.setText(tab.getName());
					title.setToolTipText(tab.getToolTipText());
				}
				setSize(getPreferredSize());
				TabHeaders.this.revalidate();
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

		}

		private Map<J, TabHeader> headerComponents = new HashMap<J, TabHeader>();

		private JButton extraTabs;
		private JPopupMenu extraTabsPopup;

		public TabHeaders() {
			extraTabs = new JButton(UtilsIconLibrary.CUSTOM_POPUP_DOWN);
			extraTabs.addActionListener(this);
			extraTabsPopup = new JPopupMenu();
		}

		@Override
		public void actionPerformed(ActionEvent e) {
			if (e.getSource() == extraTabs) {
				if (extraTabsPopup != null) {
					if (extraTabsPopup.isVisible()) {
						extraTabsPopup.setVisible(false);
					} else {
						extraTabsPopup.show(extraTabs, extraTabs.getWidth() - extraTabsPopup.getWidth(), extraTabs.getHeight());
					}
				}
			}
		}

		@Override
		public void doLayout() {
			extraTabsPopup.removeAll();
			if (tabs.size() > 0) {
				TabHeader selectedHeader = headerComponents.get(selectedTab);

				boolean selectedHeaderDone = false;
				boolean moveToPopup = false;
				int x = 0;
				int availableWidth = getWidth();
				for (int i = 0; i < tabs.size(); i++) {
					J tab = tabs.get(i);
					TabHeader tabHeader = headerComponents.get(tab);
					if (!selectedHeaderDone) {
						if (tab != selectedTab) {
							if (i + 2 == tabs.size()) { // in this case, we only need to put the current tab and the selected tab
								moveToPopup = availableWidth - (tabHeader.getWidth() + selectedHeader.getWidth()) < 0;
							} else {
								moveToPopup = availableWidth - (tabHeader.getWidth() + selectedHeader.getWidth() + extraTabs.getWidth()) < 0;
							}
						}
						if (moveToPopup) {
							// There is not enough room to put the current tab header, the selected header and the extraTabs button
							if (selectedHeader.getParent() != this) {
								add(selectedHeader);
							}
							selectedHeader.setLocation(x, 0);
						}
					} else {

					}

					availableWidth = getWidth() - x;
					selectedHeaderDone |= tab == selectedTab;
				}
			}
		}

		public void selectTab(J tab) {

		}

		public void refresh() {
			// TODO Auto-generated method stub

		}

		public void addTab(J tab) {
			headerComponents.put(tab, new TabHeader(tab));
		}

		public void removeTab(J tab) {
			TabHeader tabHeader = headerComponents.remove(tab);
			if (tabHeader != null) {
				tabHeader.delete();
			}
		}

	}

	private TabHeaderRenderer<J> tabHeaderRenderer;
	private List<TabListener<J>> tabListeners;

	private TabHeaders tabHeaders;
	private JPanel tabBody;

	private List<J> tabs;
	private J selectedTab;

	public TabbedPane() {
		tabs = new ArrayList<J>();
		tabListeners = new ArrayList<TabbedPane.TabListener<J>>();
		tabHeaders = new TabHeaders();
		tabBody = new JPanel(new FlowLayout(FlowLayout.LEADING, 0, 0));
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

	public void addTab(J tab) {
		if (!tabs.contains(tab)) {
			tabs.add(tab);
			tabHeaders.addTab(tab);
		}
	}

	public void removeTab(J tab) {
		if (tabs.remove(tab)) {
			if (selectedTab == tab) {
				// TODO: Handle removal of selected tab
			}
			tabHeaders.removeTab(tab);
			fireTabClosed(tab);
		}
	}

	public void selectTab(J tab) {
		if (tab == null) {
			throw new NullPointerException("Cannot select null tab");
		}
		selectedTab = tab;
		tabHeaders.selectTab(tab);
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

}
