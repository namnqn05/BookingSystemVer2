package com.example.booking_system.controller;

import com.example.booking_system.dto.request.UpdateAccountRequest;
import com.example.booking_system.dto.response.AccountResponse;
import com.example.booking_system.dto.response.BookingResponse;
import com.example.booking_system.security.UserPrincipal;
import com.example.booking_system.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<AccountResponse> getAccount(@AuthenticationPrincipal UserPrincipal principal) {
        return ResponseEntity.ok(accountService.getAccount(principal.getUsername()));
    }

    @PutMapping
    public ResponseEntity<AccountResponse> updateAccount(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestBody UpdateAccountRequest request) {
        return ResponseEntity.ok(accountService.updateAccount(principal.getUsername(), request));
    }

    @GetMapping("/invoices")
    public ResponseEntity<Page<BookingResponse>> getInvoices(
            @AuthenticationPrincipal UserPrincipal principal,
            @RequestParam(required = false) String q,
            Pageable pageable) {
        return ResponseEntity.ok(accountService.getInvoices(principal.getUsername(), q, pageable));
    }

    @GetMapping("/invoices/export")
    public void exportInvoicesToCSV(
            @AuthenticationPrincipal UserPrincipal principal,
            jakarta.servlet.http.HttpServletResponse response) throws IOException {
        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=invoices_export.csv");

        OutputStream os = response.getOutputStream();
        os.write(0xEF);
        os.write(0xBB);
        os.write(0xBF);

        PrintWriter writer = new PrintWriter(new OutputStreamWriter(os, StandardCharsets.UTF_8));
        accountService.exportInvoicesCsv(principal.getUsername(), writer);
        writer.flush();
    }
}
