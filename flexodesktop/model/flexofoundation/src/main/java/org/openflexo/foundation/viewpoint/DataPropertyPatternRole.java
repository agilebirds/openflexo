package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;

import org.openflexo.foundation.ontology.BuiltInDataType;
import org.openflexo.foundation.ontology.IFlexoOntologyDataProperty;
import org.openflexo.foundation.view.ActorReference;
import org.openflexo.foundation.view.ConceptActorReference;
import org.openflexo.foundation.view.EditionPatternInstance;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;

@ModelEntity(isAbstract = true)
@ImplementationClass(DataPropertyPatternRole.DataPropertyPatternRoleImpl.class)
public abstract interface DataPropertyPatternRole<P extends IFlexoOntologyDataProperty> extends PropertyPatternRole<P> {

	@PropertyIdentifier(type = BuiltInDataType.class)
	public static final String DATA_TYPE_KEY = "dataType";

	@Getter(value = DATA_TYPE_KEY)
	@XMLAttribute
	public BuiltInDataType getDataType();

	@Setter(DATA_TYPE_KEY)
	public void setDataType(BuiltInDataType dataType);

	public static abstract class DataPropertyPatternRoleImpl<P extends IFlexoOntologyDataProperty> extends PropertyPatternRoleImpl<P>
			implements DataPropertyPatternRole<P> {

		private BuiltInDataType dataType;

		public DataPropertyPatternRoleImpl() {
			super();
		}

		@Override
		public Type getType() {
			if (getParentProperty() == null) {
				return IFlexoOntologyDataProperty.class;
			}
			return super.getType();
		}

		@Override
		public String getPreciseType() {
			if (getParentProperty() != null) {
				return getParentProperty().getName();
			}
			return "";
		}

		@Override
		public IFlexoOntologyDataProperty getParentProperty() {
			return (IFlexoOntologyDataProperty) super.getParentProperty();
		}

		public void setParentProperty(IFlexoOntologyDataProperty ontologyProperty) {
			super.setParentProperty(ontologyProperty);
		}

		@Override
		public BuiltInDataType getDataType() {
			return dataType;
		}

		@Override
		public void setDataType(BuiltInDataType dataType) {
			this.dataType = dataType;
		}

		@Override
		public ActorReference<P> makeActorReference(P object, EditionPatternInstance epi) {
			return new ConceptActorReference<P>(object, this, epi);
		}
	}
}
