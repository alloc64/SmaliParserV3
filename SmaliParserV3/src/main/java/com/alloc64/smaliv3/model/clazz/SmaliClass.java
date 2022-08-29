/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/


package com.alloc64.smaliv3.model.clazz;

import com.alloc64.smaliv3.model.SmaliAnnotation;
import com.alloc64.smaliv3.model.SmaliMethod;
import com.alloc64.smaliv3.model.SmaliObject;
import com.alloc64.smaliv3.model.token.SmaliClassToken;
import com.alloc64.smaliv3.model.token.SmaliImplementsToken;
import com.alloc64.smaliv3.model.token.SmaliSourceToken;
import com.alloc64.smaliv3.model.token.SmaliSuperClassToken;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class SmaliClass extends SmaliObject
{
    private SmaliClassToken classSignature;
    private SmaliSuperClassToken superClassSignature;
    private SmaliImplementsToken interfaceSignature;
    private SmaliSourceToken source;

    private List<SmaliObject> tokenList = new ArrayList<>();

    public SmaliClass()
    {
    }

    public SmaliClass(SmaliClass c)
    {
        this.classSignature = c.getClassSignature();
        this.superClassSignature = c.getSuperClassSignature();
        this.interfaceSignature = c.getInterfaceSignature();
        this.source = c.getSource();
        this.tokenList = c.getTokenList();
    }

    public List<SmaliObject> getTokenList()
    {
        return tokenList;
    }

    private <T> T getTokenByType(Class clazz)
    {
        return (T) tokenList
                .stream()
                .filter(r -> r.getClass().equals(clazz))
                .findFirst()
                .orElse(null);
    }

    private <T> T getTokensByType(Class clazz)
    {
        return (T) tokenList
                .stream()
                .filter(r -> r.getClass().equals(clazz))
                .collect(Collectors.toList());
    }

    public SmaliClassToken getClassSignature()
    {
        return classSignature;
    }

    public void setClassSignature(SmaliClassToken classSignature)
    {
        this.classSignature = classSignature;
    }

    public SmaliSuperClassToken getSuperClassSignature()
    {
        return superClassSignature;
    }

    public void setSuperClassSignature(SmaliSuperClassToken superClassSignature)
    {
        this.superClassSignature = superClassSignature;
    }

    public SmaliImplementsToken getInterfaceSignature()
    {
        return interfaceSignature;
    }

    public void setInterfaceSignature(SmaliImplementsToken interfaceSignature)
    {
        this.interfaceSignature = interfaceSignature;
    }

    public SmaliSourceToken getSource()
    {
        return source;
    }

    public void setSource(SmaliSourceToken source)
    {
        this.source = source;
    }

    public List<SmaliAnnotation> getAnnotationList()
    {
        return getTokensByType(SmaliAnnotation.class);
    }

    public List<SmaliMethod> getMethodList()
    {
        return getTokensByType(SmaliMethod.class);
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        if(classSignature != null)
        {
            sb.append(classSignature);
            sb.append("\n");
        }

        if(superClassSignature != null)
        {
            sb.append(superClassSignature);
            sb.append("\n");
        }

        if(source == null)
            sb.append("\n\n");

        if(source != null)
        {
            sb.append(source);
            sb.append("\n\n");
        }

        if(interfaceSignature != null)
        {
            sb.append(interfaceSignature);
            sb.append("\n\n");
        }

        sb.append("\n");

        for(SmaliObject token : tokenList)
        {
            sb.append(token);
            sb.append("\n");
        }

        return sb.toString();
    }

    public File write(File folder) throws Exception
    {
        if(!folder.isDirectory())
            throw new IllegalStateException("Unable to write smali file, invalid directory specified (is directory?): " + folder);

        SmaliClassName className = getClassSignature().getClassName();

        File packageFolder = new File(folder, className
                .getPackageName()
                .getPackageName());

        packageFolder.mkdirs();

        return writeAs(
                new File(packageFolder, className.getClassName() + ".smali")
        );
    }

    public File writeAs(File outputSmaliFile) throws Exception
    {
        Files.write(outputSmaliFile.toPath(), toString().getBytes(StandardCharsets.UTF_8));

        return outputSmaliFile;
    }
}


