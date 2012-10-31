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
package org.openflexo.fge.drawingeditor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.logging.Logger;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;

public abstract class MyDrawingImpl extends MyDrawingElementImpl<MyDrawing, MyDrawingGraphicalRepresentation> implements MyDrawing {

	private static final Logger logger = FlexoLogger.getLogger(MyDrawingImpl.class.getPackage().getName());

	private static XMLMapping mapping;

	private int index;
	private File file = null;
	private EditedDrawing editedDrawing = new EditedDrawing(this);

	private DrawingEditorFactory factory;

	static {
		try {
			mapping = new XMLMapping(new FileResource("Mappings/DrawingMapping.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	// Used by PAMELA, do not use it
	public MyDrawingImpl() {
		super(null);
	}

	// Called for LOAD
	public MyDrawingImpl(DrawingBuilder builder) {
		super(null);
		builder.drawing = this;
		initializeDeserialization();
	}

	@Override
	public MyDrawingImpl getDrawing() {
		return this;
	}

	@Override
	public DrawingEditorFactory getFactory() {
		return factory;
	}

	@Override
	public void setFactory(DrawingEditorFactory factory) {
		this.factory = factory;
		setGraphicalRepresentation(factory.makeNewDrawingGR(editedDrawing));
	}

	@Override
	public String getTitle() {
		if (file != null) {
			return file.getName();
		} else {
			return FlexoLocalization.localizedForKey(TestDrawingEditor.LOCALIZATION, "untitled") + "-" + index;
		}
	}

	@Override
	public EditedDrawing getEditedDrawing() {
		return editedDrawing;
	}

	@Override
	public boolean save() {
		System.out.println("Saving " + file);

		XMLCoder coder = new XMLCoder(mapping);

		try {
			coder.encodeObject(this, new FileOutputStream(file));
			clearChanged();
			logger.info("Succeeded to save: " + file);
			System.out.println("> " + new XMLCoder(mapping).encodeObject(this));
			return true;
		} catch (Exception e) {
			logger.warning("Failed to save: " + file + " unexpected exception: " + e.getMessage());
			e.printStackTrace();
		}
		return false;
	}

	@Override
	public void finalizeDeserialization() {
		// TODO Auto-generated method stub
		super.finalizeDeserialization();

		/*for (MyDrawingElement e : childs) {
			getDrawing().getEditedDrawing().addDrawable(e, this);
		}*/
		_finalizeDeserializationFor(this);

		editedDrawing.getDrawingGraphicalRepresentation().startConnectorObserving();

	}

	private void _finalizeDeserializationFor(MyDrawingElement<?, ?> element) {
		// element.getGraphicalRepresentation().resetToDefaultIdentifier();
		for (MyDrawingElement<?, ?> e : element.getChilds()) {
			getDrawing().getEditedDrawing().addDrawable(e, element);
			_finalizeDeserializationFor(e);
		}
	}

	public static MyDrawing load(File file) {
		logger.info("Loading " + file);

		XMLDecoder decoder = new XMLDecoder(mapping, new DrawingBuilder());

		try {
			MyDrawingImpl drawing = (MyDrawingImpl) decoder.decodeObject(new FileInputStream(file));
			drawing.file = file;
			drawing.editedDrawing.init();
			logger.info("Succeeded to load: " + file);
			return drawing;
		} catch (Exception e) {
			logger.warning("Failed to load: " + file + " unexpected exception: " + e.getMessage());
			e.printStackTrace();
			return null;
		}
	}

	@Override
	public File getFile() {
		return file;
	}

	@Override
	public void setFile(File file) {
		this.file = file;
	}

	@Override
	public int getIndex() {
		return index;
	}

	@Override
	public void setIndex(int index) {
		this.index = index;
	}

}
