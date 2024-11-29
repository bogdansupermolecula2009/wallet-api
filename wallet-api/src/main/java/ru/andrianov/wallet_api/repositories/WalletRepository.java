package ru.andrianov.wallet_api.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import ru.andrianov.wallet_api.entities.WalletEntity;

import java.util.Optional;
import java.util.UUID;

public interface WalletRepository extends JpaRepository<WalletEntity, UUID> {
    @Query("SELECT w FROM WalletEntity w WHERE w.walletId = :id")
    Optional<WalletEntity> findByIdWithLock(@Param("id") UUID id);
}
