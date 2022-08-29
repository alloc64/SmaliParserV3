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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class SmaliMethodToken extends SmaliToken
{
    public static final String TOKEN = ".method";

    private SmaliAccessModifier accessModifier;
    private SmaliMethodSignatureToken signature;

    public SmaliMethodToken(int lineNumber,  SmaliAccessModifier accessModifier, SmaliMethodSignatureToken signature)
    {
        super(lineNumber, TOKEN);

        this.accessModifier = accessModifier;
        this.signature = signature;
    }

    public SmaliAccessModifier getAccessModifier()
    {
        return accessModifier;
    }

    public SmaliMethodSignatureToken getSignature()
    {
        return signature;
    }

    @Override
    public String toString()
    {
        return toString(String.format("%s %s %s(%s)%s", getName(), accessModifier, signature.getMethodName(), signature.getParameters(), signature.getReturnType()));
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliMethodToken>
    {
        private static final Pattern methodPattern = Pattern.compile(".method\\s((?<" + Smali.ACCESS_MODIFIER + ">...+)\\s)?(?<" + Smali.METHOD_SIGNATURE + ">(?<" + Smali.METHOD_NAME + ">\\S+)\\((?<" + Smali.PARAMTERS + ">\\S+)?\\)(?<" + Smali.RETURN_TYPE + ">\\S+))");

        private SmaliMethodSignatureToken.Tokenizer methodSignatureTokenizer = new SmaliMethodSignatureToken.Tokenizer();

        @Override
        public SmaliMethodToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            Matcher m = methodPattern.matcher(line);

            if (m.find())
                return new SmaliMethodToken(
                        lineNumber,
                        new SmaliAccessModifier(m.group(Smali.ACCESS_MODIFIER)),
                        methodSignatureTokenizer.tokenize(lineNumber, m.group(Smali.METHOD_SIGNATURE), clazz)
                );

            validate(TOKEN, line);

            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                SmaliClass clazz = new SmaliClass();
                clazz.setClassSignature(new SmaliClassToken(new SmaliAccessModifier("public abstract"), new SmaliClassName("Ltest/cg/client/dx/tasks/AbstractFileTask;")));

                testSignature(clazz, ".method private final encryptBlock([[I)V");
                testSignature(clazz, ".method constructor <init>()V");
                testSignature(clazz, ".method static constructor <init>()V");
                testSignature(clazz, ".method static synthetic access$000(Ltest/activity/ActivityTest;)Landroid/widget/ImageView;");
                testSignature(clazz, ".method private setContentView(Landroid/app/Activity;Ltest/ads/creatives/impl/Creative;)V");
                testSignature(clazz, ".method public asArrayValue()Ltest/pack/type/ArrayValue;");
                testSignature(clazz, ".method protected final zzb(Ltest/zzkg;[BII)Z");
            }

            private void testSignature(SmaliClass clazz, String sig) throws Exception
            {
                SmaliMethodToken signature = new SmaliMethodToken.Tokenizer().tokenize(LineNumber.NONE, sig, clazz);

                assertTrue(signature != null);
                assertTrue(signature.toString().equals(sig));
            }
        }
    }
}

