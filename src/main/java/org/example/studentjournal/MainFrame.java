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
import java.util.stream.Collectors;
import com.toedter.calendar.JDateChooser;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

class DaySpinnerDateModel extends SpinnerDateModel {
    public DaySpinnerDateModel() {
        super();
    }

    public Object getNextValue() {
        Calendar cal = Calendar.getInstance();
        cal.setTime((Date) getValue());
        cal.add(Calendar.DAY_OF_MONTH, 1);
        return cal.getTime();
    }

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
                        addAttendanceGroupDialog();
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
            List<DbManager.Student> students = dbManager.getStudents();
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет студентов для удаления.");
                return;
            }

            String[] columnNames = {"ID", "ФИО", "Дата рождения", "Группа", "Контакт"};
            Object[][] data = new Object[students.size()][5];
            for (int i = 0; i < students.size(); i++) {
                DbManager.Student s = students.get(i);
                data[i][0] = s.getId();
                data[i][1] = s.getFullName();
                data[i][2] = s.getBirthDate();
                data[i][3] = s.getGroupName();
                data[i][4] = s.getContact();
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

            DbManager.Student selected = students.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, "Удалить студента " + selected.getFullName() + "?", "Подтверждение", JOptionPane.YES_NO_OPTION);
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
            List<DbManager.Group> groups = dbManager.getGroups();
            if (groups.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет групп для удаления.");
                return;
            }

            String[] columnNames = {"ID", "Название", "Учебный план", "Преподаватель", "Предметы"};
            Object[][] data = new Object[groups.size()][5];
            for (int i = 0; i < groups.size(); i++) {
                DbManager.Group g = groups.get(i);
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

            DbManager.Group selected = groups.get(selectedRow);
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
            List<DbManager.Subject> subjects = dbManager.getSubjects();
            if (subjects.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет предметов для удаления.");
                return;
            }

            String[] columnNames = {"ID", "Название", "Преподаватель", "Расписание"};
            Object[][] data = new Object[subjects.size()][4];
            for (int i = 0; i < subjects.size(); i++) {
                DbManager.Subject s = subjects.get(i);
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

            DbManager.Subject selected = subjects.get(selectedRow);
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
            List<DbManager.Grade> grades = dbManager.getGrades();
            if (grades.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет оценок для удаления.");
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
            int option = JOptionPane.showConfirmDialog(this, scrollPane, "Выберите оценку для удаления", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите строку.");
                return;
            }

            DbManager.Grade selected = grades.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, "Удалить оценку для " + selected.getStudentName() + " по " + selected.getSubjectName() + "?", "Подтверждение", JOptionPane.YES_NO_OPTION);
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
            List<DbManager.Attendance> attendances = dbManager.getAttendance();
            if (attendances.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет записей посещаемости для удаления.");
                return;
            }

            String[] columnNames = {"ID", "Студент", "Предмет", "Дата", "Присутствие"};
            Object[][] data = new Object[attendances.size()][5];
            for (int i = 0; i < attendances.size(); i++) {
                DbManager.Attendance a = attendances.get(i);
                data[i][0] = a.getId();
                data[i][1] = a.getStudentName();
                data[i][2] = a.getSubjectName();
                data[i][3] = a.getAttendanceDate();
                data[i][4] = a.isPresent() ? "Да" : "Нет";
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

            DbManager.Attendance selected = attendances.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, "Удалить запись посещаемости для " + selected.getStudentName() + "?", "Подтверждение", JOptionPane.YES_NO_OPTION);
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
            List<DbManager.Lesson> lessons = dbManager.getLessons();
            if (lessons.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет уроков для удаления.");
                return;
            }

            String[] columnNames = {"ID", "Предмет", "Дата", "Тема"};
            Object[][] data = new Object[lessons.size()][4];
            for (int i = 0; i < lessons.size(); i++) {
                DbManager.Lesson l = lessons.get(i);
                data[i][0] = l.getId();
                data[i][1] = l.getSubjectName();
                data[i][2] = l.getLessonDate();
                data[i][3] = l.getTopic();
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

            DbManager.Lesson selected = lessons.get(selectedRow);
            int confirm = JOptionPane.showConfirmDialog(this, "Удалить урок " + selected.getTopic() + "?", "Подтверждение", JOptionPane.YES_NO_OPTION);
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
            List<DbManager.Student> students = dbManager.getStudents();
            if (students.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет студентов для редактирования.");
                return;
            }

            String[] columnNames = {"ID", "ФИО", "Дата рождения", "Группа", "Контакт"};
            Object[][] data = new Object[students.size()][5];
            for (int i = 0; i < students.size(); i++) {
                DbManager.Student s = students.get(i);
                data[i][0] = s.getId();
                data[i][1] = s.getFullName();
                data[i][2] = s.getBirthDate();
                data[i][3] = s.getGroupName();
                data[i][4] = s.getContact();
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

            DbManager.Student selected = students.get(selectedRow);

            JTextField fullNameField = new JTextField(selected.getFullName());
            JSpinner birthDateSpinner = new JSpinner(new DaySpinnerDateModel());
            birthDateSpinner.setValue(Date.from(selected.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            JSpinner.DateEditor birthDateEditor = new JSpinner.DateEditor(birthDateSpinner, "yyyy-MM-dd");
            birthDateSpinner.setEditor(birthDateEditor);
            JTextField groupField = new JTextField(selected.getGroupName());
            JTextField contactField = new JTextField(selected.getContact());

            Object[] message = {
                    "ФИО:", fullNameField,
                    "Дата рождения:", birthDateSpinner,
                    "Группа:", groupField,
                    "Контакт:", contactField
            };

            int editOption = JOptionPane.showConfirmDialog(this, message, "Редактировать студента", JOptionPane.OK_CANCEL_OPTION);
            if (editOption == JOptionPane.OK_OPTION) {
                String fullName = fullNameField.getText().trim();
                Date birthDateUtil = (Date) birthDateSpinner.getValue();
                LocalDate birthDate = LocalDate.ofInstant(birthDateUtil.toInstant(), ZoneId.systemDefault());
                String group = groupField.getText().trim();
                String contact = contactField.getText().trim();

                dbManager.updateStudent(selected.getId(), fullName, birthDate, group, contact);
                JOptionPane.showMessageDialog(this, "Студент обновлен.");
                showStudents();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void editGroupDialog() {
        try {
            List<DbManager.Group> groups = dbManager.getGroups();
            if (groups.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет групп для редактирования.");
                return;
            }

            String[] columnNames = {"ID", "Название", "Учебный план", "Преподаватель", "Предметы"};
            Object[][] data = new Object[groups.size()][5];
            for (int i = 0; i < groups.size(); i++) {
                DbManager.Group g = groups.get(i);
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

            DbManager.Group selected = groups.get(selectedRow);

            JTextField nameField = new JTextField(selected.getName());
            JTextField curriculumField = new JTextField(selected.getCurriculum());
            JTextField teacherField = new JTextField(selected.getTeacher());
            JTextField subjectsField = new JTextField(selected.getSubjects());

            Object[] message = {
                    "Название группы:", nameField,
                    "Учебный план:", curriculumField,
                    "Преподаватель:", teacherField,
                    "Предметы:", subjectsField
            };

            int editOption = JOptionPane.showConfirmDialog(this, message, "Редактировать группу", JOptionPane.OK_CANCEL_OPTION);
            if (editOption == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String curriculum = curriculumField.getText().trim();
                String teacher = teacherField.getText().trim();
                String subjects = subjectsField.getText().trim();

                dbManager.updateGroup(selected.getId(), name, curriculum, teacher, subjects);
                JOptionPane.showMessageDialog(this, "Группа обновлена.");
                showGroups();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void editSubjectDialog() {
        try {
            List<DbManager.Subject> subjects = dbManager.getSubjects();
            if (subjects.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет предметов для редактирования.");
                return;
            }

            String[] columnNames = {"ID", "Название", "Преподаватель", "Расписание"};
            Object[][] data = new Object[subjects.size()][4];
            for (int i = 0; i < subjects.size(); i++) {
                DbManager.Subject s = subjects.get(i);
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

            DbManager.Subject selected = subjects.get(selectedRow);

            JTextField nameField = new JTextField(selected.getName());
            JTextField teacherField = new JTextField(selected.getTeacher());
            JTextField scheduleField = new JTextField(selected.getSchedule());

            Object[] message = {
                    "Название предмета:", nameField,
                    "Преподаватель:", teacherField,
                    "Расписание:", scheduleField
            };

            int editOption = JOptionPane.showConfirmDialog(this, message, "Редактировать предмет", JOptionPane.OK_CANCEL_OPTION);
            if (editOption == JOptionPane.OK_OPTION) {
                String name = nameField.getText().trim();
                String teacher = teacherField.getText().trim();
                String schedule = scheduleField.getText().trim();

                dbManager.updateSubject(selected.getId(), name, teacher, schedule);
                JOptionPane.showMessageDialog(this, "Предмет обновлен.");
                showSubjects();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void editGradeDialog() {
        try {
            List<DbManager.Grade> grades = dbManager.getGrades();
            if (grades.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет оценок для редактирования.");
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
            int option = JOptionPane.showConfirmDialog(this, scrollPane, "Выберите оценку для редактирования", JOptionPane.OK_CANCEL_OPTION);
            if (option != JOptionPane.OK_OPTION) return;

            int selectedRow = table.getSelectedRow();
            if (selectedRow == -1) {
                JOptionPane.showMessageDialog(this, "Выберите строку.");
                return;
            }

            DbManager.Grade selected = grades.get(selectedRow);

            List<DbManager.Student> students = dbManager.getStudents();
            List<DbManager.Subject> subjects = dbManager.getSubjects();

            JComboBox<String> studentCombo = new JComboBox<>(students.stream().map(s -> s.getFullName()).toArray(String[]::new));
            JComboBox<String> subjectCombo = new JComboBox<>(subjects.stream().map(s -> s.getName()).toArray(String[]::new));

            for (int i = 0; i < students.size(); i++) {
                if (students.get(i).getId() == selected.getId()) {
                    studentCombo.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < subjects.size(); i++) {
                if (subjects.get(i).getId() == selected.getId()) {
                    subjectCombo.setSelectedIndex(i);
                    break;
                }
            }

            JTextField gradeTypeField = new JTextField(selected.getGradeType());
            JTextField gradeValueField = new JTextField(String.valueOf(selected.getGradeValue()));
            JSpinner dateSpinner = new JSpinner(new DaySpinnerDateModel());
            dateSpinner.setValue(Date.from(selected.getGradeDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
            dateSpinner.setEditor(dateEditor);

            Object[] message = {
                    "Студент:", studentCombo,
                    "Предмет:", subjectCombo,
                    "Тип оценки:", gradeTypeField,
                    "Значение:", gradeValueField,
                    "Дата:", dateSpinner
            };

            int editOption = JOptionPane.showConfirmDialog(this, message, "Редактировать оценку", JOptionPane.OK_CANCEL_OPTION);
            if (editOption == JOptionPane.OK_OPTION) {
                int selectedStudentIndex = studentCombo.getSelectedIndex();
                int selectedSubjectIndex = subjectCombo.getSelectedIndex();
                if (selectedStudentIndex == -1 || selectedSubjectIndex == -1) {
                    JOptionPane.showMessageDialog(this, "Выберите студента и предмет.");
                    return;
                }
                int studentId = students.get(selectedStudentIndex).getId();
                int subjectId = subjects.get(selectedSubjectIndex).getId();
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
            List<DbManager.Attendance> attendances = dbManager.getAttendance();
            if (attendances.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет записей посещаемости для редактирования.");
                return;
            }

            String[] columnNames = {"ID", "Студент", "Предмет", "Дата", "Присутствие"};
            Object[][] data = new Object[attendances.size()][5];
            for (int i = 0; i < attendances.size(); i++) {
                DbManager.Attendance a = attendances.get(i);
                data[i][0] = a.getId();
                data[i][1] = a.getStudentName();
                data[i][2] = a.getSubjectName();
                data[i][3] = a.getAttendanceDate();
                data[i][4] = a.isPresent() ? "Да" : "Нет";
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

            DbManager.Attendance selected = attendances.get(selectedRow);

            List<DbManager.Student> students = dbManager.getStudents();
            List<DbManager.Subject> subjects = dbManager.getSubjects();

            JComboBox<String> studentCombo = new JComboBox<>(students.stream().map(s -> s.getFullName()).toArray(String[]::new));
            JComboBox<String> subjectCombo = new JComboBox<>(subjects.stream().map(s -> s.getName()).toArray(String[]::new));

            for (int i = 0; i < students.size(); i++) {
                if (students.get(i).getId() == selected.getId()) {
                    studentCombo.setSelectedIndex(i);
                    break;
                }
            }
            for (int i = 0; i < subjects.size(); i++) {
                if (subjects.get(i).getId() == selected.getId()) {
                    subjectCombo.setSelectedIndex(i);
                    break;
                }
            }

            JSpinner dateSpinner = new JSpinner(new DaySpinnerDateModel());
            dateSpinner.setValue(Date.from(selected.getAttendanceDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
            JSpinner.DateEditor dateEditor = new JSpinner.DateEditor(dateSpinner, "yyyy-MM-dd");
            dateSpinner.setEditor(dateEditor);
            JCheckBox presentCheckBox = new JCheckBox("Присутствовал", selected.isPresent());

            Object[] message = {
                    "Студент:", studentCombo,
                    "Предмет:", subjectCombo,
                    "Дата:", dateSpinner,
                    presentCheckBox
            };

            int editOption = JOptionPane.showConfirmDialog(this, message, "Редактировать посещаемость", JOptionPane.OK_CANCEL_OPTION);
            if (editOption == JOptionPane.OK_OPTION) {
                int selectedStudentIndex = studentCombo.getSelectedIndex();
                int selectedSubjectIndex = subjectCombo.getSelectedIndex();
                if (selectedStudentIndex == -1 || selectedSubjectIndex == -1) {
                    JOptionPane.showMessageDialog(this, "Выберите студента и предмет.");
                    return;
                }
                int studentId = students.get(selectedStudentIndex).getId();
                int subjectId = subjects.get(selectedSubjectIndex).getId();
                Date dateUtil = (Date) dateSpinner.getValue();
                LocalDate attendanceDate = LocalDate.ofInstant(dateUtil.toInstant(), ZoneId.systemDefault());
                boolean isPresent = presentCheckBox.isSelected();

                dbManager.updateAttendance(selected.getId(), studentId, subjectId, attendanceDate, isPresent);
                JOptionPane.showMessageDialog(this, "Посещаемость обновлена.");
                showAttendance();
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }
    private void editLessonDialog() {
        try {
            List<DbManager.Lesson> lessons = dbManager.getLessons();
            if (lessons.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет уроков для редактирования.");
                return;
            }

            String[] columnNames = {"ID", "Предмет", "Дата", "Тема"};
            Object[][] data = new Object[lessons.size()][4];
            for (int i = 0; i < lessons.size(); i++) {
                DbManager.Lesson l = lessons.get(i);
                data[i][0] = l.getId();
                data[i][1] = l.getSubjectName();
                data[i][2] = l.getLessonDate();
                data[i][3] = l.getTopic();
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

            DbManager.Lesson selected = lessons.get(selectedRow);

            List<DbManager.Subject> subjects = dbManager.getSubjects();
            JComboBox<DbManager.Subject> subjectCombo = new JComboBox<>(subjects.toArray(new DbManager.Subject[0]));
            subjectCombo.setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof DbManager.Subject) {
                        setText(((DbManager.Subject) value).getName());
                    }
                    return this;
                }
            });
            for (int i = 0; i < subjects.size(); i++) {
                if (subjects.get(i).getName().equals(selected.getSubjectName())) {
                    subjectCombo.setSelectedIndex(i);
                    break;
                }
            }

            JDateChooser dateChooser = new JDateChooser();
            dateChooser.setDateFormatString("yyyy-MM-dd");
            dateChooser.setDate(java.sql.Date.valueOf(selected.getLessonDate()));

            JTextField topicField = new JTextField(selected.getTopic());

            JPanel panel = new JPanel(new GridLayout(3, 2));
            panel.add(new JLabel("Предмет:"));
            panel.add(subjectCombo);
            panel.add(new JLabel("Дата:"));
            panel.add(dateChooser);
            panel.add(new JLabel("Тема:"));
            panel.add(topicField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Редактировать урок", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            DbManager.Subject selectedSubject = (DbManager.Subject) subjectCombo.getSelectedItem();
            Date selectedDate = dateChooser.getDate();
            String topic = topicField.getText().trim();

            if (selectedDate == null || topic.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля.");
                return;
            }

            LocalDate lessonDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            dbManager.updateLesson(selected.getId(), selectedSubject.getId(), lessonDate, topic);
            JOptionPane.showMessageDialog(this, "Урок обновлен.");
            showLesson();
        } catch (Exception ex) {
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
                    long studentId = ((Number) data[i][0]).longValue();
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
    private void addLessonDialog() {
        try {
            List<DbManager.Subject> subjects = dbManager.getSubjects();
            if (subjects.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Нет предметов для создания урока.");
                return;
            }

            JComboBox<DbManager.Subject> subjectCombo = new JComboBox<>(subjects.toArray(new DbManager.Subject[0]));
            subjectCombo.setRenderer(new DefaultListCellRenderer() {
                public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                    super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                    if (value instanceof DbManager.Subject) {
                        setText(((DbManager.Subject) value).getName());
                    }
                    return this;
                }
            });

            JDateChooser dateChooser = new JDateChooser();
            dateChooser.setDateFormatString("yyyy-MM-dd");

            JTextField topicField = new JTextField();

            JPanel panel = new JPanel(new GridLayout(3, 2));
            panel.add(new JLabel("Предмет:"));
            panel.add(subjectCombo);
            panel.add(new JLabel("Дата:"));
            panel.add(dateChooser);
            panel.add(new JLabel("Тема:"));
            panel.add(topicField);

            int result = JOptionPane.showConfirmDialog(this, panel, "Добавить урок", JOptionPane.OK_CANCEL_OPTION);
            if (result != JOptionPane.OK_OPTION) return;

            DbManager.Subject selectedSubject = (DbManager.Subject) subjectCombo.getSelectedItem();
            Date selectedDate = dateChooser.getDate();
            String topic = topicField.getText().trim();

            if (selectedDate == null || topic.isEmpty()) {
                JOptionPane.showMessageDialog(this, "Заполните все поля.");
                return;
            }

            LocalDate lessonDate = selectedDate.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
            dbManager.insertLesson(selectedSubject.getId(), lessonDate, topic);
            JOptionPane.showMessageDialog(this, "Урок добавлен.");
            showLesson();
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
    private void showLesson() {
        try {
            List<DbManager.Lesson> lessons = dbManager.getLessons();
            displayArea.setText("");
            for (DbManager.Lesson l : lessons) {
                displayArea.append("ID: " + l.getId() + ", Предмет: " + l.getSubjectName() + ", Дата: " + l.getLessonDate() + ", Тема: " + l.getTopic() + "\n");
            }
        } catch (Exception ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
        ex.printStackTrace();
    }
}