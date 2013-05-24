/**
 * 
 */
package org.openflexo.model;

import java.util.Hashtable;
import java.util.Map;

import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.model.factory.ProxyMethodHandler;

import com.google.common.primitives.Primitives;

public class StringEncoder {
	private Map<Class<?>, Converter<?>> converters = new Hashtable<Class<?>, Converter<?>>();

	private ModelFactory modelFactory;

	public StringEncoder(ModelFactory modelFactory) {
		this.modelFactory = modelFactory;
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
	 * Hereunder are all the non-static elements of this class. Only those should be used.
	 */

	public <T> Converter<T> addConverter(Converter<T> converter) {
		converters.put(converter.getConverterClass(), converter);
		if (Primitives.isWrapperType(converter.getConverterClass())) {
			converters.put(Primitives.unwrap(converter.getConverterClass()), converter);
		}
		return converter;
	}

	/**
	 * @param converter
	 */
	public void removeConverter(Converter<?> converter) {
		converters.remove(converter.getConverterClass());
	}

	public <T> Converter<T> converterForClass(Class<T> objectType) {
		// 1. We try with custom converters
		Converter<T> converterForClass = converterForClass(objectType, converters);
		if (converterForClass == null) {
			// 2. We try with model-defined converters
			converterForClass = converterForClass(objectType, StringConverterLibrary.getInstance().getConverters());
		}
		return converterForClass;
	}

	public static <T> Converter<T> converterForClass(Class<T> objectType, Map<Class<?>, Converter<?>> convertersMap) {
		Converter<?> returned;
		Class<?> candidate = objectType;
		do {
			returned = convertersMap.get(candidate);
			if (candidate.equals(Object.class)) {
				candidate = null;
			} else {
				candidate = candidate.getSuperclass();
			}
		} while (returned == null && candidate != null);
		return (Converter<T>) returned;
	}

	public boolean isConvertable(Class<?> type) {
		return converterForClass(type) != null || type.isEnum();
	}

}