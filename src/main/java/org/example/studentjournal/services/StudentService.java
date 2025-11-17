package org.example.studentjournal.services;

import org.example.studentjournal.DbManager;
import org.example.studentjournal.POJO.Student;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

    @Autowired
    private DbManager dbManager;

    public List<Student> getAllStudents() {
        try {
            return dbManager.getStudents();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching students", e);
        }
    }

    public Optional<Student> getStudentById(int id) {
        return getAllStudents().stream().filter(s -> s.getId() == id).findFirst();
    }

    public void saveStudent(Student student) {
        try {
            if (student.getId() == 0) {
                dbManager.insertStudent(student.getFirstName(), student.getLastName(), student.getMiddleName(),
                        student.getBirthDate(), student.getGroupId(), student.getContact(), student.getEmail());
            } else {
                dbManager.updateStudent(student.getId(), student.getFirstName(), student.getLastName(), student.getMiddleName(),
                        student.getBirthDate(), student.getGroupId(), student.getContact(), student.getEmail());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving student", e);
        }
    }

    public void deleteStudent(int id) {
        try {
            dbManager.deleteStudent(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting student", e);
        }
    }

    public List<Student> getStudentsByGroup(int groupId) {
        try {
            return dbManager.getStudentsByGroup(groupId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching students by group", e);
        }
    }
}
