/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.clazz;

import com.alloc64.smaliv3.model.SmaliPackageName;
import com.alloc64.smaliv3.model.opcode.PrimitiveType;
import com.alloc64.smaliv3.model.test.SmaliTest;

import org.apache.commons.lang3.StringUtils;

public class SmaliClassName
{
    private SmaliPackageName packageName;
    private PrimitiveType primitiveType;
    private String className;

    private int arrayDimensions;

    public SmaliClassName(String name)
    {
        name = prepareName(name);

        this.primitiveType = PrimitiveType.from(name);

        if(primitiveType == null)
            resolveFullClassName(name);
    }

    private String prepareName(String name)
    {
        StringBuilder sb = new StringBuilder();

        for(int i = 0; i < name.length(); i++)
        {
            char c = name.charAt(i);

            if(c == '[')
            {
                arrayDimensions++;
                continue;
            }

            sb.append(c);
        }

        return sb.toString();
    }

    private void resolveFullClassName(String name)
    {
        if(StringUtils.isEmpty(name))
            throw new IllegalStateException("Invalid syntax. name is mandatory: " + name);

        if(!name.startsWith("L"))
            throw new IllegalStateException("Invalid syntax. L is required in class name declaration: " + name);

        if(!name.endsWith(";"))
            throw new IllegalStateException("Invalid syntax. ; is required in class name declaration: " + name);

        name = name.substring(1, name.length()-1);

        if(name.startsWith("["))
            name = name.substring(1);

        String[] arr = name.split("/");

        int classNameIdx = arr.length-1;

        StringBuilder packageName = new StringBuilder();

        if(arr.length > 1)
        {
            for (int i = 0; i < classNameIdx; i++)
            {
                packageName.append(arr[i]);

                if(i < classNameIdx-1)
                    packageName.append("/");
            }
        }

        this.packageName = new SmaliPackageName(packageName.toString());
        this.className = arr[classNameIdx];

        if(StringUtils.isEmpty(className))
            throw new IllegalStateException("Invalid syntax. Class name cannot be empty: " + name);
    }

    public SmaliPackageName getPackageName()
    {
        return packageName;
    }

    public void setPackageName(SmaliPackageName packageName)
    {
        this.packageName = packageName;
    }

    public PrimitiveType getPrimitiveType()
    {
        return primitiveType;
    }

    public String getClassName()
    {
        return className;
    }

    public void setClassName(String className)
    {
        this.className = className;
    }

    public String getFullClassName()
    {
        if(primitiveType != null)
            return (isArray() ? getArrayPrefix() : "") + primitiveType.getValue();

        return String.format("%s%s/%s;", (isArray() ? getArrayPrefix() + "L" : "L"), packageName, className);
    }

    public boolean isArray()
    {
        return arrayDimensions > 0;
    }

    public int getArrayDimensions()
    {
        return arrayDimensions;
    }

    private String getArrayPrefix()
    {
        return StringUtils.repeat('[', arrayDimensions);
    }

    @Override
    public boolean equals(Object o)
    {
        return toString().equals(o.toString());
    }

    @Override
    public int hashCode()
    {
        return toString().hashCode();
    }

    @Override
    public String toString()
    {
        return getFullClassName();
    }

    public static class Test extends SmaliTest
    {
        @Override
        public void test() throws Exception
        {
            SmaliClassName className = new SmaliClassName("La/okio/AsyncTimeout;");

            assertTrue(className.getPackageName().toString().equals("a/okio"));
            assertTrue(className.getPackageName().getPackageName().equals("a/okio"));
            assertTrue(className.getClassName().equals("AsyncTimeout"));
            assertTrue(className.toString().equals("La/okio/AsyncTimeout;"));

            className = new SmaliClassName("[B");

            assertTrue(className.getPackageName() == null);
            assertTrue(className.getClassName() == null);
            assertTrue(className.getPrimitiveType() == PrimitiveType.Byte);
            assertTrue(className.isArray());

            className = new SmaliClassName("J");

            assertTrue(className.getPackageName() == null);
            assertTrue(className.getClassName() == null);
            assertTrue(className.getPrimitiveType() == PrimitiveType.Long);
            assertTrue(!className.isArray());


            className = new SmaliClassName("Ltest/pub/IRLog;");

            assertTrue(className.getPackageName().toString().equals("test/pub"));
            assertTrue(className.getPackageName().getPackageName().equals("test/pub"));
            assertTrue(className.getClassName().equals("IRLog"));
            assertTrue(className.toString().equals("Ltest/pub/IRLog;"));
        }
    }
}
