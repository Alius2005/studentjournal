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

    public List<String> getStudents() throws SQLException {
        List<String> list = new ArrayList<>();
        try (ResultSet rs = executeQuery("SELECT * FROM students")) {
            while (rs.next()) {
                list.add(rs.getInt("id") + " - " + rs.getString("full_name") + " - группа: " + rs.getString("group_name"));
            }
        }
        return list;
    }

    public void insertGroup(String name, String curriculum, String teacher, String subjects) throws SQLException {
        String sql = "INSERT INTO GROUPS (ID, name, curriculum, teacher, subjects) VALUES (NEXT VALUE FOR GEN_GROUP_ID, ?, ?, ?, ?)";
        executeUpdate(sql, name, curriculum, teacher, subjects);
    }

    public List<String> getGroups() throws SQLException {
        List<String> list = new ArrayList<>();
        try (ResultSet rs = executeQuery("SELECT * FROM groups")) {
            while (rs.next()) {
                list.add(rs.getInt("id") + " - " + rs.getString("name") + " - преподаватель: " + rs.getString("teacher"));
            }
        }
        return list;
    }

    public void insertSubject(String name, String teacher, String schedule) throws SQLException {
        String sql = "INSERT INTO subjects (id, name, teacher, schedule) VALUES (NEXT VALUE FOR GEN_SUBJECT_ID, ?, ?, ?)";        executeUpdate(sql,name, teacher, schedule);
    }

    public List<String> getSubjects() throws SQLException {
        List<String> list = new ArrayList<>();
        try (ResultSet rs = executeQuery("SELECT * FROM subjects")) {
            while (rs.next()) {
                list.add(rs.getInt("id") + " - " + rs.getString("name") + " - преподаватель: " + rs.getString("teacher"));
            }
        }
        return list;
    }

    public void insertGrade(long studentId, long subjectId, String type, int value, LocalDate date) throws SQLException {
        String sql = "INSERT INTO grades (id, student_id, subject_id, grade_type, grade_value, grade_date) VALUES (NEXT VALUE FOR GEN_GRADE_ID, ?, ?, ?, ?, ?)";
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);
        executeUpdate(sql, studentId, subjectId, type, value, sqlDate);
    }

    public List<String> getGrades() throws SQLException {
        List<String> list = new ArrayList<>();
        try (ResultSet rs = executeQuery("SELECT g.id, s.full_name, sub.name, g.grade_value, g.grade_date FROM grades g JOIN students s ON g.student_id = s.id JOIN subjects sub ON g.subject_id = sub.id")) {
            while (rs.next()) {
                list.add(rs.getInt("id") + " - " + rs.getString("full_name") + " (" + rs.getString("name") + ") - оценка: " + rs.getInt("grade_value") + " от " + rs.getDate("grade_date"));
            }
        }
        return list;
    }

    public void insertAttendance(long studentId, long subjectId, LocalDate date, boolean isPresent) throws SQLException {
        String sql = "INSERT INTO attendance (id, student_id, subject_id, attendance_date, is_present) VALUES (NEXT VALUE FOR GEN_ATTENDANCE_ID, ?, ?, ?, ?)";
        java.sql.Date sqlDate = java.sql.Date.valueOf(date);
        executeUpdate(sql, studentId, subjectId, sqlDate, isPresent ? 1 : 0);
    }

    public List<String> getAttendance() throws SQLException {
        List<String> list = new ArrayList<>();
        String sql = "SELECT a.id, s.full_name, sub.name, a.attendance_date, a.is_present " +
                "FROM attendance a " +
                "JOIN students s ON a.student_id = s.id " +
                "JOIN subjects sub ON a.subject_id = sub.id";

        try (ResultSet rs = executeQuery(sql)) {
            while (rs.next()) {
                int id = rs.getInt("id");
                String fullName = rs.getString("full_name");
                String subjectName = rs.getString("name");
                boolean isPresent = rs.getInt("is_present") == 1;

                list.add(id + " - " + fullName + " (" + subjectName + ") - присутствие: " + (isPresent ? "да" : "нет"));
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