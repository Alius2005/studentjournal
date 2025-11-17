package org.example.studentjournal.services;

import org.example.studentjournal.DbManager;
import org.example.studentjournal.POJO.Lesson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class LessonService {

    @Autowired
    private DbManager dbManager;

    public List<Lesson> getAllLessons() {
        try {
            return dbManager.getLessons();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching lessons", e);
        }
    }

    public Optional<Lesson> getLessonById(int id) {
        return getAllLessons().stream().filter(g -> g.getId() == id).findFirst();
    }

    public void saveLesson(Lesson lesson) {
        try {
            if (lesson.getId() == 0) {
                dbManager.insertLesson(lesson.getSubjectId(), lesson.getPairNumber(), lesson.getType(), lesson.getRoomNumber(), lesson.getBuildingNumber(), lesson.getLessonDate());
            } else {
                dbManager.updateLesson(lesson.getId(), lesson.getSubjectId(), lesson.getPairNumber(), lesson.getType(), lesson.getRoomNumber(), lesson.getBuildingNumber(), lesson.getLessonDate());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving group", e);
        }
    }

    public void deleteLesson(int id) {
        try {
            dbManager.deleteLesson(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting lesson", e);
        }
    }
}
