package org.openflexo.foundation;

import java.io.File;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.fib.utils.LocalizedDelegateGUIImpl;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.localization.Language;
import org.openflexo.toolbox.FileResource;

/**
 * This is the general Openflexo localized implementation<br>
 * Default localized directory is managed here
 * 
 * @author sylvain
 * 
 */
public class FlexoMainLocalizer extends LocalizedDelegateGUIImpl {

	private static final Logger logger = Logger.getLogger(FlexoLocalization.class.getPackage().getName());

	public static final String LOCALIZATION_DIRNAME = "Localized";

	private static File _localizedDirectory = null;

	/**
	 * Return directory where localized dictionnaries for main localizer are stored
	 * 
	 * @return
	 */
	private static File getMainLocalizerLocalizedDirectory() {
		if (_localizedDirectory == null) {
			_localizedDirectory = new FileResource(LOCALIZATION_DIRNAME);

			if (logger.isLoggable(Level.FINE)) {
				logger.fine("Setting localized directory" + _localizedDirectory.getAbsolutePath());
			}
		}
		return _localizedDirectory;
	}

	public FlexoMainLocalizer() {
		super(getMainLocalizerLocalizedDirectory(), null, false);
	}

	public static void main(String[] args) {
		FlexoLocalization.initWith(new FlexoMainLocalizer());
		System.out.println("Returning " + FlexoLocalization.localizedForKeyAndLanguage("save", Language.FRENCH));
	}
}
