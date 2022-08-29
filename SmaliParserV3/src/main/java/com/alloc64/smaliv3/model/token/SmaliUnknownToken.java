/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

public class SmaliUnknownToken extends SmaliToken
{
    public SmaliUnknownToken(int lineNumber, String text)
    {
        super(lineNumber, text);

        System.out.println("Unknown token (" + text + ")");
    }
}


