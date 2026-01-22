package com.wzh.suyuan.backend.repository;

import java.util.List;
import java.util.Optional;

import javax.persistence.LockModeType;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.wzh.suyuan.backend.entity.Address;

public interface AddressRepository extends JpaRepository<Address, Long> {
    List<Address> findByUserIdOrderByIdDesc(Long userId);

    Optional<Address> findByIdAndUserId(Long id, Long userId);

    List<Address> findByUserIdAndIsDefaultTrue(Long userId);

    long countByUserId(Long userId);

    @Lock(LockModeType.PESSIMISTIC_WRITE)
    @Query("select a from Address a where a.userId = :userId")
    List<Address> findByUserIdForUpdate(@Param("userId") Long userId);
}
