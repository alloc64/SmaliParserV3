/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.Smali;
import com.alloc64.smaliv3.model.LineNumber;
import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

import java.util.regex.Matcher;

public class SmaliParamToken extends SmaliSingleRegisterToken
{
    public static final String TOKEN = ".param";

    private final String variableName;

    public SmaliParamToken(int lineNumber, String register, String variableName, String comment)
    {
        super(lineNumber, TOKEN, register);

        this.variableName = variableName;
        setComment(comment);
    }

    public String getVariableName()
    {
        return variableName;
    }

    @Override
    public String toString()
    {
        return toString(String.format("%s %s, \"%s\"", getName(), getRegister(), variableName));
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliParamToken>
    {
        @Override
        public SmaliParamToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            Matcher m = Smali.paramPattern.matcher(line);

            if(m.find())
                return new SmaliParamToken(lineNumber, m.group(Smali.REGISTER), m.group(Smali.VARIABLE_NAME), m.group(Smali.COMMENT));

            validate(TOKEN, line);

            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                SmaliParamToken param = new Tokenizer().tokenize(LineNumber.NONE, ".param p100, \"ctx\"    # Landroid/content/Context;", null);

                assertTrue(param.getRegister().equals("p100"));
                assertTrue(param.getVariableName().equals("ctx"));
                assertTrue(param.getComment().equals("Landroid/content/Context;"));

                param = new Tokenizer().tokenize(LineNumber.NONE, ".param p1, \"clazz\"    # Ljava/lang/String;", null);

                assertTrue(param.getRegister().equals("p1"));
                assertTrue(param.getVariableName().equals("clazz"));
                assertTrue(param.getComment().equals("Ljava/lang/String;"));
            }
        }
    }
}

