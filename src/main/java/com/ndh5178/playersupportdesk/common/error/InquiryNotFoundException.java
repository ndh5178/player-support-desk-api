package com.ndh5178.playersupportdesk.common.error;

public class InquiryNotFoundException extends RuntimeException {

    public InquiryNotFoundException(String inquiryId) {
        super("요청한 문의를 찾을 수 없습니다.");
    }
}
