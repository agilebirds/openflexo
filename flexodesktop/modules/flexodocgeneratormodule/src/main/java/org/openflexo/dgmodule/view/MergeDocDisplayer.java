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

import java.awt.event.FocusListener;
import java.util.Observable;
import java.util.Observer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JComponent;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.openflexo.dgmodule.controller.DGController;
import org.openflexo.diff.merge.DefaultMergedDocumentType;
import org.openflexo.diff.merge.Merge;
import org.openflexo.diff.merge.Merge.MergeRecomputed;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.rm.cg.ASCIIFile;
import org.openflexo.foundation.rm.cg.ASCIIFileResource;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.jedit.JEditTextArea.DisplayContext;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.toolbox.TokenMarkerStyle;

public class MergeDocDisplayer extends DocDisplayer {

	protected static final Logger logger = Logger.getLogger(MergeDocDisplayer.class.getPackage().getName());

	// private MergeCodeDisplayerComponent _component;

	public MergeDocDisplayer(GenerationAvailableFileResource resource, DGController controller) {
		super(resource, controller);
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
	protected MergeCodeDisplayerComponent buildComponent() {
		_component = null;

		if (getResource() instanceof ASCIIFileResource) {
			_component = new ASCIIFileMergeCodeDisplayer();
		}

		if (_component == null) {
			_component = new ErrorPanel();
		}

		return (MergeCodeDisplayerComponent) _component;
	}

	/*protected MergeCodeDisplayerComponent buildComponent()
	{
		_component = null;
		if (getResourceType() == ResourceType.JAVA_FILE) {
			if (getGeneratedCode() instanceof GeneratedComponent) {
				_component = new ComponentJavaMergeCodeDisplayer();
			}
			else if (getGeneratedCode() instanceof GeneratedJavaClass) {
				_component = new JavaClassMergeCodeDisplayer();
			}
			else if (getCGFile().isMarkedForDeletion()){
				_component = new JavaClassMergeCodeDisplayer();
			}
		}
		else if (getResourceType() == ResourceType.API_FILE) {
			_component = new ComponentAPIMergeCodeDisplayer();
		}
		else if (getResourceType() == ResourceType.WO_FILE) {
			_component = new WOFileMergeCodeDisplayer();
		}
		else {
			if (getGeneratedCode() instanceof GeneratedTextResource) {
				_component = new TextResourceMergeCodeDisplayer();
			}
			else if (getCGFile().isMarkedForDeletion()) {
				_component = new TextResourceMergeCodeDisplayer();
			}
		}
		if (_component == null) {
			_component = new ErrorPanel();
		}
		return _component;
	}*/

	private class ErrorPanel extends JTextArea implements MergeCodeDisplayerComponent {
		protected ErrorPanel() {
			super(FlexoLocalization.localizedForKey("problem_accessing_file_view") + "\nResource: " + getResource() + "\nCode: "
					+ getGeneratedCode() + "\n");
		}

		@Override
		public void update() {
		}

		@Override
		public String getEditedContentForKey(String contentKey) {
			// Interface
			return null;
		}

		@Override
		public void setEditedContent(CGFile file) {
			// Interface
		}

		@Override
		public void setContentSource(ContentSource aContentSource) {
			// Interface
		}

		@Override
		public void addToFocusListener(FocusListener aFocusListener) {
			// TODO Auto-generated method stub

		}

		@Override
		public void applyDisplayContext(DisplayContext context) {

		}

		@Override
		public DisplayContext getDisplayContext() {
			return null;
		}

	}

	@Override
	public void update() {
		if (_component != null) {
			_component.update();
		}
	}

	protected class ASCIIFileMergeCodeDisplayer extends JTabbedPane implements MergeCodeDisplayerComponent, Observer {
		private DGMergeEditor _generationMergeEditor;
		private DGMergeEditor _fileMergeEditor;

		protected ASCIIFileMergeCodeDisplayer() {
			super();
			update();
		}

		@Override
		public void setEditable(boolean isEditable) {
			_generationMergeEditor.setEditable(isEditable);
			_fileMergeEditor.setEditable(isEditable);
		}

		@Override
		public void setEditedContent(CGFile file) {
			// Not editable anyway
		}

		@Override
		public String getEditedContentForKey(String contentKey) {
			// Interface: this component is not supposed to be editable
			return null;
		}

		@Override
		public void update() {
			removeAll();
			_generationMergeEditor = new DGMergeEditor(getGenerationMerge(), getTokenMarkerStyle(),
					FlexoLocalization.localizedForKey("pure_generation"), FlexoLocalization.localizedForKey("last_accepted_version"),
					FlexoLocalization.localizedForKey("merged_generation"), FlexoLocalization.localizedForKey("no_structural_changes"));
			_fileMergeEditor = new DGMergeEditor(getResultFileMerge(), getTokenMarkerStyle(),
					FlexoLocalization.localizedForKey("merged_generation"), FlexoLocalization.localizedForKey("content_on_disk"),
					FlexoLocalization.localizedForKey("merged_file_(will_be_written_on_disk)"),
					FlexoLocalization.localizedForKey("no_structural_changes"));
			getResultFileMerge().addObserver(this);
			add(FlexoLocalization.localizedForKey("generation"), _generationMergeEditor);
			add(FlexoLocalization.localizedForKey("merged_file"), _fileMergeEditor);
			if (getGenerationMerge().isReallyConflicting()) {
				setSelectedIndex(0);
			} else {
				setSelectedIndex(1);
			}
			revalidate();
		}

		public void delete() {
			getGenerationMerge().deleteObserver(this);
			getResultFileMerge().deleteObserver(this);
		}

		@Override
		public void update(Observable o, Object arg) {
			if (o == getResultFileMerge() && arg instanceof MergeRecomputed) {
				logger.info("update() received in ASCIIFileMergeCodeDisplayer for MergeRecomputed");
				// logger.info("left: "+getResultFileMerge().getLeftSource().getSourceString());
				int selectedIndex = getSelectedIndex();
				remove(_fileMergeEditor);
				_fileMergeEditor = new DGMergeEditor(getResultFileMerge(), getTokenMarkerStyle(),
						FlexoLocalization.localizedForKey("merged_generation"), FlexoLocalization.localizedForKey("content_on_disk"),
						FlexoLocalization.localizedForKey("merged_file_(will_be_written_on_disk)"),
						FlexoLocalization.localizedForKey("no_structural_changes"));
				add(FlexoLocalization.localizedForKey("merged_file"), _fileMergeEditor);
				setSelectedIndex(selectedIndex);
			}
		}

		protected Merge getGenerationMerge() {
			return ((ASCIIFile) getResourceData()).getGenerationMerge();
		}

		protected Merge getResultFileMerge() {
			return ((ASCIIFile) getResourceData()).getResultFileMerge();
		}

		protected TokenMarkerStyle getTokenMarkerStyle() {
			return DefaultMergedDocumentType.getMergedDocumentType(getFileFormat()).getStyle();
		}

		@Override
		public void setContentSource(ContentSource aContentSource) {
			// Interface
		}

		@Override
		public void addToFocusListener(FocusListener aFocusListener) {
			_generationMergeEditor.addToFocusListener(aFocusListener);
			_fileMergeEditor.addToFocusListener(aFocusListener);
		}

		@Override
		public void applyDisplayContext(DisplayContext context) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Not implemented");
			}
		}

		@Override
		public DisplayContext getDisplayContext() {
			return null;
		}

	}

}
