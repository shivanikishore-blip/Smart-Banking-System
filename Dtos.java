package com.smartbank.dto;

import jakarta.validation.constraints.*;
import java.math.BigDecimal;

public final class Dtos {
    private Dtos() {}

    public record CreateAccountRequest(
            @NotBlank(message = "Name is required") String holderName,
            @NotBlank(message = "Email is required") @Email(message = "Invalid email") String email,
            @Pattern(regexp = "\\d{10}", message = "Phone must be 10 digits") String phone,
            @Pattern(regexp = "\\d{4}", message = "PIN must be exactly 4 digits") String pin,
            @NotBlank(message = "Account type is required") String accountType,
            @NotNull(message = "Initial deposit is required")
            @DecimalMin(value = "500.00", message = "Minimum opening balance is 500") BigDecimal initialDeposit) {}

    public record LoginRequest(
            @NotBlank(message = "Account number is required") String accountNumber,
            @NotBlank(message = "PIN is required") String pin) {}

    public record AmountRequest(
            @NotNull(message = "Amount is required")
            @DecimalMin(value = "1.00", message = "Amount must be at least 1") BigDecimal amount) {}

    public record WithdrawRequest(
            @NotNull(message = "Amount is required")
            @DecimalMin(value = "1.00", message = "Amount must be at least 1") BigDecimal amount,
            @NotBlank(message = "PIN is required") String pin) {}

    public record TransferRequest(
            @NotBlank(message = "Sender account is required") String fromAccount,
            @NotBlank(message = "Receiver account is required") String toAccount,
            @NotNull(message = "Amount is required")
            @DecimalMin(value = "1.00", message = "Amount must be at least 1") BigDecimal amount,
            @NotBlank(message = "PIN is required") String pin,
            String remark) {}

    public record PinRequest(@NotBlank(message = "PIN is required") String pin) {}

    public record Stats(long totalAccounts, BigDecimal totalBalance, long totalTransactions) {}
}
