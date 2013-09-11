package org.openflexo.rest.client;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity(name = WatchedRemoteDocJob.ENTITY_NAME)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class WatchedRemoteDocJob extends WatchedRemoteJob {

	public static final String ENTITY_NAME = "WatchedRemoteDocJob";

	private String saveToFolder;
	private boolean unzip;
	private boolean openDocument;

	public String getSaveToFolder() {
		return saveToFolder;
	}

	public void setSaveToFolder(String info) {
		this.saveToFolder = info;
	}

	public boolean isOpenDocument() {
		return openDocument;
	}

	public void setOpenDocument(boolean openDocument) {
		this.openDocument = openDocument;
	}

	public boolean isUnzip() {
		return unzip;
	}

	public void setUnzip(boolean unzip) {
		this.unzip = unzip;
	}

}
