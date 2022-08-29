/***********************************************************************
 * Copyright (c) 2019 Milan Jaitner                                    *
 * Distributed under the MIT software license, see the accompanying    *
 * file COPYING or https://www.opensource.org/licenses/mit-license.php.*
 ***********************************************************************/

package com.alloc64.smaliv3.model;

import com.alloc64.smaliv3.model.clazz.SmaliClass;
import com.alloc64.smaliv3.model.clazz.SmaliClassName;
import com.alloc64.smaliv3.model.test.SmaliTest;
import com.alloc64.smaliv3.model.token.SmaliClassToken;
import com.alloc64.smaliv3.model.token.SmaliLabelToken;
import com.alloc64.smaliv3.model.token.SmaliLineNumberToken;
import com.alloc64.smaliv3.model.token.SmaliMethodEndToken;
import com.alloc64.smaliv3.model.token.SmaliMethodToken;
import com.alloc64.smaliv3.model.token.SmaliParamToken;
import com.alloc64.smaliv3.model.token.SmaliRegistersToken;
import com.alloc64.smaliv3.tokenizer.SmaliInstructionTokenizer;
import com.alloc64.smaliv3.tokenizer.SmaliTokenizerMultiline;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class SmaliMethod extends SmaliObject
{
    public static final String TOKEN = SmaliMethodToken.TOKEN;

    private SmaliMethodToken signature;
    private SmaliRegistersToken registers;
    private List<SmaliParamToken> params = new ArrayList<>();
    private List<SmaliAnnotation> annotations = new ArrayList<>();

    private List<SmaliObject> instructions = new ArrayList<>();

    public SmaliMethod(SmaliMethodToken signature)
    {
        if (signature == null)
            throw new IllegalStateException("signature is mandatory.");

        this.signature = signature;
    }

    public SmaliMethodToken getSignature()
    {
        return signature;
    }

    public void setSignature(SmaliMethodToken signature)
    {
        this.signature = signature;
    }

    public SmaliRegistersToken getRegisters()
    {
        return registers;
    }

    public void setRegisters(SmaliRegistersToken registers)
    {
        this.registers = registers;
    }

    public List<SmaliParamToken> getParams()
    {
        return params;
    }

    public List<SmaliAnnotation> getAnnotations()
    {
        return annotations;
    }

    public List<SmaliObject> getInstructions()
    {
        return instructions;
    }

    @Override
    public String toString()
    {
        StringBuilder sb = new StringBuilder();

        sb.append(signature);
        sb.append("\n");

        sb.append(TAB);
        sb.append(registers);
        sb.append("\n");
        sb.append("\n");

        for (SmaliParamToken param : params)
        {
            sb.append(TAB);
            sb.append(param);
            sb.append("\n");
        }

        for (SmaliAnnotation annotation : annotations)
        {
            sb.append(TAB);
            sb.append(annotation);
            sb.append("\n");
        }

        for (int i = 0; i < instructions.size(); i++)
        {
            SmaliObject token = instructions.get(i);

            sb.append(TAB);
            sb.append(token);

            if(!(token instanceof SmaliLineNumberToken) && !(token instanceof SmaliLabelToken))
                sb.append("\n");

            if (i < instructions.size() - 1)
                sb.append("\n");
        }

        sb.append(SmaliMethodEndToken.TOKEN);
        sb.append("\n");

        return sb.toString();
    }

    public static class Tokenizer extends SmaliTokenizerMultiline<SmaliMethod>
    {
        private SmaliMethodToken.Tokenizer startTokenizer = new SmaliMethodToken.Tokenizer();
        private SmaliMethodEndToken.Tokenizer endTokenizer = new SmaliMethodEndToken.Tokenizer();

        private SmaliRegistersToken.Tokenizer registersTokenizer = new SmaliRegistersToken.Tokenizer();
        private SmaliParamToken.Tokenizer paramTokenizer = new SmaliParamToken.Tokenizer();

        private List<SmaliTokenizerMultiline> tokenizerList = Arrays.asList(
                new SmaliAnnotation.Tokenizer(),
                new SmaliPackedSwitch.Tokenizer(),
                new SmaliSparseSwitch.Tokenizer(),
                new SmaliArray.Tokenizer()
        );

        private SmaliInstructionTokenizer instructionTokenizer = new SmaliInstructionTokenizer();

        @Override
        public SmaliMethod tokenize(int lineNumber, String line, SmaliClass clazz)
        {
            if (StringUtils.isEmpty(line))
                return null;

            if (inProgress())
            {
                SmaliMethodEndToken endToken = endTokenizer.tokenize(lineNumber, line, clazz);

                if (endToken != null)
                    return finish();

                if (currentItem.getRegisters() == null)
                {
                    SmaliRegistersToken registers = registersTokenizer.tokenize(lineNumber, line, clazz);

                    if (registers != null)
                        currentItem.setRegisters(registers);
                }
                else
                {
                    SmaliParamToken param = paramTokenizer.tokenize(lineNumber, line, clazz);

                    if (param != null)
                    {
                        currentItem.getParams().add(param);
                    }
                    else
                    {
                        boolean skipInstruction = false;

                        for(SmaliTokenizerMultiline tokenizer : tokenizerList)
                        {
                            SmaliObject item = tokenizer.tokenize(lineNumber, line, clazz);

                            if (item != null)
                            {
                                if (item instanceof SmaliAnnotation)
                                {
                                    currentItem.getAnnotations().add((SmaliAnnotation) item);
                                }
                                else
                                {
                                    currentItem.getInstructions().add(item);
                                }

                                skipInstruction = true;
                                break;
                            }

                            if (tokenizer.inProgress())
                            {
                                skipInstruction = true;
                                break;
                            }
                        }

                        if (!skipInstruction)
                        {
                            SmaliObject instruction = instructionTokenizer.tokenize(lineNumber, line, clazz);

                            if (instruction == null)
                                throw new IllegalStateException("Unparseable instruction " + line + " on line " + lineNumber);

                            currentItem.getInstructions().add(instruction);
                        }
                    }
                }
            }
            else
            {
                SmaliMethodToken methodSignature = startTokenizer.tokenize(lineNumber, line, clazz);

                if (methodSignature == null) // tokenization error occured - lexers is in undefined state
                    return null;

                this.currentItem = new SmaliMethod(methodSignature);
            }

            return null;
        }

        public static class Test extends SmaliTest
        {
            @Override
            public void test() throws Exception
            {
                SmaliClass clazz = new SmaliClass();
                clazz.setClassSignature(new SmaliClassToken(new SmaliAccessModifier("public abstract"), new SmaliClassName("Ltest/cg/client/dx/tasks/AbstractFileTask;")));

                SmaliMethod.Tokenizer tokenizer = new SmaliMethod.Tokenizer();

                SmaliMethod method = testTokenizer(tokenizer, clazz, ".method public onSerialize(Ltest/pack/IPacker;)V\n" +
                        "    .registers 3\n" +
                        "    .annotation system Ldalvik/annotation/Throws;\n" +
                        "        value = {\n" +
                        "            Ljava/io/IOException;\n" +
                        "        }\n" +
                        "    .end annotation\n" +
                        "\n" +
                        "    .line 56\n" +
                        "    iget-object v0, p0, Ltest/cg/client/dx/tasks/AbstractGateway;->clazz:Ljava/lang/String;\n" +
                        "\n" +
                        "    invoke-interface {p1, v0}, Ltest/pack/IPacker;->write(Ljava/lang/String;)Ltest/pack/IPacker;\n" +
                        "\n" +
                        "    .line 57\n" +
                        "    iget-object v0, p0, Ltest/cg/client/dx/tasks/AbstractGateway;->method:Ljava/lang/String;\n" +
                        "\n" +
                        "    invoke-interface {p1, v0}, Ltest/pack/IPacker;->write(Ljava/lang/String;)Ltest/pack/IPacker;\n" +
                        "\n" +
                        "    .line 58\n" +
                        "    return-void\n" +
                        ".end method");

                assertTrue(method != null);


                method = testTokenizer(tokenizer, clazz,
                        ".method constructor <init>()V\n" +
                                "    .registers 1\n" +
                                "\n" +
                                "    .line 23\n" +
                                "    invoke-direct {p0}, Ljava/util/AbstractList;-><init>()V\n" +
                                "\n" +
                                "    return-void\n" +
                                ".end method"
                );

                assertTrue(method != null);

                method = testTokenizer(tokenizer, clazz,
                        ".method public getIdRequest()Ljava/lang/String;\n" +
                                "    .registers 2\n" +
                                "\n" +
                                "    .line 15\n" +
                                "    iget-object v0, p0, Ltest/messenger/messages/cb/AbstractClickBotRequestMessage;->idRequest:Ljava/lang/String;\n" +
                                "\n" +
                                "    return-object v0\n" +
                                ".end method"
                );

                assertTrue(method != null);

                method = testTokenizer(tokenizer, clazz, ".method public constructor <init>(Ljava/lang/String;Ljava/lang/String;)V\n" +
                        "    .registers 3\n" +
                        "    .param p1, \"clazz\"    # Ljava/lang/String;\n" +
                        "    .param p2, \"method\"    # Ljava/lang/String;\n" +
                        "\n" +
                        "    .line 28\n" +
                        "    invoke-direct {p0}, Ltest/pack/MessagePackSerializable;-><init>()V\n" +
                        "\n" +
                        "    .line 29\n" +
                        "    iput-object p1, p0, Ltest/cg/client/dx/tasks/AbstractGateway;->clazz:Ljava/lang/String;\n" +
                        "\n" +
                        "    .line 30\n" +
                        "    iput-object p2, p0, Ltest/cg/client/dx/tasks/AbstractGateway;->method:Ljava/lang/String;\n" +
                        "\n" +
                        "    .line 31\n" +
                        "    return-void\n" +
                        ".end method");

                assertTrue(method != null);

                method = testTokenizer(tokenizer, clazz, ".method protected onCreate(Landroid/os/Bundle;)V\n" +
                        "    .registers 6\n" +
                        "    .param p1, \"savedInstanceState\"    # Landroid/os/Bundle;\n" +
                        "\n" +
                        "    .line 21\n" +
                        "    invoke-virtual {p0}, Ltest/activity/AbstractBaseActivity;->getIntent()Landroid/content/Intent;\n" +
                        "\n" +
                        "    move-result-object v0\n" +
                        "\n" +
                        "    .line 23\n" +
                        "    .local v0, \"intent\":Landroid/content/Intent;\n" +
                        "    if-eqz v0, :cond_1b\n" +
                        "\n" +
                        "    .line 25\n" +
                        "    invoke-static {}, Ltest/pub/ISDK$Instance;->get()Ltest/pub/ISDK;\n" +
                        "\n" +
                        "    move-result-object v1\n" +
                        "\n" +
                        "    .line 27\n" +
                        "    .local v1, \"sdk\":Ltest/pub/ISDK;\n" +
                        "    if-eqz v1, :cond_1a\n" +
                        "\n" +
                        "    .line 28\n" +
                        "    const-string v2, \"activity_id\"\n" +
                        "\n" +
                        "    const/4 v3, -0x1\n" +
                        "\n" +
                        "    invoke-virtual {v0, v2, v3}, Landroid/content/Intent;->getIntExtra(Ljava/lang/String;I)I\n" +
                        "\n" +
                        "    move-result v2\n" +
                        "\n" +
                        "    invoke-interface {v1, v2}, Ltest/pub/ISDK;->implementActivity(I)Ltest/pub/BasePActivity;\n" +
                        "\n" +
                        "    move-result-object v2\n" +
                        "\n" +
                        "    iput-object v2, p0, Ltest/activity/AbstractBaseActivity;->activityImpl:Ltest/pub/BasePActivity;\n" +
                        "\n" +
                        "    goto :goto_1c\n" +
                        "\n" +
                        "    .line 27\n" +
                        "    :cond_1a\n" +
                        "    goto :goto_1c\n" +
                        "\n" +
                        "    .line 23\n" +
                        "    .end local v1    # \"sdk\":Ltest/pub/ISDK;\n" +
                        "    :cond_1b\n" +
                        "    nop\n" +
                        "\n" +
                        "    .line 31\n" +
                        "    :goto_1c\n" +
                        "    iget-object v1, p0, Ltest/activity/AbstractBaseActivity;->activityImpl:Ltest/pub/BasePActivity;\n" +
                        "\n" +
                        "    if-eqz v1, :cond_24\n" +
                        "\n" +
                        "    .line 32\n" +
                        "    invoke-virtual {v1, p0}, Ltest/pub/BasePActivity;->setTheme(Landroid/app/Activity;)V\n" +
                        "\n" +
                        "    goto :goto_25\n" +
                        "\n" +
                        "    .line 31\n" +
                        "    :cond_24\n" +
                        "    nop\n" +
                        "\n" +
                        "    .line 34\n" +
                        "    :goto_25\n" +
                        "    invoke-super {p0, p1}, Landroid/app/Activity;->onCreate(Landroid/os/Bundle;)V\n" +
                        "\n" +
                        "    .line 36\n" +
                        "    iget-object v1, p0, Ltest/activity/AbstractBaseActivity;->activityImpl:Ltest/pub/BasePActivity;\n" +
                        "\n" +
                        "    if-eqz v1, :cond_30\n" +
                        "\n" +
                        "    .line 37\n" +
                        "    invoke-virtual {v1, p0, p1}, Ltest/pub/BasePActivity;->onCreate(Landroid/app/Activity;Landroid/os/Bundle;)V\n" +
                        "\n" +
                        "    goto :goto_33\n" +
                        "\n" +
                        "    .line 39\n" +
                        "    :cond_30\n" +
                        "    invoke-virtual {p0}, Ltest/activity/AbstractBaseActivity;->finish()V\n" +
                        "\n" +
                        "    .line 40\n" +
                        "    :goto_33\n" +
                        "    return-void\n" +
                        ".end method");

                assertTrue(method != null);

                method = testTokenizer(tokenizer, clazz, ".method final exit(Ljava/io/IOException;)Ljava/io/IOException;\n" +
                        "    .registers 3\n" +
                        "    .param p1, \"cause\"    # Ljava/io/IOException;\n" +
                        "    .annotation system Ldalvik/annotation/Throws;\n" +
                        "        value = {\n" +
                        "            Ljava/io/IOException;\n" +
                        "        }\n" +
                        "    .end annotation\n" +
                        "\n" +
                        "    .line 284\n" +
                        "    invoke-virtual {p0}, La/okio/AsyncTimeout;->exit()Z\n" +
                        "\n" +
                        "    move-result v0\n" +
                        "\n" +
                        "    if-nez v0, :cond_7\n" +
                        "\n" +
                        "    return-object p1\n" +
                        "\n" +
                        "    .line 285\n" +
                        "    :cond_7\n" +
                        "    invoke-virtual {p0, p1}, La/okio/AsyncTimeout;->newTimeoutException(Ljava/io/IOException;)Ljava/io/IOException;\n" +
                        "\n" +
                        "    move-result-object v0\n" +
                        "\n" +
                        "    return-object v0\n" +
                        ".end method");

                assertTrue(method != null);
            }

            private SmaliMethod testTokenizer(Tokenizer tokenizer, SmaliClass clazz, String lines)
            {
                String splits[] = lines.split("\n");

                for (int i = 0; i < splits.length; i++)
                {
                    SmaliMethod method = tokenizer.tokenize(i, splits[i].trim(), clazz);

                    if (method != null)
                        return method;
                }

                return null;
            }
        }
    }
}
