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

import org.openflexo.xml.diff3.UnresolvedDeleteConflict;

public class UnresolvedDeleteConflictView extends UnresolvedConflictView {

	private JPanel _choicePanel;
	private JPanel _descriptionPanel;

	public UnresolvedDeleteConflictView(UnresolvedDeleteConflict model) {
		super(model);
	}

	@Override
	public UnresolvedDeleteConflict getModel() {
		return (UnresolvedDeleteConflict) super.getModel();
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
				_choicePanel.add(new JLabel(getModel().isMyDeletion() ? "Delete object anyway." : "Restore the deleted object."));
			} else {
				_choicePanel.add(new JLabel(getModel().isMyDeletion() ? "Restore the modified object."
						: "Delete the object (don't care about my changes)."));
			}
		}
		_choicePanel.validate();
	}

	@Override
	public JPanel getDescriptionPane() {
		if (_descriptionPanel == null) {
			_descriptionPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
			_descriptionPanel.add(new JLabel(getModel().getDeletedElementName()
					+ " has been deleted by "
					+ (getModel().isMyDeletion() ? "you. But a third party has modified model under this deleted object."
							: "third party. But you have modified model under this deleted object.")));
		}
		return _descriptionPanel;
	}

	@Override
	public String getThirdPartyChangeText() {
		return getModel().isMyDeletion() ? "RESTORE:\n" + getModel().getXMLStringRepresentation(getModel().getElement1()) : "DELETE "
				+ getModel().getDeletedElementName();
	}

	@Override
	public String getYourChangeText() {
		return getModel().isMyDeletion() ? "DELETE " + getModel().getDeletedElementName() : "KEEP:\n"
				+ getModel().getXMLStringRepresentation(getModel().getElement2());
	}
}
