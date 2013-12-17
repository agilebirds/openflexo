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
import java.awt.event.FocusListener;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.openflexo.diff.ComputeDiff;
import org.openflexo.diff.merge.DefaultMergedDocumentType;
import org.openflexo.fps.CVSFile;
import org.openflexo.fps.controller.FPSController;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.swing.diff.DiffPanel;
import org.openflexo.toolbox.TokenMarkerStyle;
import org.openflexo.xml.diff3.XMLDiff3;

public class MergeCodeDisplayer extends CodeDisplayer {

	private static final Logger logger = Logger.getLogger(MergeCodeDisplayer.class.getPackage().getName());

	// private MergeCodeDisplayerComponent _component;

	public MergeCodeDisplayer(CVSFile cvsFile, FPSController controller) {
		super(cvsFile, controller);
	}

	private boolean editable = true;

	public boolean isEditable() {
		return editable;
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
		_component.setEditable(editable);
	}

	@Override
	public JComponent getComponent() {
		return (JComponent) _component;
	}

	protected interface MergeCodeDisplayerComponent extends CodeDisplayerComponent {
		@Override
		void update();
	}

	@Override
	protected CodeDisplayerComponent buildComponent() {
		if (!getFileFormat().isBinary()) {
			if (getCVSFile().getMerge() instanceof XMLDiff3) {
				return _component = new XMLStorageResourceDisplayer();
				// if(!getCVSFile().getMerge().isResolved()){
				// return _component = new XMLStorageResourceMergeCodeDisplayer();
				// }else{ //all conflict are resolved... so we display a diff between current and furure
				// return _component = new XMLStorageResourceMergeResultDisplayer();
				// }
			} else {
				return _component = new ASCIIFileMergeCodeDisplayer();
			}
		} else {
			return super.buildComponent();
		}
	}

	@Override
	public void update() {
		if (_component != null) {
			_component.update();
		}
	}

	protected class XMLStorageResourceMergeResultDisplayer extends JPanel implements MergeCodeDisplayerComponent, Observer {
		private DiffPanel _editor;

		protected XMLStorageResourceMergeResultDisplayer() {
			super(new BorderLayout());
			setName("XML");
			update();
		}

		@Override
		public void setEditedContent(CVSFile file) {
			// Not editable anyway
		}

		@Override
		public String getEditedContent() {
			// Interface: this component is not supposed to be editable
			return null;
		}

		@Override
		public void update() {
			removeAll();

			_editor = new DiffPanel(ComputeDiff.diff(getCVSFile().getMerge().getLeftSource(), getCVSFile().getMerge().getMergedSource()),
					false);
			add(_editor, BorderLayout.CENTER);
			revalidate();
		}

		public void delete() {
			getCVSFile().getMerge().deleteObserver(this);
		}

		@Override
		public void update(Observable o, Object arg) {
			logger.info("update() received in ASCIIFileMergeCodeDisplayer for MergeRecomputed");
		}

		protected TokenMarkerStyle getTokenMarkerStyle() {
			return DefaultMergedDocumentType.getMergedDocumentType(getFileFormat()).getStyle();
		}

		public void setContentSource(ContentSource aContentSource) {
			// Interface
		}

		@Override
		public void addToFocusListener(FocusListener aFocusListener) {
		}

		@Override
		public void setEditable(boolean isEditable) {
		}
	}

	protected class ASCIIFileMergeCodeDisplayer extends JPanel implements MergeCodeDisplayerComponent, Observer {
		private CVSMergeEditor _editor;

		protected ASCIIFileMergeCodeDisplayer() {
			super(new BorderLayout());

			update();
		}

		@Override
		public void setEditedContent(CVSFile file) {
			// Not editable anyway
		}

		@Override
		public String getEditedContent() {
			// Interface: this component is not supposed to be editable
			return null;
		}

		@Override
		public void update() {
			removeAll();

			_editor = new CVSMergeEditor(getCVSFile().getMerge(), getTokenMarkerStyle(),
					FlexoLocalization.localizedForKey("content_on_disk") + " (" + FlexoLocalization.localizedForKey("based_on_revision")
							+ " " + getCVSFile().getRevision() + ")", FlexoLocalization.localizedForKey("content_on_repository") + " ("
							+ FlexoLocalization.localizedForKey("revision") + " " + getCVSFile().getRepositoryRevision() + ")",
					FlexoLocalization.localizedForKey("merged_file_(will_be_written_on_disk)"),
					FlexoLocalization.localizedForKey("no_structural_changes"));
			add(_editor, BorderLayout.CENTER);
			revalidate();
		}

		public void delete() {
			getCVSFile().getMerge().deleteObserver(this);
		}

		@Override
		public void update(Observable o, Object arg) {
			logger.info("update() received in ASCIIFileMergeCodeDisplayer for MergeRecomputed");
		}

		protected TokenMarkerStyle getTokenMarkerStyle() {
			return DefaultMergedDocumentType.getMergedDocumentType(getFileFormat()).getStyle();
		}

		public void setContentSource(ContentSource aContentSource) {
			// Interface
		}

		@Override
		public void addToFocusListener(FocusListener aFocusListener) {
			_editor.addToFocusListener(aFocusListener);
		}

		@Override
		public void setEditable(boolean isEditable) {
			_editor.setEditable(isEditable);
		}

	}

	protected class XMLStorageResourceDisplayer extends JTabbedPane implements MergeCodeDisplayerComponent, Observer {

		private final XMLStorageResourceMergeCodeDisplayer _mergeCode;
		private final XMLStorageResourceMergeResultDisplayer _mergeResult;

		public XMLStorageResourceDisplayer() {
			super();
			_mergeCode = new XMLStorageResourceMergeCodeDisplayer();
			_mergeResult = new XMLStorageResourceMergeResultDisplayer();
			add(_mergeCode);
			add(_mergeResult);
		}

		@Override
		public void addToFocusListener(FocusListener focusListener) {
			_mergeCode.addToFocusListener(focusListener);
			_mergeResult.addToFocusListener(focusListener);

		}

		@Override
		public String getEditedContent() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setEditable(boolean isEditable) {
			// TODO Auto-generated method stub

		}

		@Override
		public void setEditedContent(CVSFile file) {
			// TODO Auto-generated method stub

		}

		@Override
		public void update(Observable arg0, Object arg1) {
			_mergeCode.update(arg0, arg1);
			_mergeResult.update(arg0, arg1);
		}

		@Override
		public void update() {
			_mergeCode.update();
			_mergeResult.update();
		}

	}

	protected class XMLStorageResourceMergeCodeDisplayer extends JPanel implements MergeCodeDisplayerComponent, Observer {
		private XMLDiff3MergeEditor _editor;

		protected XMLStorageResourceMergeCodeDisplayer() {
			super(new BorderLayout());
			setName("Conflicts");
			update();
		}

		@Override
		public void setEditedContent(CVSFile file) {
			// Not editable anyway
		}

		@Override
		public String getEditedContent() {
			// Interface: this component is not supposed to be editable
			return null;
		}

		@Override
		public void update() {
			removeAll();

			_editor = new XMLDiff3MergeEditor((XMLDiff3) getCVSFile().getMerge(), FlexoLocalization.localizedForKey("content_on_disk")
					+ " (" + FlexoLocalization.localizedForKey("based_on_revision") + " " + getCVSFile().getRevision() + ")",
					FlexoLocalization.localizedForKey("content_on_repository") + " (" + FlexoLocalization.localizedForKey("revision") + " "
							+ getCVSFile().getRepositoryRevision() + ")",
					FlexoLocalization.localizedForKey("merged_file_(will_be_written_on_disk)"),
					FlexoLocalization.localizedForKey("no_structural_changes"));
			add(_editor, BorderLayout.CENTER);
			revalidate();
		}

		public void delete() {
			getCVSFile().getMerge().deleteObserver(this);
		}

		@Override
		public void update(Observable o, Object arg) {
			logger.info("update() received in ASCIIFileMergeCodeDisplayer for MergeRecomputed");
		}

		protected TokenMarkerStyle getTokenMarkerStyle() {
			return DefaultMergedDocumentType.getMergedDocumentType(getFileFormat()).getStyle();
		}

		public void setContentSource(ContentSource aContentSource) {
			// Interface
		}

		@Override
		public void addToFocusListener(FocusListener aFocusListener) {
			_editor.addToFocusListener(aFocusListener);
		}

		@Override
		public void setEditable(boolean isEditable) {
			_editor.setEditable(isEditable);
		}

	}

}
