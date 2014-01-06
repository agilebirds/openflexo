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

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.StringTokenizer;
import java.util.logging.Level;

/**
 * This class is used to encode a simple log record in Flexo
 * 
 * @author sguerin
 */
public class LogRecord {

	public Date date;

	public long millis;

	public long sequence;

	public String logger;

	public String className;

	public String methodName;

	public int threadId;

	public String message;

	public Level level;

	public StackTraceElement[] stackTrace;

	protected String _dateAsString = null;

	protected String _classAsString = null;

	protected String _millisAsString = null;

	protected String _sequenceAsString = null;

	protected String _threadAsString = null;

	public boolean isUnhandledException = false;

	public LogRecord() {
		super();
	}

	public LogRecord(java.util.logging.LogRecord record, FlexoLoggingManager loggingManager) {
		super();
		date = new Date();
		millis = record.getMillis();
		sequence = record.getSequenceNumber();
		logger = record.getLoggerName();
		className = record.getSourceClassName();
		methodName = record.getSourceMethodName();
		threadId = record.getThreadID();
		message = record.getMessage();
		if (message != null) {
			message = message.intern();
		}
		level = record.getLevel();
		if (loggingManager != null && loggingManager.getKeepLogTrace()) {
			stackTrace = new Exception().getStackTrace();
		}
		isUnhandledException = false;
	}

	public LogRecord(java.util.logging.LogRecord record, Exception e, FlexoLoggingManager loggingManager) {
		this(record, loggingManager);
		stackTrace = e.getStackTrace();
		className = stackTrace[0].getClassName();
		methodName = stackTrace[0].getMethodName();
		isUnhandledException = true;
	}

	public String dateAsString() {
		if (_dateAsString == null) {
			_dateAsString = new SimpleDateFormat("HH:mm:ss dd/MM").format(date);
		}
		return _dateAsString;
	}

	public String classAsString() {
		if (_classAsString == null && className != null) {
			StringTokenizer st = new StringTokenizer(className, ".");
			while (st.hasMoreTokens()) {
				_classAsString = st.nextToken();
			}
		}
		return _classAsString;
	}

	public String millisAsString() {
		if (_millisAsString == null) {
			_millisAsString = "" + millis;
		}
		return _millisAsString;
	}

	public String sequenceAsString() {
		if (_sequenceAsString == null) {
			_sequenceAsString = "" + sequence;
		}
		return _sequenceAsString;
	}

	public String threadAsString() {
		if (_threadAsString == null) {
			_threadAsString = "" + threadId;
		}
		return _threadAsString;
	}

	public String getStackTraceAsString() {
		if (_stackTraceAsString != null) {
			return _stackTraceAsString;
		} else if (stackTrace != null) {
			StringBuilder returned = new StringBuilder();
			int beginAt;
			if (isUnhandledException) {
				beginAt = 0;
			} else {
				beginAt = 6;
			}
			for (int i = beginAt; i < stackTrace.length; i++) {
				// returned += ("\tat " + stackTrace[i] + "\n");
				returned.append("\t").append("at ").append(stackTrace[i]).append('\n');
			}
			return returned.toString();
		} else {
			return "StackTrace not available";
		}
	}

	private String _stackTraceAsString;

	public void setStackTraceAsString(String aString) {
		_stackTraceAsString = aString;
	}
}
