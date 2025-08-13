package com.visitormanagement.repository;

import com.visitormanagement.entity.User;
import com.visitormanagement.entity.Visitor;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface VisitorRepository extends JpaRepository<Visitor, Long> {
    List<Visitor> findByCreatedByOrderByAppointmentDateDesc(User createdBy);
    
    List<Visitor> findByCreatedByAndAppointmentDateBetweenOrderByAppointmentDateDesc(
        User createdBy, LocalDate startDate, LocalDate endDate);
    
    List<Visitor> findByCreatedByAndPurposeContainingIgnoreCaseOrderByAppointmentDateDesc(
        User createdBy, String purpose);
    
    List<Visitor> findByAppointmentDateBetweenOrderByAppointmentDateDesc(
        LocalDate startDate, LocalDate endDate);
    
    @Query("SELECT v FROM Visitor v WHERE v.createdBy = :user AND " +
           "(:purpose IS NULL OR LOWER(v.purpose) LIKE LOWER(CONCAT('%', :purpose, '%'))) AND " +
           "(:startDate IS NULL OR v.appointmentDate >= :startDate) AND " +
           "(:endDate IS NULL OR v.appointmentDate <= :endDate) " +
           "ORDER BY v.appointmentDate DESC")
    List<Visitor> findByUserWithFilters(@Param("user") User user,
                                       @Param("purpose") String purpose,
                                       @Param("startDate") LocalDate startDate,
                                       @Param("endDate") LocalDate endDate);
    
    @Query("SELECT COUNT(v) FROM Visitor v")
    long countAllVisitors();
    
    @Query("SELECT COUNT(v) FROM Visitor v WHERE v.appointmentDate = CURRENT_DATE")
    long countTodayVisitors();
}