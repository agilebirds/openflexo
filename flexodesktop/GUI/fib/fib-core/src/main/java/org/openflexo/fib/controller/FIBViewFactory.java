package org.openflexo.fib.controller;

import org.openflexo.fib.model.FIBContainer;
import org.openflexo.fib.model.FIBWidget;
import org.openflexo.fib.view.FIBView;
import org.openflexo.fib.view.FIBWidgetView;

public interface FIBViewFactory {

	public FIBView makeContainer(FIBContainer fibContainer);

	public FIBWidgetView makeWidget(FIBWidget fibWidget);

}
