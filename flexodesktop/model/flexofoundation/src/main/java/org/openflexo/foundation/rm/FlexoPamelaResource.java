package org.openflexo.foundation.rm;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.logging.Level;

import org.apache.commons.io.IOUtils;
import org.jdom2.JDOMException;
import org.openflexo.foundation.utils.FlexoProgress;
import org.openflexo.foundation.utils.FlexoProjectFile;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.foundation.utils.ProjectLoadingHandler;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.model.factory.XMLDeserializer;
import org.openflexo.model.factory.XMLSerializer;
import org.openflexo.model.xml.DefaultStringEncoder;
import org.openflexo.model.xml.InvalidDataException;
import org.openflexo.model.xml.InvalidXMLDataException;
import org.openflexo.xmlcode.StringEncoder.Converter;

public class FlexoPamelaResource<SRD extends StorageResourceData<SRD>> extends FlexoStorageResource<SRD> {

	private static final java.util.logging.Logger logger = org.openflexo.logging.FlexoLogger.getLogger(FlexoPamelaResource.class
			.getPackage().getName());

	private static final class PAMELALoadResourceException extends LoadResourceException {
		private PAMELALoadResourceException(FlexoFileResource<?> fileResource, String message) {
			super(fileResource, message);
		}
	}

	public static final class ConverterAdapter<T> extends DefaultStringEncoder.Converter<T> {

		private final Converter<T> converter;

		public ConverterAdapter(Converter<T> converter) {
			super(converter.getConverterClass());
			this.converter = converter;
		}

		@Override
		public T convertFromString(String value, ModelFactory factory) throws InvalidDataException {
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

	public FlexoPamelaResource(FlexoProject project, String name, ResourceType resourceType, Class<SRD> resourceDataClass,
			FlexoProjectFile file) throws ModelDefinitionException, InvalidFileNameException, DuplicateResourceException {
		this(project);
		this.name = name;
		this.useProjectName = name == null;
		this.resourceType = resourceType;
		this.resourceDataClass = resourceDataClass;
		if (project.getProjectName().equals(name)) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("You are creating a PAMELA resource with the exact same name as the project but it is not synched with it.");
			}
		}
		setResourceFile(file);
		this.modelFactory.importClass(resourceDataClass);
		this._resourceData = modelFactory.newInstance(resourceDataClass);
		this._resourceData.setFlexoResource(this);
	}

	public FlexoPamelaResource(FlexoProject project, ResourceType resourceType, Class<SRD> resourceDataClass, FlexoProjectFile file)
			throws ModelDefinitionException, InvalidFileNameException, DuplicateResourceException {
		this(project);
		this.useProjectName = true;
		this.resourceType = resourceType;
		this.resourceDataClass = resourceDataClass;
		setResourceFile(file);
		this.modelFactory.importClass(resourceDataClass);
		this._resourceData = modelFactory.newInstance(resourceDataClass);
		this._resourceData.setFlexoResource(this);
	}

	public FlexoPamelaResource(FlexoProjectBuilder builder) {
		this(builder.project);
		builder.notifyResourceLoading(this);
	}

	private FlexoPamelaResource(FlexoProject aProject) {
		super(aProject);
		this.modelFactory = new ModelFactory();
		for (Converter<?> c : project.getStringEncoder().getConverters()) {
			modelFactory.addConverter(new ConverterAdapter(c));
		}
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
			modelFactory.importClass(resourceDataClass);
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
		XMLSerializer serializer = new XMLSerializer(modelFactory.getStringEncoder());
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(getFile());
			serializer.serializeDocument(getResourceData(), out);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new SaveResourceException(this);
		} finally {
			IOUtils.closeQuietly(out);
		}
	}

	@Override
	protected boolean isLoadable() {
		return super.isLoadable() && resourceDataClass != null;
	}

	@Override
	protected SRD performLoadResourceData(FlexoProgress progress, ProjectLoadingHandler loadingHandler) throws LoadResourceException,
			FileNotFoundException, ProjectLoadingCancelledException {
		if (!isLoadable()) {
			return null;
		}
		XMLDeserializer deserializer = new XMLDeserializer(modelFactory);
		FileInputStream fis = new FileInputStream(getFile());
		try {
			return (SRD) deserializer.deserializeDocument(fis);
		} catch (IOException e) {
			e.printStackTrace();
			throw new PAMELALoadResourceException(this, "I/O exception: " + e.getMessage());
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new PAMELALoadResourceException(this, "XML exception (JDOM): " + e.getMessage());
		} catch (InvalidXMLDataException e) {
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
