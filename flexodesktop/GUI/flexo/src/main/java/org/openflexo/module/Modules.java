package org.openflexo.module;

import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;

public class Modules {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger
			.getLogger(Modules.class.getPackage().getName());

	private static final Modules instance = new Modules();

	public static Modules getInstance() {
		return instance;
	}

	/**
	 * Vector of Module instance representing all available modules
	 */
	private ArrayList<Module> availableModules = new ArrayList<Module>();

	public Modules() {
		initialize();
	}

	public List<Module> getAvailableModules() {
		return availableModules;
	}

	/**
	 * @param moduleClass
	 *            a module implementation class
	 * @return the Module definition for the given moduleImplementation class or null if the Module is not available for the currentUserType
	 */
	public Module getModule(Class<? extends FlexoModule> moduleClass) {
		for (Module candidate : UserType.getCurrentUserType().getModules()) {
			if (moduleClass.equals(candidate.getModuleClass())) {
				return candidate;
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Module for " + moduleClass.getName() + " is not available in "
					+ UserType.getCurrentUserType().getBusinessName2());
		}
		return null;
	}

	/**
	 * @param moduleName
	 *            the name of a module
	 * @return the Module definition for the given moduleName or null if the Module is not available for the currentUserType or the module
	 *         name is unknown.
	 * @see #isAvailable(Module)
	 * @see UserType
	 */
	public Module getModule(String moduleName) {
		for (Module candidate : getAvailableModules()) {
			if (candidate.getName().equals(moduleName)) {
				return candidate;
			}
		}
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Module named " + moduleName + " is either unknown, either not available in "
					+ UserType.getCurrentUserType().getBusinessName2());
		}
		return null;
	}

	/**
	 * Given a list of modules, check if user type has right to use them and register them consequently
	 * 
	 */
	private void initialize() {
		for (Module module : UserType.getCurrentUserType().getModules()) {
			if (module.getModuleClass() != null) {
				registerModule(module);
			}
		}
	}

	/**
	 * Internally used to register module with class name moduleClass
	 * 
	 * @param module
	 *            the module to register
	 */
	private void registerModule(Module module) {
		if (module.register()) {
			availableModules.add(module);
		}
	}

}
