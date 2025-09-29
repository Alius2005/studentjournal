package org.example.studentjournal;

import javax.swing.*;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.List;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Выберите интерфейс:");
        System.out.println("1. Графический интерфейс (GUI)");
        System.out.println("2. Консольный интерфейс");

        int interfaceChoice;
        try {
            interfaceChoice = scanner.nextInt();
            scanner.nextLine(); // Сбрасываем буфер
        } catch (Exception e) {
            System.out.println("Неверный ввод. По умолчанию выбирается консольный интерфейс.");
            interfaceChoice = 2;
        }

        if (interfaceChoice == 1) {
            // Запуск графического интерфейса
            runGUI();
        } else {
            // Запуск консольного интерфейса
            runConsole(scanner);
        }
    }

    private static void runGUI() {
        try {
            Config config = new Config("settings.xml");
            DbManager dbManager = new DbManager(config.jdbcUrl, config.jdbcUser, config.jdbcPassword, true);

            SwingUtilities.invokeLater(() -> {
                MainFrame frame = new MainFrame(dbManager);
                frame.setVisible(true);
            });

        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Ошибка при запуске GUI: " + e.getMessage());
        }
    }

    private static void runConsole(Scanner scanner) {
        DbManager dbManager = null;

        try {
            Config config = new Config("settings.xml");
            dbManager = new DbManager(config.jdbcUrl, config.jdbcUser, config.jdbcPassword, true);

            System.out.println("Приложение запущено в консольном режиме!");

            while (true) {
                System.out.println("\nВыберите действие:");
                System.out.println("1. Добавить студента");
                System.out.println("2. Показать студентов");
                System.out.println("3. Добавить группу");
                System.out.println("4. Показать группы");
                System.out.println("5. Добавить предмет");
                System.out.println("6. Показать предметы");
                System.out.println("7. Добавить оценку");
                System.out.println("8. Показать оценки");
                System.out.println("9. Добавить посещаемость");
                System.out.println("10. Показать посещаемость");
                System.out.println("0. Выход");

                if (!scanner.hasNextInt()) {
                    System.out.println("Неверный ввод. Введите число.");
                    scanner.next(); // Сбрасываем неверный ввод
                    continue;
                }
                int choice = scanner.nextInt();
                scanner.nextLine(); // Сбрасываем буфер после nextInt

                if (choice == 0) break;

                switch (choice) {
                    case 1:
                        addStudent(scanner, dbManager);
                        break;
                    case 2:
                        List<String> students = dbManager.getStudents();
                        students.forEach(System.out::println);
                        break;
                    case 3:
                        addGroup(scanner, dbManager);
                        break;
                    case 4:
                        List<String> groups = dbManager.getGroups();
                        groups.forEach(System.out::println);
                        break;
                    case 5:
                        addSubject(scanner, dbManager);
                        break;
                    case 6:
                        List<String> subjects = dbManager.getSubjects();
                        subjects.forEach(System.out::println);
                        break;
                    case 7:
                        addGrade(scanner, dbManager);
                        break;
                    case 8:
                        List<String> grades = dbManager.getGrades();
                        grades.forEach(System.out::println);
                        break;
                    case 9:
                        addAttendance(scanner, dbManager);
                        break;
                    case 10:
                        List<String> attendance = dbManager.getAttendance();
                        attendance.forEach(System.out::println);
                        break;
                    default:
                        System.out.println("Неверный выбор.");
                }
            }

        } catch (Exception e) {
            System.err.println("Ошибка: " + e.getMessage());
            e.printStackTrace();
        } finally {
            if (scanner != null) scanner.close();
            if (dbManager != null) dbManager.close();
            System.out.println("Приложение завершено.");
        }
    }

    // Вспомогательные методы для меню (чтобы код был чище)
    private static void addStudent(Scanner scanner, DbManager dbManager) throws SQLException {
        System.out.print("Введите ФИО: ");
        String fullName = scanner.nextLine();
        System.out.print("Введите дату рождения (yyyy-mm-dd): ");
        LocalDate birthDate = LocalDate.parse(scanner.nextLine());
        System.out.print("Введите группу: ");
        String groupName = scanner.nextLine();
        System.out.print("Введите контакт: ");
        String contact = scanner.nextLine();
        dbManager.insertStudent(fullName, birthDate, groupName, contact);
        System.out.println("Студент добавлен.");
    }

    private static void addGroup(Scanner scanner, DbManager dbManager) throws SQLException {
        System.out.print("Введите название группы: ");
        String groupName = scanner.nextLine();
        System.out.print("Введите учебный план: ");
        String curriculum = scanner.nextLine();
        System.out.print("Введите преподавателя: ");
        String teacher = scanner.nextLine();
        System.out.print("Введите предметы: ");
        String subjects = scanner.nextLine();
        dbManager.insertGroup(groupName, curriculum, teacher, subjects);
        System.out.println("Группа добавлена.");
    }

    private static void addSubject(Scanner scanner, DbManager dbManager) throws SQLException {
        System.out.print("Введите название предмета: ");
        String subjectName = scanner.nextLine();
        System.out.print("Введите преподавателя: ");
        String teacher = scanner.nextLine();
        System.out.print("Введите расписание: ");
        String schedule = scanner.nextLine();
        dbManager.insertSubject(subjectName, teacher, schedule);
        System.out.println("Предмет добавлен.");
    }

    private static void addGrade(Scanner scanner, DbManager dbManager) throws SQLException {
        System.out.print("Введите ID студента: ");
        long studentId = scanner.nextLong();
        scanner.nextLine();
        System.out.print("Введите ID предмета: ");
        long subjectId = scanner.nextLong();
        scanner.nextLine();
        System.out.print("Введите тип оценки: ");
        String gradeType = scanner.nextLine();
        System.out.print("Введите оценку (число): ");
        int grade = scanner.nextInt();
        scanner.nextLine();
        System.out.print("Введите дату (yyyy-mm-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());
        dbManager.insertGrade(studentId, subjectId, gradeType, grade, date);
        System.out.println("Оценка добавлена.");
    }

    private static void addAttendance(Scanner scanner, DbManager dbManager) throws SQLException {
        System.out.print("Введите ID студента: ");
        long studentId = scanner.nextLong();
        scanner.nextLine();
        System.out.print("Введите ID предмета: ");
        long subjectId = scanner.nextLong();
        scanner.nextLine();
        System.out.print("Введите дату (yyyy-mm-dd): ");
        LocalDate date = LocalDate.parse(scanner.nextLine());
        System.out.print("Был присутен (true/false): ");
        boolean isPresent = scanner.nextBoolean();
        scanner.nextLine();
        dbManager.insertAttendance(studentId, subjectId, date, isPresent);
        System.out.println("Посещаемость добавлена.");
    }
}
