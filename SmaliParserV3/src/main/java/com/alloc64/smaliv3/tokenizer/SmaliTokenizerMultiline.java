/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.tokenizer;

import com.alloc64.smaliv3.model.SmaliObject;

public abstract class SmaliTokenizerMultiline<T extends SmaliObject> extends SmaliTokenizer<T>
{
    protected T currentItem;

    public boolean inProgress()
    {
        return currentItem != null;
    }

    protected T finish()
    {
        T annotation = this.currentItem;
        this.currentItem = null;

        return annotation;
    }

}
