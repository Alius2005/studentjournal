package org.example.studentjournal;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class DbManager {
    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

    public class Student {
        private int id;
        private String fullName;
        private LocalDate birthDate;
        private String groupName;
        private String contact;

        public Student(int id, String fullName, LocalDate birthDate, String groupName, String contact) {
            this.id = id;
            this.fullName = fullName;
            this.birthDate = birthDate;
            this.groupName = groupName;
            this.contact = contact;
        }

        public int getId() { return id; }
        public String getFullName() { return fullName; }
        public LocalDate getBirthDate() { return birthDate; }
        public String getGroupName() { return groupName; }
        public String getContact() { return contact; }
    }

    public class Group {
        private int id;
        private String name;
        private String curriculum;
        private String teacher;
        private String subjects;

        public Group(int id, String name, String curriculum, String teacher, String subjects) {
            this.id = id;
            this.name = name;
            this.curriculum = curriculum;
            this.teacher = teacher;
            this.subjects = subjects;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getCurriculum() { return curriculum; }
        public String getTeacher() { return teacher; }
        public String getSubjects() { return subjects; }
    }

    public class Subject {
        private int id;
        private String name;
        private String teacher;
        private String schedule;

        public Subject(int id, String name, String teacher, String schedule) {
            this.id = id;
            this.name = name;
            this.teacher = teacher;
            this.schedule = schedule;
        }

        public int getId() { return id; }
        public String getName() { return name; }
        public String getTeacher() { return teacher; }
        public String getSchedule() { return schedule; }
    }

    public class Grade {
        private int id;
        private String studentName;
        private String subjectName;
        private String gradeType;
        private int gradeValue;
        private LocalDate gradeDate;

        public Grade(int id, String studentName, String subjectName, String gradeType, int gradeValue, LocalDate gradeDate) {
            this.id = id;
            this.studentName = studentName;
            this.subjectName = subjectName;
            this.gradeType = gradeType;
            this.gradeValue = gradeValue;
            this.gradeDate = gradeDate;
        }

        public int getId() { return id; }
        public String getStudentName() { return studentName; }
        public String getSubjectName() { return subjectName; }
        public String getGradeType() {return gradeType; }
        public int getGradeValue() { return gradeValue; }
        public LocalDate getGradeDate() { return gradeDate; }
    }

    public class Attendance {
        private int id;
        private String studentName;
        private String subjectName;
        private LocalDate attendanceDate;
        private boolean isPresent;

        public Attendance(int id, String studentName, String subjectName, LocalDate attendanceDate, boolean isPresent) {
            this.id = id;
            this.studentName = studentName;
            this.subjectName = subjectName;
            this.attendanceDate = attendanceDate;
            this.isPresent = isPresent;
        }

        public int getId() { return id; }
        public String getStudentName() { return studentName; }
        public String getSubjectName() { return subjectName; }
        public LocalDate getAttendanceDate() { return attendanceDate; }
        public boolean isPresent() { return isPresent; }
    }

    private final ThreadLocal<Connection> connectionHolder = new ThreadLocal<>();

    public DbManager(String url, String user, String password, boolean createIfNotExists) throws SQLException {
        this.url = url;
        this.user = user;
        this.password = password;

        if (createIfNotExists) {
//isql-fb
//
//CREATE DATABASE '/home/student/databases/studentjournal.fdb' USER 'sysdba' PASSWORD 'masterkey';
//COMMIT;
//EXIT;
        }
        // Создаем соединение
        connection = DriverManager.getConnection(url, user, password);
        initializeTables();
        System.out.println("Подключено успешно!");
    }

    private void initializeTables() throws SQLException {
        try (Connection conn = getConnection(); Statement st = conn.createStatement()) {

            // Создаем генераторы, игнорируя ошибку, если уже существуют
            try { st.executeUpdate("CREATE GENERATOR GEN_STUDENT_ID"); } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }
            try { st.executeUpdate("CREATE GENERATOR GEN_GROUP_ID"); } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }
            try { st.executeUpdate("CREATE GENERATOR GEN_SUBJECT_ID"); } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }
            try { st.executeUpdate("CREATE GENERATOR GEN_GRADE_ID"); } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }
            try { st.executeUpdate("CREATE GENERATOR GEN_ATTENDANCE_ID"); } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }

            // Создаем таблицу students
            try {
                st.executeUpdate(
                        "CREATE TABLE students (" +
                                "id INTEGER NOT NULL PRIMARY KEY, " +
                                "full_name VARCHAR(255) NOT NULL, " +
                                "birth_date DATE, " +
                                "group_name VARCHAR(100), " +
                                "contact VARCHAR(255))"
                );
            } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }

            // Создаем таблицу groups
            try {
                st.executeUpdate(
                        "CREATE TABLE groups (" +
                                "id INTEGER NOT NULL PRIMARY KEY, " +
                                "name VARCHAR(255) NOT NULL, " +
                                "curriculum VARCHAR(500), " +
                                "teacher VARCHAR(255), " +
                                "subjects VARCHAR(500))"
                );
            } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }

            // Создаем таблицу subjects
            try {
                st.executeUpdate(
                        "CREATE TABLE subjects (" +
                                "id INTEGER NOT NULL PRIMARY KEY, " +
                                "name VARCHAR(255) NOT NULL, " +
                                "teacher VARCHAR(255), " +
                                "schedule VARCHAR(255))"
                );
            } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }

            // Создаем таблицу grades
            try {
                st.executeUpdate(
                        "CREATE TABLE grades (" +
                                "id INTEGER NOT NULL PRIMARY KEY, " +
                                "student_id INTEGER, " +
                                "subject_id INTEGER, " +
                                "grade_type VARCHAR(50), " +
                                "grade_value INTEGER, " +
                                "grade_date DATE, " +
                                "FOREIGN KEY (student_id) REFERENCES students(id), " +
                                "FOREIGN KEY (subject_id) REFERENCES subjects(id)" +
                                ")"
                );
            } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }

            // Создаем таблицу attendance
            try {
                st.executeUpdate(
                        "CREATE TABLE attendance (" +
                                "id INTEGER NOT NULL PRIMARY KEY, " +
                                "student_id INTEGER, " +
                                "subject_id INTEGER, " +
                                "attendance_date DATE, " +
                                "is_present SMALLINT, " +
                                "FOREIGN KEY (student_id) REFERENCES students(id), " +
                                "FOREIGN KEY (subject_id) REFERENCES subjects(id)" +
                                ")"
                );
            } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }

            System.out.println("Инициализация базы данных завершена.");
        }
    }

    public Connection getConnection() throws SQLException {
        Connection conn = connectionHolder.get();
        if (conn == null || conn.isClosed()) {
            conn = DriverManager.getConnection(url, user, password);
            connectionHolder.set(conn);
        }
        return conn;
    }

    private boolean tableExists(String tableName) throws SQLException {
        String query = "SELECT 1 FROM RDB$RELATIONS WHERE RDB$RELATION_NAME = ?";
        try (PreparedStatement ps = connection.prepareStatement(query)) {
            ps.setString(1, tableName.toUpperCase());
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next();
            }
        }
    }

    public ResultSet executeQuery(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeQuery();
    }

    public int executeUpdate(String sql, Object... params) throws SQLException {
        PreparedStatement stmt = connection.prepareStatement(sql);
        for (int i = 0; i < params.length; i++) {
            stmt.setObject(i + 1, params[i]);
        }
        return stmt.executeUpdate();
    }

    public void insertStudent(String fullName, LocalDate birthDate, String groupName, String contact) throws SQLException {
        String sql = "INSERT INTO students (id, full_name, birth_date, group_name, contact) VALUES (NEXT VALUE FOR GEN_STUDENT_ID, ?, ?, ?, ?)";
        java.sql.Date sqlDate = java.sql.Date.valueOf(birthDate);
        executeUpdate(sql, fullName, sqlDate, groupName, contact);
    }
    public void insertGroup(String name, String curriculum, String teacher, String subjects) throws SQLException {
        String sql = "INSERT INTO GROUPS (ID, name, curriculum, teacher, subjects) VALUES (NEXT VALUE FOR GEN_GROUP_ID, ?, ?, ?, ?)";
        executeUpdate(sql, name, curriculum, teacher, subjects);
    }
    public void insertSubject(String name, String teacher, String schedule) throws SQLException {
        String sql = "INSERT INTO subjects (id, name, teacher, schedule) VALUES (NEXT VALUE FOR GEN_SUBJECT_ID, ?, ?, ?)";        executeUpdate(sql,name, teacher, schedule);
    }
    public void insertGrade(long studentId, long subjectId, String type, int value, LocalDate date) throws SQLException {
        String sql = "INSERT INTO grades (id, student_id, subject_id, grade_type, grade_value, grade_date) VALUES (NEXT VALUE FOR GEN_GRADE_ID, ?, ?, ?, ?, ?)";
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);
        executeUpdate(sql, studentId, subjectId, type, value, sqlDate);
    }
    public void insertAttendance(long studentId, long subjectId, LocalDate date, boolean isPresent) throws SQLException {
        String sql = "INSERT INTO attendance (id, student_id, subject_id, attendance_date, is_present) VALUES (NEXT VALUE FOR GEN_ATTENDANCE_ID, ?, ?, ?, ?)";
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);
        executeUpdate(sql, studentId, subjectId, sqlDate, isPresent ? 1 : 0);
    }

    public List<Student> getStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        try (ResultSet rs = executeQuery("SELECT * FROM students")) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getDate("birth_date").toLocalDate(),
                        rs.getString("group_name"),
                        rs.getString("contact")
                ));
            }
        }
        return list;
    }

    public List<Group> getGroups() throws SQLException {
        List<Group> list = new ArrayList<>();
        try (ResultSet rs = executeQuery("SELECT * FROM groups")) {
            while (rs.next()) {
                list.add(new Group(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("curriculum"),
                        rs.getString("teacher"),
                        rs.getString("subjects")
                ));
            }
        }
        return list;
    }

    public List<Subject> getSubjects() throws SQLException {
        List<Subject> list = new ArrayList<>();
        try (ResultSet rs = executeQuery("SELECT * FROM subjects")) {
            while (rs.next()) {
                list.add(new Subject(
                        rs.getInt("id"),
                        rs.getString("name"),
                        rs.getString("teacher"),
                        rs.getString("schedule")
                ));
            }
        }
        return list;
    }

    public List<Grade> getGrades() throws SQLException {
        List<Grade> list = new ArrayList<>();
        try (ResultSet rs = executeQuery(
            "SELECT g.id, s.full_name, sub.name, g.grade_type, g.grade_value, g.grade_date FROM grades g " +
                "JOIN students s ON g.student_id = s.id " +
                "JOIN subjects sub ON g.subject_id = sub.id")) {
            while (rs.next()) {
                list.add(new Grade(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("name"),
                        rs.getString("grade_type"),
                        rs.getInt("grade_value"),
                        rs.getDate("grade_date").toLocalDate()
                ));
            }
        }
        return list;
    }

    public List<Attendance> getAttendance() throws SQLException {
        List<Attendance> list = new ArrayList<>();
        String sql = "SELECT a.id, s.full_name, sub.name, a.attendance_date, a.is_present " +
                "FROM attendance a " +
                "JOIN students s ON a.student_id = s.id " +
                "JOIN subjects sub ON a.subject_id = sub.id";

        try (ResultSet rs = executeQuery(sql)) {
            while (rs.next()) {
                list.add(new Attendance(
                        rs.getInt("id"),
                        rs.getString("full_name"),
                        rs.getString("name"),
                        rs.getDate("attendance_date").toLocalDate(),
                        rs.getInt("is_present") == 1
                ));
            }
        }
        return list;
    }

    public void close() {
        if (connection != null) {
            try {
                connection.close();
                System.out.println("Соединение закрыто.");
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

}