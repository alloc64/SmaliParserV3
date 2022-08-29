/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.opcode;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * V	void - can only be used for return types
 * Z	boolean
 * B	byte
 * S	short
 * C	char
 * I	int
 * J	long (64 bits)
 * F	float
 * D	double (64 bits)
 */
public enum PrimitiveType
{
    Void("V", "void"),
    Boolean("Z", "boolean"),
    Byte("B", "byte"),
    Short("S", "short"),
    Char("C", "char"),
    Int("I", "int"),
    Long("J", "long"),
    Float("F", "float"),
    Double("D", "double");

    private String value;
    private String type;

    private static final Map<String, PrimitiveType> typesByValue = new LinkedHashMap<>();

    static
    {
        for(PrimitiveType simpleType : PrimitiveType.values())
            typesByValue.put(simpleType.getValue(), simpleType);
    }

    PrimitiveType(String value, String type)
    {
        this.value = value;
        this.type = type;
    }

    public String getValue()
    {
        return value;
    }

    public String getType()
    {
        return type;
    }

    public static PrimitiveType from(String value)
    {
        return typesByValue.get(value);
    }
}
