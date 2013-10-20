package org.openflexo.rest.client;

import javax.persistence.Entity;
import javax.persistence.Inheritance;
import javax.persistence.InheritanceType;

@Entity(name = WatchedRemoteProtoJob.ENTITY_NAME)
@Inheritance(strategy = InheritanceType.TABLE_PER_CLASS)
public class WatchedRemoteProtoJob extends WatchedRemoteJob {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6218339251940468805L;
	public static final String ENTITY_NAME = "WatchedRemoteProtoJob";

}
