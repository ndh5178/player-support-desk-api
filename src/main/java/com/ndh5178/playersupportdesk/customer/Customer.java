package com.ndh5178.playersupportdesk.customer;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

@Entity
@Table(name = "customers")
public class Customer {

    @Id
    @Column(columnDefinition = "text")
    private String id;

    @Column(nullable = false, columnDefinition = "text")
    private String nickname;

    @Column(nullable = false, columnDefinition = "text")
    private String email;

    @Column(name = "country_code", nullable = false, length = 2)
    private String countryCode;

    @Column(name = "country_name", nullable = false, columnDefinition = "text")
    private String countryName;

    @Column(name = "language_code", nullable = false, length = 35)
    private String languageCode;

    @Column(name = "language_name", nullable = false, columnDefinition = "text")
    private String languageName;

    protected Customer() {
    }

    public String getId() {
        return id;
    }

    public String getNickname() {
        return nickname;
    }

    public String getEmail() {
        return email;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public String getCountryName() {
        return countryName;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public String getLanguageName() {
        return languageName;
    }
}
