import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import com.ibm.icu.dev.util.UnicodeMap;
import com.ibm.icu.dev.util.UnicodeMap.EntryRange;
import com.ibm.icu.text.UnicodeSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;
import java.util.function.Consumer;
import org.junit.jupiter.api.Test;
import org.unicode.cldr.util.Timer;
import org.unicode.props.IndexUnicodeProperties;
import org.unicode.props.UcdProperty;
import org.unicode.unittest.TestFmwkMinusMinus;

public class TestUnicodeMap extends TestFmwkMinusMinus {
    @Test
    public void TestMapComparison() {
        IndexUnicodeProperties iup = IndexUnicodeProperties.make();
        UnicodeMap<String> unicodeMapdata = iup.load(UcdProperty.kRSUnicode);
        UnicodeMap<String> unicodeMapdata2 = iup.load(UcdProperty.kMandarin);
        UnicodeMap<String> caseFoldingData = iup.load(UcdProperty.Case_Folding);
        UnicodeMap<String> ageData = iup.load(UcdProperty.Age);

        UnicodeMap<String> unicodeMap = new UnicodeMap<>(unicodeMapdata);
        UnicodeMapJ<String> unicodeMapJ = new UnicodeMapJ<>();
        unicodeMapJ.putEntryRanges(unicodeMapdata.entryRanges());

        final Consumer unicodeMapRanges =
                x -> {
                    for (EntryRange<String> er : unicodeMap.entryRanges()) {
                        doSomething(er);
                    }
                };
        final Consumer unicodeMapJRanges =
                x -> {
                    for (EntryRange<String> er : unicodeMapJ) {
                        doSomething(er);
                    }
                };

        final Consumer caseFoldTest =
                x -> {
                    UnicodeMap<String> caseFolding = new UnicodeMap<>(caseFoldingData);
                    UnicodeSet age14 = new UnicodeSet("\\p{age=14.0}");
                    UnicodeMap<String> result = caseFolding.retainAll(age14);
                };

        time("{\\m{Case_Folding}&[^\\p{age=14.0}]}", 10, caseFoldTest);
        time("{\\m{Case_Folding}&[^\\p{age=14.0}]}", 100, caseFoldTest);

        if (true) {
            return;
        }
        time("warmup unicodeMap", 5000, unicodeMapRanges);
        time("warmup unicodeMapJ", 5000, unicodeMapJRanges);
        time("unicodeMap", 5000, unicodeMapRanges);
        time("unicodeMapJ", 5000, unicodeMapJRanges);

        final Consumer unicodeMapReset =
                x -> {
                    unicodeMap.clear();
                    putEntryRanges(unicodeMapdata.entryRanges(), unicodeMap);
                    putEntryRanges(unicodeMapdata2.entryRanges(), unicodeMap);
                };

        final Consumer unicodeMapJReset =
                x -> {
                    unicodeMapJ.clear();
                    unicodeMapJ.putEntryRanges(unicodeMapdata.entryRanges());
                    unicodeMapJ.putEntryRanges(unicodeMapdata2.entryRanges());
                };

        time("unicodeMap reset ranges", 2, unicodeMapReset);
        time("unicodeMapJ reset ranges", 2, unicodeMapJReset);
        time("unicodeMap reset ranges", 5, unicodeMapReset);
        time("unicodeMapJ reset ranges", 5, unicodeMapJReset);

        final Consumer unicodeMapResetEach =
                x -> {
                    unicodeMap.clear();
                    putEntryIndividually(unicodeMapdata.entryRanges(), unicodeMap);
                    putEntryIndividually(unicodeMapdata2.entryRanges(), unicodeMap);
                };

        final Consumer unicodeMapJResetEach =
                x -> {
                    unicodeMapJ.clear();
                    putEntryIndividually(unicodeMapdata.entryRanges(), unicodeMapJ);
                    putEntryIndividually(unicodeMapdata2.entryRanges(), unicodeMapJ);
                };

        time("unicodeMap reset rangesI", 2, unicodeMapReset);
        time("unicodeMapJ reset rangesI", 2, unicodeMapJReset);
        time("unicodeMap reset rangesI", 5, unicodeMapReset);
        time("unicodeMapJ reset rangesI", 5, unicodeMapJReset);
    }

    static <T> void putEntryRange(EntryRange<T> value, UnicodeMap<T> target) {
        if (value.string == null) {
            target.putAll(value.codepoint, value.codepointEnd, value.value);
        } else {
            target.put(value.string, value.value);
        }
    }

    static <T> void putEntryRanges(Iterable<EntryRange<T>> entries, UnicodeMap<T> target) {
        for (EntryRange<T> value : entries) {
            putEntryRange(value, target);
        }
    }

    static <T> void putEntryIndividually(Iterable<EntryRange<T>> entries, UnicodeMap<T> target) {
        for (EntryRange<T> value : entries) {
            if (value.string == null) {
                for (int i = value.codepoint; i < value.codepointEnd; ++i)
                    target.put(i, value.value);
            } else {
                target.put(value.string, value.value);
            }
        }
    }

    static <T> void putEntryIndividually(Iterable<EntryRange<T>> entries, UnicodeMapJ<T> target) {
        for (EntryRange<T> value : entries) {
            if (value.string == null) {
                for (int i = value.codepoint; i < value.codepointEnd; ++i)
                    target.put(i, value.value);
            } else {
                target.put(value.string, value.value);
            }
        }
    }

    public <T> void time(String title, int iterations, Consumer forEach) {
        Timer t = new Timer();
        t.start();
        Iterable<EntryRange<T>> entries = null;
        for (int i = 0; i < iterations; ++i) forEach.accept(null);
        t.stop();
        System.out.println(title + "\t" + (t.getNanoseconds() / (double) iterations) + " ns");
    }

    private void doSomething(EntryRange<String> er) {}

    public static class UnicodeMapJ<T> implements Iterable<EntryRange<T>> {
        private RangeMap<Integer, T> rangeMap = TreeRangeMap.create();
        private Map<String, T> stringMap = new TreeMap<>();

        public UnicodeMapJ<T> putAll(int start, int end, T value) {
            rangeMap.put(Range.closedOpen(start, end + 1), value);
            return this;
        }

        public UnicodeMapJ<T> put(int cp, T value) {
            rangeMap.put(Range.closedOpen(cp, cp + 1), value);
            return this;
        }

        public void clear() {
            rangeMap.clear();
            stringMap.clear();
        }

        public UnicodeMapJ<T> put(String key, T value) {
            int cp = UnicodeSet.getSingleCodePoint(key);
            if (UnicodeSet.getSingleCodePoint(key) == Integer.MAX_VALUE) {
                return putAll(cp, cp, value);
            }
            stringMap.put(key, value);
            return this;
        }

        void putEntryRange(EntryRange<T> value) {
            if (value.string == null) {
                putAll(value.codepoint, value.codepointEnd, value.value);
            } else {
                put(value.string, value.value);
            }
        }

        void putEntryRanges(Iterable<EntryRange<T>> entries) {
            for (EntryRange<T> value : entries) {
                putEntryRange(value);
            }
        }

        @Override
        public Iterator<EntryRange<T>> iterator() {
            return new EntryRangeIterator();
        }

        public class EntryRangeIterator implements Iterator<EntryRange<T>> {
            private Iterator<Entry<Range<Integer>, T>> rangeMapIterator =
                    rangeMap.asMapOfRanges().entrySet().iterator();
            private Iterator<Entry<String, T>> stringMapIterator = stringMap.entrySet().iterator();
            private EntryRange<T> entryRange = new EntryRange<T>();

            @Override
            public boolean hasNext() {
                return rangeMapIterator.hasNext() || stringMapIterator.hasNext();
            }

            @Override
            public EntryRange<T> next() {
                if (rangeMapIterator.hasNext()) {
                    Entry<Range<Integer>, T> x = rangeMapIterator.next();
                    entryRange.string = null;
                    final Range<Integer> key = x.getKey();
                    entryRange.codepoint = key.lowerEndpoint();
                    entryRange.codepointEnd = key.upperEndpoint();
                    entryRange.value = x.getValue();
                    return entryRange;
                } else {
                    Entry<String, T> x = stringMapIterator.next();
                    entryRange.string = x.getKey();
                    entryRange.codepoint = entryRange.codepointEnd = -1;
                    entryRange.value = x.getValue();
                    return entryRange;
                }
            }
        }
    }
}
