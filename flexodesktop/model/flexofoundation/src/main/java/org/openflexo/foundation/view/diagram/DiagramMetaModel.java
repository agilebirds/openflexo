package org.openflexo.foundation.view.diagram;

import org.openflexo.foundation.technologyadapter.FlexoMetaModel;

public interface DiagramMetaModel extends FlexoMetaModel {

	public final static DiagramMetaModel INSTANCE = new DiagramMetaModel() {

		@Override
		public void setIsReadOnly(boolean b) {
		}

		@Override
		public boolean isReadOnly() {
			return true;
		}

		@Override
		public String getURI() {
			return "http://www.openflexo.org/metamodels/diagrammetamodel";
		}
	};
}
