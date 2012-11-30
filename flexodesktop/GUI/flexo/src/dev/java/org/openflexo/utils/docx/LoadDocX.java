package org.openflexo.utils.docx;

import org.docx4j.openpackaging.exceptions.Docx4JException;
import org.docx4j.openpackaging.packages.WordprocessingMLPackage;

public class LoadDocX {

	public static void main(String[] args) throws Docx4JException {
		WordprocessingMLPackage wordMLPackage = WordprocessingMLPackage.load(new java.io.File(
				"/Users/sylvain/tmp/OnSenFout/Documents/SEPELDocumentationTemplates.docx"));
	}
}
