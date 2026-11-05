package com.kavya.remeal.donation;

import com.kavya.remeal.donation.dto.DonationRequest;
import com.kavya.remeal.donation.dto.DonationResponse;
import com.kavya.remeal.user.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/donations")
@RequiredArgsConstructor
public class DonationController {

    private final DonationService donationService;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public DonationResponse createDonation(
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DonationRequest request) {

        return donationService.createDonation(user, request);
    }

    @GetMapping("/my")
    public List<DonationResponse> getMyDonations(
            @AuthenticationPrincipal User user) {

        return donationService.getMyDonations(user);
    }

    @GetMapping("/{id}")
    public DonationResponse getDonation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        return donationService.getDonation(id, user);
    }

    @PutMapping("/{id}")
    public DonationResponse updateDonation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user,
            @Valid @RequestBody DonationRequest request) {

        return donationService.updateDonation(id, user, request);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteDonation(
            @PathVariable Long id,
            @AuthenticationPrincipal User user) {

        donationService.deleteDonation(id, user);
    }
}