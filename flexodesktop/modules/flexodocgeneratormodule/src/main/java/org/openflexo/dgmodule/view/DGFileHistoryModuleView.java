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
package org.openflexo.dgmodule.view;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.BorderFactory;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSplitPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import org.openflexo.FlexoCst;
import org.openflexo.dg.file.DGScreenshotFile;
import org.openflexo.dgmodule.DGCst;
import org.openflexo.dgmodule.controller.DGController;
import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoEditor;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.FlexoObserver;
import org.openflexo.foundation.action.FlexoActionSource;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.CGObject;
import org.openflexo.foundation.cg.version.AbstractCGFileVersion;
import org.openflexo.foundation.cg.version.CGFileReleaseVersion.BeforeFirstRelease;
import org.openflexo.foundation.cg.version.action.RevertToHistoryVersion;
import org.openflexo.foundation.cg.version.action.ShowDifferences;
import org.openflexo.foundation.rm.cg.AbstractGeneratedFile;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.foundation.rm.cg.ContentSource.ContentSourceType;
import org.openflexo.foundation.rm.cg.GenerationStatus;
import org.openflexo.generator.ContentRegenerated;
import org.openflexo.generator.exception.GenerationException;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.icon.FilesIconLibrary;
import org.openflexo.jedit.JEditTextArea.DisplayContext;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.selection.SelectionListener;
import org.openflexo.swing.VerticalLayout;
import org.openflexo.view.FlexoPerspective;
import org.openflexo.view.ModuleView;
import org.openflexo.view.listener.FlexoActionButton;

/**
 * @author sylvain
 */
public class DGFileHistoryModuleView extends JPanel implements ModuleView<CGFile>, FlexoObserver, FlexoActionSource, SelectionListener {
	private final Logger logger = FlexoLogger.getLogger(DGFileHistoryModuleView.class.getPackage().getName());

	private final CGFile _cgFile;
	private final DGController _controller;

	private ContentSource _contentSource;
	private CGObject _displayedObject;

	public DGFileHistoryModuleView(CGFile cgFile, DGController controller) {
		super(new BorderLayout());
		_controller = controller;
		_cgFile = cgFile;
		_cgFile.addObserver(this);
		_contentSource = ContentSource.CONTENT_ON_DISK;
		_displayedObject = cgFile;
		controller.getSelectionManager().addToSelectionListeners(this);
		updateView(true);
	}

	public void refresh() {
		updateView(true);
	}

	private void updateView(boolean forceRebuild) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("CGFileHistoryModuleView :" + _cgFile.getFileName() + " refresh view with " + _contentSource);
		}
		if (forceRebuild) {
			rebuildView();
			revalidate();
			repaint();
		}

		else {
			if (_header != null) {
				_header.update();
			}
			if (_codeDisplayer != null) {
				_codeDisplayer.setContentSource(_contentSource);
				_codeDisplayer.update();
			}
		}
	}

	private DocDisplayer _codeDisplayer;
	private FileHistoryBrowserView historyBrowserView;

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
			title.setFont(DGCst.HEADER_FONT);
			title.setForeground(Color.BLACK);
			title.setBorder(BorderFactory.createEmptyBorder(5, 10, 0, 10));
			subTitle = new JLabel(subTitleForFile(), SwingConstants.LEFT);
			// title.setVerticalAlignment(JLabel.BOTTOM);
			subTitle.setFont(DGCst.SUB_TITLE_FONT);
			subTitle.setForeground(Color.GRAY);
			subTitle.setBorder(BorderFactory.createEmptyBorder(0, 10, 5, 10));

			JPanel labelsPanel = new JPanel(new GridLayout(2, 1));
			labelsPanel.add(title);
			labelsPanel.add(subTitle);
			add(labelsPanel, BorderLayout.CENTER);

			controlPanel = new JPanel(new FlowLayout());

			FlexoActionButton revertToVersionAction = new FlexoActionButton(RevertToHistoryVersion.actionType, "revert_to_version",
					DGFileHistoryModuleView.this, _controller);
			actionButtons.add(revertToVersionAction);
			controlPanel.add(revertToVersionAction);

			FlexoActionButton showDifferencesAction = new FlexoActionButton(ShowDifferences.actionType, "show_differences",
					DGFileHistoryModuleView.this, _controller);
			actionButtons.add(showDifferencesAction);
			controlPanel.add(showDifferencesAction);

			controlPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
			add(controlPanel, BorderLayout.EAST);

			update();
		}

		private String subTitleForFile() {
			String returned = _contentSource.getStringRepresentation();
			if (_contentSource.getType() == ContentSourceType.HistoryVersion
					&& _cgFile.getResource().getGeneratedResourceData() instanceof AbstractGeneratedFile) {
				AbstractCGFileVersion fileVersion = ((AbstractGeneratedFile) _cgFile.getResource().getGeneratedResourceData()).getHistory()
						.versionWithId(_contentSource.getVersion());
				if (fileVersion != null) {
					returned += ", " + fileVersion.getDateAsString() + ", " + fileVersion.getUserIdentifier();
				}
			}
			return returned;
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
		if (_codeDisplayer != null) {
			previousDisplayContext = _codeDisplayer.getDisplayContext();
		}
		removeAll();

		_header = new ViewHeader();

		add(_header, BorderLayout.NORTH);

		_codeDisplayer = null;

		if (_cgFile.getGenerationStatus() == GenerationStatus.CodeGenerationNotAvailable) {
			add(new JLabel(FlexoLocalization.localizedForKey("sorry_code_generator_not_available_in_this_version"), SwingConstants.CENTER),
					BorderLayout.CENTER);
			return;
		} else if (_cgFile instanceof DGScreenshotFile) {
			JLabel l = new JLabel();
			l.setText(FlexoLocalization.localizedForKey("sorry_history_not_available_for_screenshots", l));
			l.setHorizontalAlignment(SwingConstants.CENTER);
			add(l, BorderLayout.CENTER);
			return;
		} else {
			if (_cgFile.hasVersionOnDisk()) {
				GenerationAvailableFileResource resource = (GenerationAvailableFileResource) _cgFile.getResource();
				_codeDisplayer = new DocDisplayer(resource, _contentSource, _controller);
				if (previousDisplayContext != null) {
					_codeDisplayer.setDisplayContext(previousDisplayContext);
				}
				historyBrowserView = new FileHistoryBrowserView(getController(), _cgFile, 10);
				JSplitPane splitPane = new JSplitPane(JSplitPane.VERTICAL_SPLIT, _codeDisplayer.getComponent(), historyBrowserView);
				add(splitPane, BorderLayout.CENTER);
			} else {
				JLabel l = new JLabel();
				l.setText(FlexoLocalization.localizedForKey("no_history_yet", l));
				l.setHorizontalAlignment(SwingConstants.CENTER);
				add(l, BorderLayout.CENTER);
			}
		}

	}

	public DGController getController() {
		return _controller;
	}

	@Override
	public void update(FlexoObservable observable, DataModification dataModification) {
		if (logger.isLoggable(Level.FINE)) {
			logger.fine("CGFileModuleView : RECEIVED " + dataModification + " for " + observable);
		}

		if (dataModification instanceof ContentRegenerated) {
			updateView(true);
		} else {
			updateView(false);
		}
	}

	@Override
	public void deleteModuleView() {
		logger.info("CGFileModuleView view deleted");
		getController().removeModuleView(this);
		_cgFile.deleteObserver(this);
	}

	@Override
	public FlexoPerspective getPerspective() {
		return _controller.CODE_GENERATOR_PERSPECTIVE;
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

	protected class ExceptionPanel extends JPanel {
		protected ExceptionPanel(GenerationException exception) {
			super(new VerticalLayout());

			// Top
			JLabel exceptionLabel = new JLabel(FlexoLocalization.localizedForKey("exception"));
			exceptionLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			JTextField exceptionTF = new JTextField(exception.getLocalizedMessage());
			exceptionTF.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			JPanel topPanel = new JPanel(new BorderLayout());
			topPanel.add(exceptionLabel, BorderLayout.WEST);
			topPanel.add(exceptionTF, BorderLayout.CENTER);
			add(topPanel);

			// Details
			JLabel detailsLabel = new JLabel(FlexoLocalization.localizedForKey("details"), SwingConstants.LEFT);
			detailsLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			JTextArea detailsTA = new JTextArea(exception.getDetails());
			detailsTA.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			detailsTA.setFont(FlexoCst.MEDIUM_FONT);
			add(detailsLabel);
			add(detailsTA);

			// Stack trace
			JLabel stLabel = new JLabel(FlexoLocalization.localizedForKey("stacktrace"), SwingConstants.LEFT);
			stLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			String stackTrace = null;
			if (exception.getTargetException() != null) {
				StringWriter w = new StringWriter();
				exception.getTargetException().printStackTrace(new PrintWriter(w));
				stackTrace = w.toString();
			} else {
				stackTrace = FlexoLocalization.localizedForKey("stack_trace_not_available");
			}
			JTextArea stackTraceTA = new JTextArea(stackTrace);
			stackTraceTA.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
			stackTraceTA.setFont(FlexoCst.CODE_FONT);
			stackTraceTA.setForeground(Color.DARK_GRAY);
			add(stLabel);
			add(stackTraceTA);

			// Cause stack trace
			if (exception.getTargetException() != null && exception.getTargetException().getCause() != null) {
				JLabel causeSTLabel = new JLabel(FlexoLocalization.localizedForKey("cause_stacktrace"), SwingConstants.LEFT);
				causeSTLabel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				String causeStackTrace = null;
				StringWriter w = new StringWriter();
				exception.getTargetException().getCause().printStackTrace(new PrintWriter(w));
				causeStackTrace = w.toString();
				JTextArea causeStackTraceTA = new JTextArea(causeStackTrace);
				causeStackTraceTA.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
				causeStackTraceTA.setFont(FlexoCst.CODE_FONT);
				causeStackTraceTA.setForeground(Color.DARK_GRAY);
				add(causeSTLabel);
				add(causeStackTraceTA);
			}

			validate();
		}

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
		return _displayedObject;
	}

	@Override
	public Vector getGlobalSelection() {
		return null;
	}

	@Override
	public void fireBeginMultipleSelection() {
	}

	@Override
	public void fireEndMultipleSelection() {
	}

	@Override
	public void fireObjectDeselected(FlexoModelObject object) {
	}

	@Override
	public void fireObjectSelected(FlexoModelObject object) {
		if (object == _cgFile) {
			_contentSource = ContentSource.CONTENT_ON_DISK;
			_displayedObject = _cgFile;
			updateView(false);
		}
		if (object instanceof AbstractCGFileVersion && !(object instanceof BeforeFirstRelease)) {
			if (((AbstractCGFileVersion) object).getCGFile() == _cgFile) {
				_contentSource = ContentSource.getContentSource(ContentSourceType.HistoryVersion,
						((AbstractCGFileVersion) object).getVersionId());
				_displayedObject = (AbstractCGFileVersion) object;
				updateView(false);
			}
		}
	}

	@Override
	public void fireResetSelection() {
	}

	@Override
	public FlexoEditor getEditor() {
		return _controller.getEditor();
	}

}
