package org.example.studentjournal;

import javax.swing.*;
import javax.swing.text.MaskFormatter;
import java.awt.*;
import java.sql.SQLException;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import org.example.studentjournal.POJO.*;

public class MainFrame extends JFrame {
    private DbManager dbManager;
    private JTable mainTable;
    private JSplitPane splitPane;
    private JPanel toolPanel;
    private JPanel mainPanel;
    private CardLayout cardLayout;
    private JPanel tablePanel;
    private JPanel formPanel;
    private CardLayout formCardLayout;
    private JButton toggleToolPanelBtn;
    private boolean toolPanelVisible = true;
    private String currentEntity = "students";
    private boolean isEditMode = false; // Флаг для режима редактирования
    private int editId = -1; // ID редактируемой записи

    // Поля форм для доступа (для очистки и заполнения)
    private JTextField studentFirstNameField, studentLastNameField, studentMiddleNameField, studentGroupIdField;
    private JFormattedTextField studentContactField; // Изменено на JFormattedTextField для маски телефона
    private JTextField studentEmailField;
    private JSpinner studentBirthDateSpinner;
    private JTextField groupNameField, groupCurriculumField, groupTeacherIdField, groupSubjectsField;
    private JTextField subjectNameField, subjectTeacherIdField, subjectScheduleField;
    private JTextField gradeStudentIdField, gradeSubjectIdField, gradeTypeField, gradeValueField, gradeDateField;
    private JTextField attendanceStudentIdField, attendanceLessonIdField;
    private JCheckBox attendancePresentCheckBox;
    private JTextField lessonSubjectIdField, lessonPairNumberField, lessonRoomNumberField, lessonBuildingNumberField, lessonDateField;
    private JComboBox<String> lessonTypeComboBox;
    private JTextField teacherFirstNameField, teacherLastNameField, teacherMiddleNameField;
    private JFormattedTextField teacherPhoneField; // Изменено на JFormattedTextField для маски телефона
    private JTextField teacherEmailField, teacherDepartmentField;

    public MainFrame(DbManager dbManager) {
        this.dbManager = dbManager;
        initUI();
    }

    private void initUI() {
        setTitle("Журнал студентов");
        setSize(1000, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Левая выдвигающаяся панель инструментов
        toolPanel = new JPanel();
        toolPanel.setLayout(new BoxLayout(toolPanel, BoxLayout.Y_AXIS));
        toolPanel.setPreferredSize(new Dimension(200, 600));

        // Выбор типа сущности
        JPanel entityPanel = new JPanel(new GridLayout(7, 1));
        entityPanel.setBorder(BorderFactory.createTitledBorder("Тип"));
        JRadioButton studentBtn = new JRadioButton("Студенты", true);
        JRadioButton groupBtn = new JRadioButton("Группы");
        JRadioButton subjectBtn = new JRadioButton("Предметы");
        JRadioButton gradeBtn = new JRadioButton("Оценки");
        JRadioButton attendanceBtn = new JRadioButton("Посещаемость");
        JRadioButton lessonBtn = new JRadioButton("Пары");
        JRadioButton teacherBtn = new JRadioButton("Преподаватели");
        ButtonGroup entityGroup = new ButtonGroup();
        entityGroup.add(studentBtn);
        entityGroup.add(groupBtn);
        entityGroup.add(subjectBtn);
        entityGroup.add(gradeBtn);
        entityGroup.add(attendanceBtn);
        entityGroup.add(lessonBtn);
        entityGroup.add(teacherBtn);
        entityPanel.add(studentBtn);
        entityPanel.add(groupBtn);
        entityPanel.add(subjectBtn);
        entityPanel.add(gradeBtn);
        entityPanel.add(attendanceBtn);
        entityPanel.add(lessonBtn);
        entityPanel.add(teacherBtn);

        // Действия
        JPanel actionPanel = new JPanel(new GridLayout(3, 1));
        actionPanel.setBorder(BorderFactory.createTitledBorder("Действия"));
        JButton addBtn = new JButton("Добавить");
        JButton editBtn = new JButton("Изменить");
        JButton deleteBtn = new JButton("Удалить");
        actionPanel.add(addBtn);
        actionPanel.add(editBtn);
        actionPanel.add(deleteBtn);

        toolPanel.add(entityPanel);
        toolPanel.add(actionPanel);

        // Центральная панель с CardLayout
        mainPanel = new JPanel();
        cardLayout = new CardLayout();
        mainPanel.setLayout(cardLayout);

        // Панель таблицы с кнопкой переключения вверху
        tablePanel = new JPanel(new BorderLayout());
        JPanel topPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        toggleToolPanelBtn = new JButton("Скрыть панель");
        topPanel.add(toggleToolPanelBtn);
        tablePanel.add(topPanel, BorderLayout.NORTH);
        mainTable = new JTable();
        JScrollPane tableScroll = new JScrollPane(mainTable);
        tablePanel.add(tableScroll, BorderLayout.CENTER);
        mainPanel.add(tablePanel, "table");

        // Панель форм
        formPanel = new JPanel();
        formCardLayout = new CardLayout();
        formPanel.setLayout(formCardLayout);
        formPanel.add(createStudentForm(), "students");
        formPanel.add(createGroupForm(), "groups");
        formPanel.add(createSubjectForm(), "subjects");
        formPanel.add(createGradeForm(), "grades");
        formPanel.add(createAttendanceForm(), "attendance");
        formPanel.add(createLessonForm(), "lessons");
        formPanel.add(createTeacherForm(), "teachers");
        mainPanel.add(formPanel, "form");

        // JSplitPane
        splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT, toolPanel, mainPanel);
        splitPane.setDividerLocation(200);
        splitPane.setDividerSize(5);
        add(splitPane);

        // JMenuBar
        JMenuBar menuBar = new JMenuBar();
        JMenu viewMenu = new JMenu("Вид");
        JMenuItem toggleItem = new JMenuItem("Переключить панель инструментов");
        toggleItem.addActionListener(e -> toggleToolPanel());
        viewMenu.add(toggleItem);
        menuBar.add(viewMenu);
        setJMenuBar(menuBar);

        // Слушатели для выбора типа (теперь с автоматическим показом данных)
        studentBtn.addActionListener(e -> { currentEntity = "students"; showData(); });
        groupBtn.addActionListener(e -> { currentEntity = "groups"; showData(); });
        subjectBtn.addActionListener(e -> { currentEntity = "subjects"; showData(); });
        gradeBtn.addActionListener(e -> { currentEntity = "grades"; showData(); });
        attendanceBtn.addActionListener(e -> { currentEntity = "attendance"; showData(); });
        lessonBtn.addActionListener(e -> { currentEntity = "lessons"; showData(); });
        teacherBtn.addActionListener(e -> { currentEntity = "teachers"; showData(); });

        // Слушатели для действий
        addBtn.addActionListener(e -> showFormForAdd());
        editBtn.addActionListener(e -> showFormForEdit());
        deleteBtn.addActionListener(e -> deleteSelected());

        // Кнопка переключения в tablePanel
        toggleToolPanelBtn.addActionListener(e -> toggleToolPanel());

        // По умолчанию показать студентов
        showStudents();
    }

    private void toggleToolPanel() {
        if (toolPanelVisible) {
            splitPane.setDividerLocation(0);
            toggleToolPanelBtn.setText("Показать панель");
        } else {
            splitPane.setDividerLocation(200);
            toggleToolPanelBtn.setText("Скрыть панель");
        }
        toolPanelVisible = !toolPanelVisible;
    }

    private void showData() {
        cardLayout.show(mainPanel, "table");
        switch (currentEntity) {
            case "students":
                showStudents();
                break;
            case "groups":
                showGroups();
                break;
            case "subjects":
                showSubjects();
                break;
            case "grades":
                showGrades();
                break;
            case "attendance":
                showAttendance();
                break;
            case "lessons":
                showLesson();
                break;
            case "teachers":
                showTeachers();
                break;
        }
    }

    private void showFormForAdd() {
        isEditMode = false;
        editId = -1;
        clearForm(currentEntity);
        cardLayout.show(mainPanel, "form");
        formCardLayout.show(formPanel, currentEntity);
    }

    private void showFormForEdit() {
        int selectedRow = mainTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите строку для редактирования.");
            return;
        }
        isEditMode = true;
        fillFormForEdit(currentEntity, selectedRow);
        cardLayout.show(mainPanel, "form");
        formCardLayout.show(formPanel, currentEntity);
    }

    private void deleteSelected() {
        int selectedRow = mainTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Выберите строку для удаления.");
            return;
        }
        int confirm = JOptionPane.showConfirmDialog(this, "Удалить выбранную запись?", "Подтверждение", JOptionPane.YES_NO_OPTION);
        if (confirm == JOptionPane.YES_OPTION) {
            try {
                switch (currentEntity) {
                    case "students":
                        List<Student> students = dbManager.getStudents();
                        dbManager.deleteStudent(students.get(selectedRow).getId());
                        break;
                    case "groups":
                        List<Group> groups = dbManager.getGroups();
                        dbManager.deleteGroup(groups.get(selectedRow).getId());
                        break;
                    case "subjects":
                        List<Subject> subjects = dbManager.getSubjects();
                        dbManager.deleteSubject(subjects.get(selectedRow).getId());
                        break;
                    case "grades":
                        List<Grade> grades = dbManager.getGrades();
                        dbManager.deleteGrade(grades.get(selectedRow).getId());
                        break;
                    case "attendance":
                        List<Attendance> attendances = dbManager.getAttendance();
                        dbManager.deleteAttendance(attendances.get(selectedRow).getId());
                        break;
                    case "lessons":
                        List<Lesson> lessons = dbManager.getLessons();
                        dbManager.deleteLesson(lessons.get(selectedRow).getId());
                        break;
                    case "teachers":
                        List<Teacher> teachers = dbManager.getTeachers();
                        dbManager.deleteTeacher(teachers.get(selectedRow).getId());
                        break;
                }
                showData();
            } catch (Exception ex) {
                showError(ex);
            }
        }
    }

    private JPanel createStudentForm() {
        JPanel panel = new JPanel(new GridLayout(8, 2));
        studentFirstNameField = new JTextField();
        studentLastNameField = new JTextField();
        studentMiddleNameField = new JTextField();
        studentBirthDateSpinner = new JSpinner(new DaySpinnerDateModel());
        JSpinner.DateEditor birthDateEditor = new JSpinner.DateEditor(studentBirthDateSpinner, "yyyy-MM-dd");
        studentBirthDateSpinner.setEditor(birthDateEditor);
        studentGroupIdField = new JTextField();

        // Маска для телефона: +7 (XXX) XXX-XX-XX
        MaskFormatter phoneMask = null;
        try {
            phoneMask = new MaskFormatter("+7 (###) ###-##-##");
            phoneMask.setPlaceholderCharacter('_');
        } catch (ParseException e) {
            e.printStackTrace();
        }
        studentContactField = new JFormattedTextField(phoneMask);

        studentEmailField = new JTextField();
        studentEmailField.setInputVerifier(new EmailInputVerifier());

        JButton saveBtn = new JButton("Сохранить");

        panel.add(new JLabel("Фамилия:")); panel.add(studentFirstNameField);
        panel.add(new JLabel("Имя:")); panel.add(studentLastNameField);
        panel.add(new JLabel("Отчество:")); panel.add(studentMiddleNameField);
        panel.add(new JLabel("Дата рождения:")); panel.add(studentBirthDateSpinner);
        panel.add(new JLabel("ID группы:")); panel.add(studentGroupIdField);
        panel.add(new JLabel("Контакт:")); panel.add(studentContactField);
        panel.add(new JLabel("Email:")); panel.add(studentEmailField);
        panel.add(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                String lastName = studentFirstNameField.getText().trim();
                String firstName = studentLastNameField.getText().trim();
                String middleName = studentMiddleNameField.getText().trim();
                Date birthDateUtil = (Date) studentBirthDateSpinner.getValue();
                LocalDate birthDate = LocalDate.ofInstant(birthDateUtil.toInstant(), ZoneId.systemDefault());
                int groupId = Integer.parseInt(studentGroupIdField.getText().trim());
                String contact = studentContactField.getText().trim();
                String email = studentEmailField.getText().trim();

                if (isEditMode) {
                    dbManager.updateStudent(editId, firstName, lastName, middleName, birthDate, groupId, contact, email);
                } else {
                    dbManager.insertStudent(firstName, lastName, middleName, birthDate, groupId, contact, email);
                }
                showData();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        return panel;
    }

    private JPanel createGroupForm() {
        JPanel panel = new JPanel(new GridLayout(5, 2));
        groupNameField = new JTextField();
        groupCurriculumField = new JTextField();
        groupTeacherIdField = new JTextField();
        groupSubjectsField = new JTextField();
        JButton saveBtn = new JButton("Сохранить");

        panel.add(new JLabel("Название:")); panel.add(groupNameField);
        panel.add(new JLabel("Учебный план:")); panel.add(groupCurriculumField);
        panel.add(new JLabel("ID преподавателя:")); panel.add(groupTeacherIdField);
        panel.add(new JLabel("Предметы:")); panel.add(groupSubjectsField);
        panel.add(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                String name = groupNameField.getText().trim();
                String curriculum = groupCurriculumField.getText().trim();
                int teacherId = Integer.parseInt(groupTeacherIdField.getText().trim());
                String subjects = groupSubjectsField.getText().trim();

                if (isEditMode) {
                    dbManager.updateGroup(editId, name, curriculum, teacherId, subjects);
                } else {
                    dbManager.insertGroup(name, curriculum, teacherId, subjects);
                }
                showData();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        return panel;
    }

    private JPanel createSubjectForm() {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        subjectNameField = new JTextField();
        subjectTeacherIdField = new JTextField();
        subjectScheduleField = new JTextField();
        JButton saveBtn = new JButton("Сохранить");

        panel.add(new JLabel("Название:")); panel.add(subjectNameField);
        panel.add(new JLabel("ID преподавателя:")); panel.add(subjectTeacherIdField);
        panel.add(new JLabel("Расписание:")); panel.add(subjectScheduleField);
        panel.add(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                String name = subjectNameField.getText().trim();
                int teacherId = Integer.parseInt(subjectTeacherIdField.getText().trim());
                String schedule = subjectScheduleField.getText().trim();

                if (isEditMode) {
                    dbManager.updateSubject(editId, name, teacherId, schedule);
                } else {
                    dbManager.insertSubject(name, teacherId, schedule);
                }
                showData();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        return panel;
    }

    private JPanel createGradeForm() {
        JPanel panel = new JPanel(new GridLayout(6, 2));
        gradeStudentIdField = new JTextField();
        gradeSubjectIdField = new JTextField();
        gradeTypeField = new JTextField();
        gradeValueField = new JTextField();
        gradeDateField = new JTextField();
        JButton saveBtn = new JButton("Сохранить");

        panel.add(new JLabel("ID студента:")); panel.add(gradeStudentIdField);
        panel.add(new JLabel("ID предмета:")); panel.add(gradeSubjectIdField);
        panel.add(new JLabel("Тип оценки:")); panel.add(gradeTypeField);
        panel.add(new JLabel("Значение:")); panel.add(gradeValueField);
        panel.add(new JLabel("Дата:")); panel.add(gradeDateField);
        panel.add(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                int studentId = Integer.parseInt(gradeStudentIdField.getText().trim());
                int subjectId = Integer.parseInt(gradeSubjectIdField.getText().trim());
                String gradeType = gradeTypeField.getText().trim();
                int gradeValue = Integer.parseInt(gradeValueField.getText().trim());
                LocalDate gradeDate = LocalDate.parse(gradeDateField.getText().trim());

                if (isEditMode) {
                    dbManager.updateGrade(editId, studentId, subjectId, gradeType, gradeValue, gradeDate);
                } else {
                    dbManager.insertGrade(studentId, subjectId, gradeType, gradeValue, gradeDate);
                }
                showData();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        return panel;
    }

    private JPanel createAttendanceForm() {
        JPanel panel = new JPanel(new GridLayout(4, 2));
        attendanceStudentIdField = new JTextField();
        attendanceLessonIdField = new JTextField();
        attendancePresentCheckBox = new JCheckBox("Присутствовал");
        JButton saveBtn = new JButton("Сохранить");

        panel.add(new JLabel("ID студента:")); panel.add(attendanceStudentIdField);
        panel.add(new JLabel("ID урока:")); panel.add(attendanceLessonIdField);
        panel.add(new JLabel("Присутствие:")); panel.add(attendancePresentCheckBox);
        panel.add(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                int studentId = Integer.parseInt(attendanceStudentIdField.getText().trim());
                int lessonId = Integer.parseInt(attendanceLessonIdField.getText().trim());
                boolean isPresent = attendancePresentCheckBox.isSelected();

                if (isEditMode) {
                    dbManager.updateAttendance(editId, studentId, lessonId, isPresent);
                } else {
                    dbManager.insertAttendance(studentId, lessonId, isPresent);
                }
                showData();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        return panel;
    }

    private JPanel createLessonForm() {
        JPanel panel = new JPanel(new GridLayout(7, 2));
        lessonSubjectIdField = new JTextField();
        lessonPairNumberField = new JTextField();
        lessonTypeComboBox = new JComboBox<>(new String[]{"ЛБ", "ПР", "ЛК"});
        lessonRoomNumberField = new JTextField();
        lessonBuildingNumberField = new JTextField();
        lessonDateField = new JTextField();
        JButton saveBtn = new JButton("Сохранить");

        panel.add(new JLabel("ID предмета:")); panel.add(lessonSubjectIdField);
        panel.add(new JLabel("Номер пары:")); panel.add(lessonPairNumberField);
        panel.add(new JLabel("Тип:")); panel.add(lessonTypeComboBox);
        panel.add(new JLabel("Номер комнаты:")); panel.add(lessonRoomNumberField);
        panel.add(new JLabel("Номер здания:")); panel.add(lessonBuildingNumberField);
        panel.add(new JLabel("Дата:")); panel.add(lessonDateField);
        panel.add(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                int subjectId = Integer.parseInt(lessonSubjectIdField.getText().trim());
                int pairNumber = Integer.parseInt(lessonPairNumberField.getText().trim());
                String type = (String) lessonTypeComboBox.getSelectedItem();
                String roomNumber = lessonRoomNumberField.getText().trim();
                String buildingNumber = lessonBuildingNumberField.getText().trim();
                LocalDate lessonDate = LocalDate.parse(lessonDateField.getText().trim());

                if (isEditMode) {
                    dbManager.updateLesson(editId, subjectId, pairNumber, type, roomNumber, buildingNumber, lessonDate);
                } else {
                    dbManager.insertLesson(subjectId, pairNumber, type, roomNumber, buildingNumber, lessonDate);
                }
                showData();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        return panel;
    }

    private JPanel createTeacherForm() {
        JPanel panel = new JPanel(new GridLayout(7, 2));
        teacherFirstNameField = new JTextField();
        teacherLastNameField = new JTextField();
        teacherMiddleNameField = new JTextField();

        // Маска для телефона: +7 (XXX) XXX-XX-XX
        MaskFormatter phoneMask = null;
        try {
            phoneMask = new MaskFormatter("+7 (###) ###-##-##");
            phoneMask.setPlaceholderCharacter('_');
        } catch (ParseException e) {
            e.printStackTrace();
        }
        teacherPhoneField = new JFormattedTextField(phoneMask);

        teacherEmailField = new JTextField();
        teacherEmailField.setInputVerifier(new EmailInputVerifier());

        teacherDepartmentField = new JTextField();
        JButton saveBtn = new JButton("Сохранить");

        panel.add(new JLabel("Фамилия:")); panel.add(teacherFirstNameField);
        panel.add(new JLabel("Имя:")); panel.add(teacherLastNameField);
        panel.add(new JLabel("Отчество:")); panel.add(teacherMiddleNameField);
        panel.add(new JLabel("Email:")); panel.add(teacherEmailField);
        panel.add(new JLabel("Контакт:")); panel.add(teacherPhoneField);
        panel.add(new JLabel("Специализация:")); panel.add(teacherDepartmentField);
        panel.add(saveBtn);

        saveBtn.addActionListener(e -> {
            try {
                String lastName = teacherFirstNameField.getText().trim();
                String firstName = teacherLastNameField.getText().trim();
                String middleName = teacherMiddleNameField.getText().trim();
                String email = teacherEmailField.getText().trim();
                String contact = teacherPhoneField.getText().trim();
                String specialization = teacherDepartmentField.getText().trim();

                if (isEditMode) {
                    dbManager.updateTeacher(editId, firstName, lastName, middleName, email, contact, specialization);
                } else {
                    dbManager.insertTeacher(firstName, lastName, middleName, email, contact, specialization);
                }
                showData();
            } catch (Exception ex) {
                showError(ex);
            }
        });

        return panel;
    }

    private void clearForm(String entity) {
        switch (entity) {
            case "students":
                studentFirstNameField.setText("");
                studentLastNameField.setText("");
                studentMiddleNameField.setText("");
                studentBirthDateSpinner.setValue(new Date());
                studentGroupIdField.setText("");
                studentContactField.setText("");
                studentEmailField.setText("");
                break;
            case "groups":
                groupNameField.setText("");
                groupCurriculumField.setText("");
                groupTeacherIdField.setText("");
                groupSubjectsField.setText("");
                break;
            case "subjects":
                subjectNameField.setText("");
                subjectTeacherIdField.setText("");
                subjectScheduleField.setText("");
                break;
            case "grades":
                gradeStudentIdField.setText("");
                gradeSubjectIdField.setText("");
                gradeTypeField.setText("");
                gradeValueField.setText("");
                gradeDateField.setText("");
                break;
            case "attendance":
                attendanceStudentIdField.setText("");
                attendanceLessonIdField.setText("");
                attendancePresentCheckBox.setSelected(false);
                break;
            case "lessons":
                lessonSubjectIdField.setText("");
                lessonPairNumberField.setText("");
                lessonTypeComboBox.setSelectedIndex(0);
                lessonRoomNumberField.setText("");
                lessonBuildingNumberField.setText("");
                lessonDateField.setText("");
                break;
            case "teachers":
                teacherFirstNameField.setText("");
                teacherLastNameField.setText("");
                teacherMiddleNameField.setText("");
                teacherPhoneField.setText("");
                teacherEmailField.setText("");
                teacherDepartmentField.setText("");
                break;
        }
    }

    private void fillFormForEdit(String entity, int row) {
        try {
            switch (entity) {
                case "students":
                    List<Student> students = dbManager.getStudents();
                    Student s = students.get(row);
                    editId = s.getId();
                    studentFirstNameField.setText(s.getFirstName());
                    studentLastNameField.setText(s.getLastName());
                    studentMiddleNameField.setText(s.getMiddleName());
                    studentBirthDateSpinner.setValue(Date.from(s.getBirthDate().atStartOfDay(ZoneId.systemDefault()).toInstant()));
                    studentGroupIdField.setText(String.valueOf(s.getGroupId()));
                    studentContactField.setText(s.getContact());
                    studentEmailField.setText(s.getEmail());
                    break;
                case "groups":
                    List<Group> groups = dbManager.getGroups();
                    Group g = groups.get(row);
                    editId = g.getId();
                    groupNameField.setText(g.getName());
                    groupCurriculumField.setText(g.getCurriculum());
                    groupTeacherIdField.setText(String.valueOf(g.getTeacher()));
                    groupSubjectsField.setText(g.getSubjects());
                    break;
                case "subjects":
                    List<Subject> subjects = dbManager.getSubjects();
                    Subject subj = subjects.get(row);
                    editId = subj.getId();
                    subjectNameField.setText(subj.getName());
                    subjectTeacherIdField.setText(String.valueOf(subj.getTeacher()));
                    subjectScheduleField.setText(subj.getSchedule());
                    break;
                case "grades":
                    List<Grade> grades = dbManager.getGrades();
                    Grade gr = grades.get(row);
                    editId = gr.getId();
                    gradeStudentIdField.setText(String.valueOf(gr.getStudentId()));
                    gradeSubjectIdField.setText(String.valueOf(gr.getSubjectId()));
                    gradeTypeField.setText(gr.getGradeType());
                    gradeValueField.setText(String.valueOf(gr.getGradeValue()));
                    gradeDateField.setText(gr.getGradeDate().toString());
                    break;
                case "attendance":
                    List<Attendance> attendances = dbManager.getAttendance();
                    Attendance att = attendances.get(row);
                    editId = att.getId();
                    attendanceStudentIdField.setText(String.valueOf(att.getStudentId()));
                    attendanceLessonIdField.setText(String.valueOf(att.getLessonId()));
                    attendancePresentCheckBox.setSelected(att.isPresent());
                    break;
                case "lessons":
                    List<Lesson> lessons = dbManager.getLessons();
                    Lesson l = lessons.get(row);
                    editId = l.getId();
                    lessonSubjectIdField.setText(String.valueOf(l.getSubjectId()));
                    lessonPairNumberField.setText(String.valueOf(l.getPairNumber()));
                    lessonTypeComboBox.setSelectedItem(l.getType());
                    lessonRoomNumberField.setText(l.getRoomNumber());
                    lessonBuildingNumberField.setText(l.getBuildingNumber());
                    lessonDateField.setText(l.getLessonDate().toString());
                    break;
                case "teachers":
                    List<Teacher> teachers = dbManager.getTeachers();
                    Teacher t = teachers.get(row);
                    editId = t.getId();
                    teacherFirstNameField.setText(t.getFirstName());
                    teacherLastNameField.setText(t.getLastName());
                    teacherMiddleNameField.setText(t.getMiddleName());
                    teacherEmailField.setText(t.getEmail());
                    teacherPhoneField.setText(t.getPhone());
                    teacherDepartmentField.setText(t.getDepartment());
                    break;
            }
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showStudents() {
        try {
            List<Student> students = dbManager.getStudents();
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
            mainTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showGroups() {
        try {
            List<Group> groups = dbManager.getGroups();
            String[] columnNames = {"ID", "Название", "Учебный план", "Преподаватель", "Предметы"};
            Object[][] data = new Object[groups.size()][5];
            for (int i = 0; i < groups.size(); i++) {
                Group g = groups.get(i);
                data[i][0] = g.getId();
                data[i][1] = g.getName();
                data[i][2] = g.getCurriculum();
                data[i][3] = g.getTeacher();
                data[i][4] = g.getSubjects();
            }
            mainTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showSubjects() {
        try {
            List<Subject> subjects = dbManager.getSubjects();
            String[] columnNames = {"ID", "Название", "Преподаватель", "Расписание"};
            Object[][] data = new Object[subjects.size()][4];
            for (int i = 0; i < subjects.size(); i++) {
                Subject s = subjects.get(i);
                data[i][0] = s.getId();
                data[i][1] = s.getName();
                data[i][2] = s.getTeacher();
                data[i][3] = s.getSchedule();
            }
            mainTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showGrades() {
        try {
            List<Grade> grades = dbManager.getGrades();
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
            mainTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showAttendance() {
        try {
            List<Attendance> attendances = dbManager.getAttendance();
            String[] columnNames = {"ID", "Студент ID", "Урок ID", "Присутствие"};
            Object[][] data = new Object[attendances.size()][4];
            for (int i = 0; i < attendances.size(); i++) {
                Attendance a = attendances.get(i);
                data[i][0] = a.getId();
                data[i][1] = a.getStudentId();
                data[i][2] = a.getLessonId();
                data[i][3] = a.isPresent() ? "Да" : "Нет";
            }
            mainTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showLesson() {
        try {
            List<Lesson> lessons = dbManager.getLessons();
            String[] columnNames = {"ID", "Предмет ID", "Пара", "Тип", "Комната", "Здание", "Дата"};
            Object[][] data = new Object[lessons.size()][7];
            for (int i = 0; i < lessons.size(); i++) {
                Lesson l = lessons.get(i);
                data[i][0] = l.getId();
                data[i][1] = l.getSubjectId();
                data[i][2] = l.getPairNumber();
                data[i][3] = l.getType();
                data[i][4] = l.getRoomNumber();
                data[i][5] = l.getBuildingNumber();
                data[i][6] = l.getLessonDate();
            }
            mainTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showTeachers() {
        try {
            List<Teacher> teachers = dbManager.getTeachers();
            String[] columnNames = {"ID", "ФИО", "Email", "Контакт", "Специализация"};
            Object[][] data = new Object[teachers.size()][5];
            for (int i = 0; i < teachers.size(); i++) {
                Teacher t = teachers.get(i);
                data[i][0] = t.getId();
                data[i][1] = t.getLastName() + " " + t.getFirstName() + " " + t.getMiddleName();
                data[i][2] = t.getEmail();
                data[i][3] = t.getPhone();
                data[i][4] = t.getDepartment();
            }
            mainTable.setModel(new javax.swing.table.DefaultTableModel(data, columnNames));
        } catch (SQLException ex) {
            showError(ex);
        }
    }

    private void showError(Exception ex) {
        JOptionPane.showMessageDialog(this, "Ошибка: " + ex.getMessage(), "Ошибка", JOptionPane.ERROR_MESSAGE);
    }

    // Внутренний класс для валидации email
    private static class EmailInputVerifier extends javax.swing.InputVerifier {
        @Override
        public boolean verify(JComponent input) {
            String text = ((JTextField) input).getText();
            if (text.isEmpty()) return true; // Пустой email разрешен, если не требуется
            String emailRegex = "^[\\w-\\.]+@([\\w-]+\\.)+[\\w-]{2,4}$";
            return text.matches(emailRegex);
        }

        @Override
        public boolean shouldYieldFocus(JComponent input) {
            boolean valid = verify(input);
            if (!valid) {
                JOptionPane.showMessageDialog(input.getParent(), "Неверный формат email.", "Ошибка", JOptionPane.ERROR_MESSAGE);
            }
            return valid;
        }
    }
}
