package org.openflexo.fib.model.converter;

import java.util.logging.Logger;

import org.openflexo.fib.model.BorderLayoutConstraints;
import org.openflexo.fib.model.BoxLayoutConstraints;
import org.openflexo.fib.model.ComponentConstraints;
import org.openflexo.fib.model.FIBPanel.Layout;
import org.openflexo.fib.model.FlowLayoutConstraints;
import org.openflexo.fib.model.GridBagLayoutConstraints;
import org.openflexo.fib.model.GridLayoutConstraints;
import org.openflexo.fib.model.SplitLayoutConstraints;
import org.openflexo.fib.model.TwoColsLayoutConstraints;
import org.openflexo.model.StringConverterLibrary.Converter;
import org.openflexo.model.factory.ModelFactory;

public class ComponentConstraintsConverter extends Converter<ComponentConstraints> {

	static final Logger logger = Logger.getLogger(ComponentConstraintsConverter.class.getPackage().getName());

	public ComponentConstraintsConverter() {
		super(ComponentConstraints.class);
	}

	@Override
	public ComponentConstraints convertFromString(String aValue, ModelFactory factory) {
		try {
			// System.out.println("aValue="+aValue);
			String constraintType = aValue.substring(0, aValue.indexOf("("));
			String someConstraints = aValue.substring(aValue.indexOf("(") + 1, aValue.length() - 1);
			// System.out.println("constraintType="+constraintType);
			// System.out.println("someConstraints="+someConstraints);
			if (constraintType.equals(Layout.border.name())) {
				return new BorderLayoutConstraints(someConstraints);
			} else if (constraintType.equals(Layout.flow.name())) {
				return new FlowLayoutConstraints(someConstraints);
			} else if (constraintType.equals(Layout.grid.name())) {
				return new GridLayoutConstraints(someConstraints);
			} else if (constraintType.equals(Layout.box.name())) {
				return new BoxLayoutConstraints(someConstraints);
			} else if (constraintType.equals(Layout.border.name())) {
				return new BorderLayoutConstraints(someConstraints);
			} else if (constraintType.equals(Layout.twocols.name())) {
				return new TwoColsLayoutConstraints(someConstraints);
			} else if (constraintType.equals(Layout.gridbag.name())) {
				return new GridBagLayoutConstraints(someConstraints);
			} else if (constraintType.equals(Layout.split.name())) {
				return new SplitLayoutConstraints(someConstraints);
			}
		} catch (StringIndexOutOfBoundsException e) {
			logger.warning("Syntax error in ComponentConstraints: " + aValue);
		}
		return null;
	}

	@Override
	public String convertToString(ComponentConstraints value) {
		if (value == null) {
			return null;
		}
		return value.getStringRepresentation();
	};
}