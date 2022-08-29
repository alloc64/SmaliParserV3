/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model;


import com.alloc64.smaliv3.model.token.SmaliLabelToken;

/**
 * {:try_start_9 .. :try_end_c} :catch_d
 */
public class SmaliExceptionLabels
{
    private SmaliLabelToken tryStart;
    private SmaliLabelToken tryEnd;
    private SmaliLabelToken catchEnd;

    public SmaliExceptionLabels(SmaliLabelToken tryStart, SmaliLabelToken tryEnd, SmaliLabelToken catchEnd)
    {
        if(tryStart == null)
            throw new IllegalArgumentException("tryStart is mandatory.");

        if(tryEnd == null)
            throw new IllegalArgumentException("tryEnd is mandatory.");

        if(catchEnd == null)
            throw new IllegalArgumentException("catchEnd is mandatory.");

        this.tryStart = tryStart;
        this.tryEnd = tryEnd;
        this.catchEnd = catchEnd;
    }

    public SmaliLabelToken getTryStart()
    {
        return tryStart;
    }

    public SmaliLabelToken getTryEnd()
    {
        return tryEnd;
    }

    public SmaliLabelToken getCatchEnd()
    {
        return catchEnd;
    }

    @Override
    public String toString()
    {
        return String.format("{%s .. %s} %s", tryStart, tryEnd, catchEnd);
    }
}
