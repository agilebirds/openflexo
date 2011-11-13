/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.logging;

import java.io.File;
import java.io.IOException;
import java.util.logging.LogManager;

import org.openflexo.components.AskParametersDialog;
import org.openflexo.foundation.param.CheckboxParameter;
import org.openflexo.foundation.param.FileParameter;
import org.openflexo.foundation.param.ParameterDefinition;
import org.openflexo.foundation.param.ParametersModel;
import org.openflexo.foundation.param.RadioButtonListParameter;
import org.openflexo.foundation.param.TextFieldParameter;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.properties.FlexoProperties;
import org.openflexo.toolbox.FileResource;
import org.openflexo.view.controller.FlexoController;

public class LoggingConfigurationWindow {

	RadioButtonListParameter<String> logConfigType;
	RadioButtonListParameter<String> logLevelSelector;
	CheckboxParameter includeTraces;
	TextFieldParameter logCountTextField;
	FileParameter customFileChooser;

	public LoggingConfigurationWindow() {
		super();
	}

	private String getDefaultRadioValue(String l) {
		if (l.equals("SEVERE"))
			return FlexoLocalization.localizedForKey("log_severe");
		if (l.equals("WARNING"))
			return FlexoLocalization.localizedForKey("log_warning");
		if (l.equals("INFO"))
			return FlexoLocalization.localizedForKey("log_info");
		if (l.equals("FINE"))
			return FlexoLocalization.localizedForKey("log_fine");
		if (l.equals("FINER"))
			return FlexoLocalization.localizedForKey("log_finer");
		if (l.equals("FINEST"))
			return FlexoLocalization.localizedForKey("log_finest");
		return FlexoLocalization.localizedForKey("log_severe");
	}

	public void askLoggingConfiguration() {

		ParameterDefinition[] parameters = new ParameterDefinition[5];

		logCountTextField = new TextFieldParameter("logCount", "number_of_logs_to_keep", "" + FlexoProperties.instance().getMaxLogCount());
		parameters[0] = logCountTextField;

		includeTraces = new CheckboxParameter("includeTraces", "keep_log_trace_in_memory", FlexoProperties.instance().isLoggingTrace());
		parameters[1] = includeTraces;

		String USE_DEFAULT_LOG_CONFIG = FlexoLocalization.localizedForKey("use_a_default_log_config");
		String USE_CUSTOM_LOG_CONFIG = FlexoLocalization.localizedForKey("use_a_custom_log_config");
		String[] modes = { USE_DEFAULT_LOG_CONFIG, USE_CUSTOM_LOG_CONFIG };
		logConfigType = new RadioButtonListParameter<String>("mode", "select_a_choice",
				FlexoProperties.instance().getCustomLoggingFile() == null ? USE_DEFAULT_LOG_CONFIG : USE_CUSTOM_LOG_CONFIG, modes);
		parameters[2] = logConfigType;

		String LOG_SEVERE = FlexoLocalization.localizedForKey("log_severe");
		String LOG_WARNING = FlexoLocalization.localizedForKey("log_warning");
		String LOG_INFO = FlexoLocalization.localizedForKey("log_info");
		String LOG_FINE = FlexoLocalization.localizedForKey("log_fine");
		String LOG_FINER = FlexoLocalization.localizedForKey("log_finer");
		String LOG_FINEST = FlexoLocalization.localizedForKey("log_finest");
		String[] logLevel = { LOG_SEVERE, LOG_WARNING, LOG_INFO, LOG_FINE, LOG_FINER, LOG_FINEST };
		logLevelSelector = new RadioButtonListParameter<String>("logLevel", "select_a_choice", getDefaultRadioValue(FlexoProperties
				.instance().getDefaultLoggingLevel()), logLevel);
		parameters[3] = logLevelSelector;
		parameters[3].setDepends("mode");
		parameters[3].setConditional("mode=" + '"' + USE_DEFAULT_LOG_CONFIG + '"');

		customFileChooser = new FileParameter("selectedCustomFile", "select_a_log_config_file", FlexoProperties.instance()
				.getCustomLoggingFile());
		parameters[4] = customFileChooser;
		parameters[4].setDepends("mode");
		parameters[4].setConditional("mode=" + '"' + USE_CUSTOM_LOG_CONFIG + '"');

		AskParametersDialog dialog = AskParametersDialog.createAskParametersDialog(null, null, FlexoLocalization
				.localizedForKey("you_can_configure_flexo_logging_style_from_here"), FlexoLocalization
				.localizedForKey("some_configuration_may_be_Dangerous_for_memory_and_application_performance_use_it_carrefully"),
				new AskParametersDialog.ValidationCondition() {
					@Override
					public boolean isValid(ParametersModel model) {
						if (logConfigType.getValue().equals(FlexoLocalization.localizedForKey("use_a_custom_log_config"))
								&& (customFileChooser.getValue() == null || !customFileChooser.getValue().exists() || !customFileChooser
										.getValue().canRead())) {
							errorMessage = FlexoLocalization.localizedForKey("please_select_a_readable_file");
							return false;
						}
						if (logCountTextField.getValue() == null) {
							errorMessage = FlexoLocalization.localizedForKey("please_indicate_log_count");
							return false;
						}
						try {
							new Integer(logCountTextField.getValue());
						} catch (Exception e) {
							errorMessage = FlexoLocalization.localizedForKey("please_indicate_an_integer_as_logcount");
							return false;
						}
						return true;
					}
				}, parameters[0], parameters[1], parameters[2], parameters[3], parameters[4]);

		if (dialog.getStatus() == AskParametersDialog.VALIDATE) {
			System.out.println(dialog.parameterValueWithName("includeTraces"));
			FlexoProperties.instance().setMaxLogCount(new Integer((String) dialog.parameterValueWithName("logCount")));
			FlexoProperties.instance().setIsLoggingTrace((Boolean) dialog.parameterValueWithName("includeTraces"));
			if (dialog.parameterValueWithName("mode").equals(USE_DEFAULT_LOG_CONFIG)) {
				String fileName = "SEVERE";
				Object lev = dialog.parameterValueWithName("logLevel");
				if (lev == LOG_SEVERE)
					fileName = "SEVERE";
				if (lev == LOG_WARNING)
					fileName = "WARNING";
				if (lev == LOG_INFO)
					fileName = "INFO";
				if (lev == LOG_FINE)
					fileName = "FINE";
				if (lev == LOG_FINER)
					fileName = "FINER";
				if (lev == LOG_FINEST)
					fileName = "FINEST";
				reloadLoggingFile(new FileResource("Config/logging_" + fileName + ".properties").getAbsolutePath());
				FlexoProperties.instance().setLoggingFileName(null);
				FlexoProperties.instance().setDefaultLoggingLevel(fileName);

			} else {
				reloadLoggingFile(((File) dialog.parameterValueWithName("selectedCustomFile")).getAbsolutePath());
				FlexoProperties.instance().setLoggingFileName(
						((File) dialog.parameterValueWithName("selectedCustomFile")).getAbsolutePath());
			}
		}
	}

	private boolean reloadLoggingFile(String filePath) {
		System.setProperty("java.util.logging.config.file", filePath);
		try {
			LogManager.getLogManager().readConfiguration();
		} catch (SecurityException e) {
			FlexoController.showError("The specified logging configuration file can't be read (not enough privileges).");
			e.printStackTrace();
			return false;
		} catch (IOException e) {
			FlexoController.showError("The specified logging configuration file cannot be read.");
			e.printStackTrace();
			return false;
		}
		return true;
	}
}
