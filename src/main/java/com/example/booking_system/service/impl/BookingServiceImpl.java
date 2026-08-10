package com.example.booking_system.service.impl;

import com.example.booking_system.dto.request.CreateBookingRequest;
import com.example.booking_system.dto.response.BookingResponse;
import com.example.booking_system.model.Booking;
import com.example.booking_system.model.BookingStatus;
import com.example.booking_system.model.Notification;
import com.example.booking_system.model.NotificationType;
import com.example.booking_system.model.Role;
import com.example.booking_system.model.Room;
import com.example.booking_system.model.User;
import com.example.booking_system.repository.BookingRepository;
import com.example.booking_system.repository.NotificationRepository;
import com.example.booking_system.repository.RoomRepository;
import com.example.booking_system.repository.UserRepository;
import com.example.booking_system.service.BookingService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Service
@Transactional
public class BookingServiceImpl implements BookingService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private final NotificationRepository notificationRepository;

    public BookingServiceImpl(BookingRepository bookingRepository,
                              RoomRepository roomRepository,
                              UserRepository userRepository,
                              NotificationRepository notificationRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
        this.notificationRepository = notificationRepository;
    }

    @Override
    @Transactional(readOnly = true)
    public Page<BookingResponse> getAllBookings(String date, Pageable pageable) {
        if (date != null && !date.trim().isEmpty()) {
            LocalDate localDate;
            try {
                localDate = LocalDate.parse(date.contains("T") ? date.split("T")[0] : date.trim());
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid date format. Expected yyyy-MM-dd");
            }
            LocalDateTime start = localDate.atStartOfDay();
            LocalDateTime end = localDate.plusDays(1).atStartOfDay();
            return bookingRepository.findByDateRange(start, end, pageable).map(this::toResponse);
        }

        return bookingRepository.findAll(pageable).map(this::toResponse);
    }

    @Override
    public BookingResponse createBooking(CreateBookingRequest request, String userEmail) {
        if (!request.getEndTime().isAfter(request.getStartTime())) {
            throw new IllegalArgumentException("End time must be after start time");
        }

        Room room = roomRepository.findById(request.getRoomId())
                .orElseThrow(() -> new IllegalArgumentException("Room not found with ID: " + request.getRoomId()));

        if (!room.getIsActive()) {
            throw new IllegalArgumentException("Cannot book an inactive room");
        }

        User user = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found with email: " + userEmail));

        boolean isOverlapping = bookingRepository.existsOverlappingBooking(
                request.getRoomId(),
                request.getStartTime(),
                request.getEndTime());

        if (isOverlapping) {
            throw new IllegalArgumentException("Phòng đã được đặt trong khoảng thời gian này");
        }

        BookingStatus initialStatus = (user.getRole() == Role.ROLE_ADMIN)
                ? BookingStatus.APPROVED
                : BookingStatus.PENDING;

        BigDecimal pricePerHour = room.getPricePerHour() != null ? room.getPricePerHour() : BigDecimal.ZERO;
        long minutes = Duration.between(request.getStartTime(), request.getEndTime()).toMinutes();
        BigDecimal amount = pricePerHour.multiply(BigDecimal.valueOf(minutes))
                .divide(BigDecimal.valueOf(60), 2, RoundingMode.HALF_UP);

        Booking booking = new Booking();
        booking.setRoomId(request.getRoomId());
        booking.setUserId(user.getId());
        booking.setTitle(request.getTitle());
        booking.setStartTime(request.getStartTime());
        booking.setEndTime(request.getEndTime());
        booking.setStatus(initialStatus);
        booking.setPricePerHour(pricePerHour);
        booking.setAmount(amount);

        Booking saved = bookingRepository.save(booking);

        if (user.getRole() != Role.ROLE_ADMIN) {
            java.util.List<User> admins = userRepository.findByRole(Role.ROLE_ADMIN);
            for (User admin : admins) {
                Notification notification = new Notification();
                notification.setRecipientUser(admin);
                notification.setTitle("New Booking Request");
                notification.setMessage("New booking request '" + saved.getTitle() + "' submitted by " + (user.getFullName() != null ? user.getFullName() : user.getEmail()) + ".");
                notification.setType(NotificationType.BOOKING_PENDING);
                notification.setReferenceType("BOOKING");
                notification.setReferenceId(saved.getId());
                notification.setRead(false);
                notification.setCreatedAt(Instant.now());
                notification.setCreatedBy(user.getId());
                notificationRepository.save(notification);
            }
        }

        return toResponse(saved);
    }

    @Override
    public BookingResponse approveBooking(Long id, String userEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + id));

        if (booking.getStatus() == BookingStatus.CANCELLED || booking.getStatus() == BookingStatus.EXPIRED) {
            throw new IllegalArgumentException("Cannot approve a cancelled or rejected booking");
        }

        User actorUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!actorUser.getId().equals(booking.getUserId()) && actorUser.getRole() != Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("You are not authorized to approve this booking");
        }

        booking.setStatus(BookingStatus.APPROVED);
        Booking updated = bookingRepository.save(booking);

        User bookingOwner = userRepository.findById(booking.getUserId()).orElse(actorUser);
        Notification notification = new Notification();
        notification.setRecipientUser(bookingOwner);
        notification.setTitle("Booking Approved");
        notification.setMessage("Your booking '" + booking.getTitle() + "' has been approved.");
        notification.setType(NotificationType.BOOKING_APPROVED);
        notification.setReferenceType("BOOKING");
        notification.setReferenceId(updated.getId());
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        notification.setCreatedBy(actorUser.getId());
        notificationRepository.save(notification);

        return toResponse(updated);
    }

    @Override
    public BookingResponse rejectBooking(Long id, String userEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + id));

        User actorUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!actorUser.getId().equals(booking.getUserId()) && actorUser.getRole() != Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("You are not authorized to reject this booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updated = bookingRepository.save(booking);

        User bookingOwner = userRepository.findById(booking.getUserId()).orElse(actorUser);
        Notification notification = new Notification();
        notification.setRecipientUser(bookingOwner);
        notification.setTitle("Booking Rejected");
        notification.setMessage("Your booking '" + booking.getTitle() + "' has been rejected.");
        notification.setType(NotificationType.BOOKING_REJECTED);
        notification.setReferenceType("BOOKING");
        notification.setReferenceId(updated.getId());
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        notification.setCreatedBy(actorUser.getId());
        notificationRepository.save(notification);

        return toResponse(updated);
    }

    @Override
    public BookingResponse cancelBooking(Long id, String userEmail) {
        Booking booking = bookingRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Booking not found with ID: " + id));

        if (booking.getStatus() != BookingStatus.APPROVED) {
            throw new IllegalArgumentException("Cannot cancel a booking which is not approved");
        }

        User actorUser = userRepository.findByEmail(userEmail)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));

        if (!actorUser.getId().equals(booking.getUserId()) && actorUser.getRole() != Role.ROLE_ADMIN) {
            throw new IllegalArgumentException("You are not authorized to cancel this booking");
        }

        booking.setStatus(BookingStatus.CANCELLED);
        Booking updated = bookingRepository.save(booking);

        User bookingOwner = userRepository.findById(booking.getUserId()).orElse(actorUser);
        Notification notification = new Notification();
        notification.setRecipientUser(bookingOwner);
        notification.setTitle("Booking Cancelled");
        notification.setMessage("Your booking '" + booking.getTitle() + "' has been cancelled.");
        notification.setType(NotificationType.BOOKING_CANCELLED);
        notification.setReferenceType("BOOKING");
        notification.setReferenceId(updated.getId());
        notification.setRead(false);
        notification.setCreatedAt(Instant.now());
        notification.setCreatedBy(actorUser.getId());
        notificationRepository.save(notification);

        return toResponse(updated);
    }

    private BookingResponse toResponse(Booking booking) {
        Room room = roomRepository.findById(booking.getRoomId()).orElse(null);
        User user = userRepository.findById(booking.getUserId()).orElse(null);

        return BookingResponse.fromEntity(booking, room, user);
    }
}
