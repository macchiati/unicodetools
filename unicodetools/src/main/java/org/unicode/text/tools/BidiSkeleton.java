package org.unicode.text.tools;

import java.util.Map.Entry;

import org.unicode.props.IndexUnicodeProperties;
import org.unicode.props.UcdProperty;
import org.unicode.props.UcdPropertyValues.Bidi_Class_Values;

import com.ibm.icu.dev.util.UnicodeMap;
import com.ibm.icu.text.UnicodeSet;

public class BidiSkeleton {
    public static void main(String[] args) {
        IndexUnicodeProperties iup = IndexUnicodeProperties.make();
        UnicodeSet xidContinue = iup.loadBinary(UcdProperty.XID_Continue);
        UnicodeMap<Bidi_Class_Values> bidi = iup.loadEnum(UcdProperty.Bidi_Class);
        for (Bidi_Class_Values value : bidi.values()) {
            UnicodeSet uset = new UnicodeSet(bidi.getSet(value)).retainAll(xidContinue);
            if (uset.size() == 0) {
                continue;
            }
            System.out.println(value + "\t" + uset.size() + "\t" + uset.toPattern(false));
            
        }
    }
}
