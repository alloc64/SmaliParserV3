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
 * :cond_8
 * :try_start_9
 */
public class SmaliLabelToken extends SmaliToken
{
    public SmaliLabelToken(int lineNumber, String name)
    {
        super(lineNumber, name);
    }

    @Override
    public String toString()
    {
        return toString(String.format(":%s", getName()));
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliLabelToken>
    {
        @Override
        public SmaliLabelToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            int start = line.indexOf(":");

            if(start != -1)
            {
                start += 1;

                int i = 0;
                for(i = start; i < line.length(); i++)
                {
                    char c = line.charAt(i);

                    if(Character.isSpaceChar(c))
                        break;
                }

                SmaliLabelToken token = new SmaliLabelToken(lineNumber, line.substring(start, i));
                token.setComment(line.substring(i));

                return token;
            }

            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                SmaliLabelToken label = new Tokenizer().tokenize(LineNumber.NONE, ":try_end_6 # TEST comment", null);

                assertTrue(label.getName().equals("try_end_6"));
            }
        }
    }
}

