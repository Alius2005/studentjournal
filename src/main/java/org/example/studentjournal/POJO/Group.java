package org.example.studentjournal.POJO;

public class Group {
    private int id;
    private String name;
    private String curriculum;
    private String teacher;
    private String subjects;

    public Group() {}

    public Group(int id, String name, String curriculum, String teacher, String subjects) {
        this.id = id;
        this.name = name;
        this.curriculum = curriculum;
        this.teacher = teacher;
        this.subjects = subjects;
    }

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getCurriculum() { return curriculum; }
    public void setCurriculum(String curriculum) { this.curriculum = curriculum; }

    public String getTeacher() { return teacher; }
    public void setTeacher(String teacher) { this.teacher = teacher; }

    public String getSubjects() { return subjects; }
    public void setSubjects(String subjects) { this.subjects = subjects; }
}
