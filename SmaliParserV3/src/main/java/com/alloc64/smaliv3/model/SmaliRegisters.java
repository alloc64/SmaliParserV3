/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model;

import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

import java.util.Arrays;
import java.util.Collection;

public class SmaliRegisters extends SmaliObjectList<String>
{
    public SmaliRegisters()
    {
        super();
    }

    public SmaliRegisters(Collection<? extends String> collection)
    {
        super(collection);
    }

    @Override
    public String toString()
    {
        return String.join(", ", this);
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliRegisters>
    {
        @Override
        public SmaliRegisters tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            String[] splits = line.replace("{", "")
                    .replace("}", "")
                    .split(",");

            return new SmaliRegisters(Arrays.asList(splits));
        }
    }
}
