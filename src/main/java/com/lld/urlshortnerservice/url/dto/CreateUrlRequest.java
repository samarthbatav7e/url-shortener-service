package com.lld.urlshortnerservice.url.dto;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.springframework.cglib.core.Local;


import java.time.LocalDateTime;
import java.util.Objects;

public class CreateUrlRequest {
    @NotBlank(message= "Long URL is required.")
    String longUrl;

    @NotNull(message = "Generation Strategy is required.")
    private CodeGenerationStrategy generationStrategy;

    @FutureOrNull
    private LocalDateTime expiresAt;

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
    public LocalDateTime getExpiresAt()
    {
        return expiresAt;
    }
    public void setExpiresAt(LocalDateTime dateTime)
    {
        this.expiresAt=dateTime;
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
