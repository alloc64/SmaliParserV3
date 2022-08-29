/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3;

import com.alloc64.smaliv3.model.clazz.SmaliFileV2;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class SmaliClassParserMain
{
    public static void main(String[] args)
    {
        try
        {
            SmaliClassParser parser = new SmaliClassParser();
            List<SmaliFileV2> classList = new ArrayList<>();

            SmaliFileV2 clazz = parser.parseAsSmaliFile(new File("path-to-smali-class"));

            // modify

            clazz.save();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
