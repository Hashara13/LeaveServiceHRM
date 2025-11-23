package com.example.leaveservicehrm.controller;

import com.example.leaveservicehrm.entity.Leave;
import com.example.leaveservicehrm.model.CurrentUser;
import com.example.leaveservicehrm.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@Controller
public class LeaveController {

    @Autowired
    private LeaveRepository leaveRepository;

    @Autowired
    private CurrentUser currentUser;

    @GetMapping("/")
    public String loginPage() {
        currentUser.clear();
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username,
                        @RequestParam String password,
                        @RequestParam String role,
                        Model model) {

        if (role.equals("admin")) {
            if (username.equals("admin") && password.equals("admin123")) {
                currentUser.setUsername(username);
                currentUser.setAdmin(true);
                return "redirect:/dashboard";
            } else {
                model.addAttribute("error", "Invalid Admin Credentials");
                return "login";
            }
        }

        if (role.equals("employee")) {
            if ((username.equals("emp1") && password.equals("123")) ||
                    (username.equals("emp2") && password.equals("123"))) {

                currentUser.setUsername(username);
                currentUser.setAdmin(false);
                return "redirect:/dashboard";
            } else {
                model.addAttribute("error", "Invalid Employee Credentials");
                return "login";
            }
        }

        model.addAttribute("error", "Invalid Login");
        return "login";
    }

    @GetMapping("/dashboard")
    public String dashboard() {
        if (currentUser.getUsername() == null) return "redirect:/";
        return currentUser.isAdmin() ? "redirect:/admin/leaves" : "redirect:/employee/apply";
    }


    @GetMapping("/employee/apply")
    public String applyLeaveForm(Model model) {
        if (!isEmployee()) return "redirect:/";
        model.addAttribute("leave", new Leave());
        model.addAttribute("username", currentUser.getUsername());
        return "employee/apply";
    }

    @PostMapping("/employee/apply")
    public String applyLeave(@ModelAttribute Leave leave) {
        if (!isEmployee()) return "redirect:/";
        leave.setEmployeeName(currentUser.getUsername());
        leave.setStatus(Leave.LeaveStatus.PENDING);
        leaveRepository.save(leave);
        return "redirect:/employee/myleaves";
    }

    @GetMapping("/employee/myleaves")
    public String myLeaves(Model model) {
        if (!isEmployee()) return "redirect:/";
        model.addAttribute("leaves", leaveRepository.findByEmployeeName(currentUser.getUsername()));
        return "employee/myleaves";
    }

    @GetMapping("/employee/edit/{id}")
    public String editLeave(@PathVariable Long id, Model model) {
        if (!isEmployee()) return "redirect:/";

        Optional<Leave> opt = leaveRepository.findById(id);
        if (opt.isEmpty()) return "redirect:/employee/myleaves";

        Leave leave = opt.get();
        if (!leave.getEmployeeName().equals(currentUser.getUsername()) ||
                leave.getStatus() != Leave.LeaveStatus.PENDING) {
            return "redirect:/employee/myleaves";
        }

        model.addAttribute("leave", leave);
        return "employee/edit";
    }

    @PostMapping("/employee/update")
    public String updateLeave(@ModelAttribute Leave leave) {
        if (!isEmployee()) return "redirect:/";

        leaveRepository.findById(leave.getId()).ifPresent(existing -> {
            if (existing.getEmployeeName().equals(currentUser.getUsername()) &&
                    existing.getStatus() == Leave.LeaveStatus.PENDING) {
                existing.setFromDate(leave.getFromDate());
                existing.setToDate(leave.getToDate());
                existing.setReason(leave.getReason());
                leaveRepository.save(existing);
            }
        });
        return "redirect:/employee/myleaves";
    }

    @GetMapping("/employee/delete/{id}")
    public String deleteLeave(@PathVariable Long id) {
        if (!isEmployee()) return "redirect:/";

        leaveRepository.findById(id).ifPresent(leave -> {
            if (leave.getEmployeeName().equals(currentUser.getUsername()) &&
                    leave.getStatus() == Leave.LeaveStatus.PENDING) {
                leaveRepository.delete(leave);
            }
        });
        return "redirect:/employee/myleaves";
    }

    @GetMapping("/admin/leaves")
    public String allLeaves(Model model) {
        if (!currentUser.isAdmin()) return "redirect:/";
        model.addAttribute("leaves", leaveRepository.findAll());
        return "admin/allleaves";
    }

    @GetMapping("/admin/approve/{id}")
    public String approve(@PathVariable Long id) {
        if (!currentUser.isAdmin()) return "redirect:/";
        changeStatus(id, Leave.LeaveStatus.APPROVED);
        return "redirect:/admin/leaves";
    }

    @GetMapping("/admin/reject/{id}")
    public String reject(@PathVariable Long id) {
        if (!currentUser.isAdmin()) return "redirect:/";
        changeStatus(id, Leave.LeaveStatus.REJECTED);
        return "redirect:/admin/leaves";
    }

    private void changeStatus(Long id, Leave.LeaveStatus newStatus) {
        Optional<Leave> optionalLeave = leaveRepository.findById(id);
        if (optionalLeave.isPresent()) {
            Leave leave = optionalLeave.get();
            if (leave.getStatus() == Leave.LeaveStatus.PENDING) {
                leave.setStatus(newStatus);
                leaveRepository.save(leave);
            }
        }
    }

    @GetMapping("/logout")
    public String logout() {
        currentUser.clear();
        return "redirect:/";
    }

    private boolean isEmployee() {
        return currentUser.getUsername() != null && !currentUser.isAdmin();
    }


    @PostMapping("/api/login")
    @ResponseBody
    public ResponseEntity<?> apiLogin(@RequestBody Map<String, String> req) {

        String username = req.get("username");
        String password = req.get("password");
        String role = req.get("role");

        if (role.equals("admin")) {
            if (username.equals("admin") && password.equals("admin123")) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "role", "admin",
                        "message", "Admin Login Successful"
                ));
            }
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Admin Credentials"));
        }

        if (role.equals("employee")) {
            if ((username.equals("emp1") && password.equals("123")) ||
                    (username.equals("emp2") && password.equals("123"))) {
                return ResponseEntity.ok(Map.of(
                        "status", "success",
                        "role", "employee",
                        "message", "Employee Login Successful"
                ));
            }
            return ResponseEntity.status(401).body(Map.of("error", "Invalid Employee Credentials"));
        }

        return ResponseEntity.status(400).body(Map.of("error", "Invalid Role"));
    }


    @PostMapping("/api/employee/apply")
    @ResponseBody
    public ResponseEntity<?> apiApply(@RequestBody Leave leave) {
        leave.setStatus(Leave.LeaveStatus.PENDING);
        leaveRepository.save(leave);
        return ResponseEntity.ok(Map.of(
                "message", "Leave Applied Successfully",
                "leave", leave
        ));
    }


    @GetMapping("/api/employee/leaves/{username}")
    @ResponseBody
    public ResponseEntity<?> apiEmployeeLeaves(@PathVariable String username) {
        return ResponseEntity.ok(leaveRepository.findByEmployeeName(username));
    }


    @GetMapping("/api/admin/leaves")
    @ResponseBody
    public ResponseEntity<?> apiAllLeaves() {
        return ResponseEntity.ok(leaveRepository.findAll());
    }


    @PostMapping("/api/admin/approve/{id}")
    @ResponseBody
    public ResponseEntity<?> apiApprove(@PathVariable Long id) {
        changeStatus(id, Leave.LeaveStatus.APPROVED);
        return ResponseEntity.ok(Map.of("message", "Leave Approved", "id", id));
    }

    @PostMapping("/api/admin/reject/{id}")
    @ResponseBody
    public ResponseEntity<?> apiReject(@PathVariable Long id) {
        changeStatus(id, Leave.LeaveStatus.REJECTED);
        return ResponseEntity.ok(Map.of("message", "Leave Rejected", "id", id));
    }
    @GetMapping("/api/leave/{id}")
    @ResponseBody
    public ResponseEntity<?> apiGetSingleLeave(@PathVariable Long id) {

        Optional<Leave> opt = leaveRepository.findById(id);

        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of(
                    "error", "Leave Not Found",
                    "id", id
            ));
        }

        return ResponseEntity.ok(opt.get());
    }

    @PutMapping("/api/employee/update/{id}")
    @ResponseBody
    public ResponseEntity<?> apiUpdateLeave(@PathVariable Long id, @RequestBody Leave newData) {

        Optional<Leave> opt = leaveRepository.findById(id);

        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Leave Not Found"));
        }

        Leave existing = opt.get();

        if (existing.getStatus() != Leave.LeaveStatus.PENDING) {
            return ResponseEntity.status(400).body(Map.of("error", "Only pending leaves can be updated"));
        }

        existing.setFromDate(newData.getFromDate());
        existing.setToDate(newData.getToDate());
        existing.setReason(newData.getReason());
        leaveRepository.save(existing);

        return ResponseEntity.ok(Map.of(
                "message", "Leave Updated Successfully",
                "leave", existing
        ));
    }
    @DeleteMapping("/api/employee/delete/{id}")
    @ResponseBody
    public ResponseEntity<?> apiDeleteLeave(@PathVariable Long id) {

        Optional<Leave> opt = leaveRepository.findById(id);

        if (opt.isEmpty()) {
            return ResponseEntity.status(404).body(Map.of("error", "Leave Not Found"));
        }

        Leave leave = opt.get();

        if (leave.getStatus() != Leave.LeaveStatus.PENDING) {
            return ResponseEntity.status(400).body(Map.of("error", "Only pending leaves can be deleted"));
        }

        leaveRepository.delete(leave);

        return ResponseEntity.ok(Map.of(
                "message", "Leave Deleted Successfully",
                "id", id
        ));
    }

}
