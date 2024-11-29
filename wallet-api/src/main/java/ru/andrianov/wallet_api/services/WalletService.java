package ru.andrianov.wallet_api.services;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Transactional;
import ru.andrianov.wallet_api.entities.WalletEntity;
import ru.andrianov.wallet_api.entities.dto.UpdatedWalletDto;
import ru.andrianov.wallet_api.entities.dto.WalletDto;
import ru.andrianov.wallet_api.entities.enums.OperationType;
import ru.andrianov.wallet_api.exceptions.InsufficientFundsException;
import ru.andrianov.wallet_api.exceptions.WalletNotFoundException;
import ru.andrianov.wallet_api.repositories.WalletRepository;
import ru.andrianov.wallet_api.services.mapper.WalletDtoMapper;

import java.math.BigDecimal;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class WalletService {

    private final WalletRepository walletRepository;

    @Transactional(isolation = Isolation.REPEATABLE_READ)
    public WalletDto getWallet(UUID walletId) {
        WalletEntity wallet = walletRepository.findById(walletId)
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found"));
        return WalletDtoMapper.transformWalletEntityToDto(wallet);
    }


    @Transactional(timeout = 5, isolation = Isolation.REPEATABLE_READ)
    public WalletDto changeBalance(UpdatedWalletDto walletDto) {
        WalletEntity wallet = walletRepository.findByIdWithLock(walletDto.getWalletId())
                .orElseThrow(() -> new WalletNotFoundException("Wallet not found with ID:" + walletDto.getWalletId()));
        processOperation(wallet, walletDto.getOperationType(), walletDto.getAmount());
        saveWallet(wallet);
        return WalletDtoMapper.transformWalletEntityToDto(wallet);
    }

    private void processOperation(WalletEntity wallet, OperationType operationType, BigDecimal amount) {
        switch (operationType) {
            case DEPOSIT -> deposit(wallet, amount);
            case WITHDRAW -> withdraw(wallet, amount);
        }
    }

    private void deposit(WalletEntity wallet, BigDecimal amount) {
        wallet.setBalance(wallet.getBalance().add(amount));
    }

    private void withdraw(WalletEntity wallet, BigDecimal amount) {
        if (wallet.getBalance().compareTo(amount) < 0) {
            throw new InsufficientFundsException("Insufficient funds for withdrawal. Available: " + wallet.getBalance() + ", required: " + amount);
        }
        wallet.setBalance(wallet.getBalance().subtract(amount));
    }

    private void saveWallet(WalletEntity wallet) {
        walletRepository.save(wallet);
    }

}