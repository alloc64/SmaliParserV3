/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.Smali;
import com.alloc64.smaliv3.model.LineNumber;
import com.alloc64.smaliv3.model.SmaliAccessModifier;
import com.alloc64.smaliv3.model.SmaliObject;
import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.clazz.SmaliClassName;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmaliClassToken extends SmaliObject
{
    public static final String TOKEN = ".class";

    private SmaliAccessModifier accessModifier;
    private SmaliClassName className;

    public SmaliClassToken(SmaliAccessModifier accessModifier, SmaliClassName className)
    {
        if(accessModifier == null)
            throw new IllegalArgumentException("accessModifier is mandatory");

        if(className == null)
            throw new IllegalArgumentException("className is mandatory");

        this.accessModifier = accessModifier;
        this.className = className;
    }

    public SmaliAccessModifier getAccessModifier()
    {
        return accessModifier;
    }

    public SmaliClassName getClassName()
    {
        return className;
    }

    @Override
    public String toString()
    {
        String accessModifierStr = accessModifier.toString();

        if(accessModifier.size() > 0)
            return String.format("%s %s %s", TOKEN, accessModifierStr, className);

        return String.format("%s %s", TOKEN, className);
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliClassToken>
    {
        private static final Pattern classPattern = Pattern.compile(".class\\s?(?<" + Smali.ACCESS_MODIFIER + ">...+)?\\s(?<" + Smali.FULL_CLASS_NAME + ">L\\S+?;)");

        @Override
        public SmaliClassToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            Matcher m = classPattern.matcher(line);

            if(m.find())
            {
                return new SmaliClassToken(
                        new SmaliAccessModifier(m.group(Smali.ACCESS_MODIFIER)),
                        new SmaliClassName(m.group(Smali.FULL_CLASS_NAME))
                );
            }

            validate(TOKEN, line);

            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                assertTrue(new Tokenizer().tokenize(LineNumber.NONE, ".class public La/okio/AsyncTimeout;", null) != null);

                assertTrue(new Tokenizer().tokenize(LineNumber.NONE, ".class LAsyncTimeout;", null) != null);

                assertTrue(new Tokenizer().tokenize(LineNumber.NONE, ".class final static public LAsyncTimeout;", null) != null);
            }
        }
    }
}
