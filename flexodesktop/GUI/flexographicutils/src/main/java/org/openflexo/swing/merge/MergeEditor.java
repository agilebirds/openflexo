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
package org.openflexo.swing.merge;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openflexo.diff.merge.Merge;
import org.openflexo.swing.merge.MergePanelElements.ComparePanel;

public abstract class MergeEditor extends JLayeredPane {

	MergePanelElements mergePanelElements;
	Merge _report;
	MergePanelElements.FilterChangeList changesList;
	ComparePanel comparePanel;
	JPanel controlPanel;

	public MergeEditor(Merge merge) {
		super();
		_report = merge;

		setLayout(new BorderLayout());

		mergePanelElements = new MergePanelElements(merge);
		changesList = mergePanelElements.getChangesList();
		changesList.setVisibleRowCount(5);
		ComparePanel comparePanel = mergePanelElements.getComparePanel();
		JScrollPane mergeTextArea = mergePanelElements.getMergePanel();

		JPanel topPanel = new JPanel(new BorderLayout());
		JLabel title = new JLabel("Merge panel", SwingConstants.CENTER);

		topPanel.add(title, BorderLayout.NORTH);
		topPanel.add(changesList, BorderLayout.CENTER);

		add(topPanel, BorderLayout.NORTH);

		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, mergeTextArea, comparePanel);
		Dimension dim = mergeTextArea.getPreferredSize();
		dim.height = 280;
		mergeTextArea.setPreferredSize(dim);
		Dimension dim2 = comparePanel.getPreferredSize();
		dim2.height = 300;
		comparePanel.setPreferredSize(dim2);
		splitPane.setDividerLocation(0.5);

		add(splitPane, BorderLayout.CENTER);

		controlPanel = mergePanelElements.getControlPanel();
		add(controlPanel, BorderLayout.SOUTH);

		JButton doneButton = new JButton();
		doneButton.setText("Done");
		doneButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				done();
			}
		});

		controlPanel.add(doneButton);
		validate();

		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				if (_report.getChanges().size() > 0) {
					mergePanelElements.selectChange(_report.getChanges().firstElement());
				}
			}
		});
	}

	public abstract void done();

}
