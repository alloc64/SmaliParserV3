/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.model.SmaliRegisters;
import com.alloc64.smaliv3.model.opcode.Opcode;

public class SmaliOpcodeRegistersAwareToken extends SmaliOpcodeToken
{
    private SmaliRegisters registers;

    public SmaliOpcodeRegistersAwareToken(int lineNumber, Opcode opcode, SmaliRegisters registers)
    {
        super(lineNumber, opcode);

        if(registers == null)
            throw new IllegalArgumentException("registers is mandatory.");

        this.registers = registers;
    }

    protected String formatRegisters(SmaliRegisters registers)
    {
        return registers.toString();
    }

    public SmaliRegisters getRegisters()
    {
        return registers;
    }

    @Override
    public String toString()
    {
        return String.format("%s %s", getOpcode().name, formatRegisters(registers));
    }
}
