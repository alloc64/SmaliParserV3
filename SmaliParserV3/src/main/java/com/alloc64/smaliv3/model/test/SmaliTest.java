/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.test;

public abstract class SmaliTest
{
    public abstract void test() throws Exception;

    public void assertTrue(boolean val) throws Exception
    {
        if(!val)
            throw new Exception("Test assertion failed.");
    }
}
