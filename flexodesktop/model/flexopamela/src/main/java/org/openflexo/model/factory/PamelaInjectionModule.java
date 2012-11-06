package org.openflexo.model.factory;

import java.util.Iterator;

import javax.inject.Inject;

import com.google.inject.AbstractModule;
import com.google.inject.Injector;
import com.google.inject.Module;
import com.google.inject.Provider;

/**
 * Contains configuration informations required to create an Injector based on PAMELA management support
 * 
 * @author sylvain
 * 
 */
public class PamelaInjectionModule extends AbstractModule {

	private final ModelFactory modelFactory;
	private Module[] modules = null;

	/**
	 * @param fgeModelFactory
	 */
	public PamelaInjectionModule(ModelFactory modelFactory) {
		this.modelFactory = modelFactory;
	}

	/**
	 * @param fgeModelFactory
	 */
	public PamelaInjectionModule(ModelFactory fgeModelFactory, Module... modules) {
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
			bind(entity.getImplementedInterface()).toProvider(new GenericProvider(entity));
			System.out.println("bind " + entity.getImplementedInterface() + " to " + entity.getImplementingClass());
			// Thread.dumpStack();
		}
	}

	public class GenericProvider<T> implements Provider<T> {

		@Inject
		Injector injector;

		private final ModelEntity c;

		protected GenericProvider(ModelEntity entity) {
			this.c = entity;
		}

		@Override
		public T get() {
			T returned = (T) modelFactory.newInstance(c.getImplementedInterface());
			if (c.getImplementationClass() != null) {
				injector.injectMembers(returned);
			}
			return returned;
		}
	}
}
