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
package org.openflexo.dre;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.SwingConstants;

import org.openflexo.ColorCst;
import org.openflexo.drm.DocItem;
import org.openflexo.icon.DREIconLibrary;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;

public class DocItemListView extends JPanel {
	static final Logger logger = Logger.getLogger(DocItemListView.class.getPackage().getName());

	JList list;
	private JScrollPane scrollPane;
	private DocItemListViewFooter footer;

	DocItemListModel _listModel;

	public DocItemListView(String unlocalizedTitle, DocItemListModel listModel) {
		super(new BorderLayout());
		_listModel = listModel;
		list = new JList(new DocItemListViewListModel());
		list.setCellRenderer(new DocItemListViewCellRenderer());
		scrollPane = new JScrollPane(list);
		add(scrollPane, BorderLayout.CENTER);
		JLabel title = new JLabel();
		title.setForeground(Color.DARK_GRAY);
		title.setText(FlexoLocalization.localizedForKey(unlocalizedTitle, title));
		title.setHorizontalAlignment(SwingConstants.CENTER);
		title.setBorder(BorderFactory.createEmptyBorder(3, 3, 3, 3));
		add(title, BorderLayout.NORTH);
		add(footer = new DocItemListViewFooter(), BorderLayout.SOUTH);
		list.setVisibleRowCount(6);
		list.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				if (e.getClickCount() == 2) {
					_listModel.itemDoubleClicked((DocItem) list.getSelectedValue());
				}
			}
		});
	}

	@Override
	public void setEnabled(boolean aBoolean) {
		super.setEnabled(aBoolean);
		list.setEnabled(aBoolean);
		if (!aBoolean) {
			footer.plusButton.setEnabled(false);
			footer.minusButton.setEnabled(false);
			footer.optionsButton.setEnabled(false);
		}
	}

	class DocItemListViewListModel extends AbstractListModel {
		@Override
		public int getSize() {
			return _listModel.getItems().size();
		}

		@Override
		public Object getElementAt(int index) {
			return _listModel.getItems().elementAt(index);
		}
	}

	public void updateViewFromModel() {
		list.setVisibleRowCount(6);
		list.updateUI();
		revalidate();
		repaint();
	}

	public interface DocItemListModel {
		public Vector getItems();

		public void itemAdded();

		public void itemRemoved(DocItem anItem);

		public void itemDoubleClicked(DocItem anItem);
	}

	protected class DocItemListViewFooter extends JPanel {
		protected JButton plusButton;
		protected JButton minusButton;
		protected JButton optionsButton;

		protected DocItemListViewFooter() {
			super(new BorderLayout());
			JPanel plusMinusPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			plusMinusPanel.setBackground(ColorCst.GUI_BACK_COLOR);
			plusMinusPanel.setBorder(BorderFactory.createEmptyBorder());

			plusButton = new JButton(IconLibrary.BROWSER_PLUS_ICON);
			plusButton.setBackground(ColorCst.GUI_BACK_COLOR);
			plusButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					plusButton.setIcon(IconLibrary.BROWSER_PLUS_ICON);
					_listModel.itemAdded();
				}

			});
			plusButton.setBorder(BorderFactory.createEmptyBorder());
			plusButton.setDisabledIcon(IconLibrary.BROWSER_PLUS_DISABLED_ICON);
			plusButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent mouseEvent) {
					if (plusButton.isEnabled()) {
						plusButton.setIcon(IconLibrary.BROWSER_PLUS_SELECTED_ICON);
					}
				}

				@Override
				public void mouseReleased(MouseEvent mouseEvent) {
					if (plusButton.isEnabled()) {
						plusButton.setIcon(IconLibrary.BROWSER_PLUS_ICON);
					}
				}
			});

			minusButton = new JButton(IconLibrary.BROWSER_MINUS_ICON);
			minusButton.setBackground(ColorCst.GUI_BACK_COLOR);
			minusButton.addActionListener(new ActionListener() {
				@Override
				public void actionPerformed(ActionEvent e) {
					minusButton.setIcon(IconLibrary.BROWSER_MINUS_ICON);
					// logger.info("On enleve un truc");
					_listModel.itemRemoved((DocItem) list.getSelectedValue());
				}
			});
			minusButton.setBorder(BorderFactory.createEmptyBorder());
			minusButton.setDisabledIcon(IconLibrary.BROWSER_MINUS_DISABLED_ICON);
			minusButton.addMouseListener(new MouseAdapter() {
				@Override
				public void mousePressed(MouseEvent mouseEvent) {
					if (minusButton.isEnabled()) {
						minusButton.setIcon(IconLibrary.BROWSER_MINUS_SELECTED_ICON);
					}
				}

				@Override
				public void mouseReleased(MouseEvent mouseEvent) {
					if (minusButton.isEnabled()) {
						minusButton.setIcon(IconLibrary.BROWSER_MINUS_ICON);
					}
				}
			});

			plusMinusPanel.add(plusButton);
			plusMinusPanel.add(minusButton);

			add(plusMinusPanel, BorderLayout.WEST);
			optionsButton = new JButton(IconLibrary.BROWSER_OPTIONS_ICON);
			optionsButton.setBorder(BorderFactory.createEmptyBorder());
			optionsButton.setDisabledIcon(IconLibrary.BROWSER_OPTIONS_DISABLED_ICON);
			add(optionsButton, BorderLayout.EAST);
			optionsButton.setEnabled(false);

		}
	}

	class DocItemListViewCellRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			JLabel returned = (JLabel) super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			DocItem docItem = (DocItem) value;
			returned.setIcon(DREIconLibrary.DOC_ITEM_ICON);
			returned.setText(docItem.getIdentifier());
			return returned;
		}
	}

}
