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

import java.awt.FlowLayout;

import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.xml.diff3.UnresolvedMoveConflict;

public class UnresolvedMoveConflictView extends UnresolvedConflictView {

	private JPanel _choicePanel;
	private JPanel _descriptionPanel;

	public UnresolvedMoveConflictView(UnresolvedMoveConflict model) {
		super(model);
	}

	@Override
	public UnresolvedMoveConflict getModel() {
		return (UnresolvedMoveConflict) super.getModel();
	}

	@Override
	public JPanel getChoicePane() {
		refreshChoicePanel();
		return _choicePanel;
	}

	@Override
	public void refreshChoicePanel() {
		if (_choicePanel == null) {
			_choicePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
		} else {
			_choicePanel.removeAll();
			_choicePanel.setLayout(new FlowLayout(FlowLayout.CENTER));
		}
		if (getModel().getSolveAction() == null) {
			_choicePanel.add(new JLabel("Please, make a choice."));
		} else {
			_choicePanel.setBackground(findBackgroundColor());
			_choicePanel.add(new JLabel("You choose :"));
			if (getModel().getSolveAction().equals(getModel().getKeepYourChangeAction())) {
				_choicePanel.add(new JLabel("Move to " + getModel().getParent1Name()));
			} else {
				_choicePanel.add(new JLabel("Move to " + getModel().getParent2Name()));
			}
		}
		_choicePanel.validate();
	}

	@Override
	public JPanel getDescriptionPane() {
		if (_descriptionPanel == null) {
			_descriptionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			_descriptionPanel.add(new JLabel(getModel().getMovedContentName() + " has moved in 2 differents way."));
		}
		return _descriptionPanel;
	}

	@Override
	public String getThirdPartyChangeText() {
		return "Move to " + getModel().getParent1Name();
	}

	@Override
	public String getYourChangeText() {
		return "Move to " + getModel().getParent2Name();
	}
}
