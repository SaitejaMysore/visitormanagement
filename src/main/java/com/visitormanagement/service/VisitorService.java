package com.visitormanagement.service;

import com.visitormanagement.entity.User;
import com.visitormanagement.entity.Visitor;
import com.visitormanagement.repository.VisitorRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Service
public class VisitorService {
    
    @Autowired
    private VisitorRepository visitorRepository;
    
    public Visitor saveVisitor(Visitor visitor) {
        return visitorRepository.save(visitor);
    }
    
    public List<Visitor> findByUser(User user) {
        return visitorRepository.findByCreatedByOrderByAppointmentDateDesc(user);
    }
    
    public List<Visitor> findAllVisitors() {
        return visitorRepository.findAll();
    }
    
    public Optional<Visitor> findById(Long id) {
        return visitorRepository.findById(id);
    }
    
    public void deleteVisitor(Long id) {
        visitorRepository.deleteById(id);
    }
    
    public List<Visitor> findByUserAndDateRange(User user, LocalDate startDate, LocalDate endDate) {
        return visitorRepository.findByCreatedByAndAppointmentDateBetweenOrderByAppointmentDateDesc(
            user, startDate, endDate);
    }
    
    public List<Visitor> findByUserAndPurpose(User user, String purpose) {
        return visitorRepository.findByCreatedByAndPurposeContainingIgnoreCaseOrderByAppointmentDateDesc(
            user, purpose);
    }
    
    public List<Visitor> findByUserWithFilters(User user, String purpose, LocalDate startDate, LocalDate endDate) {
        return visitorRepository.findByUserWithFilters(user, purpose, startDate, endDate);
    }
    
    public List<Visitor> findByDateRange(LocalDate startDate, LocalDate endDate) {
        return visitorRepository.findByAppointmentDateBetweenOrderByAppointmentDateDesc(startDate, endDate);
    }
    
    public long getTotalVisitorsCount() {
        return visitorRepository.countAllVisitors();
    }
    
    public long getTodayVisitorsCount() {
        return visitorRepository.countTodayVisitors();
    }
}