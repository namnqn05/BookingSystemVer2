package com.example.booking_system.service.impl;

import com.example.booking_system.dto.response.AccountResponse;
import com.example.booking_system.dto.response.BookingResponse;
import com.example.booking_system.model.Booking;
import com.example.booking_system.model.Room;
import com.example.booking_system.model.User;
import com.example.booking_system.repository.AccountRepository;
import com.example.booking_system.repository.BookingRepository;
import com.example.booking_system.repository.RoomRepository;
import com.example.booking_system.service.AccountService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;
    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public AccountServiceImpl(AccountRepository accountRepository, BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.accountRepository = accountRepository;
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public AccountResponse getAccount(String email) {
        User user = accountRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        return AccountResponse.fromEntity(user);
    }

    @Override
    public AccountResponse updateAccount(String currentEmail, com.example.booking_system.dto.request.UpdateAccountRequest request) {
        User user = accountRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!user.getEmail().equals(request.getEmail()) && accountRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Email is already registered");
        }

        user.setFullName(request.getFullName());
        user.setEmail(request.getEmail());

        accountRepository.save(user);
        return AccountResponse.fromEntity(user);
    }

    @Override
    public Page<BookingResponse> getInvoices(String currentEmail, String q, Pageable pageable) {
        User user = accountRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        Page<Booking> bookings = bookingRepository.findBookingsByUserIdWithSearch(user.getId(), q, pageable);

        return bookings.map(booking -> {
            Room room = roomRepository.findById(booking.getRoomId()).orElse(null);
            return BookingResponse.fromEntity(booking, room, user);
        });
    }

    @Override
    public void exportInvoicesCsv(String currentEmail, java.io.PrintWriter writer) {
        User user = accountRepository.findByEmail(currentEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        java.util.List<Booking> bookings = bookingRepository.findByUserIdOrderByStartTimeDesc(user.getId());

        writer.println("ID,Room Name,Title,Start Time,End Time,Status,Price Per Hour,Total Amount");

        for (Booking booking : bookings) {
            Room room = roomRepository.findById(booking.getRoomId()).orElse(null);
            String roomName = room != null ? room.getName() : "Unknown Room";

            writer.printf("%d,%s,%s,%s,%s,%s,%s,%s\n",
                    booking.getId(),
                    escapeSpecialCharacters(roomName),
                    escapeSpecialCharacters(booking.getTitle()),
                    booking.getStartTime(),
                    booking.getEndTime(),
                    booking.getStatus(),
                    booking.getPricePerHour(),
                    booking.getAmount()
            );
        }
    }

    private String escapeSpecialCharacters(String data) {
        if (data == null) return "";
        String escapedData = data.replaceAll("\\R", " ");
        if (data.contains(",") || data.contains("\"") || data.contains("'")) {
            data = data.replace("\"", "\"\"");
            escapedData = "\"" + data + "\"";
        }
        return escapedData;
    }
}
