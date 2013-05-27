package org.openflexo.foundation.rm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.jdom2.JDOMException;
import org.openflexo.foundation.FlexoServiceManager;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.model.ModelContextLibrary;
import org.openflexo.model.StringConverterLibrary;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.xmlcode.StringEncoder.Converter;

public class FlexoPamelaResource<SRD extends StorageResourceData<SRD>> extends FlexoStorageResource<SRD> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(FlexoPamelaResource.class
			.getPackage().getName());

	private static final class PAMELALoadResourceException extends LoadResourceException {
		private PAMELALoadResourceException(FlexoFileResource<?> fileResource, String message) {
			super(fileResource, message);
		}
	}

	public static final class ConverterAdapter<T> extends StringConverterLibrary.Converter<T> {

		private final Converter<T> converter;

		public ConverterAdapter(Converter<T> converter) {
			super(converter.getConverterClass());
			this.converter = converter;
		}

		@Override
		public T convertFromString(String value, ModelFactory factory) throws InvalidDataException {
			// Watch out here: converter is of the old type of XMLCode converter, but 'this' is an instance of a PAMELA converter
			return converter.convertFromString(value);
		}

		@Override
		public String convertToString(T value) {
			return converter.convertToString(value);
		}

	}

	// This flag indicates that the name of the resource is the same as the name of the project.
	private boolean useProjectName;

	private String name;
	private ResourceType resourceType;
	private Class<SRD> resourceDataClass;

	private ModelFactory modelFactory;

	public FlexoPamelaResource(FlexoProject project, FlexoServiceManager serviceManager, String name, ResourceType resourceType,
			Class<SRD> resourceDataClass, FlexoProjectFile file) throws ModelDefinitionException, InvalidFileNameException,
			DuplicateResourceException {
		this(project, serviceManager);
		this.name = name;
		this.useProjectName = name == null;
		this.resourceType = resourceType;
		if (project.getProjectName().equals(name)) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("You are creating a PAMELA resource with the exact same name as the project but it is not synched with it.");
			}
		}
		setResourceDataClass(resourceDataClass);
		setResourceFile(file);
		this._resourceData = modelFactory.newInstance(resourceDataClass);
		this._resourceData.setFlexoResource(this);
	}

	public FlexoPamelaResource(FlexoProject project, FlexoServiceManager serviceManager, ResourceType resourceType,
			Class<SRD> resourceDataClass, FlexoProjectFile file) throws ModelDefinitionException, InvalidFileNameException,
			DuplicateResourceException {
		this(project, serviceManager);
		this.useProjectName = true;
		this.resourceType = resourceType;
		setResourceDataClass(resourceDataClass);
		setResourceFile(file);
		this._resourceData = modelFactory.newInstance(resourceDataClass);
		this._resourceData.setFlexoResource(this);
	}

	public FlexoPamelaResource(FlexoProjectBuilder builder) {
		this(builder.project, builder.serviceManager);
		builder.notifyResourceLoading(this);
	}

	private FlexoPamelaResource(FlexoProject aProject, FlexoServiceManager serviceManager) {
		super(aProject, serviceManager);
	}

	public ModelFactory getModelFactory() {
		return modelFactory;
	}

	@Override
	public Class<SRD> getResourceDataClass() {
		return resourceDataClass;
	}

	public void setResourceDataClass(Class<SRD> resourceDataClass) throws ModelDefinitionException {
		this.resourceDataClass = resourceDataClass;
		if (resourceDataClass != null) {
			this.modelFactory = new ModelFactory(ModelContextLibrary.getModelContext(resourceDataClass));
			for (Converter<?> c : project.getStringEncoder().getConverters()) {
				modelFactory.addConverter(new ConverterAdapter(c));
			}
		}
	}

	@Override
	public ResourceType getResourceType() {
		if (resourceType == null) {
			return ResourceType.PAMELA_RESOURCE;
		}
		return resourceType;
	}

	public void setResourceType(ResourceType resourceType) {
		this.resourceType = resourceType;
	}

	@Override
	public String getName() {
		if (isUseProjectName()) {
			return getProject().getName();
		}
		return name;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	public boolean isUseProjectName() {
		return useProjectName;
	}

	public void setUseProjectName(boolean useProjectName) {
		this.useProjectName = useProjectName;
		if (useProjectName) {
			name = null;
		}
	}

	@Override
	protected void saveResourceData(boolean clearIsModified) throws SaveResourceException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(getFile());
			modelFactory.serialize(getResourceData(), out);
		} catch (IOException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	@Override
	public boolean isLoadable() {
		return super.isLoadable() && resourceDataClass != null;
	}

	@Override
	protected SRD performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws LoadResourceException,
			FileNotFoundException, ProjectLoadingCancelledException {
		if (!isLoadable()) {
			return null;
		}
		FileInputStream fis = new FileInputStream(getFile());
		try {
			return (SRD) modelFactory.deserialize(fis);
		} catch (IOException e) {
			e.printStackTrace();
			throw new PAMELALoadResourceException(this, "I/O exception: " + e.getMessage());
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new PAMELALoadResourceException(this, "XML exception (JDOM): " + e.getMessage());
		} catch (InvalidDataException e) {
			e.printStackTrace();
			throw new PAMELALoadResourceException(this, "XML exception (Invalid XML): " + e.getMessage());
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			throw new PAMELALoadResourceException(this, "XML exception (Invalid model): " + e.getMessage());
		} finally {
			IOUtils.closeQuietly(fis);
		}
	}

}
