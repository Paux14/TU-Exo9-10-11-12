package bank.controller;

import bank.exception.*;
import bank.model.Account;
import bank.service.AccountService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(AccountController.class)
class AccountControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private AccountService service;

    // --- POST /accounts ---

    @Test
    void createAccount_returns201() throws Exception {
        Account account = new Account("ACC-001", "Alice");
        when(service.createAccount("ACC-001", "Alice")).thenReturn(account);

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateAccountRequest("ACC-001", "Alice"))))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.number").value("ACC-001"))
                .andExpect(jsonPath("$.owner").value("Alice"))
                .andExpect(jsonPath("$.balance").value(0.0));
    }

    @Test
    void createAccount_duplicateNumber_returns409() throws Exception {
        when(service.createAccount("ACC-001", "Alice")).thenThrow(new AccountAlreadyExistsException("ACC-001"));

        mockMvc.perform(post("/accounts")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new CreateAccountRequest("ACC-001", "Alice"))))
                .andExpect(status().isConflict());
    }

    // --- GET /accounts ---

    @Test
    void getAllAccounts_returns200() throws Exception {
        when(service.getAllAccounts()).thenReturn(List.of(new Account("ACC-001", "Alice")));

        mockMvc.perform(get("/accounts"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.length()").value(1));
    }

    // --- GET /accounts/{number} ---

    @Test
    void getAccount_existingAccount_returns200() throws Exception {
        when(service.getAccount("ACC-001")).thenReturn(new Account("ACC-001", "Alice"));

        mockMvc.perform(get("/accounts/ACC-001"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.number").value("ACC-001"));
    }

    @Test
    void getAccount_unknownNumber_returns404() throws Exception {
        when(service.getAccount("UNKNOWN")).thenThrow(new AccountNotFoundException("UNKNOWN"));

        mockMvc.perform(get("/accounts/UNKNOWN"))
                .andExpect(status().isNotFound());
    }

    // --- POST /accounts/{number}/deposit ---

    @Test
    void deposit_validAmount_returns200() throws Exception {
        Account account = new Account("ACC-001", "Alice");
        account.setBalance(200.0);
        when(service.deposit("ACC-001", 200.0)).thenReturn(account);

        mockMvc.perform(post("/accounts/ACC-001/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AmountRequest(200.0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(200.0));
    }

    @Test
    void deposit_invalidAmount_returns400() throws Exception {
        when(service.deposit("ACC-001", 0)).thenThrow(new InvalidAmountException(0));

        mockMvc.perform(post("/accounts/ACC-001/deposit")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AmountRequest(0))))
                .andExpect(status().isBadRequest());
    }

    // --- POST /accounts/{number}/withdraw ---

    @Test
    void withdraw_validAmount_returns200() throws Exception {
        Account account = new Account("ACC-001", "Alice");
        account.setBalance(300.0);
        when(service.withdraw("ACC-001", 100.0)).thenReturn(account);

        mockMvc.perform(post("/accounts/ACC-001/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AmountRequest(100.0))))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.balance").value(300.0));
    }

    @Test
    void withdraw_insufficientFunds_returns422() throws Exception {
        when(service.withdraw("ACC-001", 9999.0))
                .thenThrow(new InsufficientFundsException("ACC-001", 100.0, 9999.0));

        mockMvc.perform(post("/accounts/ACC-001/withdraw")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new AmountRequest(9999.0))))
                .andExpect(status().isUnprocessableEntity());
    }

    // --- POST /accounts/transfer ---

    @Test
    void transfer_validTransfer_returns204() throws Exception {
        doNothing().when(service).transfer("ACC-001", "ACC-002", 100.0);

        mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransferRequest("ACC-001", "ACC-002", 100.0))))
                .andExpect(status().isNoContent());
    }

    @Test
    void transfer_insufficientFunds_returns422() throws Exception {
        doThrow(new InsufficientFundsException("ACC-001", 50.0, 500.0))
                .when(service).transfer("ACC-001", "ACC-002", 500.0);

        mockMvc.perform(post("/accounts/transfer")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(new TransferRequest("ACC-001", "ACC-002", 500.0))))
                .andExpect(status().isUnprocessableEntity());
    }
}
