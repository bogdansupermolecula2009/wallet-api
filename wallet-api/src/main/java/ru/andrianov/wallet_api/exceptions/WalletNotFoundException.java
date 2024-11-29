package ru.andrianov.wallet_api.exceptions;


public class WalletNotFoundException extends RuntimeException{
    public WalletNotFoundException(String message) {
        super(message);
    }
}
