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
package org.openflexo.foundation.viewpoint.inspector;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.antar.binding.Bindable;
import org.openflexo.antar.binding.BindingFactory;
import org.openflexo.antar.binding.BindingModel;
import org.openflexo.antar.binding.BindingVariable;
import org.openflexo.antar.binding.DataBinding;
import org.openflexo.antar.binding.DataBinding.BindingDefinitionType;
import org.openflexo.foundation.viewpoint.EditionPattern;
import org.openflexo.foundation.viewpoint.EditionPatternInstanceType;
import org.openflexo.foundation.viewpoint.EditionPatternObject;
import org.openflexo.foundation.viewpoint.ViewPointLibrary;
import org.openflexo.foundation.viewpoint.VirtualModel;
import org.openflexo.foundation.viewpoint.dm.InspectorEntryInserted;
import org.openflexo.foundation.viewpoint.dm.InspectorEntryRemoved;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.model.annotations.Adder;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.Getter.Cardinality;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Remover;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;
import org.openflexo.model.annotations.XMLElement;

/**
 * Represents inspector associated with an Edition Pattern
 * 
 * @author sylvain
 * 
 */
@ModelEntity
@ImplementationClass(EditionPatternInspector.EditionPatternInspectorImpl.class)
@XMLElement(xmlTag = "Inspector")
public interface EditionPatternInspector extends EditionPatternObject, Bindable {

	@PropertyIdentifier(type = EditionPattern.class)
	public static final String EDITION_PATTERN_KEY = "edition_pattern";
	@PropertyIdentifier(type = String.class)
	public static final String INSPECTOR_TITLE_KEY = "inspectorTitle";
	@PropertyIdentifier(type = DataBinding.class)
	public static final String RENDERER_KEY = "renderer";
	@PropertyIdentifier(type = Vector.class)
	public static final String ENTRIES_KEY = "entries";

	@Override
	@Getter(value = EDITION_PATTERN_KEY, inverse = EditionPattern.INSPECTOR_KEY)
	public EditionPattern getEditionPattern();

	@Setter(EDITION_PATTERN_KEY)
	public void setEditionPattern(EditionPattern editionPattern);

	@Getter(value = INSPECTOR_TITLE_KEY)
	@XMLAttribute
	public String getInspectorTitle();

	@Setter(INSPECTOR_TITLE_KEY)
	public void setInspectorTitle(String inspectorTitle);

	@Getter(value = RENDERER_KEY)
	@XMLAttribute
	public DataBinding getRenderer();

	@Setter(RENDERER_KEY)
	public void setRenderer(DataBinding renderer);

	@Getter(value = ENTRIES_KEY, cardinality = Cardinality.LIST)
	@XMLElement
	public List<InspectorEntry> getEntries();

	@Setter(ENTRIES_KEY)
	public void setEntries(List<InspectorEntry> entries);

	@Adder(ENTRIES_KEY)
	public void addToEntries(InspectorEntry aEntrie);

	@Remover(ENTRIES_KEY)
	public void removeFromEntries(InspectorEntry aEntrie);

	public static abstract class EditionPatternInspectorImpl extends EditionPatternObjectImpl implements EditionPatternInspector {

		private static final Logger logger = FlexoLogger.getLogger(EditionPatternInspector.class.getPackage().toString());

		private String inspectorTitle;
		private EditionPattern _editionPattern;
		private Vector<InspectorEntry> entries;
		private DataBinding<String> renderer;

		private final EditionPatternFormatter formatter;

		public static EditionPatternInspector makeEditionPatternInspector(EditionPattern ep) {
			EditionPatternInspector returned = new EditionPatternInspector();
			returned.setInspectorTitle(ep.getName());
			ep.setInspector(returned);
			return returned;
		}

		public EditionPatternInspectorImpl() {
			super();
			entries = new Vector<InspectorEntry>();
			formatter = new EditionPatternFormatter();
		}

		@Override
		public String getURI() {
			return null;
		}

		public EditionPatternFormatter getFormatter() {
			return formatter;
		}

		@Override
		public Collection<InspectorEntry> getEmbeddedValidableObjects() {
			return entries;
		}

		@Override
		public EditionPattern getEditionPattern() {
			return _editionPattern;
		}

		@Override
		public void setEditionPattern(EditionPattern editionPattern) {
			_editionPattern = editionPattern;
			formatter.notifiedBindingModelRecreated();
		}

		@Override
		public VirtualModel getVirtualModel() {
			if (getEditionPattern() != null) {
				return getEditionPattern().getVirtualModel();
			}
			return null;
		}

		public ViewPointLibrary getCalcLibrary() {
			return getVirtualModel().getViewPointLibrary();
		}

		@Override
		public String getInspectorTitle() {
			return inspectorTitle;
		}

		@Override
		public void setInspectorTitle(String inspectorTitle) {
			this.inspectorTitle = inspectorTitle;
		}

		@Override
		public Vector<InspectorEntry> getEntries() {
			return entries;
		}

		public void setEntries(Vector<InspectorEntry> someEntries) {
			entries = someEntries;
		}

		@Override
		public void addToEntries(InspectorEntry anEntry) {
			anEntry.setInspector(this);
			entries.add(anEntry);
			setChanged();
			notifyObservers(new InspectorEntryInserted(anEntry, this));
		}

		@Override
		public void removeFromEntries(InspectorEntry anEntry) {
			anEntry.setInspector(null);
			entries.remove(anEntry);
			setChanged();
			notifyObservers(new InspectorEntryRemoved(anEntry, this));
		}

		public TextFieldInspectorEntry createNewTextField() {
			TextFieldInspectorEntry newEntry = new TextFieldInspectorEntry();
			newEntry.setName("textfield");
			// newEntry.setLabel("textfield");
			addToEntries(newEntry);
			return newEntry;
		}

		public TextAreaInspectorEntry createNewTextArea() {
			TextAreaInspectorEntry newEntry = new TextAreaInspectorEntry();
			newEntry.setName("textarea");
			// newEntry.setLabel("textarea");
			addToEntries(newEntry);
			return newEntry;
		}

		public IntegerInspectorEntry createNewInteger() {
			IntegerInspectorEntry newEntry = new IntegerInspectorEntry();
			newEntry.setName("integer");
			// newEntry.setLabel("integer");
			addToEntries(newEntry);
			return newEntry;
		}

		public CheckboxInspectorEntry createNewCheckbox() {
			CheckboxInspectorEntry newEntry = new CheckboxInspectorEntry();
			newEntry.setName("checkbox");
			// newEntry.setLabel("checkbox");
			addToEntries(newEntry);
			return newEntry;
		}

		public IndividualInspectorEntry createNewIndividual() {
			IndividualInspectorEntry newEntry = new IndividualInspectorEntry();
			newEntry.setName("individual");
			// newEntry.setLabel("individual");
			addToEntries(newEntry);
			return newEntry;
		}

		public ClassInspectorEntry createNewClass() {
			ClassInspectorEntry newEntry = new ClassInspectorEntry();
			newEntry.setName("class");
			// newEntry.setLabel("class");
			addToEntries(newEntry);
			return newEntry;
		}

		public PropertyInspectorEntry createNewProperty() {
			PropertyInspectorEntry newEntry = new PropertyInspectorEntry();
			newEntry.setName("property");
			// newEntry.setLabel("class");
			addToEntries(newEntry);
			return newEntry;
		}

		public ObjectPropertyInspectorEntry createNewObjectProperty() {
			ObjectPropertyInspectorEntry newEntry = new ObjectPropertyInspectorEntry();
			newEntry.setName("property");
			// newEntry.setLabel("class");
			addToEntries(newEntry);
			return newEntry;
		}

		public DataPropertyInspectorEntry createNewDataProperty() {
			DataPropertyInspectorEntry newEntry = new DataPropertyInspectorEntry();
			newEntry.setName("property");
			// newEntry.setLabel("class");
			addToEntries(newEntry);
			return newEntry;
		}

		/*public FlexoObjectInspectorEntry createNewFlexoObject() {
			FlexoObjectInspectorEntry newEntry = new FlexoObjectInspectorEntry();
			newEntry.setName("flexoObject");
			// newEntry.setLabel("flexoObject");
			addToEntries(newEntry);
			return newEntry;
		}*/

		public InspectorEntry deleteEntry(InspectorEntry entry) {
			removeFromEntries(entry);
			entry.delete();
			return entry;
		}

		@Override
		public void notifyBindingModelChanged() {
			super.notifyBindingModelChanged();
			// SGU: as all entries share the edition pattern binding model, they should
			// all notify change of their binding models
			for (InspectorEntry entry : getEntries()) {
				entry.notifyBindingModelChanged();
			}
		}

		@Override
		public final BindingModel getBindingModel() {
			return getEditionPattern().getBindingModel();
			/*if (_bindingModel == null) {
				createBindingModel();
			}
			return _bindingModel;*/
		}

		/*public void updateBindingModel() {
			logger.fine("updateBindingModel()");
			_bindingModel = null;
			createBindingModel();
		}

		private void createBindingModel() {
			_bindingModel = new BindingModel();
			for (PatternRole role : getEditionPattern().getPatternRoles()) {
				_bindingModel.addToBindingVariables(PatternRolePathElement.makePatternRolePathElement(role, (EditionPatternInstance) null));
			}
		}*/

		public void entryFirst(InspectorEntry p) {
			entries.remove(p);
			entries.insertElementAt(p, 0);
			setChanged();
			notifyObservers();
			notifyChange("entries", null, entries);
		}

		public void entryUp(InspectorEntry p) {
			int index = entries.indexOf(p);
			if (index > 0) {
				entries.remove(p);
				entries.insertElementAt(p, index - 1);
				setChanged();
				notifyObservers();
				notifyChange("entries", null, entries);
			}
		}

		public void entryDown(InspectorEntry p) {
			int index = entries.indexOf(p);
			if (index > -1) {
				entries.remove(p);
				entries.insertElementAt(p, index + 1);
				setChanged();
				notifyObservers();
				notifyChange("entries", null, entries);
			}
		}

		public void entryLast(InspectorEntry p) {
			entries.remove(p);
			entries.add(p);
			setChanged();
			notifyObservers();
			notifyChange("entries", null, entries);
		}

		@Override
		public DataBinding<String> getRenderer() {
			if (renderer == null) {
				renderer = new DataBinding<String>(formatter, String.class, BindingDefinitionType.GET);
				renderer.setBindingName("renderer");
			}
			return renderer;
		}

		public void setRenderer(DataBinding<String> renderer) {
			if (renderer != null) {
				renderer.setOwner(formatter);
				renderer.setDeclaredType(String.class);
				renderer.setBindingDefinitionType(BindingDefinitionType.GET);
				renderer.setBindingName("renderer");
			}
			this.renderer = renderer;
			notifiedBindingChanged(this.renderer);
		}

		private class EditionPatternFormatter implements Bindable {
			private BindingModel formatterBindingModel = null;

			public void notifiedBindingModelRecreated() {
				createFormatterBindingModel();
			}

			@Override
			public BindingFactory getBindingFactory() {
				return EditionPatternInspector.this.getBindingFactory();
			}

			@Override
			public BindingModel getBindingModel() {
				if (formatterBindingModel == null) {
					createFormatterBindingModel();
				}
				return formatterBindingModel;
			}

			private void createFormatterBindingModel() {
				formatterBindingModel = new BindingModel();
				formatterBindingModel.addToBindingVariables(new BindingVariable("instance", EditionPatternInstanceType
						.getEditionPatternInstanceType(getEditionPattern())) {
					@Override
					public Type getType() {
						return EditionPatternInstanceType.getEditionPatternInstanceType(getEditionPattern());
					}
				});
			}

			@Override
			public void notifiedBindingChanged(DataBinding<?> dataBinding) {
				if (dataBinding == getRenderer()) {
					EditionPatternInspector.this.notifiedBindingChanged(dataBinding);
				}
			}

			@Override
			public void notifiedBindingDecoded(DataBinding<?> dataBinding) {
				if (dataBinding == getRenderer()) {
					EditionPatternInspector.this.notifiedBindingDecoded(dataBinding);
				}
			}

		}

	}
}
