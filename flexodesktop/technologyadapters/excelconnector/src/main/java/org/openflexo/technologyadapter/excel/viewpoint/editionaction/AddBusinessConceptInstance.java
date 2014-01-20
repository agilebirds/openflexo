package org.openflexo.technologyadapter.excel.viewpoint.editionaction;

import java.lang.reflect.Type;

import org.openflexo.foundation.view.action.EditionSchemeAction;
import org.openflexo.foundation.viewpoint.AssignableAction;
import org.openflexo.foundation.viewpoint.annotations.FIBPanel;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;
import org.openflexo.technologyadapter.excel.SemanticsExcelModelSlot;
import org.openflexo.technologyadapter.excel.model.semantics.BusinessConceptInstance;

@FIBPanel("Fib/AddBusinessConceptInstancePanel.fib")
@ModelEntity
@ImplementationClass(AddBusinessConceptInstance.AddBusinessConceptInstanceImpl.class)
@XMLElement
public interface AddBusinessConceptInstance extends AssignableAction<SemanticsExcelModelSlot, BusinessConceptInstance> {

	public static abstract class AddBusinessConceptInstanceImpl extends
			AssignableActionImpl<SemanticsExcelModelSlot, BusinessConceptInstance> implements AddBusinessConceptInstance {

		@Override
		public Type getAssignableType() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public BusinessConceptInstance performAction(EditionSchemeAction action) {
			// TODO Auto-generated method stub
			return null;
		}

	}

}
