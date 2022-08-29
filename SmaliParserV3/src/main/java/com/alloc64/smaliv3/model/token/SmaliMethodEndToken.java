/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

public class SmaliMethodEndToken extends SmaliToken
{
    public static final String TOKEN = ".end method";

    public SmaliMethodEndToken(int lineNumber)
    {
        super(lineNumber, TOKEN);
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliMethodEndToken>
    {
        @Override
        public SmaliMethodEndToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            if(line.startsWith(TOKEN))
                return new SmaliMethodEndToken(lineNumber);

            return null;
        }
    }
}