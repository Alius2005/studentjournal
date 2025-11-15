package org.example.studentjournal.POJO;


public class Subject {
    private int id;
    private String name;
    private int teacher;
    private String schedule;

    public Subject() {}

    public Subject(int id, String name, int teacher, String schedule) {
        this.id = id;
        this.name = name;
        this.teacher = teacher;
        this.schedule = schedule;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public int getTeacher() { return teacher; }
    public void setTeacher(int teacher) { this.teacher = teacher; }

    public String getSchedule() { return schedule; }
    public void setSchedule(String schedule) { this.schedule = schedule; }
}
