package org.example.studentjournal.POJO;


public class Subject {
    private int id;
    private String name;
    private String teacher;
    private String schedule;

    public Subject() {}

    public Subject(int id, String name, String teacher, String schedule) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.schedule = schedule;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { this.teacher = teacher; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
}
