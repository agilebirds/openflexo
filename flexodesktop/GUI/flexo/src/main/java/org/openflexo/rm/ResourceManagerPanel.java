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
package org.openflexo.rm;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.AbstractListModel;
import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.TableColumn;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.ResourceList;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.TableSorter;

/**
 * Dialog displaying Resource Management Model
 * 
 * @author sguerin
 * 
 */
public class ResourceManagerPanel extends JPanel {
	ResourceManagerModel _rmModel;

	private FlexoResource _selectedResource = null;

	private final JList synchronizedResourcesList;

	private final JList dependantResourcesList;

	private final JList alteredResourcesList;

	ResourceManagerWindow _window;

	TableSorter sorter;

	public ResourceManagerPanel(FlexoProject project, ResourceManagerModel rmModel, ResourceManagerWindow window) {
		super();

		_window = window;

		setLayout(new BorderLayout());
		_rmModel = rmModel;

		sorter = new TableSorter(_rmModel);
		JTable resourceTable = new JTable(sorter);
		sorter.setTableHeader(resourceTable.getTableHeader());
		resourceTable.getTableHeader().setReorderingAllowed(false);
		// JTable resourceTable = new JTable(_rmModel);

		for (int i = 0; i < _rmModel.getColumnCount(); i++) {
			TableColumn col = resourceTable.getColumnModel().getColumn(i);
			col.setPreferredWidth(_rmModel.getPreferedColumnSize(i));
		}
		resourceTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		ListSelectionModel rowSM = resourceTable.getSelectionModel();
		rowSM.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				// Ignore extra messages.
				if (e.getValueIsAdjusting()) {
					return;
				}
				ListSelectionModel lsm = (ListSelectionModel) e.getSource();
				if (lsm.isSelectionEmpty()) {
					// no rows are selected
				} else {
					int selectedRow = lsm.getMinSelectionIndex();
					setSelectedResource(_rmModel.resourceAt(sorter.modelIndex(selectedRow)));
				}
				_window.update();
			}
		});

		JLabel title = new JLabel(_rmModel.getTitle(), SwingConstants.CENTER);
		title.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

		JScrollPane scrollPane = new JScrollPane(resourceTable);
		JPanel resourcesPanel = new JPanel();
		resourcesPanel.setLayout(new BorderLayout());
		resourcesPanel.add(resourceTable.getTableHeader(), BorderLayout.NORTH);
		resourcesPanel.add(scrollPane, BorderLayout.CENTER);
		resourcesPanel.setPreferredSize(new Dimension(1200, 200));

		JPanel syncPanel = new JPanel(new BorderLayout());
		JLabel syncTitle = new JLabel(FlexoLocalization.localizedForKey("synchronized_resources"), SwingConstants.CENTER);
		synchronizedResourcesList = new JList();
		synchronizedResourcesList.setCellRenderer(new ResourceListRenderer());
		synchronizedResourcesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		/*
		 * synchronizedResourcesList.addListSelectionListener(new
		 * ListSelectionListener() { public void valueChanged(ListSelectionEvent
		 * e) { //Ignore extra messages. if (e.getValueIsAdjusting()) { return; }
		 * int selectedRow = synchronizedResourcesList.getSelectedIndex(); }
		 * 
		 * });
		 */
		syncPanel.add(syncTitle, BorderLayout.NORTH);
		syncPanel.add(new JScrollPane(synchronizedResourcesList), BorderLayout.CENTER);

		JPanel dependantPanel = new JPanel(new BorderLayout());
		JLabel dependantTitle = new JLabel(FlexoLocalization.localizedForKey("dependant_resources"), SwingConstants.CENTER);
		dependantResourcesList = new JList();
		dependantResourcesList.setCellRenderer(new ResourceListRenderer());
		dependantResourcesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		dependantPanel.add(dependantTitle, BorderLayout.NORTH);
		dependantPanel.add(new JScrollPane(dependantResourcesList), BorderLayout.CENTER);

		JPanel alteredPanel = new JPanel(new BorderLayout());
		JLabel alteredTitle = new JLabel(FlexoLocalization.localizedForKey("altered_resources"), SwingConstants.CENTER);
		alteredResourcesList = new JList();
		alteredResourcesList.setCellRenderer(new ResourceListRenderer());
		alteredResourcesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		alteredPanel.add(alteredTitle, BorderLayout.NORTH);
		alteredPanel.add(new JScrollPane(alteredResourcesList), BorderLayout.CENTER);

		JPanel relationPanel = new JPanel();
		relationPanel.setLayout(new BoxLayout(relationPanel, BoxLayout.X_AXIS));
		relationPanel.add(syncPanel);
		relationPanel.add(dependantPanel);
		relationPanel.add(alteredPanel);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, resourcesPanel, relationPanel);
		splitPane.setDividerLocation(0.5);
		splitPane.setResizeWeight(0.5);

		add(title, BorderLayout.NORTH);
		add(splitPane, BorderLayout.CENTER);
	}

	/**
	 * @param resource
	 */
	protected void setSelectedResource(FlexoResource resource) {
		_selectedResource = resource;
		synchronizedResourcesList.setModel(new ResourceListModel(resource.getSynchronizedResources()));
		dependantResourcesList.setModel(new ResourceListModel(resource.getDependentResources()));
		alteredResourcesList.setModel(new ResourceListModel(resource.getAlteredResources()));
	}

	public FlexoResource getSelectedResource() {
		return _selectedResource;
	}

	protected class ResourceListModel extends AbstractListModel {
		private final ResourceList _resList;

		protected ResourceListModel(ResourceList resList) {
			super();
			_resList = resList;
		}

		@Override
		public int getSize() {
			return _resList.size();
		}

		@Override
		public Object getElementAt(int index) {
			return _resList.elementAt(index);
		}

	}

	protected class ResourceListRenderer extends DefaultListCellRenderer {
		@Override
		public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
			Component returned = super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
			if (returned instanceof JLabel) {
				JLabel label = (JLabel) returned;
				label.setText(((FlexoResource) value).getResourceIdentifier());
				label.setIcon(IconLibrary.getIconForResourceType(((FlexoResource) value).getResourceType()));
				label.setEnabled(((FlexoResource) value).isActive());
			}
			return returned;
		}
	}

	public ResourceManagerModel getRMModel() {
		return _rmModel;
	}
}
