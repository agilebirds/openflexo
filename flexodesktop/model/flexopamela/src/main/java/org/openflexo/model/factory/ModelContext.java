package org.openflexo.model.factory;

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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.StringTokenizer;

import javax.annotation.Nonnull;

import org.openflexo.model.exceptions.LockedContextException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.xml.InvalidDataException;

public class ModelContext {

	private Map<Class, ModelEntity> modelEntities;
	private Map<String, ModelEntity> modelEntitiesByXmlTag;
	private Map<Class<?>, Converter<?>> converters;
	private final Class<?> baseClass;
	private boolean locked;

	public ModelContext(@Nonnull Class<?> baseClass) throws ModelDefinitionException {
		this.baseClass = baseClass;
		modelEntities = new HashMap<Class, ModelEntity>();
		modelEntitiesByXmlTag = new HashMap<String, ModelEntity>();
		converters = new HashMap<Class<?>, ModelContext.Converter<?>>(DefaultConverter.getInstance().getConverters());
		importModelEntity(baseClass);
		locked = true;
		modelEntities = Collections.unmodifiableMap(modelEntities);
		modelEntitiesByXmlTag = Collections.unmodifiableMap(modelEntitiesByXmlTag);
		converters = Collections.unmodifiableMap(converters);
	}

	public ModelContext(Class<?>... baseClasses) throws ModelDefinitionException {
		this(null, ModelContextLibrary.getModelContext(Arrays.asList(baseClasses)));
	}

	public ModelContext(ModelContext... contexts) throws ModelDefinitionException {
		this(null, contexts);
	}

	public ModelContext(Class<?> baseClass, ModelContext... contexts) throws ModelDefinitionException {
		this(baseClass, Arrays.asList(contexts));
	}

	public ModelContext(Class<?> baseClass, List<ModelContext> contexts) throws ModelDefinitionException {
		this.baseClass = baseClass;
		modelEntities = new HashMap<Class, ModelEntity>();
		modelEntitiesByXmlTag = new HashMap<String, ModelEntity>();
		converters = new HashMap<Class<?>, ModelContext.Converter<?>>(DefaultConverter.getInstance().getConverters());
		for (ModelContext context : contexts) {
			for (Entry<String, ModelEntity> e : context.modelEntitiesByXmlTag.entrySet()) {
				ModelEntity entity = modelEntitiesByXmlTag.put(e.getKey(), e.getValue());
				// TODO: handle properly namespaces. Different namespaces allows to have identical tags
				// See also importModelEntity(Class<T>)
				if (entity != null && !entity.getImplementedInterface().equals(e.getValue().getImplementedInterface())) {
					throw new ModelDefinitionException(entity + " and " + e.getValue()
							+ " declare the same XML tag but not the same implemented interface");
				}
			}
			modelEntities.putAll(context.modelEntities);
			converters.putAll(context.converters);
		}
		if (baseClass != null) {
			importModelEntity(baseClass);
		}
		locked = true;
		modelEntities = Collections.unmodifiableMap(modelEntities);
		modelEntitiesByXmlTag = Collections.unmodifiableMap(modelEntitiesByXmlTag);
		converters = Collections.unmodifiableMap(converters);
	}

	public Class<?> getBaseClass() {
		return baseClass;
	}

	public boolean isLocked() {
		return locked;
	}

	public ModelEntity<?> getModelEntity(String xmlElementName) {
		return modelEntitiesByXmlTag.get(xmlElementName);
	}

	public Iterator<ModelEntity> getEntities() {
		return modelEntities.values().iterator();
	}

	public int getEntityCount() {
		return modelEntities.size();
	}

	public <I> ModelEntity<I> getModelEntity(Class<I> implementedInterface) {
		return modelEntities.get(implementedInterface);
	}

	public List<ModelEntity<?>> getUpperEntities(Object object) {
		List<ModelEntity<?>> entities = new ArrayList<ModelEntity<?>>();
		for (Class<?> i : object.getClass().getInterfaces()) {
			appendKnownEntities(entities, i);
		}
		return entities;
	}

	private void appendKnownEntities(List<ModelEntity<?>> entities, Class<?> i) {
		ModelEntity<?> modelEntity = getModelEntity(i);
		if (modelEntity != null && !entities.contains(i)) {
			entities.add(modelEntity);
		} else {
			for (Class<?> j : i.getInterfaces()) {
				appendKnownEntities(entities, j);
			}
		}
	}

	<T> ModelEntity<T> getModelEntity(Class<T> implementedInterface, boolean create) throws ModelDefinitionException {
		if (create) {
			return importModelEntity(implementedInterface);
		} else {
			return getModelEntity(implementedInterface);
		}
	}

	<T> ModelEntity<T> importModelEntity(Class<T> implementedInterface) throws ModelDefinitionException {
		if (locked) {
			throw new LockedContextException("You cannot modify a context that has been locked. Model entity cannot be added for class "
					+ implementedInterface.getName());
		}
		ModelEntity<T> returned = modelEntities.get(implementedInterface);
		if (returned == null && isModelEntity(implementedInterface)) {
			modelEntities.put(implementedInterface, returned = new ModelEntity<T>(implementedInterface, this));
			ModelEntity<?> put = modelEntitiesByXmlTag.put(returned.getXMLTag(), returned);
			// TODO: handle properly namespaces. Different namespaces allows to have identical tags
			// See also Modelcontext(List<Modelcontext>, Class<?>)
			if (put != null) {
				throw new ModelDefinitionException("Two entities define the same XMLTag '" + returned.getXMLTag()
						+ "'. Implemented interfaces: " + returned.getImplementedInterface().getName() + " "
						+ put.getImplementedInterface().getName());
			}
			returned.init();
		}
		return returned;
	}

	void addConverter(@Nonnull Converter<?> converter) {
		if (locked) {
			throw new LockedContextException("You cannot modify a context that has been locked");
		}
		converters.put(converter.getConverterClass(), converter);
	}

	public Map<Class<?>, Converter<?>> getConverters() {
		return Collections.unmodifiableMap(converters);
	}

	public boolean isStringConvertable(Class<?> type) {
		return type.isPrimitive() || converters.containsKey(type);
	}

	public boolean isModelEntity(Class<?> c) {
		return c.getAnnotation(org.openflexo.model.annotations.ModelEntity.class) != null;
	}

	public String debug() {
		StringBuffer returned = new StringBuffer();
		returned.append("*************** ModelContext ****************\n");
		returned.append("Entities number: " + modelEntities.size() + "\n");
		for (ModelEntity entity : modelEntities.values()) {
			returned.append("------------------- ").append(entity.getImplementedInterface().getSimpleName())
					.append(" -------------------\n");
			Iterator<ModelProperty> i;
			try {
				i = entity.getProperties();
			} catch (ModelDefinitionException e) {
				e.printStackTrace();
				continue;
			}
			while (i.hasNext()) {
				ModelProperty property = i.next();
				returned.append(property.getPropertyIdentifier()).append(" ").append(property.getCardinality()).append(" type=")
						.append(property.getType().getSimpleName()).append("\n");
			}
		}
		return returned.toString();
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

	private static class DefaultConverter {

		private static final DefaultConverter instance = new DefaultConverter();

		private static DefaultConverter getInstance() {
			return instance;
		}

		private Map<Class<?>, Converter<?>> baseConverters;

		public DefaultConverter() {
			baseConverters = new HashMap<Class<?>, ModelContext.Converter<?>>();
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
			baseConverters = Collections.unmodifiableMap(baseConverters);
		}

		private Map<Class<?>, Converter<?>> getConverters() {
			return baseConverters;
		}

		private void addConverter(Converter<?> converter) {
			baseConverters.put(converter.getConverterClass(), converter);
		}

		private static class EnumerationConverter<T> extends Converter<T> {
			private String _stringRepresationMethodName;

			private EnumerationConverter(Class<T> enumeration, String stringRepresentationMethodName) {
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
		private static class BooleanConverter extends Converter<Boolean> {

			private BooleanConverter() {
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
		private static class NumberConverter extends Converter<Number> {

			private NumberConverter() {
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
		private static class IntegerConverter extends Converter<Integer> {

			private IntegerConverter() {
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
		private static class ShortConverter extends Converter<Short> {

			private ShortConverter() {
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
		private static class LongConverter extends Converter<Long> {

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
		private static class FloatConverter extends Converter<Float> {

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
		private static class DoubleConverter extends Converter<Double> {

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
		private static class StringConverter extends Converter<String> {

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
		private static class DateConverter extends Converter<Date> {

			/** Specify date format */
			protected String _dateFormat = new SimpleDateFormat().toPattern();

			private DateConverter() {
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
		private static class URLConverter extends Converter<URL> {

			private URLConverter() {
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
		private static class FileConverter extends Converter<File> {

			private FileConverter() {
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
		private static class ClassConverter extends Converter<Class> {

			private ClassConverter() {
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
		private static class PointConverter extends Converter<Point> {

			private PointConverter() {
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
		private static class ColorConverter extends Converter<Color> {

			private ColorConverter() {
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
		private static class FontConverter extends Converter<Font> {

			private FontConverter() {
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

	}

}
