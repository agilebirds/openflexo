package org.openflexo.logging;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.toolbox.StringUtils;

public class LoggingFilter {

	static final Logger logger = Logger.getLogger(LoggingFilter.class.getPackage().getName());

	public String filterName;
	public FilterType type = FilterType.Highlight;
	public MessageFilterType messageFilterType = MessageFilterType.Contains;
	public String filteredContent;
	public Level filteredLevel = null;
	public SourceFilterType sourceFilterType = null;
	public String filteredSourceContent;
	public DateFilterType dateFilterType = null;
	public String filteredDate1;
	public String filteredDate2;
	public String filteredThread;
	public String filteredStacktrace;
	public int startSequence = -1;
	public int endSequence = -1;

	public static enum FilterType {
		OnlyKeep, Highlight, Dismiss
	}

	public static enum MessageFilterType {
		Contains, StartsWith, EndsWith
	}

	public static enum SourceFilterType {
		Package, Class, Method
	}

	public static enum DateFilterType {
		Before, After, Between
	}

	public LoggingFilter(String filterName) {
		this.filterName = filterName;
	}

	public boolean getHasFilteredMessage() {
		return messageFilterType != null;
	}

	public void setHasFilteredMessage(boolean aFlag) {
		if (aFlag) {
			messageFilterType = MessageFilterType.Contains;
		} else {
			messageFilterType = null;
		}
	}

	public boolean getHasFilteredLevel() {
		return filteredLevel != null;
	}

	public void setHasFilteredLevel(boolean aFlag) {
		if (aFlag) {
			filteredLevel = Level.INFO;
		} else {
			filteredLevel = null;
		}
	}

	public boolean getHasFilteredSource() {
		return sourceFilterType != null;
	}

	public void setHasFilteredSource(boolean aFlag) {
		if (aFlag) {
			sourceFilterType = SourceFilterType.Class;
		} else {
			sourceFilterType = null;
		}
	}

	public boolean getHasFilteredDate() {
		return dateFilterType != null;
	}

	public void setHasFilteredDate(boolean aFlag) {
		if (aFlag) {
			dateFilterType = DateFilterType.After;
		} else {
			dateFilterType = null;
		}
	}

	public boolean getHasFilteredThread() {
		return filteredThread != null;
	}

	public void setHasFilteredThread(boolean aFlag) {
		if (aFlag) {
			filteredThread = "10";
		} else {
			filteredThread = null;
		}
	}

	public boolean getHasFilteredStacktrace() {
		return filteredStacktrace != null;
	}

	public void setHasFilteredStacktrace(boolean aFlag) {
		if (aFlag) {
			filteredStacktrace = "Searched content";
		} else {
			filteredStacktrace = null;
		}
	}

	public boolean getHasFilteredSequence() {
		return startSequence > -1;
	}

	public void setHasFilteredSequence(boolean aFlag) {
		if (aFlag) {
			startSequence = 0;
			endSequence = 0;
		} else {
			startSequence = -1;
			endSequence = -1;
		}
	}

	private boolean messageMatches(LogRecord record) {
		if (StringUtils.isEmpty(record.message)) {
			return true;
		}
		switch (messageFilterType) {
		case Contains:
			if (record.message.contains(filteredContent)) {
				return true;
			}
			break;
		case StartsWith:
			if (record.message.startsWith(filteredContent)) {
				return true;
			}
			break;
		case EndsWith:
			if (record.message.endsWith(filteredContent)) {
				return true;
			}
			break;
		default:
			break;
		}
		return false;
	}

	private boolean sourceMatches(LogRecord record) {
		switch (sourceFilterType) {
		case Package:
			if (record.logger.contains(filteredSourceContent)) {
				return true;
			}
			break;
		case Class:
			if (record.classAsString().contains(filteredSourceContent)) {
				return true;
			}
			break;
		case Method:
			if (record.methodName.contains(filteredSourceContent)) {
				return true;
			}
			break;
		default:
			break;
		}
		return false;
	}

	private boolean levelMatches(LogRecord record) {
		return record.level == filteredLevel;
	}

	private boolean threadMatches(LogRecord record) {
		return record.threadAsString().equals(filteredThread);
	}

	private boolean stacktraceMatches(LogRecord record) {
		return record.getStackTraceAsString().contains(filteredStacktrace);
	}

	private boolean sequenceMatches(LogRecord record) {
		return record.sequence >= startSequence && record.sequence <= endSequence;
	}

	private boolean dateMatches(LogRecord record) {
		logger.warning("Not implemented ");
		return true;
	}

	public boolean filterDoesApply(LogRecord record) {
		if (getHasFilteredMessage()) {
			if (!messageMatches(record)) {
				return false;
			}
		}
		if (getHasFilteredLevel()) {
			if (!levelMatches(record)) {
				return false;
			}
		}
		if (getHasFilteredDate()) {
			if (!dateMatches(record)) {
				return false;
			}
		}
		if (getHasFilteredSequence()) {
			if (!sequenceMatches(record)) {
				return false;
			}

		}
		if (getHasFilteredSource()) {
			if (!sourceMatches(record)) {
				return false;
			}
		}
		if (getHasFilteredStacktrace()) {
			if (!stacktraceMatches(record)) {
				return false;
			}
		}
		if (getHasFilteredThread()) {
			if (!threadMatches(record)) {
				return false;
			}
		}
		return true;
	}

	public String getFilterDescription() {
		StringBuffer returned = new StringBuffer();
		if (getHasFilteredMessage()) {
			returned.append("message " + messageFilterType + " " + filteredContent + " ");
		}
		if (getHasFilteredLevel()) {
			returned.append("level=" + filteredLevel + " ");
		}
		if (getHasFilteredSource()) {
			returned.append(sourceFilterType + " contains " + filteredSourceContent + " ");
		}
		if (getHasFilteredDate()) {
			returned.append("date " + dateFilterType + " " + filteredDate1
					+ (dateFilterType == DateFilterType.Between ? " and " + filteredDate2 : "") + " ");
		}
		if (getHasFilteredStacktrace()) {
			returned.append("stacktrace contains " + filteredStacktrace + " ");
		}
		if (getHasFilteredThread()) {
			returned.append("thread=" + filteredThread + " ");
		}
		if (getHasFilteredSequence()) {
			returned.append("sequence between " + startSequence + " and " + endSequence + " ");
		}
		return returned.toString();
	}
}
