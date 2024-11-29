package ru.andrianov.wallet_api.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.andrianov.wallet_api.entities.dto.UpdatedWalletDto;
import ru.andrianov.wallet_api.entities.dto.WalletDto;
import ru.andrianov.wallet_api.services.WalletService;

import java.util.UUID;

@RestController
@RequiredArgsConstructor
public class WalletController {

    private final WalletService walletService;


    @GetMapping("/api/v1/wallets/{WALLET_UUID}")
    public ResponseEntity<WalletDto> getBalance(@PathVariable UUID WALLET_UUID) {
        return ResponseEntity.ok(walletService.getWallet(WALLET_UUID));
    }

    @PostMapping("/api/v1/wallet")
    public ResponseEntity<WalletDto> changeBalance(@Valid @RequestBody UpdatedWalletDto walletDto) {
        return ResponseEntity.ok(walletService.changeBalance(walletDto));
    }


}