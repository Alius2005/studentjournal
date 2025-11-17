package org.example.studentjournal.controllers;

import org.example.studentjournal.POJO.Grade;
import org.example.studentjournal.POJO.Attendance;
import org.example.studentjournal.services.GradeService;
import org.example.studentjournal.services.AttendanceService;
import org.example.studentjournal.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/journal")
public class JournalController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private AttendanceService attendanceService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or (hasRole('HEAD_STUDENT') and @userService.isHeadOfGroup(#groupId, authentication.name)) or (hasRole('STUDENT') and @userService.isInGroup(#groupId, authentication.name))")
    @GetMapping("/group/{groupId}")
    public String getGroupJournal(@PathVariable int groupId, Model model, Authentication authentication) {
        List<Grade> grades = gradeService.getGradesByGroup(groupId);
        List<Attendance> attendances = attendanceService.getAttendancesByGroup(groupId);
        model.addAttribute("grades", grades);
        model.addAttribute("attendances", attendances);
        model.addAttribute("groupId", groupId);
        return "journal/group";
    }
}
