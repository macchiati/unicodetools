package org.unicode.tools;

import java.util.Locale;
import java.util.TreeSet;

import org.unicode.props.IndexUnicodeProperties;
import org.unicode.props.UcdProperty;
import org.unicode.text.utility.Utility;

import com.ibm.icu.dev.util.UnicodeMap;
import com.ibm.icu.impl.locale.XCldrStub.Splitter;
import com.ibm.icu.text.UnicodeSet;

public class JapaneseTranslitBuilder {
    private static final boolean SHOW = false;
    public static void main(String[] args) {
        IndexUnicodeProperties iup = IndexUnicodeProperties.make();
        UnicodeMap<String> kun = iup.load(UcdProperty.kJapaneseKun);
        UnicodeMap<String> on = iup.load(UcdProperty.kJapaneseOn);
        UnicodeMap<String> rsUnicode = iup.load(UcdProperty.kRSUnicode);
        //kRSUnicode
        UnicodeSet both = new UnicodeSet(kun.keySet()).addAll(on.keySet());
        UnicodeMap<String> result = new UnicodeMap<>();
        for (String s : both) {
            String best = best(kun.get(s), on.get(s));
            result.put(s, best);
            if (SHOW) System.out.println(Utility.hex(s, " ") 
                    + "\t;\t" + fix(kun.get(s))
                    + "\t;\t" + fix(on.get(s)) 
                    + "\t;\t" + fix2(rsUnicode.get(s)) 
                    + "\t#\t" + s + " → " + best + ";");
        }
        // [啊]→a;
        TreeSet<String> values = new TreeSet<>(result.values());
        for (String value : values) {
            UnicodeSet uset = result.getSet(value);
            String source = uset.size() == 1 
                    ? uset.iterator().next() 
                    : uset.toPattern(false);
            System.out.println(source + "→" + value + ";");      
        }
    }

    private static String best(String a, String b) {
        String result = a == null ? b : a;
        return BAR.split(result).iterator().next().toLowerCase(Locale.ENGLISH);
    }

    static final Splitter BAR = Splitter.on('|');
    private static String fix(String string) {
        return string == null ? "" : string.toLowerCase(Locale.ENGLISH).replace('|', '/');
    }
    private static String fix2(String string) {
        return string == null ? "" : BAR.split(string).iterator().next().replace(".", "\t;\t");
    }
}
