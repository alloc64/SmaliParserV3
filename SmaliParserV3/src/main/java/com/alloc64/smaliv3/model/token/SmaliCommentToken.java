/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

public class SmaliCommentToken extends SmaliToken
{
    public SmaliCommentToken(int lineNumber, String comment)
    {
        super(lineNumber, null);
        setComment(comment);
    }

    @Override
    public String toString()
    {
        return "# " + getComment();
    }
}
