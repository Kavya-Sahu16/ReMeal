package com.kavya.remeal.donation;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface DonationRepository
        extends JpaRepository<Donation, Long>,
                JpaSpecificationExecutor<Donation> {

    List<Donation> findAllByDonorIdOrderByCreatedAtDesc(Long donorId);

    Optional<Donation> findByIdAndDonorId(Long id, Long donorId);

    Optional<Donation> findByIdAndStatusAndExpiryDateTimeAfter(
            Long id,
            DonationStatus status,
            LocalDateTime now
    );
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("SELECT d FROM Donation d WHERE d.id = :id")
    Optional<Donation> findByIdForUpdate(@Param("id") Long id);
}