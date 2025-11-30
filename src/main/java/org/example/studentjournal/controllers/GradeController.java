package org.example.studentjournal.controllers;

import org.example.studentjournal.POJO.Grade;
import org.example.studentjournal.services.GradeService;
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
@RequestMapping("/grades")
public class GradeController {

    @Autowired
    private GradeService gradeService;

    @Autowired
    private UserService userService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or (hasRole('HEAD_STUDENT') and @userService.isHeadOfGroup(#groupId, authentication.name))")
    @GetMapping("/group/{groupId}")
    public String getGradesByGroup(@PathVariable int groupId, Model model, Authentication authentication) {
        List<Grade> grades = gradeService.getGradesByGroup(groupId);
        model.addAttribute("grades", grades);
        model.addAttribute("groupId", groupId);
        return "grades/list";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public String getGrade(@PathVariable int id, Model model) {
        Optional<Grade> grade = gradeService.getGradeById(id);
        model.addAttribute("grade", grade);
        return "grades/form";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/new")
    public String newGradeForm(Model model) {
        model.addAttribute("grade", new Grade());
        return "grades/form";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PostMapping("/group/{groupId}")
    public String saveGrade(@PathVariable int groupId, @ModelAttribute Grade grade) {
        gradeService.saveGrade(grade);
        return "redirect:/grades/group/" + groupId;
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public String deleteGrade(@PathVariable int id) {
        Optional<Grade> gradeOpt = gradeService.getGradeById(id);
        gradeOpt.ifPresent(grade -> {
            gradeService.deleteGrade(id);
        });
        return "redirect:/grades/group/1";
    }
}
