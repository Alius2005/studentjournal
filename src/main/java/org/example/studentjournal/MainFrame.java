package org.example.studentjournal;

import javax.swing.*;
import java.awt.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import javax.swing.JTable;
import javax.swing.JScrollPane;
import javax.swing.JOptionPane;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import org.example.studentjournal.POJO.*;

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

        JPanel comboBoxPanel = new JPanel(new GridLayout(2, 2, 5, 5));

        // 1) Добавление
        JPanel addPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] addOptions = {"студента", "группу", "предмет", "оценку", "посещаемость", "урок"};
        JComboBox<String> addCombo = new JComboBox<>(addOptions);
        JButton addBtn = new JButton("Добавить");
        addPanel.add(new JLabel("Добавить:"));
        addPanel.add(addCombo);
        addPanel.add(addBtn);

        // 2) Изменение
        JPanel editPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] editOptions = {"студента", "группу", "предмет", "оценку", "посещаемость", "урок"};
        JComboBox<String> editCombo = new JComboBox<>(editOptions);
        JButton editBtn = new JButton("Изменить");
        editPanel.add(new JLabel("Изменить:"));
        editPanel.add(editCombo);
        editPanel.add(editBtn);

        // 3) Удаление
        JPanel deletePanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] deleteOptions = {"студента", "группу", "предмет", "оценку", "посещаемость", "урок"};
        JComboBox<String> deleteCombo = new JComboBox<>(deleteOptions);
        JButton deleteBtn = new JButton("Удалить");
        deletePanel.add(new JLabel("Удалить:"));
        deletePanel.add(deleteCombo);
        deletePanel.add(deleteBtn);

        // 4) Просмотр
        JPanel showPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        String[] showOptions = {"студентов", "групп", "предметов", "оценок", "посещаемости", "уроков"};
        JComboBox<String> showCombo = new JComboBox<>(showOptions);
        JButton showBtn = new JButton("Показать");
        showPanel.add(new JLabel("Показать:"));
        showPanel.add(showCombo);
        showPanel.add(showBtn);

        comboBoxPanel.add(addPanel);
        comboBoxPanel.add(editPanel);
        comboBoxPanel.add(deletePanel);
        comboBoxPanel.add(showPanel);

        panel.add(scrollPane, BorderLayout.CENTER);
        panel.add(comboBoxPanel, BorderLayout.SOUTH);

        add(panel);

        addBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = (String) addCombo.getSelectedItem();
                switch (selected) {
                    case "студента":
                        addStudentDialog();
                        break;
                    case "группу":
                        addGroupDialog();
                        break;
                    case "предмет":
                        addSubjectDialog();
                        break;
                    case "оценку":
                        addGradeDialog();
                        break;
                    case "посещаемость":
                        addAttendanceDialog();
                        break;
                    case "урок":
                        addLessonDialog();
                        break;
                    default:
                        JOptionPane.showMessageDialog(MainFrame.this, "Неизвестная опция добавления.");
                }
            }
        });
        editBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = (String) editCombo.getSelectedItem();
                switch (selected) {
                    case "студента":
                        editStudentDialog();
                        break;
                    case "группу":
                        editGroupDialog();
                        break;
                    case "предмет":
                        editSubjectDialog();
                        break;
                    case "оценку":
                        editGradeDialog();
                        break;
                    case "посещаемость":
                        editAttendanceDialog();
                        break;
                    case "урок":
                        editLessonDialog();
                        break;
                    default:
                        JOptionPane.showMessageDialog(MainFrame.this, "Неизвестная опция изменения.");
                }
            }
        });
        deleteBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = (String) deleteCombo.getSelectedItem();
                switch (selected) {
                    case "студента":
                        deleteStudentDialog();
                        break;
                    case "группу":
                        deleteGroupDialog();
                        break;
                    case "предмет":
                        deleteSubjectDialog();
                        break;
                    case "оценку":
                        deleteGradeDialog();
                        break;
                    case "посещаемость":
                        deleteAttendanceDialog();
                        break;
                    case "урок":
                        deleteLessonDialog();
                        break;
                    default:
                        JOptionPane.showMessageDialog(MainFrame.this, "Неизвестная опция удаления.");
                }
            }
        });
        showBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                String selected = (String) showCombo.getSelectedItem();
                switch (selected) {
                    case "студентов":
                        showStudents();
                        break;
                    case "групп":
                        showGroups();
                        break;
                    case "предметов":
                        showSubjects();
                        break;
                    case "оценок":
                        showGrades();
                        break;
                    case "посещаемости":
                        showAttendance();
                        break;
                    case "уроков":
                        showLesson();
                        break;
                    default:
                        JOptionPane.showMessageDialog(MainFrame.this, "Неизвестная опция просмотра.");
                }
            }
        });
    }

    private void deleteStudentDialog() {
        try {
            List<Student> students = dbManager.getStudents();
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет студентов для удаления.");
                return;
            }

            String[] columnNames = {"ID", "ФИО", "Дата рождения", "Группа", "Контакт", "Email"};
            Object[][] data = new Object[students.size()][6];
            for (int i = 0; i < students.size(); i++) {
                Student s = students.get(i);
                data[i][0] = s.getId();
                data[i][1] = s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastName();
                data[i][2] = s.getBirthDate();
                data[i][3] = s.getGroupId();
                data[i][4] = s.getContact();
                data[i][5] = s.getEmail();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            int option = JOptionPane.showConfirmDialog(this, scrollPane, "Выберите студента для удаления", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите строку.");
                return;
            }

            Student selected = students.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, "Удалить студента " + selected.getFirstName() + " " + selected.getLastName() + "?", "Подтверждение", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dbManager.deleteStudent(selected.getId());
                JOptionPane.showMessageDialog(this, "Студент удален.");
                showStudents();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void deleteGroupDialog() {
        try {
            List<Group> groups = dbManager.getGroups();
            if (groups.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет групп для удаления.");
                return;
            }

            String[] columnNames = {"ID", "Название", "Учебный план", "Преподаватель ID", "Предметы"};
            Object[][] data = new Object[groups.size()][5];
            for (int i = 0; i < groups.size(); i++) {
                Group g = groups.get(i);
                data[i][0] = g.getId();
                data[i][1] = g.getName();
                data[i][2] = g.getCurriculum();
                data[i][3] = g.getTeacher();
                data[i][4] = g.getSubjects();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            int option = JOptionPane.showConfirmDialog(this, scrollPane, "Выберите группу для удаления", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите строку.");
                return;
            }

            Group selected = groups.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, "Удалить группу " + selected.getName() + "?", "Подтверждение", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dbManager.deleteGroup(selected.getId());
                JOptionPane.showMessageDialog(this, "Группа удалена.");
                showGroups();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void deleteSubjectDialog() {
        try {
            List<Subject> subjects = dbManager.getSubjects();
            if (subjects.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет предметов для удаления.");
                return;
            }

            String[] columnNames = {"ID", "Название", "Преподаватель ID", "Расписание"};
            Object[][] data = new Object[subjects.size()][4];
            for (int i = 0; i < subjects.size(); i++) {
                Subject s = subjects.get(i);
                data[i][0] = s.getId();
                data[i][1] = s.getName();
                data[i][2] = s.getTeacher();
                data[i][3] = s.getSchedule();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            int option = JOptionPane.showConfirmDialog(this, scrollPane, "Выберите предмет для удаления", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите строку.");
                return;
            }

            Subject selected = subjects.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, "Удалить предмет " + selected.getName() + "?", "Подтверждение", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dbManager.deleteSubject(selected.getId());
                JOptionPane.showMessageDialog(this, "Предмет удален.");
                showSubjects();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void deleteGradeDialog() {
        try {
            List<Grade> grades = dbManager.getGrades();
            if (grades.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет оценок для удаления.");
                return;
            }

            String[] columnNames = {"ID", "Студент ID", "Предмет ID", "Тип", "Значение", "Дата"};
            Object[][] data = new Object[grades.size()][6];
            for (int i = 0; i < grades.size(); i++) {
                Grade g = grades.get(i);
                data[i][0] = g.getId();
                data[i][1] = g.getStudentId();
                data[i][2] = g.getSubjectId();
                data[i][3] = g.getGradeType();
                data[i][4] = g.getGradeValue();
                data[i][5] = g.getGradeDate();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            int option = JOptionPane.showConfirmDialog(this, scrollPane, "Выберите оценку для удаления", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите строку.");
                return;
            }

            Grade selected = grades.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, "Удалить оценку?", "Подтверждение", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dbManager.deleteGrade(selected.getId());
                JOptionPane.showMessageDialog(this, "Оценка удалена.");
                showGrades();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void deleteAttendanceDialog() {
        try {
            List<Attendance> attendances = dbManager.getAttendance();
            if (attendances.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет записей посещаемости для удаления.");
                return;
            }

            String[] columnNames = {"ID", "Студент ID", "Урок ID", "Присутствие"};
            Object[][] data = new Object[attendances.size()][4];
            for (int i = 0; i < attendances.size(); i++) {
                Attendance a = attendances.get(i);
                data[i][0] = a.getId();
                data[i][1] = a.getStudentId();
                data[i][2] = a.getLessonId();
                data[i][3] = a.isPresent() ? "Да" : "Нет";
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            int option = JOptionPane.showConfirmDialog(this, scrollPane, "Выберите запись посещаемости для удаления", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите строку.");
                return;
            }

            Attendance selected = attendances.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, "Удалить запись посещаемости?", "Подтверждение", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dbManager.deleteAttendance(selected.getId());
                JOptionPane.showMessageDialog(this, "Запись посещаемости удалена.");
                showAttendance();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void deleteLessonDialog() {
        try {
            List<Lesson> lessons = dbManager.getLessons();
            if (lessons.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет уроков для удаления.");
                return;
            }

            String[] columnNames = {"ID", "Предмет ID", "Дата", "Тема"};
            Object[][] data = new Object[lessons.size()][4];
            for (int i = 0; i < lessons.size(); i++) {
                Lesson l = lessons.get(i);
                data[i][0] = l.getId();
                data[i][1] = l.getSubjectId();
                data[i][2] = l.getLessonDate();
                data[i][3] = l.getType();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            int option = JOptionPane.showConfirmDialog(this, scrollPane, "Выберите урок для удаления", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите строку.");
                return;
            }

            Lesson selected = lessons.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, "Удалить урок " + selected.getType() + "?", "Подтверждение", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                dbManager.deleteLesson(selected.getId());
                JOptionPane.showMessageDialog(this, "Урок удален.");
                showLesson();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void editStudentDialog() {
        try {
            List<Student> students = dbManager.getStudents();
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет студентов для редактирования.");
                return;
            }

            String[] columnNames = {"ID", "ФИО", "Дата рождения", "Группа ID", "Контакт", "Email"};
            Object[][] data = new Object[students.size()][6];
            for (int i = 0; i < students.size(); i++) {
                Student s = students.get(i);
                data[i][0] = s.getId();
                data[i][1] = s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastName();
                data[i][2] = s.getBirthDate();
                data[i][3] = s.getGroupId();
                data[i][4] = s.getContact();
                data[i][5] = s.getEmail();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            int option = JOptionPane.showConfirmDialog(this, scrollPane, "Выберите студента для редактирования", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите строку.");
                return;
            }

            Student selected = students.get(selectedRow);

            JTextField firstNameField = new JTextField(selected.getFirstName());
            JTextField lastNameField = new JTextField(selected.getLastName());
            JTextField middleNameField = new JTextField(selected.getMiddleName());
            JSpinner birthDateSpinner = new JSpinner(new javax.swing.SpinnerDateModel());
            birthDateSpinner.setValue(Date.from(selected.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            JSpinner.DateEditor birthDateEditor = new JSpinner.DateEditor(birthDateSpinner, "yyyy-MM-dd");
            birthDateSpinner.setEditor(birthDateEditor);
            JTextField groupIdField = new JTextField(String.valueOf(selected.getGroupId()));
            JTextField contactField = new JTextField(selected.getContact());
            JTextField emailField = new JTextField(selected.getEmail());

            Object[] message = {
                    "Имя:", firstNameField,
                    "Фамилия:", lastNameField,
                    "Отчество:", middleNameField,
                    "Дата рождения:", birthDateSpinner,
                    "ID группы:", groupIdField,
                    "Контакт:", contactField,
                    "Email:", emailField
            };

            int editOption = JOptionPane.showConfirmDialog(this, message, "Редактировать студента", JOptionPane.OK_CANCEL_OPTION);
            if (editOption == JOptionPane.OK_OPTION) {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String middleName = middleNameField.getText().trim();
                Date birthDateUtil = (Date) birthDateSpinner.getValue();
                LocalDate birthDate = LocalDate.ofInstant(birthDateUtil.toInstant(), ZoneId.systemDefault());
                int groupId = Integer.parseInt(groupIdField.getText().trim());
                String contact = contactField.getText().trim();
                String email = emailField.getText().trim();

                dbManager.updateStudent(selected.getId(), firstName, lastName, middleName, birthDate, groupId, contact, email);
                JOptionPane.showMessageDialog(this, "Студент обновлен.");
                showStudents();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void editGroupDialog() {
        try {
            List<Group> groups = dbManager.getGroups();
            if (groups.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет групп для редактирования.");
                return;
            }

            String[] columnNames = {"ID", "Название", "Учебный план", "Преподаватель ID", "Предметы"};
            Object[][] data = new Object[groups.size()][5];
            for (int i = 0; i < groups.size(); i++) {
                Group g = groups.get(i);
                data[i][0] = g.getId();
                data[i][1] = g.getName();
                data[i][2] = g.getCurriculum();
                data[i][3] = g.getTeacher();
                data[i][4] = g.getSubjects();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            int option = JOptionPane.showConfirmDialog(this, scrollPane, "Выберите группу для редактирования", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите строку.");
                return;
            }

            Group selected = groups.get(selectedRow);

            JTextField nameField = new JTextField(selected.getName());
            JTextField curriculumField = new JTextField(selected.getCurriculum());
            JTextField teacherIdField = new JTextField(String.valueOf(selected.getTeacher()));
            JTextField subjectsField = new JTextField(selected.getSubjects());

            Object[] message = {
                    "Название группы:", nameField,
                    "Учебный план:", curriculumField,
                    "ID преподавателя:", teacherIdField,
                    "Предметы:", subjectsField
            };

            int editOption = JOptionPane.showConfirmDialog(this, message, "Редактировать группу", JOptionPane.OK_CANCEL_OPTION);
            if (editOption == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String curriculum = curriculumField.getText().trim();
                int teacherId = Integer.parseInt(teacherIdField.getText().trim());
                String subjects = subjectsField.getText().trim();

                dbManager.updateGroup(selected.getId(), name, curriculum, teacherId, subjects);
                JOptionPane.showMessageDialog(this, "Группа обновлена.");
                showGroups();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void editSubjectDialog() {
        try {
            List<Subject> subjects = dbManager.getSubjects();
            if (subjects.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет предметов для редактирования.");
                return;
            }

            String[] columnNames = {"ID", "Название", "Преподаватель ID", "Расписание"};
            Object[][] data = new Object[subjects.size()][4];
            for (int i = 0; i < subjects.size(); i++) {
                Subject s = subjects.get(i);
                data[i][0] = s.getId();
                data[i][1] = s.getName();
                data[i][2] = s.getTeacher();
                data[i][3] = s.getSchedule();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            int option = JOptionPane.showConfirmDialog(this, scrollPane, "Выберите предмет для редактирования", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите строку.");
                return;
            }

            Subject selected = subjects.get(selectedRow);

            JTextField nameField = new JTextField(selected.getName());
            JTextField teacherIdField = new JTextField(String.valueOf(selected.getTeacher()));
            JTextField scheduleField = new JTextField(selected.getSchedule());

            Object[] message = {
                    "Название предмета:", nameField,
                    "ID преподавателя:", teacherIdField,
                    "Расписание:", scheduleField
            };

            int editOption = JOptionPane.showConfirmDialog(this, message, "Редактировать предмет", JOptionPane.OK_CANCEL_OPTION);
            if (editOption == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                int teacherId = Integer.parseInt(teacherIdField.getText().trim());
                String schedule = scheduleField.getText().trim();

                dbManager.updateSubject(selected.getId(), name, teacherId, schedule);
                JOptionPane.showMessageDialog(this, "Предмет обновлен.");
                showSubjects();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void editGradeDialog() {
        try {
            List<Grade> grades = dbManager.getGrades();
            if (grades.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет оценок для редактирования.");
                return;
            }

            String[] columnNames = {"ID", "Студент ID", "Предмет ID", "Тип", "Значение", "Дата"};
            Object[][] data = new Object[grades.size()][6];
            for (int i = 0; i < grades.size(); i++) {
                Grade g = grades.get(i);
                data[i][0] = g.getId();
                data[i][1] = g.getStudentId();
                data[i][2] = g.getSubjectId();
                data[i][3] = g.getGradeType();
                data[i][4] = g.getGradeValue();
                data[i][5] = g.getGradeDate();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            int option = JOptionPane.showConfirmDialog(this, scrollPane, "Выберите оценку для редактирования", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите строку.");
                return;
            }

            Grade selected = grades.get(selectedRow);

            JTextField studentIdField = new JTextField(String.valueOf(selected.getStudentId()));
            JTextField subjectIdField = new JTextField(String.valueOf(selected.getSubjectId()));
            JTextField gradeTypeField = new JTextField(selected.getGradeType());
            JTextField gradeValueField = new JTextField(String.valueOf(selected.getGradeValue()));
            JSpinner dateSpinner = new JSpinner(new javax.swing.SpinnerDateModel());
            dateSpinner.setValue(Date.from(selected.getGradeDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
            dateSpinner.setEditor(dateEditor);

            Object[] message = {
                    "ID студента:", studentIdField,
                    "ID предмета:", subjectIdField,
                    "Тип оценки:", gradeTypeField,
                    "Значение:", gradeValueField,
                    "Дата:", dateSpinner
            };

            int editOption = JOptionPane.showConfirmDialog(this, message, "Редактировать оценку", JOptionPane.OK_CANCEL_OPTION);
            if (editOption == JOptionPane.OK_OPTION) {
                int studentId = Integer.parseInt(studentIdField.getText().trim());
                int subjectId = Integer.parseInt(subjectIdField.getText().trim());
                String gradeType = gradeTypeField.getText().trim();
                int gradeValue = Integer.parseInt(gradeValueField.getText().trim());
                Date dateUtil = (Date) dateSpinner.getValue();
                LocalDate gradeDate = LocalDate.ofInstant(dateUtil.toInstant(), ZoneId.systemDefault());

                dbManager.updateGrade(selected.getId(), studentId, subjectId, gradeType, gradeValue, gradeDate);
                JOptionPane.showMessageDialog(this, "Оценка обновлена.");
                showGrades();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void editAttendanceDialog() {
        try {
            List<Attendance> attendances = dbManager.getAttendance();
            if (attendances.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет записей посещаемости для редактирования.");
                return;
            }

            String[] columnNames = {"ID", "Студент ID", "Урок ID", "Присутствие"};
            Object[][] data = new Object[attendances.size()][4];
            for (int i = 0; i < attendances.size(); i++) {
                Attendance a = attendances.get(i);
                data[i][0] = a.getId();
                data[i][1] = a.getStudentId();
                data[i][2] = a.getLessonId();
                data[i][3] = a.isPresent() ? "Да" : "Нет";
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            int option = JOptionPane.showConfirmDialog(this, scrollPane, "Выберите запись посещаемости для редактирования", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите строку.");
                return;
            }

            Attendance selected = attendances.get(selectedRow);

            JTextField studentIdField = new JTextField(String.valueOf(selected.getStudentId()));
            JTextField lessonIdField = new JTextField(String.valueOf(selected.getLessonId()));
            JCheckBox presentCheckBox = new JCheckBox("Присутствовал", selected.isPresent());

            Object[] message = {
                    "ID студента:", studentIdField,
                    "ID урока:", lessonIdField,
                    presentCheckBox
            };

            int editOption = JOptionPane.showConfirmDialog(this, message, "Редактировать посещаемость", JOptionPane.OK_CANCEL_OPTION);
            if (editOption == JOptionPane.OK_OPTION) {
                int studentId = Integer.parseInt(studentIdField.getText().trim());
                int lessonId = Integer.parseInt(lessonIdField.getText().trim());
                boolean isPresent = presentCheckBox.isSelected();

                dbManager.updateAttendance(selected.getId(), studentId, lessonId, isPresent);
                JOptionPane.showMessageDialog(this, "Посещаемость обновлена.");
                showAttendance();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void editLessonDialog() {
        try {
            List<Lesson> lessons = dbManager.getLessons();
            if (lessons.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет уроков для редактирования.");
                return;
            }

            // Расширенная таблица с всеми полями
            String[] columnNames = {"ID", "Предмет ID", "Номер пары", "Дата", "Тип", "Номер комнаты", "Номер здания"};
            Object[][] data = new Object[lessons.size()][7];
            for (int i = 0; i < lessons.size(); i++) {
                Lesson l = lessons.get(i);
                data[i][0] = l.getId();
                data[i][1] = l.getSubjectId();
                data[i][2] = l.getPairNumber();  // Добавлено
                data[i][3] = l.getLessonDate();
                data[i][4] = l.getType();  // "Тема" → "Тип" для точности
                data[i][5] = l.getRoomNumber();  // Добавлено
                data[i][6] = l.getBuildingNumber();  // Добавлено
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            int option = JOptionPane.showConfirmDialog(this, scrollPane, "Выберите урок для редактирования", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите строку.");
                return;
            }

            Lesson selected = lessons.get(selectedRow);

            // Поля для редактирования (все)
            JTextField subjectIdField = new JTextField(String.valueOf(selected.getSubjectId()));
            JTextField pairNumberField = new JTextField(String.valueOf(selected.getPairNumber()));  // Добавлено
            // Замена JDateChooser на JSpinner
            JSpinner dateSpinner = new JSpinner(new javax.swing.SpinnerDateModel());
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
            dateSpinner.setEditor(dateEditor);
            dateSpinner.setValue(java.sql.Date.valueOf(selected.getLessonDate()));
            JTextField typeField = new JTextField(selected.getType());  // Переименовано для ясности
            JTextField roomNumberField = new JTextField(selected.getRoomNumber());  // Добавлено
            JTextField buildingNumberField = new JTextField(selected.getBuildingNumber());  // Добавлено

            JPanel panel = new JPanel(new GridLayout(6, 2));  // 6 рядов: 6 полей
            panel.add(new JLabel("ID предмета:"));
            panel.add(subjectIdField);
            panel.add(new JLabel("Номер пары:"));
            panel.add(pairNumberField);
            panel.add(new JLabel("Дата:"));
            panel.add(dateSpinner);
            panel.add(new JLabel("Тип:"));
            panel.add(typeField);
            panel.add(new JLabel("Номер комнаты:"));
            panel.add(roomNumberField);
            panel.add(new JLabel("Номер здания:"));
            panel.add(buildingNumberField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Редактировать урок", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            // Парсинг и валидация
            int subjectId = Integer.parseInt(subjectIdField.getText().trim());
            int pairNumber = Integer.parseInt(pairNumberField.getText().trim());
            Date selectedDate = (Date) dateSpinner.getValue();
            String type = typeField.getText().trim();
            String roomNumber = roomNumberField.getText().trim();
            String buildingNumber = buildingNumberField.getText().trim();

            if (selectedDate == null || type.isEmpty() || roomNumber.isEmpty() || buildingNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля.");
                return;
            }

            LocalDate lessonDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            dbManager.updateLesson(selected.getId(), subjectId, pairNumber, type, roomNumber, buildingNumber, lessonDate);
            JOptionPane.showMessageDialog(this, "Урок обновлен.");
            showLesson();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Неверный формат числа (ID предмета или номер пары).");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void addStudentDialog() {
        JTextField firstNameField = new JTextField();
        JTextField lastNameField = new JTextField();
        JTextField middleNameField = new JTextField();
        JSpinner birthDateSpinner = new JSpinner(new javax.swing.SpinnerDateModel());
        JSpinner.DateEditor birthDateEditor = new JSpinner.DateEditor(birthDateSpinner, "yyyy-MM-dd");
        birthDateSpinner.setEditor(birthDateEditor);
        JTextField groupIdField = new JTextField();
        JTextField contactField = new JTextField();
        JTextField emailField = new JTextField();

        Object[] message = {
                "Имя:", firstNameField,
                "Фамилия:", lastNameField,
                "Отчество:", middleNameField,
                "Дата рождения:", birthDateSpinner,
                "ID группы:", groupIdField,
                "Контакт:", contactField,
                "Email:", emailField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Добавить студента", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String firstName = firstNameField.getText().trim();
                String lastName = lastNameField.getText().trim();
                String middleName = middleNameField.getText().trim();
                Date birthDateUtil = (Date) birthDateSpinner.getValue();
                LocalDate birthDate = LocalDate.ofInstant(birthDateUtil.toInstant(), ZoneId.systemDefault());
                int groupId = Integer.parseInt(groupIdField.getText().trim());
                String contact = contactField.getText().trim();
                String email = emailField.getText().trim();

                dbManager.insertStudent(firstName, lastName, middleName, birthDate, groupId, contact, email);
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
        JTextField teacherIdField = new JTextField();
        JTextField subjectsField = new JTextField();

        Object[] message = {
                "Название группы:", nameField,
                "Учебный план:", curriculumField,
                "ID преподавателя:", teacherIdField,
                "Предметы:", subjectsField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Добавить группу", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                String curriculum = curriculumField.getText().trim();
                int teacherId = Integer.parseInt(teacherIdField.getText().trim());
                String subjects = subjectsField.getText().trim();

                dbManager.insertGroup(name, curriculum, teacherId, subjects);
                JOptionPane.showMessageDialog(this, "Группа добавлена.");
                showGroups();
            } catch (Exception ex) {
                showError(ex);
            }
        }
    }
    private void addSubjectDialog() {
        JTextField nameField = new JTextField();
        JTextField teacherIdField = new JTextField();
        JTextField scheduleField = new JTextField();

        Object[] message = {
                "Название предмета:", nameField,
                "ID преподавателя:", teacherIdField,
                "Расписание:", scheduleField
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Добавить предмет", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                String name = nameField.getText().trim();
                int teacherId = Integer.parseInt(teacherIdField.getText().trim());
                String schedule = scheduleField.getText().trim();

                dbManager.insertSubject(name, teacherId, schedule);
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
        JSpinner dateSpinner = new JSpinner(new javax.swing.SpinnerDateModel());
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
                int studentId = Integer.parseInt(studentIdField.getText().trim());
                int subjectId = Integer.parseInt(subjectIdField.getText().trim());
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
    private void addAttendanceDialog() {
        JTextField studentIdField = new JTextField();
        JTextField lessonIdField = new JTextField();
        JCheckBox presentCheckBox = new JCheckBox("Присутствовал");

        Object[] message = {
                "ID студента:", studentIdField,
                "ID урока:", lessonIdField,
                presentCheckBox
        };

        int option = JOptionPane.showConfirmDialog(this, message, "Добавить посещаемость", JOptionPane.OK_CANCEL_OPTION);
        if (option == JOptionPane.OK_OPTION) {
            try {
                int studentId = Integer.parseInt(studentIdField.getText().trim());
                int lessonId = Integer.parseInt(lessonIdField.getText().trim());
                boolean isPresent = presentCheckBox.isSelected();

                dbManager.insertAttendance(studentId, lessonId, isPresent);
                JOptionPane.showMessageDialog(this, "Посещаемость добавлена.");
                showAttendance();
            } catch (Exception ex) {
                showError(ex);
            }
        }
    }
    private void addLessonDialog() {
        try {
            // Поля для добавления (все)
            JTextField subjectIdField = new JTextField();
            JTextField pairNumberField = new JTextField();  // Добавлено
            // Замена JDateChooser на JSpinner
            JSpinner dateSpinner = new JSpinner(new javax.swing.SpinnerDateModel());
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
            dateSpinner.setEditor(dateEditor);
            JTextField typeField = new JTextField();  // Переименовано
            JTextField roomNumberField = new JTextField();  // Добавлено
            JTextField buildingNumberField = new JTextField();  // Добавлено

            JPanel panel = new JPanel(new GridLayout(6, 2));  // 6 рядов
            panel.add(new JLabel("ID предмета:"));
            panel.add(subjectIdField);
            panel.add(new JLabel("Номер пары:"));
            panel.add(pairNumberField);
            panel.add(new JLabel("Дата:"));
            panel.add(dateSpinner);
            panel.add(new JLabel("Тип:"));
            panel.add(typeField);
            panel.add(new JLabel("Номер комнаты:"));
            panel.add(roomNumberField);
            panel.add(new JLabel("Номер здания:"));
            panel.add(buildingNumberField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Добавить урок", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            // Парсинг и валидация
            int subjectId = Integer.parseInt(subjectIdField.getText().trim());
            int pairNumber = Integer.parseInt(pairNumberField.getText().trim());
            Date selectedDate = (Date) dateSpinner.getValue();
            String type = typeField.getText().trim();
            String roomNumber = roomNumberField.getText().trim();
            String buildingNumber = buildingNumberField.getText().trim();

            if (selectedDate == null || type.isEmpty() || roomNumber.isEmpty() || buildingNumber.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля.");
                return;
            }

            LocalDate lessonDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            dbManager.insertLesson(subjectId, pairNumber, type, roomNumber, buildingNumber, lessonDate);
            JOptionPane.showMessageDialog(this, "Урок добавлен.");
            showLesson();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Неверный формат числа (ID предмета или номер пары).");
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showStudents() {
        try {
            List<Student> students = dbManager.getStudents();
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет данных для отображения.");
                return;
            }

            String[] columnNames = {"ID", "ФИО", "Группа ID", "Email"};
            Object[][] data = new Object[students.size()][4];

            for (int i = 0; i < students.size(); i++) {
                Student s = students.get(i);
                data[i][0] = s.getId();
                data[i][1] = s.getFirstName() + " " + s.getMiddleName() + " " + s.getLastName();
                data[i][2] = s.getGroupId();
                data[i][3] = s.getEmail();
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
            List<Group> groups = dbManager.getGroups();
            if (groups.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет данных для отображения.");
                return;
            }

            String[] columnNames = {"ID", "Название", "Преподаватель ID"};
            Object[][] data = new Object[groups.size()][3];

            for (int i = 0; i < groups.size(); i++) {
                Group g = groups.get(i);
                data[i][0] = g.getId();
                data[i][1] = g.getName();
                data[i][2] = g.getTeacher();
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
            List<Subject> subjects = dbManager.getSubjects();
            if (subjects.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет данных для отображения.");
                return;
            }

            String[] columnNames = {"ID", "Название", "Преподаватель ID"};
            Object[][] data = new Object[subjects.size()][3];

            for (int i = 0; i < subjects.size(); i++) {
                Subject s = subjects.get(i);
                data[i][0] = s.getId();
                data[i][1] = s.getName();
                data[i][2] = s.getTeacher();
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
            List<Grade> grades = dbManager.getGrades();
            if (grades.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет данных для отображения.");
                return;
            }

            String[] columnNames = {"ID", "Студент ID", "Предмет ID", "Тип", "Значение", "Дата"};
            Object[][] data = new Object[grades.size()][6];

            for (int i = 0; i < grades.size(); i++) {
                Grade g = grades.get(i);
                data[i][0] = g.getId();
                data[i][1] = g.getStudentId();
                data[i][2] = g.getSubjectId();
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
            List<Attendance> attendance = dbManager.getAttendance();
            if (attendance.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет данных для отображения.");
                return;
            }

            String[] columnNames = {"ID", "Студент ID", "Урок ID", "Присутствие"};
            Object[][] data = new Object[attendance.size()][4];

            for (int i = 0; i < attendance.size(); i++) {
                Attendance a = attendance.get(i);
                data[i][0] = a.getId();
                data[i][1] = a.getStudentId();
                data[i][2] = a.getLessonId();
                data[i][3] = a.isPresent() ? "Да" : "Нет";
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(this, scrollPane, "Посещаемость", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }
    private void showLesson() {
        try {
            List<Lesson> lessons = dbManager.getLessons();
            if (lessons.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет данных для отображения.");
                return;
            }

            String[] columnNames = {"ID", "Предмет ID", "Дата", "Тема"};
            Object[][] data = new Object[lessons.size()][4];

            for (int i = 0; i < lessons.size(); i++) {
                Lesson l = lessons.get(i);
                data[i][0] = l.getId();
                data[i][1] = l.getSubjectId();
                data[i][2] = l.getLessonDate();
                data[i][3] = l.getType();
            }

            JTable table = new JTable(data, columnNames);
            JScrollPane scrollPane = new JScrollPane(table);
            JOptionPane.showMessageDialog(this, scrollPane, "Уроки", JOptionPane.INFORMATION_MESSAGE);
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    }
}
