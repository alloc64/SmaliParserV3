/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model;

import com.alloc64.smaliv3.model.test.SmaliTest;

import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;
import java.util.LinkedHashSet;

public class SmaliAccessModifier extends LinkedHashSet<String>
{
    public static SmaliAccessModifier none()
    {
        return new SmaliAccessModifier();
    }

    public SmaliAccessModifier()
    {
        super();
    }

    public SmaliAccessModifier(String accessModifier)
    {
        if(!StringUtils.isEmpty(accessModifier))
            addAll(Arrays.asList(accessModifier.split(" ")));
    }

    public boolean isConstructor()
    {
        return contains("constructor");
    }

    public boolean isPublic()
    {
        return contains("public");
    }

    @Override
    public String toString()
    {
        String result = String.join(" ", this);

        if(result.length() < 1)
            return "";

        return result;
    }

    public static class Test extends SmaliTest
    {
        @Override
        public void test() throws Exception
        {
            SmaliAccessModifier accessModifier = new SmaliAccessModifier("public static final constructor");

            assertTrue(accessModifier.contains("public"));
            assertTrue(accessModifier.contains("static"));
            assertTrue(accessModifier.contains("final"));
            assertTrue(accessModifier.contains("constructor"));
        }
    }
}
