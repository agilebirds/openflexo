package org.openflexo.model.factory;

import java.util.Iterator;

import com.google.inject.AbstractModule;
import com.google.inject.Module;

/**
 * Contains configuration informations required to create an Injector based on direct instanciation
 * 
 * @author sylvain
 * 
 */
public class LegacyInjectionModule extends AbstractModule {

	private final ModelFactory modelFactory;
	private Module[] modules = null;

	/**
	 * @param fgeModelFactory
	 */
	public LegacyInjectionModule(ModelFactory modelFactory) {
		this.modelFactory = modelFactory;
	}

	/**
	 * @param fgeModelFactory
	 */
	public LegacyInjectionModule(ModelFactory fgeModelFactory, Module... modules) {
		modelFactory = fgeModelFactory;
		this.modules = modules;
	}

	@Override
	protected void configure() {
		if (modules != null) {
			for (Module m : modules) {
				install(m);
			}
		}
		Iterator<ModelEntity> i = modelFactory.getEntities();
		while (i.hasNext()) {
			ModelEntity entity = i.next();
			if (!entity.isAbstract() && entity.getImplementingClass() != null) {
				bind(entity.getImplementedInterface()).to(entity.getImplementingClass());
			}
		}
	}

}
