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
package org.openflexo.fps.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.FocusListener;
import java.util.logging.Logger;

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openflexo.diff.merge.IMerge;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.merge.MergePanelElements;
import org.openflexo.swing.merge.MergePanelElements.ComparePanel;
import org.openflexo.toolbox.TokenMarkerStyle;


public class CVSMergeEditor extends JLayeredPane {

	private static final Logger logger = Logger.getLogger(CVSMergeEditor.class.getPackage().getName());

	private MergePanelElements mergePanelElements;
	private IMerge _report;
	private MergePanelElements.FilterChangeList changesList;
	private ComparePanel comparePanel;
	private JPanel controlPanel;
	
	public CVSMergeEditor(IMerge merge, TokenMarkerStyle style, String leftLabel, String rightLabel, String mergeLabel, String noChangeLabel)
	{
		super();
		_report = merge;
		
		setLayout(new BorderLayout());
		
		mergePanelElements = new MergePanelElements(merge,style,noChangeLabel) {
			@Override
			protected String localizedForKey(String key)
			{
				return FlexoLocalization.localizedForKey(key);
			}
		};
		changesList = mergePanelElements.getChangesList();
		changesList.setVisibleRowCount(5);
		comparePanel = mergePanelElements.getComparePanel();
		JScrollPane mergeTextArea = mergePanelElements.getMergePanel();

		JPanel northPanel = new JPanel(new BorderLayout());
		
		JPanel northEastPanel = new JPanel(new BorderLayout());
		
		northEastPanel.add(mergeTextArea,BorderLayout.CENTER);
		if (mergeLabel != null) {
			northEastPanel.add(new JLabel(mergeLabel,SwingConstants.CENTER),BorderLayout.NORTH);
		}
		northPanel.add(changesList,BorderLayout.WEST);
		northPanel.add(northEastPanel,BorderLayout.CENTER);
		
		JPanel southPanel = new JPanel(new BorderLayout());
		southPanel.add(comparePanel,BorderLayout.CENTER);
		if ((leftLabel != null) || (rightLabel != null)) {
			JPanel labelPanels = new JPanel(new GridLayout(1,2));
			JLabel _leftLabel = new JLabel (leftLabel,SwingConstants.CENTER);
			JLabel _rightLabel = new JLabel (rightLabel,SwingConstants.CENTER);
			labelPanels.add(_leftLabel);
			labelPanels.add(_rightLabel);
			southPanel.add(labelPanels,BorderLayout.NORTH);
		}

		
		JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT,northPanel,southPanel);
		Dimension dim = northPanel.getPreferredSize();
		dim.height = 300;
		northPanel.setPreferredSize(dim);
		Dimension dim2 = southPanel.getPreferredSize();
		dim2.height = 300;
		southPanel.setPreferredSize(dim2);
		splitPane.setDividerLocation(0.5);
		splitPane.setResizeWeight(0.5);
		
		add(splitPane,BorderLayout.CENTER);
		
		controlPanel = mergePanelElements.getControlPanel();
		add(controlPanel,BorderLayout.SOUTH);

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
	
	public boolean isEditable() 
	{
		return mergePanelElements.isEditable();
	}

	public void setEditable(boolean editable)
	{
		mergePanelElements.setEditable(editable);
		revalidate();
		repaint();
	}

	public void addToFocusListener(FocusListener aFocusListener) 
	{
		mergePanelElements.getLeftTextArea().addFocusListener(aFocusListener);
		mergePanelElements.getRightTextArea().addFocusListener(aFocusListener);
		mergePanelElements.getMergeTextArea().addFocusListener(aFocusListener);
	}

	public void setFirstVisibleLine(int firstVisibleLine) 
	{
		mergePanelElements.getMergeTextArea().scrollTo(firstVisibleLine, 0);
		mergePanelElements.getRightTextArea().scrollTo(firstVisibleLine, 0);
		mergePanelElements.getLeftTextArea().scrollTo(firstVisibleLine, 0);
	}
}
