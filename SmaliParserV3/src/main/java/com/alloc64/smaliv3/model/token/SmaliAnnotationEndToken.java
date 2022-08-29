/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.model.LineNumber;
import com.alloc64.smaliv3.model.SmaliObject;
import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

public class SmaliAnnotationEndToken extends SmaliToken
{
    public static final String TOKEN = ".end annotation";

    public SmaliAnnotationEndToken(int lineNumber)
    {
        super(lineNumber, TOKEN);
    }

    public static class Tokenizer extends SmaliTokenizer
    {
        @Override
        public SmaliObject tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            if(line.startsWith(TOKEN))
                return new SmaliAnnotationEndToken(lineNumber);

            return null;
        }
    }

    public static class Test extends SmaliTest
    {
        @Override
        public void test() throws Exception
        {
            assertTrue(new SmaliAnnotationEndToken(LineNumber.NONE).toString().equals(TOKEN));
        }
    }
}
