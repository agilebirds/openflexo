package org.openflexo.foundation.toc;

import java.lang.reflect.Type;
import java.util.List;

import org.openflexo.antar.binding.BindingDefinition;
import org.openflexo.antar.binding.BindingDefinition.BindingDefinitionType;
import org.openflexo.antar.binding.ParameterizedTypeImpl;
import org.openflexo.antar.binding.WilcardTypeImpl;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.ERDiagram;
import org.openflexo.foundation.ie.cl.OperationComponentDefinition;
import org.openflexo.foundation.toc.TOCDataBinding.TOCBindingAttribute;
import org.openflexo.foundation.wkf.FlexoProcess;
import org.openflexo.foundation.xml.FlexoTOCBuilder;

public abstract class IterableSection extends TOCEntry {

	private String iteratorName;

	// private String

	public static enum IterableSectionBindingAttribute implements TOCBindingAttribute {
		iteration, condition
	}

	public static enum IteratorType {
		View {
			@Override
			public Type getType() {
				return org.openflexo.foundation.view.View.class;
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
		OperationComponent {
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

	public IterableSection(FlexoTOCBuilder builder) {
		this(builder.tocData);
		initializeDeserialization(builder);
	}

	public IterableSection(TOCData data) {
		super(data);
	}

	public abstract IteratorType getIteratorType();

	private TOCDataBinding iteration;

	private Type LIST_BINDING_TYPE;

	private Type getListBindingType() {
		if (LIST_BINDING_TYPE == null) {
			LIST_BINDING_TYPE = new ParameterizedTypeImpl(List.class, new WilcardTypeImpl(getIteratorType().getType()));
		}
		return LIST_BINDING_TYPE;
	}

	private BindingDefinition ITERATION = new BindingDefinition("iteration", Object.class, BindingDefinitionType.GET, false) {
		@Override
		public Type getType() {
			return getListBindingType();
		};
	};

	public BindingDefinition getIterationBindingDefinition() {
		return ITERATION;
	}

	public TOCDataBinding getIteration() {
		if (iteration == null) {
			iteration = new TOCDataBinding(this, IterableSectionBindingAttribute.iteration, getIterationBindingDefinition());
		}
		return iteration;
	}

	public void setIteration(TOCDataBinding iteration) {
		iteration.setOwner(this);
		iteration.setBindingAttribute(IterableSectionBindingAttribute.iteration);
		iteration.setBindingDefinition(getIterationBindingDefinition());
		this.iteration = iteration;
	}

}
