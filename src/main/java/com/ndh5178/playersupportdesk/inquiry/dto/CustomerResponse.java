package com.ndh5178.playersupportdesk.inquiry.dto;

import com.ndh5178.playersupportdesk.customer.Customer;

public record CustomerResponse(
        String id,
        String nickname,
        String email,
        String countryCode,
        String countryName,
        String languageCode,
        String languageName) {

    public static CustomerResponse from(Customer customer) {
        return new CustomerResponse(
                customer.getId(),
                customer.getNickname(),
                customer.getEmail(),
                customer.getCountryCode(),
                customer.getCountryName(),
                customer.getLanguageCode(),
                customer.getLanguageName());
    }
}
