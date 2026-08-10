package com.example.booking_system.service;

import com.example.booking_system.dto.request.UpdateAccountRequest;
import com.example.booking_system.dto.response.AccountResponse;
import com.example.booking_system.dto.response.BookingResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AccountService {
    AccountResponse getAccount(String email);
    AccountResponse updateAccount(String currentEmail, UpdateAccountRequest request);
    Page<BookingResponse> getInvoices(String currentEmail, String q, Pageable pageable);
    void exportInvoicesCsv(String currentEmail, java.io.PrintWriter writer);
}
