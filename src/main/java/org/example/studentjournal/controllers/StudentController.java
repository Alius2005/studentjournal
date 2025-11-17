package org.example.studentjournal.controllers;

import org.example.studentjournal.POJO.Student;
import org.example.studentjournal.services.StudentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/students")
public class StudentController {

    @Autowired
    private StudentService studentService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping
    public String listStudents(Model model) {
        List<Student> students = studentService.getAllStudents();
        model.addAttribute("students", students);
        return "students/list";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public String getStudent(@PathVariable int id, Model model) {
        Optional<Student> student = studentService.getStudentById(id);
        model.addAttribute("student", student);
        return "students/form";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/new")
    public String newStudentForm(Model model) {
        model.addAttribute("student", new Student());
        return "students/form";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PostMapping
    public String saveStudent(@ModelAttribute Student student) {
        studentService.saveStudent(student);
        return "redirect:/students";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public String deleteStudent(@PathVariable int id) {
        studentService.deleteStudent(id);
        return "redirect:/students";
    }
}
