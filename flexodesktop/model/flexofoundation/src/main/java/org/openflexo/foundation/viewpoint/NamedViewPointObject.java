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
package org.openflexo.foundation.viewpoint;

import java.lang.reflect.Type;
import java.util.logging.Logger;

import org.openflexo.antar.binding.BindingModel;
import org.openflexo.foundation.NameChanged;
import org.openflexo.model.annotations.Getter;
import org.openflexo.model.annotations.ImplementationClass;
import org.openflexo.model.annotations.ModelEntity;
import org.openflexo.model.annotations.PropertyIdentifier;
import org.openflexo.model.annotations.Setter;
import org.openflexo.model.annotations.XMLAttribute;

/**
 * Represents an object which is part of the model of a ViewPoint, and has a name, a description and can be identified by an URI
 * 
 * @author sylvain
 * 
 */
@ModelEntity(isAbstract = true)
@ImplementationClass(NamedViewPointObject.NamedViewPointObjectImpl.class)
public abstract interface NamedViewPointObject extends ViewPointObject {

	@PropertyIdentifier(type = String.class)
	public static final String NAME_KEY = "name";

	@Getter(value = NAME_KEY)
	@XMLAttribute
	public String getName();

	@Setter(NAME_KEY)
	public void setName(String name);

	/**
	 * Return the URI of the {@link NamedViewPointObject}<br>
	 * The convention for URI are following: <viewpoint_uri>/<virtual_model_name>#<edition_pattern_name>.<edition_scheme_name> <br>
	 * eg<br>
	 * http://www.mydomain.org/MyViewPoint/MyVirtualModel#MyEditionPattern.MyEditionScheme
	 * 
	 * @return String representing unique URI of this object
	 */
	public String getURI();

	public static abstract class NamedViewPointObjectImpl extends ViewPointObjectImpl implements NamedViewPointObject {

		private static final Logger logger = Logger.getLogger(NamedViewPointObject.class.getPackage().getName());

		private String name;
		private String description;

		public NamedViewPointObjectImpl(/*VirtualModelBuilder builder*/) {
			super(/*builder*/);
		}

		/*public NamedViewPointObjectImpl(ViewPointBuilder builder) {
			super(builder);
		}*/

		/*public NamedViewPointObjectImpl(ExampleDiagramBuilder builder) {
			super(builder);
		}

		public NamedViewPointObjectImpl(DiagramPaletteBuilder builder) {
			super(builder);
		}*/

		/**
		 * Return the URI of the {@link NamedViewPointObject}<br>
		 * The convention for URI are following: <viewpoint_uri>/<virtual_model_name>#<edition_pattern_name>.<edition_scheme_name> <br>
		 * eg<br>
		 * http://www.mydomain.org/MyViewPoint/MyVirtualModel#MyEditionPattern.MyEditionScheme
		 * 
		 * @return String representing unique URI of this object
		 */
		// TODO: change to abstract
		@Override
		public String getURI() {
			return null;
		}

		@Override
		public String getName() {
			return name;
		}

		@Override
		public void setName(String name) {
			if (requireChange(this.name, name)) {
				String oldName = this.name;
				this.name = name;
				setChanged();
				notifyObservers(new NameChanged(oldName, name));
			}
		}

		@Override
		public Object performSuperGetter(String propertyIdentifier) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void performSuperSetter(String propertyIdentifier, Object value) {
			// TODO Auto-generated method stub

		}

		@Override
		public void performSuperAdder(String propertyIdentifier, Object value) {
			// TODO Auto-generated method stub

		}

		@Override
		public void performSuperRemover(String propertyIdentifier, Object value) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object performSuperGetter(String propertyIdentifier, Class<?> modelEntityInterface) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void performSuperSetter(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
			// TODO Auto-generated method stub

		}

		@Override
		public void performSuperAdder(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
			// TODO Auto-generated method stub

		}

		@Override
		public void performSuperRemover(String propertyIdentifier, Object value, Class<?> modelEntityInterface) {
			// TODO Auto-generated method stub

		}

		@Override
		public void performSuperSetModified(boolean modified) {
			// TODO Auto-generated method stub

		}

		@Override
		public Object performSuperFinder(String finderIdentifier, Object value) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object performSuperFinder(String finderIdentifier, Object value, Class<?> modelEntityInterface) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isSerializing() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isDeserializing() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void setModified(boolean modified) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean equalsObject(Object obj) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void destroy() {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean hasKey(String key) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean performSuperDelete(Object... context) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean performSuperUndelete() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public void performSuperDelete(Class<?> modelEntityInterface, Object... context) {
			// TODO Auto-generated method stub

		}

		@Override
		public boolean delete(Object... context) {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean undelete() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public Object cloneObject() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object cloneObject(Object... context) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public boolean isCreatedByCloning() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public boolean isBeingCloned() {
			// TODO Auto-generated method stub
			return false;
		}

		@Override
		public BindingModel getBindingModel() {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public Object objectForKey(String key) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		public void setObjectForKey(Object value, String key) {
			// TODO Auto-generated method stub
		}

		@Override
		public Type getTypeForKey(String key) {
			// TODO Auto-generated method stub
			return null;
		}

	}
}
