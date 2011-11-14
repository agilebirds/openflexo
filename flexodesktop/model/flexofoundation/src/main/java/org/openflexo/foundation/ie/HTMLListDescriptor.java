/*
 * (c) Copyright 2010-2011 AgileBirds
 *
 * This file is part of OpenFlexo.
 *
 * OpenFlexo is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenFlexo is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenFlexo. If not, see <http://www.gnu.org/licenses/>.
 *
 */
package org.openflexo.foundation.ie;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.bindings.BindingValue;
import org.openflexo.foundation.dm.DMProperty;
import org.openflexo.foundation.dm.eo.DMEOAttribute;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.ie.operator.ConditionalOperator;
import org.openflexo.foundation.ie.operator.IEOperator;
import org.openflexo.foundation.ie.operator.RepetitionOperator;
import org.openflexo.foundation.ie.widget.IEBlocWidget;
import org.openflexo.foundation.ie.widget.IECheckBoxWidget;
import org.openflexo.foundation.ie.widget.IEControlWidget;
import org.openflexo.foundation.ie.widget.IEDropDownWidget;
import org.openflexo.foundation.ie.widget.IEHTMLTableWidget;
import org.openflexo.foundation.ie.widget.IEHeaderWidget;
import org.openflexo.foundation.ie.widget.IEHyperlinkWidget;
import org.openflexo.foundation.ie.widget.IENonEditableTextWidget;
import org.openflexo.foundation.ie.widget.IESequence;
import org.openflexo.foundation.ie.widget.IESequenceTR;
import org.openflexo.foundation.ie.widget.IESequenceWidget;
import org.openflexo.foundation.ie.widget.IEStringWidget;
import org.openflexo.foundation.ie.widget.IETDWidget;
import org.openflexo.foundation.ie.widget.IETRWidget;
import org.openflexo.foundation.ie.widget.IETextAreaWidget;
import org.openflexo.foundation.ie.widget.IETextFieldWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.foundation.ie.widget.ITableRow;
import org.openflexo.foundation.ie.widget.IWidget;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.ToolBox;

public class HTMLListDescriptor {

	protected static final Logger logger = FlexoLogger.getLogger(HTMLListDescriptor.class.getPackage().getName());

	private IEBlocWidget bloc;
	private ITableRow repeatedSequenceTR;
	private IETRWidget headerRow;
	private IETRWidget searchRow;

	private HTMLListDescriptor(IEBlocWidget w, ITableRow repeatedRow, IETRWidget headers) {
		super();
		setBloc(w);
		setHeaderRow(headers);
		setRepeatedSequenceTR(repeatedRow);
		if (bloc != null && !((IEHTMLTableWidget) bloc.getContent()).getFirstTR().equals(headers)) {
			searchRow = ((IEHTMLTableWidget) bloc.getContent()).getFirstTR();
		} else {
			searchRow = null;
		}
	}

	public boolean hasSearch() {
		return searchRow != null;
	}

	public boolean isInSearchArea(IEWidget w) {
		if (w == null || searchRow == null) {
			return false;
		}
		IEWidget p = w;
		while (p != null && p.getParent() instanceof IEWidget) {
			if (p.equals(searchRow)) {
				return true;
			}
			p = (IEWidget) p.getParent();
		}
		return false;
	}

	public boolean containsHeader(IEHeaderWidget h) {
		if (getHeaderRow() == null) {
			return false;
		}
		Enumeration en = getHeaderRow().getSequenceTD().elements();
		IETDWidget td = null;
		while (en.hasMoreElements()) {
			td = (IETDWidget) en.nextElement();
			if (td.getSequenceWidget().contains(h)) {
				return true;
			}
		}
		return false;
	}

	public RepetitionOperator getRepetitionOperator() {
		IEOperator reply = getRepeatedSequenceTR().getOperator();
		ITableRow seq = getRepeatedSequenceTR();
		while (reply != null && reply instanceof ConditionalOperator) {
			if (seq.getParent() instanceof ITableRow) {
				seq = (ITableRow) seq.getParent();
				reply = seq.getOperator();
			}
		}
		if (reply instanceof RepetitionOperator) {
			return (RepetitionOperator) reply;
		}
		return null;
	}

	public String getRepetitionName() {
		return getListName();
	}

	public String getListName() {
		return ToolBox.getJavaName(getRepetitionOperator().getName());
	}

	public String getItemName() {
		if (getRepetitionOperator().getItemVariable() != null) {
			return getRepetitionOperator().getItemVariable().getCodeStringRepresentation();
		}
		return "item_" + getRepetitionName();
	}

	public DMProperty getItemProperty() {
		if (getRepetitionOperator().getItemVariable() != null) {
			BindingValue bv = getRepetitionOperator().getItemVariable();
			return (DMProperty) bv.getBindingPath().get(bv.getBindingPath().size() - 1);
		}
		return null;
	}

	public String getIndexName() {
		return getListName() + "Index";
	}

	public Vector<IEHeaderWidget> getHeaders() {
		if (headerRow == null) {
			return new Vector<IEHeaderWidget>();
		}
		Vector<IEHeaderWidget> v = new Vector<IEHeaderWidget>();
		Enumeration en = headerRow.getAllTD().elements();
		while (en.hasMoreElements()) {
			IETDWidget element = (IETDWidget) en.nextElement();
			if (element.getSequenceWidget().size() == 1 && element.getSequenceWidget().get(0) instanceof IEHeaderWidget) {
				v.add((IEHeaderWidget) element.getSequenceWidget().get(0));
			}
		}
		return v;
	}

	public Vector<IEHeaderWidget> getHeadersWithKeyPath() {
		Vector<IEHeaderWidget> v = new Vector<IEHeaderWidget>();
		Enumeration<IEHeaderWidget> en = getHeaders().elements();
		while (en.hasMoreElements()) {
			IEHeaderWidget h = en.nextElement();
			String kp = getKeyPathForWidgetInHCWO(h);

			if (kp != null && kp.trim().length() > 0) {
				v.add(h);
			}
		}
		return v;
	}

	public Vector<IEHeaderWidget> getSortableHeaders() {
		Vector<IEHeaderWidget> v = new Vector<IEHeaderWidget>();
		Enumeration<IEHeaderWidget> en = getHeaders().elements();
		IEHeaderWidget h = null;
		while (en.hasMoreElements()) {
			h = en.nextElement();
			if (h.getIsSortable()) {
				v.add(h);
			}
		}
		return v;
	}

	public boolean hasSort() {
		if (getHeaders() == null) {
			return false;
		}
		Enumeration en = getHeaders().elements();
		IEHeaderWidget h = null;
		while (en.hasMoreElements()) {
			h = (IEHeaderWidget) en.nextElement();
			if (h.getIsSortable()) {
				return true;
			}
		}
		return false;
	}

	public String getListAccessorName() {
		String accessorName = null;
		if (getRepetitionOperator().getListAccessor() != null) {
			accessorName = getRepetitionOperator().getListAccessor().getCodeStringRepresentation();
		}
		if (accessorName == null) {
			accessorName = getRepetitionName() + "ObjectArray";
		}
		return accessorName;
	}

	public IEBlocWidget getBloc() {
		return bloc;
	}

	public void setBloc(IEBlocWidget b) {
		this.bloc = b;
	}

	public IETRWidget getHeaderRow() {
		return headerRow;
	}

	public void setHeaderRow(IETRWidget h) {
		this.headerRow = h;
	}

	public ITableRow getRepeatedSequenceTR() {
		return repeatedSequenceTR;
	}

	public void setRepeatedSequenceTR(ITableRow seq) {
		this.repeatedSequenceTR = seq;
	}

	public IENonEditableTextWidget findHeaderForWidget(IEWidget widget) {
		if (headerRow == null || headerRow.getColCount() == 0) {
			if (logger.isLoggable(Level.FINEST)) {
				logger.finest("Header row null or empty");
			}
			return null;
		}
		IETDWidget td = widget.findTDInParent();
		if (td == null) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Widget not in TD");
			}
			return null;
		}
		// We lookup the TD located at the same location than the current one
		// but in the header row
		td = headerRow.getTDatXLocation(td.getXLocation());
		Vector<IWidget> v = td.getSequenceWidget().getAllNonSequenceWidget();
		Enumeration<IWidget> en = v.elements();
		while (en.hasMoreElements()) {
			IWidget w = en.nextElement();
			if (w instanceof IENonEditableTextWidget) {
				return (IENonEditableTextWidget) w;
			}
		}
		return null;
	}

	public IEWidget findWidgetMatchingHeader(IEHeaderWidget h) {
		IETRWidget firstTR = getRepeatedSequenceTR().getFirstTR();
		if (firstTR != null) {
			int gridx = (((IESequenceWidget) h.getParent()).td()).constraints.gridx;

			Enumeration en = firstTR.colsEnumeration();
			while (en.hasMoreElements()) {
				IETDWidget td = (IETDWidget) en.nextElement();
				if (td.constraints.gridx == gridx) {
					IEWidget reply = td.getSequenceWidget().findFirstWidgetOfClass(IEStringWidget.class);
					if (reply == null) {
						reply = td.getSequenceWidget().findFirstWidgetOfClass(IEHyperlinkWidget.class);
					}
					if (reply == null && td.getSequenceWidget().size() > 0) {
						reply = td.getSequenceWidget().get(0);
					}
					return reply;
				}
			}
			if (logger.isLoggable(Level.FINE)) {
				logger.fine("cannot find a widget matching header :" + h.getValue() + " at gridx = " + gridx);
			}
		}

		return null;
	}

	private Object[] findWidgetsMatchingHeader(IEHeaderWidget h) {
		Vector<IEWidget> answer = new Vector<IEWidget>();

		Enumeration en1 = getRepeatedSequenceTR().getAllTR().elements();
		while (en1.hasMoreElements()) {
			IETRWidget firstTR = (IETRWidget) en1.nextElement();
			if (firstTR != null) {
				int gridx = (((IESequenceWidget) h.getParent()).td()).constraints.gridx;

				Enumeration en = firstTR.colsEnumeration();
				while (en.hasMoreElements()) {
					IETDWidget td = (IETDWidget) en.nextElement();
					if (td.constraints.gridx == gridx) {
						IEWidget reply = td.getSequenceWidget().findFirstWidgetOfClass(IEStringWidget.class);
						if (reply == null) {
							reply = td.getSequenceWidget().findFirstWidgetOfClass(IEHyperlinkWidget.class);
						}
						if (reply == null && td.getSequenceWidget().size() > 0) {
							reply = td.getSequenceWidget().get(0);
						}
						answer.add(reply);
					}
				}
				if (logger.isLoggable(Level.FINE)) {
					logger.fine("cannot find a widget matching header :" + h.getValue() + " at gridx = " + gridx);
				}
			}
		}
		return answer.toArray();
	}

	// private int getExampleRowCount(){
	// int maxValues = 0;
	// Enumeration en = getHeaders().elements();
	// while(en.hasMoreElements()){
	// IEWidget widget = findWidgetMatchingHeader((IEHeaderWidget)en.nextElement());
	// int c = getPrototypeValuesForWidget(widget).size();
	// if(c>maxValues)maxValues=c;
	// }
	// return maxValues;
	// }

	public String getKeyPathForHeader(IEHeaderWidget header) {
		return getKeyPathForWidget(findWidgetMatchingHeader(header));
	}

	public String getKeyPathForWidgetInHCWO(IEHeaderWidget header) {
		IEWidget widget = findWidgetMatchingHeader(header);
		if (widget == null) {
			return null;
		}
		if (widget instanceof IENonEditableTextWidget && ((IENonEditableTextWidget) widget).getBindingValue() != null) {
			return ((IENonEditableTextWidget) widget).getBindingValue().getCodeStringRepresentation();
		}
		return null;
	}

	public String getKeyPathForWidget(IEWidget widget) {

		if (widget instanceof IEHyperlinkWidget) {
			String defaultValue = ((IEHyperlinkWidget) widget).getValue();
			String string = defaultValue + widget.getFlexoID();
			return ToolBox.getJavaName(string);
		}
		if (widget instanceof IEStringWidget) {
			String keyPathAttributeValue = ((IEStringWidget) widget).getKeyPath();
			String kp = keyPathAttributeValue;
			if (kp == null) {
				String binding_value = "";
				if (((IEStringWidget) widget).getBindingValue() != null) {
					binding_value = ToolBox.getJavaName(((IEStringWidget) widget).getBindingValue().getCodeStringRepresentation());
				}
				binding_value = ToolBox.getJavaName(binding_value);
				if (binding_value == null || binding_value.equals("")) {
					String relatedLabelValue = widget.getLabel();
					if (relatedLabelValue == null) {
						relatedLabelValue = widget.getCalculatedLabel();
					}
					if (relatedLabelValue != null && !relatedLabelValue.equals("")) {
						binding_value = ToolBox.getJavaName(relatedLabelValue) + widget.getFlexoID();
					} else {
						binding_value = "string_" + widget.getFlexoID();
					}
				}
				kp = binding_value;
			}
			return kp;
		}
		return null;
	}

	public String getBindingValueCodeStringRepresentationForWidget(IEWidget widget) {
		AbstractBinding bv = null;
		if (widget instanceof IEHyperlinkWidget) {
			bv = ((IEHyperlinkWidget) widget).getBindingValue();
		} else if (widget instanceof IEStringWidget) {
			bv = ((IEStringWidget) widget).getBindingValue();
		}
		if (bv == null) {
			return null;
		}
		if (bv.getCodeStringRepresentation() == null || bv.getCodeStringRepresentation().trim().length() == 0) {
			return null;
		}
		String reply = bv.getCodeStringRepresentation();
		BindingValue item = getRepetitionOperator().getItemVariable();
		if (item == null || item.getCodeStringRepresentation() == null || item.getCodeStringRepresentation().trim().length() == 0) {
			return null;
		}
		String prefix = item.getCodeStringRepresentation() + ".";
		try {
			return reply.substring(prefix.length());
		} catch (IndexOutOfBoundsException e) {
			if (logger.isLoggable(Level.SEVERE)) {
				logger.severe("Item is : " + item.getCodeStringRepresentation() + ".+\nwhile bindinvalue is :" + reply);
			}
		}
		return null;
	}

	// public String getPrototypeArrayCode(){
	// try{
	// int maxValues = getExampleRowCount();
	// if (maxValues == 0)
	// maxValues = 15;
	// StringBuffer prototypeArray = new StringBuffer();
	// for (int exampleRowIndex = 0; exampleRowIndex < maxValues; exampleRowIndex++) {
	// StringBuffer keyValues = new StringBuffer();
	// for (int j = 0; j < getHeaders().size(); j++) {
	//
	// Object[] allWidgetsInCol = findWidgetsMatchingHeader(getHeaders().get(j));
	// for (int k = 0; k < allWidgetsInCol.length; k++) {
	// IEWidget repeatedWidget = (IEWidget)allWidgetsInCol[k];
	// if (repeatedWidget != null) {
	// String keyPath = getKeyPathForWidget(repeatedWidget);
	// if (keyPath != null && keyPath.trim().length() > 0) {
	//
	// Object value;
	// Vector<String> values = getPrototypeValuesForWidget(repeatedWidget);
	// if (values == null || values.size() == 0) {
	// value = getSingleExampleValueForWidget(repeatedWidget);
	// } else {
	// value = values.elementAt(exampleRowIndex % values.size());
	// }
	// if (value != null) {
	// keyValues.append("		item.setObjectForKey(\"" + ToolBox.convertStringToJavaString(value.toString()) + "\",\"" + keyPath +
	// "\");").append(StringUtils.LINE_SEPARATOR);
	// }
	// }
	// }
	// }
	// }
	// prototypeArray.append("		item = new NSMutableDictionary();").append(StringUtils.LINE_SEPARATOR);
	// prototypeArray.append("		prototypeArray.addObject(item);").append(StringUtils.LINE_SEPARATOR);
	// prototypeArray.append("		item.setObjectForKey(\"ROW"+String.valueOf(exampleRowIndex)+"\",\"filterTest\");").append(StringUtils.LINE_SEPARATOR);
	// prototypeArray.append(keyValues);
	//
	// }
	//
	// //_properties.put("<REP_EXCELL_COLUMN>", rep_Excell_Code.toString());
	//
	// //_properties.put("<RAW_ROW_KEY_PATH>", rawRowKeyPath.toString());
	// return prototypeArray.toString();
	// }catch(ClassCastException e){
	// e.printStackTrace();
	// throw e;
	// }
	// }

	public Vector<String> getRawRowKeyPaths() {
		Vector<String> v = new Vector<String>();
		Vector<IETDWidget> tds = getRepeatedSequenceTR().getAllTD();
		for (IETDWidget td : tds) {
			Vector<IWidget> widgets = td.getSequenceWidget().getAllNonSequenceWidget();
			for (IWidget widget : widgets) {
				String s = widget.getRawRowKeyPath();
				if (s != null && !v.contains(s)) {
					v.add(s);
				}
			}
		}
		if (getRepetitionOperator().getEntity() instanceof DMEOEntity) {
			DMEOEntity entity = (DMEOEntity) getRepetitionOperator().getEntity();
			for (DMEOAttribute a : entity.getPrimaryKeyAttributes()) {
				if (!v.contains(a.getName())) {
					v.add(a.getName());
				}
			}
		}
		return v;
	}

	public static HTMLListDescriptor createInstanceForBloc(IEBlocWidget w) {
		if (w != null) {
			if (w.getContent() instanceof IEHTMLTableWidget) {
				IEHTMLTableWidget topTable = (IEHTMLTableWidget) w.getContent();
				IESequenceTR repeatedRow = topTable.getFirstRepeatedSequence();

				if (repeatedRow == null) {
					if (topTable.getTR(0) != null && topTable.getTR(0).getTD(0) != null
							&& topTable.getTR(0).getTD(0).getColSpan() == topTable.getColCount()) {
						if (topTable.getTR(0).getTD(0).getSequenceWidget().get(0) != null
								&& topTable.getTR(0).getTD(0).getSequenceWidget().get(0) instanceof IEHTMLTableWidget) {
							repeatedRow = ((IEHTMLTableWidget) topTable.getTR(0).getTD(0).getSequenceWidget().get(0))
									.getFirstRepeatedSequence();
						}
					}

					if (repeatedRow == null) {
						if (topTable.getTR(1) != null && topTable.getTR(1).getTD(0) != null
								&& topTable.getTR(1).getTD(0).getColSpan() == topTable.getColCount()) {
							if (topTable.getTR(1).getTD(0).getSequenceWidget().get(0) != null
									&& topTable.getTR(1).getTD(0).getSequenceWidget().get(0) instanceof IEHTMLTableWidget) {
								repeatedRow = ((IEHTMLTableWidget) topTable.getTR(1).getTD(0).getSequenceWidget().get(0))
										.getFirstRepeatedSequence();
							}
						}
					}

				}
				if (repeatedRow != null && repeatedRow.htmlTable() != null) {
					IETRWidget headerRow = repeatedRow.htmlTable().getHeaderRowForSequence(repeatedRow);
					if (headerRow != null) {
						return new HTMLListDescriptor(w, repeatedRow, headerRow);
					}
				}
			}
		}
		return null;
	}

	public Vector<IEControlWidget> getFilterWidgets() {
		if (hasSearch()) {
			Vector<IEControlWidget> reply = new Vector<IEControlWidget>();
			Enumeration<IObject> en = searchRow.getAllEmbeddedIEObjects().elements();
			while (en.hasMoreElements()) {
				IObject element = en.nextElement();
				if (element instanceof IETextFieldWidget || element instanceof IETextAreaWidget || element instanceof IEDropDownWidget
						|| element instanceof IECheckBoxWidget) {
					reply.add((IEControlWidget) element);
				}
			}
			return reply;
		}
		return null;
	}

	public boolean isHeaderCell(IETDWidget td) {
		return getHeaderRow() != null && getHeaderRow().containsTD(td);
	}

	public boolean isRepeatedCell(IETDWidget td) {
		return getRepeatedSequenceTR().containsTD(td);
	}

	public boolean hasRefresh() {
		return getRepetitionOperator().refreshButton();
	}

	public boolean hasExcel() {
		return getRepetitionOperator().excelButton();
	}

	public boolean isSearchRow(IEWidget widget) {
		if (widget != null) {
			return widget.equals(searchRow);
		}
		return false;
	}

	public String getDisplayGroupName() {
		return getListName() + "DisplayGroup";
	}

	/**
	 * @param widget
	 * @return
	 */
	public static HTMLListDescriptor createInstanceForHTMLTable(IEHTMLTableWidget widget) {
		IEHTMLTableWidget topTable = widget;
		IESequenceTR repeatedRow = topTable.getFirstRepeatedSequence();

		if (repeatedRow == null) {
			if (topTable.getTR(0) != null && topTable.getTR(0).getTD(0) != null
					&& topTable.getTR(0).getTD(0).getColSpan() == topTable.getColCount()) {
				if (topTable.getTR(0).getTD(0).getSequenceWidget().get(0) != null
						&& topTable.getTR(0).getTD(0).getSequenceWidget().get(0) instanceof IEHTMLTableWidget) {
					repeatedRow = ((IEHTMLTableWidget) topTable.getTR(0).getTD(0).getSequenceWidget().get(0)).getFirstRepeatedSequence();
				}
			}

			if (repeatedRow == null) {
				if (topTable.getTR(1) != null && topTable.getTR(1).getTD(0) != null
						&& topTable.getTR(1).getTD(0).getColSpan() == topTable.getColCount()) {
					if (topTable.getTR(1).getTD(0).getSequenceWidget().get(0) != null
							&& topTable.getTR(0).getTD(0).getSequenceWidget().get(0) instanceof IEHTMLTableWidget) {
						repeatedRow = ((IEHTMLTableWidget) topTable.getTR(1).getTD(0).getSequenceWidget().get(0))
								.getFirstRepeatedSequence();
					}
				}
				if (repeatedRow == null) {
					if (topTable.getTR(2) != null && topTable.getTR(2).getTD(0) != null
							&& topTable.getTR(2).getTD(0).getColSpan() == topTable.getColCount()) {
						if (topTable.getTR(2).getTD(0).getSequenceWidget().get(0) != null
								&& topTable.getTR(0).getTD(0).getSequenceWidget().get(0) instanceof IEHTMLTableWidget) {
							repeatedRow = ((IEHTMLTableWidget) topTable.getTR(2).getTD(0).getSequenceWidget().get(0))
									.getFirstRepeatedSequence();
						}
					}
				}
			}

		}
		if (repeatedRow != null && repeatedRow.htmlTable() != null) {
			IETRWidget headerRow = repeatedRow.htmlTable().getHeaderRowForSequence(repeatedRow);
			return new HTMLListDescriptor(null, repeatedRow, headerRow);
		}
		return null;
	}

	public static HTMLListDescriptor createInstanceForWidget(IEWidget widget) {
		IEWidget currentWidget = widget;
		RepetitionOperator repetition = null;
		if (widget instanceof RepetitionOperator) {
			repetition = (RepetitionOperator) widget;
		}
		while (currentWidget != null && currentWidget.getParent() instanceof IEWidget && repetition == null) {
			currentWidget = (IEWidget) currentWidget.getParent();
			if (currentWidget instanceof IESequence) {
				if (((IESequence) currentWidget).isRepetition()) {
					repetition = (RepetitionOperator) ((IESequence) currentWidget).getOperator();
				}
			}
		}
		if (repetition == null) {
			return null;
		}
		if (repetition.getOperatedSequence() == null) {
			return null;
		}
		if (repetition.getOperatedSequence().size() < 1) {
			return null;
		}
		IWidget w = repetition.getOperatedSequence().get(0);
		if (!(w instanceof ITableRow)) {
			return null;
		}
		ITableRow repeatedRow = (ITableRow) w;
		if (repeatedRow.htmlTable() != null) {
			IETRWidget headerRow = repeatedRow.htmlTable().getHeaderRowForSequence(repeatedRow);
			return new HTMLListDescriptor(null, repeatedRow, headerRow);
		}
		return null;
	}

	public String getAnchor() {
		if (getBloc() != null) {
			return getBloc().getAnchor();
		}
		return getRepetitionOperator().getAnchor();
	}

	public boolean isFetchingDMEOEntity() {
		return getRepetitionOperator().getEntity() != null && getRepetitionOperator().getEntity() instanceof DMEOEntity;
	}

	public boolean isFetchingRawRow() {
		return !getRepetitionOperator().getFetchObjects();
	}

	public Vector<DMEOAttribute> pkAttributesForFetchedEntity() {
		if (isFetchingDMEOEntity()) {
			return ((DMEOEntity) getRepetitionOperator().getEntity()).pkAttributes();
		}
		return new Vector<DMEOAttribute>();
	}

	public boolean isFetchingDMEOEntityWithIntegerPK() {
		if (isFetchingDMEOEntity()) {
			return ((DMEOEntity) getRepetitionOperator().getEntity()).isIntegerPrimaryKey();
		}
		return false;
	}

	public String getEntityName() {
		if (isFetchingDMEOEntity()) {
			return ((DMEOEntity) getRepetitionOperator().getEntity()).getName();
		}
		return null;
	}
}
