package org.openflexo.technologyadapter.excel.viewpoint.binding;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.FunctionPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.foundation.ontology.IFlexoOntologyFeatureAssociation;
import org.openflexo.foundation.ontology.IndividualOfClass;
import org.openflexo.foundation.technologyadapter.TechnologyAdapterBindingFactory;
import org.openflexo.foundation.viewpoint.TechnologySpecificCustomType;
import org.openflexo.technologyadapter.excel.model.ExcelSheet;
import org.openflexo.technologyadapter.excel.model.ExcelWorkbook;

/**
 * This class represent the {@link BindingFactory} dedicated to handle Excel technology-specific binding elements
 * 
 * @author sylvain, vincent
 * 
 */
public final class ExcelBindingFactory extends TechnologyAdapterBindingFactory {
	static final Logger logger = Logger.getLogger(ExcelBindingFactory.class.getPackage().getName());

	public ExcelBindingFactory() {
		super();
	}

	@Override
	protected SimplePathElement makeSimplePathElement(Object object, BindingPathElement parent) {
		logger.warning("Unexpected " + object);
		return null;
	}

	@Override
	public boolean handleType(TechnologySpecificCustomType technologySpecificType) {
		if ((technologySpecificType instanceof ExcelSheetType)) {
			return true;
		}
		return false;
	}

	@Override
	public List<? extends SimplePathElement> getAccessibleSimplePathElements(BindingPathElement parent) {
		List<SimplePathElement> returned = new ArrayList<SimplePathElement>();
		if (parent instanceof ExcelWorkbook) {
			for ( ExcelSheet sheet : ((ExcelWorkbook)parent).getExcelSheets()) {
				returned.add(getSimplePathElement(sheet, parent));
			}
		}
		return returned;
	}

	@Override
	public List<? extends FunctionPathElement> getAccessibleFunctionPathElements(BindingPathElement parent) {
		// TODO
		return Collections.emptyList();
	}

}