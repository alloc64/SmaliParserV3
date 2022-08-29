/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.tokenizer;

import com.alloc64.smaliv3.model.SmaliObject;
import com.alloc64.smaliv3.model.SmaliRegisters;
import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.opcode.Opcode;
import com.alloc64.smaliv3.model.opcode.ReferenceType;
import com.alloc64.smaliv3.model.token.SmaliCatchAllToken;
import com.alloc64.smaliv3.model.token.SmaliCatchToken;
import com.alloc64.smaliv3.model.token.SmaliFieldSignatureToken;
import com.alloc64.smaliv3.model.token.SmaliLabelToken;
import com.alloc64.smaliv3.model.token.SmaliLineNumberToken;
import com.alloc64.smaliv3.model.token.SmaliLocalEndToken;
import com.alloc64.smaliv3.model.token.SmaliLocalToken;
import com.alloc64.smaliv3.model.token.SmaliMethodSignatureToken;
import com.alloc64.smaliv3.model.token.SmaliOpcodeConstructorOrConstantToken;
import com.alloc64.smaliv3.model.token.SmaliOpcodeFieldAwareToken;
import com.alloc64.smaliv3.model.token.SmaliOpcodeMethodAwareToken;
import com.alloc64.smaliv3.model.token.SmaliOpcodeRegistersAwareToken;
import com.alloc64.smaliv3.model.token.SmaliOpcodeToken;
import com.alloc64.smaliv3.model.token.SmaliParamToken;
import com.alloc64.smaliv3.model.token.SmaliRestartLocalToken;
import com.alloc64.smaliv3.model.token.SmaliToken;
import com.alloc64.smaliv3.model.token.SmaliUnknownToken;

import org.apache.commons.lang3.StringUtils;

import java.util.LinkedHashMap;
import java.util.Map;

public class SmaliInstructionTokenizer extends SmaliTokenizer<SmaliObject>
{
    private final Map<String, SmaliTokenizer<? extends SmaliToken>> tokenizers = new LinkedHashMap<>();

    private SmaliRegisters.Tokenizer registersTokenizer = new SmaliRegisters.Tokenizer();
    private SmaliFieldSignatureToken.Tokenizer fieldSignatureTokenizer = new SmaliFieldSignatureToken.Tokenizer();
    private SmaliMethodSignatureToken.Tokenizer methodSignatureToken = new SmaliMethodSignatureToken.Tokenizer();
    private SmaliLabelToken.Tokenizer labelTokenizer = new SmaliLabelToken.Tokenizer();

    public SmaliInstructionTokenizer()
    {
        addTokenizer(SmaliParamToken.TOKEN, new SmaliParamToken.Tokenizer());
        addTokenizer(SmaliLocalToken.TOKEN, new SmaliLocalToken.Tokenizer());
        addTokenizer(SmaliLocalEndToken.TOKEN, new SmaliLocalEndToken.Tokenizer());
        addTokenizer(SmaliRestartLocalToken.TOKEN, new SmaliRestartLocalToken.Tokenizer());

        addTokenizer(SmaliCatchToken.TOKEN, new SmaliCatchToken.Tokenizer());
        addTokenizer(SmaliCatchAllToken.TOKEN, new SmaliCatchAllToken.Tokenizer());
        addTokenizer(SmaliLineNumberToken.TOKEN, new SmaliLineNumberToken.Tokenizer());
    }

    private void addTokenizer(String token, SmaliTokenizer<? extends SmaliToken> tokenizer)
    {
        tokenizers.put(token, tokenizer);
    }

    @Override
    public SmaliObject tokenize(int lineNumber, String line, SmaliClass clazz)
    {
        String token = token(line);

        if (token != null)
        {
            if(token.charAt(0) == ':')
            {
                SmaliToken result = labelTokenizer.tokenize(lineNumber, line, clazz);

                if(result != null)
                    return result;
            }

            Opcode opcode = Opcode.from(token);

            if(opcode != null)
            {
                return tokenizeOpcode(lineNumber, opcode, line, clazz);
            }

            SmaliTokenizer tokenizer = tokenizers.get(token);

            if (tokenizer != null)
            {
                SmaliToken result = (SmaliToken) tokenizer.tokenize(lineNumber, line, clazz);

                if(result != null)
                    return result;
            }
        }

        return new SmaliUnknownToken(lineNumber, line);
    }

    private SmaliToken tokenizeOpcode(int lineNumber, Opcode opcode, String line, SmaliClass clazz)
    {
        RegisterResult<SmaliRegisters> registersResult = resolveRegisters(lineNumber, opcode, line, clazz);

        SmaliRegisters registers = null;
        String value = null;

        if(registersResult != null)
        {
            registers = registersResult.getResult();
            value = registersResult.getValue();
        }

        if(registers == null)
            registers = new SmaliRegisters();

        switch (opcode.referenceType)
        {
            case ReferenceType.FIELD:
                SmaliFieldSignatureToken fieldSignature = fieldSignatureTokenizer.tokenize(lineNumber, value, clazz);

                return new SmaliOpcodeFieldAwareToken(lineNumber, opcode, registers, fieldSignature);

            case ReferenceType.METHOD:
                SmaliMethodSignatureToken methodSignature = methodSignatureToken.tokenize(lineNumber, value, clazz);

                return new SmaliOpcodeMethodAwareToken(lineNumber, opcode, registers, methodSignature);
        }

        if(registers.size() > 0)
        {
            if (StringUtils.isEmpty(value))
            {
                return new SmaliOpcodeRegistersAwareToken(lineNumber, opcode, registers);
            }
            else
            {
                return new SmaliOpcodeConstructorOrConstantToken(lineNumber, opcode, registers, value);
            }
        }

        return new SmaliOpcodeToken(lineNumber, opcode);
    }

    private RegisterResult<SmaliRegisters> resolveRegisters(int lineNumber, Opcode opcode, String line, SmaliClass clazz)
    {
        RegisterResult<SmaliRegisters> result = resolveConstantRegisters(lineNumber, opcode, line, clazz);

        if(result != null)
            return result;

        StringBuilder sb = new StringBuilder();
        for(int offset = opcode.name.length()+1; offset < line.length(); offset++)
        {
            char c = line.charAt(offset);

            if(c != '{' && c != '}' && c != 'L' && !Character.isWhitespace(c))
                sb.append(c);

            boolean end = offset >= line.length()-1;

            if(c == 'L' || end)
            {
                line = end ? "" : line.substring(offset);

                return new RegisterResult<>(line, registersTokenizer.tokenize(lineNumber, sb.toString(), clazz));
            }
        }

        return null;
    }

    private RegisterResult<SmaliRegisters> resolveConstantRegisters(int lineNumber, Opcode opcode, String line, SmaliClass clazz)
    {
        if(!opcode.name.contains("const"))
            return null;

        int lastSpaceIdx = line.lastIndexOf(" ");
        String registers = line.substring(opcode.name.length()+1, lastSpaceIdx);
        line = line.substring(lastSpaceIdx+1);

        return new RegisterResult<>(line, registersTokenizer.tokenize(lineNumber, registers, clazz));
    }

    private class RegisterResult<T>
    {
        private String value;
        private T result;

        public RegisterResult(String value, T result)
        {
            this.value = value;
            this.result = result;
        }

        public String getValue()
        {
            return value;
        }

        public T getResult()
        {
            return result;
        }
    }
}
