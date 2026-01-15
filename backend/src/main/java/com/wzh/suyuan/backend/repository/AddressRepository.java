package com.wzh.suyuan.backend.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.wzh.suyuan.backend.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserIdOrderByIdDesc(Long userId);

    Optional<Address> findByIdAndUserId(Long id, Long userId);

    List<Address> findByUserIdAndIsDefaultTrue(Long userId);

    long countByUserId(Long userId);
}
