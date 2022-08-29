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
 * .packed-switch 0x1
 */
public class SmaliPackedSwitchToken extends SmaliToken
{
    public static String TOKEN = ".packed-switch";
    private final int count;

    public SmaliPackedSwitchToken(int lineNumber, int count)
    {
        super(lineNumber, TOKEN);

        this.count = count;
    }

    public int getCount()
    {
        return count;
    }

    @Override
    public String toString()
    {
        return toString(String.format("%s %s", getName(), Integer.toHexString(count)));
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliPackedSwitchToken>
    {
        @Override
        public SmaliPackedSwitchToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            Matcher m = Smali.packedSwitchPattern.matcher(line);

            if(m.find())
                return new SmaliPackedSwitchToken(lineNumber, Integer.decode(m.group(Smali.PACKED_SWITCH_COUNT)));

            validate(TOKEN, line);

            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                SmaliPackedSwitchToken param = new SmaliPackedSwitchToken.Tokenizer().tokenize(LineNumber.NONE, ".packed-switch 0x2", null);

                assertTrue(param != null);
                assertTrue(param.getCount() == 2);
            }
        }
    }
}

