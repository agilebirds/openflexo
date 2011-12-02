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
import java.util.logging.Logger;

import javax.swing.JComponent;

import org.openflexo.dgmodule.controller.DGController;
import org.openflexo.diff.merge.DefaultMergedDocumentType;
import org.openflexo.foundation.cg.CGFile;
import org.openflexo.foundation.cg.generator.GeneratedCodeResult;
import org.openflexo.foundation.cg.generator.IFlexoResourceGenerator;
import org.openflexo.foundation.rm.ResourceType;
import org.openflexo.foundation.rm.cg.ASCIIFile;
import org.openflexo.foundation.rm.cg.ContentSource;
import org.openflexo.generator.rm.GenerationAvailableFile;
import org.openflexo.generator.rm.GenerationAvailableFileResource;
import org.openflexo.jedit.JEditTextArea.DisplayContext;
import org.openflexo.jedit.cd.GenericCodeDisplayer;
import org.openflexo.toolbox.FileFormat;

public class DocDisplayer {

	private static final Logger logger = Logger.getLogger(DocDisplayer.class.getPackage().getName());

	protected CodeDisplayerComponent _component;
	private final GenerationAvailableFileResource _resource;
	private final DGController _controller;

	private ContentSource _contentSource;

	public DocDisplayer(GenerationAvailableFileResource resource, ContentSource contentSource, DGController controller) {
		super();
		_resource = resource;
		_contentSource = contentSource;
		buildComponent();
		_controller = controller;
		if (_controller != null) {
			addToFocusListener(_controller.getFooter());
		}
	}

	public DocDisplayer(GenerationAvailableFileResource resource, DGController controller) {
		this(resource, ContentSource.GENERATED_MERGE, controller);
	}

	public GenerationAvailableFileResource getResource() {
		return _resource;
	}

	public IFlexoResourceGenerator getGenerator() {
		if (_resource != null) {
			return _resource.getGenerator();
		}
		return null;
	}

	public CGFile getCGFile() {
		if (_resource != null) {
			return _resource.getCGFile();
		}
		return null;
	}

	public GenerationAvailableFile getResourceData() {
		if (_resource != null) {
			return _resource.getGeneratedResourceData();
		}
		return null;
	}

	public GeneratedCodeResult getGeneratedCode() {
		if (getGenerator() != null) {
			return getGenerator().getGeneratedCode();
		}
		return null;
	}

	public ResourceType getResourceType() {
		return _resource.getResourceType();
	}

	public FileFormat getFileFormat() {
		return _resource.getResourceFormat();
	}

	public JComponent getComponent() {
		return (JComponent) _component;
	}

	public DisplayContext getDisplayContext() {
		if (_component != null) {
			return _component.getDisplayContext();
		}
		return null;
	}

	public void setDisplayContext(DisplayContext context) {
		if ((_component != null) && (context != null)) {
			_component.applyDisplayContext(context);
		}
	}

	protected CodeDisplayerComponent buildComponent() {
		_component = null;

		if (getResourceData() instanceof ASCIIFile) {
			_component = new ASCIIFileCodePanel(_contentSource);
		}
		return _component;
	}

	public void update() {
		if (_component != null) {
			_component.update();
		}
	}

	protected interface CodeDisplayerComponent {
		void update();

		public void setEditable(boolean isEditable);

		public String getEditedContentForKey(String contentKey);

		public void setEditedContent(CGFile file);

		public void setContentSource(ContentSource aContentSource);

		public void addToFocusListener(FocusListener aFocusListener);

		public DisplayContext getDisplayContext();

		public void applyDisplayContext(DisplayContext context);
	}

	protected String getASCIIContent(ContentSource contentSource) {
		return getASCIIContentForFile(getCGFile(), contentSource);
	}

	protected String getASCIIContentForFile(CGFile aFile, ContentSource contentSource) {
		if ((aFile != null) && (aFile.getResource() != null) && (aFile.getResource().getGeneratedResourceData() != null)
				&& (aFile.getResource().getGeneratedResourceData() instanceof ASCIIFile)) {
			return ((ASCIIFile) aFile.getResource().getGeneratedResourceData()).getContent(contentSource);
		}
		return null;
	}

	protected class ASCIIFileCodePanel extends GenericCodeDisplayer implements CodeDisplayerComponent {
		private ContentSource contentSource;

		protected ASCIIFileCodePanel(ContentSource contentSource) {
			super(getASCIIContent(contentSource), DefaultMergedDocumentType.getMergedDocumentType(getFileFormat()).getStyle());
			this.contentSource = contentSource;
		}

		@Override
		public void update() {
			setTextKeepDisplayContext(getASCIIContent(contentSource));
		}

		@Override
		public String getEditedContentForKey(String contentKey) {
			return getText();
		}

		@Override
		public void setEditedContent(CGFile file) {
			String newContent = getASCIIContentForFile(file, contentSource);
			if (newContent != null) {
				setTextKeepDisplayContext(newContent);
			}
		}

		@Override
		public void setContentSource(ContentSource aContentSource) {
			contentSource = aContentSource;
			update();
		}

		@Override
		public void addToFocusListener(FocusListener aFocusListener) {
			addFocusListener(aFocusListener);
		}

	}

	public ContentSource getContentSource() {
		return _contentSource;
	}

	public void setContentSource(ContentSource aContentSource) {
		ContentSource old = _contentSource;

		if (!old.equals(aContentSource)) {
			_contentSource = aContentSource;
			if (_component != null) {
				_component.setContentSource(aContentSource);
			}
		}
	}

	public void addToFocusListener(FocusListener aFocusListener) {
		if (_component != null) {
			_component.addToFocusListener(aFocusListener);
		}
	}
}
