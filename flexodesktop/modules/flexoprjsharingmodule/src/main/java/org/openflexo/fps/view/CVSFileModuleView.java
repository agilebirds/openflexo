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
package org.openflexo.fps.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextArea;
import javax.swing.SwingConstants;

import org.openflexo.FlexoCst;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.CVSFile.FileContentEditor;
import org.openflexo.fps.CVSStatus;
import org.openflexo.fps.FPSCst;
import org.openflexo.fps.action.CommitFiles;
import org.openflexo.fps.action.EditCVSFile;
import org.openflexo.fps.action.MarkAsMergedFiles;
import org.openflexo.fps.action.RevertToSavedCVSFile;
import org.openflexo.fps.action.SaveCVSFile;
import org.openflexo.fps.action.UpdateFiles;
import org.openflexo.fps.controller.FPSController;
import org.openflexo.icon.FilesIconLibrary;
import org.openflexo.icon.IconLibrary;
import org.openflexo.icon.UtilsIconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.toolbox.ImageIconResource;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.listener.FlexoActionButton;

/**
 * @author sylvain
 */
public class CVSFileModuleView extends JPanel implements ModuleView<CVSFile>, FlexoObserver, FlexoActionSource, FileContentEditor, Observer {
	private final Logger logger = FlexoLogger.getLogger(CVSFileModuleView.class.getPackage().getName());

	protected CVSFile _cvsFile;
	private final FPSController _controller;

	public CVSFileModuleView(CVSFile cvsFile, FPSController controller) {
		super(new BorderLayout());
		_controller = controller;
		_cvsFile = cvsFile;
		_cvsFile.addObserver(this);
		updateView(true);
	}

	private CVSStatus cvsStatus = CVSStatus.Unknown;
	private boolean isEdited = false;

	public void refresh() {
		updateView(true);
	}

	private void updateView(boolean forceRebuild) {
		if (forceRebuild || cvsStatus == CVSStatus.Unknown || cvsStatus != _cvsFile.getStatus() || isEdited != _cvsFile.isEdited()) {
			logger.fine("CVSFileModuleView :" + _cvsFile.getFileName() + " rebuild view for new status " + _cvsFile.getStatus());
			rebuildView();
			_header.update();
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

	public static ImageIcon bigIconForFile(CVSFile file) {
		ResourceType resourceType = file.getResourceType();
		return FilesIconLibrary.mediumIconForFileFormat(resourceType.getFormat());
	}

	protected class ViewHeader extends JPanel {
		JLabel icon;
		JLabel title;
		JLabel subTitle;
		JLabel statusIcon;
		JLabel mergeStatusIcon;
		JPanel titlePanel;
		JPanel subTitlePanel;
		JPanel mergeInfoPanel;
		JLabel mergeInfo;

		JPanel controlPanel;
		Vector<FlexoActionButton> actionButtons = new Vector<FlexoActionButton>();

		protected ViewHeader() {
			super(new BorderLayout());
			icon = new JLabel(bigIconForFile(_cvsFile));
			icon.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			add(icon, BorderLayout.WEST);
			title = new JLabel(_cvsFile.getFileName(), SwingConstants.LEFT);
			// title.setVerticalAlignment(JLabel.BOTTOM);
			title.setFont(FPSCst.HEADER_FONT);
			title.setForeground(Color.BLACK);
			title.setBorder(BorderFactory.createEmptyBorder(5, 0, 0, 5));
			subTitle = new JLabel(subTitleForFile(), SwingConstants.LEFT);
			// title.setVerticalAlignment(JLabel.BOTTOM);
			subTitle.setFont(FPSCst.SUB_TITLE_FONT);
			subTitle.setForeground(Color.GRAY);
			subTitle.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));

			titlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			statusIcon = new JLabel();
			// statusIcon.setVerticalAlignment(JLabel.BOTTOM);
			statusIcon.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 5));
			titlePanel.add(statusIcon);
			titlePanel.add(title);

			subTitlePanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			statusIcon = new JLabel();
			mergeStatusIcon = new JLabel();
			mergeStatusIcon.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
			subTitlePanel.add(mergeStatusIcon);
			subTitlePanel.add(subTitle);

			mergeInfoPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 0, 0));
			mergeInfo = new JLabel(mergeInfoForFile(), SwingConstants.LEFT);
			mergeInfo.setFont(FPSCst.SUB_TITLE_FONT);
			mergeInfo.setForeground(Color.BLACK);
			mergeInfo.setBorder(BorderFactory.createEmptyBorder(0, 5, 0, 5));
			mergeInfoPanel.add(mergeInfo);

			JPanel labelsPanel = new JPanel(new GridLayout(3, 1));
			labelsPanel.add(titlePanel);
			labelsPanel.add(subTitlePanel);
			labelsPanel.add(mergeInfoPanel);
			add(labelsPanel, BorderLayout.CENTER);

			controlPanel = new JPanel(new FlowLayout());

			if (isEdited) {
				FlexoActionButton saveAction = new FlexoActionButton(SaveCVSFile.actionType, "save", CVSFileModuleView.this,
						getController());
				FlexoActionButton revertToSavedAction = new FlexoActionButton(RevertToSavedCVSFile.actionType, "revert_to_saved",
						CVSFileModuleView.this, getController());
				actionButtons.add(saveAction);
				actionButtons.add(revertToSavedAction);
				controlPanel.add(saveAction);
				controlPanel.add(revertToSavedAction);
			} else {
				FlexoActionButton editFileAction = new FlexoActionButton(EditCVSFile.actionType, "edit", CVSFileModuleView.this,
						getController());
				actionButtons.add(editFileAction);
				controlPanel.add(editFileAction);

				if (_cvsFile.getStatus().isConflicting()) {
					FlexoActionButton markAsMergedFileAction = new FlexoActionButton(MarkAsMergedFiles.actionType, "mark_as_merged",
							CVSFileModuleView.this, getController());
					actionButtons.add(markAsMergedFileAction);
					controlPanel.add(markAsMergedFileAction);
				}

				if (_cvsFile.getStatus().isLocallyModified() && !_cvsFile.getStatus().isConflicting()) {
					FlexoActionButton commitFileAction = new FlexoActionButton(CommitFiles.actionType, "commit", CVSFileModuleView.this,
							getController());
					actionButtons.add(commitFileAction);
					controlPanel.add(commitFileAction);
				}

				if (_cvsFile.getStatus().isRemotelyModified() && !_cvsFile.getStatus().isConflicting()) {
					FlexoActionButton updateFileAction = new FlexoActionButton(UpdateFiles.actionType, "update", CVSFileModuleView.this,
							getController());
					actionButtons.add(updateFileAction);
					controlPanel.add(updateFileAction);
				}
			}

			controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			add(controlPanel, BorderLayout.EAST);

			update();
			validate();
		}

		private void addInfoPanel(Icon icon, String titleString, String textString) {
			JLabel regenerateIcon = new JLabel(icon);
			JLabel title = new JLabel(titleString, SwingConstants.LEFT);
			title.setFont(FlexoCst.BOLD_FONT);
			JTextArea text = new JTextArea(textString);
			text.setBackground(null);
			text.setEditable(false);
			text.setFont(FlexoCst.NORMAL_FONT);
			text.setLineWrap(true);
			text.setWrapStyleWord(true);
			text.setBorder(BorderFactory.createEmptyBorder(5, 30, 10, 30));
			JPanel infoPanel = new JPanel(new VerticalLayout());
			JPanel titlePanel = new JPanel(new FlowLayout());
			titlePanel.add(regenerateIcon);
			titlePanel.add(title);
			infoPanel.add(titlePanel);
			infoPanel.add(text);
			add(infoPanel, BorderLayout.SOUTH);

		}

		private String subTitleForFile() {
			CVSStatus status = _cvsFile.getStatus();
			String returned = "";
			if (isEdited) {
				returned = FlexoLocalization.localizedForKey("edition_of_file_on_disk");
			} else {
				returned = status.getLocalizedStringRepresentation();
				if (_cvsFile.getStatus().isConflicting()) {
					if (_cvsFile.getMerge().isResolved()) {
						returned += ", " + FlexoLocalization.localizedForKey("all_conflicts_are_resolved");
					} else {
						returned += ", " + FlexoLocalization.localizedForKey("some_conflicts_are_still_unresolved");
					}
				}
			}
			return returned;
		}

		private String mergeInfoForFile() {
			if (_cvsFile.getStatus().isConflicting()) {
				String leftChangesIconHTML = ((ImageIconResource) UtilsIconLibrary.LEFT_MODIFICATION_ICON).getHTMLImg();
				String rightChangesIconHTML = ((ImageIconResource) UtilsIconLibrary.RIGHT_MODIFICATION_ICON).getHTMLImg();
				String conflictIconHTML = ((ImageIconResource) UtilsIconLibrary.CONFLICT_ICON).getHTMLImg();
				return "<html>" + rightChangesIconHTML + " " + _cvsFile.getMerge().getRightChangeCount() + " &nbsp; " + leftChangesIconHTML
						+ " " + _cvsFile.getMerge().getLeftChangeCount() + " &nbsp; " + conflictIconHTML + " "
						+ _cvsFile.getMerge().getConflictsChangeCount() + " " + " ("
						+ _cvsFile.getMerge().getResolvedConflictsChangeCount() + "/" + _cvsFile.getMerge().getConflictsChangeCount() + " "
						+ FlexoLocalization.localizedForKey("resolved_plural") + ")" + "</html>";
			} else if (_cvsFile.getStatus().isLocallyModified()) {
				String leftRemovalIconHTML = ((ImageIconResource) UtilsIconLibrary.LEFT_ADDITION_ICON).getHTMLImg();
				String leftAdditionIconHTML = ((ImageIconResource) UtilsIconLibrary.LEFT_REMOVAL_ICON).getHTMLImg();
				String leftModificationIconHTML = ((ImageIconResource) UtilsIconLibrary.LEFT_MODIFICATION_ICON).getHTMLImg();
				String additionAsString = "";
				String removalAsString = "";
				String modificationAsString = "";
				if (_codeDisplayer instanceof DiffCodeDisplayer) {
					additionAsString = "" + ((DiffCodeDisplayer) _codeDisplayer).getAdditionChangeCount();
					removalAsString = "" + ((DiffCodeDisplayer) _codeDisplayer).getRemovalChangeCount();
					modificationAsString = "" + ((DiffCodeDisplayer) _codeDisplayer).getModificationChangeCount();
				} else {
					additionAsString = "?";
					removalAsString = "?";
					modificationAsString = "?";
				}
				return "<html>" + leftModificationIconHTML + " " + modificationAsString + " &nbsp; " + leftAdditionIconHTML + " "
						+ additionAsString + " &nbsp; " + leftRemovalIconHTML + " " + removalAsString + "</html>";
			} else if (_cvsFile.getStatus().isRemotelyModified()) {
				String rightAdditionIconHTML = ((ImageIconResource) UtilsIconLibrary.RIGHT_ADDITION_ICON).getHTMLImg();
				String rightRemovalIconHTML = ((ImageIconResource) UtilsIconLibrary.RIGHT_REMOVAL_ICON).getHTMLImg();
				String rightModificationIconHTML = ((ImageIconResource) UtilsIconLibrary.RIGHT_MODIFICATION_ICON).getHTMLImg();
				String additionAsString = "";
				String removalAsString = "";
				String modificationAsString = "";
				if (_codeDisplayer instanceof DiffCodeDisplayer) {
					additionAsString = "" + ((DiffCodeDisplayer) _codeDisplayer).getAdditionChangeCount();
					removalAsString = "" + ((DiffCodeDisplayer) _codeDisplayer).getRemovalChangeCount();
					modificationAsString = "" + ((DiffCodeDisplayer) _codeDisplayer).getModificationChangeCount();
				} else {
					additionAsString = "?";
					removalAsString = "?";
					modificationAsString = "?";
				}
				return "<html>" + rightModificationIconHTML + " " + modificationAsString + " &nbsp; " + rightAdditionIconHTML + " "
						+ additionAsString + " &nbsp; " + rightRemovalIconHTML + " " + removalAsString + "</html>";
			} else {
				return "";
			}
		}

		protected void update() {
			title.setText(_cvsFile.getFileName());
			if (statusIcon.getIcon() == null) {
				titlePanel.add(statusIcon, 0);
			}
			statusIcon.setIcon(iconForStatus());
			statusIcon.setBorder(BorderFactory.createEmptyBorder(10, 5, 0, 0));
			if (statusIcon.getIcon() == null) {
				titlePanel.remove(statusIcon);
			}
			if (mergeStatusIcon.getIcon() == null) {
				subTitlePanel.add(mergeStatusIcon, 0);
			}
			mergeStatusIcon.setIcon(iconForMergeStatus());
			mergeStatusIcon.setBorder(BorderFactory.createEmptyBorder(5, 5, 0, 5));
			if (mergeStatusIcon.getIcon() == null) {
				subTitlePanel.remove(mergeStatusIcon);
			}
			subTitle.setText(subTitleForFile());
			mergeInfo.setText(mergeInfoForFile());
			for (FlexoActionButton button : actionButtons) {
				button.update();
			}
		}

		private Icon iconForStatus() {
			if (_cvsFile.getStatus() == CVSStatus.UpToDate) {
				return null;
			} else if (_cvsFile.getStatus() == CVSStatus.LocallyModified) {
				return UtilsIconLibrary.LEFT_MODIFICATION_ICON;
			} else if (_cvsFile.getStatus() == CVSStatus.MarkedAsMerged) {
				return UtilsIconLibrary.LEFT_MODIFICATION_ICON;
			} else if (_cvsFile.getStatus() == CVSStatus.LocallyAdded) {
				return UtilsIconLibrary.LEFT_ADDITION_ICON;
			} else if (_cvsFile.getStatus() == CVSStatus.LocallyRemoved) {
				return UtilsIconLibrary.LEFT_REMOVAL_ICON;
			} else if (_cvsFile.getStatus() == CVSStatus.RemotelyModified) {
				return UtilsIconLibrary.RIGHT_MODIFICATION_ICON;
			} else if (_cvsFile.getStatus() == CVSStatus.RemotelyAdded) {
				return UtilsIconLibrary.RIGHT_ADDITION_ICON;
			} else if (_cvsFile.getStatus() == CVSStatus.RemotelyRemoved) {
				return UtilsIconLibrary.RIGHT_REMOVAL_ICON;
			} else if (_cvsFile.getStatus() == CVSStatus.Conflicting) {
				return UtilsIconLibrary.CONFLICT_ICON;
			} else if (_cvsFile.getStatus() == CVSStatus.ConflictingAdded) {
				return UtilsIconLibrary.CONFLICT_ADDITION_ICON;
			} else if (_cvsFile.getStatus() == CVSStatus.ConflictingRemoved) {
				return UtilsIconLibrary.CONFLICT_REMOVAL_ICON;
			} else {
				return IconLibrary.QUESTION_ICON;
			}
		}

		private Icon iconForMergeStatus() {
			if (_cvsFile.getStatus().isConflicting()) {
				if (_cvsFile.getMerge().isResolved()) {
					return UtilsIconLibrary.ACCEPT_ICON;
				} else {
					return UtilsIconLibrary.REFUSE_ICON;
				}
			} else {
				return null;
			}
		}

	}

	private Observable observedMerged = null;

	private void rebuildView() {
		if (observedMerged != null) {
			observedMerged.deleteObserver(this);
			observedMerged = null;
		}
		removeAll();

		isEdited = _cvsFile.isEdited();

		_header = new ViewHeader();

		add(_header, BorderLayout.NORTH);
		validate();
		cvsStatus = _cvsFile.getStatus();
		_codeDisplayer = null;

		if (isEdited) {
			_codeDisplayer = new CodeEditor(_cvsFile, _controller);
			_codeDisplayer.getComponent().validate();
			add(_codeDisplayer.getComponent());
			validate();
			_codeDisplayer.getComponent().requestFocus();
		}

		else {

			if (_cvsFile.getStatus().isConflicting()) {
				_codeDisplayer = new MergeCodeDisplayer(_cvsFile, _controller);
				add(_codeDisplayer.getComponent());
				observedMerged = (Observable) _cvsFile.getMerge();
				observedMerged.addObserver(this);
			}

			else if (_cvsFile.getStatus().isLocallyModified() || _cvsFile.getStatus().isRemotelyModified()) {
				_codeDisplayer = new DiffCodeDisplayer(_cvsFile, _controller);
				add(_codeDisplayer.getComponent());
			}

			else if (_cvsFile.getStatus().isUpToDate()) {
				_codeDisplayer = new CodeDisplayer(_cvsFile, _controller);
				add(_codeDisplayer.getComponent());
			}

			else {
				logger.warning("I should not come here !");
			}
			validate();
		}
	}

	public FPSController getController() {
		return _controller;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("CGFileModuleView : RECEIVED " + dataModification + " for " + observable);
		}
		updateView(true);
	}

	@Override
	public void update(Observable observable, Object dataModification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("CGFileModuleView : RECEIVED " + dataModification + " for " + observable);
		}
		updateView(false);
		getController().getFooter().refresh();
	}

	@Override
	public void deleteModuleView() {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("CVSFileModuleView view deleted");
		}
		getController().removeModuleView(this);
		_cvsFile.deleteObserver(this);
	}

	@Override
	public FlexoPerspective getPerspective() {
		return _controller.getCurrentPerspective();
	}

	@Override
	public CVSFile getRepresentedObject() {
		return _cvsFile;
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
		// Only exception panel requires Flexo scroll scheme
		// Other panels are autonomous
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
	public String getEditedContent() {
		if (isEdited) {
			return ((FileContentEditor) _codeDisplayer).getEditedContent();
		}
		logger.warning("getEditedContent() called for a non edited file");
		return null;
	}

	@Override
	public void setEditedContent(CVSFile file) {
		if (isEdited) {
			((FileContentEditor) _codeDisplayer).setEditedContent(file);
		} else {
			logger.warning("setEditedContent() called for a non edited file");
		}
	}

	@Override
	public FlexoEditor getEditor() {
		return _controller.getEditor();
	}

}
