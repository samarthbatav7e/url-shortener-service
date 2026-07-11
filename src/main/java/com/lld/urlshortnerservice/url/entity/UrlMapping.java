package com.lld.urlshortnerservice.url.entity;

import com.lld.urlshortnerservice.common.enums.CodeGenerationStrategy;
import com.lld.urlshortnerservice.url.strategy.generator.ShortCodeGenerator;

import java.time.LocalDateTime;
import java.util.Objects;

public class UrlMapping {
    private long id;
    private String longUrl;
    private String shortCode;
    private CodeGenerationStrategy generationStrategy;
    private LocalDateTime createdAt;
    private LocalDateTime expiresAt;

   public UrlMapping()
   {

   }
   public UrlMapping(Long id, String longUrl,String shortCode, CodeGenerationStrategy strategy,LocalDateTime createdAt, LocalDateTime expiresAt)
   {
       this.id=id;
       this.longUrl=longUrl;
       this.shortCode=shortCode;
       this.generationStrategy=strategy;
       this.createdAt=createdAt;
       this.expiresAt=expiresAt;
   }
   public Long getId()
   {
       return id;
   }

   public String getLongUrl()
   {
       return longUrl;
   }
   public String getShortCode()
   {
       return shortCode;
   }
   public void setShortCode(String shortCode)
   {
       this.shortCode=shortCode;
   }
   public CodeGenerationStrategy getGenerationStrategy()
   {
       return generationStrategy;
   }
   public void setGenerationStrategy(CodeGenerationStrategy strategy)
       {
           this.generationStrategy=strategy;
       }

       public LocalDateTime getCreatedAt()
       {
           return createdAt;
       }
       public LocalDateTime getExpiresAt()
       {
           return expiresAt;
       }
       public void setCreatedAt(LocalDateTime createdAt)
       {
           this.createdAt=createdAt;
       }
       public void setExpiresAt(LocalDateTime expiresAt)
       {
           this.expiresAt=expiresAt;
       }

       @Override
    public boolean equals(Object o)
       {
           if(this==o)
           {
               return true;
           }
           if(!(o instanceof UrlMapping))
           {
               return false;
           }
           UrlMapping that=(UrlMapping) o;
           return Objects.equals(id, that.id)
                   && Objects.equals(longUrl,that.longUrl)
                   && Objects.equals(shortCode,that.shortCode)
                   && generationStrategy==that.generationStrategy;
       }
       @Override
     public int hashCode()
       {
           return Objects.hash(id, longUrl,shortCode, generationStrategy);
       }
       @Override
       public String toString()
       {
           return "UrlMapping{" +
                   "id= " + id +
                   ", longUrl=' " + longUrl + '\'' +
                   ", shortCode=' " + shortCode + '\'' +
                   ", generationStrategy=" + generationStrategy +
                   ", createdAt="  + createdAt +
                   ", expiresAt=" + expiresAt +
                   '}';
       }

       public static class Builder
       {

           private Long id;

           private String longUrl;

           private String shortCode;

           private CodeGenerationStrategy generationStrategy;

           private LocalDateTime createdAt;

           private LocalDateTime expiresAt;

           public Builder id(Long id) {
               this.id = id;
               return this;
           }

           public Builder longUrl(String longUrl) {
               this.longUrl = longUrl;
               return this;
           }

           public Builder shortCode(String shortCode) {
               this.shortCode = shortCode;
               return this;
           }

           public Builder generationStrategy(CodeGenerationStrategy strategy) {
               this.generationStrategy = strategy;
               return this;
           }

           public Builder createdAt(LocalDateTime createdAt) {
               this.createdAt = createdAt;
               return this;
           }

           public Builder expiresAt(LocalDateTime expiresAt) {
               this.expiresAt = expiresAt;
               return this;
           }

           public UrlMapping build() {
               return new UrlMapping(
                       id,
                       longUrl,
                       shortCode,
                       generationStrategy,
                       createdAt,
                       expiresAt
               );
           }
       }


}
