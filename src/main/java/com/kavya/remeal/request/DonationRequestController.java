package com.kavya.remeal.request;

import com.kavya.remeal.request.dto.CreateRequest;
import com.kavya.remeal.request.dto.RequestResponse;
import com.kavya.remeal.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class DonationRequestController {

    private final DonationRequestService requestService;

    // RECIPIENT: REQUEST DONATION
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public RequestResponse createRequest(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody CreateRequest request) {

        return requestService.createRequest(user, request);
    }

    // RECIPIENT: VIEW MY REQUESTS
    @GetMapping("/my")
    public List<RequestResponse> getMyRequests(
            @AuthenticationPrincipal User user) {

        return requestService.getMyRequests(user);
    }

    // DONOR: VIEW REQUESTS RECEIVED
    @GetMapping("/received")
    public List<RequestResponse> getReceivedRequests(
            @AuthenticationPrincipal User user) {

        return requestService.getReceivedRequests(user);
    }

    // DONOR: APPROVE REQUEST
    @PutMapping("/{id}/approve")
    public RequestResponse approveRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return requestService.approveRequest(id, user);
    }

    // DONOR: REJECT REQUEST
    @PutMapping("/{id}/reject")
    public RequestResponse rejectRequest(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return requestService.rejectRequest(id, user);
    }
}