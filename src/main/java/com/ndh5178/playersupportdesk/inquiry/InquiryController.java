package com.ndh5178.playersupportdesk.inquiry;

import com.ndh5178.playersupportdesk.inquiry.dto.CreateInquiryNoteRequest;
import com.ndh5178.playersupportdesk.inquiry.dto.InquiryListResponse;
import com.ndh5178.playersupportdesk.inquiry.dto.InquiryNoteResponse;
import com.ndh5178.playersupportdesk.inquiry.dto.InquiryResponse;
import com.ndh5178.playersupportdesk.inquiry.dto.UpdateInquiryRequest;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import tools.jackson.databind.JsonNode;

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

    @PatchMapping("/{inquiryId}")
    public InquiryResponse updateInquiry(
            @PathVariable String inquiryId,
            @RequestBody JsonNode body) {
        return inquiryService.updateInquiry(inquiryId, UpdateInquiryRequest.from(body));
    }

    @PostMapping("/{inquiryId}/notes")
    @ResponseStatus(HttpStatus.CREATED)
    public InquiryNoteResponse addInquiryNote(
            @PathVariable String inquiryId,
            @RequestBody JsonNode body) {
        return inquiryService.addInquiryNote(inquiryId, CreateInquiryNoteRequest.from(body));
    }
}
