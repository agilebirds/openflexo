package org.openflexo.fib.editor.controller;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;

public abstract class ElementDrag<T> implements Transferable {
	public static final DataFlavor DEFAULT_FLAVOR = new DataFlavor(ElementDrag.class, "PaletteElement");

	private T _transferedData;

	public ElementDrag(T transferedData, Point dragOrigin) {
		_transferedData = transferedData;
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { DEFAULT_FLAVOR };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return true;
	}

	@Override
	public T getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return _transferedData;
	}

}