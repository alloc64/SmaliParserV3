/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.Smali;
import com.alloc64.smaliv3.model.LineNumber;
import com.alloc64.smaliv3.model.SmaliObject;
import com.alloc64.smaliv3.model.SmaliParameterClasses;
import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.clazz.SmaliClassName;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmaliMethodSignatureToken extends SmaliObject
{
    private SmaliClassName className;
    private String originalMethodName;
    private String methodName;
    private SmaliParameterClasses parameters;
    private SmaliClassName returnType;

    public SmaliMethodSignatureToken(SmaliClassName className,
                                     String methodName,
                                     SmaliParameterClasses parameters,
                                     SmaliClassName returnType)
    {
        this.className = className;
        this.methodName = methodName;
        this.originalMethodName = methodName;
        this.parameters = parameters;
        this.returnType = returnType;
    }

    public SmaliClassName getClassName()
    {
        return className;
    }

    public String getMethodName()
    {
        return methodName;
    }

    public String getOriginalMethodName()
    {
        return originalMethodName;
    }

    public SmaliParameterClasses getParameters()
    {
        return parameters;
    }

    public SmaliClassName getReturnType()
    {
        return returnType;
    }

    @Override
    public String toString()
    {
        return String.format("%s->%s(%s)%s",
                className.getFullClassName(),
                methodName,
                parameters,
                returnType
        );
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliMethodSignatureToken>
    {
        private static final Pattern methodSignaturePattern = Pattern.compile(
                "((?<" + Smali.FULL_CLASS_NAME + ">L...+;)->)?(?<" + Smali.METHOD_NAME + ">\\S+)\\((?<" + Smali.PARAMTERS + ">\\S+)?\\)(?<" + Smali.RETURN_TYPE + ">\\S+)"
        );

        @Override
        public SmaliMethodSignatureToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            Matcher m = methodSignaturePattern.matcher(line);

            if(m.find())
            {
                String fullClassName = m.group(Smali.FULL_CLASS_NAME);

                return new SmaliMethodSignatureToken(
                        fullClassName == null ? null : new SmaliClassName(fullClassName),
                        m.group(Smali.METHOD_NAME),
                        new SmaliParameterClasses(m.group(Smali.PARAMTERS)),
                        new SmaliClassName(m.group(Smali.RETURN_TYPE))
                );
            }

            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                testSignature(
                        "Landroid/util/Log;->zzb([Ljava/lang/String;)Z",
                        new SmaliClassName("[Ljava/lang/String;")
                );

                testSignature(
                        "Landroid/util/Log;->zzb([[[BII)Z",
                        new SmaliClassName("[[[B"),
                        new SmaliClassName("I"),
                        new SmaliClassName("I")
                );

                testSignature(
                        "Landroid/util/Log;->zza(Ltest/zzkg;)I",
                        new SmaliClassName("Ltest/zzkg;")
                );

                testSignature(
                        "Landroid/util/Log;->zzb([BII)Z",
                        new SmaliClassName("[B"),
                        new SmaliClassName("I"),
                        new SmaliClassName("I")
                );

                testSignature(
                        "Landroid/util/Log;->zzb(Ltest/zzkg;[BII)Z",
                        new SmaliClassName("Ltest/zzkg;"),
                        new SmaliClassName("[B"), new SmaliClassName("I"),
                        new SmaliClassName("I")
                );
            }

            private void testSignature(String sig, SmaliClassName... signatures) throws Exception
            {
                List<SmaliClassName> params = new Tokenizer().tokenize(LineNumber.NONE, sig, null)
                        .getParameters();

                assertTrue(params.equals(Arrays.asList(signatures)));
            }
        }
    }
}
