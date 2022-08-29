/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.model.SmaliRegisters;
import com.alloc64.smaliv3.model.opcode.Opcode;

public class SmaliOpcodeConstructorOrConstantToken extends SmaliOpcodeRegistersAwareToken
{
    private final String value;

    public SmaliOpcodeConstructorOrConstantToken(int lineNumber, Opcode opcode, SmaliRegisters registers, String value)
    {
        super(lineNumber, opcode, registers);

        this.value = value;
    }

    public String getValue()
    {
        return value;
    }

    @Override
    public String toString()
    {
        return toString(String.format("%s, %s", super.toString(), value));
    }
}
