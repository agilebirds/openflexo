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

package org.openflexo.xmlcode;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.StringTokenizer;

/**
 * Utility class used to encode objects
 * 
 * @author sguerin
 */
public class StringEncoder {

	private static final StringEncoder defaultInstance = new StringEncoder();

	public static String encodeBoolean(boolean aBoolean) {
		return aBoolean ? "true" : "false";
	}

	public static String encodeByte(byte aByte) {
		return "" + aByte;
	}

	public static String encodeCharacter(char aChar) {
		return "" + aChar;
	}

	public static String encodeDouble(double aDouble) {
		return "" + aDouble;
	}

	public static String encodeFloat(float aFloat) {
		return "" + aFloat;
	}

	public static String encodeInteger(int anInt) {
		return "" + anInt;
	}

	public static String encodeLong(long aLong) {
		return "" + aLong;
	}

	public static String encodeShort(short aShort) {
		return "" + aShort;
	}

	public static boolean decodeAsBoolean(String value) {
		return value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes");
	}

	public static byte decodeAsByte(String value) {
		return Byte.parseByte(value);
	}

	public static char decodeAsCharacter(String value) {
		return value.charAt(0);
	}

	public static double decodeAsDouble(String value) {
		try {
			return Double.parseDouble(value);
		} catch (NumberFormatException e) {
			return 0;
		}
	}

	public static float decodeAsFloat(String value) {
		return Float.parseFloat(value);
	}

	public static int decodeAsInteger(String value) {
		if (value == null) {
			return -1;
		}
		try {
			return Integer.parseInt(value);
		} catch (NumberFormatException e) {
			return (int) decodeAsDouble(value);
		}
	}

	public static long decodeAsLong(String value) {
		return Long.parseLong(value);
	}

	public static short decodeAsShort(String value) {
		return Short.parseShort(value);
	}

	/**
	 * 
	 * @param value
	 * @param objectType
	 * @return
	 * @deprecated use the non static method {@link #_decodeObject(String, Class)}
	 */
	@Deprecated
	public static <T> T decodeObject(String value, Class<T> objectType) {
		return defaultInstance._decodeObject(value, objectType);
	}

	/**
	 * 
	 * @param object
	 * @return
	 * @deprecated use the non static method {@link #_encodeObject(Object)}
	 */
	@Deprecated
	public static <T> String encodeObject(T object) {
		return defaultInstance._encodeObject(object);
	}

	/**
	 * 
	 * @param objectType
	 * @return
	 * @deprecated use the non static method {@link #_converterForClass(Class)}
	 */
	@Deprecated
	public static <T> Converter<T> converterForClass(Class<T> objectType) {
		return defaultInstance._converterForClass(objectType);
	}

	/**
	 * 
	 * @param objectType
	 * @return
	 * @deprecated use the non static method {@link #_isConvertable(Class)}
	 */
	@Deprecated
	public static <T> boolean isConvertable(Class<T> objectType) {
		return defaultInstance._isConvertable(objectType);
	}

	/**
	 * Sets date format, under the form <code>"yyyy.MM.dd G 'at' HH:mm:ss a zzz"</code>
	 * 
	 * @deprecated use the non static method {@link #_setDateFormat(String)}
	 */
	@Deprecated
	public static void setDateFormat(String aFormat) {
		defaultInstance._setDateFormat(aFormat);
	}

	/**
	 * 
	 * @return
	 * @deprecated use the non static method {@link #_getDateFormat()}
	 */
	@Deprecated
	public static String getDateFormat() {
		return defaultInstance._getDateFormat();
	}

	/**
	 * Return a string representation of a date, according to valid date format
	 * 
	 * @deprecated use the non-static method {@link #_getDateRepresentation(Date)}
	 */
	@Deprecated
	public static String getDateRepresentation(Date aDate) {
		return defaultInstance._getDateRepresentation(aDate);
	}

	/**
	 * 
	 * @param converter
	 * @return
	 * @deprecated use the non-static method {@link #_addConverter(org.openflexo.xmlcode.StringEncoder.Converter)}
	 */
	@Deprecated
	public static <T> Converter<T> addConverter(Converter<T> converter) {
		return defaultInstance._addConverter(converter);
	}

	/**
	 * @param converter
	 */
	public static <T> void removeConverter(Converter<T> converter) {
		defaultInstance._removeConverter(converter);
	}

	public static void initialize() {
		defaultInstance._initialize();
	}

	/**
	 * Abstract class defining a converter to and from a String for a given class
	 * 
	 * @author sguerin
	 */
	public static abstract class Converter<T> {

		protected Class<T> converterClass;

		public Converter(Class<T> aClass) {
			super();
			converterClass = aClass;
		}

		public Class<T> getConverterClass() {
			return converterClass;
		}

		public abstract T convertFromString(String value);

		public abstract String convertToString(T value);

	}

	public static class EnumerationConverter<T> extends Converter<T> {
		private String _stringRepresationMethodName;

		public EnumerationConverter(Class<T> enumeration, String stringRepresentationMethodName) {
			super(enumeration);
			_stringRepresationMethodName = stringRepresentationMethodName;
		}

		@Override
		public T convertFromString(String value) {
			if (value == null) {
				return null;
			}
			for (int i = 0; i < converterClass.getEnumConstants().length; i++) {
				if (value.equals(convertToString(converterClass.getEnumConstants()[i]))) {
					return converterClass.getEnumConstants()[i];
				}
			}
			return null;
		}

		@Override
		public String convertToString(T value) {
			if (value == null) {
				return null;
			}
			try {
				Method m = value.getClass().getDeclaredMethod(_stringRepresationMethodName, (Class[]) null);
				return (String) m.invoke(value, (Object[]) null);
			} catch (NoSuchMethodException e) {
				System.err.println(_stringRepresationMethodName + " doesn't exist on enum :" + value.getClass().getName());
			} catch (InvocationTargetException e) {
				System.err.println("Invocation of " + _stringRepresationMethodName + " on enum :" + value.getClass().getName()
						+ " caused the following error : " + e.getMessage());
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
		}
	}

	/**
	 * Class defining how to convert Boolean from/to String
	 * 
	 * @author sguerin
	 */
	public static class BooleanConverter extends Converter<Boolean> {

		protected BooleanConverter() {
			super(Boolean.class);
		}

		@Override
		public Boolean convertFromString(String value) {
			return new Boolean(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"));
		}

		@Override
		public String convertToString(Boolean value) {
			return value.toString();
		}

	}

	/**
	 * Class defining how to convert Integer from/to String
	 * 
	 * @author sguerin
	 */
	public static class NumberConverter extends Converter<Number> {

		protected NumberConverter() {
			super(Number.class);
		}

		@Override
		public Number convertFromString(String value) {
			try {
				Number returned = Integer.parseInt(value);
				// System.out.println("Build a integer: "+value);
				return returned;
			} catch (NumberFormatException e1) {
				try {
					Number returned = Long.parseLong(value);
					// System.out.println("Build a long: "+value);
					return returned;
				} catch (NumberFormatException e2) {
					try {
						Number returned = Float.parseFloat(value);
						// System.out.println("Build a float: "+value);
						return returned;
					} catch (NumberFormatException e3) {
						try {
							Number returned = Double.parseDouble(value);
							// System.out.println("Build a double: "+value);
							return returned;
						} catch (NumberFormatException e4) {
						}
					}
				}
			}

			return null;
		}

		@Override
		public String convertToString(Number value) {
			return value.toString();
		}

	}

	/**
	 * Class defining how to convert Integer from/to String
	 * 
	 * @author sguerin
	 */
	public static class IntegerConverter extends Converter<Integer> {

		protected IntegerConverter() {
			super(Integer.class);
		}

		@Override
		public Integer convertFromString(String value) {
			return new Integer(value);
		}

		@Override
		public String convertToString(Integer value) {
			return value.toString();
		}

	}

	/**
	 * Class defining how to convert Short from/to String
	 * 
	 * @author sguerin
	 */
	public static class ShortConverter extends Converter<Short> {

		protected ShortConverter() {
			super(Short.class);
		}

		@Override
		public Short convertFromString(String value) {
			return new Short(value);
		}

		@Override
		public String convertToString(Short value) {
			return value.toString();
		}

	}

	/**
	 * Class defining how to convert Long from/to String
	 * 
	 * @author sguerin
	 */
	public static class LongConverter extends Converter<Long> {

		protected LongConverter() {
			super(Long.class);
		}

		@Override
		public Long convertFromString(String value) {
			return new Long(value);
		}

		@Override
		public String convertToString(Long value) {
			return value.toString();
		}

	}

	/**
	 * Class defining how to convert Float from/to String
	 * 
	 * @author sguerin
	 */
	public static class FloatConverter extends Converter<Float> {

		protected FloatConverter() {
			super(Float.class);
		}

		@Override
		public Float convertFromString(String value) {
			return new Float(value);
		}

		@Override
		public String convertToString(Float value) {
			return value.toString();
		}

	}

	/**
	 * Class defining how to convert Double from/to String
	 * 
	 * @author sguerin
	 */
	public static class DoubleConverter extends Converter<Double> {

		protected DoubleConverter() {
			super(Double.class);
		}

		@Override
		public Double convertFromString(String value) {
			return new Double(value);
		}

		@Override
		public String convertToString(Double value) {
			return value.toString();
		}

	}

	/**
	 * Class defining how to convert String from/to String (easy !)
	 * 
	 * @author sguerin
	 */
	public static class StringConverter extends Converter<String> {

		protected StringConverter() {
			super(String.class);
		}

		@Override
		public String convertFromString(String value) {
			return value;
		}

		@Override
		public String convertToString(String value) {
			return value;
		}

	}

	/**
	 * Class defining how to convert String from/to Date
	 * 
	 * @author sguerin
	 */
	public static class DateConverter extends Converter<Date> {

		/** Specify date format */
		protected String _dateFormat = new SimpleDateFormat().toPattern();

		public DateConverter() {
			super(Date.class);
		}

		@Override
		public Date convertFromString(String value) {
			try {
				return tryToConvertFromString(value);
			} catch (ParseException e) {
				SimpleDateFormat formatter = new SimpleDateFormat(_dateFormat);
				Date currentTime = new Date();
				String dateString = formatter.format(currentTime);
				System.err.println("Supplied value is not parsable as a date. " + " Date format should be for example " + dateString);
				return null;
			}
		}

		public Date tryToConvertFromString(String value) throws ParseException {
			Date returned = null;
			StringTokenizer st = new StringTokenizer(value, ",");
			String dateFormat = _dateFormat;
			String dateAsString = null;
			if (st.hasMoreTokens()) {
				dateFormat = st.nextToken();
			}
			if (st.hasMoreTokens()) {
				dateAsString = st.nextToken();
			}
			if (dateAsString != null) {
				try {
					returned = new SimpleDateFormat(dateFormat).parse(dateAsString);
				} catch (IllegalArgumentException e) {
					throw new ParseException("While parsing supposed date format: " + e.getMessage(), 0);
				}
			}
			if (returned == null) {
				throw new ParseException("Cannot parse as a date " + value, 0);
			}
			return returned;
		}

		@Override
		public String convertToString(Date date) {
			if (date != null) {
				return _dateFormat + "," + new SimpleDateFormat(_dateFormat).format(date);
			} else {
				return null;
			}
		}

		/**
		 * Return a string representation of a date, according to valid date format
		 */
		public String getDateRepresentation(Date date) {
			if (date != null) {
				return new SimpleDateFormat(_dateFormat).format(date);
			} else {
				return null;
			}
		}

	}

	/**
	 * Class defining how to convert String from/to URL
	 * 
	 * @author sguerin
	 */
	public static class URLConverter extends Converter<URL> {

		protected URLConverter() {
			super(URL.class);
		}

		@Override
		public URL convertFromString(String value) {
			try {
				return new URL(value);
			} catch (MalformedURLException e) {
				System.err.println("Supplied value is not parsable as an URL:" + value);
				return null;
			}
		}

		@Override
		public String convertToString(URL anURL) {
			if (anURL != null) {
				return anURL.toExternalForm();
			} else {
				return null;
			}
		}

	}

	/**
	 * Class defining how to convert String from/to File
	 * 
	 * @author sguerin
	 */
	public static class FileConverter extends Converter<File> {

		protected FileConverter() {
			super(File.class);
		}

		@Override
		public File convertFromString(String value) {
			return new File(value);
		}

		@Override
		public String convertToString(File aFile) {
			if (aFile != null) {
				return aFile.getAbsolutePath();
			} else {
				return null;
			}
		}

	}

	/**
	 * Class defining how to convert String from/to Class
	 * 
	 * @author sguerin
	 */
	public static class ClassConverter extends Converter<Class> {

		protected ClassConverter() {
			super(Class.class);
		}

		@Override
		public Class convertFromString(String value) {
			if (value == null || value.isEmpty()) {
				return null;
			}
			try {
				if (value.equals("boolean"))
					return Boolean.TYPE;
				if (value.equals("int"))
					return Integer.TYPE;
				if (value.equals("short"))
					return Short.TYPE;
				if (value.equals("long"))
					return Long.TYPE;
				if (value.equals("float"))
					return Float.TYPE;
				if (value.equals("double"))
					return Double.TYPE;
				if (value.equals("byte"))
					return Byte.TYPE;
				if (value.equals("char"))
					return Character.TYPE;
				return Class.forName(value);
			} catch (ClassNotFoundException e) {
				// Warns about the exception
				throw new InvalidDataException("Supplied value represents a class not found: " + value);
			}
		}

		@Override
		public String convertToString(Class aClass) {
			if (aClass != null) {
				return aClass.getName();
			} else {
				return null;
			}
		}

	}

	/**
	 * Class defining how to convert String from/to Point
	 * 
	 * @author sguerin
	 */
	public static class PointConverter extends Converter<Point> {

		protected PointConverter() {
			super(Point.class);
		}

		@Override
		public Point convertFromString(String value) {
			try {
				Point returned = new Point();
				StringTokenizer st = new StringTokenizer(value, ",");
				if (st.hasMoreTokens()) {
					returned.x = Integer.parseInt(st.nextToken());
				}
				if (st.hasMoreTokens()) {
					returned.y = Integer.parseInt(st.nextToken());
				}
				return returned;
			} catch (NumberFormatException e) {
				// Warns about the exception
				System.err.println("Supplied value is not parsable as a Point:" + value);
				return null;
			}
		}

		@Override
		public String convertToString(Point aPoint) {
			if (aPoint != null) {
				return aPoint.x + "," + aPoint.y;
			} else {
				return null;
			}
		}

	}

	/**
	 * Class defining how to convert String from/to Point
	 * 
	 * @author sguerin
	 */
	public static class RectangleConverter extends Converter<Rectangle> {

		protected RectangleConverter() {
			super(Rectangle.class);
		}

		@Override
		public Rectangle convertFromString(String value) {
			try {
				Rectangle returned = new Rectangle();
				StringTokenizer st = new StringTokenizer(value, ",");
				if (st.hasMoreTokens()) {
					returned.x = Integer.parseInt(st.nextToken());
				}
				if (st.hasMoreTokens()) {
					returned.y = Integer.parseInt(st.nextToken());
				}
				if (st.hasMoreTokens()) {
					returned.width = Integer.parseInt(st.nextToken());
				}
				if (st.hasMoreTokens()) {
					returned.height = Integer.parseInt(st.nextToken());
				}
				return returned;
			} catch (NumberFormatException e) {
				// Warns about the exception
				System.err.println("Supplied value is not parsable as a Rectangle:" + value);
				return null;
			}
		}

		@Override
		public String convertToString(Rectangle rect) {
			if (rect != null) {
				return rect.x + "," + rect.y + "," + rect.width + "," + rect.height;
			} else {
				return null;
			}
		}

	}

	/**
	 * Class defining how to convert String from/to Color
	 * 
	 * @author sguerin
	 */
	public static class ColorConverter extends Converter<Color> {

		protected ColorConverter() {
			super(Color.class);
		}

		@Override
		public Color convertFromString(String value) {
			return new Color(redFromString(value), greenFromString(value), blueFromString(value));
		}

		@Override
		public String convertToString(Color aColor) {
			return aColor.getRed() + "," + aColor.getGreen() + "," + aColor.getBlue();

		}

		private static int redFromString(String s) {
			return Integer.parseInt(s.substring(0, s.indexOf(",")));
		}

		private static int greenFromString(String s) {
			return Integer.parseInt(s.substring(s.indexOf(",") + 1, s.lastIndexOf(",")));
		}

		private static int blueFromString(String s) {
			return Integer.parseInt(s.substring(s.lastIndexOf(",") + 1));
		}

	}

	/**
	 * Class defining how to convert String from/to Font
	 * 
	 * @author sguerin
	 */
	public static class FontConverter extends Converter<Font> {

		protected FontConverter() {
			super(Font.class);
		}

		@Override
		public Font convertFromString(String value) {
			return new Font(nameFromString(value), styleFromString(value), sizeFromString(value));
		}

		@Override
		public String convertToString(Font aFont) {
			return aFont.getName() + "," + aFont.getStyle() + "," + aFont.getSize();
		}

		private static String nameFromString(String s) {
			return s.substring(0, s.indexOf(","));
		}

		private static int styleFromString(String s) {
			return Integer.parseInt(s.substring(s.indexOf(",") + 1, s.lastIndexOf(",")));
		}

		private static int sizeFromString(String s) {
			return Integer.parseInt(s.substring(s.lastIndexOf(",") + 1));
		}

	}

	/**
	 * Hereunder are all the non-static elements of this class. Only those should be used.
	 */

	private Hashtable<Class, Converter> converters = new Hashtable<Class, Converter>();

	private boolean isInitialized = false;

	@SuppressWarnings("unchecked")
	public <T> T _decodeObject(String value, Class<T> objectType) {
		if (!isInitialized) {
			_initialize();
		}
		if (value == null) {
			return null;
		}
		Converter<T> converter = _converterForClass(objectType);
		if (converter != null) {
			return converter.convertFromString(value);
		} else if (objectType.isEnum()) {
			try {
				return (T) Enum.valueOf((Class) objectType, value);
			} catch (IllegalArgumentException e) {
				System.err.println("Could not decode " + value + " as a " + objectType);
				return null;
			}
		} else {
			throw new InvalidDataException("Supplied value has no converter for type " + objectType.getName());
		}
	}

	public <T> String _encodeObject(T object) {
		if (!isInitialized) {
			_initialize();
		}
		if (object == null) {
			return null;
		}
		Converter converter = _converterForClass(object.getClass());
		if (converter != null) {
			return converter.convertToString(object);
		} else {
			if (object instanceof StringConvertable) {
				converter = ((StringConvertable) object).getConverter();
				if (converter != null) {
					// System.out.println ("Registering my own converter");
					_addConverter(converter);
					return converter.convertToString(object);
				}
			} else if (object instanceof Enum) {
				return ((Enum) object).name();
			}
			throw new InvalidDataException("Supplied value has no converter for type " + object.getClass().getName());
		}
	}

	public <T> Converter<T> _converterForClass(Class<T> objectType) {
		if (!isInitialized) {
			_initialize();
		}
		/*
		 * System.out.println ("I've got those converters:"); for (Enumeration e =
		 * converters.keys(); e.hasMoreElements();) { Class key =
		 * (Class)e.nextElement(); Converter converter =
		 * (Converter)converters.get(key); System.out.println ("Key: "+key+"
		 * Converter: "+converter.getConverterClass().getName()); }
		 */
		Converter returned;
		Class tryThis = objectType;
		boolean iAmAtTheTop = false;
		do {
			returned = converters.get(tryThis);
			iAmAtTheTop = tryThis.equals(Object.class);
			if (!iAmAtTheTop) {
				tryThis = tryThis.getSuperclass();
			}
		} while (returned == null && !iAmAtTheTop && tryThis != null);
		return returned;
	}

	public <T> boolean _isConvertable(Class<T> objectType) {
		if (!isInitialized) {
			_initialize();
		}
		return _converterForClass(objectType) != null;
	}

	public <T> boolean _isEncodable(Class<T> objectType) {
		return _isConvertable(objectType) || objectType.isEnum();
	}

	/**
	 * Sets date format, under the form <code>"yyyy.MM.dd G 'at' HH:mm:ss a zzz"</code>
	 */
	public void _setDateFormat(String aFormat) {
		DateConverter dc = (DateConverter) _converterForClass(Date.class);
		dc._dateFormat = aFormat;
	}

	public String _getDateFormat() {
		DateConverter dc = (DateConverter) _converterForClass(Date.class);
		return dc._dateFormat;
	}

	/**
	 * Return a string representation of a date, according to valid date format
	 */
	public String _getDateRepresentation(Date aDate) {
		DateConverter dc = (DateConverter) _converterForClass(Date.class);
		return dc.getDateRepresentation(aDate);
	}

	public <T> Converter<T> _addConverter(Converter<T> converter) {
		converters.put(converter.getConverterClass(), converter);
		return converter;
	}

	/**
	 * @param converter
	 */
	public void _removeConverter(Converter converter) {
		converters.remove(converter.getConverterClass());
	}

	public void _initialize() {
		if (!isInitialized) {
			_addConverter(new BooleanConverter());
			_addConverter(new IntegerConverter());
			_addConverter(new ShortConverter());
			_addConverter(new LongConverter());
			_addConverter(new FloatConverter());
			_addConverter(new DoubleConverter());
			_addConverter(new StringConverter());
			_addConverter(new DateConverter());
			_addConverter(new URLConverter());
			_addConverter(new FileConverter());
			_addConverter(new ClassConverter());
			_addConverter(new PointConverter());
			_addConverter(new RectangleConverter());
			_addConverter(new ColorConverter());
			_addConverter(new FontConverter());
			_addConverter(new NumberConverter());
			isInitialized = true;
		}
	}

	public static StringEncoder getDefaultInstance() {
		return defaultInstance;
	}

}
