/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model.token;

import com.alloc64.smaliv3.Smali;
import com.alloc64.smaliv3.model.SmaliObject;
import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.clazz.SmaliClassName;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Ltest/ads/AdFactory;->ctx:Landroid/content/Context;
 */
public class SmaliFieldSignatureToken extends SmaliObject
{
    private SmaliClassName className;
    private String fieldName;
    private SmaliClassName fieldType;

    public SmaliFieldSignatureToken(SmaliClassName className, String fieldName, SmaliClassName fieldType)
    {
        this.className = className;
        this.fieldName = fieldName;
        this.fieldType = fieldType;
    }

    public SmaliClassName getClassName()
    {
        return className;
    }

    public String getFieldName()
    {
        return fieldName;
    }

    public SmaliClassName getFieldType()
    {
        return fieldType;
    }

    @Override
    public String toString()
    {
        return String.format("%s->%s:%s", className, fieldName, fieldType);
    }

    public static class Tokenizer extends SmaliTokenizer<SmaliFieldSignatureToken>
    {
        private Pattern fieldPattern = Pattern.compile("((?<" + Smali.FULL_CLASS_NAME + ">L...+;)->)?(?<" + Smali.METHOD_NAME + ">\\S+):(?<" + Smali.RETURN_TYPE + ">\\S+)");

        @Override
        public SmaliFieldSignatureToken tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            Matcher m = fieldPattern.matcher(line);

            if(m.find())
            {
                return new SmaliFieldSignatureToken(
                        new SmaliClassName(m.group(Smali.FULL_CLASS_NAME)),
                        m.group(Smali.METHOD_NAME),
                        new SmaliClassName(m.group(Smali.RETURN_TYPE))
                );
            }

            return null;
        }
    }
}
