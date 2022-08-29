/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

public class SmaliSparseSwitchEndToken extends SmaliToken
{
    public static String TOKEN = ".end sparse-switch";

    public SmaliSparseSwitchEndToken(int lineNumber)
    {
        super(lineNumber, TOKEN);
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliSparseSwitchEndToken>
    {
        @Override
        public SmaliSparseSwitchEndToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            if(line.startsWith(TOKEN))
                return new SmaliSparseSwitchEndToken(lineNumber);

            return null;
        }
    }
}
