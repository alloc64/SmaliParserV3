/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.model.SmaliObject;

import org.apache.commons.lang3.StringUtils;

public abstract class SmaliToken extends SmaliObject
{
    private int lineNumber;
    private String name;
    private String comment;

    public SmaliToken(int lineNumber, String name)
    {
        this.lineNumber = lineNumber;
        this.name = name;
    }

    public int getLineNumber()
    {
        return lineNumber;
    }

    public String getName()
    {
        return name;
    }

    public String getComment()
    {
        return comment;
    }

    public void setComment(String comment)
    {
        if(comment != null)
        {
            comment = comment.trim();

            if(comment.startsWith("#"))
                comment = comment.substring(1).trim();

            this.comment = comment;
        }
    }

    @Override
    public String toString()
    {
        return toString(name);
    }

    protected String toString(String value)
    {
        if(StringUtils.isEmpty(comment))
            return value;

        return String.format("%s%s# %s", value, TAB, comment);
    }
}


