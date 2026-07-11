package com.lld.urlshortnerservice.url.dto;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;

import java.util.Objects;

public class CreateUrlRequest {
    String longUrl;
    private CodeGenerationStrategy generationStrategy;

    public CreateUrlRequest()
    {

    }
    public CreateUrlRequest(String longUrl, CodeGenerationStrategy strategy)
    {
        this.longUrl=longUrl;
        this.generationStrategy=strategy;
    }
    public String getLongUrl()
    {
        return longUrl;
    }
    public CodeGenerationStrategy getGenerationStrategy()
    {
        return generationStrategy;
    }
    public void setLongUrl(String longUrl)
    {
        this.longUrl=longUrl;
    }
    public void setGenerationStrategy(CodeGenerationStrategy strategy)
    {
        this.generationStrategy=strategy;
    }
    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (!(o instanceof CreateUrlRequest))
            return false;

        CreateUrlRequest that = (CreateUrlRequest) o;

        return Objects.equals(longUrl, that.longUrl)
                && generationStrategy== that.generationStrategy;
    }

    @Override
    public int hashCode() {
        return Objects.hash(longUrl, generationStrategy);
    }

    @Override
    public String toString() {
        return "CreateUrlRequest{" +
                "longUrl='" + longUrl + '\'' +
                ", strategy=" + generationStrategy +
                '}';
    }

}
