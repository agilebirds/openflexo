package org.openflexo;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;

import org.apache.commons.lang3.reflect.TypeUtils;

@MappedSuperclass
public abstract class DBObject<I> implements Serializable {
	private Date creationDate;
	private Date lastUpdateDate;

	public abstract I getId();

	public abstract void setId(I id);

	public Class<I> getIdClass() {
		return (Class<I>) TypeUtils.getTypeArguments(getClass(), DBObject.class).get(DBObject.class.getTypeParameters()[0]);
	}

	@PrePersist
	public void onCreate() {
		creationDate = new Date();
		lastUpdateDate = creationDate;
	}

	@PreUpdate
	public void onUpdate() {
		lastUpdateDate = new Date();
	}

	public Date getCreationDate() {
		return creationDate;
	}

	public void setCreationDate(Date creationDate) {
		this.creationDate = creationDate;
	}

	public Date getLastUpdateDate() {
		return lastUpdateDate;
	}

	public void setLastUpdateDate(Date lastUpdateDate) {
		this.lastUpdateDate = lastUpdateDate;
	}

}