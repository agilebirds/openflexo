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
package org.openflexo.rm;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Enumeration;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.Icon;
import javax.swing.table.AbstractTableModel;

import org.openflexo.foundation.DataModification;
import org.openflexo.foundation.FlexoObservable;
import org.openflexo.foundation.GraphicalFlexoObserver;
import org.openflexo.foundation.rm.FlexoFileResource;
import org.openflexo.foundation.rm.FlexoGeneratedResource;
import org.openflexo.foundation.rm.FlexoImportedResource;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoResource;
import org.openflexo.foundation.rm.FlexoStorageResource;
import org.openflexo.foundation.rm.FlexoXMLStorageResource;
import org.openflexo.icon.IconLibrary;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.module.FlexoModule;
import org.openflexo.module.ModuleLoader;

/**
 * Table model representing ResourceManager model
 * 
 * @author sguerin
 * 
 */
public abstract class ResourceManagerModel extends AbstractTableModel implements GraphicalFlexoObserver
{

    private static final Logger logger = Logger.getLogger(ResourceManagerModel.class.getPackage().getName());

    protected FlexoProject _project;
    protected Vector<FlexoResource> _resources;

    public ResourceManagerModel(FlexoProject project)
    {
        super();
        _project = project;
        _resources = new Vector<FlexoResource>();
        refresh();
        project.addObserver(this);
    }
    
    protected void refresh()
    {
        if (logger.isLoggable(Level.FINE)) {
			logger.fine("refresh() in ResourceManagerModel");
		}
        _resources.clear();
        for (Enumeration en = _project.getResources().elements(); en.hasMoreElements();) {
            FlexoResource resource = (FlexoResource)en.nextElement();
            if (selectResource(resource)) {
				_resources.add(resource);
			}
        }
        fireTableDataChanged();
    }

    public abstract boolean selectResource (FlexoResource resource);
    
    protected void delete()
    {
        _resources.clear();
        _project.deleteObserver(this);
    }

     @Override
	public int getRowCount()
    {
        if (_resources == null) {
            return 0;
        }
        return _resources.size();
    }

    @Override
	public boolean isCellEditable(int rowIndex, int columnIndex)
    {
        return false;
    }

    public FlexoResource resourceAt(int rowIndex)
    {
        if ((_resources != null) && (rowIndex < _resources.size())) {
            return _resources.elementAt(rowIndex);
        }
        return null;
    }

    @Override
	public void setValueAt(Object value, int rowIndex, int columnIndex)
    {
    }

    public abstract String getTitle();
    
    public abstract int getPreferedColumnSize(int col);
    
    protected String modulesRetainingResource(FlexoResource resource)
    {
        String returned = null;
        Enumeration en = ModuleLoader.loadedModules();
        while (en.hasMoreElements()) {
            FlexoModule module = (FlexoModule) en.nextElement();
            if (module.isRetaining(resource)) {
                if (returned == null) {
                    returned = module.getModule().getShortName();
                } else {
                    returned += "," + module.getModule().getShortName();
                }
            }
        }
        if (returned == null) {
            returned = "";
        }
        return returned;
    }

    /**
     * Implements
     * 
     * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
     *      org.openflexo.foundation.DataModification)
     * @see org.openflexo.foundation.FlexoObserver#update(org.openflexo.foundation.FlexoObservable,
     *      org.openflexo.foundation.DataModification)
     */
    @Override
	public void update(FlexoObservable o, DataModification arg)
    {
        refresh();
    }
    
    public static class StorageResourceModel extends ResourceManagerModel
    {
        public StorageResourceModel(FlexoProject project)
        {
            super(project);
        }

        @Override
		public boolean selectResource (FlexoResource resource)
        {
            return (resource instanceof FlexoStorageResource);
        }
        
        @Override
		public String getTitle()
        {
            return FlexoLocalization.localizedForKeyWithParams("storage_resources_registered_for_project_($projectName)", _project);
        }
        
        @Override
		public int getColumnCount()
        {
            return 12;
        }

        public FlexoStorageResource storageResourceAt(int rowIndex)
        {
            return (FlexoStorageResource)resourceAt(rowIndex);
        }
        
        @Override
		public String getColumnName(int columnIndex)
        {
        	if (columnIndex == 0) {
                return "";
            } else if (columnIndex == 1) {
                return FlexoLocalization.localizedForKey("name");
            } else if (columnIndex == 2) {
                return FlexoLocalization.localizedForKey("type");
            } else if (columnIndex == 3) {
                return FlexoLocalization.localizedForKey("is_loaded");
            } else if (columnIndex == 4) {
                return FlexoLocalization.localizedForKey("is_modified");
            } else if (columnIndex == 5) {
                return FlexoLocalization.localizedForKey("retain_by_modules");
            } else if (columnIndex == 6) {
                return FlexoLocalization.localizedForKey("last_memory_update");
            } else if (columnIndex == 7) {
                return FlexoLocalization.localizedForKey("file_name");
            } else if (columnIndex == 8) {
                return FlexoLocalization.localizedForKey("file_format");
            } else if (columnIndex == 9) {
                return FlexoLocalization.localizedForKey("last_saved_on");
            } else if (columnIndex == 10) {
                return FlexoLocalization.localizedForKey("version");
            } else if (columnIndex == 11) {
                return FlexoLocalization.localizedForKey("active");
            }
            return "???";
        }

        @Override
		public int getPreferedColumnSize(int arg0)
        {
            switch (arg0) {
            case 0:
                return 25; // icon
            case 1:
                return 150; // name
            case 2:
                return 150; // type
            case 3:
                return 80; // is_loaded
            case 4:
                return 80; // is_modified
            case 5:
                return 150; // retain_by_modules
            case 6:
                return 120; // last_memory_update
            case 7:
                return 130; // file_name
            case 8:
                return 80; // file_format
            case 9:
                return 120; // last_saved_on
            case 10:
                return 50; // version
            case 11:
                return 50; // active
            default:
                return 50;
            }
        }

     
        @Override
		public Class<?> getColumnClass(int columnIndex)
        {
            if (columnIndex == 0) {
                return Icon.class;
            } else if (columnIndex == 1) {
                return String.class;
            } else if (columnIndex == 2) {
                return String.class;
            } else if (columnIndex == 3) {
                return Boolean.class;
            } else if (columnIndex == 4) {
                return Boolean.class;
            } else if (columnIndex == 5) {
                return String.class;
            } else if (columnIndex == 6) {
                return String.class;
            } else if (columnIndex == 7) {
                return String.class;
            } else if (columnIndex == 8) {
                return String.class;
            } else if (columnIndex == 9) {
                return String.class;
            } else if (columnIndex == 10) {
                return String.class;
            } else if (columnIndex == 11) {
                return Boolean.class;
            }
            return String.class;
        }

        @Override
		public Object getValueAt(int rowIndex, int columnIndex)
        {
            if (_resources == null) {
                return null;
            }
            if (columnIndex == 0) {
                return IconLibrary.getIconForResourceType(resourceAt(rowIndex).getResourceType());
            } else if (columnIndex == 1) {
                return resourceAt(rowIndex).getName();
            } else if (columnIndex == 2) {
                return resourceAt(rowIndex).getResourceType().getName();
            } else if (columnIndex == 3) {
                return new Boolean(storageResourceAt(rowIndex).isLoaded());
            } else if (columnIndex == 4) {
                return new Boolean(storageResourceAt(rowIndex).isModified());
            } else if (columnIndex == 5) {
                return modulesRetainingResource(resourceAt(rowIndex));
            } else if (columnIndex == 6) {
                Date date = storageResourceAt(rowIndex).lastMemoryUpdate();
                if (date != null) {
                   	if (date.getTime() == 0) {
						return "-";
					}
                    return (new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(date);
                } else {
                    return "-";
                }
            } else if (columnIndex == 7) {
                if (resourceAt(rowIndex) instanceof FlexoFileResource) {
                    return ((FlexoFileResource) resourceAt(rowIndex)).getResourceFile().getRelativePath();
                } else {
                    return "-";
                }
            } else if (columnIndex == 8) {
                return resourceAt(rowIndex).getResourceFormat().getIdentifier();
            } else if (columnIndex == 9) {
                if (resourceAt(rowIndex) instanceof FlexoFileResource) {
                    Date date = ((FlexoFileResource) resourceAt(rowIndex)).getDiskLastModifiedDate();
                    if (date != null) {
                       	if (date.getTime() == 0) {
							return "-";
						}
                        return (new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(date);
                    } else {
                        return "-";
                    }
                } else {
                    return "-";
                }
            } else if (columnIndex == 10) {
                if (resourceAt(rowIndex) instanceof FlexoXMLStorageResource) {
                    return ((FlexoXMLStorageResource) resourceAt(rowIndex)).getXmlVersion().toString();
                } else {
                    return "-";
                }
            } else if (columnIndex == 11) {
            	return new Boolean(storageResourceAt(rowIndex).isActive());
            }
            return null;
        }


    }
    
    public static class ImportedResourceModel extends ResourceManagerModel
    {
        public ImportedResourceModel(FlexoProject project)
        {
            super(project);
        }

        @Override
		public boolean selectResource (FlexoResource resource)
        {
            return (resource instanceof FlexoImportedResource);
        }
        
        @Override
		public String getTitle()
        {
            return FlexoLocalization.localizedForKeyWithParams("imported_resources_registered_for_project_($projectName)", _project);
        }
        
        @Override
		public int getColumnCount()
        {
            return 10;
        }

        public FlexoImportedResource importedResourceAt(int rowIndex)
        {
            return (FlexoImportedResource)resourceAt(rowIndex);
        }
        
        @Override
		public String getColumnName(int columnIndex)
        {
        	if (columnIndex == 0) {
                return "";
            } else if (columnIndex == 1) {
                return FlexoLocalization.localizedForKey("name");
            } else if (columnIndex == 2) {
                return FlexoLocalization.localizedForKey("type");
            } else if (columnIndex == 3) {
                return FlexoLocalization.localizedForKey("is_loaded");
            } else if (columnIndex == 4) {
                return FlexoLocalization.localizedForKey("retain_by_modules");
            } else if (columnIndex == 5) {
                return FlexoLocalization.localizedForKey("last_import_date");
            } else if (columnIndex == 6) {
                return FlexoLocalization.localizedForKey("file_name");
            } else if (columnIndex == 7) {
                return FlexoLocalization.localizedForKey("file_format");
            } else if (columnIndex == 8) {
                return FlexoLocalization.localizedForKey("disk_update");
            } else if (columnIndex == 9) {
                return FlexoLocalization.localizedForKey("active");
            } 
            return "???";
        }

        @Override
		public int getPreferedColumnSize(int arg0)
        {
            switch (arg0) {
            case 0:
                return 25; // icon
            case 1:
                return 150; // name
            case 2:
                return 150; // type
            case 3:
                return 80; // is_loaded
            case 4:
                return 150; // retain_by_modules
            case 5:
                return 120; // last_import_date
            case 6:
                return 130; // file_name
            case 7:
                return 80; // file_format
            case 8:
                return 120; // disk_upate
            case 9:
                return 50; // active
           default:
                return 50;
            }
        }

        @Override
		public Class<?> getColumnClass(int columnIndex)
        {
        	if (columnIndex == 0) {
        		return Icon.class;
        	} else if (columnIndex == 1) {
        		return String.class;
        	} else if (columnIndex == 2) {
        		return String.class;
        	} else if (columnIndex == 3) {
        		return Boolean.class;
        	} else if (columnIndex == 4) {
        		return String.class;
        	} else if (columnIndex == 5) {
                return String.class;
            } else if (columnIndex == 6) {
                return String.class;
            } else if (columnIndex == 7) {
                return String.class;
            } else if (columnIndex == 8) {
                return String.class;
            } else if (columnIndex == 9) {
                return Boolean.class;
            }
            return String.class;
        }

        @Override
		public Object getValueAt(int rowIndex, int columnIndex)
        {
            if (_resources == null) {
                return null;
            }
 
            if (columnIndex == 0) {
                return IconLibrary.getIconForResourceType(resourceAt(rowIndex).getResourceType());
            } else if (columnIndex == 1) {
                return resourceAt(rowIndex).getName();
            } else if (columnIndex == 2) {
                return resourceAt(rowIndex).getResourceType().getName();
            } else if (columnIndex == 3) {
                return new Boolean(importedResourceAt(rowIndex).isLoaded());
            } else if (columnIndex == 4) {
                return modulesRetainingResource(resourceAt(rowIndex));
            } else if (columnIndex == 5) {
                Date date = importedResourceAt(rowIndex).getLastImportDate();
                if (date != null) {
                   	if (date.getTime() == 0) {
						return "-";
					}
                    return (new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(date);
                } else {
                    return "-";
                }
            } else if (columnIndex == 6) {
                if (resourceAt(rowIndex) instanceof FlexoFileResource) {
                    return ((FlexoFileResource) resourceAt(rowIndex)).getResourceFile().getRelativePath();
                } else {
                    return "-";
                }
            } else if (columnIndex == 7) {
                return resourceAt(rowIndex).getResourceFormat().getIdentifier();
            } else if (columnIndex == 8) {
                if (resourceAt(rowIndex) instanceof FlexoFileResource) {
                    Date date = ((FlexoFileResource) resourceAt(rowIndex)).getDiskLastModifiedDate();
                    if (date != null) {
                       	if (date.getTime() == 0) {
							return "-";
						}
                       	return (new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(date);
                    } else {
                        return "-";
                    }
                } else {
                    return "-";
                }
            } 
            else if (columnIndex == 9) {
            	return new Boolean(importedResourceAt(rowIndex).isActive());
            }
            return null;
        }


    }
    
    public static class GeneratedResourceModel extends ResourceManagerModel
    {
        public GeneratedResourceModel(FlexoProject project)
        {
            super(project);
        }

        @Override
		public boolean selectResource (FlexoResource resource)
        {
            return (resource instanceof FlexoGeneratedResource);
        }
        
        @Override
		public String getTitle()
        {
            return FlexoLocalization.localizedForKeyWithParams("generated_resources_registered_for_project_($projectName)", _project);
        }
        
        @Override
		public int getColumnCount()
        {
            return 11;
        }

        public FlexoGeneratedResource generatedResourceAt(int rowIndex)
        {
            return (FlexoGeneratedResource)resourceAt(rowIndex);
        }
        
        @Override
		public String getColumnName(int columnIndex)
        {
        	if (columnIndex == 0) {
                return "";
            } else if (columnIndex == 1) {
                return FlexoLocalization.localizedForKey("name");
            } else if (columnIndex == 2) {
                return FlexoLocalization.localizedForKey("type");
            } else if (columnIndex == 3) {
                return FlexoLocalization.localizedForKey("is_loaded");
            } else if (columnIndex == 4) {
                return FlexoLocalization.localizedForKey("is_generated");
            } else if (columnIndex == 5) {
                return FlexoLocalization.localizedForKey("retain_by_modules");
            } else if (columnIndex == 6) {
                return FlexoLocalization.localizedForKey("last_generated_date");
            } else if (columnIndex == 7) {
                return FlexoLocalization.localizedForKey("file_name");
            } else if (columnIndex == 8) {
                return FlexoLocalization.localizedForKey("file_format");
            } else if (columnIndex == 9) {
                return FlexoLocalization.localizedForKey("disk_update");
            } else if (columnIndex == 10) {
                return FlexoLocalization.localizedForKey("active");
            } 
            return "???";
        }

        @Override
		public int getPreferedColumnSize(int arg0)
        {
            switch (arg0) {
            case 0:
                return 25; // icon
            case 1:
                return 150; // name
            case 2:
                return 150; // type
            case 3:
                return 80; // is_loaded
            case 4:
                return 80; // is_generated
            case 5:
                return 150; // retain_by_modules
            case 6:
                return 120; // last_generation_update
            case 7:
                return 130; // file_name
            case 8:
                return 80; // file_format
            case 9:
                return 120; // disk_update
            case 10:
                return 50; // active
            default:
                return 50;
            }
        }
        @Override
		public Class<?> getColumnClass(int columnIndex)
        {
        	if (columnIndex == 0) {
        		return Icon.class;
        	} else if (columnIndex == 1) {
        		return String.class;
        	} else if (columnIndex == 2) {
        		return String.class;
        	} else if (columnIndex == 3) {
        		return Boolean.class;
        	} else if (columnIndex == 4) {
        		return Boolean.class;
        	} else if (columnIndex == 5) {
        		return String.class;
        	} else if (columnIndex == 6) {
        		return String.class;
        	} else if (columnIndex == 7) {
        		return String.class;
        	} else if (columnIndex == 8) {
        		return String.class;
        	} else if (columnIndex == 9) {
        		return String.class;
        	} else if (columnIndex == 10) {
        		return Boolean.class;
           	}
        	return String.class;
        }

        @Override
		public Object getValueAt(int rowIndex, int columnIndex)
        {
        	if (_resources == null) {
        		return null;
        	}
 
        	if (columnIndex == 0) {
                return IconLibrary.getIconForResourceType(resourceAt(rowIndex).getResourceType());
            } else if (columnIndex == 1) {
                return resourceAt(rowIndex).getName();
            } else if (columnIndex == 2) {
                return resourceAt(rowIndex).getResourceType().getName();
            } else if (columnIndex == 3) {
                return new Boolean(generatedResourceAt(rowIndex).isLoaded());
            } else if (columnIndex == 4) {
                return new Boolean(generatedResourceAt(rowIndex).isLoaded());
            } else if (columnIndex == 5) {
                return modulesRetainingResource(resourceAt(rowIndex));
            } else if (columnIndex == 6) {
                Date date = generatedResourceAt(rowIndex).getLastGenerationDate();
                if (date != null) {
                   	if (date.getTime() == 0) {
						return "-";
					}
                   	return (new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(date);
                } else {
                    return "-";
                }
            } else if (columnIndex == 7) {
                if (resourceAt(rowIndex) instanceof FlexoFileResource) {
                    return ((FlexoFileResource) resourceAt(rowIndex)).getResourceFile().getRelativePath();
                } else {
                    return "-";
                }
            } else if (columnIndex == 8) {
                return resourceAt(rowIndex).getResourceFormat().getIdentifier();
            } else if (columnIndex == 9) {
                if (resourceAt(rowIndex) instanceof FlexoFileResource) {
                    Date date = ((FlexoFileResource) resourceAt(rowIndex)).getDiskLastModifiedDate();
                    if (date != null) {
                    	if (date.getTime() == 0) {
							return "-";
						}
                        return (new SimpleDateFormat("dd/MM HH:mm:ss SSS")).format(date);
                    } else {
                        return "-";
                    }
                } else {
                    return "-";
                }
            } else if (columnIndex == 10) {
                return new Boolean(generatedResourceAt(rowIndex).isActive());
            } 
            return null;
        }


    }
    
}
