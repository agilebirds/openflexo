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
package org.openflexo.foundation;

import java.awt.Color;
import java.awt.geom.Dimension2D;
import java.awt.geom.Point2D;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Logger;

import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.utils.FlexoColor;
import org.openflexo.foundation.utils.FlexoFont;
import org.openflexo.foundation.wkf.dm.LabelLocationChanged;
import org.openflexo.foundation.wkf.dm.ObjectAlignementChanged;
import org.openflexo.foundation.wkf.dm.ObjectLocationChanged;
import org.openflexo.foundation.wkf.dm.ObjectLocationResetted;
import org.openflexo.foundation.wkf.dm.ObjectSizeChanged;
import org.openflexo.foundation.wkf.dm.ObjectVisibilityChanged;
import org.openflexo.foundation.wkf.dm.WKFAttributeDataModification;
import org.openflexo.xmlcode.PropertiesKeyValueProperty;
import org.openflexo.xmlcode.StringEncoder.Converter;

/**
 * A RepresentableFlexoModelObjectObject instance represents an object which stores data related to a potential graphical representation in
 * some (unknown at this level) graphical editors. Those objects are then graphically representable, and thus, have a position, a width and
 * a heigth <br>
 * This class implements the common management of basic graphical features (position, size, colors)
 * 
 * @author sylvain
 */
public abstract class RepresentableFlexoModelObject extends FlexoModelObject {

	private static final Logger logger = Logger.getLogger(RepresentableFlexoModelObject.class.getPackage().getName());

	public static final String DEFAULT = "default";

	public static final String BG_COLOR = "bgColor";
	public static final String FG_COLOR = "fgColor";
	public static final String TEXT_COLOR = "textColor";
	public static final String TEXT_FONT = "textFont";
	public static final String POSX = "posx";
	public static final String POSY = "posy";
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String VISIBILITY = "visible";
	public static final String LABEL_POSX = "labelx";
	public static final String LABEL_POSY = "labely";
	public static final String ALIGNED_ON_GRID = "alignedOnGrid";
	public static final String GRID_SIZE = "gridSize";

	/**
	 * Constructor
	 */
	public RepresentableFlexoModelObject(FlexoProject project) {
		super(project);
		graphicalProperties = new TreeMap<String, Object>();
	}

	private Map<String, Object> graphicalProperties;

	public Map<String, Object> _getGraphicalProperties() {
		if (isSerializing() && (graphicalProperties == null || graphicalProperties.size() == 0)) {
			return null;
		}
		return graphicalProperties;
	}

	public void _setGraphicalProperties(Map<String, Object> graphicalProperties) {
		this.graphicalProperties = new TreeMap<String, Object>(graphicalProperties);
	}

	public boolean hasGraphicalPropertyForKey(String key) {
		return graphicalProperties.get(key) != null;
	}

	public Object _graphicalPropertyForKey(String key) {
		Object returned = graphicalProperties.get(key);
		if (returned instanceof PropertiesKeyValueProperty.UndecodableProperty) {
			// Try to decode now
			Class<?> objectType;
			String className = ((PropertiesKeyValueProperty.UndecodableProperty) returned).className;
			String value = ((PropertiesKeyValueProperty.UndecodableProperty) returned).value;
			try {
				objectType = Class.forName(className);
			} catch (ClassNotFoundException e) {
				logger.warning("Class named '" + className + "' not found");
				return returned;
			}
			if (objectType == FlexoColor.class) {
				objectType = Color.class;
			}
			Converter<?> converter = getProject().getStringEncoder()._converterForClass(objectType);
			if (converter == null) {
				// Is it an enum ???
				if (objectType.isEnum()) {
					return Enum.valueOf((Class) objectType, value);
				}
				logger.warning("No converter for " + objectType);
				return returned;
			}
			returned = converter.convertFromString(value);
			// System.out.println("Decoding UndecodableProperty to "+objectType+" as "+returned);
			return returned;
		}
		return returned;
	}

	public Object _objectGraphicalPropertyForKey(String key, Object defaultValue) {
		Object returned = _graphicalPropertyForKey(key);
		if (returned == null) {
			// logger.warning("Cannot retrieve value for "+key+". Using default value: "+defaultValue);
			_setGraphicalPropertyForKey(defaultValue, key);
			returned = defaultValue;
		}
		return returned;
	}

	public double _doubleGraphicalPropertyForKey(String key, double defaultValue) {
		return _doubleGraphicalPropertyForKey(key, defaultValue, true);
	}

	public double _doubleGraphicalPropertyForKey(String key, double defaultValue, boolean storeDefaultValue) {
		Object returned = _graphicalPropertyForKey(key);
		if (returned == null) {
			if (!storeDefaultValue) {
				return defaultValue;
			}
			// logger.warning("Cannot retrieve value for "+key+". Using default value: "+defaultValue);
			_setGraphicalPropertyForKey(defaultValue, key);
			returned = defaultValue;
		}
		return (Double) returned;
	}

	public int _integerGraphicalPropertyForKey(String key, int defaultValue) {
		return _integerGraphicalPropertyForKey(key, defaultValue, true);
	}

	public int _integerGraphicalPropertyForKey(String key, int defaultValue, boolean storeDefaultValue) {
		Object returned = _graphicalPropertyForKey(key);
		if (returned == null) {
			// logger.warning("Cannot retrieve value. Using default.");
			if (!storeDefaultValue) {
				return defaultValue;
			}
			_setGraphicalPropertyForKey(defaultValue, key);
			returned = defaultValue;
		}
		return (Integer) returned;
	}

	public boolean _booleanGraphicalPropertyForKey(String key, boolean defaultValue) {
		Object returned = _graphicalPropertyForKey(key);
		if (returned == null) {
			// logger.warning("Cannot retrieve value. Using default.");
			_setGraphicalPropertyForKey(defaultValue, key);
			returned = defaultValue;
		}
		return (Boolean) returned;
	}

	public Color _colorGraphicalPropertyForKey(String key, Color defaultValue) {
		Object returned = _graphicalPropertyForKey(key);
		if (returned == null && defaultValue != null) {
			// logger.warning("Cannot retrieve value. Using default.");
			_setGraphicalPropertyForKey(defaultValue, key);
			returned = defaultValue;
		}
		return (Color) returned;
	}

	public FlexoFont _fontGraphicalPropertyForKey(String key, FlexoFont defaultValue) {
		Object returned = _graphicalPropertyForKey(key);
		if (returned == null) {
			// logger.warning("Cannot retrieve value. Using default.");
			_setGraphicalPropertyForKey(defaultValue, key);
			returned = defaultValue;
		}
		return (FlexoFont) returned;
	}

	// Note that following is not notified (important to avoid loops), and does NOT call setChanged()
	// Use high-level methods to do it (eg: setIntegerParameter()/setX()/setY()/etc....)
	public void _setGraphicalPropertyForKey(Object value, String key) {
		if (value != null) {
			graphicalProperties.put(key, value);
		} else {
			graphicalProperties.remove(key);
		}
		setChanged();
	}

	public void _removeGraphicalPropertyWithKey(String key) {
		if (graphicalProperties.get(key) != null) {
			graphicalProperties.remove(key);
		}
		setChanged();
	}

	// ================================================================
	// ============ Common features graphical management ==============
	// ================================================================

	public void setIsVisible(boolean b) {
		if (b != getIsVisible()) {
			_setGraphicalPropertyForKey(b, VISIBILITY);
			setChanged();
			notifyObservers(new ObjectVisibilityChanged(b));
		}
		// logger.info("setIsVisible("+b+") for "+this);
		// printObservers();
	}

	public boolean getIsVisible() {
		return getIsVisible(false);
	}

	/**
	 * 
	 * @param defaultVisibility
	 *            : default value when never accessed
	 * @return
	 */
	public boolean getIsVisible(boolean defaultVisibility) {
		return _booleanGraphicalPropertyForKey(VISIBILITY, defaultVisibility);
	}

	public boolean getIsVisible(String context) {
		return getIsVisible(context, true);
	}

	public boolean getIsVisible(String context, boolean defaultValue) {
		return _booleanGraphicalPropertyForKey(VISIBILITY + "_" + context, defaultValue);
	}

	public void setIsVisible(boolean visible, String context) {
		if (visible != getIsVisible(context)) {
			_setGraphicalPropertyForKey(visible, VISIBILITY + "_" + context);
			setChanged();
			notifyObservers(new ObjectVisibilityChanged(visible));
		}
	}

	public boolean getIsAlignedOnGrid(String context) {
		return getIsAlignedOnGrid(context, false);
	}

	public boolean getIsAlignedOnGrid(String context, boolean defaultValue) {
		return _booleanGraphicalPropertyForKey(ALIGNED_ON_GRID + "_" + context, defaultValue);
	}

	public void setIsAlignedOnGrid(boolean isAligned, String context) {
		if (isAligned != getIsAlignedOnGrid(context)) {
			_setGraphicalPropertyForKey(isAligned, ALIGNED_ON_GRID + "_" + context);
			setChanged();
			notifyObservers(new ObjectAlignementChanged(isAligned));
		}
	}

	public int getGridSize(String context) {
		return getGridSize(context, 30, false);
	}

	public int getGridSize(String context, int defaultValue) {
		return getGridSize(context, defaultValue, true);
	}

	public int getGridSize(String context, int defaultValue, boolean storeDefaultValue) {
		return _integerGraphicalPropertyForKey(GRID_SIZE + "_" + context, defaultValue, storeDefaultValue);
	}

	public void setGridSize(int gridSize, String context) {
		if (gridSize < 1) {
			gridSize = 1;
		}
		if (gridSize > 200) {
			gridSize = 200;
		}
		if (gridSize != getGridSize(context)) {
			_setGraphicalPropertyForKey(gridSize, GRID_SIZE + "_" + context);
			setChanged();
			notifyObservers(new ObjectAlignementChanged(gridSize));
		}
	}

	public Point2D getLocation(String context) {
		return new Point2D.Double(getX(context), getY(context));
	}

	public boolean hasLocationForContext(String context) {
		return hasGraphicalPropertyForKey(POSX + "_" + context) && hasGraphicalPropertyForKey(POSY + "_" + context);
	}

	public boolean hasDimensionForContext(String context) {
		return hasGraphicalPropertyForKey(WIDTH + "_" + context) && hasGraphicalPropertyForKey(HEIGHT + "_" + context);
	}

	public boolean hasLabelLocationForContext(String context) {
		return hasGraphicalPropertyForKey(getLabelXKeyForContext(context)) && hasGraphicalPropertyForKey(getLabelYKeyForContext(context));
	}

	public void resetLocation() {
		setChanged();
		notifyObservers(new ObjectLocationResetted());
	}

	public void resetLocation(String context) {
		Point2D oldLocation = getLocation(context);
		_removeGraphicalPropertyWithKey(POSX + "_" + context);
		_removeGraphicalPropertyWithKey(POSY + "_" + context);
		setChanged();
		notifyObservers(new ObjectLocationChanged(oldLocation, null, context));
	}

	public void resetLabelLocation(String context) {
		// Point2D oldLocation = getLabelLocation(context);
		_removeGraphicalPropertyWithKey(getLabelXKeyForContext(context));
		_removeGraphicalPropertyWithKey(getLabelYKeyForContext(context));
		setChanged();
		notifyObservers(new LabelLocationChanged(/*oldLocation, null, context*/));
	}

	public double getX(String context) {
		return getX(context, 0, false);
	}

	public double getX(String context, double defaultValue) {
		return getX(context, defaultValue, true);
	}

	public double getX(String context, double defaultValue, boolean storeDefaultValue) {
		return _doubleGraphicalPropertyForKey(POSX + "_" + context, defaultValue, storeDefaultValue);
	}

	public void setX(double x, String context) {
		x = Math.round(x);
		Point2D oldLocation = getLocation(context);
		if (x != getX(context)) {
			_setGraphicalPropertyForKey(x, POSX + "_" + context);
			setChanged();
			notifyObservers(new ObjectLocationChanged(oldLocation, getLocation(context), context));
		}
	}

	public double getY(String context) {
		return getY(context, 0, false);
	}

	public double getY(String context, double defaultValue) {
		return getY(context, defaultValue, true);
	}

	public double getY(String context, double defaultValue, boolean storeDefaultValue) {
		return _doubleGraphicalPropertyForKey(POSY + "_" + context, defaultValue, storeDefaultValue);
	}

	public void setY(double y, String context) {
		y = Math.round(y);
		Point2D oldLocation = getLocation(context);
		if (y != getY(context)) {
			_setGraphicalPropertyForKey(y, POSY + "_" + context);
			setChanged();
			notifyObservers(new ObjectLocationChanged(oldLocation, getLocation(context), context));
		}
	}

	public Dimension2D getDimension(final String context) {
		return new Dimension2D() {
			@Override
			public double getWidth() {
				return RepresentableFlexoModelObject.this.getWidth(context);
			}

			@Override
			public double getHeight() {
				return RepresentableFlexoModelObject.this.getHeight(context);
			}

			@Override
			public void setSize(double width, double height) {
			}
		};
	}

	public double getWidth(String context) {
		return getWidth(context, getDefaultWidth(), false);
	}

	protected int getDefaultWidth() {
		return 100;
	}

	public double getWidth(String context, double defaultValue) {
		return getWidth(context, defaultValue, true);
	}

	public double getWidth(String context, double defaultValue, boolean storeDefaultValue) {
		return _doubleGraphicalPropertyForKey(WIDTH + "_" + context, defaultValue, storeDefaultValue);
	}

	public void setWidth(double width, String context) {
		if (width != getWidth(context)) {
			Dimension2D oldDimension = getDimension(context);
			_setGraphicalPropertyForKey(width, WIDTH + "_" + context);
			setChanged();
			notifyObservers(new ObjectSizeChanged(oldDimension, getDimension(context), context));
		}
	}

	public double getHeight(String context) {
		return getHeight(context, getDefaultHeight(), false);
	}

	protected int getDefaultHeight() {
		return 50;
	}

	public double getHeight(String context, double defaultValue) {
		return getHeight(context, defaultValue, true);
	}

	public double getHeight(String context, double defaultValue, boolean storeDefaultValue) {
		return _doubleGraphicalPropertyForKey(HEIGHT + "_" + context, defaultValue, storeDefaultValue);
	}

	public void setHeight(double height, String context) {
		if (height != getHeight(context)) {
			Dimension2D oldDimension = getDimension(context);
			_setGraphicalPropertyForKey(height, HEIGHT + "_" + context);
			setChanged();
			notifyObservers(new ObjectSizeChanged(oldDimension, getDimension(context), context));
		}
	}

	public Point2D getLabelLocation(String context) {
		return new Point2D.Double(getLabelX(context), getLabelY(context));
	}

	public double getLabelX(String context) {
		return getLabelX(context, 0, false);
	}

	public double getLabelX(String context, double defaultValue) {
		return getLabelX(context, defaultValue, true);
	}

	public double getLabelX(String context, double defaultValue, boolean storeDefaultValue) {
		return _doubleGraphicalPropertyForKey(getLabelXKeyForContext(context), defaultValue, storeDefaultValue);
	}

	public void setLabelX(double x, String context) {
		if (x != getLabelX(context)) {
			_setGraphicalPropertyForKey(x, getLabelXKeyForContext(context));
			setChanged();
			notifyObservers(new LabelLocationChanged());
		}
	}

	public double getLabelY(String context) {
		return getLabelY(context, 0, false);
	}

	public double getLabelY(String context, double defaultValue) {
		return getLabelY(context, defaultValue, true);
	}

	public double getLabelY(String context, double defaultValue, boolean storeDefaultValue) {
		return _doubleGraphicalPropertyForKey(getLabelYKeyForContext(context), defaultValue, storeDefaultValue);
	}

	public void setLabelY(double y, String context) {
		if (y != getLabelY(context)) {
			_setGraphicalPropertyForKey(y, getLabelYKeyForContext(context));
			setChanged();
			notifyObservers(new LabelLocationChanged());
		}
	}

	public static String getLabelXKeyForContext(String context) {
		return LABEL_POSX + "_" + context;
	}

	public static String getLabelYKeyForContext(String context) {
		return LABEL_POSY + "_" + context;
	}

	public Color getBgColor(String context) {
		return _colorGraphicalPropertyForKey(getBgColorKeyForContext(context), FlexoColor.WHITE_COLOR);
	}

	public Color getBgColor(String context, Color defaultValue) {
		return _colorGraphicalPropertyForKey(getBgColorKeyForContext(context), defaultValue);
	}

	public void setBgColor(Color bgColor, String context) {
		String key = getBgColorKeyForContext(context);
		_setGraphicalPropertyForKey(bgColor, key);
		setChanged();
		notifyObservers(new DataModification(key, null, bgColor));
	}

	public static String getBgColorKeyForContext(String context) {
		return BG_COLOR + "_" + context;
	}

	public Color getFgColor(String context) {
		return _colorGraphicalPropertyForKey(getFgColorKeyForContext(context), FlexoColor.BLACK_COLOR);
	}

	public Color getFgColor(String context, Color defaultValue) {
		return _colorGraphicalPropertyForKey(getFgColorKeyForContext(context), defaultValue);
	}

	public void setFgColor(Color fgColor, String context) {
		String key = getFgColorKeyForContext(context);
		_setGraphicalPropertyForKey(fgColor, key);
		setChanged();
		notifyObservers(new DataModification(key, null, fgColor));
	}

	public static String getFgColorKeyForContext(String context) {
		return FG_COLOR + "_" + context;
	}

	public Color getTextColor(String context) {
		return _colorGraphicalPropertyForKey(getTextColorKeyForContext(context), FlexoColor.BLACK_COLOR);
	}

	public Color getTextColor(String context, Color defaultValue) {
		return _colorGraphicalPropertyForKey(getTextColorKeyForContext(context), defaultValue);
	}

	public void setTextColor(Color textColor, String context) {
		String key = getTextColorKeyForContext(context);
		_setGraphicalPropertyForKey(textColor, key);
		setChanged();
		notifyObservers(new DataModification(key, null, textColor));
	}

	public static String getTextColorKeyForContext(String context) {
		return TEXT_COLOR + "_" + context;
	}

	public FlexoFont getTextFont(String context) {
		return _fontGraphicalPropertyForKey(getTextFontKeyForContext(context), FlexoFont.SANS_SERIF);
	}

	public FlexoFont getTextFont(String context, FlexoFont defaultValue) {
		return _fontGraphicalPropertyForKey(getTextFontKeyForContext(context), defaultValue);
	}

	public void setTextFont(FlexoFont textFont, String context) {
		String key = getTextFontKeyForContext(context);
		_setGraphicalPropertyForKey(textFont, key);
		setChanged();
		notifyObservers(new DataModification(key, null, textFont));
	}

	public static String getTextFontKeyForContext(String context) {
		return TEXT_FONT + "_" + context;
	}

	public int getIntegerParameter(String key) {
		return getIntegerParameter(key, 0);
	}

	public int getIntegerParameter(String key, int defaultValue) {
		return getIntegerParameter(key, defaultValue, true);
	}

	public int getIntegerParameter(String key, int defaultValue, boolean storeDefaultValue) {
		return _integerGraphicalPropertyForKey(key, defaultValue, storeDefaultValue);
	}

	public void setIntegerParameter(int value, String key) {
		int oldValue = getIntegerParameter(key);
		if (oldValue != value) {
			_setGraphicalPropertyForKey(value, key);
			setChanged();
			notifyObservers(new WKFAttributeDataModification(key, oldValue, value));
		}
	}

	public void setIntegerParameter(int value, String key, int defaultValue) {
		int oldValue = getIntegerParameter(key, defaultValue);
		if (oldValue != value) {
			_setGraphicalPropertyForKey(value, key);
			setChanged();
			notifyObservers(new WKFAttributeDataModification(key, oldValue, value));
		}
	}

	/*public boolean getBooleanParameter(String key)
	{
		return getBooleanParameter(key,false);
	}

	public boolean getBooleanParameter(String key, boolean defaultValue)
	{
		return _booleanGraphicalPropertyForKey(key,defaultValue);
	}

	public void setBooleanParameter(boolean value, String key)
	{
		boolean oldValue = getBooleanParameter(key);
		if (oldValue != value) {
			_setGraphicalPropertyForKey(value,key);
			setChanged();
			notifyObservers(new WKFAttributeDataModification(key,oldValue,value));
		}
	}

	public void setBooleanParameter(boolean value, String key, boolean defaultValue)
	{
		boolean oldValue = getBooleanParameter(key,defaultValue);
		if (oldValue != value) {
			_setGraphicalPropertyForKey(value,key);
			setChanged();
			notifyObservers(new WKFAttributeDataModification(key,oldValue,value));
		}
	}*/

}
