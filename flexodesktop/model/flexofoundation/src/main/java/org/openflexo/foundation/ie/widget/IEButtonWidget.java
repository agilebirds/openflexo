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

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.Serializable;
import java.util.logging.Level;
import java.util.logging.Logger;


import org.openflexo.foundation.Inspectors;
import org.openflexo.foundation.ie.IEObject;
import org.openflexo.foundation.ie.IEWOComponent;
import org.openflexo.foundation.ie.dm.IEDataModification;
import org.openflexo.foundation.ie.util.HyperlinkType;
import org.openflexo.foundation.rm.FlexoProject;
import org.openflexo.foundation.rm.FlexoProject.ImageFile;
import org.openflexo.foundation.validation.Validable;
import org.openflexo.foundation.validation.ValidationIssue;
import org.openflexo.foundation.validation.ValidationRule;
import org.openflexo.foundation.validation.ValidationWarning;
import org.openflexo.foundation.xml.FlexoComponentBuilder;
import org.openflexo.logging.FlexoLogger;
import org.openflexo.toolbox.ImageInfo;

/**
 * Represents a button
 *
 * @author bmangez
 */
public class IEButtonWidget extends IEHyperlinkWidget implements Indexable, Serializable, IButton
{
	/**
     *
     */
    public static final String BUTTON_WIDGET = "button_widget";

    private static final Logger logger = FlexoLogger.getLogger(IEButtonWidget.class.getPackage().getName());

    private ImageFile file;

    private boolean maintainAspectRatio = true;
    private boolean usePercentage = false;

    private int widthPercentage=100;
    private int heightPercentage=100;
    private int widthPixel=-1;
    private int heightPixel=-1;

    public IEButtonWidget(FlexoComponentBuilder builder)
    {
        this(builder.woComponent, null, builder.getProject());
        initializeDeserialization(builder);
    }

    public IEButtonWidget(IEWOComponent woComponent, IEObject parent, FlexoProject prj)
    {
        super(woComponent, parent, prj);
    }

    @Override
	public String getDefaultInspectorName()
    {
        return Inspectors.IE.BUTTON_INSPECTOR;
    }

    public int getSmallButtonIndex()
    {
        return ((ButtonContainerInterface) getParent()).getButtonIndex(this);
    }

    /**
     *
     * @return
     * @deprecated use getName()
     */
    @Deprecated
	public String getButtonName()
    {
        return getName();
    }

    /**
     *
     * @param name
     * @deprecated use setName();
     */
    @Deprecated
	public void setButtonName(String name)
    {
        setName(name);
    }

    @Override
	public String getFullyQualifiedName()
    {
        return "Button" + getButtonName();
    }

	public boolean isImportedImage() {
		return getFile()!=null && getFile().isImported();
	}

    @Override
	public String getBeautifiedName()
    {
        String s = "";
        s = getLabel();
        if (s == null || s.trim().length() == 0)
            s = getName();
        if ((s == null || s.trim().length() == 0) && getFile()!=null && getFile().getImageName() != null)
            s = getFile().getBeautifiedImageName();
        return s;
    }

    public ImageFile getFile()
    {
    	if (file==null && getProject()!=null && !isDeserializing())
    		return getProject().getDefaultImageFile();
        return file;
    }

    public void setFile(ImageFile f)
    {
        Object old = this.file;
        this.file = f;
        imageInformation = null;
        if (f != null) {
            setWidthPercentage(widthPercentage);
           	setWidthPixel(getImageInformation().getWidth());
           	setHeightPixel(getImageInformation().getHeight());
        }
        setChanged();
        notifyObservers(new IEDataModification("file", old, f));
    }

    public String getImageName() {
    	if (getFile()!=null)
    		return getFile().getImageName();
    	return null;
    }

    /**
     * Overrides getClassNameKey
     *
     * @see org.openflexo.foundation.FlexoModelObject#getClassNameKey()
     */
    @Override
	public String getClassNameKey()
    {
        return BUTTON_WIDGET;
    }

	/********************************************************
	 * WARNING: The code hereunder can be quite sensitive and
	 * should be modified with great care. There are several
	 * different cases that are quite tricky. Make sure to
	 * test a lot your modification and verify the various
	 * cases that can happen:
	 *  - aspect ratio
	 *  - using percentage
	 *  - width/height pixel/percentage
	 ********************************************************/

	private ImageInfo imageInformation;

	public ImageInfo getImageInformation() {
		if (imageInformation==null) {
			ImageInfo ii = new ImageInfo();
			FileInputStream fis = null;
			if (getFile() != null) {
				try {
					ii.setInput(fis = new FileInputStream(getFile().getImageFile()));
					ii.check();
					imageInformation = ii;
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} finally {
					if (fis != null)
						try {
							fis.close();
						} catch (IOException e) {
							e.printStackTrace();
						}
				}
			} else {
				if (logger.isLoggable(Level.WARNING))
					logger.warning("This button has no file");
			}
		}
		return imageInformation;
	}

	public boolean getMaintainAspectRatio() {
		return maintainAspectRatio;
	}

	public void setMaintainAspectRatio(boolean maintainAspectRatio) {
		boolean old = this.maintainAspectRatio;
		this.maintainAspectRatio = maintainAspectRatio;
		if (!isDeserializing() && !isCreatedByCloning() && old!=maintainAspectRatio && maintainAspectRatio) {
			setWidthPixel(getWidthPixel());
			setWidthPercentage(getWidthPercentage());
		}
		setChanged();
		notifyObservers(new IEDataModification("maintainAspectRatio",null,maintainAspectRatio));
	}

	public boolean getUsePercentage() {
		return usePercentage;
	}

	public void setUsePercentage(boolean usePercentage) {
		/*if (usePercentage!=this.usePercentage && !isDeserializing() && !isCreatedByCloning() && getImageInformation()!=null) {
			ImageInfo ii = getImageInformation();
			if (usePercentage) {
				widthPercentage = Math.round(100*(float)widthPixel/ii.getWidth());
				heightPercentage = Math.round(100*(float)heightPixel/ii.getHeight());
			} else {
				widthPixel = Math.round((float)widthPercentage * ii.getWidth()/100);
				heightPixel = Math.round((float)heightPercentage * ii.getHeight()/100);
			}
		}*/
		if (usePercentage)// If we use percentage, we better force to maintain aspect ratio because browsers don't render them the same way.
			setMaintainAspectRatio(true);
		this.usePercentage = usePercentage;
		setChanged();
		notifyObservers(new IEDataModification("usePercentage",null,usePercentage));
	}

	public int getWidthPercentage() {
		/*if (widthPercentage<0 && getImageInformation()!=null) {
			widthPercentage = getWidthPixel()/getImageInformation().getWidth()*100;
		}*/
		return widthPercentage;
	}

	public void setWidthPercentage(int widthPercentage) {
		this.widthPercentage = widthPercentage;
		if (!isCreatedByCloning() && !isDeserializing()) {
			if (maintainAspectRatio) {
				this.heightPercentage = widthPercentage;
				setChanged();
				notifyObservers(new IEDataModification("heightPercentage",null,heightPercentage));
			}
			/*if (getImageInformation()!=null) {
				this.widthPixel = widthPercentage*getImageInformation().getWidth()/100;
				setChanged();
				notifyObservers(new IEDataModification("widthPixel",null,widthPixel));
				if (maintainAspectRatio) {
					this.heightPixel = heightPercentage*getImageInformation().getHeight()/100;
					setChanged();
					notifyObservers(new IEDataModification("heightPixel",null,heightPixel));
				}
			}*/
			setChanged();
			notifyObservers(new IEDataModification("widthPercentage",null,widthPercentage));
		}
	}

	public int getHeightPercentage() {
		/*if (heightPercentage<0 && getImageInformation()!=null) {
			heightPercentage = getHeightPixel()/getImageInformation().getHeight()*100;
		}*/
		return heightPercentage;
	}

	public void setHeightPercentage(int heightPercentage) {
		this.heightPercentage = heightPercentage;
		if (!isCreatedByCloning() && !isDeserializing()) {
			if (maintainAspectRatio) {
				this.widthPercentage = heightPercentage;
				setChanged();
				notifyObservers(new IEDataModification("widthPercentage",null,widthPercentage));
			}
			if (getImageInformation()!=null) {
				this.heightPixel = heightPercentage*getImageInformation().getHeight()/100;
				setChanged();
				notifyObservers(new IEDataModification("heightPixel",null,heightPixel));
				if (maintainAspectRatio) {
					this.widthPixel = widthPercentage*getImageInformation().getWidth()/100;
					setChanged();
					notifyObservers(new IEDataModification("widthPixel",null,widthPixel));
				}
			}
			setChanged();
			notifyObservers(new IEDataModification("heightPercentage",null,heightPercentage));
		}
	}

	public int getWidthPixel() {
		if (widthPixel<0 && getFile()!=null && getFile().exists() && getImageInformation()!=null) {
			ImageInfo ii = getImageInformation();
			widthPixel = ii.getWidth();
			heightPixel = ii.getHeight();
		}
		return widthPixel;
	}

	public void setWidthPixel(int widthPixel) {
		this.widthPixel = widthPixel;
		if (!isCreatedByCloning() && !isDeserializing()) {
			if (getFile() != null) {
				if (maintainAspectRatio) {
					if (getImageInformation() != null) {
						this.heightPixel = (widthPixel * getImageInformation().getHeight()) / getImageInformation().getWidth();
						setChanged();
						notifyObservers(new IEDataModification("heightPixel", null, heightPixel));
					}
				}
/*				if (getImageInformation() != null) {
					this.widthPercentage = (widthPixel * 100 / getImageInformation().getWidth());
					setChanged();
					notifyObservers(new IEDataModification("widthPercentage", null, widthPercentage));
					if (maintainAspectRatio) {
						this.heightPercentage = (heightPixel * 100 / getImageInformation().getHeight());
						setChanged();
						notifyObservers(new IEDataModification("heightPercentage", null, heightPercentage));
					}
				}*/
			}
			setChanged();
			notifyObservers(new IEDataModification("widthPixel",null,widthPixel));
		}
	}

	public int getHeightPixel() {
		if (heightPixel<0 && getFile()!=null && getFile().exists() && getImageInformation()!=null) {
			ImageInfo ii = getImageInformation();
			heightPixel = ii.getHeight();
			widthPixel = ii.getWidth();
		}
		return heightPixel;
	}

	public void setHeightPixel(int heightPixel) {
		this.heightPixel = heightPixel;
		if (!isCreatedByCloning() && !isDeserializing()) {
			if (getFile() != null) {
				if (maintainAspectRatio) {
					if (getImageInformation() != null) {
						this.widthPixel = (heightPixel * getImageInformation().getWidth()) / getImageInformation().getHeight();
						setChanged();
						notifyObservers(new IEDataModification("widthPixel", null, widthPixel));
					}
				}
				/*if (getImageInformation() != null) {
					this.heightPercentage = (heightPixel * 100 / getImageInformation().getHeight());
					setChanged();
					notifyObservers(new IEDataModification("heightPercentage", null, heightPercentage));
					if (maintainAspectRatio) {
						this.widthPercentage = (widthPixel * 100 / getImageInformation().getWidth());
						setChanged();
						notifyObservers(new IEDataModification("widthPercentage", null, widthPercentage));
					}
				}*/
			}
			setChanged();
			notifyObservers(new IEDataModification("heightPixel",null,heightPixel));
		}
	}

	public boolean isOriginalSize() {
		if (getImageInformation()!=null) {
			return getImageInformation().getWidth()==getWidthPixel() && getImageInformation().getHeight()==getHeightPixel();
		}
		return false;
	}

	/**
	 * Validation
	 */

    public static class EmailButtonMustBeOfTypeEmail extends ValidationRule<EmailButtonMustBeOfTypeEmail, IEButtonWidget>
    {
        public EmailButtonMustBeOfTypeEmail()
        {
            super(IEButtonWidget.class, "this_kind_of_button_is_usually_a_mailto_link");
        }

        @Override
		public ValidationIssue applyValidation(IEButtonWidget object)
        {
        	IEButtonWidget button = object;
            if (button.getFile() != null && button.getFile().getImageFile() != null && button.getFile().getImageName().toUpperCase().indexOf("EMAIL") > -1
                    && (button.getHyperlinkType() == null || button.getHyperlinkType() != HyperlinkType.MAILTO)) {
                ValidationWarning warning = new ValidationWarning<EmailButtonMustBeOfTypeEmail, IEButtonWidget>(this, object, "this_kind_of_button_is_usually_a_mailto_link");

                warning.addToFixProposals(new SetLinkTypeMailto(button));

                return warning;
            }
            return null;
        }

    }

    public static class SearchButtonMustBeOfTypeSearch extends ValidationRule
    {
        public SearchButtonMustBeOfTypeSearch()
        {
            super(IEButtonWidget.class, "this_kind_of_button_is_usually_a_search_button");
        }

        @Override
		public ValidationIssue applyValidation(final Validable object)
        {
            final IEButtonWidget button = (IEButtonWidget) object;
            if (button.getFile() != null && button.getFile().getImageFile() != null && button.getFile().getImageName().toUpperCase().indexOf("PREVIEWFILE") > -1
                    && button.isInSearchArea() && (button.getHyperlinkType() == null || button.getHyperlinkType() != HyperlinkType.SEARCH)) {
                ValidationWarning warning = new ValidationWarning(this, object, "this_kind_of_button_is_usually_a_search_button");

                warning.addToFixProposals(new SetLinkTypeSearch(button));

                return warning;
            }
            return null;
        }

    }

}