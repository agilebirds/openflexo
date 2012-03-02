package org.openflexo.tm.postgres.impl;

import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.sg.services.database.DriverDefinitionProvider;

public class PostgresDriverDefinitionProvider implements DriverDefinitionProvider {

    private ImplementationModel implementationModel;
    private PostgresImplementation postgresImplementation;
    @Override
    public void init(ImplementationModel implementationModel) {
        this.implementationModel = implementationModel;
        postgresImplementation = (PostgresImplementation)implementationModel.getTechnologyModule(getTechnologyModuleName());
    }
    
    @Override
    public String getDriverClassname() {
        return postgresImplementation.getDriverClassname();
    }

    @Override
    public String getDriverArtifactId() {
        return postgresImplementation.getDriverArtifactId();
    }

    @Override
    public String getDriverGroupId() {
        return postgresImplementation.getDriverGroupId();
    }

    @Override
    public String getDriverVersion() {
        return postgresImplementation.getDriverVersion();
    }

    @Override
    public String getTechnologyModuleName() {
        return "Postgres";
    }

    @Override
    public String getLobHandlerClassname() {
        return "org.springframework.jdbc.support.lob.DefaultLobHandler";
    }
}
