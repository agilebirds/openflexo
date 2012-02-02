package org.openflexo.foundation.cg.dm;

/**
 * MOS 
 * @author MOSTAFA
 * 
 * TODO_MOS
 */


import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.ptoc.PTOCRepository;

public class PTOCRepositoryChanged extends DataModification {

	public PTOCRepositoryChanged(PTOCRepository oldValue, PTOCRepository newValue) {
		super(-1, "repository", oldValue, newValue);
	}

}
