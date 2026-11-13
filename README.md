# 🏦 Smart Banking Management System (Mini Project)

**Stack:** HTML + CSS + JavaScript (frontend) | Spring Boot 3 + Spring Data JPA (backend) | H2 / MySQL (database)

## Features
- Open a new account (Savings / Current) – unique account number auto-generated
- Login with Account Number + 4-digit PIN (PIN stored as SHA-256 hash)
- Deposit, Withdraw (PIN required), Transfer between accounts (PIN required)
- Transaction history with running balance
- Close account
- Admin panel: all accounts + total accounts / money / transactions
- Validation + proper error messages (insufficient balance, wrong PIN, etc.)
- `@Transactional` transfer – both debit & credit succeed or both roll back

## Prerequisites
1. **JDK 17 or above** – `java -version`
2. **Maven 3.8+** – `mvn -version` (or use VS Code's Maven extension)
3. **VS Code** + extensions: *Extension Pack for Java* and *Spring Boot Extension Pack*

## How to run in VS Code
1. Unzip the project and open the folder in VS Code (`File > Open Folder`).
2. Wait for VS Code to download dependencies (bottom-right progress).
3. Run **any one** of these:
   - Open `SmartBankApplication.java` → click **Run** above `main`, or
   - Press **F5** (uses `.vscode/launch.json`), or
   - In terminal: `mvn spring-boot:run`
4. Open the browser: **http://localhost:8080**

First time: click **Open Account**, create 2 accounts, then test deposit / withdraw / transfer.

## Database
### Option 1 – H2 (default, no installation)
Data is saved in the `data/` folder. View tables at **http://localhost:8080/h2-console**
- JDBC URL: `jdbc:h2:file:./data/smartbank;AUTO_SERVER=TRUE`
- User: `sa`  Password: *(blank)*

### Option 2 – MySQL
1. In MySQL: `CREATE DATABASE smartbank;`
2. Open `src/main/resources/application.properties`
3. Comment the H2 block, uncomment the MySQL block, put your MySQL password.
4. Run again – tables `accounts` and `transactions` are created automatically.

## Project structure
```
smart-banking-system
├── pom.xml
└── src/main
    ├── java/com/smartbank
    │   ├── SmartBankApplication.java     (entry point)
    │   ├── model/        Account, BankTransaction   (JPA entities = DB tables)
    │   ├── repository/   AccountRepository, TransactionRepository  (DB access)
    │   ├── service/      BankService                (business logic)
    │   ├── controller/   BankController             (REST API)
    │   ├── dto/          Dtos                       (request objects + validation)
    │   └── exception/    BankException, GlobalExceptionHandler
    └── resources
        ├── application.properties                   (DB config)
        └── static/  index.html, css/style.css, js/app.js   (frontend)
```

## REST API
| Method | URL | Purpose |
|---|---|---|
| POST | /api/accounts | Create account |
| POST | /api/auth/login | Login |
| GET | /api/accounts | List all accounts (admin) |
| GET | /api/accounts/{accNo} | Account details |
| POST | /api/accounts/{accNo}/deposit | Deposit `{amount}` |
| POST | /api/accounts/{accNo}/withdraw | Withdraw `{amount, pin}` |
| POST | /api/transfer | Transfer `{fromAccount,toAccount,amount,pin,remark}` |
| GET | /api/accounts/{accNo}/transactions | History |
| POST | /api/accounts/{accNo}/close | Close account `{pin}` |
| GET | /api/stats | Admin statistics |

## Architecture (for viva)
`Browser (HTML/JS)  →  Controller  →  Service  →  Repository  →  Database`

## Possible improvements (extra marks)
- Spring Security + JWT login, BCrypt for PIN
- Admin login, loan module, interest calculation
- PDF statement download, email/SMS alerts
- Convert frontend to React/Angular

> Note: Admin panel is open (no admin login) to keep this a simple mini project.
