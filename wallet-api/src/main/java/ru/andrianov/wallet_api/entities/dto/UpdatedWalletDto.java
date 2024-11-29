package ru.andrianov.wallet_api.entities.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.andrianov.wallet_api.entities.enums.OperationType;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdatedWalletDto {

    @NotNull(message = "ID cannot be null")
    private UUID walletId;

    @NotNull
    @Min(value = 0, message = "Amount must be greater than 0")
    private BigDecimal amount;

    @NotNull(message = "Operation type cannot be null")
    private OperationType operationType;
}
