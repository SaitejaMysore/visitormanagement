package com.visitormanagement.controller;

import com.visitormanagement.entity.Notification;
import com.visitormanagement.entity.User;
import com.visitormanagement.entity.Visitor;
import com.visitormanagement.service.NotificationService;
import com.visitormanagement.service.UserService;
import com.visitormanagement.service.VisitorService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDate;
import java.util.List;

@Controller
@RequestMapping("/user")
public class UserController {
    
    @Autowired
    private VisitorService visitorService;
    
    @Autowired
    private UserService userService;
    
    @Autowired
    private NotificationService notificationService;
    
    @GetMapping("/dashboard")
    public String dashboard(Model model, Authentication authentication,
                           @RequestParam(required = false) String purpose,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
                           @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate endDate) {
        User currentUser = userService.findByEmail(authentication.getName()).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        List<Visitor> visitors;
        if (purpose != null || startDate != null || endDate != null) {
            visitors = visitorService.findByUserWithFilters(currentUser, purpose, startDate, endDate);
        } else {
            visitors = visitorService.findByUser(currentUser);
        }
        
        List<Notification> notifications = notificationService.findByUser(currentUser);
        long unreadCount = notificationService.countUnreadByUser(currentUser);
        
        model.addAttribute("visitors", visitors);
        model.addAttribute("notifications", notifications);
        model.addAttribute("unreadCount", unreadCount);
        model.addAttribute("user", currentUser);
        model.addAttribute("newVisitor", new Visitor());
        model.addAttribute("purpose", purpose);
        model.addAttribute("startDate", startDate);
        model.addAttribute("endDate", endDate);
        
        return "user-dashboard";
    }
    
    @PostMapping("/visitors")
    public String addVisitor(@Valid @ModelAttribute("newVisitor") Visitor visitor,
                            BindingResult result,
                            Authentication authentication,
                            RedirectAttributes redirectAttributes) {
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please fill all required fields correctly.");
            return "redirect:/user/dashboard";
        }
        
        User currentUser = userService.findByEmail(authentication.getName()).orElse(null);
        if (currentUser == null) {
            return "redirect:/login";
        }
        
        visitor.setCreatedBy(currentUser);
        visitorService.saveVisitor(visitor);
        
        redirectAttributes.addFlashAttribute("success", "Visitor appointment added successfully!");
        return "redirect:/user/dashboard";
    }
    
    @GetMapping("/visitors/{id}/edit")
    @ResponseBody
    public Visitor getVisitor(@PathVariable Long id, Authentication authentication) {
        User currentUser = userService.findByEmail(authentication.getName()).orElse(null);
        Visitor visitor = visitorService.findById(id).orElse(null);
        
        if (visitor != null && visitor.getCreatedBy().equals(currentUser)) {
            return visitor;
        }
        return null;
    }
    
    @PostMapping("/visitors/{id}/update")
    public String updateVisitor(@PathVariable Long id,
                               @Valid @ModelAttribute Visitor visitor,
                               BindingResult result,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(authentication.getName()).orElse(null);
        Visitor existingVisitor = visitorService.findById(id).orElse(null);
        
        if (existingVisitor == null || !existingVisitor.getCreatedBy().equals(currentUser)) {
            redirectAttributes.addFlashAttribute("error", "Visitor not found or access denied.");
            return "redirect:/user/dashboard";
        }
        
        if (result.hasErrors()) {
            redirectAttributes.addFlashAttribute("error", "Please fill all required fields correctly.");
            return "redirect:/user/dashboard";
        }
        
        existingVisitor.setName(visitor.getName());
        existingVisitor.setPhoneNumber(visitor.getPhoneNumber());
        existingVisitor.setPurpose(visitor.getPurpose());
        existingVisitor.setAppointmentDate(visitor.getAppointmentDate());
        
        visitorService.saveVisitor(existingVisitor);
        redirectAttributes.addFlashAttribute("success", "Visitor appointment updated successfully!");
        return "redirect:/user/dashboard";
    }
    
    @PostMapping("/visitors/{id}/delete")
    public String deleteVisitor(@PathVariable Long id,
                               Authentication authentication,
                               RedirectAttributes redirectAttributes) {
        User currentUser = userService.findByEmail(authentication.getName()).orElse(null);
        Visitor visitor = visitorService.findById(id).orElse(null);
        
        if (visitor == null || !visitor.getCreatedBy().equals(currentUser)) {
            redirectAttributes.addFlashAttribute("error", "Visitor not found or access denied.");
            return "redirect:/user/dashboard";
        }
        
        visitorService.deleteVisitor(id);
        redirectAttributes.addFlashAttribute("success", "Visitor appointment deleted successfully!");
        return "redirect:/user/dashboard";
    }
    
    @PostMapping("/notifications/{id}/read")
    @ResponseBody
    public String markNotificationAsRead(@PathVariable Long id) {
        notificationService.markAsRead(id);
        return "success";
    }
    
    @PostMapping("/notifications/mark-all-read")
    @ResponseBody
    public String markAllNotificationsAsRead(Authentication authentication) {
        User currentUser = userService.findByEmail(authentication.getName()).orElse(null);
        if (currentUser != null) {
            notificationService.markAllAsRead(currentUser);
        }
        return "success";
    }
}