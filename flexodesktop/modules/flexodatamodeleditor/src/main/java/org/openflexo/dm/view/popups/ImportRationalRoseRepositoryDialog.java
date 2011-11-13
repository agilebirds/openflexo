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
package org.openflexo.dm.view.popups;

import java.awt.FlowLayout;
import java.awt.GridLayout;

import javax.swing.JPanel;

import org.openflexo.foundation.dm.action.CreateDMRepository;
import org.openflexo.foundation.dm.action.ImportRationalRoseRepository;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.view.FlexoFrame;

/**
 * Popup allowing to choose and import a RationalRose file (*.mdl)
 * 
 * @author sguerin
 * 
 */
public class ImportRationalRoseRepositoryDialog extends AskNewRepositoryDialog {

	public ImportRationalRoseRepositoryDialog(ImportRationalRoseRepository flexoAction, FlexoFrame owner) {
		super(flexoAction, owner);
	}

	public static int displayDialog(ImportRationalRoseRepository flexoAction, FlexoProject project, FlexoFrame owner) {
		flexoAction.setProject(project);
		ImportRationalRoseRepositoryDialog dialog = new ImportRationalRoseRepositoryDialog(flexoAction, owner);
		return dialog.getStatus();
	}

	@Override
	protected void init() {
		choicePanel = new JPanel();
		choicePanel.setLayout(new GridLayout(2, 2));
		choicePanel.add(rationalRoseRepositoryButton);
		choicePanel.add(rationalRoseRepositorySelector);
		JPanel p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
		p.add(packageNameLabel);
		choicePanel.add(p);
		p = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 5));
		packageName.setColumns(25);
		packageName.setText(_flexoAction.getProject().getPrefix() + ".rational.rose");
		p.add(packageName);
		choicePanel.add(p);
		choicePanel.validate();
		rationalRoseRepositoryButton.setSelected(true);
		selectRepositoryType(CreateDMRepository.RATIONAL_ROSE_REPOSITORY);
	}

}
