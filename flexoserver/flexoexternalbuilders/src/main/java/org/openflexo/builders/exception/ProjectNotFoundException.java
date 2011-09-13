package org.openflexo.builders.exception;


/**
 * This exception is thrown if the project cannot be found within an uploaded
 * zip file supposed to contain a project.
 * 
 * @author gpolet
 * 
 */
public class ProjectNotFoundException extends FlexoRunException {

	public ProjectNotFoundException(Exception exception) {
		super(exception);
	}

	public ProjectNotFoundException() {
		// TODO Auto-generated constructor stub
	}

}
