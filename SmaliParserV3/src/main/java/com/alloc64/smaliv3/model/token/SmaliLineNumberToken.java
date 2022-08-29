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
 * .line 29
 */
public class SmaliLineNumberToken extends SmaliToken
{
    public static final String TOKEN = ".line";
    private final int lineNumberInCode;

    public SmaliLineNumberToken(int lineNumber, int lineNumberInCode)
    {
        super(lineNumber, TOKEN);

        this.lineNumberInCode = lineNumberInCode;
    }

    public int getLineNumberInCode()
    {
        return lineNumberInCode;
    }

    @Override
    public String toString()
    {
        return toString(String.format("%s %d", getName(), lineNumberInCode));
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliLineNumberToken>
    {
        @Override
        public SmaliLineNumberToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            int start = line.indexOf(TOKEN);

            if(start != -1)
            {
                start += TOKEN.length() + 1; // space

                StringBuilder sb = new StringBuilder();

                int i = 0;
                for(i = start; i < line.length(); i++)
                {
                    char c = line.charAt(i);

                    if(!Character.isDigit(c))
                        break;

                    sb.append(c);
                }

                SmaliLineNumberToken lineNumberToken = new SmaliLineNumberToken(lineNumber, Integer.valueOf(sb.toString()));
                lineNumberToken.setComment(line.substring(i));

                return lineNumberToken;
            }

            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                assertTrue(new SmaliLineNumberToken.Tokenizer().tokenize(LineNumber.NONE, ".line 34892 # TEST T4444", null).getLineNumberInCode() == 34892);
            }
        }
    }
}
