package org.example.studentjournal.controllers;

import org.example.studentjournal.POJO.Attendance;
import org.example.studentjournal.services.AttendanceService;
import org.example.studentjournal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/attendance")
public class AttendanceController {

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or (hasRole('HEAD_STUDENT') and @userService.isHeadOfGroup(#groupId, authentication.name)) or (hasRole('STUDENT') and @userService.isInGroup(#groupId, authentication.name))")
    @GetMapping("/group/{groupId}")
    public String getAttendancesByGroup(@PathVariable int groupId, Model model, Authentication authentication) {
        List<Attendance> attendances = attendanceService.getAttendancesByGroup(groupId);
        model.addAttribute("attendances", attendances);
        model.addAttribute("groupId", groupId);
        return "attendance/list";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public String getAttendance(@PathVariable int id, Model model) {
        Optional<Attendance> attendance = attendanceService.getAttendanceById(id);
        model.addAttribute("attendance", attendance);
        return "attendance/form";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/new")
    public String newAttendanceForm(Model model) {
        model.addAttribute("attendance", new Attendance());
        return "attendance/form";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or (hasRole('HEAD_STUDENT') and @userService.isHeadOfGroup(#groupId, authentication.name))")
    @PostMapping("/group/{groupId}")
    public String saveAttendance(@PathVariable int groupId, @ModelAttribute Attendance attendance, Authentication authentication) {
        attendanceService.saveAttendance(attendance);
        return "redirect:/attendance/group/" + groupId;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or (hasRole('HEAD_STUDENT') and @userService.isHeadOfGroup(#groupId, authentication.name))")
    @PutMapping("/group/{groupId}/{id}")
    public String updateAttendance(@PathVariable int groupId, @PathVariable int id, @ModelAttribute Attendance attendance, Authentication authentication) {
        attendanceService.updateAttendanceByGroup(id, attendance.isPresent(), groupId);
        return "redirect:/attendance/group/" + groupId;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public String deleteAttendance(@PathVariable int id) {
        Optional<Attendance> attendanceOpt = attendanceService.getAttendanceById(id);
        attendanceOpt.ifPresent(attendance -> {
            attendanceService.deleteAttendance(id);
        });
        return "redirect:/attendance/group/1";
    }
}
