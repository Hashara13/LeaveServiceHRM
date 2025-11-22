package com.example.leaveservicehrm;

import com.example.leaveservicehrm.entity.Leave;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;

public interface LeaveRepository extends JpaRepository<Leave, Long> {
    List<Leave> findByEmployeeName(String employeeName);
}
