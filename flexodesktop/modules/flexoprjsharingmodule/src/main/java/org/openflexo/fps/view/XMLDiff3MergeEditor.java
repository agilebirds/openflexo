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
import java.awt.Component;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.event.FocusListener;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import javax.swing.Box;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

import org.openflexo.swing.merge.MergePanelElements.ComparePanel;
import org.openflexo.xml.diff3.UnresolvedConflict;
import org.openflexo.xml.diff3.XMLDiff3;


public class XMLDiff3MergeEditor extends JLayeredPane {

	private static final Logger logger = Logger.getLogger(CVSMergeEditor.class.getPackage().getName());

	private XMLDiff3 _diff3;
	private ComparePanel comparePanel;
	private JPanel controlPanel;
	
	public XMLDiff3MergeEditor(XMLDiff3 diff3, String leftLabel, String rightLabel, String mergeLabel, String noChangeLabel)
	{
		super();
		_diff3 = diff3;
		setLayout(new BorderLayout());
		UnresolvedConflictListView listView = new UnresolvedConflictListView(_diff3.getAllUnresolvedConflicts());
		JScrollPane scroll = new JScrollPane(listView);
		scroll.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
		scroll.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
		add(scroll,BorderLayout.CENTER);
	}
	
	public boolean isEditable() 
	{
		return false;
	}

	public void setEditable(boolean editable)
	{
	}

	public void addToFocusListener(FocusListener aFocusListener) 
	{
	}

	public void setFirstVisibleLine(int firstVisibleLine) 
	{
	}
	
	private class UnresolvedConflictListView extends JPanel/* implements ListCellRenderer*/{
		public UnresolvedConflictListView(Vector<UnresolvedConflict> list){
			super();
			GridBagLayout gridBag = new GridBagLayout();
			setLayout(gridBag);
			Enumeration<UnresolvedConflict> en  = list.elements();
			int i = 0;
			while(en.hasMoreElements()){
				UnresolvedConflict conflict = en.nextElement();
				Component c = org.openflexo.xml.diff3.view.UnresolvedConflictView.getView(conflict);
				GridBagConstraints cons = new GridBagConstraints();
				cons.gridy = i;
				cons.anchor = GridBagConstraints.NORTH;
				cons.weightx = 1.0;
				cons.fill = GridBagConstraints.HORIZONTAL;
				add(c,cons);
				i++;
			}
			
			GridBagConstraints c = new GridBagConstraints();
            c.fill = GridBagConstraints.HORIZONTAL;
            c.weightx = 0.0;
            c.weighty = 1.0;
            c.gridwidth = GridBagConstraints.NONE;
            c.gridheight = GridBagConstraints.REMAINDER;
            c.anchor = GridBagConstraints.CENTER;
            Component glue = Box.createGlue();
            gridBag.setConstraints(glue, c);
            add(glue);
		}

		
	}
	
}
