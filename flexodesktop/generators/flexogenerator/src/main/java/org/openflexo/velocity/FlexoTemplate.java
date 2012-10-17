package org.openflexo.velocity;

import org.apache.velocity.Template;

public class FlexoTemplate extends Template {

	private long size = -1;

	public long getSize() {
		return size;
	}

	public void setSize(long size) {
		this.size = size;
	}
}
