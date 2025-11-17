package org.example.studentjournal.controllers;

import org.example.studentjournal.POJO.Teacher;
import org.example.studentjournal.services.TeacherService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/teachers")
public class TeacherController {

    @Autowired
    private TeacherService teacherService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping
    public String listTeachers(Model model) {
        List<Teacher> teachers = teacherService.getAllTeachers();
        model.addAttribute("teachers", teachers);
        return "teachers/list";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public String getTeacher(@PathVariable int id, Model model) {
        Optional<Teacher> teacher = teacherService.getTeacherById(id);
        model.addAttribute("teacher", teacher);
        return "teachers/form";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/new")
    public String newTeacherForm(Model model) {
        model.addAttribute("teacher", new Teacher());
        return "teachers/form";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PostMapping
    public String saveTeacher(@ModelAttribute Teacher teacher) {
        teacherService.saveTeacher(teacher);
        return "redirect:/teachers";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public String deleteTeacher(@PathVariable int id) {
        teacherService.deleteTeacher(id);
        return "redirect:/teachers";
    }
}
