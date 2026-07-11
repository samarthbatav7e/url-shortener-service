package com.lld.urlshortnerservice.url.dto;

import java.util.Objects;

public class CreateUrlResponse {
    private String shortCode;
    private String shortUrl;

    public CreateUrlResponse() {
    }

    public CreateUrlResponse(String shortCode,
                             String shortUrl) {

        this.shortCode = shortCode;
        this.shortUrl = shortUrl;
    }

    public String getShortCode() {
        return shortCode;
    }

    public void setShortCode(String shortCode) {
        this.shortCode = shortCode;
    }

    public String getShortUrl() {
        return shortUrl;
    }

    public void setShortUrl(String shortUrl) {
        this.shortUrl = shortUrl;
    }

    @Override
    public boolean equals(Object o) {

        if (this == o)
            return true;

        if (!(o instanceof CreateUrlResponse))
            return false;

        CreateUrlResponse that = (CreateUrlResponse) o;

        return Objects.equals(shortCode, that.shortCode)
                && Objects.equals(shortUrl, that.shortUrl);
    }

    @Override
    public int hashCode() {
        return Objects.hash(shortCode, shortUrl);
    }

    @Override
    public String toString() {
        return "CreateUrlResponse{" +
                "shortCode='" + shortCode + '\'' +
                ", shortUrl='" + shortUrl + '\'' +
                '}';
    }
}
