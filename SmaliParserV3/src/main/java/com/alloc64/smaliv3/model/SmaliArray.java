/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model;

import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.model.token.SmaliArrayEndToken;
import com.alloc64.smaliv3.model.token.SmaliArrayToken;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizerMultiline;

import java.util.ArrayList;
import java.util.List;

public class SmaliArray extends SmaliObject
{
    public static final String TOKEN = SmaliArrayToken.TOKEN;

    private SmaliArrayToken signature;

    private List<String> data = new ArrayList<>();

    public SmaliArray(SmaliArrayToken signature)
    {
        this.signature = signature;
    }

    public List<String> getData()
    {
        return data;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(signature);
        sb.append("\n");

        for (int i = 0; i < data.size(); i++)
        {
            sb.append(TAB);
            sb.append(data.get(i));
            sb.append("\n");
        }

        sb.append(SmaliArrayEndToken.TOKEN);
        sb.append("\n\n");

        return sb.toString();
    }

    public static class Tokenizer extends SmaliTokenizerMultiline<SmaliArray>
    {
        private SmaliArrayToken.Tokenizer startTokenizer = new SmaliArrayToken.Tokenizer();
        private SmaliArrayEndToken.Tokenizer endTokenizer = new SmaliArrayEndToken.Tokenizer();

        @Override
        public SmaliArray tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            if (inProgress())
            {
                SmaliObject endToken = endTokenizer.tokenize(lineNumber, line, clazz);

                if (endToken != null)
                    return finish();

                currentItem.getData().add(line);
            }
            else
            {
                SmaliArrayToken arraySignature = startTokenizer.tokenize(lineNumber, line, clazz);

                if (arraySignature == null) // tokenization error occured - lexers is in undefined state
                    return null;

                this.currentItem = new SmaliArray(arraySignature);
            }

            return null;
        }

        public static class Test extends SmaliTest
        {

            @Override
            public void test() throws Exception
            {
                String lines = ".array-data 1\n" +
                        "        0x41t\n" +
                        "        0x42t\n" +
                        "        0x43t\n" +
                        "        0x44t\n" +
                        "        0x45t\n" +
                        "        0x46t\n" +
                        "        0x47t\n" +
                        "        0x48t\n" +
                        "        0x49t\n" +
                        "        0x4at\n" +
                        "        0x4bt\n" +
                        "        0x4ct\n" +
                        "        0x4dt\n" +
                        "        0x4et\n" +
                        "        0x4ft\n" +
                        "        0x50t\n" +
                        "        0x51t\n" +
                        "        0x52t\n" +
                        "        0x53t\n" +
                        "        0x54t\n" +
                        "        0x55t\n" +
                        "        0x56t\n" +
                        "        0x57t\n" +
                        "        0x58t\n" +
                        "        0x59t\n" +
                        "        0x5at\n" +
                        "        0x61t\n" +
                        "        0x62t\n" +
                        "        0x63t\n" +
                        "        0x64t\n" +
                        "        0x65t\n" +
                        "        0x66t\n" +
                        "        0x67t\n" +
                        "        0x68t\n" +
                        "        0x69t\n" +
                        "        0x6at\n" +
                        "        0x6bt\n" +
                        "        0x6ct\n" +
                        "        0x6dt\n" +
                        "        0x6et\n" +
                        "        0x6ft\n" +
                        "        0x70t\n" +
                        "        0x71t\n" +
                        "        0x72t\n" +
                        "        0x73t\n" +
                        "        0x74t\n" +
                        "        0x75t\n" +
                        "        0x76t\n" +
                        "        0x77t\n" +
                        "        0x78t\n" +
                        "        0x79t\n" +
                        "        0x7at\n" +
                        "        0x30t\n" +
                        "        0x31t\n" +
                        "        0x32t\n" +
                        "        0x33t\n" +
                        "        0x34t\n" +
                        "        0x35t\n" +
                        "        0x36t\n" +
                        "        0x37t\n" +
                        "        0x38t\n" +
                        "        0x39t\n" +
                        "        0x2bt\n" +
                        "        0x2ft\n" +
                        "    .end array-data";

                SmaliArray array = testTokenizer(new SmaliArray.Tokenizer(), lines);

                assertTrue(array != null);
                assertTrue(array.getData().size() == 64);
            }

            private SmaliArray testTokenizer(SmaliArray.Tokenizer tokenizer, String lines)
            {
                String splits[] = lines.split("\n");

                for (int i = 0; i < splits.length; i++)
                {
                    SmaliArray array = tokenizer.tokenize(i, splits[i].trim(), null);

                    if(array != null)
                        return array;
                }

                return null;
            }
        }
    }
}
