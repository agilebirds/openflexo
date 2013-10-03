package org.openflexo.technologyadapter.excel.rm;

import org.apache.poi.poifs.filesystem.OfficeXmlFileException;
import org.openflexo.foundation.rm.LoadResourceException;

public class InvalidExcelFormatException extends LoadResourceException {

	public InvalidExcelFormatException(ExcelWorkbookResource resource, OfficeXmlFileException e) {
		super(resource, e.getMessage());
	}
}
