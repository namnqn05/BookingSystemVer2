package com.example.booking_system.service.impl;

import com.example.booking_system.dto.response.BackendRevenuePeriod;
import com.example.booking_system.dto.response.DayRevenueResponse;
import com.example.booking_system.dto.response.RevenueResponse;
import com.example.booking_system.dto.response.RoomRevenueResponse;
import com.example.booking_system.model.Booking;
import com.example.booking_system.model.BookingStatus;
import com.example.booking_system.model.Room;
import com.example.booking_system.repository.BookingRepository;
import com.example.booking_system.repository.RoomRepository;
import com.example.booking_system.service.AdminRevenueService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.YearMonth;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.time.temporal.ChronoField;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AdminRevenueServiceImpl implements AdminRevenueService {

    private final BookingRepository bookingRepository;
    private final RoomRepository roomRepository;

    public AdminRevenueServiceImpl(BookingRepository bookingRepository, RoomRepository roomRepository) {
        this.bookingRepository = bookingRepository;
        this.roomRepository = roomRepository;
    }

    @Override
    public RevenueResponse getRevenue(String yearMonthStr) {
        YearMonth currentYm = parseYearMonth(yearMonthStr);
        String currentYmFormatted = currentYm.toString();

        LocalDateTime start = currentYm.atDay(1).atStartOfDay();
        LocalDateTime end = currentYm.plusMonths(1).atDay(1).atStartOfDay();

        List<Booking> monthBookings = bookingRepository.findByStartTimeBetween(start, end);
        List<Room> allRooms = roomRepository.findAll();

        Map<Long, Room> roomMap = allRooms.stream().collect(Collectors.toMap(Room::getId, r -> r, (r1, r2) -> r1));

        List<Booking> approvedBookings = monthBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .collect(Collectors.toList());

        long cancelledCount = monthBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CANCELLED || b.getStatus() == BookingStatus.EXPIRED)
                .count();

        int totalBookings = approvedBookings.size();
        BigDecimal totalAmount = BigDecimal.ZERO;

        Map<Long, BigDecimal> roomAmountMap = new HashMap<>();
        Map<Long, Integer> roomBookingCountMap = new HashMap<>();
        Map<LocalDate, BigDecimal> dayAmountMap = new HashMap<>();
        Map<LocalDate, Integer> dayBookingCountMap = new HashMap<>();

        for (Booking b : approvedBookings) {
            BigDecimal amt = calculateBookingAmount(b, roomMap.get(b.getRoomId()));
            totalAmount = totalAmount.add(amt);

            roomAmountMap.merge(b.getRoomId(), amt, BigDecimal::add);
            roomBookingCountMap.merge(b.getRoomId(), 1, Integer::sum);

            LocalDate bDate = b.getStartTime().toLocalDate();
            dayAmountMap.merge(bDate, amt, BigDecimal::add);
            dayBookingCountMap.merge(bDate, 1, Integer::sum);
        }

        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal averageAmount = totalBookings > 0
                ? totalAmount.divide(BigDecimal.valueOf(totalBookings), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long totalCombined = totalBookings + cancelledCount;
        BigDecimal cancellationRate = totalCombined > 0
                ? BigDecimal.valueOf((cancelledCount * 100.0) / totalCombined).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        // Previous period summary
        BackendRevenuePeriod previousPeriod = calculatePeriodSummary(currentYm.minusMonths(1), roomMap);

        // Breakdown By Room
        List<RoomRevenueResponse> byRoom = new ArrayList<>();
        for (Room room : allRooms) {
            BigDecimal rAmt = roomAmountMap.getOrDefault(room.getId(), BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            int rCount = roomBookingCountMap.getOrDefault(room.getId(), 0);
            BigDecimal sharePercent = totalAmount.compareTo(BigDecimal.ZERO) > 0
                    ? rAmt.multiply(BigDecimal.valueOf(100)).divide(totalAmount, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            byRoom.add(new RoomRevenueResponse(room.getId(), room.getName(), rCount, rAmt, sharePercent));
        }

        // Breakdown By Day
        List<DayRevenueResponse> byDay = new ArrayList<>();
        int daysInMonth = currentYm.lengthOfMonth();
        for (int day = 1; day <= daysInMonth; day++) {
            LocalDate date = currentYm.atDay(day);
            String dateStr = date.toString();
            int dCount = dayBookingCountMap.getOrDefault(date, 0);
            BigDecimal dAmt = dayAmountMap.getOrDefault(date, BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);

            byDay.add(new DayRevenueResponse(dateStr, dCount, dAmt));
        }

        return new RevenueResponse(
                currentYmFormatted,
                totalAmount,
                totalBookings,
                averageAmount,
                (int) cancelledCount,
                cancellationRate,
                previousPeriod,
                byRoom,
                byDay
        );
    }

    private BackendRevenuePeriod calculatePeriodSummary(YearMonth ym, Map<Long, Room> roomMap) {
        LocalDateTime start = ym.atDay(1).atStartOfDay();
        LocalDateTime end = ym.plusMonths(1).atDay(1).atStartOfDay();

        List<Booking> monthBookings = bookingRepository.findByStartTimeBetween(start, end);

        List<Booking> approvedBookings = monthBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .collect(Collectors.toList());

        long cancelledCount = monthBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.CANCELLED || b.getStatus() == BookingStatus.EXPIRED)
                .count();

        int totalBookings = approvedBookings.size();
        BigDecimal totalAmount = BigDecimal.ZERO;

        for (Booking b : approvedBookings) {
            totalAmount = totalAmount.add(calculateBookingAmount(b, roomMap.get(b.getRoomId())));
        }

        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);
        BigDecimal averageAmount = totalBookings > 0
                ? totalAmount.divide(BigDecimal.valueOf(totalBookings), 2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        long totalCombined = totalBookings + cancelledCount;
        BigDecimal cancellationRate = totalCombined > 0
                ? BigDecimal.valueOf((cancelledCount * 100.0) / totalCombined).setScale(2, RoundingMode.HALF_UP)
                : BigDecimal.ZERO;

        return new BackendRevenuePeriod(
                ym.toString(),
                totalAmount,
                totalBookings,
                averageAmount,
                (int) cancelledCount,
                cancellationRate
        );
    }

    private BigDecimal calculateBookingAmount(Booking booking, Room room) {
        if (booking.getAmount() != null && booking.getAmount().compareTo(BigDecimal.ZERO) > 0) {
            return booking.getAmount();
        }

        BigDecimal pricePerHour = booking.getPricePerHour();
        if (pricePerHour == null || pricePerHour.compareTo(BigDecimal.ZERO) == 0) {
            pricePerHour = room != null && room.getPricePerHour() != null ? room.getPricePerHour() : BigDecimal.ZERO;
        }

        if (booking.getStartTime() != null && booking.getEndTime() != null) {
            long minutes = Duration.between(booking.getStartTime(), booking.getEndTime()).toMinutes();
            double hours = minutes / 60.0;
            return pricePerHour.multiply(BigDecimal.valueOf(hours));
        }

        return BigDecimal.ZERO;
    }

    private YearMonth parseYearMonth(String str) {
        if (str == null || str.trim().isEmpty()) {
            return YearMonth.now();
        }
        String trimmed = str.trim();
        try {
            return YearMonth.parse(trimmed);
        } catch (Exception e) {
            try {
                DateTimeFormatter formatter = new DateTimeFormatterBuilder()
                        .appendPattern("yyyy-")
                        .appendValue(ChronoField.MONTH_OF_YEAR)
                        .toFormatter();
                return YearMonth.parse(trimmed, formatter);
            } catch (Exception ex) {
                return YearMonth.now();
            }
        }
    }

    @Override
    public Page<RoomRevenueResponse> getRevenueByRoom(String yearMonthStr, String q, Pageable pageable) {
        YearMonth currentYm = parseYearMonth(yearMonthStr);

        LocalDateTime start = currentYm.atDay(1).atStartOfDay();
        LocalDateTime end = currentYm.plusMonths(1).atDay(1).atStartOfDay();

        List<Booking> monthBookings = bookingRepository.findByStartTimeBetween(start, end);
        List<Room> allRooms = roomRepository.findAll();
        Map<Long, Room> roomMap = allRooms.stream().collect(Collectors.toMap(Room::getId, r -> r, (r1, r2) -> r1));

        List<Booking> approvedBookings = monthBookings.stream()
                .filter(b -> b.getStatus() == BookingStatus.APPROVED)
                .collect(Collectors.toList());

        BigDecimal totalAmount = BigDecimal.ZERO;
        Map<Long, BigDecimal> roomAmountMap = new HashMap<>();
        Map<Long, Integer> roomBookingCountMap = new HashMap<>();

        for (Booking b : approvedBookings) {
            BigDecimal amt = calculateBookingAmount(b, roomMap.get(b.getRoomId()));
            totalAmount = totalAmount.add(amt);
            roomAmountMap.merge(b.getRoomId(), amt, BigDecimal::add);
            roomBookingCountMap.merge(b.getRoomId(), 1, Integer::sum);
        }

        totalAmount = totalAmount.setScale(2, RoundingMode.HALF_UP);

        List<Room> targetRooms;
        if (q != null && !q.trim().isEmpty()) {
            targetRooms = roomRepository.findByNameContainingIgnoreCase(q.trim());
        } else {
            targetRooms = allRooms;
        }

        List<RoomRevenueResponse> responseList = new ArrayList<>();
        for (Room room : targetRooms) {
            BigDecimal rAmt = roomAmountMap.getOrDefault(room.getId(), BigDecimal.ZERO).setScale(2, RoundingMode.HALF_UP);
            int rCount = roomBookingCountMap.getOrDefault(room.getId(), 0);
            BigDecimal sharePercent = totalAmount.compareTo(BigDecimal.ZERO) > 0
                    ? rAmt.multiply(BigDecimal.valueOf(100)).divide(totalAmount, 2, RoundingMode.HALF_UP)
                    : BigDecimal.ZERO;

            responseList.add(new RoomRevenueResponse(room.getId(), room.getName(), rCount, rAmt, sharePercent));
        }

        if (pageable != null && pageable.getSort() != null && pageable.getSort().isSorted()) {
            Sort sort = pageable.getSort();
            responseList.sort((r1, r2) -> {
                for (Sort.Order order : sort) {
                    int result = compareByProperty(r1, r2, order.getProperty());
                    if (result != 0) {
                        return order.isAscending() ? result : -result;
                    }
                }
                return 0;
            });
        }

        if (pageable == null || pageable.isUnpaged()) {
            return new PageImpl<>(responseList);
        }

        int startIdx = (int) pageable.getOffset();
        if (startIdx >= responseList.size()) {
            return new PageImpl<>(Collections.emptyList(), pageable, responseList.size());
        }
        int endIdx = Math.min(startIdx + pageable.getPageSize(), responseList.size());
        List<RoomRevenueResponse> pageContent = responseList.subList(startIdx, endIdx);

        return new PageImpl<>(pageContent, pageable, responseList.size());
    }

    private int compareByProperty(RoomRevenueResponse r1, RoomRevenueResponse r2, String property) {
        switch (property) {
            case "roomId":
            case "id":
                return compareValues(r1.getRoomId(), r2.getRoomId());
            case "roomName":
            case "name":
                return compareValues(r1.getRoomName(), r2.getRoomName());
            case "bookingCount":
                return compareValues(r1.getBookingCount(), r2.getBookingCount());
            case "amount":
                return compareValues(r1.getAmount(), r2.getAmount());
            case "sharePercent":
                return compareValues(r1.getSharePercent(), r2.getSharePercent());
            default:
                return 0;
        }
    }

    private <T extends Comparable<T>> int compareValues(T v1, T v2) {
        if (v1 == null && v2 == null) return 0;
        if (v1 == null) return -1;
        if (v2 == null) return 1;
        return v1.compareTo(v2);
    }

    @Override
    public byte[] exportRevenueCsv(String yearMonthStr) {
        RevenueResponse revenue = getRevenue(yearMonthStr);

        StringBuilder sb = new StringBuilder();
        // Add UTF-8 BOM for compatibility with Excel
        sb.append("\uFEFF");

        // Summary Section
        sb.append("Year/Month,Total Amount,Total Bookings,Average Amount,Cancelled Count,Cancellation Rate (%)\n");
        sb.append(String.format("%s,%s,%d,%s,%d,%s\n\n",
                revenue.getYearMonth(),
                revenue.getTotalAmount() != null ? revenue.getTotalAmount() : "0.00",
                revenue.getTotalBookings() != null ? revenue.getTotalBookings() : 0,
                revenue.getAverageAmount() != null ? revenue.getAverageAmount() : "0.00",
                revenue.getCancelledCount() != null ? revenue.getCancelledCount() : 0,
                revenue.getCancellationRate() != null ? revenue.getCancellationRate() : "0.00"
        ));

        // Previous Period Section
        sb.append("Previous Period,Total Amount,Total Bookings,Average Amount,Cancelled Count,Cancellation Rate (%)\n");
        if (revenue.getPrevious() != null) {
            BackendRevenuePeriod prev = revenue.getPrevious();
            sb.append(String.format("%s,%s,%d,%s,%d,%s\n\n",
                    prev.getYearMonth(),
                    prev.getTotalAmount() != null ? prev.getTotalAmount() : "0.00",
                    prev.getTotalBookings() != null ? prev.getTotalBookings() : 0,
                    prev.getAverageAmount() != null ? prev.getAverageAmount() : "0.00",
                    prev.getCancelledCount() != null ? prev.getCancelledCount() : 0,
                    prev.getCancellationRate() != null ? prev.getCancellationRate() : "0.00"
            ));
        } else {
            sb.append("N/A,0.00,0,0.00,0,0.00\n\n");
        }

        // Room Breakdown Section
        sb.append("Breakdown By Room\n");
        sb.append("Room ID,Room Name,Booking Count,Total Amount,Share Percent (%)\n");
        if (revenue.getByRoom() != null) {
            for (RoomRevenueResponse room : revenue.getByRoom()) {
                sb.append(String.format("%d,%s,%d,%s,%s\n",
                        room.getRoomId(),
                        escapeCsvField(room.getRoomName()),
                        room.getBookingCount() != null ? room.getBookingCount() : 0,
                        room.getAmount() != null ? room.getAmount() : "0.00",
                        room.getSharePercent() != null ? room.getSharePercent() : "0.00"
                ));
            }
        }
        sb.append("\n");

        // Day Breakdown Section
        sb.append("Breakdown By Day\n");
        sb.append("Date,Booking Count,Total Amount\n");
        if (revenue.getByDay() != null) {
            for (DayRevenueResponse day : revenue.getByDay()) {
                sb.append(String.format("%s,%d,%s\n",
                        day.getDate(),
                        day.getBookingCount() != null ? day.getBookingCount() : 0,
                        day.getAmount() != null ? day.getAmount() : "0.00"
                ));
            }
        }

        return sb.toString().getBytes(java.nio.charset.StandardCharsets.UTF_8);
    }

    private String escapeCsvField(String field) {
        if (field == null) return "";
        String escaped = field.replaceAll("\\R", " ");
        if (escaped.contains(",") || escaped.contains("\"") || escaped.contains("'")) {
            escaped = escaped.replace("\"", "\"\"");
            escaped = "\"" + escaped + "\"";
        }
        return escaped;
    }
}

