package org.example.studentjournal.services;

import org.example.studentjournal.DbManager;
import org.example.studentjournal.POJO.Grade;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class GradeService {

    @Autowired
    private DbManager dbManager;

    public List<Grade> getAllGrades() {
        try {
            return dbManager.getGrades();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching grades", e);
        }
    }

    public Optional<Grade> getGradeById(int id) {
        return getAllGrades().stream().filter(g -> g.getId() == id).findFirst();
    }

    public void saveGrade(Grade grade) {
        try {
            if (grade.getId() == 0) {
                dbManager.insertGrade(grade.getStudentId(), grade.getSubjectId(), grade.getGradeType(), grade.getGradeValue(), grade.getGradeDate());
            } else {
                dbManager.updateGrade(grade.getId(), grade.getStudentId(), grade.getSubjectId(), grade.getGradeType(), grade.getGradeValue(), grade.getGradeDate());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving grade", e);
        }
    }

    public void deleteGrade(int id) {
        try {
            dbManager.deleteGrade(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting grade", e);
        }
    }

    public List<Grade> getGradesByGroup(int groupId) {
        try {
            return dbManager.getGradesByGroup(groupId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching grades by group", e);
        }
    }
}
