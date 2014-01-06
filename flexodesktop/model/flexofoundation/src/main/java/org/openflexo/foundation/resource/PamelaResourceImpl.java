package org.openflexo.foundation.resource;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Iterator;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.filter.ElementFilter;
import org.jdom2.input.SAXBuilder;
import org.openflexo.foundation.FlexoService.ServiceNotification;
import org.openflexo.foundation.IOFlexoException;
import org.openflexo.foundation.InconsistentDataException;
import org.openflexo.foundation.InvalidModelDefinitionException;
import org.openflexo.foundation.InvalidXMLException;
import org.openflexo.foundation.utils.ProjectLoadingCancelledException;
import org.openflexo.kvc.AccessorInvocationException;
import org.openflexo.localization.FlexoLocalization;
import org.openflexo.model.exceptions.InvalidDataException;
import org.openflexo.model.exceptions.ModelDefinitionException;
import org.openflexo.model.factory.ModelFactory;
import org.openflexo.toolbox.FileUtils;
import org.openflexo.toolbox.FlexoVersion;
import org.openflexo.toolbox.IProgress;

/**
 * Default implementation for {@link PamelaResource} (a resource where underlying model is managed by PAMELA framework)
 * 
 * @param <RD>
 *            the type of the resource data reference by this resource
 * @author Sylvain
 * 
 */
public abstract class PamelaResourceImpl<RD extends ResourceData<RD>, F extends ModelFactory> extends FlexoFileResourceImpl<RD> implements
		PamelaResource<RD, F> {

	private static final Logger logger = Logger.getLogger(PamelaResourceImpl.class.getPackage().getName());

	private boolean isLoading = false;

	// private boolean isConverting = false;
	// protected boolean performLoadWithPreviousVersion = true;

	/**
	 * Save the &quot;real&quot; resource data of this resource.
	 * 
	 * @throws SaveResourceException
	 */
	@Override
	public final void save(IProgress progress) throws SaveResourceException {
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("saving") + " " + this.getName());
		}
		if (!isLoaded()) {
			return;
		}
		if (!isDeleted) {
			saveResourceData(true);
			resourceData.clearIsModified(false);
		}

	}

	/**
	 * Load resource data by applying a special scheme handling XML versionning, ie to find right XML version of current resource file.<br>
	 * If version of stored file is not conform to latest declared version, convert resource file and update it to latest version.
	 * 
	 * @throws ProjectLoadingCancelledException
	 * @throws MalformedXMLException
	 * 
	 * @see org.openflexo.foundation.rm.FlexoResource#loadResourceData()
	 */
	@Override
	public RD loadResourceData(IProgress progress) throws FlexoFileNotFoundException, IOFlexoException, InvalidXMLException,
			InconsistentDataException, InvalidModelDefinitionException {
		if (resourceData != null) {
			// already loaded
			return resourceData;
		}

		/*if (getXMLSerializationService() == null) {
			throw new FlexoException("XMLSerializationService not registered");
		}*/

		isLoading = true;
		if (progress != null) {
			progress.setProgress(FlexoLocalization.localizedForKey("loading") + " " + this.getName());
			progress.resetSecondaryProgress(4);
			progress.setProgress(FlexoLocalization.localizedForKey("loading_from_disk"));
		}

		LoadResourceException exception = null;

		if (logger.isLoggable(Level.FINE)) {
			logger.fine("Load resource data for " + this);
		}
		if (!getFile().exists()) {
			recoverFile();
			if (!getFile().exists()) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.severe("File " + getFile().getAbsolutePath() + " does not exist, throwing exception now!");
				}
				throw new FlexoFileNotFoundException(this);
			}
		}

		try {

			FileInputStream fis = new FileInputStream(getFile());
			resourceData = (RD) getFactory().deserialize(fis);

			isLoading = false;
			resourceData.setResource(this);
			return resourceData;

		} catch (IOException e) {
			e.printStackTrace();
			throw new IOFlexoException(e);
		} catch (JDOMException e) {
			e.printStackTrace();
			throw new InvalidXMLException(e);
		} catch (InvalidDataException e) {
			e.printStackTrace();
			throw new InconsistentDataException(e);
		} catch (ModelDefinitionException e) {
			e.printStackTrace();
			throw new InvalidModelDefinitionException(e);
		} finally {
			isLoading = false;
			// isConverting = false;
		}
	}

	/**
	 * Save current resource data to current XML resource file.<br>
	 * Forces XML version to be the latest one.
	 * 
	 * @return
	 */
	protected void saveResourceData(boolean clearIsModified) throws SaveResourceException, SaveResourcePermissionDeniedException {
		System.out.println("Saving " + getFile());
		if (!hasWritePermission()) {
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Permission denied : " + getFile().getAbsolutePath());
			}
			throw new SaveResourcePermissionDeniedException(this);
		}
		if (resourceData != null) {
			logger.warning("I think the SerializationHandler is no more necessary");
			_saveResourceData(/*new SerializationHandler() {
								@Override
								public void objectWillBeSerialized(XMLSerializable object) {
								if (object instanceof TestModelObject) {
								// ((FlexoModelObject) object).initializeSerialization();
								}
								}

								@Override
								public void objectHasBeenSerialized(XMLSerializable object) {
								if (object instanceof TestModelObject) {
								// ((FlexoModelObject) object).finalizeSerialization();
								}
								}
								},*/clearIsModified);
			if (logger.isLoggable(Level.INFO)) {
				logger.info("Succeeding to save Resource " + this + " : " + getFile().getName() + " version=" + getModelVersion()
						+ " with date " + FileUtils.getDiskLastModifiedDate(getFile()));
			}
		}
		if (clearIsModified) {
			try {
				getResourceData(null).clearIsModified(false);// No need to reset the last memory update since it is valid
				notifyResourceSaved();
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}

	public static class WillWriteFileOnDiskNotification implements ServiceNotification {
		private final File file;

		public WillWriteFileOnDiskNotification(File file) {
			this.file = file;
		}

		public File getFile() {
			return file;
		}
	}

	private void _saveResourceData(/*SerializationHandler handler,*/boolean clearIsModified) throws SaveResourceException {
		File temporaryFile = null;
		FileWritingLock lock = willWriteOnDisk();

		if (logger.isLoggable(Level.INFO)) {
			logger.info("Saving resource " + this + " : " + getFile() + " version=" + getModelVersion());
		}

		try {
			File dir = getFile().getParentFile();
			if (!dir.exists()) {
				getServiceManager().notify(null, new WillWriteFileOnDiskNotification(dir));
				dir.mkdirs();
			}
			getServiceManager().notify(null, new WillWriteFileOnDiskNotification(getFile()));
			// Make local copy
			makeLocalCopy();
			// Using temporary file
			temporaryFile = File.createTempFile("temp", ".xml", dir);
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("Creating temp file " + temporaryFile.getAbsolutePath());
			}
			// try {
			performXMLSerialization(/*handler,*/temporaryFile);
			// Renaming temporary file
			if (logger.isLoggable(Level.FINE)) {
				logger.finer("Renaming temp file " + temporaryFile.getAbsolutePath() + " to " + getFile().getAbsolutePath());
			}
			// temporaryFile.renameTo(getFile());
			postXMLSerialization(temporaryFile, lock, clearIsModified);
			/*} catch (DuplicateSerializationIdentifierException e) {
				if (logger.isLoggable(Level.SEVERE)) {
					logger.log(Level.SEVERE, "Duplicate serialization identifier: " + e.getMessage(), e);
				}
				hasWrittenOnDisk(lock);
				//((FlexoXMLSerializable) resourceData).finalizeSerialization();
				throw new SaveXMLResourceException(this, e, version);
			}*/
		} catch (IOException e) {
			e.printStackTrace();
			if (temporaryFile != null) {
				temporaryFile.delete();
			}
			if (logger.isLoggable(Level.WARNING)) {
				logger.warning("Failed to save resource " + this + " with model version " + getModelVersion());
			}
			hasWrittenOnDisk(lock);
			// ((FlexoXMLSerializable) resourceData).finalizeSerialization();
			// throw new SaveXMLResourceException(this, e, version);
		}
	}

	/**
	 * @param version
	 * @param temporaryFile
	 * @param lock
	 * @param clearIsModified
	 * @throws IOException
	 */
	private void postXMLSerialization(File temporaryFile, FileWritingLock lock, boolean clearIsModified) throws IOException {
		FileUtils.rename(temporaryFile, getFile());
		hasWrittenOnDisk(lock);
		// ((FlexoXMLSerializable) resourceData).finalizeSerialization();
		// setModelVersion(version);
		if (clearIsModified) {
			notifyResourceStatusChanged();
		}
	}

	/**
	 * @param version
	 * @param handler
	 * @param temporaryFile
	 * @throws InvalidObjectSpecificationException
	 * @throws InvalidModelException
	 * @throws AccessorInvocationException
	 * @throws DuplicateSerializationIdentifierException
	 * @throws IOException
	 */
	private void performXMLSerialization(/*SerializationHandler handler,*/File temporaryFile) throws IOException {
		FileOutputStream out = null;
		try {
			out = new FileOutputStream(temporaryFile);
			getFactory().serialize(resourceData, out);
			out.flush();
			out.close();
			out = null;
		} finally {
			if (out != null) {
				out.close();
			}
			out = null;
		}
	}

	/*private StringEncoder STRING_ENCODER = null;

	@Override
	public StringEncoder getStringEncoder() {
		if (STRING_ENCODER == null) {
			if (this instanceof FlexoProjectResource) {
				STRING_ENCODER = new StringEncoder(super.getStringEncoder(), ((FlexoProjectResource) this).getProject()
						.getObjectReferenceConverter());
			} else {
				STRING_ENCODER = super.getStringEncoder();
			}
		}
		return STRING_ENCODER;
	}*/

	public void recoverFile() {
		if (getFile() == null) {
			return;
		}
		if (getFile().exists()) {
			return;
		}
		if (getFile().getParentFile().exists()) {
			File[] files = getFile().getParentFile().listFiles();
			for (int i = 0; i < files.length; i++) {
				File file = files[i];
				if (file.getName().equalsIgnoreCase(getFile().getName())) {
					if (logger.isLoggable(Level.WARNING)) {
						logger.warning("Found file " + file.getAbsolutePath() + ". Using it and repairing project as well!");
					}
					setFile(file);
					break;
				}
			}
		}
	}

	/**
	 * Manually converts resource file from version v1 to version v2. This methods only warns and does nothing, and must be overriden in
	 * subclasses !
	 * 
	 * @param v1
	 * @param v2
	 * @return boolean indicating if conversion was sucessfull
	 */
	protected boolean convertResourceFileFromVersionToVersion(FlexoVersion v1, FlexoVersion v2) {
		if (logger.isLoggable(Level.WARNING)) {
			logger.warning("Unable to find converter for resource " + this + " from version " + v1 + " to version " + v2);
		}
		return false;
	}

	private void makeLocalCopy() throws IOException {
		if (getFile() != null && getFile().exists()) {
			String localCopyName = getFile().getName() + "~";
			File localCopy = new File(getFile().getParentFile(), localCopyName);
			FileUtils.copyFileToFile(getFile(), localCopy);

		}
	}

	private boolean lastUniqueIDHasBeenSet = false;
	private long lastID;

	public boolean lastUniqueIDHasBeenSet() {
		return lastUniqueIDHasBeenSet;
	}

	@Override
	public long getNewFlexoID() {
		if (lastID < 0) {
			return -1;
		}
		return ++lastID;
	}

	/**
	 * @return Returns the lastUniqueID.
	 */
	public long getLastID() {
		if (lastUniqueIDHasBeenSet && lastID < 0) {
			lastID = 0;
		}
		return lastID;
	}

	/**
	 * @param lastUniqueID
	 *            The lastUniqueID to set.
	 */
	@Override
	public void setLastID(long lastUniqueID) {
		System.out.println(">>>>>>>>>> setLastID with " + lastUniqueID);
		if (lastUniqueID > lastID) {
			lastID = lastUniqueID;
			lastUniqueIDHasBeenSet = true;
			System.out.println("Resource " + this + " lastID is now " + lastID);
		}
	}

	public static Document readXMLFile(File f) throws JDOMException, IOException {
		FileInputStream fio = new FileInputStream(f);
		SAXBuilder parser = new SAXBuilder();
		Document reply = parser.build(fio);
		return reply;
	}

	public static Element getElement(Document document, String name) {
		Iterator it = document.getDescendants(new ElementFilter(name));
		if (it.hasNext()) {
			return (Element) it.next();
		} else {
			return null;
		}
	}

	public static Element getElement(Element from, String name) {
		Iterator it = from.getDescendants(new ElementFilter(name));
		if (it.hasNext()) {
			return (Element) it.next();
		} else {
			return null;
		}
	}

}
