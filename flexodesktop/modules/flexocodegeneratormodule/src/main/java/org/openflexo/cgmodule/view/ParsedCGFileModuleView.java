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
package org.openflexo.cgmodule.view;

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
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.openflexo.FlexoCst;
import org.openflexo.cgmodule.GeneratorCst;
import org.openflexo.cgmodule.controller.GeneratorController;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGFile.FileContentEditor;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.generator.action.AcceptDiskUpdate;
import org.openflexo.generator.action.AcceptDiskUpdateAndReinjectInModel;
import org.openflexo.generator.action.EditGeneratedFile;
import org.openflexo.generator.action.GenerateSourceCode;
import org.openflexo.generator.action.ReinjectInModel;
import org.openflexo.generator.action.RevertToSavedGeneratedFile;
import org.openflexo.generator.action.SaveGeneratedFile;
import org.openflexo.generator.action.UpdateModel;
import org.openflexo.generator.action.WriteModifiedGeneratedFiles;
import org.openflexo.generator.cg.CGJavaFile;
import org.openflexo.icon.FilesIconLibrary;
import org.openflexo.icon.GeneratorIconLibrary;
import org.openflexo.icon.IconLibrary;
import org.openflexo.jedit.JEditTextArea.DisplayContext;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.listener.FlexoActionButton;

/**
 * @author sylvain
 */
public class ParsedCGFileModuleView extends JPanel implements ModuleView<CGFile>, FlexoObserver, FlexoActionSource, FileContentEditor {
	private final Logger logger = FlexoLogger.getLogger(ParsedCGFileModuleView.class.getPackage().getName());

	protected CGFile _cgFile;
	protected GeneratorController _controller;
	private boolean isEdited = false;

	private static final ImageIcon CG_PERSPECTIVE_ICON = GeneratorIconLibrary.GENERATE_CODE_ICON;

	public ParsedCGFileModuleView(CGFile cgFile, GeneratorController controller) {
		super(new BorderLayout());
		_controller = controller;
		_cgFile = cgFile;
		_cgFile.addObserver(this);
		updateView(true);
	}

	private GenerationStatus generationStatus = GenerationStatus.Unknown;

	public void refresh() {
		updateView(true);
	}

	private void updateView(boolean forceRebuild) {
		// logger.info("updateView() isEdited="+isEdited+" _cgFile.isEdited()="+_cgFile.isEdited());
		if ((forceRebuild) || (generationStatus == GenerationStatus.Unknown) || (generationStatus != _cgFile.getGenerationStatus())
				|| (isEdited != _cgFile.isEdited()) || (generationStatus == GenerationStatus.GenerationError)) {
			logger.fine("CGFileModuleView :" + _cgFile.getFileName() + " rebuild view for new status " + _cgFile.getGenerationStatus());
			rebuildView();
			revalidate();
			repaint();
		}

		else {
			if (_header != null) {
				_header.update();
			}
			if (_javaFileView != null) {
				_javaFileView.update();
			}
		}
	}

	private ParsedJavaFileView _javaFileView;

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
			title.setFont(GeneratorCst.HEADER_FONT);
			title.setForeground(Color.BLACK);
			title.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
			subTitle = new JLabel(subTitleForFile(), SwingConstants.LEFT);
			// title.setVerticalAlignment(JLabel.BOTTOM);
			subTitle.setFont(GeneratorCst.SUB_TITLE_FONT);
			subTitle.setForeground(Color.GRAY);
			subTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

			JPanel labelsPanel = new JPanel(new GridLayout(2, 1));
			labelsPanel.add(title);
			labelsPanel.add(subTitle);
			add(labelsPanel, BorderLayout.CENTER);

			controlPanel = new JPanel(new FlowLayout());

			if (isEdited) {
				FlexoActionButton saveAction = new FlexoActionButton(SaveGeneratedFile.actionType, "save", ParsedCGFileModuleView.this,
						_controller.getEditor());
				FlexoActionButton revertToSavedAction = new FlexoActionButton(RevertToSavedGeneratedFile.actionType, "revert_to_saved",
						ParsedCGFileModuleView.this, _controller.getEditor());
				actionButtons.add(saveAction);
				actionButtons.add(revertToSavedAction);
				controlPanel.add(saveAction);
				controlPanel.add(revertToSavedAction);
			} else if ((_cgFile.getGenerationStatus().isDiskModified()) || (_cgFile.getGenerationStatus().isGenerationModified())
					|| (_cgFile.getGenerationStatus().isConflicting()) || (_cgFile.getGenerationStatus() == GenerationStatus.UpToDate)) {
				FlexoActionButton editFileAction = new FlexoActionButton(EditGeneratedFile.actionType, "edit", ParsedCGFileModuleView.this,
						_controller.getEditor());
				actionButtons.add(editFileAction);
				controlPanel.add(editFileAction);
				FlexoActionButton reinjectInModelAction = new FlexoActionButton(ReinjectInModel.actionType, "reinject_in_model",
						ParsedCGFileModuleView.this, _controller.getEditor());
				actionButtons.add(reinjectInModelAction);
				controlPanel.add(reinjectInModelAction);
				FlexoActionButton updateModelAction = new FlexoActionButton(UpdateModel.actionType, "update_model",
						ParsedCGFileModuleView.this, _controller.getEditor());
				actionButtons.add(updateModelAction);
				controlPanel.add(updateModelAction);
			}

			controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			add(controlPanel, BorderLayout.EAST);

			if (!(_cgFile instanceof CGJavaFile)) {
				addInfoPanel(IconLibrary.UNFIXABLE_WARNING_ICON, FlexoLocalization.localizedForKey("model_reinjection_not_implemented"),
						FlexoLocalization.localizedForKey("model_reinjection_not_implemented_description"));
			}

			if (_cgFile instanceof CGJavaFile) {
				boolean hasJavaParsingError = (((CGJavaFile) _cgFile).getParseException() != null);
				if (hasJavaParsingError) {
					addInfoPanel(
							IconLibrary.UNFIXABLE_ERROR_ICON,
							FlexoLocalization.localizedForKey("parse_exception"),
							FlexoLocalization.localizedForKey("parse_exception_description") + "\n"
									+ FlexoLocalization.localizedForKey("message") + " : "
									+ ((CGJavaFile) _cgFile).getParseException().getMessage());
				}
			}

			if (_cgFile.getGenerationStatus().isGenerationModified()) {
				FlexoActionButton generateAction = new FlexoActionButton(GenerateSourceCode.actionType, "regenerate",
						ParsedCGFileModuleView.this, _controller.getEditor());
				actionButtons.add(generateAction);
				FlexoActionButton writeFileAction = new FlexoActionButton(WriteModifiedGeneratedFiles.actionType, "write_file",
						ParsedCGFileModuleView.this, _controller.getEditor());
				actionButtons.add(writeFileAction);
				JButton switchPerspectiveButton = new JButton(FlexoLocalization.localizedForKey("switch_to_cg_perspective"),
						CG_PERSPECTIVE_ICON);
				switchPerspectiveButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getController().switchToPerspective(getController().CODE_GENERATOR_PERSPECTIVE);
					}
				});
				if (_cgFile.hasVersionOnDisk()) {
					addInfoPanel(IconLibrary.UNFIXABLE_WARNING_ICON, FlexoLocalization.localizedForKey("is_generation_modified_warning"),
							FlexoLocalization.localizedForKey("is_generation_modified_warning_description"), switchPerspectiveButton,
							generateAction, writeFileAction);
				} else {
					addInfoPanel(IconLibrary.UNFIXABLE_WARNING_ICON, FlexoLocalization.localizedForKey("is_new_generation_warning"),
							FlexoLocalization.localizedForKey("is_new_generation_warning_description"), switchPerspectiveButton,
							generateAction, writeFileAction);
				}
			} else if (_cgFile.getGenerationStatus().isDiskModified()) {
				FlexoActionButton acceptDiskVersionAction = new FlexoActionButton(AcceptDiskUpdate.actionType, "accept_disk_version",
						ParsedCGFileModuleView.this, _controller.getEditor());
				actionButtons.add(acceptDiskVersionAction);
				FlexoActionButton acceptAndReinjectAction = new FlexoActionButton(AcceptDiskUpdateAndReinjectInModel.actionType,
						"accept_and_reinject", ParsedCGFileModuleView.this, _controller.getEditor());
				actionButtons.add(acceptAndReinjectAction);
				JButton switchPerspectiveButton = new JButton(FlexoLocalization.localizedForKey("switch_to_cg_perspective"),
						CG_PERSPECTIVE_ICON);
				switchPerspectiveButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getController().switchToPerspective(getController().CODE_GENERATOR_PERSPECTIVE);
					}
				});
				addInfoPanel(IconLibrary.UNFIXABLE_WARNING_ICON, FlexoLocalization.localizedForKey("is_disk_modified_warning"),
						FlexoLocalization.localizedForKey("is_disk_modified_warning_description"), switchPerspectiveButton,
						acceptDiskVersionAction, acceptAndReinjectAction);
			} else if (_cgFile.getGenerationStatus().isConflicting()) {
				JButton switchPerspectiveButton = new JButton(FlexoLocalization.localizedForKey("switch_to_cg_perspective"),
						CG_PERSPECTIVE_ICON);
				switchPerspectiveButton.addActionListener(new ActionListener() {
					@Override
					public void actionPerformed(ActionEvent e) {
						getController().switchToPerspective(getController().CODE_GENERATOR_PERSPECTIVE);
					}
				});
				addInfoPanel(IconLibrary.UNFIXABLE_WARNING_ICON, FlexoLocalization.localizedForKey("is_conflicting_warning"),
						FlexoLocalization.localizedForKey("is_conflicting_warning_description"), switchPerspectiveButton);
			}

			update();
			validate();
		}

		private void addInfoPanel(Icon icon, String titleString, String textString) {
			addInfoPanel(icon, titleString, textString, (JButton[]) null);
		}

		private void addInfoPanel(Icon icon, String titleString, String textString, JButton... buttons) {
			JLabel regenerateIcon = new JLabel(icon);
			JLabel title = new JLabel(titleString, SwingConstants.LEFT);
			title.setFont(FlexoCst.BOLD_FONT);
			JTextArea text = new JTextArea(textString);
			text.setBackground(null);
			text.setEditable(false);
			text.setFont(FlexoCst.NORMAL_FONT);
			text.setLineWrap(true);
			text.setWrapStyleWord(true);
			text.setBorder(BorderFactory.createEmptyBorder(0, 30, 10, 30));
			JPanel infoPanel = new JPanel(new VerticalLayout());
			JPanel titlePanel = new JPanel(new FlowLayout());
			titlePanel.add(regenerateIcon);
			titlePanel.add(title);
			infoPanel.add(titlePanel);
			infoPanel.add(text);
			if ((buttons != null) && (buttons.length > 0)) {
				JPanel buttonPanel = new JPanel(new FlowLayout());
				for (JButton b : buttons) {
					buttonPanel.add(b);
				}
				infoPanel.add(buttonPanel);
			}
			add(infoPanel, BorderLayout.SOUTH);

		}

		private String subTitleForFile() {
			if (isEdited) {
				return FlexoLocalization.localizedForKey("edition_of_file_on_disk");
			}
			if (_cgFile instanceof CGJavaFile) {
				if ((_cgFile.getGenerationStatus().isDiskModified()) || (_cgFile.getGenerationStatus().isGenerationModified())
						|| (_cgFile.getGenerationStatus().isConflicting()) || (_cgFile.getGenerationStatus() == GenerationStatus.UpToDate)) {
					return FlexoLocalization.localizedForKey("file_on_disk");
				}
			}
			return FlexoLocalization.localizedForKey("perspective_not_available");
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
		DisplayContext previousDisplayContext = null;
		if ((_javaFileView != null) && (_javaFileView.getJavaCodeDisplayer() != null)) {
			previousDisplayContext = _javaFileView.getJavaCodeDisplayer().getDisplayContext();

			if (logger.isLoggable(Level.INFO)) {
				logger.info("Rebuild view, display context was: " + previousDisplayContext);
			}
		}

		removeAll();

		isEdited = _cgFile.isEdited();

		_header = new ViewHeader();

		add(_header, BorderLayout.NORTH);
		validate();
		generationStatus = _cgFile.getGenerationStatus();
		_javaFileView = null;

		if (_cgFile instanceof CGJavaFile) {
			if (_cgFile.hasVersionOnDisk()) {
				if ((_cgFile.getGenerationStatus().isDiskModified()) || (_cgFile.getGenerationStatus().isGenerationModified())
						|| (_cgFile.getGenerationStatus().isConflicting()) || (_cgFile.getGenerationStatus() == GenerationStatus.UpToDate)) {
					_javaFileView = new ParsedJavaFileView(((CGJavaFile) _cgFile).getResource(), _controller, isEdited);
					add(_javaFileView, BorderLayout.CENTER);
				}
			}
		}
		// add(new JLabel(FlexoLocalization.localizedForKey("perspective_not_available")),BorderLayout.CENTER);

		if (previousDisplayContext != null) {
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Restore display context: " + previousDisplayContext);
			}
			_javaFileView.getJavaCodeDisplayer().setDisplayContext(previousDisplayContext);
		}

		validate();

		return;
	}

	public GeneratorController getController() {
		return _controller;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("CGFileModuleView : RECEIVED " + dataModification + " for " + observable);
		}
		updateView(false);
	}

	@Override
	public void deleteModuleView() {
		logger.info("CGFileModuleView view deleted");
		getController().removeModuleView(this);
		_cgFile.deleteObserver(this);
	}

	@Override
	public FlexoPerspective<FlexoModelObject> getPerspective() {
		return _controller.MODEL_REINJECTION_PERSPECTIVE;
	}

	@Override
	public CGFile getRepresentedObject() {
		return _cgFile;
	}

	@Override
	public void willHide() {
		// TODO Auto-generated method stub
	}

	@Override
	public void willShow() {
		// TODO Auto-generated method stub
	}

	/**
	 * Returns flag indicating if this view is itself responsible for scroll management When not, Flexo will manage it's own scrollbar for
	 * you
	 * 
	 * @return
	 */
	@Override
	public boolean isAutoscrolled() {
		return true;
	}

	@Override
	public FlexoModelObject getFocusedObject() {
		return getRepresentedObject();
	}

	@Override
	public Vector getGlobalSelection() {
		return null;
	}

	@Override
	public String getEditedContentForKey(String contentKey) {
		if (isEdited) {
			return _javaFileView.getEditedContentForKey(contentKey);
		}
		logger.warning("getEditedContentForKey() called for a non edited file");
		return null;
	}

	@Override
	public void setEditedContent(CGFile file) {
		if (isEdited) {
			_javaFileView.setEditedContent(file);
		} else {
			logger.warning("setEditedContent() called for a non edited file");
		}
	}

	@Override
	public FlexoEditor getEditor() {
		return _controller.getEditor();
	}
}
