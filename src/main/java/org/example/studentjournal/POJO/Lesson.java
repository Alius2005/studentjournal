package org.example.studentjournal.POJO;

import java.time.LocalDate;

public class Lesson {
    private int id;
    private int subjectId;
    private int pairNumber;
    private String type;
    private String roomNumber;
    private String buildingNumber;
    private LocalDate lessonDate;

    public Lesson() {}

    public Lesson(int id, int subjectId, int pairNumber, String type, String roomNumber, String buildingNumber, LocalDate lessonDate) {
        this.id = id;
        this.subjectId = subjectId;
        this.pairNumber = pairNumber;
        this.type = type;
        this.roomNumber = roomNumber;
        this.buildingNumber = buildingNumber;
        this.lessonDate = lessonDate;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getSubjectId() { return subjectId; }
    public void setSubjectId(int subjectId) { this.subjectId = subjectId; }

    public int getPairNumber() { return pairNumber; }
    public void setPairNumber(int pairNumber) { this.pairNumber = pairNumber; }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getRoomNumber() { return roomNumber; }
    public void setRoomNumber(String roomNumber) { this.roomNumber = roomNumber; }

    public String getBuildingNumber() { return buildingNumber; }
    public void setBuildingNumber(String buildingNumber) { this.buildingNumber = buildingNumber; }

    public LocalDate getLessonDate() { return lessonDate; }
    public void setLessonDate(LocalDate lessonDate) { this.lessonDate = lessonDate; }
}
