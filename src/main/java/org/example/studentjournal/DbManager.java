package org.example.studentjournal;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import org.example.studentjournal.POJO.*;

public class DbManager {
    private final String url;
    private final String user;
    private final String password;
    private Connection connection;

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
            try { st.executeUpdate("CREATE GENERATOR GEN_USER_ID"); } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }
            try { st.executeUpdate("CREATE GENERATOR GEN_LESSON_ID"); } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }
            try { st.executeUpdate("CREATE GENERATOR GEN_TEACHER_ID"); } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }

            try {
                st.executeUpdate(
                        "CREATE TABLE teachers (" +
                                "id INTEGER NOT NULL PRIMARY KEY, " +
                                "first_name VARCHAR(100) NOT NULL, " +
                                "last_name VARCHAR(100) NOT NULL, " +
                                "middle_name VARCHAR(100), " +
                                "email VARCHAR(255), " +
                                "phone VARCHAR(20), " +
                                "department VARCHAR(255)" +
                                ")"
                );
            } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }

            try {
                st.executeUpdate(
                        "CREATE TABLE subjects (" +
                                "id INTEGER NOT NULL PRIMARY KEY, " +
                                "name VARCHAR(255) NOT NULL, " +
                                "teacher_id INTEGER, " +
                                "schedule VARCHAR(255), " +
                                "FOREIGN KEY (teacher_id) REFERENCES teachers(id)" +
                                ")"
                );
            } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }

            try {
                st.executeUpdate(
                        "CREATE TABLE groups (" +
                                "id INTEGER NOT NULL PRIMARY KEY, " +
                                "name VARCHAR(255) NOT NULL, " +
                                "curriculum VARCHAR(500), " +
                                "teacher_id INTEGER, " +
                                "subjects VARCHAR(500), " +
                                "FOREIGN KEY (teacher_id) REFERENCES teachers(id)" +
                                ")"
                );
            } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }

            try {
                st.executeUpdate(
                        "CREATE TABLE lessons (" +
                                "id INTEGER NOT NULL PRIMARY KEY, " +
                                "subject_id INTEGER NOT NULL, " +
                                "pair_number INTEGER NOT NULL, " +
                                "type VARCHAR(10) NOT NULL CHECK (type IN ('ЛБ', 'ПР', 'ЛК')), " +
                                "room_number VARCHAR(20), " +
                                "building_number VARCHAR(20), " +
                                "lesson_date DATE NOT NULL, " +
                                "FOREIGN KEY (subject_id) REFERENCES subjects(id)" +
                                ")"
                );
            } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }

            try {
                st.executeUpdate(
                        "CREATE TABLE students (" +
                                "id INTEGER NOT NULL PRIMARY KEY, " +
                                "first_name VARCHAR(100) NOT NULL, " +
                                "last_name VARCHAR(100) NOT NULL, " +
                                "middle_name VARCHAR(100), " +
                                "birth_date DATE, " +
                                "group_id INTEGER, " +
                                "contact VARCHAR(255), " +
                                "email VARCHAR(255), " +
                                "FOREIGN KEY (group_id) REFERENCES groups(id)" +
                                ")"
                );
            } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }

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

            try {
                st.executeUpdate(
                        "CREATE TABLE attendance (" +
                                "id INTEGER NOT NULL PRIMARY KEY, " +
                                "student_id INTEGER, " +
                                "lesson_id INTEGER NOT NULL, " +
                                "is_present SMALLINT, " +
                                "FOREIGN KEY (student_id) REFERENCES students(id), " +
                                "FOREIGN KEY (lesson_id) REFERENCES lessons(id)" +
                                ")"
                );
            } catch (SQLException e) {
                if (!e.getMessage().toLowerCase().contains("already exists")) throw e;
            }

            try {
                st.executeUpdate(
                        "CREATE TABLE users (" +
                                "id INTEGER NOT NULL PRIMARY KEY, " +
                                "first_name VARCHAR(100) NOT NULL, " +
                                "last_name VARCHAR(100) NOT NULL, " +
                                "middle_name VARCHAR(100), " +
                                "password_hash VARCHAR(128) NOT NULL, " +
                                "role VARCHAR(20) NOT NULL CHECK (role IN ('student', 'starosta', 'dean'))" +
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

    // Update methods
    public void updateStudent(int id, String firstName, String lastName, String middleName, LocalDate birthDate, int groupId, String contact, String email) throws SQLException {
        String sql = "UPDATE students SET first_name = ?, last_name = ?, middle_name = ?, birth_date = ?, group_id = ?, contact = ?, email = ? WHERE id = ?";
        java.sql.Date sqlDate = java.sql.Date.valueOf(birthDate);
        executeUpdate(sql, firstName, lastName, middleName, sqlDate, groupId, contact, email, id);
    }
    public void updateGroup(int id, String name, String curriculum, int teacherId, String subjects) throws SQLException {
        String sql = "UPDATE groups SET name = ?, curriculum = ?, teacher_id = ?, subjects = ? WHERE id = ?";
        executeUpdate(sql, name, curriculum, teacherId, subjects, id);
    }
    public void updateSubject(int id, String name, int teacherId, String schedule) throws SQLException {
        String sql = "UPDATE subjects SET name = ?, teacher_id = ?, schedule = ? WHERE id = ?";
        executeUpdate(sql, name, teacherId, schedule, id);
    }
    public void updateGrade(int id, int studentId, int subjectId, String gradeType, int gradeValue, LocalDate gradeDate) throws SQLException {
        String sql = "UPDATE grades SET student_id = ?, subject_id = ?, grade_type = ?, grade_value = ?, grade_date = ? WHERE id = ?";
        java.sql.Date sqlDate = java.sql.Date.valueOf(gradeDate);
        executeUpdate(sql, studentId, subjectId, gradeType, gradeValue, sqlDate, id);
    }
    public void updateAttendance(int id, int studentId, int lessonId, boolean isPresent) throws SQLException {
        String sql = "UPDATE attendance SET student_id = ?, lesson_id = ?, is_present = ? WHERE id = ?";
        executeUpdate(sql, studentId, lessonId, isPresent ? 1 : 0, id);
    }
    public void updateLesson(int id, int subjectId, int pairNumber, String type, String roomNumber, String buildingNumber, LocalDate lessonDate) throws SQLException {
        String sql = "UPDATE lessons SET subject_id = ?, pair_number = ?, type = ?, room_number = ?, building_number = ?, lesson_date = ? WHERE id = ?";
        java.sql.Date sqlDate = java.sql.Date.valueOf(lessonDate);
        executeUpdate(sql, subjectId, pairNumber, type, roomNumber, buildingNumber, sqlDate, id);
    }
    public void updateTeacher(int id, String firstName, String lastName, String middleName, String email, String contact, String specialization) throws SQLException {
        String sql = "UPDATE teachers SET first_name = ?, last_name = ?, middle_name = ?, email = ?, contact = ?, specialization = ? WHERE id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, middleName);
            stmt.setString(4, email);
            stmt.setString(5, contact);
            stmt.setString(6, specialization);
            stmt.setInt(7, id);
            stmt.executeUpdate();
        }
    }

    // Insert methods
    public void insertStudent(String firstName, String lastName, String middleName, LocalDate birthDate, int groupId, String contact, String email) throws SQLException {
        String sql = "INSERT INTO students (id, first_name, last_name, middle_name, birth_date, group_id, contact, email) VALUES (NEXT VALUE FOR GEN_STUDENT_ID, ?, ?, ?, ?, ?, ?, ?)";
        java.sql.Date sqlDate = java.sql.Date.valueOf(birthDate);
        executeUpdate(sql, firstName, lastName, middleName, sqlDate, groupId, contact, email);
    }
    public void insertGroup(String name, String curriculum, int teacherId, String subjects) throws SQLException {
        String sql = "INSERT INTO groups (id, name, curriculum, teacher_id, subjects) VALUES (NEXT VALUE FOR GEN_GROUP_ID, ?, ?, ?, ?)";
        executeUpdate(sql, name, curriculum, teacherId, subjects);
    }
    public void insertSubject(String name, int teacherId, String schedule) throws SQLException {
        String sql = "INSERT INTO subjects (id, name, teacher_id, schedule) VALUES (NEXT VALUE FOR GEN_SUBJECT_ID, ?, ?, ?)";
        executeUpdate(sql, name, teacherId, schedule);
    }
    public void insertGrade(int studentId, int subjectId, String type, int value, LocalDate date) throws SQLException {
        String sql = "INSERT INTO grades (id, student_id, subject_id, grade_type, grade_value, grade_date) VALUES (NEXT VALUE FOR GEN_GRADE_ID, ?, ?, ?, ?, ?)";
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);
        executeUpdate(sql, studentId, subjectId, type, value, sqlDate);
    }
    public void insertAttendance(int studentId, int lessonId, boolean isPresent) throws SQLException {
        String sql = "INSERT INTO attendance (id, student_id, lesson_id, is_present) VALUES (NEXT VALUE FOR GEN_ATTENDANCE_ID, ?, ?, ?)";
        executeUpdate(sql, studentId, lessonId, isPresent ? 1 : 0);
    }
    public void insertLesson(int subjectId, int pairNumber, String type, String roomNumber, String buildingNumber, LocalDate lessonDate) throws SQLException {
        String sql = "INSERT INTO lessons (id, subject_id, pair_number, type, room_number, building_number, lesson_date) VALUES (NEXT VALUE FOR GEN_LESSON_ID, ?, ?, ?, ?, ?, ?)";
        java.sql.Date sqlDate = java.sql.Date.valueOf(lessonDate);
        executeUpdate(sql, subjectId, pairNumber, type, roomNumber, buildingNumber, sqlDate);
    }
    public void insertTeacher(String firstName, String lastName, String middleName, String email, String contact, String specialization) throws SQLException {
        String sql = "INSERT INTO teachers (first_name, last_name, middle_name, email, contact, specialization) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, firstName);
            stmt.setString(2, lastName);
            stmt.setString(3, middleName);
            stmt.setString(4, email);
            stmt.setString(5, contact);
            stmt.setString(6, specialization);
            stmt.executeUpdate();
        }
    }

    // Get methods
    public List<Student> getStudents() throws SQLException {
        List<Student> list = new ArrayList<>();
        try (ResultSet rs = executeQuery("SELECT * FROM students")) {
            while (rs.next()) {
                list.add(new Student(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("middle_name"),
                        rs.getDate("birth_date").toLocalDate(),
                        rs.getInt("group_id"),
                        rs.getString("contact"),
                        rs.getString("email")
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
                        rs.getString("teacher_id"),
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
                        rs.getString("teacher_id"),
                        rs.getString("schedule")
                ));
            }
        }
        return list;
    }
    public List<Grade> getGrades() throws SQLException {
        List<Grade> list = new ArrayList<>();
        try (ResultSet rs = executeQuery("SELECT * FROM grades")) {
            while (rs.next()) {
                list.add(new Grade(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("subject_id"),
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
        try (ResultSet rs = executeQuery("SELECT * FROM attendance")) {
            while (rs.next()) {
                list.add(new Attendance(
                        rs.getInt("id"),
                        rs.getInt("student_id"),
                        rs.getInt("lesson_id"),
                        rs.getInt("is_present") == 1
                ));
            }
        }
        return list;
    }
    public List<Lesson> getLessons() throws SQLException {
        List<Lesson> lessons = new ArrayList<>();
        try (ResultSet rs = executeQuery("SELECT * FROM lessons")) {
            while (rs.next()) {
                lessons.add(new Lesson(
                        rs.getInt("id"),
                        rs.getInt("subject_id"),
                        rs.getInt("pair_number"),
                        rs.getString("type"),
                        rs.getString("room_number"),
                        rs.getString("building_number"),
                        rs.getDate("lesson_date").toLocalDate()
                ));
            }
        }
        return lessons;
    }
    public List<Teacher> getTeachers() throws SQLException {
        List<Teacher> teachers = new ArrayList<>();
        try (ResultSet rs = executeQuery("SELECT * FROM teachers")) {
            while (rs.next()) {
                teachers.add(new Teacher(
                        rs.getInt("id"),
                        rs.getString("first_name"),
                        rs.getString("last_name"),
                        rs.getString("middle_name"),
                        rs.getString("email"),
                        rs.getString("contact"),
                        rs.getString("specialization")
                ));
            }
        }
        return teachers;
    }

    // Delete methods
    public void deleteStudent(int id) throws SQLException {
        String sql = "DELETE FROM students WHERE id = ?";
        executeUpdate(sql, id);
    }
    public void deleteGroup(int id) throws SQLException {
        String sql = "DELETE FROM groups WHERE id = ?";
        executeUpdate(sql, id);
    }
    public void deleteSubject(int id) throws SQLException {
        String sql = "DELETE FROM subjects WHERE id = ?";
        executeUpdate(sql, id);
    }
    public void deleteGrade(int id) throws SQLException {
        String sql = "DELETE FROM grades WHERE id = ?";
        executeUpdate(sql, id);
    }
    public void deleteAttendance(int id) throws SQLException {
        String sql = "DELETE FROM attendance WHERE id = ?";
        executeUpdate(sql, id);
    }
    public void deleteLesson(int id) throws SQLException {
        String sql = "DELETE FROM lessons WHERE id = ?";
        executeUpdate(sql, id);
    }
    public void deleteTeacher(int id) throws SQLException {
        String sql = "DELETE FROM teachers WHERE id = ?";
        executeUpdate(sql, id);
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
