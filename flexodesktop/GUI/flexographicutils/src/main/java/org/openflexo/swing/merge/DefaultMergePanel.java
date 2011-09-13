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

import javax.swing.JLabel;
import javax.swing.JLayeredPane;
import javax.swing.JList;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.diff.merge.IMerge;
import org.openflexo.diff.merge.Merge;
import org.openflexo.swing.merge.MergePanelElements.ComparePanel;
import org.openflexo.toolbox.TokenMarkerStyle;


public class DefaultMergePanel extends JLayeredPane {

	private MergePanelElements mergePanelElements;
	private Merge _merge;
	JList changesList;
	ComparePanel comparePanel;
	
	public DefaultMergePanel(Merge merge, TokenMarkerStyle style)
	{
		super();
		_merge = merge;
		
		setLayout(new BorderLayout());
		
		mergePanelElements = new MergePanelElements(merge,style);
		MergePanelElements.FilterChangeList changesList = mergePanelElements.getChangesList();
		changesList.setVisibleRowCount(10);
		ComparePanel comparePanel = mergePanelElements.getComparePanel();
		
		JPanel topPanel = new JPanel(new BorderLayout());
		JLabel title = new JLabel("Merge panel",SwingConstants.CENTER);

			topPanel.add(title,BorderLayout.NORTH);
			topPanel.add(changesList,BorderLayout.CENTER);

		add(topPanel,BorderLayout.NORTH);
		add(comparePanel,BorderLayout.CENTER);

		if (_merge.getChanges().size() > 0) {
			mergePanelElements.selectChange(_merge.getChanges().firstElement());
		}
	}

	public IMerge getMerge()
	{
		return mergePanelElements.getMerge();
	}


}
