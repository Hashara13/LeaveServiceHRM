package com.example.leaveservicehrm.controller;

import com.example.leaveservicehrm.entity.Leave;
import com.example.leaveservicehrm.model.CurrentUser;
import com.example.leaveservicehrm.repository.LeaveRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;

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
}
