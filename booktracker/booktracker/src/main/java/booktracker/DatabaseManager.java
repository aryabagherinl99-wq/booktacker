package booktracker;

import java.sql.*;

public class DatabaseManager {

    private static final String DB_URL = "jdbc:sqlite:booktracker.db";

    public static Connection connect() throws SQLException {
        return DriverManager.getConnection(DB_URL);
    }

    public static void initializeDatabase() {
        String createUser = """
                CREATE TABLE IF NOT EXISTS User (
                    userID INTEGER PRIMARY KEY,
                    age    INTEGER NOT NULL,
                    gender TEXT    NOT NULL
                );
                """;

        String createReadingHabit = """
                CREATE TABLE IF NOT EXISTS ReadingHabit (
                    habitID          INTEGER PRIMARY KEY,
                    book             TEXT    NOT NULL,
                    pagesRead        INTEGER NOT NULL,
                    submissionMoment DATETIME NOT NULL,
                    user             INTEGER NOT NULL,
                    FOREIGN KEY (user) REFERENCES User(userID)
                );
                """;

        try (Connection conn = connect();
             Statement stmt = conn.createStatement()) {
            stmt.execute(createUser);
            stmt.execute(createReadingHabit);
            System.out.println("Database initialized.");
        } catch (SQLException e) {
            System.err.println("Error initializing database: " + e.getMessage());
        }
    }

    public static void seedDatabase() {
        // Check if already seeded
        try (Connection conn = connect();
             Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery("SELECT COUNT(*) FROM User")) {
            if (rs.getInt(1) > 0) {
                System.out.println("Database already seeded.");
                return;
            }
        } catch (SQLException e) {
            System.err.println("Error checking seed status: " + e.getMessage());
            return;
        }

        insertUsers();
        insertReadingHabits();
        System.out.println("Database seeded with dataset.");
    }

    private static void insertUsers() {
        String sql = "INSERT INTO User (userID, age, gender) VALUES (?, ?, ?)";
        int[][] users = {
            {1,24},{2,32},{3,18},{4,45},{5,29},{6,40},{7,21},{8,36},{9,50},{10,27},
            {11,34},{12,22},{13,48},{14,31},{15,25},{16,39},{17,30},{18,44},{19,20},{20,29},
            {21,41},{22,23},{23,38},{24,46},{25,28},{26,35},{27,19},{28,42},{29,37},{30,33},
            {31,26},{32,47},{33,21},{34,49},{35,22},{36,43},{37,24},{38,50},{39,19},{40,45},
            {41,23},{42,39},{43,32},{44,20},{45,48},{46,27},{47,38},{48,34},{49,28},{50,31},
            {51,40},{52,25},{53,22},{54,36},{55,26},{56,42},{57,29},{58,41},{59,23},{60,47},
            {61,35},{62,20},{63,33},{64,30},{65,44}
        };
        String[] genders = {
            "f","m","f","m","f","m","f","m","f","m",
            "f","m","f","m","f","m","f","m","f","m",
            "f","m","f","m","f","m","f","m","f","m",
            "f","m","f","m","f","m","f","m","f","m",
            "f","m","f","m","f","m","f","m","f","m",
            "f","m","f","m","f","m","f","m","f","m",
            "f","m","f","m","f"
        };

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (int i = 0; i < users.length; i++) {
                pstmt.setInt(1, users[i][0]);
                pstmt.setInt(2, users[i][1]);
                pstmt.setString(3, genders[i]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error inserting users: " + e.getMessage());
        }
    }

    private static void insertReadingHabits() {
        String sql = "INSERT INTO ReadingHabit (habitID, user, pagesRead, book, submissionMoment) VALUES (?, ?, ?, ?, ?)";
        Object[][] habits = {
            {1, 41, 2, "Data Analysis Using R (Low Priced Edition): A Primer for Data Scientist", "2023-02-08 19:49:34"},
            {2, 46, 107, "Head First Data Analysis: A learner's guide to big numbers, statistics, and good decisions", "2023-08-15 22:32:15"},
            {3, 25, 31, "Python for Data Analysis: Data Wrangling with Pandas, NumPy, and IPython", "2023-07-07 01:56:16"},
            {4, 48, 139, "Python for Data Analysis: Data Wrangling with Pandas, NumPy, and IPython", "2023-11-13 12:00:33"},
            {5, 44, 22, "Excel Data Analysis For Dummies (For Dummies (Computer/Tech))", "2023-01-02 22:09:03"},
            {6, 1, 19, "Getting Started with Natural Language Processing", "2023-03-02 04:10:43"},
            {7, 13, 89, "SQL for Data Analysis: Advanced Techniques for Transforming Data into Insights", "2023-09-08 21:15:19"},
            {8, 37, 117, "Qualitative Data Analysis: A Methods Sourcebook", "2023-05-02 21:34:16"},
            {9, 32, 143, "Topological Data Analysis with Applications", "2023-03-06 09:20:49"},
            {10, 16, 82, "R in Action, Third Edition: Data analysis and graphics with R and Tidyverse", "2023-09-04 20:44:07"},
            {11, 7, 54, "R in Action, Third Edition: Data analysis and graphics with R and Tidyverse", "2023-09-13 08:23:02"},
            {12, 33, 18, "SQL for Data Analytics: Perform efficient and fast data analysis with the power of SQL", "2023-02-13 06:48:41"},
            {13, 4, 133, "Topological Data Analysis with Applications", "2023-04-15 21:02:43"},
            {14, 59, 83, "Getting Started with Natural Language Processing", "2023-05-28 16:34:02"},
            {15, 35, 137, "Getting Started with Natural Language Processing", "2023-10-12 17:45:12"},
            {16, 6, 24, "SQL QuickStart Guide: The Simplified Beginner's Guide to Managing, Analyzing, and Manipulating Data With SQL", "2023-10-22 01:39:16"},
            {17, 62, 13, "Getting Started with Natural Language Processing", "2023-06-05 00:13:48"},
            {18, 2, 107, "Python in easy steps", "2023-11-16 11:14:11"},
            {19, 51, 100, "Think Python: How to Think Like a Computer Scientist", "2023-01-09 11:28:41"},
            {20, 10, 146, "Quantum Machine Learning and Optimisation in Finance: On the Road to Quantum Advantage", "2023-04-19 01:38:32"},
            {21, 2, 12, "Python Tricks: A Buffet of Awesome Python Features", "2023-07-17 09:05:26"},
            {22, 63, 36, "Think Bayes: Bayesian Statistics in Python (O'reilly)", "2023-07-30 00:54:39"},
            {23, 34, 143, "Think Bayes: Bayesian Statistics in Python (O'reilly)", "2023-02-28 03:30:41"},
            {24, 14, 62, "Python Data Science Handbook: Essential Tools for Working with Data", "2023-07-01 07:09:07"},
            {25, 50, 106, "Data Science for Supply Chain Forecasting", "2023-12-02 05:08:39"},
            {26, 15, 44, "Analyzing Social Networks Using R", "2023-09-02 07:39:32"},
            {27, 64, 6, "Fluent Python: Clear, Concise, and Effective Programming", "2023-06-17 19:32:09"},
            {28, 22, 66, "Getting Started with Natural Language Processing", "2023-09-24 03:39:19"},
            {29, 36, 43, "Boa Vs. Python", "2023-03-07 12:08:11"},
            {30, 61, 98, "Practical Linear Algebra for Data Science: From Core Concepts to Applications Using Python", "2023-01-22 00:19:00"},
            {31, 25, 12, "Preparing For A Data Interview: Learn Enough Python For A Project Or An Interview", "2023-02-24 10:31:51"},
            {32, 2, 48, "Coding Games in Python (Computer Coding for Kids)", "2023-12-01 20:17:49"},
            {33, 55, 144, "Python for Everybody: Exploring Data in Python 3", "2023-05-28 02:02:15"},
            {34, 19, 107, "Think Python: How to Think Like a Computer Scientist", "2023-06-01 23:28:42"},
            {35, 56, 86, "Categorical Data Analysis", "2023-08-03 08:13:33"},
            {36, 37, 59, "Software Architecture: The Hard Parts: Modern Trade-Off Analyses for Distributed Architectures", "2023-08-05 22:40:11"},
            {37, 8, 21, "Analyzing Social Networks Using R", "2023-02-04 10:44:49"},
            {38, 53, 31, "Python for Everybody: Exploring Data in Python 3", "2023-01-20 06:19:09"},
            {39, 33, 121, "Intro to Python for Computer Science and Data Science: Learning to Program with AI, Big Data and The Cloud", "2023-05-20 04:33:55"},
            {40, 44, 46, "Practical Linear Algebra for Data Science: From Core Concepts to Applications Using Python", "2023-08-17 23:39:33"},
            {41, 47, 57, "Thematic Analysis: A Practical Guide", "2023-06-03 02:40:10"},
            {42, 59, 68, "Statistics and Data Analysis for Financial Engineering: with R examples (Springer Texts in Statistics)", "2023-06-15 10:35:04"},
            {43, 62, 116, "Analyzing Social Networks Using R", "2023-04-27 19:17:42"},
            {44, 51, 23, "Think Bayes: Bayesian Statistics in Python (O'reilly)", "2023-04-30 08:01:44"},
            {45, 24, 10, "Categorical Data Analysis", "2023-10-25 21:12:23"},
            {46, 64, 85, "Qualitative Data Analysis: A Methods Sourcebook", "2023-01-22 05:44:07"},
            {47, 57, 38, "Statistical Analysis of Network Data with R (Use R!)", "2023-10-01 15:01:32"},
            {48, 1, 18, "Designing Data-Intensive Applications: The Big Ideas Behind Reliable, Scalable, and Maintainable Systems", "2023-06-23 14:19:37"},
            {49, 21, 38, "Categorical Data Analysis", "2023-10-30 13:31:08"},
            {50, 7, 25, "Bite-Size Python: An Introduction to Python Programming", "2023-07-29 17:22:10"},
            {51, 57, 98, "Data Science for Supply Chain Forecasting", "2023-06-23 20:30:48"},
            {52, 53, 146, "Bite-Size Python: An Introduction to Python Programming", "2023-02-07 20:40:25"},
            {53, 38, 68, "Topological Data Analysis with Applications", "2023-08-04 05:21:14"},
            {54, 64, 32, "Topological Data Analysis with Applications", "2023-04-19 17:57:23"},
            {55, 65, 96, "Qualitative Data Analysis: A Methods Sourcebook", "2023-08-27 00:36:40"},
            {56, 25, 7, "Topological Data Analysis with Applications", "2023-03-30 18:08:46"},
            {57, 22, 127, "Bite-Size Python: An Introduction to Python Programming", "2023-03-08 09:28:30"},
            {58, 62, 83, "Boa Vs. Python", "2023-03-18 08:53:24"},
            {59, 64, 72, "Time Series Forecasting in Python", "2023-07-10 04:37:27"},
            {60, 55, 94, "Python in easy steps", "2023-02-13 11:10:41"},
            {61, 41, 104, "Spatial Analysis with R", "2023-02-08 22:08:36"},
            {62, 47, 52, "R Graphics Cookbook: Practical Recipes for Visualizing Data", "2023-07-06 15:26:08"},
            {63, 31, 81, "A History of Data Visualization and Graphic Communication", "2023-09-13 00:20:39"},
            {64, 50, 136, "Murach's R for Data Analysis", "2023-02-14 03:52:38"},
            {65, 42, 14, "Topological Data Analysis with Applications", "2023-09-14 08:49:06"},
            {66, 10, 9, "The Data Lakehouse Architecture", "2023-06-17 10:09:16"},
            {67, 14, 93, "Topological Data Analysis with Applications", "2023-08-15 02:59:17"},
            {68, 12, 41, "Boa Vs. Python", "2023-05-03 10:42:32"},
            {69, 58, 98, "Qualitative Data Analysis: A Methods Sourcebook", "2023-11-09 05:57:55"},
            {70, 34, 53, "Everyday Adventures with Unruly Data", "2023-01-15 20:27:20"},
            {71, 25, 19, "Machine Learning: A First Course for Engineers and Scientists", "2023-10-04 15:51:33"},
            {72, 30, 91, "Python Data Analysis - Second Edition", "2023-07-22 22:20:10"},
            {73, 36, 106, "Exploratory Data Analysis with MATLAB (Chapman & Hall/CRC Computer Science & Data Analysis)", "2023-10-18 10:59:21"},
            {74, 61, 104, "Essential R For Data Analysis: Data manipulation and visualization using R for beginning and intermediate users", "2023-01-08 23:25:40"},
            {75, 59, 120, "R in Action: Data Analysis and Graphics with R", "2023-10-15 00:34:50"},
            {76, 61, 37, "R in Action: Data Analysis and Graphics with R", "2023-10-23 12:38:32"},
            {77, 58, 93, "Qualitative Data Analysis: A Methods Sourcebook", "2023-11-18 17:26:17"},
            {78, 62, 72, "Longitudinal Data Analysis", "2023-11-03 14:11:46"},
            {79, 47, 87, "Topological Data Analysis with Applications", "2023-04-03 07:49:37"},
            {80, 63, 143, "Statistical Analysis of Network Data with R (Use R!)", "2023-12-05 10:16:12"},
            {81, 19, 33, "Designing Data-Intensive Applications: The Big Ideas Behind Reliable, Scalable, and Maintainable Systems", "2023-07-05 11:54:01"},
            {82, 32, 4, "Boa Vs. Python", "2023-09-02 04:04:24"},
            {83, 12, 29, "Designing Cloud Data Platforms", "2023-11-25 06:21:40"},
            {84, 20, 86, "Multivariate Data Analysis", "2023-04-08 11:01:25"},
            {85, 60, 41, "Bite-Size Python: An Introduction to Python Programming", "2023-12-10 16:32:55"},
            {86, 46, 18, "Data Analysis with Python and PySpark", "2023-08-15 03:35:47"},
            {87, 46, 124, "Boas and Pythons of the World", "2023-04-14 01:00:50"},
            {88, 43, 117, "Qualitative Data Analysis: A Methods Sourcebook", "2023-01-30 00:26:25"},
            {89, 55, 21, "Qualitative Data Analysis: A Methods Sourcebook", "2023-10-09 11:38:01"},
            {90, 9, 150, "Bayesian Data Analysis for the Behavioral and Neural Sciences", "2023-01-14 23:59:45"},
            {91, 30, 62, "Root Cause Analysis", "2023-10-16 03:35:21"},
            {92, 21, 138, "Python Crash Course", "2023-04-08 04:31:02"},
            {93, 55, 44, "The Pythons' Autobiography By The Pythons", "2023-12-16 01:59:51"},
            {94, 50, 2, "Boa Vs. Python", "2023-04-29 00:23:44"},
            {95, 40, 100, "Topological Data Analysis with Applications", "2023-05-06 12:12:28"},
            {96, 14, 75, "Data Analysis with Python and PySpark", "2023-03-02 20:57:41"},
            {97, 39, 136, "Topological Data Analysis with Applications", "2023-12-07 08:46:05"},
            {98, 2, 100, "Topological Data Analysis with Applications", "2023-12-20 03:24:02"},
            {99, 19, 124, "Statistical Analysis of Network Data with R (Use R!)", "2023-02-01 17:38:20"},
            {100, 32, 95, "Designing Data-Intensive Applications: The Big Ideas Behind Reliable, Scalable, and Maintainable Systems", "2023-08-24 08:41:02"},
        };

        try (Connection conn = connect();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            for (Object[] h : habits) {
                pstmt.setInt(1, (int) h[0]);
                pstmt.setInt(2, (int) h[1]);
                pstmt.setInt(3, (int) h[2]);
                pstmt.setString(4, (String) h[3]);
                pstmt.setString(5, (String) h[4]);
                pstmt.addBatch();
            }
            pstmt.executeBatch();
        } catch (SQLException e) {
            System.err.println("Error inserting reading habits: " + e.getMessage());
        }
    }
}
