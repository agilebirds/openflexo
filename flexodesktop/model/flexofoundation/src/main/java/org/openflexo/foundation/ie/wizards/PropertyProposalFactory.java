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
package org.openflexo.foundation.ie.wizards;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.bindings.BindingDefinition;
import org.openflexo.foundation.dm.DMType;
import org.openflexo.foundation.dm.eo.DMEOPrototype;
import org.openflexo.foundation.ie.util.DropDownType;
import org.openflexo.foundation.ie.util.TextFieldType;
import org.openflexo.foundation.ie.widget.IECheckBoxWidget;
import org.openflexo.foundation.ie.widget.IEControlWidget;
import org.openflexo.foundation.ie.widget.IEDropDownWidget;
import org.openflexo.foundation.ie.widget.IEStringWidget;
import org.openflexo.foundation.ie.widget.IETextAreaWidget;
import org.openflexo.foundation.ie.widget.IETextFieldWidget;
import org.openflexo.foundation.ie.widget.IEWidget;
import org.openflexo.toolbox.ToolBox;

public class PropertyProposalFactory {

	private List relevantWidgets;
	private EntityFromWidgets entityFromWidgets;

	private PropertyProposalFactory() {
		super();
		relevantWidgets = Arrays.asList(IETextFieldWidget.class, IETextAreaWidget.class, IEDropDownWidget.class, IEStringWidget.class,
				IECheckBoxWidget.class);
	}

	public static PropertyProposalFactory getFactory(EntityFromWidgets entityFromWidgets) {
		PropertyProposalFactory reply = new PropertyProposalFactory();
		reply.entityFromWidgets = entityFromWidgets;
		return reply;
	}

	public boolean isRelevant(FlexoModelObject widget) {

		if (widget instanceof IEControlWidget && ((IEControlWidget) widget).getIsFilterForRepetition() != null) {
			return false;
		}
		if (relevantWidgets.contains(widget.getClass())) {
			return true;
		}
		return false;
	}

	private String transformPropertyNameIntoUniquePropertyName(String propertyName) {
		if (propertyName == null) {
			return null;
		}
		String originalPropertyName = propertyName;
		int i = 1;
		while (entityFromWidgets.isPropertyNameUsed(propertyName)) {
			propertyName = originalPropertyName + i;
			i++;
		}
		return propertyName;
	}

	public PropertyProposal getEOAttributeProposal(IEWidget widget) {
		String propertyName = ToolBox.convertStringToJavaString(widget.getCalculatedLabel());
		transformPropertyNameIntoUniquePropertyName(propertyName);
		if (widget instanceof IEStringWidget) {
			if (propertyName == null) {
				propertyName = transformPropertyNameIntoUniquePropertyName("textAttribute");
			}
			DMEOPrototype p = findBestProptotype((IEStringWidget) widget);
			return new EOAttributeProposal(widget, propertyName, p);
		} else if (widget instanceof IETextFieldWidget) {
			if (propertyName == null) {
				propertyName = transformPropertyNameIntoUniquePropertyName("textAttribute");
			}
			DMEOPrototype p = findBestProptotype((IETextFieldWidget) widget);
			return new EOAttributeProposal(widget, propertyName, p);
		} else if (widget instanceof IETextAreaWidget) {
			if (propertyName == null) {
				propertyName = transformPropertyNameIntoUniquePropertyName("longTextAttribute");
			}
			return new EOAttributeProposal(widget, propertyName, widget.getProject().getDataModel().getPrototypeNamed("clob"));
		} else if (widget instanceof IECheckBoxWidget) {
			if (propertyName == null) {
				propertyName = transformPropertyNameIntoUniquePropertyName("isTrue");
			} else {
				propertyName = "is" + ToolBox.capitalize(propertyName, true);
			}
			return new EOAttributeProposal(widget, propertyName, widget.getProject().getDataModel().getPrototypeNamed("boolean"));
		} else if (widget instanceof IEDropDownWidget) {
			if (propertyName == null) {
				propertyName = transformPropertyNameIntoUniquePropertyName("typeProperty");
			}
			if (((IEDropDownWidget) widget).getDropdownType() == DropDownType.DOMAIN_KEY_VALUE) {
				return new EOAttributeProposal(widget, propertyName, widget.getProject().getDataModel().getPrototypeNamed("str10"));
			} else {
				return new EOAttributeProposal(widget, propertyName, widget.getProject().getDataModel().getPrototypeNamed("str100"));
			}
		}
		return null;
	}

	private DMEOPrototype findBestProptotype(IEStringWidget widget) {
		if (widget.getFieldType() == TextFieldType.DATE) {
			return widget.getProject().getDataModel().getPrototypeNamed("date");
		}
		if (widget.getFieldType() == TextFieldType.DOUBLE) {
			return widget.getProject().getDataModel().getPrototypeNamed("double");
		}
		if (widget.getFieldType() == TextFieldType.FLOAT) {
			return widget.getProject().getDataModel().getPrototypeNamed("float");
		}
		if (widget.getFieldType() == TextFieldType.INTEGER) {
			return widget.getProject().getDataModel().getPrototypeNamed("int");
		}
		if (widget.getFieldType() == TextFieldType.KEYVALUE) {
			return widget.getProject().getDataModel().getPrototypeNamed("str10");
		}
		if (widget.getFieldType() == TextFieldType.STATUS_LIST) {
			return widget.getProject().getDataModel().getPrototypeNamed("str50");
		}
		return widget.getProject().getDataModel().getPrototypeNamed("str200");
	}

	private DMEOPrototype findBestProptotype(IETextFieldWidget widget) {
		if (widget.getFieldType() == TextFieldType.DATE) {
			return widget.getProject().getDataModel().getPrototypeNamed("date");
		}
		if (widget.getFieldType() == TextFieldType.DOUBLE) {
			return widget.getProject().getDataModel().getPrototypeNamed("double");
		}
		if (widget.getFieldType() == TextFieldType.FLOAT) {
			return widget.getProject().getDataModel().getPrototypeNamed("float");
		}
		if (widget.getFieldType() == TextFieldType.INTEGER) {
			return widget.getProject().getDataModel().getPrototypeNamed("int");
		}
		if (widget.getFieldType() == TextFieldType.KEYVALUE) {
			return widget.getProject().getDataModel().getPrototypeNamed("str10");
		}
		if (widget.getFieldType() == TextFieldType.STATUS_LIST) {
			return widget.getProject().getDataModel().getPrototypeNamed("str50");
		}
		return widget.getProject().getDataModel().getPrototypeNamed("str200");
	}

	public DMPropertyProposal getDMAttributeProposal(IEWidget widget) {
		String propertyName = ToolBox.convertStringToJavaString(widget.getCalculatedLabel());
		transformPropertyNameIntoUniquePropertyName(propertyName);
		if (widget instanceof IETextFieldWidget) {
			if (propertyName == null) {
				propertyName = transformPropertyNameIntoUniquePropertyName("textAttribute");
			}
			DMType t = findBestType((IETextFieldWidget) widget);
			return new DMPropertyProposal(widget, propertyName, t);
		} else if (widget instanceof IEStringWidget) {
			if (propertyName == null) {
				propertyName = transformPropertyNameIntoUniquePropertyName("textAttribute");
			}
			DMType t = findBestType((IEStringWidget) widget);
			return new DMPropertyProposal(widget, propertyName, t);
		} else if (widget instanceof IETextAreaWidget) {
			if (propertyName == null) {
				propertyName = transformPropertyNameIntoUniquePropertyName("longTextAttribute");
			}
			return new DMPropertyProposal(widget, propertyName, DMType.makeResolvedDMType(widget.getProject().getDataModel()
					.getDMEntity(String.class)));
		} else if (widget instanceof IECheckBoxWidget) {
			if (propertyName == null) {
				propertyName = transformPropertyNameIntoUniquePropertyName("isTrue");
			} else {
				propertyName = "is" + ToolBox.capitalize(propertyName, true);
			}
			return new DMPropertyProposal(widget, propertyName, DMType.makeResolvedDMType(widget.getProject().getDataModel()
					.getDMEntity(Boolean.class)));
		} else if (widget instanceof IEDropDownWidget) {
			if (propertyName == null) {
				propertyName = transformPropertyNameIntoUniquePropertyName("typeProperty");
			}
			if (((IEDropDownWidget) widget).getDropdownType() == DropDownType.DOMAIN_KEY_VALUE) {
				return new DMPropertyProposal(widget, propertyName, DMType.makeResolvedDMType(widget.getProject().getDataModel()
						.getDMEntity(String.class)));
			} else {
				return new DMPropertyProposal(widget, propertyName, DMType.makeResolvedDMType(widget.getProject().getDataModel()
						.getDMEntity(String.class)));
			}
		}
		return null;
	}

	private DMType findBestType(IEStringWidget widget) {
		if (widget.getFieldType() == TextFieldType.DATE) {
			return DMType.makeResolvedDMType(widget.getProject().getDataModel().getDMEntity(Date.class));
		}
		if (widget.getFieldType() == TextFieldType.DOUBLE) {
			return DMType.makeResolvedDMType(widget.getProject().getDataModel().getDMEntity(Double.class));
		}
		if (widget.getFieldType() == TextFieldType.FLOAT) {
			return DMType.makeResolvedDMType(widget.getProject().getDataModel().getDMEntity(Float.class));
		}
		if (widget.getFieldType() == TextFieldType.INTEGER) {
			return DMType.makeResolvedDMType(widget.getProject().getDataModel().getDMEntity(Integer.class));
		}
		if (widget.getFieldType() == TextFieldType.KEYVALUE) {
			return DMType.makeResolvedDMType(widget.getProject().getDataModel().getDMEntity(String.class));
		}
		if (widget.getFieldType() == TextFieldType.STATUS_LIST) {
			return DMType.makeResolvedDMType(widget.getProject().getDataModel().getDMEntity(String.class));
		}
		return DMType.makeResolvedDMType(widget.getProject().getDataModel().getDMEntity(String.class));
	}

	private DMType findBestType(IETextFieldWidget widget) {
		if (widget.getFieldType() == TextFieldType.DATE) {
			return DMType.makeResolvedDMType(widget.getProject().getDataModel().getDMEntity(Date.class));
		}
		if (widget.getFieldType() == TextFieldType.DOUBLE) {
			return DMType.makeResolvedDMType(widget.getProject().getDataModel().getDMEntity(Double.class));
		}
		if (widget.getFieldType() == TextFieldType.FLOAT) {
			return DMType.makeResolvedDMType(widget.getProject().getDataModel().getDMEntity(Float.class));
		}
		if (widget.getFieldType() == TextFieldType.INTEGER) {
			return DMType.makeResolvedDMType(widget.getProject().getDataModel().getDMEntity(Integer.class));
		}
		if (widget.getFieldType() == TextFieldType.KEYVALUE) {
			return DMType.makeResolvedDMType(widget.getProject().getDataModel().getDMEntity(String.class));
		}
		if (widget.getFieldType() == TextFieldType.STATUS_LIST) {
			return DMType.makeResolvedDMType(widget.getProject().getDataModel().getDMEntity(String.class));
		}
		return DMType.makeResolvedDMType(widget.getProject().getDataModel().getDMEntity(String.class));
	}

	public BindingDefinition retreiveRelevantBindingDefinition(IEWidget owner) {
		if (owner.getClass().equals(IETextFieldWidget.class)) {
			return ((IETextFieldWidget) owner).getBindingValueDefinition();
		} else if (owner.getClass().equals(IETextAreaWidget.class)) {
			return ((IETextAreaWidget) owner).getBindingValueDefinition();
		} else if (owner.getClass().equals(IEDropDownWidget.class)) {
			return ((IEDropDownWidget) owner).getBindingSelectionDefinition();
		} else if (owner.getClass().equals(IEStringWidget.class)) {
			return ((IEStringWidget) owner).getBindingValueDefinition();
		} else if (owner.getClass().equals(IECheckBoxWidget.class)) {
			return ((IECheckBoxWidget) owner).getBindingCheckedDefinition();
		}
		return null;
	}
}
