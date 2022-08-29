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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmaliImplementsToken extends SmaliToken
{
    public static final String TOKEN = ".implements";

    private SmaliClassName className;

    public SmaliImplementsToken(int lineNumber, SmaliClassName className)
    {
        super(lineNumber, TOKEN);

        if(className == null)
            throw new IllegalArgumentException("className is mandatory");

        this.className = className;
    }

    public SmaliClassName getClassName()
    {
        return className;
    }

    @Override
    public String toString()
    {
        return toString(String.format("%s %s", getName(), className));
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliImplementsToken>
    {
        public static final Pattern interfacePattern = Pattern.compile(".implements\\s(?<" + Smali.FULL_CLASS_NAME + ">L\\S+?;)");

        @Override
        public SmaliImplementsToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            Matcher m = interfacePattern.matcher(line);

            if(m.find())
            {
                return new SmaliImplementsToken(
                        lineNumber,
                        new SmaliClassName(m.group(Smali.FULL_CLASS_NAME))
                );
            }

            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                assertTrue(new SmaliImplementsToken.Tokenizer().tokenize(LineNumber.NONE, ".implements La/okhttp3/Interceptor;", null) != null);
            }
        }
    }
}


