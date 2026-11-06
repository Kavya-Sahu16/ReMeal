package com.kavya.remeal.donation;

import com.kavya.remeal.donation.dto.DonationRequest;
import com.kavya.remeal.donation.dto.DonationResponse;
import com.kavya.remeal.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    // PHASE 4: CREATE DONATION
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonationResponse createDonation(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DonationRequest request) {

        return donationService.createDonation(user, request);
    }

    // PHASE 4: GET MY DONATIONS
    @GetMapping("/my")
    public List<DonationResponse> getMyDonations(
            @AuthenticationPrincipal User user) {

        return donationService.getMyDonations(user);
    }

    // PHASE 4: GET SPECIFIC DONATION
    @GetMapping("/{id}")
    public DonationResponse getDonation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return donationService.getDonation(id, user);
    }

    // PHASE 4: UPDATE DONATION
    @PutMapping("/{id}")
    public DonationResponse updateDonation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DonationRequest request) {

        return donationService.updateDonation(id, user, request);
    }

    // PHASE 4: DELETE DONATION
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDonation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        donationService.deleteDonation(id, user);
    }

    // PHASE 5: BROWSE AND FILTER DONATIONS
    @GetMapping("/available")
    public List<DonationResponse> browseDonations(
            @AuthenticationPrincipal User user,

            @RequestParam(required = false)
            String city,

            @RequestParam(required = false)
            DonationCategory category,

            @RequestParam(required = false)
            BigDecimal minQuantity,

            @RequestParam(required = false)
            BigDecimal maxQuantity,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime expiryFrom,

            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime expiryTo,

            @RequestParam(required = false)
            String foodName) {

        return donationService.browseDonations(
                user,
                city,
                category,
                minQuantity,
                maxQuantity,
                expiryFrom,
                expiryTo,
                foodName
        );
    }

    // PHASE 5: VIEW AVAILABLE DONATION DETAILS
    @GetMapping("/available/{id}")
    public DonationResponse getAvailableDonation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return donationService.getAvailableDonation(id, user);
    }
}