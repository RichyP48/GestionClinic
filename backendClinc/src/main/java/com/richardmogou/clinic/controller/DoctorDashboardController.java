package com.richardmogou.clinic.controller;

import com.richardmogou.clinic.model.core.Appointment;
import com.richardmogou.clinic.model.security.User;
import com.richardmogou.clinic.service.DashboardService;
import com.richardmogou.clinic.service.core.AppointmentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/doctor/dashboard")
@RequiredArgsConstructor
@PreAuthorize("hasRole('DOCTOR')") // Ensure only DOCTOR can access
public class DoctorDashboardController {

    private final DashboardService dashboardService;
    private final AppointmentService appointmentService; // Inject AppointmentService

    // Endpoint for the main statistic cards
    @GetMapping("/stats")
    public ResponseEntity<Map<String, Object>> getDashboardStats(@AuthenticationPrincipal User currentUser) {
        Map<String, Object> stats = new HashMap<>();
        Long doctorId = currentUser.getId();

        stats.put("appointmentsToday", dashboardService.getDoctorAppointmentsTodayCount(doctorId));
        stats.put("totalPatients", dashboardService.getDoctorTotalPatientsCount(doctorId));
        stats.put("pendingPrescriptions", dashboardService.getDoctorPendingPrescriptionsCount(doctorId));
        // Getting "Next Appointment" details might be complex here, consider separate endpoint or frontend logic
        stats.put("nextAppointmentTime", "N/A"); // Placeholder

        return ResponseEntity.ok(stats);
    }

    // Endpoint for today's appointments list
    @GetMapping("/today-appointments")
    public ResponseEntity<List<Appointment>> getTodayAppointments(@AuthenticationPrincipal User currentUser) {
        // Assuming AppointmentService has a method like getAppointmentsForDoctorOnDate
        List<Appointment> appointments = appointmentService.getAppointmentsForDoctorOnDate(currentUser.getId(), LocalDate.now());
        return ResponseEntity.ok(appointments);
    }

}