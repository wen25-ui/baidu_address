package com.graduation.platform.repository;

import com.graduation.platform.model.entity.BillingRecord;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BillingRepository extends JpaRepository<BillingRecord, Long> {
    // Additional query methods can be defined here if needed
}