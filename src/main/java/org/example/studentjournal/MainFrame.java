package org.example.studentjournal;

import com.toedter.calendar.JCalendar;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.Calendar;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import java.util.stream.Collectors;  // Для Collectors
import com.toedter.calendar.JDateChooser;  // Для JDateChooser

class DaySpinnerDateModel extends SpinnerDateModel {
    public DaySpinnerDateModel() {
        super();
    }

    @Override
    public Object getNextValue() {
        Calendar cal = Calendar.getInstance();
        cal.setTime((Date) getValue());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

    @Override
    public Object getPreviousValue() {
        Calendar cal = Calendar.getInstance();
        cal.setTime((Date) getValue());
        cal.add(Calendar.DAY_OF_MONTH, -1);
        return cal.getTime();
    }
}

public class MainFrame extends JFrame {
    private DbManager dbManager;
    private JTextArea displayArea;

    public MainFrame(DbManager dbManager) {
        this.dbManager = dbManager;
        initUI();
    }

    private void initUI() {
        setTitle("Журнал студентов");
        setSize(700, 500);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new BorderLayout());
        displayArea = new JTextArea();
        displayArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(displayArea);

        JPanel buttonPanel = new JPanel(new GridLayout(2, 5, 10, 10));

        JButton btnAddStudent = new JButton("Добавить студента");
        JButton btnShowStudents = new JButton("Показать студентов");
        JButton btnAddGroup = new JButton("Добавить группу");
        JButton btnShowGroups = new JButton("Показать группы");
        JButton btnAddSubject = new JButton("Добавить предмет");
        JButton btnShowSubjects = new JButton("Показать предметы");
        JButton btnAddGrade = new JButton("Добавить оценку");
        JButton btnShowGrades = new JButton("Показать оценки");
        JButton btnAddAttendance = new JButton("Добавить/редактировать посещаемость");
        JButton btnShowAttendance = new JButton("Показать посещаемость");

        buttonPanel.add(btnAddStudent);
        buttonPanel.add(btnShowStudents);
        buttonPanel.add(btnAddGroup);
        buttonPanel.add(btnShowGroups);
        buttonPanel.add(btnAddSubject);
        buttonPanel.add(btnShowSubjects);
        buttonPanel.add(btnAddGrade);
        buttonPanel.add(btnShowGrades);
        buttonPanel.add(btnAddAttendance);
        buttonPanel.add(btnShowAttendance);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(buttonPanel, BorderLayout.SOUTH);

        add(panel);

        btnShowStudents.addActionListener(e -> showStudents());
        btnAddStudent.addActionListener(e -> addStudentDialog());

        btnShowGroups.addActionListener(e -> showGroups());
        btnAddGroup.addActionListener(e -> addGroupDialog());

        btnShowSubjects.addActionListener(e -> showSubjects());
        btnAddSubject.addActionListener(e -> addSubjectDialog());

        btnShowGrades.addActionListener(e -> showGrades());
        btnAddGrade.addActionListener(e -> addGradeDialog());

        btnShowAttendance.addActionListener(e -> showAttendance());
        btnAddAttendance.addActionListener(e -> addAttendanceGroupDialog());
    }

    private void addStudentDialog() {
        JTextField fullNameField = new JTextField();
        JSpinner birthDateSpinner = new JSpinner(new DaySpinnerDateModel());
        JSpinner.DateEditor birthDateEditor = new JSpinner.DateEditor(birthDateSpinner, "yyyy-MM-dd");
        birthDateSpinner.setEditor(birthDateEditor);
        JTextField groupField = new JTextField();
        JTextField contactField = new JTextField();

        Object[] message = {
                "ФИО:", fullNameField,
                "Дата рождения:", birthDateSpinner,
                "Группа:", groupField,
                "Контакт:", contactField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Добавить студента", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String fullName = fullNameField.getText().trim();
                Date birthDateUtil = (Date) birthDateSpinner.getValue();
                LocalDate birthDate = LocalDate.ofInstant(birthDateUtil.toInstant(), ZoneId.systemDefault());
                String group = groupField.getText().trim();
                String contact = contactField.getText().trim();

                dbManager.insertStudent(fullName, birthDate, group, contact);
                JOptionPane.showMessageDialog(this, "Студент добавлен.");
                showStudents();
            } catch (Exception ex) {
                showError(ex);
            }
        }
    }

    private void addGroupDialog() {
        JTextField nameField = new JTextField();
        JTextField curriculumField = new JTextField();
        JTextField teacherField = new JTextField();
        JTextField subjectsField = new JTextField();

        Object[] message = {
                "Название группы:", nameField,
                "Учебный план:", curriculumField,
                "Преподаватель:", teacherField,
                "Предметы:", subjectsField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Добавить группу", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String curriculum = curriculumField.getText().trim();
                String teacher = teacherField.getText().trim();
                String subjects = subjectsField.getText().trim();

                dbManager.insertGroup(name, curriculum, teacher, subjects);
                JOptionPane.showMessageDialog(this, "Группа добавлена.");
                showGroups();
            } catch (Exception ex) {
                showError(ex);
            }
        }
    }

    private void addSubjectDialog() {
        JTextField nameField = new JTextField();
        JTextField teacherField = new JTextField();

        JCalendar calendar = new JCalendar();
        JButton addDateButton = new JButton("Добавить дату");
        DefaultListModel<String> dateListModel = new DefaultListModel<>();
        JList<String> dateList = new JList<>(dateListModel);
        JScrollPane dateScroll = new JScrollPane(dateList);
        dateScroll.setPreferredSize(new Dimension(200, 100));
        JButton removeDateButton = new JButton("Удалить выбранную дату");

        JPanel datePanelContainer = new JPanel(new BorderLayout());
        JPanel topDatePanel = new JPanel(new FlowLayout());
        topDatePanel.add(calendar);
        topDatePanel.add(addDateButton);
        datePanelContainer.add(topDatePanel, BorderLayout.NORTH);
        datePanelContainer.add(dateScroll, BorderLayout.CENTER);
        datePanelContainer.add(removeDateButton, BorderLayout.SOUTH);

        addDateButton.addActionListener(e -> {
            Date selectedDateUtil = calendar.getDate();
            if (selectedDateUtil != null) {
                LocalDate selectedDate = selectedDateUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
                String dateStr = selectedDate.toString();
                if (!dateListModel.contains(dateStr)) {
                    dateListModel.addElement(dateStr);
                } else {
                    JOptionPane.showMessageDialog(this, "Эта дата уже добавлена.");
                }
            } else {
                JOptionPane.showMessageDialog(this, "Выберите дату в календаре.");
            }
        });

        removeDateButton.addActionListener(e -> {
            int selectedIndex = dateList.getSelectedIndex();
            if (selectedIndex != -1) {
                dateListModel.remove(selectedIndex);
            } else {
                JOptionPane.showMessageDialog(this, "Выберите дату для удаления.");
            }
        });

        Object[] message = {
                "Название предмета:", nameField,
                "Преподаватель:", teacherField,
                "Расписание (выберите даты):", datePanelContainer
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Добавить предмет", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String teacher = teacherField.getText().trim();
                for (int i = 0; i < dateListModel.size(); i++) {
                    dbManager.insertSubject(name, teacher, dateListModel.get(i));
                }

                JOptionPane.showMessageDialog(this, "Предмет добавлен.");
                showSubjects();
            } catch (Exception ex) {
                showError(ex);
            }
        }
    }

    private void addGradeDialog() {
        JTextField studentIdField = new JTextField();
        JTextField subjectIdField = new JTextField();
        JTextField gradeTypeField = new JTextField();
        JTextField gradeValueField = new JTextField();
        JSpinner dateSpinner = new JSpinner(new DaySpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);

        Object[] message = {
                "ID студента:", studentIdField,
                "ID предмета:", subjectIdField,
                "Тип оценки:", gradeTypeField,
                "Значение оценки:", gradeValueField,
                "Дата:", dateSpinner
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Добавить оценку", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                long studentId = Long.parseLong(studentIdField.getText().trim());
                long subjectId = Long.parseLong(subjectIdField.getText().trim());
                String type = gradeTypeField.getText().trim();
                int value = Integer.parseInt(gradeValueField.getText().trim());
                Date dateUtil = (Date) dateSpinner.getValue();
                LocalDate date = LocalDate.ofInstant(dateUtil.toInstant(), ZoneId.systemDefault());

                dbManager.insertGrade(studentId, subjectId, type, value, date);
                JOptionPane.showMessageDialog(this, "Оценка добавлена.");
                showGrades();
            } catch (Exception ex) {
                showError(ex);
            }
        }
    }

    private void addAttendanceGroupDialog() {
        JTextField groupField = new JTextField();
        JDateChooser dateChooser = new JDateChooser();
        dateChooser.setDateFormatString("yyyy-MM-dd");
        JTextField subjectIdField = new JTextField();

        Object[] inputMessage = {
                "Группа:", groupField,
                "Дата:", dateChooser,
                "ID предмета:", subjectIdField
        };

        int inputOption = JOptionPane.showConfirmDialog(this, inputMessage, "Выбрать параметры", JOptionPane.OK_CANCEL_OPTION);

        if (inputOption != JOptionPane.OK_OPTION) return;

        try {
            String group = groupField.getText().trim();
            Date dateUtil = dateChooser.getDate();
            if (dateUtil == null) {
                JOptionPane.showMessageDialog(this, "Выберите дату.");
                return;
            }
            LocalDate date = dateUtil.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            long subjectId = Long.parseLong(subjectIdField.getText().trim());

            List<DbManager.Student> allStudents = dbManager.getStudents();
            List<DbManager.Student> groupStudents = allStudents.stream()
                    .filter(s -> s.getGroupName().equals(group))
                    .collect(Collectors.toList());

            if (groupStudents.isEmpty()) {
                JOptionPane.showMessageDialog(this, "В группе нет студентов.");
                return;
            }

            String[] columnNames = {"ID", "ФИО", "Присутствие"};
            Object[][] data = new Object[groupStudents.size()][3];

            for (int i = 0; i < groupStudents.size(); i++) {
                DbManager.Student st = groupStudents.get(i);
                data[i][0] = st.getId();
                data[i][1] = st.getFullName();
                data[i][2] = false;
            }

            JTable table = new JTable(data, columnNames);
            table.getColumn("Присутствие").setCellEditor(new DefaultCellEditor(new JCheckBox()));
            JScrollPane tableScroll = new JScrollPane(table);

            int tableOption = JOptionPane.showConfirmDialog(this, tableScroll, "Редактировать посещаемость группы", JOptionPane.OK_CANCEL_OPTION);

            if (tableOption == JOptionPane.OK_OPTION) {
                for (int i = 0; i < data.length; i++) {
                    long studentId = (Long) data[i][0];
                    boolean isPresent = (Boolean) data[i][2];
                    dbManager.insertAttendance(studentId, subjectId, date, isPresent);
                }
                JOptionPane.showMessageDialog(this, "Посещаемость сохранена.");
                showAttendance();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showStudents() {
        try {
            List<DbManager.Student> students = dbManager.getStudents();
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет данных для отображения.");
                return;
            }

            String[] columnNames = {"ID", "ФИО", "Группа"};
            Object[][] data = new Object[students.size()][3];

            for (int i = 0; i < students.size(); i++) {
                DbManager.Student s = students.get(i);
                data[i][0] = s.getId();
                data[i][1] = s.getFullName();
                data[i][2] = s.getGroupName();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(this, scrollPane, "Студенты", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showGroups() {
        try {
            List<DbManager.Group> groups = dbManager.getGroups();
            if (groups.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет данных для отображения.");
                return;
            }

            String[] columnNames = {"ID", "Название"};
            Object[][] data = new Object[groups.size()][2];

            for (int i = 0; i < groups.size(); i++) {
                DbManager.Group g = groups.get(i);
                data[i][0] = g.getId();
                data[i][1] = g.getName();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(this, scrollPane, "Группы", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showSubjects() {
        try {
            List<DbManager.Subject> subjects = dbManager.getSubjects();
            if (subjects.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет данных для отображения.");
                return;
            }

            String[] columnNames = {"ID", "Название"};
            Object[][] data = new Object[subjects.size()][2];

            for (int i = 0; i < subjects.size(); i++) {
                DbManager.Subject s = subjects.get(i);
                data[i][0] = s.getId();
                data[i][1] = s.getName();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(this, scrollPane, "Предметы", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showGrades() {
        try {
            List<DbManager.Grade> grades = dbManager.getGrades();
            if (grades.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет данных для отображения.");
                return;
            }

            String[] columnNames = {"ID", "Студент", "Предмет", "Тип", "Значение", "Дата"};
            Object[][] data = new Object[grades.size()][6];

            for (int i = 0; i < grades.size(); i++) {
                DbManager.Grade g = grades.get(i);
                data[i][0] = g.getId();
                data[i][1] = g.getStudentName();
                data[i][2] = g.getSubjectName();
                data[i][3] = g.getGradeType();
                data[i][4] = g.getGradeValue();
                data[i][5] = g.getGradeDate();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(this, scrollPane, "Оценки", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showAttendance() {
        try {
            List<DbManager.Attendance> attendance = dbManager.getAttendance();
            if (attendance.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет данных для отображения.");
                return;
            }

            String[] columnNames = {"ID", "Студент", "Предмет", "Дата", "Присутствие"};
            Object[][] data = new Object[attendance.size()][5];

            for (int i = 0; i < attendance.size(); i++) {
                DbManager.Attendance a = attendance.get(i);
                data[i][0] = a.getId();
                data[i][1] = a.getStudentName();
                data[i][2] = a.getSubjectName();
                data[i][3] = a.getAttendanceDate();
                data[i][4] = a.isPresent() ? "Да" : "Нет";
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(this, scrollPane, "Посещаемость", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}