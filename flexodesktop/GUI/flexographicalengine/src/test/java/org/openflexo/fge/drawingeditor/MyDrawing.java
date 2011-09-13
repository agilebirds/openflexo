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

import org.openflexo.toolbox.FileResource;
import org.openflexo.xmlcode.XMLCoder;
import org.openflexo.xmlcode.XMLDecoder;
import org.openflexo.xmlcode.XMLMapping;

import org.openflexo.localization.FlexoLocalization;
import org.openflexo.logging.FlexoLogger;

public class MyDrawing extends MyDrawingElement {

	private static final Logger logger = FlexoLogger.getLogger(MyDrawing.class.getPackage().getName());

	private static XMLMapping mapping;
	
	private static int totalOccurences = 0;
	
	private int index;
	
	static {
		try {
			mapping = new XMLMapping(new FileResource("Mappings/DrawingMapping.xml"));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public File file = null;
	
	private EditedDrawing editedDrawing;
	
	// Called for NEW
	public static MyDrawing makeNewDrawing()
	{
		totalOccurences++;
		MyDrawing returned = new MyDrawing();
		returned.index = totalOccurences;
		returned.editedDrawing.init();
		return returned;
	}
	
	private MyDrawing()
	{
		super(null);
		_drawing = this;
		editedDrawing = new EditedDrawing(this);
	}
	
	// Called for LOAD
	public MyDrawing(DrawingBuilder builder)
	{
		this();
		builder.drawing = editedDrawing;
		initializeDeserialization();
	}
	
	public String getTitle()
	{
		if (file != null) return file.getName();
		else return FlexoLocalization.localizedForKey("untitled")+"-"+index;
	}
	
	@Override
	public MyDrawingGraphicalRepresentation getGraphicalRepresentation()
	{
		return editedDrawing.getDrawingGraphicalRepresentation();
	}

	public void setGraphicalRepresentation(MyDrawingGraphicalRepresentation aGR)
	{
		aGR.setDrawable(this);
		editedDrawing.setDrawingGraphicalRepresentation(aGR);
	}

	public EditedDrawing getEditedDrawing()
	{
		return editedDrawing;
	}
	
	public void save()
	{
		System.out.println("Saving "+file);
		
		XMLCoder coder = new XMLCoder(mapping);
		
		try {
			coder.encodeObject(this,new FileOutputStream(file));
			logger.info("Succeeded to save: "+file);
			System.out.println("> "+(new XMLCoder(mapping)).encodeObject(this));
		} catch (Exception e) {
			logger.warning("Failed to save: "+file+" unexpected exception: "+e.getMessage());
			e.printStackTrace();
		}
		
	}
	
	public static MyDrawing load(File file)
	{
		logger.info("Loading "+file);
		
		XMLDecoder decoder = new XMLDecoder(mapping,new DrawingBuilder());
		
		try {
			MyDrawing drawing = (MyDrawing)decoder.decodeObject(new FileInputStream(file));
			drawing.file = file;
			drawing.editedDrawing.init();
			logger.info("Succeeded to load: "+file);
			return drawing;
		} catch (Exception e) {
			logger.warning("Failed to load: "+file+" unexpected exception: "+e.getMessage());
			e.printStackTrace();
			return null;
		}
	}
	
	public static class DrawingBuilder
	{
		public EditedDrawing drawing;
	}
	
	@Override
	public void finalizeDeserialization()
	{
		// TODO Auto-generated method stub
		super.finalizeDeserialization();
		
		/*for (MyDrawingElement e : childs) {
			getDrawing().getEditedDrawing().addDrawable(e, this);
		}*/
		_finalizeDeserializationFor(this);
		
		editedDrawing.getDrawingGraphicalRepresentation().startConnectorObserving();
		
	}

	private void _finalizeDeserializationFor(MyDrawingElement element)
	{
		//element.getGraphicalRepresentation().resetToDefaultIdentifier();
		for (MyDrawingElement e : element.getChilds()) {
			getDrawing().getEditedDrawing().addDrawable(e, element);
			_finalizeDeserializationFor(e);
		}
	}
}
