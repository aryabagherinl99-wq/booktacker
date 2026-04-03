package booktracker;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;


public class BooktrackerApp {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        DatabaseManager.initializeDatabase();
        DatabaseManager.seedDatabase();

        boolean running = true;
        while (running) {
            printMenu();
            String choice = scanner.nextLine().trim();
            System.out.println();
            switch (choice) {
                case "1"  -> addUser();
                case "2"  -> getReadingHabitsForUser();
                case "3"  -> changeBookTitle();
                case "4"  -> deleteReadingHabit();
                case "5"  -> getMeanAge();
                case "6"  -> getUsersForBook();
                case "7"  -> getTotalPagesRead();
                case "8"  -> getUsersWithMoreThanOneBook();
                case "9"  -> addNameColumn();
                case "0"  -> running = false;
                default   -> System.out.println("Invalid option. Please try again.");
            }
            System.out.println();
        }
        System.out.println("Goodbye!");
    }

    private static void printMenu() {
        System.out.println("══════════════════════════════════════");
        System.out.println("         BOOKTRACKER MENU             ");
        System.out.println("══════════════════════════════════════");
        System.out.println(" 1. Add a user");
        System.out.println(" 2. Get reading habits for a user");
        System.out.println(" 3. Change the title of a book");
        System.out.println(" 4. Delete a reading habit record");
        System.out.println(" 5. Mean age of all users");
        System.out.println(" 6. Users that read a specific book");
        System.out.println(" 7. Total pages read by all users");
        System.out.println(" 8. Users that have read more than one book");
        System.out.println(" 9. Add 'Name' column to User table");
        System.out.println(" 0. Exit");
        System.out.println("══════════════════════════════════════");
        System.out.print("Choose an option: ");
    }

    private static void addUser() {
        System.out.println("── Add User ──");
        System.out.print("Enter user ID (integer): ");
        int userID = readInt();
        System.out.print("Enter age: ");
        int age = readInt();
        System.out.print("Enter gender (m/f): ");
        String gender = scanner.nextLine().trim();

        String sql = "INSERT INTO User (userID, age, gender) VALUES (?, ?, ?)";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            pstmt.setInt(2, age);
            pstmt.setString(3, gender);
            pstmt.executeUpdate();
            System.out.printf("✔ User %d added successfully.%n", userID);
        } catch (SQLException e) {
            System.err.println("Error adding user: " + e.getMessage());
        }
    }

    private static void getReadingHabitsForUser() {
        System.out.println("── Reading Habits for User ──");
        System.out.print("Enter user ID: ");
        int userID = readInt();

        String sql = """
                SELECT rh.habitID, rh.book, rh.pagesRead, rh.submissionMoment
                FROM   ReadingHabit rh
                WHERE  rh.user = ?
                ORDER  BY rh.submissionMoment
                """;

        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, userID);
            ResultSet rs = pstmt.executeQuery();

            boolean found = false;
            System.out.printf("%-8s %-70s %-10s %-20s%n",
                    "HabitID", "Book", "Pages", "Submitted");
            System.out.println("-".repeat(110));
            while (rs.next()) {
                found = true;
                System.out.printf("%-8d %-70s %-10d %-20s%n",
                        rs.getInt("habitID"),
                        truncate(rs.getString("book"), 68),
                        rs.getInt("pagesRead"),
                        rs.getString("submissionMoment"));
            }
            if (!found) System.out.println("No reading habits found for user " + userID + ".");
        } catch (SQLException e) {
            System.err.println("Error fetching habits: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 3. Change book title
    // ─────────────────────────────────────────────────────────────────────────

    private static void changeBookTitle() {
        System.out.println("── Change Book Title ──");
        System.out.print("Enter the current book title (exact): ");
        String oldTitle = scanner.nextLine().trim();
        System.out.print("Enter the new book title: ");
        String newTitle = scanner.nextLine().trim();

        String sql = "UPDATE ReadingHabit SET book = ? WHERE book = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newTitle);
            pstmt.setString(2, oldTitle);
            int rows = pstmt.executeUpdate();
            System.out.printf("✔ Updated %d record(s) with new title.%n", rows);
        } catch (SQLException e) {
            System.err.println("Error updating title: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 4. Delete a reading habit record
    // ─────────────────────────────────────────────────────────────────────────

    private static void deleteReadingHabit() {
        System.out.println("── Delete Reading Habit ──");
        System.out.print("Enter the habit ID to delete: ");
        int habitID = readInt();

        String sql = "DELETE FROM ReadingHabit WHERE habitID = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, habitID);
            int rows = pstmt.executeUpdate();
            if (rows > 0) System.out.printf("✔ Habit %d deleted.%n", habitID);
            else System.out.println("No record found with habit ID " + habitID + ".");
        } catch (SQLException e) {
            System.err.println("Error deleting habit: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 5. Mean age of users
    // ─────────────────────────────────────────────────────────────────────────

    private static void getMeanAge() {
        System.out.println("── Mean Age of Users ──");
        String sql = "SELECT AVG(age) AS meanAge FROM User";
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                System.out.printf("Mean age: %.2f%n", rs.getDouble("meanAge"));
            }
        } catch (SQLException e) {
            System.err.println("Error calculating mean age: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 6. Users that have read a specific book
    // ─────────────────────────────────────────────────────────────────────────

    private static void getUsersForBook() {
        System.out.println("── Users That Read a Book ──");
        System.out.print("Enter the book title (exact): ");
        String title = scanner.nextLine().trim();

        String sql = "SELECT COUNT(DISTINCT user) AS userCount FROM ReadingHabit WHERE book = ?";
        try (Connection conn = DatabaseManager.connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, title);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.printf("Number of users who read \"%s\": %d%n",
                        title, rs.getInt("userCount"));
            }
        } catch (SQLException e) {
            System.err.println("Error querying users for book: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 7. Total pages read by all users
    // ─────────────────────────────────────────────────────────────────────────

    private static void getTotalPagesRead() {
        System.out.println("── Total Pages Read ──");
        String sql = "SELECT SUM(pagesRead) AS totalPages FROM ReadingHabit";
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                System.out.printf("Total pages read by all users: %d%n", rs.getLong("totalPages"));
            }
        } catch (SQLException e) {
            System.err.println("Error calculating total pages: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 8. Users that have read more than one book
    // ─────────────────────────────────────────────────────────────────────────

    private static void getUsersWithMoreThanOneBook() {
        System.out.println("── Users That Have Read More Than One Book ──");
        String sql = """
                SELECT COUNT(*) AS userCount
                FROM (
                    SELECT   user
                    FROM     ReadingHabit
                    GROUP BY user
                    HAVING   COUNT(DISTINCT book) > 1
                )
                """;
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            if (rs.next()) {
                System.out.printf("Users that have read more than one book: %d%n",
                        rs.getInt("userCount"));
            }
        } catch (SQLException e) {
            System.err.println("Error querying: " + e.getMessage());
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // 9. Add "Name" column to User table
    // ─────────────────────────────────────────────────────────────────────────

    private static void addNameColumn() {
        System.out.println("── Add 'Name' Column to User Table ──");
        String sql = "ALTER TABLE User ADD COLUMN Name TEXT";
        try (Connection conn = DatabaseManager.connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(sql);
            System.out.println("✔ Column 'Name' (TEXT) added to User table.");
        } catch (SQLException e) {
            // SQLite returns an error if the column already exists
            if (e.getMessage().contains("duplicate column")) {
                System.out.println("Column 'Name' already exists in User table.");
            } else {
                System.err.println("Error adding column: " + e.getMessage());
            }
        }
    }

    // ─────────────────────────────────────────────────────────────────────────
    // Helpers
    // ─────────────────────────────────────────────────────────────────────────

    private static int readInt() {
        while (true) {
            try {
                int value = Integer.parseInt(scanner.nextLine().trim());
                return value;
            } catch (NumberFormatException e) {
                System.out.print("Please enter a valid integer: ");
            }
        }
    }

    private static String truncate(String s, int max) {
        return (s != null && s.length() > max) ? s.substring(0, max - 1) + "…" : s;
    }
}
