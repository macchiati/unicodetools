package org.unicode.tools;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import org.unicode.props.IndexUnicodeProperties;
import org.unicode.props.UcdProperty;
import org.unicode.props.VersionToAge;
import org.unicode.text.utility.Utility;
import org.unicode.tools.emoji.Emoji;
import org.unicode.tools.emoji.EmojiData;

import com.google.common.base.Joiner;
import com.google.common.collect.Multimap;
import com.google.common.collect.TreeMultimap;
import com.ibm.icu.dev.util.UnicodeMap;
import com.ibm.icu.impl.locale.XCldrStub.ImmutableSet;
import com.ibm.icu.lang.UCharacter;
import com.ibm.icu.text.UnicodeSet;
import com.ibm.icu.util.VersionInfo;

public class PropertiesOverTime {

    public static void main(String[] args) {
        //showPropGrowth();
        showEmojiClasses();
    }

    private static void showEmojiClasses() {
        IndexUnicodeProperties iup = IndexUnicodeProperties.make(); // latest version
        UnicodeSet emojiCharacter = iup.loadBinary(UcdProperty.Emoji);
        UnicodeSet emojiPresentation = iup.loadBinary(UcdProperty.Emoji_Presentation);
        UnicodeSet patternSyntax = iup.loadBinary(UcdProperty.Pattern_Syntax);
        UnicodeMap<String> emojiPresentationSequence = iup.load(UcdProperty.Emoji_Variation_Sequences);
        
        // System.out.println("values: " + Joiner.on('\n').join(emojiPresentationSequence.entryRanges()));
        
        UnicodeSet hasEmojiPresentationSequence = new UnicodeSet();
        for (String key : emojiPresentationSequence.getSet("emoji style")) {
            int first = key.codePointAt(0);
            if (emojiCharacter.contains(first)) {
                hasEmojiPresentationSequence.add(first);
            }
        }
        hasEmojiPresentationSequence.freeze();
        
        UnicodeSet zwjseq = iup.loadBinary(UcdProperty.RGI_Emoji_Zwj_Sequence);
        UnicodeSet modifier = iup.loadBinary(UcdProperty.Emoji_Modifier);

        Multimap<Integer, String> nonFinalNoModifierNoVariant = TreeMultimap.create();
        for (String s : zwjseq) {
            int cp = 0;
            int cpLen;
            for (int i = 0; i < s.length(); i += cpLen) {
                cp = s.codePointAt(i);
                cpLen = UCharacter.charCount(cp);
                if (s.length() == i + cpLen) {
                    continue;
                }
                if (!emojiCharacter.contains(cp) 
                        || modifier.contains(cp)
                        || hasEmojiPresentationSequence.contains(cp)) {
                    continue;
                }
                int nextCp = s.codePointAt(i + cpLen);
                if (nextCp == Emoji.EMOJI_VARIANT) {
                    continue;
                }
                if (modifier.contains(nextCp)) {
                    continue;
                }
                System.out.println(s + "\t" + Utility.hex(s));
                nonFinalNoModifierNoVariant.put(cp, s);
            }
        }

        System.out.println("nonFinalNoModifierNoVariant: " + Joiner.on('\n').join(nonFinalNoModifierNoVariant.asMap().entrySet()));

       // System.out.println("values: " + Joiner.on('\n').join(emojiPresentationSequence.entryRanges()));

        
        
        System.out.println("has emoji presentation sequence\t" + hasEmojiPresentationSequence.toPattern(false));
        
        UnicodeSet result = new UnicodeSet();
        List<String> fullTitle = new ArrayList<>();
        for (int i = 0; i < 8; ++i) {
            fullTitle.clear();
            result.clear().addAll(emojiCharacter);
            retainOrRemove(i, 4, "patternSyntax", patternSyntax, result, fullTitle);
            retainOrRemove(i, 2, "emojiPresentationSequence", hasEmojiPresentationSequence, result, fullTitle);
            retainOrRemove(i, 1, "emojiPresentation", emojiPresentation, result, fullTitle);
            System.out.println(Joiner.on('\t').join(fullTitle) + "\t" + result.size() + "\t" + result.toPattern(false));
        }
    }

    public static void retainOrRemove(int i, int mask, String title, UnicodeSet emojiPresentation, UnicodeSet result, List<String> fullTitle) {
        if ((i & mask) != 0) {
            result.retainAll(emojiPresentation);
        } else {
            result.removeAll(emojiPresentation);
            title = "-" + title;
        }
        fullTitle.add(title);
    }

    public static void showPropGrowth() {
        for (int year = 1995; year <= VersionToAge.ucd.maxYear(); ++year) {
            VersionInfo age = VersionToAge.ucd.getVersionInfoForYear(year);
            if (age == VersionToAge.UNASSIGNED) {
                continue;
            }
            ArrayList<Integer> data2 = new ArrayList<>();
            Multimap<Integer,UcdProperty> data = TreeMultimap.create();
            IndexUnicodeProperties iup = IndexUnicodeProperties.make(age);

            main:
                for (UcdProperty prop : iup.getAvailableUcdProperties()) {
                    if (prop == UcdProperty.Name || prop == UcdProperty.Unicode_1_Name) {
                        continue main;
                    }
                    if (!RegexProps.contains(prop)
                            // && !ICUProps.contains(prop)
                            ) {
                        continue main;
                    }
                    //String defaultValue = IndexUnicodeProperties.getDefaultValue(prop);
                    UnicodeMap<String> map;
                    try {
                        map = iup.load(prop);
                    } catch (Exception e) {
                        System.out.println(prop + " can't load in " + age + "\t" + e.getMessage());
                        //                    e.printStackTrace();
                        continue main;
                    }
                    Set<String> values = map.values();
                    if (values.size() < 2) {
                        continue main;
                    }
                    data2.add(values.size());
                    data.put(values.size(), prop);
                }
            Collections.sort(data2, Collections.reverseOrder());

            System.out.println("#\t" + year + "\t" + Joiner.on("\t").join(data2));
            System.out.println("#\t" + year + "\t" + data.asMap());
        }
    }

    static final Set<UcdProperty> ICUProps = getPropertySet("Canonical_Combining_Class", "Name", "Unicode_1_Name", "Alphabetic", "ASCII_Hex_Digit", "Basic_Emoji", 
            "Bidi_Control", "Bidi_Mirrored", "Case_Ignorable", "Cased", "Changes_When_Casefolded", "Changes_When_Casemapped", 
            "Changes_When_NFKC_Casefolded", "Changes_When_Lowercased", "Changes_When_Titlecased", "Changes_When_Uppercased", "Dash", 
            "Default_Ignorable_Code_Point", "Deprecated", "Diacritic", "Emoji", "Emoji_Component", "Emoji_Keycap_Sequence", "Emoji_Modifier", 
            "Emoji_Modifier_Base", "Emoji_Presentation", "Extended_Pictographic", "Extender", "Full_Composition_Exclusion", "Grapheme_Base", "Grapheme_Extend", "Grapheme_Link", "Hex_Digit", "Hyphen", "ID_Continue", "ID_Start", 
            "Ideographic", "IDS_Binary_Operator", "IDS_Triary_Operator", "Join_Control", "Logical_Order_Exception", "Lowercase", "Math", 
            "Noncharacter_Code_Point", "Pattern_Syntax", "Pattern_White_Space", "Prepended_Concatenation_Mark", "Quotation_Mark", "Radical", "Regional_Indicator",
            "RGI_Emoji", "RGI_Emoji_Flag_Sequence", "RGI_Emoji_Modifier_Sequence", "RGI_Emoji_Tag_Sequence", "RGI_Emoji_ZWJ_Sequence", 
            "Soft_Dotted", "STerm", "Terminal_Punctuation", "Unified_Ideograph", "Uppercase", "White_Space", "XID_Continue", "XID_Start", 
            "Numeric_Value", "Bidi_Class", "Bidi_Paired_Bracket_Type", "Block", "Decomposition_Type", "East_Asian_Width", "General_Category", 
            "Grapheme_Cluster_Break", "Hangul_Syllable_Type", "Indic_Positional_Category", "Indic_Syllabic_Category", "Joining_Group", 
            "Joining_Type", "Line_Break", 
            "Numeric_Type", "Script", "Sentence_Break", "Vertical_Orientation", "Word_Break", 
            "Script_Extensions", "Age", "ISO_Comment", "Name_Alias", "Bidi_Mirroring_Glyph", "Simple_Case_Folding", 
            "Simple_Lowercase_Mapping", "Simple_Titlecase_Mapping", "Simple_Uppercase_Mapping", "Case_Folding", "Decomposition_Mapping", 
            "FC_NFKC_Closure", "Lowercase_Mapping", "NFKC_Casefold", "Titlecase_Mapping", "Uppercase_Mapping");

    static final Set<UcdProperty> RegexProps = getPropertySet(
            "Age",
            "Alphabetic",
            "ASCII_Hex_Digit",
            "Basic_Emoji",
            "Bidi_Class",
            "Bidi_Control",
            "Bidi_Mirrored",
            "Bidi_Mirroring_Glyph",
            "Bidi_Paired_Bracket",
            "Bidi_Paired_Bracket_Type",
            "Block",
            "Canonical_Combining_Class",
            "Case_Ignorable",
            "Cased",
            "Changes_When_Casefolded",
            "Changes_When_Casemapped",
            "Changes_When_Lowercased",
            "Changes_When_NFKC_Casefolded",
            "Changes_When_Titlecased",
            "Changes_When_Uppercased",
            "Dash",
            "Decomposition_Type",
            "Default_Ignorable_Code_Point",
            "Deprecated",
            "Diacritic",
            "East_Asian_Width",
            "Emoji",
            "Emoji_Component",
            "Emoji_Keycap_Sequence",
            "Emoji_Modifier",
            "Emoji_Modifier_Base",
            "Emoji_Presentation",
            "Equivalent_Unified_Ideograph",
            "Extended_Pictographic",
            "Extender",
            "General_Category",
            "Grapheme_Base",
            "Grapheme_Cluster_Break",
            "Grapheme_Extend",
            "Hangul_Syllable_Type",
            "Hex_Digit",
            "ID_Continue",
            "ID_Start",
            "Identifier_Status",
            "Identifier_Type",
            "Ideographic",
            "IDS_Binary_Operator",
            "IDS_Trinary_Operator",
            "Join_Control",
            "Joining_Group",
            "Joining_Type",
            "Line_Break",
            "Logical_Order_Exception",
            "Lowercase",
            "Math",
            "Name",
            "Name_Alias",
            "NFC_Quick_Check",
            "NFD_Quick_Check",
            "NFKC_Casefold",
            "NFKC_Quick_Check",
            "NFKD_Quick_Check",
            "Noncharacter_Code_Point",
            "Numeric_Type",
            "Numeric_Value",
            "Pattern_Syntax",
            "Pattern_White_Space",
            "Prepended_Concatenation_Mark",
            "Quotation_Mark",
            "Radical",
            "Regional_Indicator",
            "RGI_Emoji_Flag_Sequence",
            "RGI_Emoji_Modifier_Sequence",
            "RGI_Emoji_Tag_Sequence",
            "RGI_Emoji_ZWJ_Sequence",
            "RGI_Emoji",
            "Script",
            "Script_Extensions",
            "Sentence_Break",
            "Sentence_Terminal",
            "Simple_Case_Folding",
            "Simple_Lowercase_Mapping",
            "Simple_Titlecase_Mapping",
            "Simple_Uppercase_Mapping",
            "Soft_Dotted",
            "Terminal_Punctuation",
            "Unified_Ideograph",
            "Uppercase",
            "Variation_Selector",
            "Vertical_Orientation",
            "White_Space",
            "Word_Break",
            "XID_Continue",
            "XID_Start"
            );

    /*
         Couldn't get property from: Emoji_Keycap_Sequence
        Couldn't get property from: IDS_Triary_Operator
        Couldn't get property from: RGI_Emoji
        Couldn't get property from: RGI_Emoji_ZWJ_Sequence
        Couldn't get property from: STerm
        Couldn't get property from: NF_QuickCheck
        Couldn't get property from: Expands_On_NF
        Couldn't get property from: Simple_Lowercase_ Mapping
        Couldn't get property from: Simple_Titlecase_ Mapping
        Couldn't get property from: Simple_Uppercase_ Mapping
     */

    private static Set<UcdProperty> getPropertySet(String ... propNames) {
        Set<UcdProperty> coreProps = new TreeSet<>();
        for (String prop : propNames) {
            try {
                coreProps.add(UcdProperty.valueOf(prop));
            } catch (Exception e) {
                System.out.println("Couldn't get property from: " + prop);
            }
        }
        return ImmutableSet.copyOf(coreProps);
    }
}
