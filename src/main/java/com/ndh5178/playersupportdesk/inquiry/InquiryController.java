package com.ndh5178.playersupportdesk.inquiry;

import com.ndh5178.playersupportdesk.inquiry.dto.InquiryListResponse;
import com.ndh5178.playersupportdesk.inquiry.dto.InquiryResponse;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/inquiries")
public class InquiryController {

    private final InquiryService inquiryService;

    public InquiryController(InquiryService inquiryService) {
        this.inquiryService = inquiryService;
    }

    @GetMapping
    public InquiryListResponse getInquiries(HttpServletRequest request) {
        InquiryListQuery query = InquiryListQueryParser.parse(request);
        return inquiryService.getInquiries(query);
    }

    @GetMapping("/{inquiryId}")
    public InquiryResponse getInquiry(@PathVariable String inquiryId) {
        return inquiryService.getInquiry(inquiryId);
    }
}
