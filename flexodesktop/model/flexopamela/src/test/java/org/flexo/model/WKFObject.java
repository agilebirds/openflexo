package org.flexo.model;

import org.openflexo.model.annotations.CloningStrategy;
import org.openflexo.model.annotations.CloningStrategy.StrategyType;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.IntegrityConstraint;
import org.openflexo.model.annotations.IntegrityConstraints;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.Setter;

@ModelEntity(isAbstract=true)
@IntegrityConstraints({
	@IntegrityConstraint("process != null")
})
public interface WKFObject extends FlexoModelObject {

	public static final String PROCESS = "process";

	@Getter(id = PROCESS)
	@CloningStrategy(StrategyType.IGNORE)
	public FlexoProcess getProcess();

	@Setter(id=PROCESS)
	public void setProcess(FlexoProcess aProcess);

	@Override
	@Setter(id=FlexoModelObject.FLEXO_ID)
	public void setFlexoID(String flexoID);
}
