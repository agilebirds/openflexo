package org.openflexo.technologyadapter.powerpoint.rm;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.openflexo.foundation.rm.LoadResourceException;

public class InvalidPowerpointFormatException extends LoadResourceException {

	public InvalidPowerpointFormatException(PowerpointSlideshowResource resource, OfficeXmlFileException e) {
		super(resource, e.getMessage());
	}
}
