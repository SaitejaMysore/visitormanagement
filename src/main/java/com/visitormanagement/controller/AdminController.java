package com.visitormanagement.controller;

import com.visitormanagement.entity.User;
import com.visitormanagement.entity.Visitor;
import com.visitormanagement.service.NotificationService;
import com.visitormanagement.service.ReportService;
import com.visitormanagement.service.UserService;
import com.visitormanagement.service.VisitorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private VisitorService visitorService;
    
    @Autowired
    private NotificationService notificationService;
    
    @Autowired
    private ReportService reportService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model,
                           @RequestParam(required = false) Long userId,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<User> users = userService.findAllUsers();
        List<Visitor> visitors;
        
        if (userId != null) {
            User selectedUser = userService.findById(userId);
            if (selectedUser != null) {
                if (startDate != null && endDate != null) {
                    visitors = visitorService.findByUserAndDateRange(selectedUser, startDate, endDate);
                } else {
                    visitors = visitorService.findByUser(selectedUser);
                }
            } else {
                visitors = visitorService.findAllVisitors();
            }
        } else if (startDate != null && endDate != null) {
            visitors = visitorService.findByDateRange(startDate, endDate);
        } else {
            visitors = visitorService.findAllVisitors();
        }
        
        // Statistics
        long totalUsers = userService.getTotalUsersCount();
        long totalAdmins = userService.getTotalAdminsCount();
        long totalVisitors = visitorService.getTotalVisitorsCount();
        long todayVisitors = visitorService.getTodayVisitorsCount();
        
        model.addAttribute("users", users);
        model.addAttribute("visitors", visitors);
        model.addAttribute("selectedUserId", userId);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        model.addAttribute("totalUsers", totalUsers);
        model.addAttribute("totalAdmins", totalAdmins);
        model.addAttribute("totalVisitors", totalVisitors);
        model.addAttribute("todayVisitors", todayVisitors);
        
        return "admin-dashboard";
    }
    
    @PostMapping("/visitors/{id}/notify")
    public String notifyUser(@PathVariable Long id,
                            @RequestParam String message,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        Visitor visitor = visitorService.findById(id).orElse(null);
        if (visitor != null) {
            notificationService.createNotification(visitor.getCreatedBy(), visitor, message);
            redirectAttributes.addFlashAttribute("success", "Notification sent successfully!");
        } else {
            redirectAttributes.addFlashAttribute("error", "Visitor not found.");
        }
        return "redirect:/admin/dashboard";
    }
    
    @GetMapping("/reports/csv")
    public ResponseEntity<String> downloadCSVReport(@RequestParam(required = false) Long userId,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Visitor> visitors = getFilteredVisitors(userId, startDate, endDate);
        String csvContent = reportService.generateCSVReport(visitors);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=visitors_report.csv")
                .contentType(MediaType.parseMediaType("text/csv"))
                .body(csvContent);
    }
    
    @GetMapping("/reports/pdf")
    public ResponseEntity<byte[]> downloadPDFReport(@RequestParam(required = false) Long userId,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                                                   @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        List<Visitor> visitors = getFilteredVisitors(userId, startDate, endDate);
        byte[] pdfContent = reportService.generatePDFReport(visitors);
        
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=visitors_report.pdf")
                .contentType(MediaType.APPLICATION_PDF)
                .body(pdfContent);
    }
    
    private List<Visitor> getFilteredVisitors(Long userId, LocalDate startDate, LocalDate endDate) {
        if (userId != null) {
            User selectedUser = userService.findById(userId);
            if (selectedUser != null) {
                if (startDate != null && endDate != null) {
                    return visitorService.findByUserAndDateRange(selectedUser, startDate, endDate);
                } else {
                    return visitorService.findByUser(selectedUser);
                }
            }
        } else if (startDate != null && endDate != null) {
            return visitorService.findByDateRange(startDate, endDate);
        }
        return visitorService.findAllVisitors();
    }
}