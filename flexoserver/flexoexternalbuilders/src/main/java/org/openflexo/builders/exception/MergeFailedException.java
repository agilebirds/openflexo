package org.openflexo.builders.exception;
import java.util.Vector;

import org.openflexo.fps.CVSFile;

/**
 * This exception is thrown whenever the merge cannot be fully completed.
 * Conflicting files can be retrieved from this exception.
 * 
 * @author gpolet
 * 
 */
public class MergeFailedException extends FlexoRunException {

	private CVSFile conflictingFile;

	private Vector<CVSFile> conflictingFiles;

	public MergeFailedException(CVSFile file) {
		this.conflictingFile = file;
		this.conflictingFiles = new Vector<CVSFile>();
		this.conflictingFiles.add(conflictingFile);
	}

	public MergeFailedException(Vector<CVSFile> files) {
		this.conflictingFiles = files;
		this.conflictingFile = files.firstElement();
	}

	public CVSFile getConflictingFile() {
		return conflictingFile;
	}

	public Vector<CVSFile> getConflictingFiles() {
		return conflictingFiles;
	}

	@Override
	public String getMessage() {
		StringBuilder  sb = new StringBuilder();
		for (CVSFile cvsFile : conflictingFiles) {
			sb.append(cvsFile.getFileName()).append(", ");
		}
		return "The following files were in conflict: "+sb.toString();
	}
}
