package org.example.studentjournal.services;

import org.example.studentjournal.DbManager;
import org.example.studentjournal.POJO.Attendance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class AttendanceService {

    @Autowired
    private DbManager dbManager;

    public List<Attendance> getAllAttendances() {
        try {
            return dbManager.getAttendance();
        } catch (Exception e) {
            throw new RuntimeException("Error fetching attendances", e);
        }
    }

    public Optional<Attendance> getAttendanceById(int id) {
        return getAllAttendances().stream().filter(a -> a.getId() == id).findFirst();
    }

    public void saveAttendance(Attendance attendance) {
        try {
            if (attendance.getId() == 0) {
                dbManager.insertAttendance(attendance.getStudentId(), attendance.getLessonId(), attendance.isPresent());
            } else {
                dbManager.updateAttendance(attendance.getId(), attendance.getStudentId(), attendance.getLessonId(), attendance.isPresent());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error saving attendance", e);
        }
    }

    public void deleteAttendance(int id) {
        try {
            dbManager.deleteAttendance(id);
        } catch (Exception e) {
            throw new RuntimeException("Error deleting attendance", e);
        }
    }

    public List<Attendance> getAttendancesByGroup(int groupId) {
        try {
            return dbManager.getAttendancesByGroup(groupId);
        } catch (Exception e) {
            throw new RuntimeException("Error fetching attendances by group", e);
        }
    }

    public void updateAttendanceByGroup(int attendanceId, boolean isPresent, int groupId) {
        try {
            dbManager.updateAttendanceByGroup(attendanceId, isPresent, groupId);
        } catch (Exception e) {
            throw new RuntimeException("Error updating attendance by group", e);
        }
    }
}
