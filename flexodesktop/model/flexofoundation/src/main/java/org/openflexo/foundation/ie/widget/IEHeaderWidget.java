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
package org.openflexo.foundation.ie.widget;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.foundation.bindings.AbstractBinding;
import org.openflexo.foundation.ie.HTMLListDescriptor;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.IObject;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.dm.SortChanged;
import org.openflexo.foundation.ie.operator.RepetitionOperator;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.validation.FixProposal;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;

/**
 * Represents a header widget
 * 
 * @author bmangez
 */
public class IEHeaderWidget extends IENonEditableTextWidget implements ExtensibleWidget {

	/**
     * 
     */
	public static final String HEADER_WIDGET = "header_widget";

	private static final Logger logger = FlexoLogger.getLogger(IEHeaderWidget.class.getPackage().getName());

	protected boolean isSortable = true;

	protected boolean isSorted = false;

	protected boolean caseSensitive = false;

	protected boolean defaultDescending = false;

	// ==========================================================================
	// ============================= Constructor
	// ================================
	// ==========================================================================

	public IEHeaderWidget(FlexoComponentBuilder builder) {
		this(builder.woComponent, null, builder.getProject());
		initializeDeserialization(builder);
	}

	public IEHeaderWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj) {
		super(woComponent, parent, prj);
	}

	@Override
	public String getDefaultInspectorName() {
		return "Header.inspector";
	}

	public boolean getDefaultDescending() {
		return defaultDescending;
	}

	public boolean getDefaultAscending() {
		return !getDefaultDescending();
	}

	public void setDefaultDescending(boolean defaultDescending) {
		this.defaultDescending = defaultDescending;
		setChanged();
		notifyObservers(new IEDataModification("defaultDescending", null, new Boolean(defaultDescending)));
	}

	public boolean getIsSortable() {
		return isSortable;
	}

	public void setIsSortable(boolean isSortable) {
		this.isSortable = isSortable;
		setChanged();
		notifyObservers(new SortChanged());
	}

	public boolean getIsSorted() {
		return isSorted;
	}

	public boolean getIsNotSorted() {
		return !getIsSorted();
	}

	public void setIsSorted(boolean isSorted) {
		this.isSorted = isSorted;
		setChanged();
		notifyObservers(new SortChanged());
	}

	public boolean getCaseSensitive() {
		return caseSensitive;
	}

	public void setCaseSensitive(boolean value) {
		this.caseSensitive = value;
		setChanged();
		notifyObservers(new IEDataModification("caseSensitive", null, new Boolean(caseSensitive)));
	}

	/**
	 * Return a Vector of embedded IEObjects at this level. NOTE that this is NOT a recursive method
	 * 
	 * @return a Vector of IEObject instances
	 */
	@Override
	public Vector<IObject> getEmbeddedIEObjects() {
		return EMPTY_IOBJECT_VECTOR;
	}

	@Override
	public String getFullyQualifiedName() {
		return "Header";
	}

	/**
	 * Overrides getClassNameKey
	 * 
	 * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
	 */
	@Override
	public String getClassNameKey() {
		return HEADER_WIDGET;
	}

	public RepetitionOperator relatedRepetitionOperator() {
		if (relatedRepeatedSequence() == null) {
			return null;
		}
		return (RepetitionOperator) relatedRepeatedSequence().getOperator();
	}

	public IESequenceTR relatedRepeatedSequence() {
		if (getParent() instanceof IESequenceWidget && ((IESequenceWidget) getParent()).getParent() instanceof IETDWidget) {
			IETDWidget td = (IETDWidget) ((IESequenceWidget) getParent()).getParent();
			ITableRow tr = td.tr();
			IESequenceTR repeatedSequence = tr.findNextRepeatedSequence();
			if (repeatedSequence != null) {
				return repeatedSequence;
			} else {
				logger.warning("cannot find a repeated sequence for Header with title: " + getValue());
			}
		}
		return null;
	}

	public ITableData tdContainer() {
		if (getParent() instanceof IESequenceWidget) {
			return ((IESequenceWidget) getParent()).td();
		}
		return null;
	}

	public AbstractBinding relatedSortableBindingValueInRepetition() {
		IESequenceTR repeatedSequence = relatedRepeatedSequence();
		if (repeatedSequence != null) {
			int x = tdContainer().getIndex();
			IETRWidget firstTR = repeatedSequence.getFirstTR();
			IETDWidget relatedTD = firstTR.getTD(x);
			if (relatedTD != null) {
				if (relatedTD.getSequenceWidget().size() == 0) {
					return null;
				}
				IEWidget candidate = null;
				Enumeration en = relatedTD.getSequenceWidget().elements();
				while (en.hasMoreElements()) {
					candidate = (IEWidget) en.nextElement();
					if (candidate instanceof IENonEditableTextWidget) {
						return ((IENonEditableTextWidget) candidate).getBindingValue();
					}
				}
				return null;
			} else {
				logger.warning("cannot find a td for col index: " + x);
			}
		}
		return null;
	}

	public boolean hasRepetition() {
		return relatedRepetitionOperator() != null || getHTMLListDescriptor() != null;
	}

	public String getRepetitionName() {
		RepetitionOperator repetition = relatedRepetitionOperator();
		HTMLListDescriptor descriptor = getHTMLListDescriptor();
		if (descriptor == null && repetition != null) {
			descriptor = repetition.getHTMLListDescriptor();
		}
		if (descriptor == null) {
			return "norepetition";
		}
		return descriptor.getListName();
	}

	public IEWidget getSortedWidget() {
		if (getIsSortable()) {
			HTMLListDescriptor desc = getHTMLListDescriptor();
			if (desc != null) {
				ITableRow sequenceTR = desc.getRepeatedSequenceTR();
				if (sequenceTR != null) {
					IETRWidget tr = sequenceTR.getAllTR().firstElement();
					IETDWidget td = tr.getTDatXLocation(findTDInParent().getXLocation());
					return (IEWidget) td.getSequenceWidget().firstObject();
				} else {
					return null;
				}
			} else {
				return null;
			}
		} else {
			return null;
		}
	}

	public static class HeaderMustBeInATableContainingARepetition extends ValidationRule {

		/**
		 * @author gpolet
		 * 
		 */
		public class DeleteHeader extends FixProposal {

			/**
			 * @param aMessage
			 */
			public DeleteHeader() {
				super("delete_this_header");
			}

			/**
			 * Overrides fixAction
			 * 
			 * @see org.openflexo.foundation.validation.FixProposal#fixAction()
			 */
			@Override
			protected void fixAction() {
				((IEHeaderWidget) getObject()).removeFromContainer();
			}

		}

		/**
		 * @param objectType
		 * @param ruleName
		 */
		public HeaderMustBeInATableContainingARepetition() {
			super(IEHeaderWidget.class, "headers_must_be_in_a_table_containing_a_repetition");
		}

		/**
		 * Overrides applyValidation
		 * 
		 * @see org.openflexo.foundation.validation.ValidationRule#applyValidation(org.openflexo.foundation.validation.Validable)
		 */
		@Override
		public ValidationIssue applyValidation(Validable object) {
			IEHeaderWidget header = (IEHeaderWidget) object;
			HTMLListDescriptor desc = header.getHTMLListDescriptor();
			if (desc == null) {
				return new ValidationWarning(this, object, "headers_must_be_in_a_table_containing_a_repetition", new DeleteHeader());
			}
			return null;
		}

	}

	@Override
	public boolean generateJavascriptID() {
		return true;
	}

}
