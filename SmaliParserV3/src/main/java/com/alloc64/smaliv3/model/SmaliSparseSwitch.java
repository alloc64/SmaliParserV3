/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model;

import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.token.SmaliSparseSwitchEndToken;
import com.alloc64.smaliv3.model.token.SmaliSparseSwitchToken;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizerMultiline;

import java.util.ArrayList;
import java.util.List;

/*    
.sparse-switch
        0xc8 -> :sswitch_35
        0xcb -> :sswitch_35
        0xcc -> :sswitch_35
        0x12c -> :sswitch_35
        0x12d -> :sswitch_35
        0x12e -> :sswitch_9
        0x133 -> :sswitch_9
        0x134 -> :sswitch_35
        0x194 -> :sswitch_35
        0x195 -> :sswitch_35
        0x19a -> :sswitch_35
        0x19e -> :sswitch_35
        0x1f5 -> :sswitch_35
    .end sparse-switch
*/
public class SmaliSparseSwitch extends SmaliObject
{
    public static final String TOKEN = SmaliSparseSwitchToken.TOKEN;

    private SmaliSparseSwitchToken signature;

    private List<String> labelList = new ArrayList<>();

    public SmaliSparseSwitch(SmaliSparseSwitchToken signature)
    {
        this.signature = signature;
    }

    public SmaliSparseSwitchToken getSignature()
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

        sb.append(SmaliSparseSwitchEndToken.TOKEN);
        sb.append("\n\n");

        return sb.toString();
    }

    public static class Tokenizer extends SmaliTokenizerMultiline<SmaliSparseSwitch>
    {
        private SmaliSparseSwitchToken.Tokenizer startTokenizer = new SmaliSparseSwitchToken.Tokenizer();
        private SmaliSparseSwitchEndToken.Tokenizer endTokenizer = new SmaliSparseSwitchEndToken.Tokenizer();

        @Override
        public SmaliSparseSwitch tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            if (currentItem != null)
            {
                SmaliObject endToken = endTokenizer.tokenize(lineNumber, line, clazz);

                if (endToken != null)
                    return finish();

                currentItem.getLabelList().add(line);
            }
            else
            {
                SmaliSparseSwitchToken SparseSwitchSignature = startTokenizer.tokenize(lineNumber, line, clazz);

                if (SparseSwitchSignature == null) // tokenization error occured - lexers is in undefined state
                    return null;

                this.currentItem = new SmaliSparseSwitch(SparseSwitchSignature);
            }

            return null;
        }
    }
}
