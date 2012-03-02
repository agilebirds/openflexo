package org.openflexo.tm.bpslib.impl;

import org.openflexo.foundation.sg.implmodel.ImplementationModel;
import org.openflexo.sg.services.database.SessionFactoryProvider;

public class BpsSessionFactoryProvider implements SessionFactoryProvider {

    @Override
    public String getSessionFactoryClassname() {
        return "org.springframework.orm.hibernate3.annotation.AnnotationSessionFactoryBean";
    }


    @Override
    public String getTechnologyModuleName() {
        return "BpsLib";
    }

    @Override
    public void init(ImplementationModel implementationModel) {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
