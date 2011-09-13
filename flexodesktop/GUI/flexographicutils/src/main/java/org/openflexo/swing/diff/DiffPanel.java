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
package org.openflexo.swing.diff;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.AdjustmentEvent;
import java.awt.event.AdjustmentListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.DefaultListCellRenderer;
import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.ScrollPaneConstants;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import org.openflexo.diff.ComputeDiff;
import org.openflexo.diff.ComputeDiff.AdditionChange;
import org.openflexo.diff.ComputeDiff.DiffChange;
import org.openflexo.diff.ComputeDiff.DiffReport;
import org.openflexo.diff.ComputeDiff.ModificationChange;
import org.openflexo.diff.ComputeDiff.RemovalChange;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.toolbox.TokenMarkerStyle;


public class DiffPanel extends JLayeredPane {

	// This flag is used to get either left perspective or right perspective
	boolean isLeftOriented = true;
	
	private DiffReport _report;
	DiffTextArea _leftTextArea;
	DiffTextArea _rightTextArea;
	JList changesList;
	private ListSelectionListener lsl;
	
	private String NO_CHANGES_LABEL = "No structural changes";
	
	static Icon iconForChange(DiffChange value, boolean isLeftOriented)
	{
		if (value instanceof ModificationChange) {
			   return (isLeftOriented?UtilsIconLibrary.MODIFICATION_LEFT_ICON:UtilsIconLibrary.MODIFICATION_RIGHT_ICON);
		   }
		   else if (value instanceof AdditionChange) {
			   return (isLeftOriented?UtilsIconLibrary.REMOVAL_LEFT_ICON:UtilsIconLibrary.ADDITION_RIGHT_ICON);
		   }
		   else if (value instanceof RemovalChange) {
			   return (isLeftOriented?UtilsIconLibrary.ADDITION_LEFT_ICON:UtilsIconLibrary.REMOVAL_RIGHT_ICON);
		   }
		return null;
	}
	
	public DiffPanel(DiffReport diffReport)
	{
		this(diffReport,TokenMarkerStyle.None);
	}
	
	public DiffPanel(DiffReport diffReport,boolean _isLeftOriented)
	{
		this(diffReport,TokenMarkerStyle.None, _isLeftOriented);
	}
	
	public DiffPanel(DiffReport diffReport, TokenMarkerStyle style)
	{
		this(diffReport,style,null,null,null,true,BorderLayout.NORTH);
	}
	public DiffPanel(DiffReport diffReport, TokenMarkerStyle style, boolean _isLeftOriented)
	{
		this(diffReport,style,null,null,null,_isLeftOriented,BorderLayout.NORTH);
	}
	public DiffPanel(DiffReport diffReport, TokenMarkerStyle style, String changesListOrientation)
	{
		this(diffReport,style,null,null,null,true,changesListOrientation);
	}
	
	public DiffPanel(DiffReport diffReport, TokenMarkerStyle style, String leftLabel, String rightLabel, String noChangeLabel, boolean _isLeftOriented)
	{
		this(diffReport,style,leftLabel,rightLabel,noChangeLabel,_isLeftOriented,BorderLayout.NORTH);
	}
	
	public DiffPanel(DiffReport diffReport, TokenMarkerStyle style, String leftLabel, String rightLabel, String noChangeLabel, boolean _isLeftOriented, String changesListOrientation)
	{
		super();
		_report = diffReport;
		
		setLayout(new BorderLayout());
		
		this.isLeftOriented = _isLeftOriented;
		
		JPanel topPanel = new JPanel(new BorderLayout());
		//JLabel title = new JLabel("Diff panel",JLabel.CENTER);

		if (_report.getChanges().size() > 0) {
			changesList = new JList(_report.getChanges());
			changesList.setVisibleRowCount(5);
			changesList.setCellRenderer(new DefaultListCellRenderer(){
				@Override
				public Component getListCellRendererComponent(
						JList list,
						Object value,
						int index,
						boolean isSelected,
						boolean cellHasFocus)
				{
					JLabel returned = (JLabel)super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
					returned.setIcon(iconForChange((DiffChange)value,DiffPanel.this.isLeftOriented));
					returned.setText(((DiffChange)value).toNiceString(DiffPanel.this.isLeftOriented));
					return returned;
				}			
			});
			changesList.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
			lsl = new ListSelectionListener() {
				@Override
				public void valueChanged(ListSelectionEvent e) {
					selectChange((ComputeDiff.DiffChange)changesList.getSelectedValue(),false);
				}
			};
			changesList.addListSelectionListener(lsl);
			changesList.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					super.mouseClicked(e);
					if (e.getClickCount() == 2) {
						// When double-clicked, scroll to selected change
						selectChange((ComputeDiff.DiffChange)changesList.getSelectedValue(),true);
					}
				}
			});
		}
		else {
			if (noChangeLabel != null)
				NO_CHANGES_LABEL = noChangeLabel;
			Object[] obj = {NO_CHANGES_LABEL};
			changesList = new JList(obj);
			changesList.setEnabled(false);
			changesList.setVisibleRowCount(1);
		}

			//topPanel.add(title,BorderLayout.NORTH);
			topPanel.add(new JScrollPane(changesList),BorderLayout.CENTER);

			if ((leftLabel != null) || (rightLabel != null)) {

				JPanel labelPanels = new JPanel(new GridLayout(1,2));
				JLabel _leftLabel = new JLabel (leftLabel,SwingConstants.CENTER);
				JLabel _rightLabel = new JLabel (rightLabel,SwingConstants.CENTER);
				labelPanels.add(_leftLabel);
				labelPanels.add(_rightLabel);
                labelPanels.validate();
				topPanel.add(labelPanels,BorderLayout.SOUTH);
			}
            topPanel.validate();
			_leftTextArea = new DiffTextArea(_report.getInput0(),diffReport,DiffTextArea.Side.Left, style, isLeftOriented);
			_rightTextArea = new DiffTextArea(_report.getInput1(),diffReport,DiffTextArea.Side.Right, style, isLeftOriented);
		
		
		JComponent separator = getSeparator();
		
		JComponent left = _leftTextArea;
		JComponent right = _rightTextArea;
		
		GridBagLayout gridbag = new GridBagLayout();
		GridBagConstraints c = new GridBagConstraints();
		JPanel centerPane = new JPanel();
		centerPane.setLayout(gridbag);
		
		
		//c.gridwidth = 1;               
		//c.gridheight = 1;
		c.weightx = 0.0;               
		c.anchor = GridBagConstraints.NORTHWEST;
        c.fill = GridBagConstraints.NONE;
		//c.gridwidth = 1;
		gridbag.setConstraints(_leftTextArea.getLinesTA(), c);
		centerPane.add(_leftTextArea.getLinesTA());

		c.weightx = 1.0;               
		c.weighty = 1.0;               
		//c.gridwidth = 1;               
        //c.gridheight = 1;
		c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
		//c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(left, c);
		centerPane.add(left);
		
		//c.gridwidth = 1;               
		//c.gridheight = 1;
		c.weightx = 0.0;               
		c.anchor = GridBagConstraints.CENTER;
        c.fill = GridBagConstraints.VERTICAL;
		//c.gridwidth = 1;
		gridbag.setConstraints(separator, c);
		centerPane.add(separator);
		
		c.weightx = 1.0;               
		c.weighty = 1.0;               
		//c.gridwidth = 1;               
		//c.gridheight = 1;
		c.anchor = GridBagConstraints.EAST;
       c.fill = GridBagConstraints.BOTH;
		//c.gridwidth = GridBagConstraints.REMAINDER;
		gridbag.setConstraints(right, c);
		centerPane.add(right);
		
		//c.gridwidth = 1;               
		//c.gridheight = 1;
		c.weightx = 0.0;               
		c.anchor = GridBagConstraints.NORTHEAST;
        c.fill = GridBagConstraints.NONE;
		//c.gridwidth = 1;
		gridbag.setConstraints(_rightTextArea.getLinesTA(), c);
		centerPane.add(_rightTextArea.getLinesTA());
		centerPane.validate();
		add(topPanel,changesListOrientation);
		
		JPanel comparePanel = new JPanel(new BorderLayout());
		scrollPane = new JScrollPane(centerPane,ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED,ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		comparePanel.add(scrollPane,BorderLayout.CENTER);
		scrollPane.getVerticalScrollBar().setUnitIncrement(10);
		scrollPane.getVerticalScrollBar().setBlockIncrement(50);
		_leftTextArea.getHorizontalScrollBar().addAdjustmentListener(leftSBAdjustementListener = new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) 
			{
				_rightTextArea.getHorizontalScrollBar().removeAdjustmentListener(rightSBAdjustementListener);
				_rightTextArea.getHorizontalScrollBar().setValue(e.getValue()*
						(_rightTextArea.getHorizontalScrollBar().getMaximum()-_rightTextArea.getHorizontalScrollBar().getVisibleAmount()-_rightTextArea.getHorizontalScrollBar().getMinimum())
						/(_leftTextArea.getHorizontalScrollBar().getMaximum()-_leftTextArea.getHorizontalScrollBar().getVisibleAmount()-_leftTextArea.getHorizontalScrollBar().getMinimum()));
				_rightTextArea.getHorizontalScrollBar().addAdjustmentListener(rightSBAdjustementListener);
			}
		});
		_rightTextArea.getHorizontalScrollBar().addAdjustmentListener(rightSBAdjustementListener = new AdjustmentListener() {
			@Override
			public void adjustmentValueChanged(AdjustmentEvent e) 
			{
				_leftTextArea.getHorizontalScrollBar().removeAdjustmentListener(leftSBAdjustementListener);
				_leftTextArea.getHorizontalScrollBar().setValue(e.getValue()*(_leftTextArea.getHorizontalScrollBar().getMaximum()-_leftTextArea.getHorizontalScrollBar().getVisibleAmount()-_leftTextArea.getHorizontalScrollBar().getMinimum())/(_rightTextArea.getHorizontalScrollBar().getMaximum()-_rightTextArea.getHorizontalScrollBar().getVisibleAmount()-_rightTextArea.getHorizontalScrollBar().getMinimum()));
				_leftTextArea.getHorizontalScrollBar().addAdjustmentListener(leftSBAdjustementListener);
			}
		});
        JPanel scrollBars = new JPanel(new GridLayout(1,2));
        scrollBars.add(_leftTextArea.getHorizontalScrollBar());
        scrollBars.add(_rightTextArea.getHorizontalScrollBar());
        scrollBars.validate();
        
        comparePanel.add(scrollBars,BorderLayout.SOUTH);
		comparePanel.validate();
        
		add(comparePanel,BorderLayout.CENTER);
		validate();
		if (_report.getChanges().size() > 0) {
			//selectChange(_report.getChanges().firstElement());
			SwingUtilities.invokeLater(new Runnable() {
				@Override
				public void run() {
					changesList.setSelectedIndex(0);
				}				
			});
		}
	}
	
	private JScrollPane scrollPane;
	
	AdjustmentListener leftSBAdjustementListener;
	AdjustmentListener rightSBAdjustementListener;
	
	void selectChange (ComputeDiff.DiffChange change, boolean forceSelect)
	
	{
		if ((ComputeDiff.DiffChange)changesList.getSelectedValue() != change) {
			changesList.removeListSelectionListener(lsl);
			changesList.setSelectedValue(change, true);
			changesList.addListSelectionListener(lsl);
		}
		_leftTextArea.setChange(change,true,forceSelect);
		_rightTextArea.setChange(change,true,forceSelect);
	}
	
	private JComponent _separator = null;
	
	
	private JComponent getSeparator()
	{
		if (_separator == null) {
			_separator = new JPanel();
			_separator.setLayout(new BoxLayout(_separator,BoxLayout.Y_AXIS));
			_separator.setBorder(BorderFactory.createEmptyBorder(0,5,0,5));
		}
		return _separator;
	}
	
	protected boolean buttonsInitialized = false;

	private class SelectChange implements ActionListener
	{
		private DiffChange change;
		SelectChange(DiffChange change) { this.change=change; }
		@Override
		public void actionPerformed(ActionEvent e) { selectChange(change,false); }
	}
	
	private void updateChangeButtonLocations()
	{
		if (!buttonsInitialized) {
			if (_leftTextArea.readyToDisplay()) {
				int remainderHeight = _leftTextArea.getPreferredSize().height;
				for (DiffChange c : _report.getChanges()) {
					JButton button = new JButton(iconForChange(c,isLeftOriented));
					button.addActionListener(new SelectChange(c));
					button.setBorder(BorderFactory.createEmptyBorder(0,0,0,_leftTextArea.getVerticalScrollBar().getPreferredSize().width));
					int height = _leftTextArea.heightAboveChange(c,button.getPreferredSize().height);
					_separator.add(Box.createRigidArea(new Dimension(0,height)));
					_separator.add(button);
					remainderHeight -= button.getPreferredSize().height;
					remainderHeight -= height;
				}
				_separator.add(Box.createRigidArea(new Dimension(0,remainderHeight)));
				buttonsInitialized = true;
			}
			else {
				// Not ready 
			}
		}
	}

	@Override
	public void paint(Graphics g) 
	{
		super.paint(g);
		updateChangeButtonLocations();
	}

	public DiffTextArea getLeftTextArea() 
	{
		return _leftTextArea;
	}

	public DiffTextArea getRightTextArea() 
	{
		return _rightTextArea;
	}
	
	public void setFirstVisibleLine(int firstVisibleLine) 
	{
		scrollPane.scrollRectToVisible(new Rectangle(100,100));
	}
}
