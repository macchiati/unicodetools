package org.unicode.jsptest;

import java.io.IOException;
import java.util.Arrays;

import org.unicode.jsp.ICUPropertyFactory;
import org.unicode.jsp.UnicodeProperty;
import org.unicode.jsp.UnicodeSetUtilities;
import org.unicode.jsp.UnicodeJsp;
import org.unicode.jsp.XPropertyFactory;

import com.google.common.base.Joiner;
import com.ibm.icu.text.UnicodeSet;

public class QuickCheck {
    
    static UnicodeProperty.Factory xfactory = XPropertyFactory.make();
    public static void main(String[] args) throws IOException {
	//   public static void showSet(String grouping, UnicodeSet a, boolean abbreviate, boolean ucdFormat, Appendable out) throws IOException {
	//   public static String getSimpleSet(String setA, UnicodeSet a, boolean abbreviate, boolean escape) {

	//    StringBuilder out = new StringBuilder();
	//    UnicodeSet a = new UnicodeSet();
	//    String a_out = UnicodeJsp.getSimpleSet("[:confusables:]", a, true, true);
	//    System.out.println(a_out);
	//
	//    String outer = UnicodeJsp.getSimpleSet("[:emoji=yes:]", a, false, false);
	//    //UnicodeJsp.showSet("", a, true, false, out);
	//    //String outer = out.toString();
	//    System.out.println(outer);

	
	checkSetProperty("Exemplar_Main", "en");
	checkSetProperty("Script_Extensions", "Cyrl");

//	UnicodeSet t = UnicodeSetUtilities.parseUnicodeSet("\\p{Script_Extensions=Cyrillic}");
//
//	System.out.println(xfactory.getAvailableNames());
//	UnicodeProperty.Factory ifactory = ICUPropertyFactory.make();
//
//	System.out.println(ifactory.getProperty("Script_Extensions").getValue('A'));
//
//	for (String prop : Arrays.asList("Exemplar_Aux", "Exemplar_Index", "Exemplar_Main", "Exemplar_Punctuation")) {
//	    System.out.println(xfactory.getProperty(prop).getValue('A'));
//	}
    }
    
    public static void checkSetProperty(String propName, String propValue) {
	UnicodeProperty p = xfactory.getProperty(propName);
	p.isCollection = true;
	UnicodeProperty p2 = xfactory.getProperty(propName);
	if (!p2.isCollection) {
	    throw new IllegalArgumentException("isCollection didn't stick");
	}
	print("getAvailableValues ", p.getAvailableValues().toString());
	print("getValueAliases ", p.getValueAliases().toString());

	UnicodeSet s = UnicodeSetUtilities.parseUnicodeSet("\\p{" + propName + "=" + propValue + "}");
	System.out.println(s.complement().complement());
    }

    public static void print(Joiner joiner, String... p) {
	System.out.println(joiner.join(p));
    }
    public static void print(String... p) {
	print(Joiner.on('\t'), p);
    }
}
