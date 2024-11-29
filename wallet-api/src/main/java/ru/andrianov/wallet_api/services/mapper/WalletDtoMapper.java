package ru.andrianov.wallet_api.services.mapper;

import lombok.experimental.UtilityClass;
import ru.andrianov.wallet_api.entities.WalletEntity;
import ru.andrianov.wallet_api.entities.dto.WalletDto;

@UtilityClass
public class WalletDtoMapper {
    public static WalletDto transformWalletEntityToDto(WalletEntity wallet) {
        return new WalletDto(wallet.getWalletId(), wallet.getBalance());
    }
}
