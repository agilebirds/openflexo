package org.openflexo.fib.editor.controller;

import java.awt.Point;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.util.Vector;
import java.util.logging.Level;

import org.openflexo.fib.editor.view.FIBEditableView;
import org.openflexo.fib.editor.view.PlaceHolder;
import org.openflexo.fib.model.FIBComponent;
import org.openflexo.fib.view.FIBView;

public abstract class ElementDrag<T> implements Transferable {
	private FIBEditorController _controller;
	private static DataFlavor _defaultFlavor;

	private T _transferedData;

	private Vector<FIBComponent> focusedComponentPath;

	public ElementDrag(T transferedData, Point dragOrigin) {
		_transferedData = transferedData;
		focusedComponentPath = new Vector<FIBComponent>();
	}

	public void reset() {
		if (PaletteElement.logger.isLoggable(Level.FINE)) {
			PaletteElement.logger.fine("Resetting drag");
		}
		int end = focusedComponentPath.size();
		for (int i = 0; i < end; i++) {
			FIBComponent c2 = focusedComponentPath.remove(focusedComponentPath.size() - 1);
			FIBView v = getController().viewForComponent(c2);
			if (v instanceof FIBEditableView) {
				((FIBEditableView) v).getDelegate().setPlaceHoldersAreVisible(false);
				((FIBEditableView) v).getDelegate().setFocused(false);
			}
		}
	}

	public FIBComponent getCurrentlyFocusedComponent() {
		if (focusedComponentPath.size() > 0) {
			return focusedComponentPath.lastElement();
		}
		return null;
	}

	public void enterComponent(FIBComponent c, PlaceHolder ph, Point location) {
		if (getController() == null) {
			return;
		}
		if (PaletteElement.logger.isLoggable(Level.FINE)) {
			PaletteElement.logger.fine("Drag enter in component " + c + " ph=" + ph);
		}
		Vector<FIBComponent> appendingPath = new Vector<FIBComponent>();
		FIBComponent current = c;
		while (current != null && !focusedComponentPath.contains(current)) {
			appendingPath.insertElementAt(current, 0);
			current = current.getParent();
		}
		for (FIBComponent c2 : appendingPath) {
			focusedComponentPath.add(c2);
			FIBView v = getController().viewForComponent(c2);
			if (v instanceof FIBEditableView) {
				((FIBEditableView) v).getDelegate().setPlaceHoldersAreVisible(true);

				/* Some explanations required here
				 * What may happen is that making place holders visible will
				 * place current cursor location inside a newly displayed place
				 * holder, and cause a subsequent exitComponent() event to the
				 * current component, and then a big blinking. We test here that
				 * case and ignore following exitComponent()
				 * SGU/ I'm not sure this behaviour is platform independant
				 * please check...
				 * 
				 */
				if (ph == null && ((FIBEditableView<?, ?>) v).getPlaceHolders() != null) {
					for (PlaceHolder ph2 : ((FIBEditableView<?, ?>) v).getPlaceHolders()) {
						if (ph2.getBounds().contains(location)) {
							temporaryDisable = true;
						}
					}
				}
			}
		}

		if (ph != null) {
			ph.setFocused(true);
		} else {
			FIBView v = getController().viewForComponent(c);
			if (v instanceof FIBEditableView) {
				((FIBEditableView) v).getDelegate().setFocused(true);
			}
		}
		if (PaletteElement.logger.isLoggable(Level.FINE)) {
			PaletteElement.logger.fine("focusedComponentPath=" + focusedComponentPath);
		}
	}

	private boolean temporaryDisable = false;

	public void exitComponent(FIBComponent c, PlaceHolder ph) {
		if (getController() == null) {
			return;
		}
		if (PaletteElement.logger.isLoggable(Level.FINE)) {
			PaletteElement.logger.fine("Drag exit from component " + c + " ph=" + ph);
		}
		if (temporaryDisable) {
			temporaryDisable = false;
			return;
		}
		if (focusedComponentPath.contains(c)) {
			int index = focusedComponentPath.indexOf(c);
			int end = focusedComponentPath.size();
			for (int i = index; i < end; i++) {
				FIBComponent c2 = focusedComponentPath.remove(focusedComponentPath.size() - 1);
				FIBView v = getController().viewForComponent(c2);
				if (v instanceof FIBEditableView) {
					((FIBEditableView) v).getDelegate().setPlaceHoldersAreVisible(false);
					((FIBEditableView) v).getDelegate().setFocused(false);
				}
			}
		} else {
			// Weird....
		}
		if (ph != null) {
			ph.setFocused(false);
		} else {
			FIBView v = getController().viewForComponent(c);
			if (v instanceof FIBEditableView) {
				((FIBEditableView) v).getDelegate().setFocused(false);
			}
		}
		if (PaletteElement.logger.isLoggable(Level.FINE)) {
			PaletteElement.logger.fine("focusedComponentPath=" + focusedComponentPath);
		}
	}

	@Override
	public DataFlavor[] getTransferDataFlavors() {
		return new DataFlavor[] { defaultFlavor() };
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		return true;
	}

	@Override
	public T getTransferData(DataFlavor flavor) throws UnsupportedFlavorException, IOException {
		return _transferedData;
	}

	public static DataFlavor defaultFlavor() {
		if (_defaultFlavor == null) {
			_defaultFlavor = new DataFlavor(ElementDrag.class, "PaletteElement");
		}
		return _defaultFlavor;
	}

	public FIBEditorController getController() {
		return _controller;
	}

	public void setController(FIBEditorController controller) {
		// System.out.println("Setting controller");
		_controller = controller;
	}

}