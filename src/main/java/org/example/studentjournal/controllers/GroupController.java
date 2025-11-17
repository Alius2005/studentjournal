package org.example.studentjournal.controllers;

import org.example.studentjournal.POJO.Group;
import org.example.studentjournal.services.GroupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/groups")
public class GroupController {

    @Autowired
    private GroupService groupService;

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER') or hasRole('HEAD_STUDENT') or hasRole('STUDENT')")
    @GetMapping
    public String listGroups(Model model) {
        List<Group> groups = groupService.getAllGroups();
        model.addAttribute("groups", groups);
        return "groups/list";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/{id}")
    public String getGroup(@PathVariable int id, Model model) {
        Optional<Group> group = groupService.getGroupById(id);
        model.addAttribute("group", group);
        return "groups/form";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @GetMapping("/new")
    public String newGroupForm(Model model) {
        model.addAttribute("group", new Group());
        return "groups/form";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @PostMapping
    public String saveGroup(@ModelAttribute Group group) {
        groupService.saveGroup(group);
        return "redirect:/groups";
    }

    @PreAuthorize("hasRole('ADMIN') or hasRole('TEACHER')")
    @DeleteMapping("/{id}")
    public String deleteGroup(@PathVariable int id) {
        groupService.deleteGroup(id);
        return "redirect:/groups";
    }
}
