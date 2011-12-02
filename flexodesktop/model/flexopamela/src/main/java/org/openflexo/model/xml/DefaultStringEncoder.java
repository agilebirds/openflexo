/**
 * 
 */
package org.openflexo.model.xml;

import java.awt.Color;
import java.awt.Font;
import java.awt.Point;
import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Hashtable;
import java.util.Map;
import java.util.StringTokenizer;

import org.openflexo.model.factory.ModelFactory;
import org.openflexo.model.factory.ProxyMethodHandler;

public class DefaultStringEncoder {
	private Map<Class, Converter> converters = new Hashtable<Class, Converter>();

	private ModelFactory modelFactory;

	public DefaultStringEncoder(ModelFactory modelFactory) {
		this.modelFactory = modelFactory;
		addConverter(new BooleanConverter());
		addConverter(new IntegerConverter());
		addConverter(new ShortConverter());
		addConverter(new LongConverter());
		addConverter(new FloatConverter());
		addConverter(new DoubleConverter());
		addConverter(new StringConverter());
		addConverter(new DateConverter());
		addConverter(new URLConverter());
		addConverter(new FileConverter());
		addConverter(new ClassConverter());
		addConverter(new PointConverter());
		addConverter(new ColorConverter());
		addConverter(new FontConverter());
		addConverter(new NumberConverter());

	}

	public String toString(Object object) throws InvalidDataException {
		if (object == null) {
			return null;
		}
		Class<? extends Object> aClass = object.getClass();
		ProxyMethodHandler<?> handler = null;
		if (modelFactory != null) {
			handler = modelFactory.getHandler(object);
		}
		if (handler != null) {
			aClass = handler.getModelEntity().getImplementedInterface();
		}
		Converter converter = converterForClass(aClass);
		if (converter != null) {
			return converter.convertToString(object);
		} else {
			if (object instanceof Enum) {
				return ((Enum) object).name();
			}
			throw new InvalidDataException("Supplied value has no converter for type " + aClass.getName());
		}
	}

	public <T> T fromString(Class<T> type, String value) throws InvalidDataException {
		if (value == null) {
			return null;
		}
		Converter<T> converter = converterForClass(type);
		if (converter != null) {
			return converter.convertFromString(value, modelFactory);
		} else if (type.isEnum()) {
			try {
				return (T) Enum.valueOf((Class) type, value);
			} catch (IllegalArgumentException e) {
				System.err.println("Could not decode " + value + " as a " + type);
				return null;
			}
		} else {
			throw new InvalidDataException("Supplied value has no converter for type " + type.getName());
		}
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

		public abstract T convertFromString(String value, ModelFactory factory) throws InvalidDataException;

		public abstract String convertToString(T value);

	}

	public static class EnumerationConverter<T> extends Converter<T> {
		private String _stringRepresationMethodName;

		public EnumerationConverter(Class<T> enumeration, String stringRepresentationMethodName) {
			super(enumeration);
			_stringRepresationMethodName = stringRepresentationMethodName;
		}

		@Override
		public T convertFromString(String value, ModelFactory factory) {
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
		public Boolean convertFromString(String value, ModelFactory factory) {
			return Boolean.valueOf(value.equalsIgnoreCase("true") || value.equalsIgnoreCase("yes"));
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
		public Number convertFromString(String value, ModelFactory factory) {
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
		public Integer convertFromString(String value, ModelFactory factory) {
			return Integer.valueOf(value);
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
		public Short convertFromString(String value, ModelFactory factory) {
			return Short.valueOf(value);
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
		public Long convertFromString(String value, ModelFactory factory) {
			return Long.valueOf(value);
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
		public Float convertFromString(String value, ModelFactory factory) {
			return Float.valueOf(value);
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
		public Double convertFromString(String value, ModelFactory factory) {
			return Double.valueOf(value);
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
		public String convertFromString(String value, ModelFactory factory) {
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
		public Date convertFromString(String value, ModelFactory factory) {
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
		public URL convertFromString(String value, ModelFactory factory) {
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
		public File convertFromString(String value, ModelFactory factory) {
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
		public Class<?> convertFromString(String value, ModelFactory factory) throws InvalidDataException {
			try {
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
		public Point convertFromString(String value, ModelFactory factory) {
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
	 * Class defining how to convert String from/to Color
	 * 
	 * @author sguerin
	 */
	public static class ColorConverter extends Converter<Color> {

		protected ColorConverter() {
			super(Color.class);
		}

		@Override
		public Color convertFromString(String value, ModelFactory factory) {
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
		public Font convertFromString(String value, ModelFactory factory) {
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

	public class AnnotatedConverter<I> extends Converter<I> {

		private Converter<I> annotatedConverter;

		public AnnotatedConverter(Converter<I> annotatedConverter, Class<I> aClass) {
			super(aClass);
			this.annotatedConverter = annotatedConverter;
		}

		@Override
		public I convertFromString(String value, ModelFactory factory) throws InvalidDataException {
			return annotatedConverter.convertFromString(value, factory);
		}

		@Override
		public String convertToString(I value) {
			return annotatedConverter.convertToString(value);
		}

	}

	/**
	 * Hereunder are all the non-static elements of this class. Only those should be used.
	 */

	public <T> Converter<T> addConverter(Converter<T> converter) {
		converters.put(converter.getConverterClass(), converter);
		return converter;
	}

	/**
	 * @param converter
	 */
	public void removeConverter(Converter<?> converter) {
		converters.remove(converter.getConverterClass());
	}

	public <T> Converter<T> converterForClass(Class<T> objectType) {
		if (objectType.isPrimitive()) {
			if (objectType.equals(Integer.TYPE)) {
				return (Converter<T>) converterForClass(Integer.class);
			}
			if (objectType.equals(Long.TYPE)) {
				return (Converter<T>) converterForClass(Long.class);
			}
			if (objectType.equals(Short.TYPE)) {
				return (Converter<T>) converterForClass(Short.class);
			}
			if (objectType.equals(Byte.TYPE)) {
				return (Converter<T>) converterForClass(Byte.class);
			}
			if (objectType.equals(Double.TYPE)) {
				return (Converter<T>) converterForClass(Double.class);
			}
			if (objectType.equals(Float.TYPE)) {
				return (Converter<T>) converterForClass(Float.class);
			}
			if (objectType.equals(Boolean.TYPE)) {
				return (Converter<T>) converterForClass(Boolean.class);
			}
			if (objectType.equals(Character.TYPE)) {
				return (Converter<T>) converterForClass(Character.class);
			}
		}
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

	public boolean isConvertable(Class<?> type) {
		return converterForClass(type) != null || type.isEnum();
	}

}