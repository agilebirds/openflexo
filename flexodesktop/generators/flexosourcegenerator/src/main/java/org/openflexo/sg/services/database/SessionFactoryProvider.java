package org.openflexo.sg.services.database;

import org.openflexo.foundation.sg.implmodel.GenerationService;

public interface SessionFactoryProvider extends GenerationService {

    public String getSessionFactoryClassname();

}
