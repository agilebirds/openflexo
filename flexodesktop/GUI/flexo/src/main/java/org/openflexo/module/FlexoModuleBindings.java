package org.openflexo.module;

import javax.inject.Singleton;

import org.openflexo.view.controller.FlexoController;
import org.openflexo.view.menu.FlexoMenuBar;

import com.google.inject.AbstractModule;

public abstract class FlexoModuleBindings extends AbstractModule {

	public abstract String getModuleName();

	public abstract <T extends FlexoController> Class<T> getFlexoControllerClass();

	public abstract <T extends FlexoModule> Class<T> getFlexoModuleClass();

	public abstract <T extends FlexoMenuBar> Class<T> getMenuBarClass();

	@Override
	protected void configure() {
		bind(getFlexoModuleClass()).in(Singleton.class);
		bind(getFlexoControllerClass()).in(Singleton.class);
		bind(getMenuBarClass()).in(Singleton.class);
	}

}
