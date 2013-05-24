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
package org.openflexo.sgmodule.view.popup;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.SwingConstants;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.icon.FilesIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.sgmodule.SGCst;
import org.openflexo.sgmodule.controller.SGController;
import org.openflexo.sgmodule.view.CodeDisplayer;
import org.openflexo.view.FlexoDialog;
import org.openflexo.view.listener.FlexoActionButton;

/**
 * @author sylvain
 */
public class CGFileVersionPopup extends FlexoDialog {
	private final Logger logger = FlexoLogger.getLogger(CGFileVersionPopup.class.getPackage().getName());

	private final CGFile _cgFile;
	private final SGController _controller;
	private final ContentSource _contentSource;
	private final CGFileVersionPanel view;

	public CGFileVersionPopup(CGFile file, ContentSource contentSource, SGController controller) {
		super(controller.getFlexoFrame(), getTitle(file, contentSource), false);
		_cgFile = file;
		_contentSource = contentSource;
		_controller = controller;

		view = new CGFileVersionPanel();

		getContentPane().setLayout(new BorderLayout());
		getContentPane().add(view, BorderLayout.CENTER);
		JPanel controlPanel = new JPanel(new FlowLayout());
		JButton button = new JButton();
		button.setText(FlexoLocalization.localizedForKey("close", button));
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				view.delete();
				dispose();
			}
		});
		controlPanel.add(button);
		getContentPane().add(controlPanel, BorderLayout.SOUTH);
		validate();
		pack();
	}

	public class CGFileVersionPanel extends JPanel implements FlexoObserver {
		public CGFileVersionPanel() {
			super(new BorderLayout());
			_cgFile.addObserver(this);
			updateView();
		}

		private GenerationStatus generationStatus = GenerationStatus.Unknown;
		private boolean isEdited = false;

		private void updateView() {
			if (generationStatus == GenerationStatus.Unknown || generationStatus != _cgFile.getGenerationStatus()
					|| generationStatus == GenerationStatus.GenerationError || isEdited != _cgFile.isEdited()) {
				logger.info("CGFileVersionPanel :" + _cgFile.getFileName() + " rebuild view for new status "
						+ _cgFile.getGenerationStatus());
				rebuildView();
				revalidate();
				repaint();
			}

			else {
				if (_header != null) {
					_header.update();
				}
				if (_codeDisplayer != null) {
					_codeDisplayer.update();
				}
			}
		}

		private CodeDisplayer _codeDisplayer;

		private ViewHeader _header;

		protected class ViewHeader extends JPanel {
			JLabel icon;
			JLabel title;
			JLabel subTitle;
			JPanel controlPanel;
			Vector<FlexoActionButton> actionButtons = new Vector<FlexoActionButton>();

			protected ViewHeader() {
				super(new BorderLayout());
				icon = new JLabel(FilesIconLibrary.mediumIconForFileFormat(_cgFile.getFileFormat()));
				icon.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
				add(icon, BorderLayout.WEST);
				title = new JLabel(_cgFile.getFileName(), SwingConstants.LEFT);
				// title.setVerticalAlignment(JLabel.BOTTOM);
				title.setFont(SGCst.HEADER_FONT);
				title.setForeground(Color.BLACK);
				title.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
				subTitle = new JLabel(subTitleForFile(), SwingConstants.LEFT);
				// title.setVerticalAlignment(JLabel.BOTTOM);
				subTitle.setFont(SGCst.SUB_TITLE_FONT);
				subTitle.setForeground(Color.GRAY);
				subTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

				JPanel labelsPanel = new JPanel(new GridLayout(2, 1));
				labelsPanel.add(title);
				labelsPanel.add(subTitle);
				add(labelsPanel, BorderLayout.CENTER);

				update();
			}

			private String subTitleForFile() {
				return _contentSource.getStringRepresentation();
			}

			protected void update() {
				title.setText(_cgFile.getFileName());
				subTitle.setText(subTitleForFile());
				for (FlexoActionButton button : actionButtons) {
					button.update();
				}
			}

		}

		private void rebuildView() {
			removeAll();

			isEdited = _cgFile.isEdited();

			_header = new ViewHeader();

			add(_header, BorderLayout.NORTH);

			generationStatus = _cgFile.getGenerationStatus();
			_codeDisplayer = null;

			if (_cgFile.getGenerationStatus() == GenerationStatus.CodeGenerationNotAvailable) {
				add(new JLabel(FlexoLocalization.localizedForKey("sorry_code_generator_not_available_int_this_version"),
						SwingConstants.CENTER), BorderLayout.CENTER);
				return;
			}

			GenerationAvailableFileResource resource = (GenerationAvailableFileResource) _cgFile.getResource();

			_codeDisplayer = new CodeDisplayer(resource, _contentSource, _controller);
			add(_codeDisplayer.getComponent());

		}

		public SGController getController() {
			return _controller;
		}

		@Override
		public void update(FlexoObservable observable, DataModification dataModification) {
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("CGFileModuleView : RECEIVED " + dataModification + " for " + observable);
			}
			updateView();
		}

		public void delete() {
			logger.info("CGFileVersionPopup deleted");
			_cgFile.deleteObserver(this);
			_controller.deletePopupShowingFileVersion(_cgFile, _contentSource);
		}

	}

	public CGFile getRepresentedObject() {
		return _cgFile;
	}

	@Override
	public String getTitle() {
		return getTitle(_cgFile, _contentSource);
	}

	private static String getTitle(CGFile file, ContentSource contentSource) {
		return file.getResource().getFile().getName() + " [" + contentSource.getStringRepresentation() + "]";
	}

}
