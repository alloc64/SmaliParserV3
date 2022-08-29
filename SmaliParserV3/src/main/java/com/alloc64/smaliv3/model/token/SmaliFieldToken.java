/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.Smali;
import com.alloc64.smaliv3.model.LineNumber;
import com.alloc64.smaliv3.model.SmaliAccessModifier;
import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.clazz.SmaliClassName;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

import org.apache.commons.lang3.StringUtils;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * .field private static zza:Ltest/pub/IRLog;
 * .field private zzb:Z
 * .field private zzc:Ltest/zzc;
 */
public class SmaliFieldToken extends SmaliToken
{
    public static final String TOKEN = ".field";

    private final SmaliAccessModifier accessModifier;
    private final String variableName;
    private final SmaliClassName variableType;

    public SmaliFieldToken(int lineNumber, SmaliAccessModifier accessModifier, String variableName, SmaliClassName variableType)
    {
        super(lineNumber, TOKEN);

        if(accessModifier == null)
            throw new IllegalArgumentException("accessModifier is mandatory");

        if(StringUtils.isEmpty(variableName))
            throw new IllegalArgumentException("variableName is mandatory");

        if(variableType == null)
            throw new IllegalArgumentException("variableType is mandatory");

        this.accessModifier = accessModifier;
        this.variableName = variableName;
        this.variableType = variableType;
    }

    @Override
    public String toString()
    {
        return toString(String.format("%s %s %s:%s", getName(), accessModifier, variableName, variableType));
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliFieldToken>
    {
        private static final Pattern fieldPattern = Pattern.compile(".field\\s((?<" + Smali.ACCESS_MODIFIER + ">...+)\\s)?(?<" + Smali.FIELD_NAME + ">[^\\s]+):(?<" + Smali.TYPE + ">[^\\s]+)");

        @Override
        public SmaliFieldToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            Matcher m = fieldPattern.matcher(line);

            if(m.find())
            {
                return new SmaliFieldToken(
                        lineNumber,
                        new SmaliAccessModifier(m.group(Smali.ACCESS_MODIFIER)),
                        m.group(Smali.FIELD_NAME),
                        new SmaliClassName(m.group(Smali.TYPE))
                );
            }

            validate(TOKEN, line);

            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                assertTrue(new SmaliFieldToken.Tokenizer().tokenize(LineNumber.NONE, ".field limit:I\n", null) != null);
                assertTrue(new SmaliFieldToken.Tokenizer().tokenize(LineNumber.NONE, ".field private static zza:Ltest/pub/Log;", null) != null);
                assertTrue(new SmaliFieldToken.Tokenizer().tokenize(LineNumber.NONE, ".field private zzb:Z", null) != null);
                assertTrue(new SmaliFieldToken.Tokenizer().tokenize(LineNumber.NONE, ".field private zzc:Ltest/zzc;", null) != null);
            }
        }
    }
}

