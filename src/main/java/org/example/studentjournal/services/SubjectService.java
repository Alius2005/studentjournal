package org.example.studentjournal.services;

import org.example.studentjournal.DbManager;
import org.example.studentjournal.POJO.Subject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class SubjectService {

    @Autowired
    private DbManager dbManager;

    public List<Subject> getAllSubjects() {
        try {
            return dbManager.getSubjects();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching subjects", e);
        }
    }

    public Optional<Subject> getSubjectById(int id) {
        return getAllSubjects().stream().filter(s -> s.getId() == id).findFirst();
    }

    public void saveSubject(Subject subject) {
        try {
            if (subject.getId() == 0) {
                dbManager.insertSubject(subject.getName(), subject.getTeacher(), subject.getSchedule());
            } else {
                dbManager.updateSubject(subject.getId(), subject.getName(), subject.getTeacher(), subject.getSchedule());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving subject", e);
        }
    }

    public void deleteSubject(int id) {
        try {
            dbManager.deleteSubject(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting subject", e);
        }
    }
}
