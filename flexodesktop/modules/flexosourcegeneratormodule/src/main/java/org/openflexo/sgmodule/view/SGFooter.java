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
package org.openflexo.sgmodule.view;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.FocusEvent;
import java.awt.event.FocusListener;

import javax.swing.BorderFactory;
import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.sg.SourceRepository;
import org.openflexo.icon.CGIconLibrary;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.jedit.JEditTextArea;
import org.openflexo.jedit.JEditTextArea.CursorPositionListener;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.sgmodule.controller.SGController;

public class SGFooter extends JPanel implements GraphicalFlexoObserver,FocusListener, CursorPositionListener
{

	/**
	 * 
	 */
	private final SGController _sgController;
	private final JLabel statusLabel;
	private final JPanel statusCountPanel;
	private final JPanel editorInfoPanel;

	private final JLabel generationModifiedLabel;
	private final JLabel diskModifiedLabel;
	private final JLabel conflictsLabel;
	private final JLabel needsMemoryGenerationLabel;
	private final JLabel needsReinjectionLabel;
	private final JLabel errorsLabel;

	private final JLabel cursorPositionLabel;
	private final JLabel editorStatusLabel;

	public SGFooter(SGController sgController)
	{
		super(new GridLayout(1,3));
		_sgController = sgController;
		statusLabel = new JLabel("012345678901234567890123456789012345678901234567890123456789",SwingConstants.LEFT);
		statusLabel.setFont(FlexoCst.MEDIUM_FONT);
		statusLabel.setBorder(BorderFactory.createEmptyBorder(0, 20, 0, 20));
		add(statusLabel);

		statusCountPanel = new JPanel(new FlowLayout());
		generationModifiedLabel = new JLabel("0");
		generationModifiedLabel.setFont(FlexoCst.MEDIUM_FONT);
		statusCountPanel.add(generationModifiedLabel);
		statusCountPanel.add(new JLabel(UtilsIconLibrary.LEFT_MODIFICATION_ICON));
		statusCountPanel.add(Box.createRigidArea(new Dimension(3,16)));
		diskModifiedLabel = new JLabel("5");
		diskModifiedLabel.setFont(FlexoCst.MEDIUM_FONT);
		statusCountPanel.add(diskModifiedLabel);
		statusCountPanel.add(new JLabel(UtilsIconLibrary.RIGHT_MODIFICATION_ICON));
		statusCountPanel.add(Box.createRigidArea(new Dimension(3,16)));
		conflictsLabel = new JLabel("8");
		conflictsLabel.setFont(FlexoCst.MEDIUM_FONT);
		statusCountPanel.add(conflictsLabel);
		statusCountPanel.add(new JLabel(UtilsIconLibrary.CONFLICT_ICON));
		statusCountPanel.add(Box.createRigidArea(new Dimension(3,16)));
		statusCountPanel.add(new JLabel(IconLibrary.SEPARATOR_ICON));
		statusCountPanel.add(Box.createRigidArea(new Dimension(3,16)));
		needsMemoryGenerationLabel = new JLabel("1");
		needsMemoryGenerationLabel.setFont(FlexoCst.MEDIUM_FONT);
		statusCountPanel.add(needsMemoryGenerationLabel);
		statusCountPanel.add(new JLabel(GeneratorIconLibrary.GENERATE_CODE_ICON));
		statusCountPanel.add(Box.createRigidArea(new Dimension(3,16)));
		needsReinjectionLabel = new JLabel("1");
		needsReinjectionLabel.setFont(FlexoCst.MEDIUM_FONT);
		statusCountPanel.add(needsReinjectionLabel);
		statusCountPanel.add(new JLabel(GeneratorIconLibrary.NEEDS_MODEL_REINJECTION_ICON));
		statusCountPanel.add(Box.createRigidArea(new Dimension(3,16)));
		statusCountPanel.add(new JLabel(IconLibrary.SEPARATOR_ICON));
		statusCountPanel.add(Box.createRigidArea(new Dimension(3,16)));
		errorsLabel = new JLabel("0");
		errorsLabel.setFont(FlexoCst.MEDIUM_FONT);
		statusCountPanel.add(errorsLabel);
		statusCountPanel.add(new JLabel(CGIconLibrary.UNFIXABLE_ERROR_ICON));
		add(statusCountPanel);

		editorInfoPanel = new JPanel(new FlowLayout());
		editorInfoPanel.add(new JLabel(IconLibrary.SEPARATOR_ICON));
		editorInfoPanel.add(Box.createRigidArea(new Dimension(3,16)));
		cursorPositionLabel = new JLabel("-:-",SwingConstants.CENTER);
		cursorPositionLabel.setPreferredSize(new Dimension(50,16));
		cursorPositionLabel.setFont(FlexoCst.MEDIUM_FONT);
		editorInfoPanel.add(cursorPositionLabel);
		editorInfoPanel.add(Box.createRigidArea(new Dimension(3,16)));
		editorInfoPanel.add(new JLabel(IconLibrary.SEPARATOR_ICON));
		editorInfoPanel.add(Box.createRigidArea(new Dimension(3,16)));
		editorStatusLabel = new JLabel("");
		editorStatusLabel.setFont(FlexoCst.MEDIUM_FONT);
		editorInfoPanel.add(editorStatusLabel);
		add(editorInfoPanel);
		refreshEditorInfoPanel();

	}

	public void refresh()
	{
		SourceRepository repositoryToConsider = _sgController.getCurrentGeneratedCodeRepository();
		//logger.info("Refresh footer with "+repositoryToConsider);
		boolean displayItemStatus;
		if (repositoryToConsider != null) {
			if (!_sgController.getObservedRepositories().contains(repositoryToConsider)) {
				_sgController.getObservedRepositories().add(repositoryToConsider);
				repositoryToConsider.addObserver(this);
			}
			String repName = "["+repositoryToConsider.getName()+"] ";
			if (!repositoryToConsider.isConnected()) {
				statusLabel.setText(repName+FlexoLocalization.localizedForKey("repository_disconnected"));
				statusLabel.setForeground(Color.BLACK);   
				displayItemStatus = false;
			}
			else {
				if ((_sgController.getProjectGenerator(repositoryToConsider) == null)
						|| !_sgController.getProjectGenerator(repositoryToConsider).hasBeenInitialized()) {
					statusLabel.setText(repName+FlexoLocalization.localizedForKey("code_generation_not_synchronized"));
					displayItemStatus = false;
				}
				else {
					statusLabel.setText(repName+FlexoLocalization.localizedForKey("code_generation_is_synchronized"));
					displayItemStatus = true;
				}
				statusLabel.setForeground(Color.BLACK);       			
			}
		}
		else {
			statusLabel.setText(FlexoLocalization.localizedForKey("no_repository_selected"));
			statusLabel.setForeground(Color.GRAY);
			displayItemStatus = false;
		}

		if (displayItemStatus) {
			generationModifiedLabel.setForeground(Color.BLACK);
			generationModifiedLabel.setText(""+repositoryToConsider.getGenerationModifiedCount());
			diskModifiedLabel.setForeground(Color.BLACK);
			diskModifiedLabel.setText(""+repositoryToConsider.getDiskModifiedCount());
			conflictsLabel.setForeground(Color.BLACK);
			conflictsLabel.setText(""+repositoryToConsider.getConflictsCount());
			needsMemoryGenerationLabel.setForeground(Color.BLACK);
			needsMemoryGenerationLabel.setText(""+repositoryToConsider.getNeedsMemoryGenerationCount());
			needsReinjectionLabel.setForeground(Color.BLACK);
			needsReinjectionLabel.setText(""+repositoryToConsider.getNeedsModelReinjectionCount());
			errorsLabel.setForeground(Color.BLACK);
			errorsLabel.setText(""+repositoryToConsider.getErrorsCount());
		}
		else {
			generationModifiedLabel.setForeground(Color.GRAY);
			generationModifiedLabel.setText("-");
			diskModifiedLabel.setForeground(Color.GRAY);
			diskModifiedLabel.setText("-");
			conflictsLabel.setForeground(Color.GRAY);
			conflictsLabel.setText("-");
			needsMemoryGenerationLabel.setForeground(Color.GRAY);
			needsMemoryGenerationLabel.setText("-");
			needsReinjectionLabel.setForeground(Color.GRAY);
			needsReinjectionLabel.setText("-");
			errorsLabel.setForeground(Color.GRAY);
			errorsLabel.setText("-");
		}

		refreshEditorInfoPanel();

		validate();
		repaint();
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) 
	{
		refresh();
	}

	@Override
	public void focusGained(FocusEvent e)
	{
		if (e.getComponent() instanceof JEditTextArea) {
			((JEditTextArea)e.getComponent()).addToCursorPositionListener(this);
			_activeGenericCodeDisplayer = ((JEditTextArea)e.getComponent());
			refresh();
		}
	}

	@Override
	public void focusLost(FocusEvent e) 
	{
		if (e.getComponent() instanceof JEditTextArea) {
			((JEditTextArea)e.getComponent()).removeFromCursorPositionListener(this);
			if (_activeGenericCodeDisplayer == e.getComponent()) {
				_activeGenericCodeDisplayer = null;
			}
			refresh();
		}
	}

	private JEditTextArea _activeGenericCodeDisplayer;

	@Override
	public void positionChanged(int newPosX, int newPosY) 
	{
		refreshEditorInfoPanel();
	}	


	private void refreshEditorInfoPanel()
	{
		//logger.info("refreshEditorInfoPanel()");
		if (_activeGenericCodeDisplayer == null) {
			cursorPositionLabel.setText("-");
			editorStatusLabel.setText(FlexoLocalization.localizedForKey("no_edition"));
		}
		else {
			cursorPositionLabel.setText(_activeGenericCodeDisplayer.getCursorY()+":"+_activeGenericCodeDisplayer.getCursorX());
			editorStatusLabel.setText((_activeGenericCodeDisplayer.isEditable()?FlexoLocalization.localizedForKey("edition"):FlexoLocalization.localizedForKey("read_only")));
		}
	}

}