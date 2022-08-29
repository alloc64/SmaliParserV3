/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.model.LineNumber;
import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.clazz.SmaliClassName;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

/**
 * .annotation system Ldalvik/annotation/MemberClasses;
 */
public class SmaliAnnotationToken extends SmaliToken
{
    public static final String TOKEN = ".annotation";

    private SmaliClassName className;
    private String type;

    public SmaliAnnotationToken(int lineNumber, String type, SmaliClassName className)
    {
        super(lineNumber, TOKEN);

        this.type = type;
        this.className = className;
    }

    public String getType()
    {
        return type;
    }

    public SmaliClassName getClassName()
    {
        return className;
    }

    @Override
    public String toString()
    {
        return toString(String.format("%s %s %s", getName(), type, className));
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliAnnotationToken>
    {
        @Override
        public SmaliAnnotationToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            if(line.startsWith(TOKEN))
            {
                String[] splits = line.split(" ");

                if (splits.length >= 3)
                {
                    String type = splits[1];
                    String className = splits[2];

                    return new SmaliAnnotationToken(lineNumber, type, new SmaliClassName(className));
                }

                validate(TOKEN, line);
            }


            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                assertTrue(new SmaliAnnotationToken.Tokenizer().tokenize(LineNumber.NONE, ".annotation system Ldalvik/annotation/MemberClasses;", null) != null);
            }
        }
    }
}

