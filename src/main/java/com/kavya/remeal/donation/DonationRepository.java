package com.kavya.remeal.donation;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface DonationRepository
        extends JpaRepository<Donation, Long> {

    List<Donation> findAllByDonorIdOrderByCreatedAtDesc(Long donorId);

    Optional<Donation> findByIdAndDonorId(Long id, Long donorId);
}