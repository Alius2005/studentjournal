package org.example.studentjournal.POJO;

import java.time.LocalDate;

public class Grade {
    private int id;
    private int studentId;
    private int subjectId;
    private String gradeType;
    private int gradeValue;
    private LocalDate gradeDate;

    public Grade() {}

    public Grade(int id, int studentId, int subjectId, String gradeType, int gradeValue, LocalDate gradeDate) {
        this.id = id;
        this.studentId = studentId;
        this.subjectId = subjectId;
        this.gradeType = gradeType;
        this.gradeValue = gradeValue;
        this.gradeDate = gradeDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getStudentId() { return studentId; }
    public void setStudentId(int studentId) { this.studentId = studentId; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public String getGradeType() { return gradeType; }
    public void setGradeType(String gradeType) { this.gradeType = gradeType; }

    public int getGradeValue() { return gradeValue; }
    public void setGradeValue(int gradeValue) { this.gradeValue = gradeValue; }

    public LocalDate getGradeDate() { return gradeDate; }
    public void setGradeDate(LocalDate gradeDate) { this.gradeDate = gradeDate; }
}
