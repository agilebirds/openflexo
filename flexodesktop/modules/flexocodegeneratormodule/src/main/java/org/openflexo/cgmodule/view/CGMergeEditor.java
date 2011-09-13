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
package org.openflexo.cgmodule.view;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.FocusListener;
import java.io.File;
import java.io.IOException;
import java.util.logging.Logger;

import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSplitPane;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openflexo.diff.DiffSource;
import org.openflexo.diff.merge.DefaultMergedDocumentType;
import org.openflexo.diff.merge.Merge;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.merge.MergePanelElements;
import org.openflexo.swing.merge.MergeTextArea;
import org.openflexo.swing.merge.MergePanelElements.ComparePanel;
import org.openflexo.toolbox.FileResource;
import org.openflexo.toolbox.TokenMarkerStyle;


public class CGMergeEditor extends JLayeredPane {

	private static final Logger logger = Logger.getLogger(MergeCodeDisplayer.class.getPackage().getName());

	private MergePanelElements mergePanelElements;
	private Merge _report;
	private MergePanelElements.FilterChangeList changesList;
	private ComparePanel comparePanel;
	private JPanel controlPanel;
	
	public CGMergeEditor(Merge merge, TokenMarkerStyle style, String leftLabel, String rightLabel, String mergeLabel, String noChangeLabel)
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

		/*JButton showOriginalButton = new JButton("Show original");
		showOriginalButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("showOriginalButton: Current diff source:");
				logger.info(_report.getOriginalSource().getSourceString());
			}
		});
		JButton showLeftButton = new JButton("Show left");
		showLeftButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("showLeftButton: Current diff source:");
				logger.info(_report.getLeftSource().getSourceString());
			}
		});
		JButton showRightButton = new JButton("Show right");
		showRightButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				logger.info("showRightButton: Current diff source:");
				logger.info(_report.getRightSource().getSourceString());
			}
		});
		JButton showNewMergeButton = new JButton("Show new merge");
		showNewMergeButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				Merge newMerge = new Merge(
						new DiffSource(_report.getOriginalSource().getSourceString()),
						new DiffSource(_report.getLeftSource().getSourceString()),
						new DiffSource(_report.getRightSource().getSourceString()));
				editMerge(newMerge);
			}
		});
		
		controlPanel.add(showOriginalButton);
		controlPanel.add(showLeftButton);
		controlPanel.add(showRightButton);
		controlPanel.add(showNewMergeButton);*/
		
		
		/*JButton markAsMergedButton = new JButton();
		markAsMergedButton.setText(FlexoLocalization.localizedForKey("mark_as_merged",markAsMergedButton));
		markAsMergedButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				markAsMerged();
			}
		});
		
		controlPanel.add(markAsMergedButton);*/
		
		
		validate();
		
		SwingUtilities.invokeLater(new Runnable() {
			@Override
			public void run() {
				/*if (!isValid()) {
					System.out.println("Je recommence");
					SwingUtilities.invokeLater(this);
				}
				else {*/
				if (_report.getChanges().size() > 0) {
					mergePanelElements.selectChange(_report.getChanges().firstElement());
				}
				//}
			}			
		});
	}
	
	/**
	 * @param args
	 */
	public static void main(String[] args) 
	{
		File original = new FileResource("TestMerge/TestMerge2-original.java");
		File left = new FileResource("TestMerge/TestMerge2-left.java");
		File right = new FileResource("TestMerge/TestMerge2-right.java");

		Merge merge = null;
		try {
			merge = new Merge(new DiffSource(original),new DiffSource(left),new DiffSource(right),DefaultMergedDocumentType.JAVA);
		} catch (IOException e) {
			e.printStackTrace();
		}
		
		final JDialog dialog = new JDialog((Frame)null,true);

		JPanel panel = new JPanel(new BorderLayout());
		CGMergeEditor editor = new CGMergeEditor(merge,TokenMarkerStyle.Java,"left","right","merge","no changes");
		panel.add(editor,BorderLayout.CENTER);
		editor.setEditable(true);
		
		dialog.setPreferredSize(new Dimension(1000,800));
		dialog.getContentPane().add(panel);
		dialog.pack();
		dialog.setVisible(true);
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

	public MergeTextArea getLeftTextArea() {
		return mergePanelElements.getLeftTextArea();
	}

	public MergeTextArea getMergeTextArea() {
		return mergePanelElements.getMergeTextArea();
	}

	public MergeTextArea getRightTextArea() {
		return mergePanelElements.getRightTextArea();
	}

	public void setFirstVisibleLine(int firstVisibleLine) 
	{
		getMergeTextArea().scrollTo(firstVisibleLine, 0);
		getRightTextArea().scrollTo(firstVisibleLine, 0);
		getLeftTextArea().scrollTo(firstVisibleLine, 0);
	}

	
}
