package com.example.leaveservicehrm.entity;

import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDate;

@Entity
@Data
@Table(name = "leaves")
public class Leave {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String employeeName;
    private LocalDate fromDate;
    private LocalDate toDate;
    private String reason;

    @Enumerated(EnumType.STRING)
    private LeaveStatus status = LeaveStatus.PENDING;

    public enum LeaveStatus {
        PENDING, APPROVED, REJECTED
    }
}