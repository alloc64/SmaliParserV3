/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

/**
 * .local v2, "handler":Landroid/os/Handler;
 .end local v2    # "handler":Landroid/os/Handler;
 */
public abstract class SmaliSingleRegisterToken extends SmaliToken
{
    private String register;

    protected SmaliSingleRegisterToken(int lineNumber, String name, String register)
    {
        super(lineNumber, name);

        this.register = register;
    }

    public String getRegister()
    {
        return register;
    }
}
