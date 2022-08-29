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
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

import java.util.regex.Matcher;

public class SmaliCatchAllToken extends SmaliToken
{
    public static final String TOKEN = ".catchall";

    private final SmaliExceptionLabels exceptionLabels;

    public SmaliCatchAllToken(int lineNumber, SmaliExceptionLabels exceptionLabels)
    {
        super(lineNumber, TOKEN);

        this.exceptionLabels = exceptionLabels;
    }

    public SmaliExceptionLabels getExceptionLabels()
    {
        return exceptionLabels;
    }

    @Override
    public String toString()
    {
        return toString(String.format("%s %s", super.toString(), exceptionLabels));
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliCatchAllToken>
    {
        @Override
        public SmaliCatchAllToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            Matcher m = Smali.catchAllPattern.matcher(line);

            if (m.find())
            {
                return new SmaliCatchAllToken(
                        lineNumber,
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
                SmaliCatchAllToken catchToken = new SmaliCatchAllToken.Tokenizer().tokenize(LineNumber.NONE, ".catchall {:try_start_0 .. :try_end_6} :catch_7", null);

                SmaliExceptionLabels exceptionLabels = catchToken.getExceptionLabels();

                assertTrue(exceptionLabels.getTryStart().getName().equals("try_start_0"));
                assertTrue(exceptionLabels.getTryEnd().getName().equals("try_end_6"));
                assertTrue(exceptionLabels.getCatchEnd().getName().equals("catch_7"));
                assertTrue(exceptionLabels.toString().equals("{:try_start_0 .. :try_end_6} :catch_7"));
            }
        }
    }
}
