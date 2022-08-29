/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.tokenizer;

import com.alloc64.smaliv3.model.SmaliObject;
import com.alloc64.smaliv3.model.clazz.SmaliClass;

public abstract class SmaliTokenizer<T extends SmaliObject>
{
    public abstract T tokenize(int lineNumber, String line, SmaliClass clazz);

    protected void validate(String token, String line)
    {
        if(line.startsWith(token))
            throw new IllegalStateException(token + " signature expected, but " + line + " found");
    }

    protected String token(String line)
    {
        String token = line;

        int spaceIndex = line.indexOf(" ");

        if (spaceIndex != -1)
        {
            token = line
                    .substring(0, spaceIndex)
                    .trim();

            if (token.equals(".end") || token.equals(".restart"))
            {
                String[] arr = line.split(" ");

                if (arr.length >= 2)
                    return String.format("%s %s", arr[0], arr[1]);
            }
        }

        return token;
    }

}

