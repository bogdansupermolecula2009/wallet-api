package ru.andrianov.wallet_api;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import ru.andrianov.wallet_api.controllers.WalletController;
import ru.andrianov.wallet_api.entities.dto.UpdatedWalletDto;
import ru.andrianov.wallet_api.entities.dto.WalletDto;
import ru.andrianov.wallet_api.entities.enums.OperationType;
import ru.andrianov.wallet_api.exceptions.InsufficientFundsException;
import ru.andrianov.wallet_api.exceptions.WalletNotFoundException;
import ru.andrianov.wallet_api.services.WalletService;

import java.math.BigDecimal;
import java.util.UUID;

import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


@WebMvcTest(WalletController.class)
@ExtendWith(MockitoExtension.class)
class WalletControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private WalletService walletService;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void testGetBalance_Success() throws Exception {

        UUID walletId = UUID.randomUUID();
        WalletDto walletDto = new WalletDto(walletId, BigDecimal.valueOf(1000));

        when(walletService.getWallet(walletId)).thenReturn(walletDto);

        mockMvc.perform(get("/api/v1/wallets/{walletID}", walletId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletId.toString()))
                .andExpect(jsonPath("$.balance").value(1000));
    }

    @Test
    void testGetBalance_WalletNotFound() throws Exception {

        UUID walletId = UUID.randomUUID();

        when(walletService.getWallet(walletId)).thenThrow(new WalletNotFoundException("Wallet not found"));

        mockMvc.perform(get("/api/v1/wallets/{walletID}", walletId))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.message").value("Wallet not found"));
    }

    @Test
    void testChangeBalance_SuccessDeposit() throws Exception {

        UpdatedWalletDto walletDto = new UpdatedWalletDto();
        walletDto.setWalletId(UUID.randomUUID());
        walletDto.setAmount(BigDecimal.valueOf(500));
        walletDto.setOperationType(OperationType.DEPOSIT);

        WalletDto updatedWallet = new WalletDto(walletDto.getWalletId(), BigDecimal.valueOf(1500));

        when(walletService.changeBalance(walletDto)).thenReturn(updatedWallet);
        System.out.println("Returned wallet: " + walletService.changeBalance(walletDto));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(walletDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletDto.getWalletId().toString()))
                .andExpect(jsonPath("$.balance").value(1500));
    }

    @Test
    void testChangeBalance_InsufficientBalance() throws Exception {

        UpdatedWalletDto walletDto = new UpdatedWalletDto();
        walletDto.setWalletId(UUID.randomUUID());
        walletDto.setAmount(BigDecimal.valueOf(500));
        walletDto.setOperationType(OperationType.WITHDRAW);

        WalletDto updatedWallet = new WalletDto(walletDto.getWalletId(), BigDecimal.valueOf(400));

        when(walletService.changeBalance(walletDto))
                .thenThrow(new InsufficientFundsException("Insufficient funds for withdrawal. Available: " + 400 + ", required: " + 500));

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(walletDto)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Insufficient funds for withdrawal. Available: " + 400 + ", required: " + 500))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testChangeBalance_SuccessWithdraw() throws Exception {

        UpdatedWalletDto walletDto = new UpdatedWalletDto();
        walletDto.setWalletId(UUID.randomUUID());
        walletDto.setAmount(BigDecimal.valueOf(100));
        walletDto.setOperationType(OperationType.WITHDRAW);

        WalletDto walletDtoAfterWithdraw = new WalletDto(walletDto.getWalletId(), BigDecimal.valueOf(300));

        when(walletService.changeBalance(walletDto))
                .thenReturn(walletDtoAfterWithdraw);

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(walletDto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.walletId").value(walletDto.getWalletId().toString()))
                .andExpect(jsonPath("$.balance").value(300));
    }

    @Test
    void testChangeBalance_InvalidJson() throws Exception {

        String invalidJson = "{\"walletId\":null,\"amount\":null,\"operationType\":null}";

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid JSON: walletId, amount, and operationType are required"));
    }

    @Test
    void testChangeBalance_InvalidJsonFormat() throws Exception {

        String invalidJson = "{ \"key\": \"value\" ";

        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(invalidJson))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid JSON format"))
                .andExpect(jsonPath("$.status").value(400));
    }

    @Test
    void testChangeBalance_InvalidOperationType() throws Exception {

        String invalidOperation = "{ \"walletId\": \"" + UUID.randomUUID() + "\", \"amount\": 100, \"operationType\": \"TRANSFER\" }";


        mockMvc.perform(post("/api/v1/wallet")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(invalidOperation)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.message").value("Invalid JSON format"));
    }


}

