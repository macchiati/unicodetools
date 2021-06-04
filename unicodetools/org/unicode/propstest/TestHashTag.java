package org.unicode.propstest;

import org.unicode.cldr.util.props.UnicodePropertySymbolTable;
import org.unicode.props.IndexUnicodeProperties;

import com.ibm.icu.text.UnicodeSet;

public class TestHashTag {
    static IndexUnicodeProperties iup = IndexUnicodeProperties.make();
    public static void main(String[] args) {
	UnicodePropertySymbolTable upst = new UnicodePropertySymbolTable(iup);
	UnicodeSet.setDefaultXSymbolTable(upst);

	UnicodeSet naive  = new UnicodeSet("[\\p{XID_Continue}"
		+ "_"
		+ "\\p{Basic_Emoji}"
		+ "\\p{RGI_Emoji_Modifier_Sequence}"
		+ "\\p{RGI_Emoji_Tag_Sequence}"
		+ "\\p{RGI_Emoji_Zwj_Sequence}"
		+ "]");
	
	UnicodeSet flattened = new UnicodeSet();
	for (String s : naive) {
	    flattened.addAll(s);
	}
	flattened.freeze();
	System.out.println(flattened.toPattern(false));

	UnicodeSet uax31Raw  = new UnicodeSet("[\\p{XID_Continue}\\p{Extended_Pictographic}\\p{Emoji_Component}[-+_]-[#﹟＃]]");
	System.out.println(uax31Raw.toPattern(false));
    }
}
