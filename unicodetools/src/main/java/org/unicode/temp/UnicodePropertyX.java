/*
 *******************************************************************************
 * Copyright (C) 1996-2014, International Business Machines Corporation and    *
 * others. All Rights Reserved.                                                *
 *******************************************************************************
 */
package org.unicode.temp;

import com.ibm.icu.dev.util.UnicodeMap;
import com.ibm.icu.text.UnicodeSet;
import java.util.List;
import java.util.stream.Collectors;

import org.unicode.props.UnicodeProperty;

public class UnicodePropertyX<T> extends UnicodeProperty {
    private UnicodeMap<T> data;

    @Override
    public String getTypeName() {
        return super.getTypeName();
    }

    @Override
    public String getVersion() {
        return super.getVersion();
    }

    @Override
    public String getValue(int codepoint) {
        return getValueInternal(codepoint).toString();
    }

    public T getValueInternal(int codepoint) {
        return data.getValue(codepoint);
    }

    @Override
    public List<String> getNameAliases(List<String> result) {
        return super.getNameAliases(result);
    }

    @Override
    public List<String> getValueAliases(String valueAlias, List<String> result) {
        return super.getValueAliases(valueAlias, result);
    }
    public List<String> getValueAliasesInternal(T internalValue, List<String> result) {
        return super.getValueAliases(valueAlias, result);
    }


    @Override
    public List<String> getAvailableValues(List<String> result) {
        data.getAvailableValues().forEach(x -> result.add(x.toString()));
        return result;
    }

    public List<T> getAvailableValuesInternal(List<T> result) {
        return data.getAvailableValues(result);
    }


    @Override
    protected String _getVersion() {
        return null;
    }

    @Override
    protected String _getValue(int codepoint) { // TODO
        return null;
    }

    @Override
    protected List<String> _getNameAliases(List<String> result) {
        return null;
    }

    @Override
    protected List<String> _getValueAliases(String valueAlias, List<String> result) {
        return null;
    }

    @Override
    protected List<String> _getAvailableValues(List<String> result) {
        return null;
    }

    @Override
    public int getMaxWidth(boolean getShortest) {
        return super.getMaxWidth(getShortest);
    }

    @Override
    public UnicodeSet getSet(PatternMatcher matcher, UnicodeSet result) {
        return super.getSet(matcher, result);
    }

    @Override
    public UnicodeMap getUnicodeMap() {
        return data;
    }

    @Override
    public UnicodeMap getUnicodeMap(boolean getShortest) {
        return super.getUnicodeMap(getShortest);
    }

    @Override
    public UnicodeMap getUnicodeMap_internal() {
        return super.getUnicodeMap_internal();
    }

    @Override
    protected UnicodeMap _getUnicodeMap() {
        return super._getUnicodeMap();
    }

    @Override
    public boolean isValidValue(String propertyValue) {
        return super.isValidValue(propertyValue);
    }

    @Override
    public List<String> getValueAliases() {
        return super.getValueAliases();
    }

    @Override
    public boolean isDefault(int cp) {
        return super.isDefault(cp);
    }

    @Override
    public boolean hasUniformUnassigned() {
        return super.hasUniformUnassigned();
    }

    @Override
    protected UnicodeProperty setUniformUnassigned(boolean hasUniformUnassigned) {
        return super.setUniformUnassigned(hasUniformUnassigned);
    }

    @Override
    public String transform(Integer codepoint) {
        return super.transform(codepoint);
    }

    @Override
    public String getValue(String s, String separator, boolean withCodePoint) {
        return super.getValue(s, separator, withCodePoint);
    }

    @Override
    public int hashCode() {
        return data.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return data.equals(obj);
    }
    
    public boolean equals(UnicodePropertyX<T> obj) {
        return data.equals(obj.data);
    }
}
