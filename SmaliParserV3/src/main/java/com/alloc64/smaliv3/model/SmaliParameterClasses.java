/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model;

import com.alloc64.smaliv3.model.clazz.SmaliClassName;
import com.alloc64.smaliv3.model.opcode.PrimitiveType;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;

/**
 * [BII
 * [B[I[I
 * B
 * Ltest/zzkg;
 */
public class SmaliParameterClasses extends ArrayList<SmaliClassName>
{
    public SmaliParameterClasses(String value)
    {
        if (StringUtils.isEmpty(value))
            return;

        boolean lookingForClassName = false;
        int arrayDimensions = 0;

        StringBuilder className = new StringBuilder();

        for (int i = 0; i < value.length(); i++)
        {
            char c = value.charAt(i);

            if(c == '[')
            {
                arrayDimensions++;
                continue;
            }

            if (lookingForClassName)
            {
                className.append(c);

                if (c == ';')
                {
                    lookingForClassName = false;

                    add(new SmaliClassName(StringUtils.repeat('[', arrayDimensions) + className.toString()));
                    arrayDimensions = 0;
                }
            }
            else
            {
                if (c == 'L')
                {
                    lookingForClassName = true;

                    className = new StringBuilder();
                    className.append(c);
                }
                else
                {
                    String p = String.valueOf(c);

                    PrimitiveType primitiveType = PrimitiveType.from(p);

                    if(primitiveType != null)
                    {
                        add(new SmaliClassName(StringUtils.repeat('[', arrayDimensions) + primitiveType.getValue()));
                        arrayDimensions = 0;
                    }
                }
            }
        }

        System.currentTimeMillis();
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        for(SmaliClassName clazz : this)
            sb.append(clazz.toString());

        return sb.toString();
    }
}
