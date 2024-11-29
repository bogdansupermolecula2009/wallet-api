package ru.andrianov.wallet_api.entities.dto;

import lombok.*;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class WalletDto {

    private UUID walletId;

    private BigDecimal balance;

}
