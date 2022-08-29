/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.model.LineNumber;
import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

/**
 * .sparse-switch
 */
public class SmaliSparseSwitchToken extends SmaliToken
{
    public static String TOKEN = ".sparse-switch";

    public SmaliSparseSwitchToken(int lineNumber)
    {
        super(lineNumber, TOKEN);
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliSparseSwitchToken>
    {
        @Override
        public SmaliSparseSwitchToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            if(line.startsWith(TOKEN))
                return new SmaliSparseSwitchToken(lineNumber);

            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                SmaliPackedSwitchToken param = new SmaliPackedSwitchToken.Tokenizer().tokenize(LineNumber.NONE, ".sparse-switch", null);

                assertTrue(param != null);
            }
        }
    }
}

