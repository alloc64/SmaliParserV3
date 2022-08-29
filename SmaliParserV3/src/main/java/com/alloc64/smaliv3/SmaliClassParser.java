/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3;

import com.alloc64.smaliv3.model.SmaliAnnotation;
import com.alloc64.smaliv3.model.SmaliField;
import com.alloc64.smaliv3.model.SmaliMethod;
import com.alloc64.smaliv3.model.SmaliObject;
import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.clazz.SmaliFileV2;
import com.alloc64.smaliv3.model.token.SmaliClassToken;
import com.alloc64.smaliv3.model.token.SmaliCommentToken;
import com.alloc64.smaliv3.model.token.SmaliImplementsToken;
import com.alloc64.smaliv3.model.token.SmaliSkipToken;
import com.alloc64.smaliv3.model.token.SmaliSourceToken;
import com.alloc64.smaliv3.model.token.SmaliSuperClassToken;
import com.alloc64.smaliv3.model.token.SmaliUnknownToken;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizer;

import org.apache.commons.lang3.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class SmaliClassParser
{
    private final Map<String, SmaliTokenizer> tokenizers = new LinkedHashMap<>();

    private SmaliAnnotation.Tokenizer annotationTokenizer = new SmaliAnnotation.Tokenizer();
    private SmaliField.Tokenizer fieldTokenizer = new SmaliField.Tokenizer();
    private SmaliMethod.Tokenizer methodTokenizer = new SmaliMethod.Tokenizer();

    public SmaliClassParser()
    {
        // class signatures and implementations
        addTokenizer(SmaliClassToken.TOKEN, new SmaliClassToken.Tokenizer());
        addTokenizer(SmaliSuperClassToken.TOKEN, new SmaliSuperClassToken.Tokenizer());
        addTokenizer(SmaliImplementsToken.TOKEN, new SmaliImplementsToken.Tokenizer());
        addTokenizer(SmaliSourceToken.TOKEN, new SmaliSourceToken.Tokenizer());

        // fields
    }

    private void addTokenizer(String token, SmaliTokenizer tokenizer)
    {
        tokenizers.put(token, tokenizer);
    }

    public SmaliClass parse(File smaliFile) throws Exception
    {
        if (smaliFile == null || !smaliFile.exists())
            throw new FileNotFoundException("Smali file not found: " + smaliFile);

        return parse(
                Files.readAllLines(smaliFile.toPath())
        );
    }

    public SmaliFileV2 parseAsSmaliFile(File smaliFile) throws Exception
    {
        if (smaliFile == null || !smaliFile.exists())
            throw new FileNotFoundException("Smali file not found: " + smaliFile);

        SmaliClass smaliClass = parse(
                Files.readAllLines(smaliFile.toPath())
        );

        return new SmaliFileV2(smaliFile, smaliClass);
    }


    public SmaliClass parse(List<String> lines)
    {
        SmaliClass clazz = new SmaliClass();

        int lineNumber = 0;
        for (int i = 0; i < lines.size(); i++)
        {
            String line = lines.get(i);

            lineNumber++;

            if(line != null)
                line = line.trim();

            if (StringUtils.isEmpty(line))
                continue;

            tokenize(lineNumber, line, clazz);
        }

        return clazz;
    }

    private void tokenize(int lineNumber, String line, SmaliClass clazz)
    {
        String token = parseToken(line);

        List<SmaliObject> tokens = clazz.getTokenList();

        if(line.charAt(0) == '#')
        {
            tokens.add(new SmaliCommentToken(lineNumber, line));
            return;
        }

        if(line.contains(".method public constructor <init>()V"))
            System.currentTimeMillis();

        SmaliObject field = fieldTokenizer.tokenize(lineNumber, line, clazz);

        if(field instanceof SmaliSkipToken)
            return;

        if(field instanceof SmaliField)
        {
            tokens.add(field);
            return;
        }

        if(fieldTokenizer.inProgress())
            return;

        SmaliTokenizer tokenizer = tokenizers.get(token);

        if(tokenizer != null)
        {
            SmaliObject value = tokenizer.tokenize(lineNumber, line, clazz);

            if(value != null)
            {
                if(value instanceof SmaliClassToken)
                {
                    clazz.setClassSignature((SmaliClassToken) value);
                }
                else if(value instanceof SmaliSuperClassToken)
                {
                    clazz.setSuperClassSignature((SmaliSuperClassToken) value);
                }
                else if(value instanceof SmaliImplementsToken)
                {
                    clazz.setInterfaceSignature((SmaliImplementsToken) value);
                }
                else if(value instanceof SmaliSourceToken)
                {
                    clazz.setSource((SmaliSourceToken) value);
                }
                else
                {
                    tokens.add(value);
                }

                return;
            }
        }

        boolean methodBeingParsed = methodTokenizer.inProgress();

        if(!methodBeingParsed)
        {
            if (token.equals(SmaliAnnotation.TOKEN) || annotationTokenizer.inProgress())
            {
                SmaliAnnotation annotation = annotationTokenizer.tokenize(lineNumber, line, clazz);

                if (annotationTokenizer.inProgress())
                    return;

                if (annotation != null)
                {
                    tokens.add(annotation);
                    return;
                }
            }
        }

        if(token.equals(SmaliMethod.TOKEN) || methodBeingParsed)
        {
            SmaliMethod method = methodTokenizer.tokenize(lineNumber, line, clazz);

            if(methodTokenizer.inProgress())
                return;

            if(method != null)
            {
                tokens.add(method);
                return;
            }
        }

        tokens.add(new SmaliUnknownToken(lineNumber, line));
    }

    private String parseToken(String line)
    {
        String token = line;

        int spaceIndex = line.indexOf(" ");

        if (spaceIndex != -1)
        {
            token = line
                    .substring(0, spaceIndex)
                    .trim();

            if (token.equals(".end"))
            {
                String[] arr = line.split(" ");

                if (arr.length == 2)
                    return String.format("%s %s", arr[0], arr[1]);
            }
        }

        return token;
    }
}
