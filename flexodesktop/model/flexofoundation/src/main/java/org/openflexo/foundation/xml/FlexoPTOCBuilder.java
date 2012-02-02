package org.openflexo.foundation.xml;

/**
 * MOS
 * @author MOSTAFA
 * 
 * TODO_MOS
 * 
 */



import org.openflexo.foundation.rm.FlexoPTOCResource;
import org.openflexo.foundation.rm.FlexoTOCResource;
import org.openflexo.foundation.ptoc.PTOCData;

public class FlexoPTOCBuilder extends FlexoBuilder<FlexoPTOCResource> {

	public PTOCData ptocData;
	
	public FlexoPTOCBuilder(FlexoPTOCResource resource) {
		super(resource);
	}

}