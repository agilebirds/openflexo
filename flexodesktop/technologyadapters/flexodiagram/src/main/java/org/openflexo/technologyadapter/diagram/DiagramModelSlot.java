package org.openflexo.technologyadapter.diagram;

import java.util.logging.Logger;

import org.openflexo.foundation.technologyadapter.ModelSlot;
import org.openflexo.foundation.viewpoint.PatternRole;
import org.openflexo.model.annotations.Implementation;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.technologyadapter.diagram.fml.ConnectorPatternRole;
import org.openflexo.technologyadapter.diagram.fml.DiagramPatternRole;
import org.openflexo.technologyadapter.diagram.fml.ShapePatternRole;
import org.openflexo.technologyadapter.diagram.model.Diagram;

/**
 * Implemented by all ModelSlot class for the Openflexo built-in diagram technology adapter<br>
 * 
 * @author sylvain
 * 
 */
@ModelEntity
public interface DiagramModelSlot extends ModelSlot<Diagram> {

	@Implementation
	public abstract class DiagramModelSlotImpl implements DiagramModelSlot {

		private static final Logger logger = Logger.getLogger(DiagramModelSlot.class.getPackage().getName());

		@Override
		public <PR extends PatternRole<?>> String defaultPatternRoleName(Class<PR> patternRoleClass) {
			if (DiagramPatternRole.class.isAssignableFrom(patternRoleClass)) {
				return "diagram";
			} else if (ShapePatternRole.class.isAssignableFrom(patternRoleClass)) {
				return "shape";
			} else if (ConnectorPatternRole.class.isAssignableFrom(patternRoleClass)) {
				return "connector";
			}
			logger.warning("Unexpected pattern role: " + patternRoleClass.getName());
			return null;
		}
	}
}
