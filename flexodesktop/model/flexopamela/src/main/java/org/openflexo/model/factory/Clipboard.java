package org.openflexo.model.factory;

import java.util.List;

public class Clipboard {

	private ModelFactory modelFactory;
	private Object[] originalContents;
	private Object contents;
	private boolean isSingleObject;

	protected Clipboard(ModelFactory modelFactory, Object... objects) throws ModelExecutionException, ModelDefinitionException,
			CloneNotSupportedException {
		this.modelFactory = modelFactory;
		this.originalContents = objects;
		if (objects == null || objects.length == 0) {
			new ClipboardOperationException("Cannot build an empty Clipboard");
		}
		isSingleObject = objects.length == 1;
		// TODO: This should rather be done when pasting instead of cloning immediately
		if (isSingleObject) {
			Object object = objects[0];
			contents = modelFactory.getHandler(object).cloneObject(objects);
		} else {
			contents = modelFactory.getHandler(objects[0]).cloneObjects(objects);
		}
	}

	public ModelFactory getModelFactory() {
		return modelFactory;
	}

	public Object[] getOriginalContents() {
		return originalContents;
	}

	public Object getContents() {
		return contents;
	}

	public boolean isSingleObject() {
		return isSingleObject;
	}

	public Class<?> getType() {
		if (isSingleObject()) {
			return getContents().getClass();
		} else {
			return ((List) getContents()).get(0).getClass();
		}
	}

	public String debug() {
		StringBuffer returned = new StringBuffer();
		returned.append("*************** Clipboard ****************\n");
		returned.append("Single object: " + isSingleObject() + "\n");
		if (isSingleObject()) {
			returned.append("------------------- " + contents + " -------------------\n");
			List<Object> embeddedList = modelFactory.getEmbeddedObjects(contents);
			for (Object e : embeddedList) {
				returned.append(Integer.toHexString(e.hashCode()) + " Embedded: " + e + "\n");
			}
		} else {
			List contentsList = (List) contents;
			for (Object object : contentsList) {
				returned.append("------------------- " + object + " -------------------\n");
				List<Object> embeddedList = modelFactory.getEmbeddedObjects(object, contentsList.toArray());
				for (Object e : embeddedList) {
					returned.append(Integer.toHexString(e.hashCode()) + " Embedded: " + e + "\n");
				}
			}
		}
		return returned.toString();
	}

	/**
	 * Called when clipboard has been used somewhere. Copy again contents for a future use
	 * 
	 * @throws ModelExecutionException
	 * @throws ModelDefinitionException
	 * @throws CloneNotSupportedException
	 */
	public void consume() throws ModelExecutionException, ModelDefinitionException, CloneNotSupportedException {
		if (isSingleObject) {
			contents = modelFactory.getHandler(contents).cloneObject(contents);
		} else {
			contents = modelFactory.getHandler(((List) contents).get(0)).cloneObjects(((List) contents).toArray());
		}
	}
}
