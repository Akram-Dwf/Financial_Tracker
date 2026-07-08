# Financial Tracker

A clean, modern, and intuitive Android application designed to track business transactions, manage cash flows, monitor active debts & receivables, and generate professional PDF reports.

## Features

- **Dashboard (Beranda)**:
  - Real-time **Actual Cash Balance** tracking (only counts cash and bank transfer methods).
  - Quick summary of active **Debt (Utang)** and **Receivables (Piutang)** across all time.
  - Quick-action buttons to add sales, purchases, or expenses.

- **Transaction Management**:
  - **Orders (Pesanan)**: Track customer orders with quantity, items, and pricing.
  - **Supplies (Belanja Bahan)**: Keep logs of stock purchases, suppliers, volume, and costs.
  - **Expenses (Biaya Lain)**: Log operational costs (utilities, rent, salaries, shipping, etc.).

- **Debt & Receivable Settlement**:
  - Manage unpaid invoices or operational debts inside a dedicated **Debt & Receivable Manager**.
  - Settle invoices with **Cash** or **Transfer** methods, updating cash balances and reports automatically.

- **Financial Analytics & Reporting**:
  - **Profit & Loss Statement (Laporan Laba Rugi)**: Exclude outstanding debts to view a realistic cash-flow picture.
  - **Interactive Charts**: Visualize allocation of funds (supplies, operations, and profit) using graphical pie charts.
  - **Monthly PDFs**: Export detailed monthly financial statements to PDF with single-page summaries and multi-page transactional breakdowns.
  - **Receipt Printing**: Print individual orders with details of items, quantity, payment methods, and total cost.

## Tech Stack

- **Platform**: Android (minSDK 26, targetSDK 36)
- **Language**: Java
- **Database**: Room Persistence Library (SQLite under the hood)
- **UI Architecture**: Model-View-ViewModel (MVVM) with LiveData
- **Dependency Injection & Jetpack Components**: ViewModel, LiveData, ViewBinding, Navigation Component
- **Reporting Library**: native Android `PdfDocument` APIs for lightweight and clean PDF generation.
- **Charts**: MPAndroidChart

## Getting Started

### Prerequisites

- Android Studio Koala / Ladybug or newer
- JDK 11 or higher
- Android SDK 26 (Android 8.0 Oreo) or above

### Installation & Build

1. Clone this repository to your local directory:
   ```bash
   git clone <repository-url>
   ```
2. Open the project in Android Studio.
3. Let Gradle sync and download dependencies.
4. To build the debug version:
   Run the app directly onto your emulator or device using the **Run** button.
5. To build the signed release APK:
   Run the following Gradle task in the terminal:
   ```bash
   ./gradlew assembleRelease
   ```
   The generated APK will be available in:
   `app/build/outputs/apk/release/app-release.apk`
