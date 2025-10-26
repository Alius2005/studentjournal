package org.example.studentjournal.POJO;

public class Attendance {
    private int id;
    private int studentId;
    private int lessonId;
    private boolean isPresent;

    public Attendance() {}

    public Attendance(int id, int studentId, int lessonId, boolean isPresent) {
        this.id = id;
        this.studentId = studentId;
        this.lessonId = lessonId;
        this.isPresent = isPresent;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getLessonId() { return lessonId; }
    public void setLessonId(int lessonId) { this.lessonId = lessonId; }

    public boolean isPresent() { return isPresent; }
    public void setPresent(boolean present) { isPresent = present; }
}
