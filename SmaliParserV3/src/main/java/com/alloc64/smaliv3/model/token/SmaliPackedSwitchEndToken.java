/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

public class SmaliPackedSwitchEndToken extends SmaliToken
{
    public static String TOKEN = ".end packed-switch";

    public SmaliPackedSwitchEndToken(int lineNumber)
    {
        super(lineNumber, TOKEN);
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliPackedSwitchEndToken>
    {
        @Override
        public SmaliPackedSwitchEndToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            if(line.startsWith(TOKEN))
                return new SmaliPackedSwitchEndToken(lineNumber);

            return null;
        }
    }
}

