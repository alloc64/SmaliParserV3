/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.Smali;
import com.alloc64.smaliv3.model.LineNumber;
import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

import java.util.regex.Matcher;

/**
 * .restart local v4    # "fileNameLength":I
 */
public class SmaliRestartLocalToken extends SmaliSingleRegisterToken
{
    public static final String TOKEN = ".restart local";

    public SmaliRestartLocalToken(int lineNumber, String register, String comment)
    {
        super(lineNumber, TOKEN, register);

        setComment(comment);
    }

    @Override
    public String toString()
    {
        return toString(String.format("%s %s ", getName(), getRegister()));
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliRestartLocalToken>
    {
        @Override
        public SmaliRestartLocalToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            Matcher m = Smali.restartLocalPattern.matcher(line);

            if (m.find())
            {
                return new SmaliRestartLocalToken(
                        lineNumber,
                        m.group(Smali.REGISTER),
                        m.group(Smali.COMMENT)
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
                SmaliLocalEndToken local = new SmaliLocalEndToken.Tokenizer().tokenize(LineNumber.NONE, ".restart local v2    # \"handler\":Landroid/os/Handler;", null);

                assertTrue(local.getRegister().equals("v2"));
                assertTrue(local.getComment().equals("\"handler\":Landroid/os/Handler;"));
            }
        }
    }
}
