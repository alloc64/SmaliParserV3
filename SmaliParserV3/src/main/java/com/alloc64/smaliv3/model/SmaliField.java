/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model;

import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.model.token.SmaliFieldEndToken;
import com.alloc64.smaliv3.model.token.SmaliFieldToken;
import com.alloc64.smaliv3.model.token.SmaliSkipToken;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

import java.util.ArrayList;
import java.util.List;

public class SmaliField extends SmaliObject
{
    public static final String TOKEN = SmaliFieldToken.TOKEN;

    private SmaliFieldToken signature;
    private List<SmaliAnnotation> annotationList = new ArrayList<>();

    public SmaliField(SmaliFieldToken signature)
    {
        this.signature = signature;
    }

    public SmaliFieldToken getSignature()
    {
        return signature;
    }

    public List<SmaliAnnotation> getAnnotationList()
    {
        return annotationList;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(signature);
        sb.append("\n");

        if(annotationList.size() > 0)
        {
            for (SmaliAnnotation annotation : annotationList)
            {
                sb.append(TAB);
                sb.append(annotation);
                sb.append("\n");
            }

            sb.append(SmaliFieldEndToken.TOKEN);
            sb.append("\n");
        }

        return sb.toString();
    }

    /**
     * Mulitiline tokenizer not used because of SmaliSkipTokens
     */
    public static class Tokenizer extends SmaliTokenizer<SmaliObject>
    {
        private SmaliField currentField;

        private SmaliFieldToken.Tokenizer startTokenizer = new SmaliFieldToken.Tokenizer();
        private SmaliFieldEndToken.Tokenizer endTokenizer = new SmaliFieldEndToken.Tokenizer();

        private SmaliAnnotation.Tokenizer annotationTokenizer = new SmaliAnnotation.Tokenizer();

        public boolean inProgress()
        {
            return currentField != null;
        }

        @Override
        public SmaliObject tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            if (inProgress())
            {
                SmaliObject endToken = endTokenizer.tokenize(lineNumber, line, clazz);

                if (endToken != null)
                {
                    this.currentField = null;

                    return new SmaliSkipToken();
                }
                else
                {
                    SmaliAnnotation annotation = annotationTokenizer.tokenize(lineNumber, line, clazz);

                    if (annotation != null)
                    {
                        currentField.getAnnotationList().add(annotation);
                        return new SmaliSkipToken();
                    }

                    if (annotationTokenizer.inProgress())
                        return new SmaliSkipToken();

                    currentField = null;
                }
            }

            SmaliFieldToken fieldSignature = startTokenizer.tokenize(lineNumber, line, clazz);

            if (fieldSignature == null) // tokenization error occured - lexers is in undefined state
                return null;

            this.currentField = new SmaliField(fieldSignature);

            return currentField;
        }
    }

    public static class Test extends SmaliTest
    {
        @Override
        public void test() throws Exception
        {
            List<SmaliField> field = testTokenizer(new Tokenizer(), null,
                    ".field final data:[B\n" +
                            "\n" +
                            ".field limit:I\n" +
                            "\n" +
                            ".field next:La/okio/Segment;\n" +
                            "\n" +
                            ".field owner:Z\n" +
                            "\n" +
                            ".field pos:I\n" +
                            "\n" +
                            ".field prev:La/okio/Segment;\n" +
                            "\n" +
                            ".field shared:Z");

            assertTrue(field.size() == 7);

            field = testTokenizer(new Tokenizer(), null,
                    ".field protected hLen:I\n" +
                            ".field protected hLen2:I\n" +
                            ".field private static sCache:Ljava/util/HashMap;\n" +
                            "    .annotation system Ldalvik/annotation/Signature;\n" +
                            "        value = {\n" +
                            "            \"Ljava/util/HashMap<\",\n" +
                            "            \"Ljava/lang/String;\",\n" +
                            "            \"La/androidx/content/FileProvider$PathStrategy;\",\n" +
                            "            \">;\"\n" +
                            "        }\n" +
                            "    .end annotation\n" +
                            ".end field\n");

            assertTrue(field.size() == 3);

            field = testTokenizer(new Tokenizer(), null,
                    ".field public static final ISO_8859_1:Ljava/nio/charset/Charset;\n" +
                    "    .annotation runtime Ljava/lang/Deprecated;\n" +
                    "    .end annotation\n" +
                    ".end field\n" +
                    "\n" +
                    ".field public static final US_ASCII:Ljava/nio/charset/Charset;\n" +
                    "    .annotation runtime Ljava/lang/Deprecated;\n" +
                    "    .end annotation\n" +
                    ".end field\n" +
                    "\n" +
                    ".field public static final UTF_16:Ljava/nio/charset/Charset;\n" +
                    "    .annotation runtime Ljava/lang/Deprecated;\n" +
                    "    .end annotation\n" +
                    ".end field\n" +
                    "\n" +
                    ".field public static final UTF_16BE:Ljava/nio/charset/Charset;\n" +
                    "    .annotation runtime Ljava/lang/Deprecated;\n" +
                    "    .end annotation\n" +
                    ".end field\n" +
                    "\n" +
                    ".field public static final UTF_16LE:Ljava/nio/charset/Charset;\n" +
                    "    .annotation runtime Ljava/lang/Deprecated;\n" +
                    "    .end annotation\n" +
                    ".end field\n" +
                    "\n" +
                    ".field public static final UTF_8:Ljava/nio/charset/Charset;\n" +
                    "    .annotation runtime Ljava/lang/Deprecated;\n" +
                    "    .end annotation\n" +
                    ".end field\n");

            assertTrue(field.size() == 6);
        }

        private List<SmaliField> testTokenizer(SmaliField.Tokenizer tokenizer, SmaliClass clazz, String lines)
        {
            List<SmaliField> result = new ArrayList<>();

            String splits[] = lines.split("\n");

            for (int i = 0; i < splits.length; i++)
            {
                SmaliObject field = tokenizer.tokenize(i, splits[i].trim(), clazz);

                if (field instanceof SmaliField)
                    result.add((SmaliField) field);
            }

            return result;
        }
    }
}
