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
package org.openflexo.dm.model;

import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Logger;

import org.openflexo.components.browser.BrowserConfiguration;
import org.openflexo.components.browser.BrowserElement;
import org.openflexo.components.browser.BrowserElementFactory;
import org.openflexo.components.browser.ProjectBrowser;
import org.openflexo.components.browser.dm.DMEOEntityElement;
import org.openflexo.components.browser.dm.DMEntityElement;
import org.openflexo.components.tabular.model.EditableStringColumn;
import org.openflexo.components.tabular.model.StringColumn;
import org.openflexo.components.tabularbrowser.TabularBrowserModel;
import org.openflexo.foundation.FlexoModelObject;
import org.openflexo.foundation.FlexoObject;
import org.openflexo.foundation.dm.DMEntity;
import org.openflexo.foundation.dm.eo.DMEOEntity;
import org.openflexo.foundation.FlexoProject;

public class TypeHierarchyBrowserModel extends TabularBrowserModel {

	protected static final Logger logger = Logger.getLogger(TypeHierarchyBrowserModel.class.getPackage().getName());

	public TypeHierarchyBrowserModel(DMEntity entity) {
		super(makeBrowserConfiguration(entity), " ", 300);
		addToColumns(new StringColumn("package", 250) {
			@Override
			public String getValue(FlexoModelObject object) {
				if (object instanceof DMEntity) {
					return ((DMEntity) object).getPackage().getLocalizedName();
				}
				return null;
			}
		});
		addToColumns(new EditableStringColumn<FlexoModelObject>("description", 300) {
			@Override
			public void setValue(FlexoModelObject object, String aValue) {
				if (object instanceof DMEntity) {
					((DMEntity) object).setDescription(aValue);
				}
			}

			@Override
			public String getValue(FlexoModelObject object) {
				if (object instanceof DMEntity) {
					return ((DMEntity) object).getDescription();
				}
				return null;
			}
		});
		setRowHeight(20);
		focusOn(entity);
	}

	private static BrowserConfiguration makeBrowserConfiguration(final DMEntity entity) {
		BrowserConfiguration returned = new TypeHierarchyBrowserConfiguration(entity);
		return returned;
	}

	private static class TypeHierarchyBrowserConfiguration implements BrowserConfiguration {
		private DMEntity _entity;
		private Vector<DMEntity> _parentEntities;
		private TypeHierarchyBrowserElementFactory _factory;
		private DMEntity _topLevelEntity;

		protected TypeHierarchyBrowserConfiguration(DMEntity entity) {
			super();
			_entity = entity;
			_parentEntities = new Vector<DMEntity>();
			_parentEntities.add(_entity);
			DMEntity current = _entity;
			while (current.getParentBaseEntity() != null) {
				_parentEntities.add(current.getParentBaseEntity());
				current = current.getParentBaseEntity();
			}
			_topLevelEntity = current;
			_factory = new TypeHierarchyBrowserElementFactory();
		}

		protected DMEntity getEntity() {
			return _entity;
		}

		protected Vector getParentEntities() {
			return _parentEntities;
		}

		@Override
		public FlexoProject getProject() {
			return _entity.getProject();
		}

		@Override
		public void configure(ProjectBrowser browser) {
		}

		@Override
		public FlexoModelObject getDefaultRootObject() {
			return _topLevelEntity;
			// return getProject().getDataModel().getDMEntity(Object.class);
		}

		@Override
		public BrowserElementFactory getBrowserElementFactory() {
			return _factory;
		}

		private class TypeHierarchyBrowserElementFactory implements BrowserElementFactory {

			TypeHierarchyBrowserElementFactory() {
				super();
			}

			@Override
			public BrowserElement makeNewElement(FlexoObject object, ProjectBrowser browser, BrowserElement parent) {
				if (object instanceof DMEOEntity) {
					return new THDMEOEntityElement((DMEOEntity) object, browser, parent);
				} else if (object instanceof DMEntity) {
					return new THDMEntityElement((DMEntity) object, browser, parent);
				}
				return null;
			}

			private class THDMEntityElement extends DMEntityElement {
				public THDMEntityElement(DMEntity entity, ProjectBrowser browser, BrowserElement parent) {
					super(entity, browser, parent);
				}

				@Override
				protected void buildChildrenVector() {
					// logger.info("Build children for "+getDMEntity());
					for (Enumeration e = getDMEntity().getChildEntities().elements(); e.hasMoreElements();) {
						DMEntity next = (DMEntity) e.nextElement();
						// logger.info("Identify "+next+" as children for "+getDMEntity());
						TabularBrowserModel myBrowser = (TabularBrowserModel) _browser;
						TypeHierarchyBrowserConfiguration configuration = (TypeHierarchyBrowserConfiguration) myBrowser.getConfiguration();
						// logger.info("configuration="+configuration);
						if (next.isAncestorOf(configuration.getEntity())) {
							// logger.info("Anscestor "+next+"_parentEntities="+configuration.getParentEntities());
							if (configuration.getParentEntities().contains(next)) {
								addToChilds(next);
							}
						} else if (configuration.getEntity().isAncestorOf(next)) {
							addToChilds(next);
						}
					}
				}

			}

			private class THDMEOEntityElement extends DMEOEntityElement {
				public THDMEOEntityElement(DMEOEntity entity, ProjectBrowser browser, BrowserElement parent) {
					super(entity, browser, parent);
				}

				@Override
				protected void buildChildrenVector() {
					// logger.info("Build children for "+getDMEntity());
					for (Enumeration e = getDMEntity().getChildEntities().elements(); e.hasMoreElements();) {
						DMEntity next = (DMEntity) e.nextElement();
						// logger.info("Identify "+next+" as children for "+getDMEntity());
						TabularBrowserModel myBrowser = (TabularBrowserModel) _browser;
						TypeHierarchyBrowserConfiguration configuration = (TypeHierarchyBrowserConfiguration) myBrowser.getConfiguration();
						// logger.info("configuration="+configuration);
						if (next.isAncestorOf(configuration.getEntity())) {
							// logger.info("Anscestor "+next+"_parentEntities="+configuration.getParentEntities());
							if (configuration.getParentEntities().contains(next)) {
								addToChilds(next);
							}
						} else if (configuration.getEntity().isAncestorOf(next)) {
							addToChilds(next);
						}
					}
				}

			}
		}
	}

}