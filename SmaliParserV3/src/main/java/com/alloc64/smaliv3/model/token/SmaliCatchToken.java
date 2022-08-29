/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.Smali;
import com.alloc64.smaliv3.model.LineNumber;
import com.alloc64.smaliv3.model.SmaliExceptionLabels;
import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.clazz.SmaliClassName;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

import java.util.regex.Matcher;

/**
 * .catch Ljava/lang/Exception; {:try_start_9 .. :try_end_c} :catch_d
 * .catchall {:try_start_3 .. :try_end_12} :catchall_1c
 */
public class SmaliCatchToken extends SmaliToken
{
    public static final String TOKEN = ".catch";

    private final SmaliClassName exceptionType;
    private final SmaliExceptionLabels exceptionLabels;

    public SmaliCatchToken(int lineNumber, SmaliClassName exceptionType, SmaliExceptionLabels exceptionLabels)
    {
        super(lineNumber, TOKEN);

        this.exceptionType = exceptionType;
        this.exceptionLabels = exceptionLabels;
    }

    public SmaliClassName getExceptionType()
    {
        return exceptionType;
    }

    public SmaliExceptionLabels getExceptionLabels()
    {
        return exceptionLabels;
    }

    @Override
    public String toString()
    {
        return toString(String.format("%s %s %s", getName(), exceptionType, exceptionLabels));
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliCatchToken>
    {
        @Override
        public SmaliCatchToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            Matcher m = Smali.catchPattern.matcher(line);

            if (m.find())
            {
                return new SmaliCatchToken(
                        lineNumber,
                        new SmaliClassName(m.group(Smali.TYPE)),
                        new SmaliExceptionLabels(
                                new SmaliLabelToken(lineNumber, m.group(Smali.TRY_START_LABEL)),
                                new SmaliLabelToken(lineNumber, m.group(Smali.TRY_END_LABEL)),
                                new SmaliLabelToken(lineNumber, m.group(Smali.CATCH_LABEL))
                        )
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
                SmaliCatchToken catchToken = new SmaliCatchToken.Tokenizer().tokenize(LineNumber.NONE, ".catch Ljava/lang/Exception; {:try_start_0 .. :try_end_6} :catch_7", null);

                assertTrue(catchToken.getExceptionType().toString().equals("Ljava/lang/Exception;"));

                SmaliExceptionLabels exceptionLabels = catchToken.getExceptionLabels();

                assertTrue(exceptionLabels.getTryStart().getName().equals("try_start_0"));
                assertTrue(exceptionLabels.getTryEnd().getName().equals("try_end_6"));
                assertTrue(exceptionLabels.getCatchEnd().getName().equals("catch_7"));
                assertTrue(exceptionLabels.toString().equals("{:try_start_0 .. :try_end_6} :catch_7"));
            }
        }
    }
}
