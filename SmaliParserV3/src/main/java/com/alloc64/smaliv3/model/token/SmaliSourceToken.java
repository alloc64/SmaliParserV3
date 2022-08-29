/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.Smali;
import com.alloc64.smaliv3.model.LineNumber;
import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;

public class SmaliSourceToken extends SmaliToken
{
    public static final String TOKEN = ".source";

    private String fileName;

    public SmaliSourceToken(int lineNumber, String fileName)
    {
        super(lineNumber, TOKEN);

        if(StringUtils.isEmpty(fileName))
            throw new IllegalArgumentException("fileName is mandatory");

        this.fileName = fileName;
    }

    public String getFileName()
    {
        return fileName;
    }

    @Override
    public String toString()
    {
        return toString(String.format("%s \"%s\"", getName(), fileName));
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliSourceToken>
    {
        @Override
        public SmaliSourceToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            Matcher m = Smali.sourcePattern.matcher(line);

            if(m.find())
                return new SmaliSourceToken(lineNumber, m.group(Smali.FILE_NAME));

            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                SmaliSourceToken sourceToken = new Tokenizer().tokenize(LineNumber.NONE, ".source \"BridgeInterceptor.java\"", null);

                assertTrue(sourceToken != null);
                assertTrue(sourceToken.toString().equals(".source \"BridgeInterceptor.java\""));
            }
        }
    }
}
