package org.openflexo.technologyadapter.powerpoint.model.io;

import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.apache.poi.hslf.model.AutoShape;
import org.apache.poi.hslf.model.Background;
import org.apache.poi.hslf.model.Line;
import org.apache.poi.hslf.model.Picture;
import org.apache.poi.hslf.model.Shape;
import org.apache.poi.hslf.model.ShapeGroup;
import org.apache.poi.hslf.model.SimpleShape;
import org.apache.poi.hslf.model.Slide;
import org.apache.poi.hslf.model.TextBox;
import org.apache.poi.hslf.usermodel.SlideShow;
import org.openflexo.technologyadapter.powerpoint.PowerpointTechnologyAdapter;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointAutoShape;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointBackground;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointLine;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointObject;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointPicture;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointShape;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointShapeGroup;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSimpleShape;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlide;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointSlideshow;
import org.openflexo.technologyadapter.powerpoint.model.PowerpointTextBox;

public class BasicPowerpointModelConverter {

	private static final Logger logger = Logger.getLogger(BasicPowerpointModelConverter.class.getPackage().getName());

	/** Powerpoint Objects. */
	protected final Map<Object, PowerpointObject> powerpointObjects = new HashMap<Object, PowerpointObject>();

	/**
	 * Constructor.
	 */
	public BasicPowerpointModelConverter() {
	}

	/**
	 * Convert a Workbook into an Excel Workbook
	 */
	public PowerpointSlideshow convertPowerpointSlideshow(SlideShow slideshow, PowerpointTechnologyAdapter technologyAdapter) {
		PowerpointSlideshow powerpointSlideshow = new PowerpointSlideshow(slideshow, this, technologyAdapter);
		powerpointObjects.put(slideshow, powerpointSlideshow);
		for (Slide slide : slideshow.getSlides()) {
			PowerpointSlide powerpointSlide = convertPowerpointSlideToSlide(slide, powerpointSlideshow, technologyAdapter);
			powerpointSlideshow.getPowerpointSlides().add(powerpointSlide);
		}
		return powerpointSlideshow;
	}

	/**
	 * Convert a Sheet into an Excel Sheet
	 */
	public PowerpointSlide convertPowerpointSlideToSlide(Slide slide, PowerpointSlideshow slideshow, PowerpointTechnologyAdapter technologyAdapter) {
		PowerpointSlide powerpointSlide = null;
		if (powerpointObjects.get(slide) == null) {
			powerpointSlide = new PowerpointSlide(slide, slideshow, technologyAdapter);
			powerpointObjects.put(slide, powerpointSlide);
			for (Shape shape : slide.getShapes()) {
				PowerpointShape powerpointShape = convertPowerpointShapeToShape(shape, powerpointSlide, technologyAdapter);
				powerpointSlide.addToPowerpointShapes(powerpointShape);
			}
		} else {
			powerpointSlide = (PowerpointSlide) powerpointObjects.get(slide);
		}
		return powerpointSlide;
	}

	/**
	 * Convert a Shape into a PowerpointShape
	 */
	public PowerpointShape convertPowerpointShapeToShape(Shape shape, PowerpointSlide powerpointSlide, PowerpointTechnologyAdapter technologyAdapter) {
		PowerpointShape powerpointShape=null;
		if (powerpointObjects.get(shape) == null) {
			if(shape instanceof TextBox){
				powerpointShape = new PowerpointTextBox((TextBox)shape, powerpointSlide, technologyAdapter);
			}
			if(shape instanceof AutoShape){
				powerpointShape = new PowerpointAutoShape((AutoShape)shape, powerpointSlide, technologyAdapter);
			}
			if(shape instanceof Line){
				powerpointShape = new PowerpointLine((Line)shape, powerpointSlide, technologyAdapter);
			}
			if(shape instanceof Picture){
				powerpointShape = new PowerpointPicture((Picture)shape, powerpointSlide, technologyAdapter);
			}
			if(shape instanceof Line){
				powerpointShape = new PowerpointLine((Line)shape, powerpointSlide, technologyAdapter);
			}
			if(shape instanceof Background){
				powerpointShape = new PowerpointBackground((Background)shape, powerpointSlide, technologyAdapter);
			}
			if(shape instanceof ShapeGroup){
				powerpointShape = new PowerpointShapeGroup((ShapeGroup)shape, powerpointSlide, technologyAdapter);
			}
			powerpointObjects.put(shape, powerpointShape);
		} else {
			powerpointShape = (PowerpointShape) powerpointObjects.get(shape);
		}

		return powerpointShape;
	}


	/**
	 * Getter of excel objects.
	 * 
	 * @return the individuals value
	 */
	public Map<Object, PowerpointObject> getPowerpointObjects() {
		return powerpointObjects;
	}

}
