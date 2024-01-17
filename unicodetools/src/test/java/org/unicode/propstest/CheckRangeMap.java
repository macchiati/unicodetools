package org.unicode.propstest;

import com.google.common.collect.Range;
import com.google.common.collect.RangeMap;
import com.google.common.collect.TreeRangeMap;
import java.util.Map.Entry;

public class CheckRangeMap {
    public static void main(String[] args) {
        RangeMap<Integer, String> rangeMap = TreeRangeMap.create();
        rangeMap.put(Range.closedOpen(3, 7), "3-7");
        rangeMap.put(Range.closedOpen(0, 6), "0-5");
        rangeMap.putCoalescing(Range.closedOpen(6, 10), "0-5");
        for (Entry<Range<Integer>, String> entry : rangeMap.asMapOfRanges().entrySet()) {
            System.out.println(entry);
        }
        rangeMap.span();
        for (int i = rangeMap.span().lowerEndpoint(); i <= rangeMap.span().upperEndpoint(); ++i) {
            System.out.println(i + "\t" + rangeMap.get(i));
        }
    }
}
