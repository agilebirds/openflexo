package org.openflexo.localization;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;

public class JavaPropertiesOutputStream extends OutputStream {
	private final FileOutputStream fos;
	boolean isOnNewLine = true;
	boolean ignoreNextChars = false;

	public JavaPropertiesOutputStream(FileOutputStream fos) {
		this.fos = fos;
	}

	@Override
	public void write(int b) throws IOException {
		if (isOnNewLine) {
			ignoreNextChars = b == '#';
			isOnNewLine = false;
		}
		if (b == '\n') {
			isOnNewLine = true;
		}
		if (!ignoreNextChars) {
			fos.write(b);
		}
	}

	@Override
	public void close() throws IOException {
		super.close();
		fos.close();
	}
}