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
package org.openflexo.xml.diff3.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;

import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.xml.diff3.UnresolvedAttributesConflict;
import org.openflexo.xml.diff3.UnresolvedConflict;
import org.openflexo.xml.diff3.UnresolvedDeleteConflict;
import org.openflexo.xml.diff3.UnresolvedInsertionConflict;
import org.openflexo.xml.diff3.UnresolvedMoveConflict;
import org.openflexo.xml.diff3.UnresolvedTextConflict;


public abstract class UnresolvedConflictView extends JPanel implements Observer{

	private final UnresolvedConflict _model;
	private static final Color MANUAL_SOLVED_COLOR = new Color(57,206,41);
	private static final Color AUTO_SOLVED_COLOR = new Color(220,220,220);
	private static final Color CONFLICT_COLOR = new Color(231,57,74);
	
	private JPanel _thirdPartyChangePanel;
	private JPanel _yourChangePanel;
	
	protected UnresolvedConflictView(UnresolvedConflict model){
		super(new BorderLayout());
		_model = model;
		_model.addObserver(this);
		setBackground(findBackgroundColor());
		setBorder(BorderFactory.createLineBorder(findBackgroundColor(), 2));
		add(getDescriptionPane(),BorderLayout.NORTH);
		
		JPanel seePane = new JPanel(new GridLayout(1,2));
		getYourChangePane().setBorder(BorderFactory.createTitledBorder("Your change"));
		getThirdPartyChangePane().setBorder(BorderFactory.createTitledBorder("Third party change"));
		seePane.add(getYourChangePane());
		seePane.add(getThirdPartyChangePane());
		//getYourChangePane().setPreferredSize(new Dimension(getPreferredSize().width/2,-1));
		//getThirdPartyChangePane().setPreferredSize(new Dimension(getPreferredSize().width/2,-1));
		add(seePane,BorderLayout.CENTER);
		
		JPanel solutionPane = new JPanel(new BorderLayout());
		
		if(model.getIsManualChoice() || !model.isSolved()){
			JPanel buttonPane = new JPanel(new GridLayout(1,2));
			JButton keepYourChange = new JButton("keep your change");
			//keepYourChange.setBorder(BorderFactory.createEtchedBorder());
			keepYourChange.setIcon(UtilsIconLibrary.LEFT_UPDATE_ICON);
			keepYourChange.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					getModel().setSolveAction(getModel().getKeepYourChangeAction(),true);
				}
				
			});
			
			JButton discardYourChange = new JButton("discard your change");
			//discardYourChange.setBorder(BorderFactory.createEtchedBorder());
			discardYourChange.setIcon(UtilsIconLibrary.RIGHT_UPDATE_ICON);
			discardYourChange.addActionListener(new ActionListener(){

				@Override
				public void actionPerformed(ActionEvent e) {
					getModel().setSolveAction(getModel().getDiscardYourChangeAction(),true);
				}
				
			});
			JPanel keepYourChangePane = new JPanel(new FlowLayout(FlowLayout.CENTER));
			keepYourChangePane.add(keepYourChange);
			JPanel discardYourChangePane = new JPanel(new FlowLayout(FlowLayout.CENTER));
			discardYourChangePane.add(discardYourChange);
			buttonPane.add(keepYourChangePane);
			buttonPane.add(discardYourChangePane);
			solutionPane.add(buttonPane,BorderLayout.NORTH);
		}else{
			solutionPane.add(new JLabel("Auto-resolved"),BorderLayout.NORTH);
		}
		solutionPane.add(getChoicePane(),BorderLayout.CENTER);
		add(solutionPane,BorderLayout.SOUTH);
		validate();
	}

	public abstract JPanel getChoicePane();
	public abstract JPanel getDescriptionPane();
	public abstract String getYourChangeText();
	public abstract String getThirdPartyChangeText();
	public abstract void refreshChoicePanel();
	
	@Override
	public void update(Observable arg0, Object arg1) {
		setBorder(BorderFactory.createLineBorder(findBackgroundColor(), 2));
		refreshChoicePanel();
	}
	
	protected Color findBackgroundColor(){
		if(_model.isSolved()) {
			return _model.getIsManualChoice()?MANUAL_SOLVED_COLOR:AUTO_SOLVED_COLOR;
		}
		return CONFLICT_COLOR;
	}
	
	public UnresolvedConflict getModel(){
		return _model;
	}
	
	public static UnresolvedConflictView getView(UnresolvedConflict model){
		if(model instanceof UnresolvedAttributesConflict) {
			return new UnresolvedAttributeConflictView((UnresolvedAttributesConflict)model);
		}
		if(model instanceof UnresolvedTextConflict) {
			return new UnresolvedTextConflictView((UnresolvedTextConflict)model);
		}
		if(model instanceof UnresolvedDeleteConflict) {
			return new UnresolvedDeleteConflictView((UnresolvedDeleteConflict)model);
		}
		if(model instanceof UnresolvedMoveConflict) {
			return new UnresolvedMoveConflictView((UnresolvedMoveConflict)model);
		}
		if(model instanceof UnresolvedInsertionConflict) {
			return new UnresolvedInsertionConflictView((UnresolvedInsertionConflict)model);
		}
		return null;
	}
	
	protected String truncated(String s){
		if(s==null) {
			return "";
		}
		if(s.length()<51) {
			return s;
		}
		return s.substring(0,50)+"...";
	}
	
	private JPanel getThirdPartyChangePane() {
		if(_thirdPartyChangePanel==null){
			_thirdPartyChangePanel = new JPanel(new BorderLayout());
			JTextArea text = new JTextArea();
			text.setText(getThirdPartyChangeText());
			JScrollPane scrollPane = new JScrollPane(text);
			//scrollPane.setPreferredSize(new Dimension(50,250));
			scrollPane.getViewport().setPreferredSize(new Dimension((getPreferredSize().width/2)-scrollPane.getVerticalScrollBar().getWidth(),Math.min(200, Math.max(200, getPreferredSize().height))));
			_thirdPartyChangePanel.add(scrollPane,BorderLayout.CENTER);
		}
		return _thirdPartyChangePanel;
	}

	private JPanel getYourChangePane() {
		if(_yourChangePanel==null){
			_yourChangePanel = new JPanel(new BorderLayout());
			JTextArea text = new JTextArea();
			text.setText(getYourChangeText());
			JScrollPane scrollPane = new JScrollPane(text);
			//scrollPane.setPreferredSize(new Dimension(50,250));
			scrollPane.getViewport().setPreferredSize(new Dimension((getPreferredSize().width/2)-scrollPane.getVerticalScrollBar().getWidth(),Math.min(200, Math.max(200, getPreferredSize().height))));
			_yourChangePanel.add(scrollPane,BorderLayout.CENTER);
			if(scrollPane.getVerticalScrollBar()!=null) {
				scrollPane.getVerticalScrollBar().setValue(0);
			}
			scrollPane.getViewport().setViewPosition(new Point(0,0));
		}
		return _yourChangePanel;
	}
}

