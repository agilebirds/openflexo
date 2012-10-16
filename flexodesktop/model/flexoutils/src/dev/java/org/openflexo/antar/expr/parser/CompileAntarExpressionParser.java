package org.openflexo.antar.expr.parser;

import java.io.File;

import org.openflexo.toolbox.FileResource;
import org.sablecc.sablecc.SableCC;

public class CompileAntarExpressionParser {

	public static void main(String[] args) {
		File grammar = new FileResource("Grammar/antar_expr.grammar");
		System.out.println("file : " + grammar.getAbsolutePath());
		System.out.println("exist=" + grammar.exists());
		File output = new FileResource("flexoutils/src/main/java");
		try {
			SableCC.processGrammar(grammar, output);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
