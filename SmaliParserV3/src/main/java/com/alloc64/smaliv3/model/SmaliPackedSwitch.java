/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model;

import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.model.token.SmaliPackedSwitchEndToken;
import com.alloc64.smaliv3.model.token.SmaliPackedSwitchToken;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizerMultiline;

import java.util.ArrayList;
import java.util.List;

/**
 *     .packed-switch 0x1
 *         :pswitch_44
 *         :pswitch_39
 *         :pswitch_30
 *     .end packed-switch
 */
public class SmaliPackedSwitch extends SmaliObject
{
    public static final String TOKEN = SmaliPackedSwitchToken.TOKEN;

    private SmaliPackedSwitchToken signature;

    private List<String> labelList = new ArrayList<>();

    public SmaliPackedSwitch(SmaliPackedSwitchToken signature)
    {
        this.signature = signature;
    }

    public SmaliPackedSwitchToken getSignature()
    {
        return signature;
    }

    public List<String> getLabelList()
    {
        return labelList;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(signature);
        sb.append("\n");

        for (int i = 0; i < labelList.size(); i++)
        {
            sb.append(TAB);
            sb.append(labelList.get(i));
            sb.append("\n");
        }

        sb.append(SmaliPackedSwitchEndToken.TOKEN);
        sb.append("\n\n");

        return sb.toString();
    }


    public static class Tokenizer extends SmaliTokenizerMultiline<SmaliPackedSwitch>
    {
        private SmaliPackedSwitchToken.Tokenizer startTokenizer = new SmaliPackedSwitchToken.Tokenizer();
        private SmaliPackedSwitchEndToken.Tokenizer endTokenizer = new SmaliPackedSwitchEndToken.Tokenizer();

        @Override
        public SmaliPackedSwitch tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            if (inProgress())
            {
                SmaliObject endToken = endTokenizer.tokenize(lineNumber, line, clazz);

                if (endToken != null)
                    return finish();

                currentItem.getLabelList().add(line);
            }
            else
            {
                SmaliPackedSwitchToken packedSwitchSignature = startTokenizer.tokenize(lineNumber, line, clazz);

                if (packedSwitchSignature == null) // tokenization error occured - lexers is in undefined state
                    return null;

                this.currentItem = new SmaliPackedSwitch(packedSwitchSignature);
            }

            return null;
        }

        public static class Test extends SmaliTest
        {

            @Override
            public void test() throws Exception
            {
                String lines =
                        "    .packed-switch 0x2\n" +
                        "        :pswitch_3e\n" +
                        "        :pswitch_3e\n" +
                        "    .end packed-switch";

                SmaliPackedSwitch packedSwitch = testTokenizer(new Tokenizer(), null, lines);

                assertTrue(packedSwitch != null);
                assertTrue(packedSwitch.getSignature().getCount() == 2);
                assertTrue(packedSwitch.getLabelList().size() == 2);
            }

            private SmaliPackedSwitch testTokenizer(SmaliPackedSwitch.Tokenizer tokenizer, SmaliClass clazz, String lines)
            {
                String splits[] = lines.split("\n");

                for (int i = 0; i < splits.length; i++)
                {
                    SmaliPackedSwitch packedSwitch = tokenizer.tokenize(i, splits[i].trim(), clazz);

                    if(packedSwitch != null)
                        return packedSwitch;
                }

                return null;
            }
        }
    }
}