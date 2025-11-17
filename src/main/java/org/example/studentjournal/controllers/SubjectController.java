package org.example.studentjournal.controllers;

import org.example.studentjournal.POJO.Subject;
import org.example.studentjournal.services.SubjectService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/subjects")
public class SubjectController {

    @Autowired
    private SubjectService subjectService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping
    public String listSubjects(Model model) {
        List<Subject> subjects = subjectService.getAllSubjects();
        model.addAttribute("subjects", subjects);
        return "subjects/list";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public String getSubject(@PathVariable int id, Model model) {
        Optional<Subject> subject = subjectService.getSubjectById(id);
        model.addAttribute("subject", subject);
        return "subjects/form";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/new")
    public String newSubjectForm(Model model) {
        model.addAttribute("subject", new Subject());
        return "subjects/form";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PostMapping
    public String saveSubject(@ModelAttribute Subject subject) {
        subjectService.saveSubject(subject);
        return "redirect:/subjects";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public String deleteSubject(@PathVariable int id) {
        subjectService.deleteSubject(id);
        return "redirect:/subjects";
    }
}
