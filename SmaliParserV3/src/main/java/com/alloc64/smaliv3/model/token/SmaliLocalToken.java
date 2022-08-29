/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.Smali;
import com.alloc64.smaliv3.model.LineNumber;
import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.clazz.SmaliClassName;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;

/**
 * .local v2, "handler":Landroid/os/Handler;
 */
public class SmaliLocalToken extends SmaliSingleRegisterToken
{
    public static final String TOKEN = ".local";
    private final String variableName;
    private final SmaliClassName variableType;
    private final String genericType;

    public SmaliLocalToken(int lineNumber, String register, String variableName, SmaliClassName variableType, String genericType)
    {
        super(lineNumber, TOKEN, register);

        this.variableName = variableName;
        this.variableType = variableType;
        this.genericType = genericType;
    }

    public String getVariableName()
    {
        return variableName;
    }

    public SmaliClassName getVariableType()
    {
        return variableType;
    }

    public String getGenericType()
    {
        return genericType;
    }

    @Override
    public String toString()
    {
        if(StringUtils.isEmpty(genericType))
            return toString(String.format("%s %s, \"%s\":%s", getName(), getRegister(), variableName, variableType));

        return toString(String.format("%s %s, \"%s\":%s, \"%s\"", getName(), getRegister(), variableName, variableType, genericType));
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliLocalToken>
    {
        @Override
        public SmaliLocalToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            Matcher m = Smali.localPattern.matcher(line);

            if (m.find())
            {
                return new SmaliLocalToken(
                        lineNumber,
                        m.group(Smali.REGISTER),
                        m.group(Smali.VARIABLE_NAME),
                        new SmaliClassName(m.group(Smali.TYPE)),
                        m.group(Smali.GENERIC_TYPE)
                );
            }

            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                SmaliLocalToken local = new Tokenizer().tokenize(LineNumber.NONE, ".local v2, \"handler\":Landroid/os/Handler;", null);

                assertTrue(local.getRegister().equals("v2"));
                assertTrue(local.getVariableName().equals("handler"));
                assertTrue(local.getVariableType().toString().equals("Landroid/os/Handler;"));
                assertTrue(local.toString().equals(".local v2, \"handler\":Landroid/os/Handler;"));

                local = new Tokenizer().tokenize(LineNumber.NONE, ".local v2, \"startNanos\":J", null);

                assertTrue(local.getRegister().equals("v2"));
                assertTrue(local.getVariableName().equals("startNanos"));
                assertTrue(local.getVariableType().toString().equals("J"));
                assertTrue(local.toString().equals(".local v2, \"startNanos\":J"));

                local = new Tokenizer().tokenize(LineNumber.NONE, ".local p1, \"referenceQueue2\":Ljava/lang/ref/ReferenceQueue;, \"Ljava/lang/ref/ReferenceQueue<Ljava/lang/Object;>;\"", null);

                assertTrue(local.getRegister().equals("p1"));
                assertTrue(local.getVariableName().equals("referenceQueue2"));
                assertTrue(local.getVariableType().toString().equals("Ljava/lang/ref/ReferenceQueue;"));
                assertTrue(local.getGenericType().equals("Ljava/lang/ref/ReferenceQueue<Ljava/lang/Object;>;"));
            }
        }
    }
}




