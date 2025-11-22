package com.example.leaveservicehrm.repository;

import com.example.leaveservicehrm.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRepository extends JpaRepository<com.example.leaveservicehrm.entity.Leave, Long> {
    List<com.example.leaveservicehrm.entity.Leave> findByEmployeeName(String employeeName);
}
