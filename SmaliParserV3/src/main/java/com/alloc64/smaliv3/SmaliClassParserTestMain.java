/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3;

import com.alloc64.smaliv3.model.SmaliAccessModifier;
import com.alloc64.smaliv3.model.SmaliAnnotation;
import com.alloc64.smaliv3.model.SmaliArray;
import com.alloc64.smaliv3.model.SmaliField;
import com.alloc64.smaliv3.model.SmaliMethod;
import com.alloc64.smaliv3.model.SmaliPackedSwitch;
import com.alloc64.smaliv3.model.clazz.SmaliClassName;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.model.token.SmaliAnnotationEndToken;
import com.alloc64.smaliv3.model.token.SmaliAnnotationToken;
import com.alloc64.smaliv3.model.token.SmaliArrayToken;
import com.alloc64.smaliv3.model.token.SmaliCatchToken;
import com.alloc64.smaliv3.model.token.SmaliClassToken;
import com.alloc64.smaliv3.model.token.SmaliFieldToken;
import com.alloc64.smaliv3.model.token.SmaliImplementsToken;
import com.alloc64.smaliv3.model.token.SmaliLabelToken;
import com.alloc64.smaliv3.model.token.SmaliLineNumberToken;
import com.alloc64.smaliv3.model.token.SmaliLocalEndToken;
import com.alloc64.smaliv3.model.token.SmaliLocalToken;
import com.alloc64.smaliv3.model.token.SmaliMethodSignatureToken;
import com.alloc64.smaliv3.model.token.SmaliMethodToken;
import com.alloc64.smaliv3.model.token.SmaliPackedSwitchToken;
import com.alloc64.smaliv3.model.token.SmaliParamToken;
import com.alloc64.smaliv3.model.token.SmaliRegistersToken;
import com.alloc64.smaliv3.model.token.SmaliSourceToken;
import com.alloc64.smaliv3.model.token.SmaliSuperClassToken;

public class SmaliClassParserTestMain
{
    public static void main(String[] args)
    {
        try
        {
            // Class signatures
            run(new SmaliAccessModifier.Test());
            run(new SmaliClassName.Test());
            run(new SmaliClassToken.Tokenizer.Test());
            run(new SmaliSuperClassToken.Tokenizer.Test());
            run(new SmaliSourceToken.Tokenizer.Test());
            run(new SmaliImplementsToken.Tokenizer.Test());

            // Fields
            run(new SmaliFieldToken.Tokenizer.Test());
            run(new SmaliField.Test());

            // Annotations
            run(new SmaliAnnotationToken.Tokenizer.Test());
            run(new SmaliAnnotationEndToken.Test());
            run(new SmaliAnnotation.Tokenizer.Test());

            // Methods
            run(new SmaliMethodSignatureToken.Tokenizer.Test());
            run(new SmaliMethodToken.Tokenizer.Test());
            run(new SmaliMethod.Tokenizer.Test());
            run(new SmaliRegistersToken.Tokenizer.Test());
            run(new SmaliParamToken.Tokenizer.Test());

            // Opcodes and other
            run(new SmaliLocalToken.Tokenizer.Test());
            run(new SmaliLocalEndToken.Tokenizer.Test());
            run(new SmaliLabelToken.Tokenizer.Test());
            run(new SmaliLineNumberToken.Tokenizer.Test());
            run(new SmaliCatchToken.Tokenizer.Test());

            // Packed switch
            run(new SmaliPackedSwitchToken.Tokenizer.Test());
            run(new SmaliPackedSwitch.Tokenizer.Test());

            // Arrays
            run(new SmaliArrayToken.Tokenizer.Test());
            run(new SmaliArray.Tokenizer.Test());
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }

    public static void run(SmaliTest test) throws Exception
    {
        test.test();
    }
}
