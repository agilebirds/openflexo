package org.openflexo.foundation.view.diagram;

import org.openflexo.foundation.resource.FlexoResource;
import org.openflexo.foundation.technologyadapter.FlexoMetaModel;
import org.openflexo.foundation.technologyadapter.TechnologyAdapter;

public interface DiagramMetaModel extends FlexoMetaModel<DiagramMetaModel> {

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

		@Override
		public Object getObject(String objectURI) {
			return null;
		}

		@Override
		public TechnologyAdapter<?, ?> getTechnologyAdapter() {
			// TODO
			return null;
		}

		@Override
		public FlexoResource<DiagramMetaModel> getResource() {
			return null;
		}

		@Override
		public void setResource(FlexoResource<DiagramMetaModel> resource) {
		}

		@Override
		public void setIsModified() {
		}

		@Override
		public void clearIsModified() {
		}

		@Override
		public void clearIsModified(boolean clearLastMemoryUpdate) {
		}
	};
}
