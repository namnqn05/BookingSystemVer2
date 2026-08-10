package com.example.booking_system.controller;

import com.example.booking_system.dto.response.AccountResponse;
import com.example.booking_system.service.AccountService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.data.domain.Page;
import com.example.booking_system.dto.response.BookingResponse;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.RequestBody;
import com.example.booking_system.dto.request.UpdateAccountRequest;

@RestController
@RequestMapping("/api/account")
public class AccountController {

    private final AccountService accountService;

    public AccountController(AccountService accountService) {
        this.accountService = accountService;
    }

    @GetMapping
    public ResponseEntity<AccountResponse> getAccount() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email;
        if (authentication.getPrincipal() instanceof UserDetails) {
            email = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            email = authentication.getPrincipal().toString();
        }

        return ResponseEntity.ok(accountService.getAccount(email));
    }

    @PutMapping
    public ResponseEntity<AccountResponse> updateAccount(@RequestBody UpdateAccountRequest request) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email;
        if (authentication.getPrincipal() instanceof UserDetails) {
            email = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            email = authentication.getPrincipal().toString();
        }

        return ResponseEntity.ok(accountService.updateAccount(email, request));
    }
    
    @GetMapping("/invoices")
    public ResponseEntity<Page<BookingResponse>> getInvoices(
            @RequestParam(required = false) String q,
            Pageable pageable) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return ResponseEntity.status(401).build();
        }

        String email;
        if (authentication.getPrincipal() instanceof UserDetails) {
            email = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            email = authentication.getPrincipal().toString();
        }

        return ResponseEntity.ok(accountService.getInvoices(email, q, pageable));
    }

    @GetMapping("/invoices/export")
    public void exportInvoicesToCSV(jakarta.servlet.http.HttpServletResponse response) throws java.io.IOException {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            response.setStatus(401);
            return;
        }

        String email;
        if (authentication.getPrincipal() instanceof UserDetails) {
            email = ((UserDetails) authentication.getPrincipal()).getUsername();
        } else {
            email = authentication.getPrincipal().toString();
        }

        response.setContentType("text/csv; charset=UTF-8");
        response.setHeader("Content-Disposition", "attachment; filename=invoices_export.csv");
        
        // Write BOM for UTF-8 correctly reading in Excel
        java.io.OutputStream os = response.getOutputStream();
        os.write(0xEF);
        os.write(0xBB);
        os.write(0xBF);

        java.io.PrintWriter writer = new java.io.PrintWriter(new java.io.OutputStreamWriter(os, java.nio.charset.StandardCharsets.UTF_8));
        accountService.exportInvoicesCsv(email, writer);
        writer.flush();
    }
}
