/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.model.opcode.Opcode;

public class SmaliOpcodeToken extends SmaliToken
{
    private Opcode opcode;

    public SmaliOpcodeToken(int lineNumber, Opcode opcode)
    {
        super(lineNumber, opcode == null ? null : opcode.name);

        if(opcode == null)
            throw new IllegalArgumentException("opcode is mandatory.");

        this.opcode = opcode;
    }

    public Opcode getOpcode()
    {
        return opcode;
    }

    @Override
    public String toString()
    {
        return opcode.name;
    }
}



