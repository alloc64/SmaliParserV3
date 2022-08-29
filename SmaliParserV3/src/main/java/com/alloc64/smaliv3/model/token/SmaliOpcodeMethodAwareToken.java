/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.model.SmaliRegisters;
import com.alloc64.smaliv3.model.opcode.Opcode;

public class SmaliOpcodeMethodAwareToken extends SmaliOpcodeRegistersAwareToken
{
    private final SmaliMethodSignatureToken signature;

    public SmaliOpcodeMethodAwareToken(int lineNumber, Opcode opcode, SmaliRegisters registers, SmaliMethodSignatureToken signature)
    {
        super(lineNumber, opcode, registers);

        this.signature = signature;
    }

    public SmaliMethodSignatureToken getSignature()
    {
        return signature;
    }

    @Override
    public String toString()
    {
        return toString(String.format("%s {%s}, %s", getOpcode().name, getRegisters(), signature));
    }
}

