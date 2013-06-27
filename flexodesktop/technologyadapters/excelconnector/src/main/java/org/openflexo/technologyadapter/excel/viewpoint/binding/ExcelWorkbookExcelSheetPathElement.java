package org.openflexo.technologyadapter.excel.viewpoint.binding;

import java.lang.reflect.Type;

import org.openflexo.antar.binding.BindingEvaluationContext;
import org.openflexo.antar.binding.BindingPathElement;
import org.openflexo.antar.binding.SimplePathElement;
import org.openflexo.antar.expr.InvocationTargetTransformException;
import org.openflexo.antar.expr.NullReferenceException;
import org.openflexo.antar.expr.TypeMismatchException;

public class ExcelWorkbookExcelSheetPathElement extends SimplePathElement {

	public ExcelWorkbookExcelSheetPathElement(BindingPathElement parent,
			String propertyName, Type type) {
		super(parent, propertyName, type);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String getLabel() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getTooltipText(Type resultingType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public Object getBindingValue(Object target,
			BindingEvaluationContext context) throws TypeMismatchException,
			NullReferenceException, InvocationTargetTransformException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setBindingValue(Object value, Object target,
			BindingEvaluationContext context) throws TypeMismatchException,
			NullReferenceException {
		// TODO Auto-generated method stub
		
	}

	
}
