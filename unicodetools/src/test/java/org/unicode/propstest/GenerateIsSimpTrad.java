package org.unicode.propstest;

import com.ibm.icu.dev.util.UnicodeMap;
import com.ibm.icu.text.UnicodeSet;
import org.unicode.props.IndexUnicodeProperties;
import org.unicode.props.UcdProperty;
import org.unicode.props.UcdPropertyValues;

/* Generate derived properties for Simplified vs Traditional
<br>1. X is used in both SC and TC and is unchanged when mapping between them. An example would be 井 U+4E95. This is the most common case, and is indicated by both the kSimplifiedVariant and kTraditionalVariant fields being empty.
<br>2. X is used in TC but not SC, that is, it is changed when converting from TC to SC, but not vice versa. In this case, the kSimplifiedVariant field lists the character(s) to which it is mapped and the kTraditionalVariant field is empty. An example would be 書 U+66F8 whose kSimplifiedVariant field is 书 U+4E66.
<br>3. X is used in SC but not TC, that is, it is changed when converting from SC to TC, but not vice versa. In this case, the kTraditionalVariant field lists the character(s) to which it is mapped and the kSimplifiedVariant field is empty. An example would be 学 U+5B66 whose kTraditionalVariant field is 學 U+5B78.
<br>4. X is used in both SC and TC and may be changed when mapping between them. This is the most complex case, because there are two distinct sub-cases:
<br>4.1 X may be mapped to itself or to another character when converting between SC and TC. In this case, the character is its own simplification as well as the simplification for other characters.
An example would be 后 U+540E, which is the simplification for itself and for 後 U+5F8C.
When mapping TC to SC, it is left alone, but when mapping SC to TC it may or may not be changed, depending on context.
 In this case, both kTraditionalVariant and kSimplifiedVariant fields are defined and X is included among the values for both.
<br>4.2 X is used for different words in SC and TC. When converting between the two, it is always changed. An example would be 苧 U+82E7.
In traditional Chinese, it is pronounced zhù and refers to a kind of nettle.
In simplified Chinese, it is pronounced níng and means limonene (a chemical found in the rinds of lemons and other citrus fruits).
When converting TC to SC it is mapped to 苎 U+82CE, and when converting SC to TC it is mapped to 薴 U+85B4.
In this case, both kTraditionalVariant and kSimplifiedVariant fields are defined but X is not included in the values for either.
*/
public class GenerateIsSimpTrad {
    // eventually, could make this a derived property
    public static void main(String[] args) {
        IndexUnicodeProperties iup = IndexUnicodeProperties.make();
        UnicodeMap<String> kSimp = iup.load(UcdProperty.kSimplifiedVariant);
        UnicodeMap<String> kTrad = iup.load(UcdProperty.kTraditionalVariant);
        UnicodeSet Hani =
                iup.loadEnum(UcdProperty.Script, UcdPropertyValues.Script_Values.class)
                        .getSet(UcdPropertyValues.Script_Values.Han);
        UnicodeSet kTradKeys = kSimp.keySet();
        UnicodeSet kSimpKeys = kTrad.keySet();
        UnicodeSet kSimpValues = new UnicodeSet();
        kSimp.values().forEach(x -> kSimpValues.addAll(x));
        UnicodeSet kTradValues = new UnicodeSet();
        kTrad.values().forEach(x -> kTradValues.addAll(x));
        System.out.println("kSimpKeys:\t" + kSimpKeys.size() + "\t" + kSimpKeys.toPattern(false));
        System.out.println("kTradKeys:\t" + kTradKeys.size() + "\t" + kTradKeys.toPattern(false));
        System.out.println(
                "kSimpValues:\t" + kSimpValues.size() + "\t" + kSimpValues.toPattern(false));
        System.out.println(
                "kTradValues:\t" + kTradValues.size() + "\t" + kTradValues.toPattern(false));

        UnicodeSet simpUnion = new UnicodeSet(kSimpKeys).addAll(kSimpValues);
        UnicodeSet tradUnion = new UnicodeSet(kTradKeys).addAll(kTradValues);
        UnicodeSet SimpMinusTrad = new UnicodeSet(simpUnion).removeAll(tradUnion);
        UnicodeSet TradMinusSimp = new UnicodeSet(tradUnion).removeAll(simpUnion);

        System.out.println("simpUnion:\t" + simpUnion.size() + "\t" + simpUnion.toPattern(false));
        System.out.println("tradUnion:\t" + tradUnion.size() + "\t" + tradUnion.toPattern(false));
        System.out.println(
                "SimpMinusTrad:\t"
                        + SimpMinusTrad.size()
                        + "\t"
                        + SimpMinusTrad.getRangeCount()
                        + "\t"
                        + (SimpMinusTrad.toPattern(false).length() - 2)
                        + "\t"
                        + SimpMinusTrad.toPattern(false));
        System.out.println(
                "TradMinusSimp:\t"
                        + TradMinusSimp.size()
                        + "\t"
                        + TradMinusSimp.getRangeCount()
                        + "\t"
                        + (TradMinusSimp.toPattern(false).length() - 2)
                        + "\t"
                        + TradMinusSimp.toPattern(false));
    }
}
