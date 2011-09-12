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
package org.openflexo.components.wizard;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.view.FlexoDialog;

public class WizardDialog extends FlexoDialog implements ActionListener {
	
	private static final Logger logger = FlexoLogger.getLogger(WizardDialog.class.getPackage().getName());

	protected FlexoWizard wizard;
	
	private JLabel pageTitle;
	
	private JPanel northPanel;
	private JPanel controlPanel;
	
	private JPanel mainPanel;
	private BorderLayout mainPanelLayout;
	
	private JButton previous;
	private JButton next;
	
	private JButton cancel;
	private JButton finish;
	
	public WizardDialog(Frame owner, FlexoWizard wizard) {
		super(owner,true);
		setTitle(wizard.getWizardTitle());
		this.wizard = wizard;
		init();
	}
	
	private void init() {
		pageTitle = new JLabel();
		
		northPanel = new JPanel() {
			@Override
			public void paint(Graphics g) {
				if (wizard.getPageImage()!=null)
					g.drawImage(wizard.getPageImage(), 0, 0, getWidth(), getHeight(), null);
				super.paint(g);
			}
		};
		northPanel.add(pageTitle);
		
		controlPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT,5,5));

		// init the content pane;
		mainPanel = new JPanel(mainPanelLayout=new BorderLayout()){
			
			@Override
			protected void processMouseEvent(MouseEvent e) {
				super.processMouseEvent(e);
				updateControls();
			}
			@Override
			protected void processKeyEvent(KeyEvent e) {
				super.processKeyEvent(e);
				updateControls();
			}
		};
		mainPanel.add(northPanel,BorderLayout.NORTH);
		mainPanel.add(controlPanel,BorderLayout.SOUTH);
		mainPanel.validate();
		
		cancel = new JButton();
		cancel.setText(FlexoLocalization.localizedForKey("cancel", cancel));
		cancel.addActionListener(this);
		finish = new JButton();
		finish.setText(FlexoLocalization.localizedForKey("finish",finish));
		
		controlPanel.add(cancel);
		controlPanel.add(finish);
		// init the buttons
		if (wizard.needsPreviousAndNext()) {
			previous = new JButton();
			previous.setText(FlexoLocalization.localizedForKey("previous",previous));
			next = new JButton();
			next.setText(FlexoLocalization.localizedForKey("next",next));
			controlPanel.add(next);
			controlPanel.add(previous);
		}
		// We add the main panel first
		add(mainPanel);
		// Then we update the central panel, so that the window of the mainpanel is set.
		updateCurrentPage();
		validate();
	}
	
	private void updateCurrentPage() {
		if (mainPanelLayout.getLayoutComponent(BorderLayout.CENTER)!=null)
			mainPanel.remove(mainPanelLayout.getLayoutComponent(BorderLayout.CENTER));
		if (wizard.getCurrentPage().getUserInterface()==null)
			wizard.getCurrentPage().initUserInterface(mainPanel);
		mainPanel.add(wizard.getCurrentPage().getUserInterface());
		pageTitle.setText(wizard.getCurrentPage().getTitle());
		updateControls();
	}
	
	protected void updateControls() {
		if (wizard.needsPreviousAndNext()) {
			if (previous!=null)
				previous.setEnabled(wizard.isPreviousEnabled());
			else
				if (logger.isLoggable(Level.WARNING))
					logger.warning("FlexoWizard.needsPreviousAndNext() returned true but previous button is not initialized");
			if (next!=null)
				next.setEnabled(wizard.isNextEnabled());
			else
				if (logger.isLoggable(Level.WARNING))
					logger.warning("FlexoWizard.needsPreviousAndNext() returned true but next button is not initialized");
		}
		finish.setEnabled(wizard.canFinish());
	}

	@Override
	public void actionPerformed(ActionEvent e) {
		if (e.getSource()==null) {
			return;
		}
		if (e.getSource()==cancel) {
			wizard.performCancel();
			dispose();
			return;
		} else if (e.getSource()==finish) {
			wizard.performFinish();
			dispose();
			return;
		} else if (e.getSource()==next) {
			IWizardPage nextPage = wizard.getNextPage(wizard.getCurrentPage());
			if (nextPage!=null) {
				wizard.setCurrentPage(nextPage);
				updateCurrentPage();
			}
		} else if (e.getSource()==previous) {
			IWizardPage previousPage = wizard.getPreviousPage(wizard.getCurrentPage());
			if (previousPage!=null) {
				wizard.setCurrentPage(previousPage);
				updateCurrentPage();
			}
		}
	}
}
