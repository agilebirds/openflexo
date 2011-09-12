import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jdom.Document;
import org.jdom.Element;
import org.jdom.output.Format;
import org.jdom.output.XMLOutputter;
import org.openflexo.toolbox.FileUtils;

public class Test {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		String csv = FileUtils.fileContents(new File("D:\\Mes documents\\Pays.csv"), "ISO-8859-1");
		String[] lines = csv.split("\n");
		Document document = new Document();
		Element root = new Element("Domain");
		root.setAttribute("id", "GUI_1059713");
		root.setAttribute("name", "Country");
		root.setAttribute("userID", "GUI");
		root.setAttribute("flexoID", "1059713");
		root.setAttribute("docFormat", "HTML");
		root.setAttribute("dontGenerate", "false");
		root.setAttribute("description", "List of countries");
		root.setAttribute("useSpecificDescriptions", "false");
		document.setRootElement(root);
		long ids = 1060938;
		int index = 1;
		List<Element> keys = new ArrayList<Element>();
		List<Element> enVal = new ArrayList<Element>();
		List<Element> frVal = new ArrayList<Element>();
		List<Element> nlVal = new ArrayList<Element>();
		for (String line : lines) {
			String[] s = line.split(";");
			long id = ids++;
			Element key = new Element("Key");
			String idRef = "GUI_" + id;
			key.setAttribute("id", idRef);
			key.setAttribute("userID", "GUI");
			key.setAttribute("flexoID", String.valueOf(id));
			key.setAttribute("name", s[0]);
			Element indexEl = new Element("index");
			indexEl.setText(String.valueOf(index));
			index++;
			key.addContent(indexEl);
			keys.add(key);

			id = ids++;
			Element en = new Element("Value");
			en.setAttribute("id", "GUI_" + id);
			en.setAttribute("userID", "GUI");
			en.setAttribute("flexoID", String.valueOf(id));
			en.setAttribute("value", cleanValue(s[1]));
			en.addContent(getKeyRef(idRef));
			Element lg = new Element("Language");
			lg.setAttribute("idref", "DOM_388");
			en.addContent(lg);
			enVal.add(en);

			id = ids++;
			Element fr = new Element("Value");
			fr.setAttribute("id", "GUI_" + id);
			fr.setAttribute("userID", "GUI");
			fr.setAttribute("flexoID", String.valueOf(id));
			fr.setAttribute("value", cleanValue(s[2]));
			fr.addContent(getKeyRef(idRef));
			lg = new Element("Language");
			lg.setAttribute("idref", "DOM_133824");
			fr.addContent(lg);
			frVal.add(fr);

			id = ids++;
			Element nl = new Element("Value");
			nl.setAttribute("id", "GUI_" + id);
			nl.setAttribute("userID", "GUI");
			nl.setAttribute("flexoID", String.valueOf(id));
			nl.setAttribute("value", cleanValue(s[3]));
			nl.addContent(getKeyRef(idRef));
			lg = new Element("Language");
			lg.setAttribute("idref", "DOM_55831");
			nl.addContent(lg);
			nlVal.add(nl);
		}
		root.addContent(keys);
		root.addContent(enVal);
		root.addContent(frVal);
		root.addContent(nlVal);
		XMLOutputter outputter = new XMLOutputter(Format.getPrettyFormat());
		outputter.output(document, new FileOutputStream("d:\\Mes Documents\\Pays.xml"));
		System.err.println(ids);
	}

	public static String cleanValue(String value) {
		value = value.trim();
		return value.replaceAll("\\([^)]+\\)", "");
	}

	private static Element getKeyRef(String idRef) {
		Element keyRef = new Element("Key");
		keyRef.setAttribute("idref", idRef);
		return keyRef;
	}

}
