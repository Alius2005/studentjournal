package org.example.studentjournal.services;

import org.example.studentjournal.DbManager;
import org.example.studentjournal.POJO.Teacher;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class TeacherService {

    @Autowired
    private DbManager dbManager;

    public List<Teacher> getAllTeachers() {
        try {
            return dbManager.getTeachers();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching teachers", e);
        }
    }

    public Optional<Teacher> getTeacherById(int id) {
        return getAllTeachers().stream().filter(t -> t.getId() == id).findFirst();
    }

    public void saveTeacher(Teacher teacher) {
        try {
            if (teacher.getId() == 0) {
                dbManager.insertTeacher(teacher.getFirstName(), teacher.getLastName(), teacher.getMiddleName(), teacher.getEmail(), teacher.getPhone(), teacher.getDepartment());
            } else {
                dbManager.updateTeacher(teacher.getId(), teacher.getFirstName(), teacher.getLastName(), teacher.getMiddleName(), teacher.getEmail(), teacher.getPhone(), teacher.getDepartment());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving teacher", e);
        }
    }

        public void deleteTeacher(int id) {
        try {
            dbManager.deleteTeacher(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting teacher", e);
        }
    }
}
