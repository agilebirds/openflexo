package org.openflexo.foundation.viewpoint;

import java.util.HashMap;
import java.util.StringTokenizer;

import org.openflexo.toolbox.StringUtils;

public class FMLRepresentationContext {

	private static int INDENTATION = 2;
	// private int currentIndentation = 0;
	private final HashMap<String, NamedViewPointObject> nameSpaces;

	public FMLRepresentationContext() {
		// currentIndentation = 0;
		nameSpaces = new HashMap<String, NamedViewPointObject>();
	}

	public void addToNameSpaces(NamedViewPointObject object) {
		nameSpaces.put(object.getURI(), object);
	}

	/*public int getCurrentIndentation() {
		return currentIndentation;
	}*/

	public FMLRepresentationContext makeSubContext() {
		FMLRepresentationContext returned = new FMLRepresentationContext();
		for (String uri : nameSpaces.keySet()) {
			returned.nameSpaces.put(uri, nameSpaces.get(uri));
		}
		// returned.currentIndentation = currentIndentation + 1;
		return returned;
	}

	public static class FMLRepresentationOutput {

		StringBuffer sb;

		public FMLRepresentationOutput(FMLRepresentationContext aContext) {
			sb = new StringBuffer();
		}

		public void append(String s, FMLRepresentationContext context) {
			append(s, context, 0);
		}

		public void appendnl() {
			sb.append(StringUtils.LINE_SEPARATOR);
		}

		public void append(String s, FMLRepresentationContext context, int indentation) {
			if (s == null) {
				return;
			}
			StringTokenizer st = new StringTokenizer(s, StringUtils.LINE_SEPARATOR, true);
			while (st.hasMoreTokens()) {
				String l = st.nextToken();
				sb.append(StringUtils.buildWhiteSpaceIndentation((indentation) * INDENTATION) + l);
			}

			/*if (s.equals(StringUtils.LINE_SEPARATOR)) {
				appendnl();
				return;
			}

			BufferedReader rdr = new BufferedReader(new StringReader(s));
			boolean isFirst = true;
			for (;;) {
				String line = null;
				try {
					line = rdr.readLine();
				} catch (IOException e) {
					e.printStackTrace();
				}
				if (line == null) {
					break;
				}
				if (!isFirst) {
					sb.append(StringUtils.LINE_SEPARATOR);
				}
				sb.append(StringUtils.buildWhiteSpaceIndentation((indentation) * INDENTATION) + line);
				isFirst = false;
			}*/

		}

		/*public void append(ViewPointObject o) {
			FMLRepresentationContext subContext = context.makeSubContext();
			String lr = o.getFMLRepresentation(subContext);
			for (int i = 0; i < StringUtils.linesNb(lr); i++) {
				String l = StringUtils.extractStringFromLine(lr, i);
				sb.append(StringUtils.buildWhiteSpaceIndentation(subContext.indentation * 2 + 2) + l);
			}
		}*/

		@Override
		public String toString() {
			return sb.toString();
		}
	}
}