package org.example.studentjournal.controllers;

import org.example.studentjournal.POJO.Lesson;
import org.example.studentjournal.services.LessonService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/lessons")
public class LessonController {

    @Autowired
    private LessonService lessonService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('HEAD_STUDENT') or hasRole('STUDENT')")
    @GetMapping
    public String listLessons(Model model) {
        List<Lesson> lessons = lessonService.getAllLessons();
        model.addAttribute("lessons", lessons);
        return "lessons/list";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public String getLesson(@PathVariable int id, Model model) {
        Optional<Lesson> lesson = lessonService.getLessonById(id);
        model.addAttribute("lesson", lesson);
        return "lessons/form";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/new")
    public String newLessonForm(Model model) {
        model.addAttribute("lesson", new Lesson());
        return "lessons/form";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PostMapping
    public String saveLesson(@ModelAttribute Lesson lesson) {
        lessonService.saveLesson(lesson);
        return "redirect:/lessons";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public String deleteLesson(@PathVariable int id) {
        lessonService.deleteLesson(id);
        return "redirect:/lessons";
    }
}
