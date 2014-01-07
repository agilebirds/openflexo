package org.openflexo.fib.model;

import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.XMLElement;

@ModelEntity
@ImplementationClass(FIBTreeTable.FIBTreeTableImpl.class)
@XMLElement(xmlTag = "TreeTable")
public interface FIBTreeTable extends FIBBrowser, FIBTable {

	public static abstract class FIBTreeTableImpl extends FIBBrowserImpl implements FIBTreeTable {

	}
}
