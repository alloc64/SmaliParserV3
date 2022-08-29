/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model;

import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.model.token.SmaliAnnotationEndToken;
import com.alloc64.smaliv3.model.token.SmaliAnnotationToken;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizerMultiline;

import java.util.ArrayList;
import java.util.List;

public class SmaliAnnotation extends SmaliObject
{
    public static final String TOKEN = SmaliAnnotationToken.TOKEN;

    private SmaliAnnotationToken signature;

    private List<String> tokenList = new ArrayList<>();

    public SmaliAnnotation(SmaliAnnotationToken signature)
    {
        this.signature = signature;
    }

    public List<String> getTokenList()
    {
        return tokenList;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(signature);
        sb.append("\n");

        for (int i = 0; i < tokenList.size(); i++)
        {
            sb.append(TAB);
            sb.append(tokenList.get(i));
            sb.append("\n");
        }

        sb.append(TAB);
        sb.append(SmaliAnnotationEndToken.TOKEN);

        return sb.toString();
    }

    public static class Tokenizer extends SmaliTokenizerMultiline<SmaliAnnotation>
    {
        private SmaliAnnotationToken.Tokenizer startTokenizer = new SmaliAnnotationToken.Tokenizer();
        private SmaliAnnotationEndToken.Tokenizer endTokenizer = new SmaliAnnotationEndToken.Tokenizer();

        @Override
        public SmaliAnnotation tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            if(inProgress())
            {
                SmaliObject endToken = endTokenizer.tokenize(lineNumber, line, clazz);

                if(endToken != null)
                    return finish();

                currentItem.getTokenList().add(line);
            }
            else
            {
                SmaliAnnotationToken annotationSignature = startTokenizer.tokenize(lineNumber, line, clazz);

                if(annotationSignature == null) // tokenization error occured - lexers is in undefined state
                    return null;

                this.currentItem = new SmaliAnnotation(annotationSignature);
            }

            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                Tokenizer tokenizer = new Tokenizer();

                SmaliAnnotation annotation = testTokenizer(tokenizer,
                        "    .annotation system Ldalvik/annotation/Signature;\n" +
                                "        value = {\n" +
                                "            \"(\",\n" +
                                "            \"Ljava/util/List<\",\n" +
                                "            \"La/okhttp3/Cookie;\",\n" +
                                "            \">;)\",\n" +
                                "            \"Ljava/lang/String;\"\n" +
                                "        }\n" +
                                "    .end annotation"
                );

                assertTrue(annotation != null);

                annotation = testTokenizer(tokenizer,
                        ".annotation system Ldalvik/annotation/Signature;\n" +
                                "    value = {\n" +
                                "        \"Ljava/util/AbstractList<\",\n" +
                                "        \"Ltest/pack/type/Value;\",\n" +
                                "        \">;\",\n" +
                                "        \"Ltest/pack/type/ArrayValue;\"\n" +
                                "    }\n" +
                                ".end annotation\n");

                assertTrue(annotation != null);
            }

            private SmaliAnnotation testTokenizer(Tokenizer tokenizer, String lines)
            {
                String splits[] = lines.split("\n");

                for (int i = 0; i < splits.length; i++)
                {
                    SmaliAnnotation annotation = tokenizer.tokenize(i, splits[i].trim(), null);

                    if(annotation != null)
                        return annotation;
                }

                return null;
            }
        }
    }
}
