package org.openflexo.foundation.toc;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public abstract class ModelObjectSection<T extends FlexoModelObject> extends TOCEntry {

	public static enum ModelObjectType {
		View {
			@Override
			public Type getType() {
				return org.openflexo.foundation.view.ViewDefinition.class;
			}
		},
		ViewFolder {
			@Override
			public Type getType() {
				return org.openflexo.foundation.view.ViewFolder.class;
			}
		},
		Process {
			@Override
			public Type getType() {
				return FlexoProcess.class;
			}
		},
		Role {
			@Override
			public Type getType() {
				return org.openflexo.foundation.wkf.Role.class;
			}
		},
		Entity {
			@Override
			public Type getType() {
				return DMEntity.class;
			}
		},
		OperationScreen {
			@Override
			public Type getType() {
				return OperationComponentDefinition.class;
			}
		},
		ERDiagram {
			@Override
			public Type getType() {
				return ERDiagram.class;
			}
		};

		public abstract Type getType();

	}

	private DataBinding<Object> value;

	public ModelObjectSection(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public ModelObjectSection(TOCData data) {
		super(data);
	}

	public abstract ModelObjectType getModelObjectType();

	public DataBinding<Object> getValue() {
		if (value == null) {
			value = new DataBinding<Object>(this, getModelObjectType().getType(), BindingDefinitionType.GET);
		}
		return value;
	}

	public void setValue(DataBinding<Object> value) {
		if (value != null) {
			value.setOwner(this);
			value.setDeclaredType(getModelObjectType().getType());
			value.setBindingDefinitionType(BindingDefinitionType.GET);
		}
		this.value = value;
	}

	@Override
	public FlexoModelObject getObject() {
		return getModelObject(false);
	}

	public T getModelObject(boolean forceResourceLoad) {
		if (getModelObjectReference() != null) {
			return (T) getModelObjectReference().getObject(forceResourceLoad);
		} else {
			return null;
		}
	}

	public void setModelObject(T modelObject) {
		System.out.println("set model object with " + modelObject);
		if (modelObjectReference != null) {
			modelObjectReference.delete();
			modelObjectReference = null;
		}
		if (modelObject != null) {
			modelObjectReference = new FlexoModelObjectReference<FlexoModelObject>(modelObject, this);
			modelObjectReference.setSerializeClassName(true);
		} else {
			modelObjectReference = null;
		}
	}

	private FlexoModelObjectReference modelObjectReference;

	public FlexoModelObjectReference getModelObjectReference() {
		return modelObjectReference;
	}

	public void setModelObjectReference(FlexoModelObjectReference objectReference) {
		if (this.modelObjectReference != null) {
			this.modelObjectReference = null;
		}
		this.modelObjectReference = objectReference;
		if (this.modelObjectReference != null) {
			this.modelObjectReference.setOwner(this);
		}
	}

	@Override
	public void notifyObjectLoaded(FlexoModelObjectReference reference) {
	}

	@Override
	public void objectCantBeFound(FlexoModelObjectReference reference) {
		setChanged();
		notifyObservers(new TOCModification(reference, null));
	}

	@Override
	public void objectDeleted(FlexoModelObjectReference reference) {
		setChanged();
		notifyObservers(new TOCModification(reference, null));
	}

	public boolean isModelObjectSection() {
		return true;
	}

	@Override
	public boolean getRenderContent() {
		return false;
	}

}
