package org.openflexo.ws.jira;

import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.lang.reflect.TypeVariable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.openflexo.ws.jira.model.JIRAObject;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.annotations.SerializedName;
import com.google.gson.internal.$Gson$Types;
import com.google.gson.internal.Primitives;
import com.google.gson.internal.bind.ReflectiveTypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

public class JiraObjectTypeAdapterFactory implements TypeAdapterFactory {

	public class TypeAdapterRuntimeTypeWrapper<T> extends TypeAdapter<T> {
		private final Gson context;
		private final TypeAdapter<T> delegate;
		private final Type type;

		TypeAdapterRuntimeTypeWrapper(Gson context, TypeAdapter<T> delegate, Type type) {
			this.context = context;
			this.delegate = delegate;
			this.type = type;
		}

		@Override
		public T read(JsonReader in) throws IOException {
			return delegate.read(in);
		}

		@SuppressWarnings({ "rawtypes", "unchecked" })
		@Override
		public void write(JsonWriter out, T value) throws IOException {
			// Order of preference for choosing type adapters
			// First preference: a type adapter registered for the runtime type
			// Second preference: a type adapter registered for the declared type
			// Third preference: reflective type adapter for the runtime type (if it is a sub class of the declared type)
			// Fourth preference: reflective type adapter for the declared type

			TypeAdapter chosen = delegate;
			Type runtimeType = getRuntimeTypeIfMoreSpecific(type, value);
			if (runtimeType != type) {
				TypeAdapter runtimeTypeAdapter = context.getAdapter(TypeToken.get(runtimeType));
				if (!(runtimeTypeAdapter instanceof ReflectiveTypeAdapterFactory.Adapter)) {
					// The user registered a type adapter for the runtime type, so we will use that
					chosen = runtimeTypeAdapter;
				} else if (!(delegate instanceof ReflectiveTypeAdapterFactory.Adapter)) {
					// The user registered a type adapter for Base class, so we prefer it over the
					// reflective type adapter for the runtime type
					chosen = delegate;
				} else {
					// Use the type adapter for runtime type
					chosen = runtimeTypeAdapter;
				}
			}
			chosen.write(out, value);
		}

		/**
		 * Finds a compatible runtime type if it is more specific
		 */
		private Type getRuntimeTypeIfMoreSpecific(Type type, Object value) {
			if (value != null && (type == Object.class || type instanceof TypeVariable<?> || type instanceof Class<?>)) {
				type = value.getClass();
			}
			return type;
		}
	}

	private String getFieldName(Field f) {
		SerializedName serializedName = f.getAnnotation(SerializedName.class);
		return serializedName == null ? f.getName() : serializedName.value();
	}

	@Override
	public <T> TypeAdapter<T> create(Gson gson, final TypeToken<T> type) {
		Class<?> raw = type.getRawType();

		if (JIRAObject.class.isAssignableFrom(raw)) {
			Type valueType = $Gson$Types.getMapKeyAndValueTypes(type.getType(), raw)[1];
			TypeAdapter<Object> valueAdapter = (TypeAdapter<Object>) gson.getAdapter(TypeToken.get(valueType));
			return new Adapter(type, new TypeAdapterRuntimeTypeWrapper(gson, valueAdapter, valueType), getBoundFields(gson, type, raw));
		}
		if (List.class.isAssignableFrom(raw)) {
			Type parameter = ((ParameterizedType) type.getType()).getActualTypeArguments()[0];
			if (parameter instanceof TypeVariable) {
				Type type2 = ((TypeVariable) parameter).getBounds()[0];
				if (JIRAObject.class.isAssignableFrom($Gson$Types.getRawType(type2))) {
					TypeAdapter<Collection> collectionAdapter = gson.getAdapter(Collection.class);
					TypeAdapter<JIRAObject> jiraObjectAdapter = gson.getAdapter(JIRAObject.class);
					return new JIRAObjectListAdapter<T>(collectionAdapter, jiraObjectAdapter);
				}
			}
		}
		return null;
	}

	private static class JIRAObjectListAdapter<T> extends TypeAdapter<T> {

		private final TypeAdapter<Collection> collectionAdapter;
		private final TypeAdapter<JIRAObject> jiraObjectAdapter;

		public JIRAObjectListAdapter(TypeAdapter<Collection> collectionAdapter, TypeAdapter<JIRAObject> jiraObjectAdapter) {
			this.collectionAdapter = collectionAdapter;
			this.jiraObjectAdapter = jiraObjectAdapter;
		}

		@Override
		public void write(JsonWriter out, T value) throws IOException {
			collectionAdapter.write(out, (Collection) value);
		}

		@Override
		public T read(JsonReader in) throws IOException {
			if (in.peek() == JsonToken.NULL) {
				in.nextNull();
				return null;
			}

			Collection collection = new ArrayList();
			in.beginArray();
			while (in.hasNext()) {
				JIRAObject<?> instance = jiraObjectAdapter.read(in);
				collection.add(instance);
			}
			in.endArray();
			return (T) collection;
		}

	}

	private JiraObjectTypeAdapterFactory.BoundField createBoundField(final Gson context, final Field field, final String name,
			final TypeToken<?> fieldType) {
		final boolean isPrimitive = Primitives.isPrimitive(fieldType.getRawType());

		// special casing primitives here saves ~5% on Android...
		return new JiraObjectTypeAdapterFactory.BoundField(name) {
			final TypeAdapter<?> typeAdapter = context.getAdapter(fieldType);

			@SuppressWarnings({ "unchecked", "rawtypes" })
			// the type adapter and field type always agree
			@Override
			void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException {
				Object fieldValue = field.get(value);
				TypeAdapter t = new TypeAdapterRuntimeTypeWrapper(context, this.typeAdapter, fieldType.getType());
				t.write(writer, fieldValue);
			}

			@Override
			void read(JsonReader reader, Object value) throws IOException, IllegalAccessException {
				Object fieldValue = typeAdapter.read(reader);
				if (fieldValue != null || !isPrimitive) {
					field.set(value, fieldValue);
				}
			}
		};
	}

	private Map<String, BoundField> getBoundFields(Gson context, TypeToken<?> type, Class<?> raw) {
		Map<String, BoundField> result = new LinkedHashMap<String, BoundField>();
		if (raw.isInterface()) {
			return result;
		}

		Type declaredType = type.getType();
		while (raw != null && raw != HashMap.class) {
			Field[] fields = raw.getDeclaredFields();
			for (Field field : fields) {
				if (Modifier.isStatic(field.getModifiers()) || Modifier.isFinal(field.getModifiers())
						|| Modifier.isTransient(field.getModifiers())) {
					continue;
				}
				field.setAccessible(true);
				Type fieldType = $Gson$Types.resolve(type.getType(), raw, field.getGenericType());
				BoundField boundField = createBoundField(context, field, getFieldName(field), TypeToken.get(fieldType));
				BoundField previous = result.put(boundField.name, boundField);
				if (previous != null) {
					throw new IllegalArgumentException(declaredType + " declares multiple JSON fields named " + previous.name);
				}
			}
			type = TypeToken.get($Gson$Types.resolve(type.getType(), raw, raw.getGenericSuperclass()));
			raw = type.getRawType();
		}
		return result;
	}

	static abstract class BoundField {
		final String name;

		protected BoundField(String name) {
			this.name = name;
		}

		abstract void write(JsonWriter writer, Object value) throws IOException, IllegalAccessException;

		abstract void read(JsonReader reader, Object value) throws IOException, IllegalAccessException;
	}

	public final class Adapter<T extends JIRAObject<T>> extends TypeAdapter<T> {
		private final Map<String, BoundField> boundFields;
		private final TypeToken<T> type;
		private final TypeAdapter<Object> valueTypeAdapter;

		public Adapter(TypeToken<T> type, TypeAdapter<Object> valueTypeAdapter, Map<String, BoundField> boundFields) {
			this.type = type;
			this.valueTypeAdapter = valueTypeAdapter;
			this.boundFields = boundFields;
		}

		@Override
		public T read(JsonReader in) throws IOException {
			if (in.peek() == JsonToken.NULL) {
				in.nextNull();
				return null;
			}

			T instance;
			try {
				instance = (T) type.getRawType().newInstance();
			} catch (InstantiationException e1) {
				throw new RuntimeException(e1);
			} catch (IllegalAccessException e1) {
				throw new RuntimeException(e1);
			}

			try {
				in.beginObject();
				while (in.hasNext()) {
					String name = in.nextName();
					BoundField field = boundFields.get(name);
					if (field == null) {
						instance.put(name, valueTypeAdapter.read(in));
					} else {
						field.read(in, instance);
					}
				}
			} catch (IllegalStateException e) {
				throw new JsonSyntaxException(e);
			} catch (IllegalAccessException e) {
				throw new AssertionError(e);
			}
			in.endObject();
			return instance;
		}

		@Override
		public void write(JsonWriter out, T value) throws IOException {
			if (value == null) {
				out.nullValue(); // TODO: better policy here?
				return;
			}

			out.beginObject();
			try {
				for (BoundField boundField : boundFields.values()) {
					out.name(boundField.name);
					boundField.write(out, value);
				}
				for (Map.Entry<String, Object> e : value.entrySet()) {
					out.name(e.getKey());
					valueTypeAdapter.write(out, e.getValue());
				}
			} catch (IllegalAccessException e) {
				throw new AssertionError();
			}
			out.endObject();
		}
	}
}
