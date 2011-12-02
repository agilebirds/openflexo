package org.openflexo.builders.utils;

import org.openflexo.fps.CVSConsole;
import org.openflexo.fps.CVSConsole.ConsoleListener;

public class FlexoCVSConsoleListener implements ConsoleListener {

	private StringBuilder logs = new StringBuilder();

	public FlexoCVSConsoleListener() {
		CVSConsole.getCVSConsole().addToConsoleListeners(this);
	}

	@Override
	public void commandLog(String logString) {
		logs.append("COMMAND: ").append(logString).append("\n");
	}

	@Override
	public void errorLog(String logString) {
		logs.append("ERROR: ").append(logString).append("\n");
	}

	@Override
	public void log(String logString) {
		logs.append("LOG: ").append(logString).append("\n");
	}

	public StringBuilder getLogs() {
		return logs;
	}
}
