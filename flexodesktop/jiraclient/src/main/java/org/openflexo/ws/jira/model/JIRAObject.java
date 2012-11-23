package org.openflexo.ws.jira.model;

import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.lang.reflect.ParameterizedType;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class JIRAObject<J extends JIRAObject<J>> extends HashMap<String, Object> {

	/**
	 * 
	 */
	private static final long serialVersionUID = -3961683012433439153L;

	private transient J originalObject;

	/** The REST reference to this object */
	private String self;

	/** The id of the object, usually an integer, but this is represented as a string in JSON */
	private String id;

	/** The key of the object which is a string. */
	private String key;

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getKey() {
		return key;
	}

	public void setKey(String key) {
		this.key = key;
	}

	public String getSelf() {
		return self;
	}

	public void setSelf(String self) {
		this.self = self;
	}

	public Class<J> getTypedClass() {
		return (Class<J>) getClass();
	}

	public J getAsIdentityObject() throws InstantiationException, IllegalAccessException {
		J j = getTypedClass().newInstance();
		if (id != null) {
			j.setId(id);
		} else if (key != null) {
			j.setKey(key);
		} else {
			j.setSelf(self);
		}
		if (originalObject != null) {
			j.originalObject = originalObject;
		} else {
			j.originalObject = (J) this;
		}
		return j;
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o != null && o.getClass() == getClass() && ((JIRAObject<?>) o).id != null && ((JIRAObject<?>) o).id.equals(id);
	}

	@Override
	public int hashCode() {
		if (getId() != null) {
			return getId().hashCode();
		} else if (getKey() != null) {
			return getKey().hashCode();
		} else if (getSelf() != null) {
			return getSelf().hashCode();
		} else {
			return super.hashCode();
		}
	}

	public J restoreObject() {
		return originalObject;
	}

	public void automaticMutate() {

	}

	public <O extends JIRAObject> O mutate(Class<O> target) throws InstantiationException, IllegalAccessException, SecurityException,
			NoSuchFieldException {
		O o = target.newInstance();
		Class<?> klass = getClass();
		while (klass != null && JIRAObject.class.isAssignableFrom(klass)) {
			for (Field f : klass.getDeclaredFields()) {
				if (Modifier.isStatic(f.getModifiers())) {
					continue;
				}
				if (Modifier.isFinal(f.getModifiers())) {
					continue;
				}
				if (Modifier.isTransient(f.getModifiers())) {
					continue;
				}
				Object value = f.get(this);
				setMutatedValueForField(o, value, f.getName());
			}
			klass = klass.getSuperclass();
		}
		for (Map.Entry<String, Object> e : entrySet()) {
			Object value = e.getValue();
			setMutatedValueForField(o, value, e.getKey());
		}
		return o;
	}

	protected <O extends JIRAObject> void setMutatedValueForField(O o, Object value, String fieldName) throws IllegalAccessException,
			InstantiationException, IllegalArgumentException, SecurityException, NoSuchFieldException {
		Field field = null;
		Class<?> klass = o.getClass();
		while (field == null && klass != null) {
			try {
				field = klass.getDeclaredField(fieldName);
			} catch (NoSuchFieldException e) {
			} catch (SecurityException e) {
			}
			klass = klass.getSuperclass();
		}

		if (value != null) {
			if (field != null) {
				field.setAccessible(true);
				if (value instanceof JIRAObject && JIRAObject.class.isAssignableFrom(field.getType())) {
					JIRAObject jiraObject = (JIRAObject) value;
					field.set(o, jiraObject.mutate(field.getType()));
				} else if (value instanceof List
						&& List.class.isAssignableFrom(field.getGenericType().getClass())
						&& JIRAObject.class.isAssignableFrom(((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0]
								.getClass())) {
					List<? extends JIRAObject> list = (List<? extends JIRAObject>) value;
					List<JIRAObject> values = new ArrayList<JIRAObject>();
					for (JIRAObject jiraObject : list) {
						values.add(jiraObject.mutate((Class<? extends JIRAObject>) ((ParameterizedType) field.getGenericType())
								.getActualTypeArguments()[0]));

					}
					field.set(o, values);
				} else {
					field.set(o, value);
				}
			} else {
				o.put(fieldName, value);
			}
		}
	}

}
