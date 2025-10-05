package org.example.studentjournal;

import com.toedter.calendar.JCalendar;
import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ArrayList;
import java.util.Calendar;

// Добавляем кастомную модель для JSpinner, которая всегда меняет день при перелистывании
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

    private void showStudents() {
        try {
            List<String> students = dbManager.getStudents();
            displayArea.setText("");
            for (String s : students) {
                displayArea.append(s + "\n");
            }
        } catch (SQLException ex) {
            showError(ex);
        }
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

    private void showGroups() {
        try {
            List<String> groups = dbManager.getGroups();
            displayArea.setText("");
            for (String s : groups) {
                displayArea.append(s + "\n");
            }
        } catch (SQLException ex) {
            showError(ex);
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

    private void showSubjects() {
        try {
            List<String> subjects = dbManager.getSubjects();
            displayArea.setText("");
            for (String s : subjects) {
                displayArea.append(s + "\n");
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void addSubjectDialog() {
        JTextField nameField = new JTextField();
        JTextField teacherField = new JTextField();

        // Компоненты для выбора дат расписания с использованием календаря
        JCalendar calendar = new JCalendar();
        JButton addDateButton = new JButton("Добавить дату");
        DefaultListModel<String> dateListModel = new DefaultListModel<>();
        JList<String> dateList = new JList<>(dateListModel);
        JScrollPane dateScroll = new JScrollPane(dateList);
        dateScroll.setPreferredSize(new Dimension(200, 100));
        JButton removeDateButton = new JButton("Удалить выбранную дату");

        // Панель для дат
        JPanel datePanelContainer = new JPanel(new BorderLayout());
        JPanel topDatePanel = new JPanel(new FlowLayout());
        topDatePanel.add(calendar); // Добавляем календарь
        topDatePanel.add(addDateButton);
        datePanelContainer.add(topDatePanel, BorderLayout.NORTH);
        datePanelContainer.add(dateScroll, BorderLayout.CENTER);
        datePanelContainer.add(removeDateButton, BorderLayout.SOUTH);

        // События кнопок
        addDateButton.addActionListener(e -> {
            // Получаем выбранную дату из календаря
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

    private void showGrades() {
        try {
            List<String> grades = dbManager.getGrades();
            displayArea.setText("");
            for (String s : grades) {
                displayArea.append(s + "\n");
            }
        } catch (SQLException ex) {
            showError(ex);
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

    private void showAttendance() {
        try {
            List<String> attendance = dbManager.getAttendance();
            displayArea.setText("");
            for (String s : attendance) {
                displayArea.append(s + "\n");
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void addAttendanceGroupDialog() {
        JTextField groupField = new JTextField();
        JSpinner dateSpinner = new JSpinner(new DaySpinnerDateModel());
        JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
        dateSpinner.setEditor(dateEditor);
        JTextField subjectIdField = new JTextField();

        Object[] inputMessage = {
                "Группа:", groupField,
                "Дата:", dateSpinner,
                "ID предмета:", subjectIdField
        };

        int inputOption = JOptionPane.showConfirmDialog(this, inputMessage, "Выбрать параметры", JOptionPane.OK_CANCEL_OPTION);

        if (inputOption != JOptionPane.OK_OPTION) return;

        try {
            String group = groupField.getText().trim();
            Date dateUtil = (Date) dateSpinner.getValue();
            LocalDate date = LocalDate.ofInstant(dateUtil.toInstant(), ZoneId.systemDefault());
            long subjectId = Long.parseLong(subjectIdField.getText().trim());

            List<String> allStudents = dbManager.getStudents();
            List<Student> groupStudents = new ArrayList<>();

            for (String s : allStudents) {
                String[] parts = s.split(" ");
                if (parts.length > 3 && parts[3].equals(group)) {
                    long id = Long.parseLong(parts[0].replace("ID:", ""));
                    String name = parts[1] + " " + parts[2];
                    groupStudents.add(new Student(id, name));
                }
            }

            if (groupStudents.isEmpty()) {
                JOptionPane.showMessageDialog(this, "В группе нет студентов.");
                return;
            }

            String[] columnNames = {"ID", "ФИО", "Присутствие"};
            Object[][] data = new Object[groupStudents.size()][3];

            for (int i = 0; i < groupStudents.size(); i++) {
                Student st = groupStudents.get(i);
                data[i][0] = st.id;
                data[i][1] = st.name;
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

    private static class Student {
        long id;
        String name;

        Student(long id, String name) {
            this.id = id;
            this.name = name;
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}