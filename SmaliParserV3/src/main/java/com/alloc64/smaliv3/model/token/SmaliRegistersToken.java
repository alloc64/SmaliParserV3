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
import java.util.regex.Pattern;

/**
 * .registers 4
 */
public class SmaliRegistersToken extends SmaliToken
{
    public static final String TOKEN = ".registers";
    private int registerCount;

    public SmaliRegistersToken(int lineNumber, int registerCount)
    {
        super(lineNumber, TOKEN);

        this.registerCount = registerCount;
    }

    public int getRegisterCount()
    {
        return registerCount;
    }

    public void setRegisterCount(int registerCount)
    {
        this.registerCount = registerCount;
    }

    @Override
    public String toString()
    {
        return toString(String.format("%s %d", getName(), registerCount));
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliRegistersToken>
    {
        private static final Pattern registersPattern = Pattern.compile("\\.registers\\s(?<" + Smali.REGISTER_COUNT + ">\\d+)");

        @Override
        public SmaliRegistersToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            Matcher m = registersPattern.matcher(line);

            if(m.find())
                return new SmaliRegistersToken(lineNumber, Integer.valueOf(m.group(Smali.REGISTER_COUNT)));

            validate(TOKEN, line);

            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                assertTrue(new SmaliRegistersToken.Tokenizer().tokenize(LineNumber.NONE, ".registers 5", null).toString().equals(".registers 5"));
            }
        }
    }
}

