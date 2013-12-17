package org.openflexo.foundation.toc;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.DataBinding;
import org.openflexo.foundation.AgileBirdsObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.utils.FlexoModelObjectReference;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public abstract class ModelObjectSection<T extends AgileBirdsObject> extends TOCEntry {

	public static enum ModelObjectType {
		View {
			@Override
			public Type getType() {
				return org.openflexo.foundation.view.View.class;
			}
		},
		RepositoryFolder {
			@Override
			public Type getType() {
				return org.openflexo.foundation.resource.RepositoryFolder.class;
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
			value = new DataBinding<Object>(this, getModelObjectType().getType(), DataBinding.BindingDefinitionType.GET);
		}
		return value;
	}

	public void setValue(DataBinding<Object> value) {
		if (value != null) {
			value.setOwner(this);
			value.setDeclaredType(getModelObjectType().getType());
			value.setBindingDefinitionType(DataBinding.BindingDefinitionType.GET);
		}
		this.value = value;
	}

	@Override
	public AgileBirdsObject getObject() {
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
		if (modelObjectReference != null) {
			modelObjectReference.delete(false);
			modelObjectReference = null;
		}
		if (modelObject != null) {
			modelObjectReference = new FlexoModelObjectReference<AgileBirdsObject>(modelObject, this);
			modelObjectReference.setSerializeClassName(true);
		} else {
			modelObjectReference = null;
		}
	}

	private FlexoModelObjectReference<?> modelObjectReference;

	public FlexoModelObjectReference<?> getModelObjectReference() {
		return modelObjectReference;
	}

	public void setModelObjectReference(FlexoModelObjectReference<?> objectReference) {
		if (this.modelObjectReference != null) {
			FlexoModelObjectReference<?> old = this.modelObjectReference;
			this.modelObjectReference = null;
			old.delete();
		}
		this.modelObjectReference = objectReference;
		if (this.modelObjectReference != null) {
			this.modelObjectReference.setOwner(this);
		}
	}

	@Override
	public void notifyObjectLoaded(FlexoModelObjectReference<?> reference) {
	}

	@Override
	public void objectCantBeFound(FlexoModelObjectReference<?> reference) {
		if (this.modelObjectReference == reference) {
			setModelObjectReference(null);
			setChanged();
			notifyObservers(new TOCModification(reference, null));
		}
	}

	@Override
	public void objectDeleted(FlexoModelObjectReference<?> reference) {
		if (reference == this.modelObjectReference) {
			setModelObjectReference(null);
			setChanged();
			notifyObservers(new TOCModification(reference, null));
		}
	}

	public boolean isModelObjectSection() {
		return true;
	}

	@Override
	public boolean getRenderContent() {
		return false;
	}

}
