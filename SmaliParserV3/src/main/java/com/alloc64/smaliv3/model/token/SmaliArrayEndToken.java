/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

/**
 * .end array-data
 */
public class SmaliArrayEndToken extends SmaliToken
{
    public static final String TOKEN = ".end array-data";

    public SmaliArrayEndToken(int lineNumber)
    {
        super(lineNumber, TOKEN);
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliArrayEndToken>
    {
        @Override
        public SmaliArrayEndToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            if(line.startsWith(TOKEN))
                return new SmaliArrayEndToken(lineNumber);

            return null;
        }
    }
}
