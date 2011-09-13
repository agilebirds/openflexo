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
import java.awt.FlowLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;
import javax.swing.SwingUtilities;

import org.openflexo.diff.merge.MergeChange;
import org.openflexo.diff.merge.MergeChange.MergeChangeAction;
import org.openflexo.jedit.JEditTextAreaWithHighlights;
import org.openflexo.jedit.TokenMarker;
import org.openflexo.toolbox.FontCst;
import org.openflexo.toolbox.TokenMarkerStyle;


public class HandConflictResolver extends JDialog {

	private MergeChangeAction previous;
	private MergeChange conflictChange;
	private EditorTextArea editor;
	
	public HandConflictResolver(MergeChange conflictChange, TokenMarkerStyle style, MergeChangeAction previous, MergePanelElements owner)
	{
		super((JFrame)SwingUtilities.getAncestorOfClass(JFrame.class, owner.getComparePanel()),true);
		
		this.previous = previous;
		this.conflictChange = conflictChange;
		conflictChange.setMergeChangeAction(previous);
		String[] changeResult = conflictChange.getMergeChangeResult().getSignificativeTokens();
		StringBuffer sb = new StringBuffer();
		for (String line : changeResult) {
			sb.append(line+"\n");
		}
		editor = new EditorTextArea(sb.toString(),style);
		
		JLabel title = new JLabel(owner.localizedForKey("please_resolve_conflict"),SwingConstants.CENTER);
		title.setBorder(BorderFactory.createEmptyBorder(10,10,10,10));
		
		JPanel controlPanel = new JPanel(new FlowLayout());
		JButton validateButton;
		JButton cancelButton;
		controlPanel.add(validateButton = new JButton(owner.localizedForKey("validate")));
		validateButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				ok();
				dispose();
			}
		});
		controlPanel.add(cancelButton = new JButton(owner.localizedForKey("cancel")));
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				cancel();
				dispose();
			}
		});
		controlPanel.add(validateButton);
		controlPanel.add(cancelButton);
		
		JPanel panel = new JPanel(new BorderLayout());

		panel.add(title,BorderLayout.NORTH);
		panel.add(editor,BorderLayout.CENTER);
		panel.add(controlPanel,BorderLayout.SOUTH);
		
		getContentPane().add(panel);
		
		validate();
		pack();
		setVisible(true);
	}
	
	protected class EditorTextArea extends JEditTextAreaWithHighlights
	{
		public EditorTextArea (String text, TokenMarkerStyle style)
		{
			super();
			disableDefaultMouseWheelListener();
			setTokenMarker(TokenMarker.makeTokenMarker(style));
			setText(text);
			setEditable(true);
			setFont(FontCst.TEXT_FONT);
		}
	}

	public void ok()
	{
		conflictChange.setCustomHandEdition(editor.getText());
		conflictChange.setMergeChangeAction(MergeChangeAction.CustomEditing);
	}

	public void cancel()
	{
		conflictChange.setMergeChangeAction(previous);
	}
}
