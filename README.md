 Booktracker Application

##  Description

This project is a command-line Java application that allows users to manage and analyze book reading habits.
It uses a SQLite database and JDBC for data storage and retrieval.

---

## Technologies Used

* Java
* SQLite
* JDBC

---

## Database Structure
booktracker/
│
├── src/
├── BooktrackerApp.java   ← Main program (menu + features)
└── DatabaseManager.java ← Handles DB connection
│
├── database/
└── booktracker.db       ← SQLite database

### User Table

* userID (INTEGER, Primary Key)
* age (INTEGER)
* name (TEXT)

### ReadingHabit Table

* habitID (INTEGER, Primary Key)
* userID (INTEGER, Foreign Key)
* book (TEXT)
* pages (INTEGER)

---

## How to Run

1. Make sure Java is installed

2. Open the project in an IDE (e.g., IntelliJ, Eclipse)

3. Add SQLite JDBC driver (if not already included)

4. Run the application:

   ```
   java BooktrackerApp
   ```

5. The database (`booktracker.db`) will be created automatically

---

## 🧪 Features Implemented

1. Add a user
2. Display reading habits for a user
3. Update book title
4. Delete a reading record
5. Calculate mean age (using SQL AVG)
6. Count users who read a specific book
7. Calculate total pages read
8. Count users who read more than one book
9. User table includes a "name" column

---

## Notes

* All calculations (mean, counts, totals) are performed using SQL queries as required.
* The application uses JDBC to interact with the SQLite database.

---

## GitHub Repository



