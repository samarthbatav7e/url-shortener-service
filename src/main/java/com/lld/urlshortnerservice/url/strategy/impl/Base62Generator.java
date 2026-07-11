package com.lld.urlshortnerservice.url.strategy.impl;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.common.excpetions.InvalidUrlException;
import com.lld.urlshortnerservice.url.strategy.generator.ShortCodeGenerator;

public class Base62Generator implements ShortCodeGenerator {

    private static final String BASE62="0123456789ABCDEFGHIJKLMNOPQRSTUWXYZabcdefghijklmnopqrstuvwxyz";

    @Override
    public String generate(long id)
    {
     if(id<0)
     {
         throw new IllegalArgumentException("ID cannot be negative.");
     }
     if(id==0)
     {
         return String.valueOf(BASE62.charAt(0));
     }
     StringBuilder builder=new StringBuilder();

     while(id>0)
     {
         int remainder=(int) (id%62);
         builder.append(BASE62.charAt(remainder));
         id/=62;
     }
     return builder.reverse().toString();

    }
    @Override
    public CodeGenerationStrategy getStrategy()
    {
        return CodeGenerationStrategy.BASE62;
    }
}
