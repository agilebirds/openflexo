package org.openflexo.fge;

import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;

@ModelEntity(isAbstract = true)
@ImplementationClass(FGEStyleImpl.class)
public interface FGEStyle extends FGEObject {

}
