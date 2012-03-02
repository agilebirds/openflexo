package org.openflexo.sg.services.database;

import org.openflexo.foundation.sg.implmodel.GenerationService;

public interface DriverDefinitionProvider extends GenerationService {

    public String getDriverClassname();

    public String getDriverArtifactId();

    public String getDriverGroupId();

    public String getDriverVersion();
    
    public String getLobHandlerClassname();
}
