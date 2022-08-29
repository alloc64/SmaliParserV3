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
 * .array-data 4
 */
public class SmaliArrayToken extends SmaliToken
{
    public static final String TOKEN = ".array-data";
    private final int byteSize;

    public SmaliArrayToken(int lineNumber, int byteSize)
    {
        super(lineNumber, TOKEN);
        this.byteSize = byteSize;
    }

    public int getByteSize()
    {
        return byteSize;
    }

    @Override
    public String toString()
    {
        return toString(String.format("%s %d", getName(), getByteSize()));
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliArrayToken>
    {
        @Override
        public SmaliArrayToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            Matcher m = Smali.arrayDataPattern.matcher(line);

            if (m.find())
                return new SmaliArrayToken(lineNumber, Integer.valueOf(m.group(Smali.ARRAY_BYTE_SIZE)));

            validate(TOKEN, line);

            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                assertTrue(new SmaliArrayToken.Tokenizer().tokenize(LineNumber.NONE, ".array-data 1", null).toString().equals(".array-data 1"));
            }
        }
    }
}


