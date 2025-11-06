import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.math.BigDecimal;
import java.sql.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.text.DecimalFormat;
import java.util.List;
import java.util.ArrayList;

public class BankingApp extends JFrame {
    private JTextField accountField;
    private JTextField amountField;
    private JTextArea displayArea;
    private JTextField fromAccountField;
    private JTextField toAccountField;
    private JTextField usernameField;
    private String userRole;
    private static final int SESSION_TIMEOUT = 5 * 60 * 1000;
    private Timer sessionTimer;
    private JLabel countdownLabel;
    private int remainingTime;
    private boolean warningShown = false;
    private JTextField amountFieldcal, interestField, termField;
    private JTextArea resultArea;
    private JComboBox<String> fromCurrencyComboBox;
    private JComboBox<String> toCurrencyComboBox;
    private JTextField currencyField;
    private JTextField resultField;
    private JButton convertButton;

    private Map<String, Double> conversionRates;

    public class DatabaseConnection {
        private static final String URL = "jdbc:mysql://localhost:3306/javadb";
        private static final String USER = "root";
        private static final String PASSWORD = "tomisql@2025";

        public static Connection getConnection() throws SQLException {
            return DriverManager.getConnection(URL, USER, PASSWORD);
        }
    }

    public BankingApp() {
        if (!showLogin()) {
            System.exit(0);
        }
    }

    private void initializeAdminUI() {
        setTitle("Reliable Bank - Banking Application");
        setSize(2000, 2000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        GradientPanel backgroundPanel = new GradientPanel(new Color(0, 51, 102), new Color(0, 102, 204));
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setPreferredSize(new Dimension(2000, 1200));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        ImageIcon logoIcon = new ImageIcon(
                new ImageIcon("bank_logo.png").getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH));
        JLabel logoLabel = new JLabel(logoIcon);
        logoPanel.add(logoLabel);

        JLabel bankNameLabel = new JLabel("Reliable Bank", SwingConstants.CENTER);
        bankNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        bankNameLabel.setForeground(Color.WHITE);

        JButton logoutButton = createStyledLogoutButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());

        topPanel.add(logoPanel, BorderLayout.CENTER);
        topPanel.add(bankNameLabel, BorderLayout.SOUTH);
        topPanel.add(logoutButton, BorderLayout.EAST);

        backgroundPanel.add(topPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        mainPanel.setOpaque(false);

        accountField = new JTextField(5);
        amountField = new JTextField(5);

        JButton depositButton = createStyledButton("Deposit");
        JButton balanceButton = createStyledButton("Check Balance");
        JButton transactionHistoryButton = createStyledButton("Transaction History");
        JButton createAccountButton = createStyledButton("Create Account");
        JButton updateAccountButton = createStyledButton("Update Account");
        JButton viewAccountButton = createStyledButton("View Account");
        JButton clearButton = createStyledButton("Clear Messages");
        JButton calculatorButton = createStyledButton("Loan Calculator");
        JButton manageLoanRequestsButton = createStyledButton("Manage Loan Requests");
        JButton searchTransactionButton = createStyledButton("Search Transactions");
        JButton convertCurrencyButton = createStyledButton("Convert Currency");

        usernameField = new JTextField(20);

        displayArea = new JTextArea(10, 40);
        displayArea.setEditable(false);
        displayArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        displayArea.setPreferredSize(new Dimension(1000, 200));

        mainPanel.add(createLabeledField("Account Number:", accountField), BorderLayout.NORTH);
        mainPanel.add(createLabeledField("Amount:", amountField), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 3, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        buttonPanel.add(depositButton);
        buttonPanel.add(balanceButton);
        buttonPanel.add(transactionHistoryButton);
        buttonPanel.add(createAccountButton);
        buttonPanel.add(updateAccountButton);
        buttonPanel.add(viewAccountButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(calculatorButton);
        buttonPanel.add(manageLoanRequestsButton);
        buttonPanel.add(searchTransactionButton);
        buttonPanel.add(convertCurrencyButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        backgroundPanel.add(mainPanel, BorderLayout.CENTER);

        add(backgroundPanel, BorderLayout.CENTER);
        add(new JScrollPane(displayArea), BorderLayout.SOUTH);

        calculatorButton.addActionListener(e -> showLoanCalculator());

        createAccountButton.addActionListener(e -> createAccount());
        updateAccountButton.addActionListener(e -> updateAccount());
        viewAccountButton.addActionListener(e -> viewAccount());
        depositButton.addActionListener(e -> deposit());
        balanceButton.addActionListener(e -> checkBalance());
        clearButton.addActionListener(e -> clearMessages());
        transactionHistoryButton.addActionListener(e -> viewTransactionHistory());
        searchTransactionButton.addActionListener(e -> searchTransactions());
        manageLoanRequestsButton.addActionListener(e -> manageLoanRequests());
        convertCurrencyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initializeCurrencyConverter();
            }
        });
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void initializeUserUI() {
        setTitle("Reliable Bank - Banking Application");
        setSize(2000, 2000);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        GradientPanel backgroundPanel = new GradientPanel(new Color(0, 51, 102), new Color(0, 102, 204));
        backgroundPanel.setLayout(new BorderLayout());
        backgroundPanel.setPreferredSize(new Dimension(2000, 1200));

        JPanel topPanel = new JPanel(new BorderLayout());
        topPanel.setOpaque(false);

        JPanel logoPanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
        logoPanel.setOpaque(false);
        ImageIcon logoIcon = new ImageIcon(
                new ImageIcon("bank_logo.png").getImage().getScaledInstance(120, 120, Image.SCALE_SMOOTH));
        JLabel logoLabel = new JLabel(logoIcon);
        logoPanel.add(logoLabel);

        JLabel bankNameLabel = new JLabel("Reliable Bank", SwingConstants.CENTER);
        bankNameLabel.setFont(new Font("Segoe UI", Font.BOLD, 40));
        bankNameLabel.setForeground(Color.WHITE);

        JButton logoutButton = createStyledLogoutButton("Logout");
        logoutButton.addActionListener(e -> handleLogout());

        topPanel.add(logoPanel, BorderLayout.CENTER);
        topPanel.add(bankNameLabel, BorderLayout.SOUTH);
        topPanel.add(logoutButton, BorderLayout.EAST);

        backgroundPanel.add(topPanel, BorderLayout.NORTH);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.setBorder(BorderFactory.createEmptyBorder(30, 50, 30, 50));
        mainPanel.setOpaque(false);

        accountField = new JTextField(5);
        amountField = new JTextField(5);

        JButton depositButton = createStyledButton("Deposit");
        JButton withdrawButton = createStyledButton("Withdraw");
        JButton transferButton = createStyledButton("Transfer Funds");
        JButton balanceButton = createStyledButton("Check Balance");
        JButton transactionHistoryButton = createStyledButton("Transaction History");
        JButton loanButton = createStyledButton("Apply for Loan");
        JButton repayLoanButton = createStyledButton("Repay Loan");
        JButton clearButton = createStyledButton("Clear Messages");
        JButton calculatorButton = createStyledButton("Loan Calculator");
        JButton viewAccountDetailsButton = createStyledButton("View My Account");
        JButton convertCurrencyButton = createStyledButton("Convert Currency");

        usernameField = new JTextField(20);

        displayArea = new JTextArea(10, 40);
        displayArea.setEditable(false);
        displayArea.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY, 1));
        displayArea.setPreferredSize(new Dimension(1000, 200));

        mainPanel.add(createLabeledField("Account Number:", accountField), BorderLayout.NORTH);
        mainPanel.add(createLabeledField("Amount:", amountField), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new GridLayout(4, 3, 10, 10));
        buttonPanel.setOpaque(false);
        buttonPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));

        buttonPanel.add(depositButton);
        buttonPanel.add(withdrawButton);
        buttonPanel.add(transferButton);
        buttonPanel.add(balanceButton);
        buttonPanel.add(transactionHistoryButton);
        buttonPanel.add(loanButton);
        buttonPanel.add(repayLoanButton);
        buttonPanel.add(clearButton);
        buttonPanel.add(calculatorButton);
        buttonPanel.add(viewAccountDetailsButton);
        buttonPanel.add(convertCurrencyButton);

        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        backgroundPanel.add(mainPanel, BorderLayout.CENTER);

        add(backgroundPanel, BorderLayout.CENTER);
        add(new JScrollPane(displayArea), BorderLayout.SOUTH);

        calculatorButton.addActionListener(e -> showLoanCalculator());

        depositButton.addActionListener(e -> deposit());
        withdrawButton.addActionListener(e -> withdraw());
        balanceButton.addActionListener(e -> checkBalance());
        loanButton.addActionListener(e -> applyLoan());
        clearButton.addActionListener(e -> clearMessages());
        repayLoanButton.addActionListener(e -> repayLoan());
        transactionHistoryButton.addActionListener(e -> viewTransactionHistory());
        viewAccountDetailsButton.addActionListener(e -> viewAccountDetails());
        convertCurrencyButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                initializeCurrencyConverter();
            }
        });
        transferButton.addActionListener(e -> {
            String fromAccount = showStylishInputDialog("Enter From Account:");
            String toAccount = showStylishInputDialog("Enter To Account:");
            String amount = showStylishInputDialog("Enter Amount:");
            transferFunds(fromAccount, toAccount, amount);
        });
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private String showStylishInputDialog(String message) {
        UIManager.put("OptionPane.background", new Color(0, 51, 102));
        UIManager.put("Panel.background", new Color(0, 51, 102));
        UIManager.put("OptionPane.messageForeground", Color.WHITE);
        return JOptionPane.showInputDialog(this, message, "Reliable Bank", JOptionPane.PLAIN_MESSAGE);
    }

    private JButton createStyledButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(0, 153, 204));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 102, 153));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(0, 153, 204));
            }
        });

        return button;
    }

    private JButton createStyledLogoutButton(String text) {
        JButton button = new JButton(text);
        button.setBackground(new Color(255, 69, 58));
        button.setForeground(Color.WHITE);
        button.setFocusPainted(false);
        button.setBorder(BorderFactory.createEmptyBorder(10, 20, 10, 20));

        button.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseEntered(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(220, 50, 50));
            }

            public void mouseExited(java.awt.event.MouseEvent evt) {
                button.setBackground(new Color(255, 69, 58));
            }
        });

        return button;
    }

    class GradientPanel extends JPanel {
        private final Color startColor;
        private final Color endColor;

        public GradientPanel(Color startColor, Color endColor) {
            this.startColor = startColor;
            this.endColor = endColor;
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            Graphics2D g2d = (Graphics2D) g;
            int width = getWidth();
            int height = getHeight();
            GradientPaint gradientPaint = new GradientPaint(0, 0, startColor, width, height, endColor);
            g2d.setPaint(gradientPaint);
            g2d.fillRect(0, 0, width, height);
        }
    }

    private void handleLogout() {
        JOptionPane.showMessageDialog(this, "You have been logged out.");
        this.dispose();

        BankingApp newApp = new BankingApp();
        newApp.setVisible(true);
    }

    private JPanel createLabeledField(String label, JTextField field) {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(new JLabel(label), BorderLayout.WEST);
        panel.add(field, BorderLayout.CENTER);
        panel.setBorder(BorderFactory.createEmptyBorder(5, 5, 5, 5));
        return panel;
    }

    private boolean showLogin() {
        while (true) {
            JPanel rolePanel = new JPanel(new FlowLayout(FlowLayout.CENTER));
            rolePanel.setBackground(new Color(240, 240, 240));

            JRadioButton userRadioButton = new JRadioButton("User", true);
            JRadioButton adminRadioButton = new JRadioButton("Admin");
            ButtonGroup roleGroup = new ButtonGroup();
            roleGroup.add(userRadioButton);
            roleGroup.add(adminRadioButton);

            rolePanel.add(new JLabel("Select Role:"));
            rolePanel.add(userRadioButton);
            rolePanel.add(adminRadioButton);

            JPanel loginPanel = new JPanel(new GridLayout(5, 2, 10, 10));
            loginPanel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));
            loginPanel.setBackground(Color.WHITE);

            JTextField usernameField = new JTextField(20);
            JPasswordField passwordField = new JPasswordField(20);

            JCheckBox showPasswordCheckBox = new JCheckBox("Show Password");
            showPasswordCheckBox.addActionListener(e -> {
                passwordField.setEchoChar(showPasswordCheckBox.isSelected() ? (char) 0 : '•');
                resetSessionTimer();
            });

            countdownLabel = new JLabel("Session expires in: 05:00", JLabel.CENTER);
            countdownLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
            countdownLabel.setForeground(Color.RED);

            loginPanel.add(new JLabel("Username:", JLabel.RIGHT));
            loginPanel.add(usernameField);
            loginPanel.add(new JLabel("Password:", JLabel.RIGHT));
            loginPanel.add(passwordField);
            loginPanel.add(new JLabel());
            loginPanel.add(showPasswordCheckBox);

            JLabel createAccountLabel = new JLabel("Sign up?");
            createAccountLabel.setForeground(Color.BLACK.darker());
            createAccountLabel.setCursor(new Cursor(Cursor.HAND_CURSOR));
            createAccountLabel.setVisible(true);
            createAccountLabel.addMouseListener(new MouseAdapter() {
                public void mouseClicked(MouseEvent e) {
                    createUserAccount();
                    resetSessionTimer();
                }
            });

            userRadioButton.addActionListener(e -> {
                createAccountLabel.setVisible(true);
                resetSessionTimer();
            });

            adminRadioButton.addActionListener(e -> {
                createAccountLabel.setVisible(false);
                resetSessionTimer();
            });

            JPanel combinedPanel = new JPanel(new BorderLayout());
            combinedPanel.setBackground(new Color(230, 230, 250));
            combinedPanel.setBorder(BorderFactory.createCompoundBorder(
                    BorderFactory.createLineBorder(Color.LIGHT_GRAY, 2),
                    BorderFactory.createEmptyBorder(20, 20, 20, 20)));

            combinedPanel.add(rolePanel, BorderLayout.NORTH);
            combinedPanel.add(loginPanel, BorderLayout.CENTER);
            combinedPanel.add(createAccountLabel, BorderLayout.SOUTH);
            combinedPanel.add(countdownLabel, BorderLayout.PAGE_END);

            int option = JOptionPane.showConfirmDialog(null, combinedPanel, "WELCOME", JOptionPane.OK_CANCEL_OPTION);

            if (option == JOptionPane.OK_OPTION) {
                String role = userRadioButton.isSelected() ? "User" : "Admin";
                String username = usernameField.getText().trim();
                String password = new String(passwordField.getPassword()).trim();

                if (authenticate(username, password, role)) {
                    JOptionPane.showMessageDialog(null, role + " login successful! Welcome " + username + "!");
                    startSessionTimer();

                    if (role.equals("User")) {
                        initializeUserUI();
                    } else {
                        initializeAdminUI();
                    }

                    return true;
                } else {
                    JOptionPane.showMessageDialog(null, "Invalid " + role + " credentials. Please try again.");
                }
            } else {
                return false;
            }

        }
    }

    private void startSessionTimer() {
        remainingTime = SESSION_TIMEOUT;
        warningShown = false;

        sessionTimer = new Timer(1000, e -> {
            remainingTime--;

            int minutes = remainingTime / 60;
            int seconds = remainingTime % 60;
            countdownLabel.setText(String.format("Session expires in: %02d:%02d", minutes, seconds));

            if (remainingTime == 60 && !warningShown) {
                JOptionPane.showMessageDialog(null, "⚠️ Warning: Only 1 minute remaining! Please finish your actions.");
                warningShown = true;
            }

            if (remainingTime <= 30) {
                countdownLabel.setForeground(countdownLabel.getForeground() == Color.RED ? Color.ORANGE : Color.RED);
            }

            if (remainingTime <= 0) {
                sessionTimer.stop();
                JOptionPane.showMessageDialog(null, "Session timed out due to inactivity. Please log in again.");
                handleLogout();
            }
        });

        sessionTimer.start();
    }

    private void resetSessionTimer() {
        if (sessionTimer != null) {
            remainingTime = SESSION_TIMEOUT;
            sessionTimer.restart();
            countdownLabel.setText("Session expires in: 05:00");
            countdownLabel.setForeground(Color.RED);
            warningShown = false;
        }
    }

    private boolean authenticate(String username, String password, String role) {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javadb", "root",
                "tomisql@2025")) {

            String query = role.equals("Admin")
                    ? "SELECT * FROM admins WHERE username=? AND password=? AND role='Admin'"
                    : "SELECT * FROM customers_login WHERE username=? AND password=?";

            try (PreparedStatement stmt = conn.prepareStatement(query)) {
                stmt.setString(1, username);
                stmt.setString(2, password);
                ResultSet rs = stmt.executeQuery();

                if (rs.next()) {
                    return true;
                }
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
        }
        return false;
    }

    private void createUserAccount() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javadb", "root",
                "tomisql@2025")) {
            String accountNumber = JOptionPane.showInputDialog("Enter your Account Number:").trim();

            try (PreparedStatement checkAccountStmt = conn
                    .prepareStatement("SELECT * FROM accounts WHERE account_number = ?")) {
                checkAccountStmt.setString(1, accountNumber);
                ResultSet rs = checkAccountStmt.executeQuery();

                if (!rs.next()) {
                    JOptionPane.showMessageDialog(null, "Invalid account number. Please try again.");
                    return;
                }
            }

            String newUsername = JOptionPane.showInputDialog("Enter a new username:").trim();
            String newPassword = JOptionPane.showInputDialog("Enter a new password:").trim();

            try (PreparedStatement insertStmt = conn.prepareStatement(
                    "INSERT INTO customers_login (customer_id, username, password) VALUES (?, ?, ?)")) {
                insertStmt.setString(1, accountNumber);
                insertStmt.setString(2, newUsername);
                insertStmt.setString(3, newPassword);
                insertStmt.executeUpdate();
                JOptionPane.showMessageDialog(null, "Account created successfully!");
            } catch (SQLException e) {
                JOptionPane.showMessageDialog(null, "Error creating account: " + e.getMessage());
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(null, "Database Error: " + e.getMessage());
        }
    }

    private void deposit() {
        String account = accountField.getText().trim();
        double amount = Double.parseDouble(amountField.getText().trim());
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javadb", "root",
                "tomisql@2025");
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE Accounts SET account_balance = account_balance + ? WHERE account_number = ?")) {
            stmt.setDouble(1, amount);
            stmt.setString(2, account);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                displayArea.append("Deposited $" + amount + " to account " + account + "\n");
            } else {
                displayArea.append("Account not found.\n");
            }
        } catch (SQLException e) {
            displayArea.append("Error during deposit: " + e.getMessage() + "\n");
        }
    }

    private void withdraw() {
        String account = accountField.getText().trim();
        double amount = Double.parseDouble(amountField.getText().trim());
        double withdrawalLimit = 25000;

        if (amount > withdrawalLimit) {
            displayArea.append("Withdrawal amount exceeds the allowed limit of $" + withdrawalLimit + "\n");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javadb", "root",
                "tomisql@2025");
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE Accounts SET account_balance = account_balance - ? WHERE account_number = ? AND account_balance >= ?")) {
            stmt.setDouble(1, amount);
            stmt.setString(2, account);
            stmt.setDouble(3, amount);
            int rowsAffected = stmt.executeUpdate();
            if (rowsAffected > 0) {
                displayArea.append("Withdrawn $" + amount + " from account " + account + "\n");
            } else {
                displayArea.append("Insufficient funds or account does not exist.\n");
            }
        } catch (SQLException e) {
            displayArea.append("Error during withdrawal: " + e.getMessage() + "\n");
        }
    }

    private void applyLoan() {
        String[] loanTypes = {
                "Personal Loan (10% Interest)",
                "Home Loan (7% Interest)",
                "Car Loan (8% Interest)",
                "Education Loan (5% Interest)",
                "Business Loan (12% Interest)",
                "Gold Loan (6% Interest)",
                "Medical Emergency Loan (9% Interest)"
        };
        double[] interestRates = { 0.10, 0.07, 0.08, 0.05, 0.12, 0.06, 0.09 };

        UIManager.put("OptionPane.background", new Color(240, 240, 240));
        UIManager.put("Panel.background", new Color(240, 240, 240));
        UIManager.put("Button.background", new Color(0, 102, 204));
        UIManager.put("Button.foreground", Color.WHITE);

        JPanel loanPanel = new JPanel(new GridLayout(5, 2, 10, 10));
        loanPanel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JComboBox<String> loanDropdown = new JComboBox<>(loanTypes);
        JTextField ageField = new JTextField();
        JTextField amountField = new JTextField();

        loanPanel.add(new JLabel("Select Loan Type:"));
        loanPanel.add(loanDropdown);
        loanPanel.add(new JLabel("Enter Age:"));
        loanPanel.add(ageField);
        loanPanel.add(new JLabel("Enter Loan Amount:"));
        loanPanel.add(amountField);

        int option = JOptionPane.showConfirmDialog(null, loanPanel, "Loan Application", JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            if (ageField.getText().trim().isEmpty() || amountField.getText().trim().isEmpty()) {
                displayArea.append("Please fill all fields correctly.\n");
                return;
            }

            try {
                int age = Integer.parseInt(ageField.getText().trim());
                double loanAmount = Double.parseDouble(amountField.getText().trim());
                int selectedIndex = loanDropdown.getSelectedIndex();
                double selectedInterest = interestRates[selectedIndex];

                String account = accountField.getText().trim();

                try (Connection conn = DatabaseConnection.getConnection();
                        PreparedStatement stmt = conn
                                .prepareStatement("SELECT account_balance FROM Accounts WHERE account_number = ?")) {

                    stmt.setString(1, account);
                    ResultSet rs = stmt.executeQuery();

                    if (rs.next()) {
                        double balance = rs.getDouble("account_balance");

                        if (age >= 18 && balance >= 100000) {
                            double totalRepayment = loanAmount + (loanAmount * selectedInterest);

                            try (PreparedStatement loanStmt = conn.prepareStatement(
                                    "INSERT INTO loan_requests (account_number, amount, interest_rate, loan_type) VALUES (?, ?, ?, ?)")) {

                                loanStmt.setString(1, account);
                                loanStmt.setDouble(2, loanAmount);
                                loanStmt.setDouble(3, selectedInterest);
                                loanStmt.setString(4, loanTypes[selectedIndex]);
                                loanStmt.executeUpdate();

                                displayArea.append("✅ Loan request submitted successfully.\n");
                                displayArea.append("💰 Requested Amount: $" + loanAmount + "\n");
                                displayArea.append("💳 Total Repayment (if approved): $" + totalRepayment + "\n");
                            }
                        } else {
                            displayArea.append(
                                    "❌ Loan Denied. Age must be 18+ and balance should be at least $100,000.\n");
                        }
                    } else {
                        displayArea.append("❌ Account not found.\n");
                    }
                }
            } catch (NumberFormatException e) {
                displayArea.append("❗ Invalid input format. Please enter valid numbers.\n");
            } catch (SQLException e) {
                displayArea.append("❗ Error during loan application: " + e.getMessage() + "\n");
            }
        }
    }

    private void processLoanApproval(boolean isApproved) {
        String requestId = showStylishInputDialog("Enter Loan Request ID:");

        if (requestId != null && !requestId.trim().isEmpty()) {
            String newStatus = isApproved ? "Approved" : "Rejected";

            try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement pstmt = conn.prepareStatement(
                            "UPDATE loan_requests SET status = ? WHERE request_id = ?")) {

                pstmt.setString(1, newStatus);
                pstmt.setInt(2, Integer.parseInt(requestId));

                int updatedRows = pstmt.executeUpdate();

                if (updatedRows > 0) {
                    displayArea.append("Loan request #" + requestId + " has been " + newStatus + ".\n");

                    if (isApproved) {
                        creditLoanToUserAccount(requestId);
                    }
                } else {
                    displayArea.append("No pending loan request found with ID: " + requestId + "\n");
                }

            } catch (SQLException | NumberFormatException e) {
                displayArea.append("Error processing loan request: " + e.getMessage() + "\n");
            }
        } else {
            displayArea.append("Please provide a valid Loan Request ID.\n");
        }
    }

    private void creditLoanToUserAccount(String requestId) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement getLoanDetails = conn.prepareStatement(
                        "SELECT account_number, amount FROM loan_requests WHERE request_id = ?");
                PreparedStatement creditAccount = conn.prepareStatement(
                        "UPDATE Accounts SET loan_balance = loan_balance + ? WHERE account_number = ?")) {

            getLoanDetails.setInt(1, Integer.parseInt(requestId));

            ResultSet rs = getLoanDetails.executeQuery();

            if (rs.next()) {
                String accountNumber = rs.getString("account_number");
                BigDecimal loanAmount = rs.getBigDecimal("amount");

                creditAccount.setBigDecimal(1, loanAmount);
                creditAccount.setString(2, accountNumber);
                creditAccount.executeUpdate();

                displayArea.append("Loan amount of " + loanAmount + " credited to account: " + accountNumber + "\n");
            }
        } catch (SQLException e) {
            displayArea.append("Error crediting loan amount: " + e.getMessage() + "\n");
        }
    }

    private void manageLoanRequests() {
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javadb", "root",
                "tomisql@2025");
                PreparedStatement stmt = conn
                        .prepareStatement("SELECT * FROM loan_requests WHERE status = 'Pending'")) {

            ResultSet rs = stmt.executeQuery();

            if (!rs.isBeforeFirst()) {
                displayArea.append("✅ No pending loan requests.\n");
                return;
            }

            while (rs.next()) {
                int requestId = rs.getInt("request_id");
                String accountNumber = rs.getString("account_number");
                double amount = rs.getDouble("amount");
                String loanType = rs.getString("loan_type");
                double interestRate = rs.getDouble("interest_rate");

                String message = "🔹 Request ID: " + requestId + "\n" +
                        "💼 Account Number: " + accountNumber + "\n" +
                        "💰 Amount: $" + amount + "\n" +
                        "📋 Loan Type: " + loanType + "\n" +
                        "💲 Interest Rate: " + (interestRate * 100) + "%\n\n" +
                        "Do you want to approve or reject this loan request?";

                int decision = JOptionPane.showConfirmDialog(null, message,
                        "Approve or Reject Loan", JOptionPane.YES_NO_OPTION);

                if (decision == JOptionPane.YES_OPTION) {
                    approveLoan(requestId, accountNumber, amount);
                } else {
                    rejectLoan(requestId);
                }
            }
        } catch (SQLException e) {
            displayArea.append("❗ Error fetching loan requests: " + e.getMessage() + "\n");
        }
    }

    private void approveLoan(int requestId, String accountNumber, double amount) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement loanStmt = conn.prepareStatement(
                        "UPDATE Accounts SET loan_balance = loan_balance + ? WHERE account_number = ?");
                PreparedStatement statusStmt = conn.prepareStatement(
                        "UPDATE loan_requests SET status = 'Approved' WHERE request_id = ?")) {

            loanStmt.setDouble(1, amount);
            loanStmt.setString(2, accountNumber);
            loanStmt.executeUpdate();

            statusStmt.setInt(1, requestId);
            statusStmt.executeUpdate();

            displayArea.append("✅ Loan Approved for Account: " + accountNumber + "\n");
        } catch (SQLException e) {
            displayArea.append("❗ Error approving loan: " + e.getMessage() + "\n");
        }
    }

    private void rejectLoan(int requestId) {
        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement statusStmt = conn.prepareStatement(
                        "UPDATE loan_requests SET status = 'Rejected' WHERE request_id = ?")) {

            statusStmt.setInt(1, requestId);
            statusStmt.executeUpdate();

            displayArea.append("❌ Loan Rejected for Request ID: " + requestId + "\n");
        } catch (SQLException e) {
            displayArea.append("❗ Error rejecting loan: " + e.getMessage() + "\n");
        }
    }

    private void checkBalance() {
        String account = accountField.getText().trim();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javadb", "root",
                "tomisql@2025");
                PreparedStatement stmt = conn
                        .prepareStatement("SELECT account_balance FROM Accounts WHERE account_number = ?")) {
            stmt.setString(1, account);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                double balance = rs.getDouble("account_balance");
                displayArea.append("Account Balance for " + account + ": $" + balance + "\n");
            } else {
                displayArea.append("Account not found.\n");
            }
        } catch (SQLException e) {
            displayArea.append("Error fetching balance: " + e.getMessage() + "\n");
        }
    }

    private void createAccount() {
        String account = accountField.getText().trim();
        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javadb", "root",
                "tomisql@2025");
                PreparedStatement stmt = conn
                        .prepareStatement("INSERT INTO Accounts (account_number, account_balance) VALUES (?, 0)")) {
            stmt.setString(1, account);
            stmt.executeUpdate();
            displayArea.append("Account created successfully for account number: " + account + "\n");
        } catch (SQLException e) {
            displayArea.append("Error creating account: " + e.getMessage() + "\n");
        }
    }

    private void repayLoan() {
        String account = accountField.getText().trim();
        String amountText = amountField.getText().trim();

        if (account.isEmpty() || amountText.isEmpty()) {
            displayArea.append("check wether the account and amount field is filled.\n");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                displayArea.append("Repayment amount must be positive.\n");
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javadb", "root",
                    "tomisql@2025");
                    PreparedStatement stmt = conn.prepareStatement(
                            "UPDATE Accounts SET loan_balance = IFNULL(loan_balance, 0) - ? WHERE account_number = ? AND loan_balance >= ?")) {
                stmt.setDouble(1, amount);
                stmt.setString(2, account);
                stmt.setDouble(3, amount);

                int rowsAffected = stmt.executeUpdate();
                if (rowsAffected > 0) {
                    displayArea.append("Loan repayment successful. Amount repaid: " + amount + "\n");
                } else {
                    displayArea.append("Account not found or no active loan to repay.\n");
                }
            } catch (SQLException e) {
                displayArea.append("Error repaying loan: " + e.getMessage() + "\n");
            }
        } catch (NumberFormatException e) {
            displayArea.append("Invalid amount format. Please enter a valid number.\n");
        }
    }

    private void viewAccount() {
        String account = accountField.getText().trim();
        String username = usernameField.getText().trim();

        try (Connection conn = DriverManager.getConnection(
                "jdbc:mysql://localhost:3306/javadb", "root", "tomisql@2025");
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT name, account_balance, loan_balance, phone_number, address, age " +
                                "FROM accounts WHERE account_number = ?")) {

            stmt.setString(1, account);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                double balance = rs.getDouble("account_balance");
                double loanBalance = rs.getDouble("loan_balance");
                String phoneNumber = rs.getString("phone_number");
                String address = rs.getString("address");
                String age = rs.getString("age");

                displayArea.append("✅ Account Details:\n");
                displayArea.append("🔹 Name: " + name + "\n");
                displayArea.append("💰 Balance: $" + String.format("%.2f", balance) + "\n");
                displayArea.append("💳 Loan Balance: " +
                        (loanBalance > 0 ? "$" + String.format("%.2f", loanBalance) : "No active loan") + "\n");
                displayArea.append("📞 Phone Number: " + phoneNumber + "\n");
                displayArea.append("🏠 Address: " + address + "\n");
                displayArea.append("🎂 Age: " + age + "\n");
            } else {
                displayArea.append("❌ Error: Account not found.\n");
            }
        } catch (SQLException e) {
            displayArea.append("❗ Database Error: Unable to retrieve account details.\n");
            e.printStackTrace();
        }
    }

    private void viewAccountDetails() {
        String accountNumber = accountField.getText().trim();

        if (accountNumber.isEmpty()) {
            displayArea.append("❗ Please enter your account number in the field.\n");
            return;
        }

        try (Connection conn = DatabaseConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT name, account_balance, loan_balance, phone_number, address, age, created_at " +
                                "FROM Accounts WHERE account_number = ?")) {

            stmt.setString(1, accountNumber);
            ResultSet rs = stmt.executeQuery();

            if (rs.next()) {
                String name = rs.getString("name");
                double balance = rs.getDouble("account_balance");
                double loanBalance = rs.getDouble("loan_balance");
                String phone = rs.getString("phone_number");
                String address = rs.getString("address");
                int age = rs.getInt("age");
                Timestamp createdAt = rs.getTimestamp("created_at");

                displayArea.append("🔍 Account Details:\n");
                displayArea.append("👤 Name: " + name + "\n");
                displayArea.append("💰 Balance: $" + balance + "\n");
                displayArea.append("💳 Loan Balance: $" + loanBalance + "\n");
                displayArea.append("📞 Phone: " + (phone != null ? phone : "N/A") + "\n");
                displayArea.append("🏠 Address: " + (address != null ? address : "N/A") + "\n");
                displayArea.append("🎂 Age: " + (age != 0 ? age : "N/A") + "\n");
                displayArea.append("📅 Account Created On: " + createdAt + "\n");
            } else {
                displayArea.append("❌ Account not found.\n");
            }

        } catch (SQLException e) {
            displayArea.append("❗ Error fetching account details: " + e.getMessage() + "\n");
        }
    }

    private void updateAccount() {
        String account = accountField.getText().trim();
        String newPhone = JOptionPane.showInputDialog("Enter  phone number:");
        String newAddress = JOptionPane.showInputDialog("Enter  address:");
        String newAge = JOptionPane.showInputDialog("Enter age:");

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javadb", "root",
                "tomisql@2025");
                PreparedStatement stmt = conn.prepareStatement(
                        "UPDATE Accounts SET phone_number = ?, address = ?, age = ? WHERE account_number = ?")) {
            stmt.setString(1, newPhone);
            stmt.setString(2, newAddress);
            stmt.setString(3, newAge);
            stmt.setString(4, account);

            int rowsUpdated = stmt.executeUpdate();
            if (rowsUpdated > 0) {
                displayArea.append("Account details updated successfully.\n");
            } else {
                displayArea.append("Account not found.\n");
            }
        } catch (SQLException e) {
            displayArea.append("Error updating account details: " + e.getMessage() + "\n");
        }
    }

    private void transferFunds(String fromAccount, String toAccount, String amountText) {
        if (fromAccount.isEmpty() || toAccount.isEmpty() || amountText.isEmpty()) {
            displayArea.append("Please fill in all fields for the transfer.\n");
            return;
        }

        try {
            double amount = Double.parseDouble(amountText);
            if (amount <= 0) {
                displayArea.append("Amount must be greater than zero.\n");
                return;
            }

            try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javadb", "root",
                    "tomisql@2025");
                    PreparedStatement debitStmt = conn.prepareStatement(
                            "UPDATE accounts SET account_balance = account_balance - ? WHERE account_number = ?");
                    PreparedStatement creditStmt = conn.prepareStatement(
                            "UPDATE accounts SET account_balance = account_balance + ? WHERE account_number = ?");
                    PreparedStatement transactionStmt = conn.prepareStatement(
                            "INSERT INTO transactions (from_account, to_account, amount) VALUES (?, ?, ?)");) {

                conn.setAutoCommit(false);

                debitStmt.setDouble(1, amount);
                debitStmt.setString(2, fromAccount);
                creditStmt.setDouble(1, amount);
                creditStmt.setString(2, toAccount);

                int debitResult = debitStmt.executeUpdate();
                int creditResult = creditStmt.executeUpdate();

                if (debitResult > 0 && creditResult > 0) {
                    transactionStmt.setString(1, fromAccount);
                    transactionStmt.setString(2, toAccount);
                    transactionStmt.setDouble(3, amount);
                    transactionStmt.executeUpdate();
                    conn.commit();
                    displayArea.append("Transfer successful: $" + amount + " transferred from Account " + fromAccount
                            + " to Account " + toAccount + "\n");
                } else {
                    conn.rollback();
                    displayArea.append("Transfer failed. Please check account details and balance.\n");
                }
            }
        } catch (NumberFormatException e) {
            displayArea.append("Invalid amount. Please enter a valid number.\n");
        } catch (SQLException e) {
            displayArea.append("SQL Error: " + e.getMessage() + "\n");
        } catch (Exception e) {
            displayArea.append("Unexpected error: " + e.getMessage() + "\n");
        }
    }

    private void viewTransactionHistory() {
        String accountNumber = accountField.getText().trim();
        if (accountNumber.isEmpty()) {
            displayArea.append("Please enter an account number.");
            return;
        }

        try (Connection conn = DriverManager.getConnection("jdbc:mysql://localhost:3306/javadb", "root",
                "tomisql@2025");
                PreparedStatement stmt = conn.prepareStatement(
                        "SELECT from_account, to_account, amount FROM transactions WHERE from_account = ? OR to_account = ?")) {
            stmt.setString(1, accountNumber);
            stmt.setString(2, accountNumber);

            ResultSet rs = stmt.executeQuery();
            displayArea.append("Transaction History for Account " + accountNumber + ":\n");
            while (rs.next()) {
                displayArea.append("From: " + rs.getString("from_account") + " | To: " + rs.getString("to_account")
                        + " | Amount: $" + rs.getDouble("amount") + "\n");
            }
        } catch (SQLException e) {
            displayArea.append("SQL Error: " + e.getMessage() + "\n");
        }
    }

    private void showLoanCalculator() {
        JTextField loanAmountField = new JTextField(10);
        JTextField interestRateField = new JTextField(10);
        JTextField loanTermField = new JTextField(10);

        JPanel loanPanel = new JPanel(new GridLayout(4, 2, 10, 10));
        loanPanel.add(new JLabel("Loan Amount:"));
        loanPanel.add(loanAmountField);
        loanPanel.add(new JLabel("Interest Rate (%):"));
        loanPanel.add(interestRateField);
        loanPanel.add(new JLabel("Loan Term (years):"));
        loanPanel.add(loanTermField);

        int result = JOptionPane.showConfirmDialog(null, loanPanel, "Loan Calculator", JOptionPane.OK_CANCEL_OPTION);

        if (result == JOptionPane.OK_OPTION) {
            double amount = Double.parseDouble(loanAmountField.getText());
            double rate = Double.parseDouble(interestRateField.getText()) / 100 / 12;
            int term = Integer.parseInt(loanTermField.getText()) * 12;

            double monthlyPayment = (amount * rate) / (1 - Math.pow(1 + rate, -term));
            double totalPayment = monthlyPayment * term;

            displayArea.setText("Monthly Payment: $" + String.format("%.2f", monthlyPayment) +
                    "\nTotal Payment: $" + String.format("%.2f", totalPayment));
        }
    }

    public void LoanCalculator() {
        setTitle("Loan Calculator");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);

        JPanel panel = new JPanel(new GridLayout(5, 2, 10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(15, 15, 15, 15));

        amountField = new JTextField(10);
        interestField = new JTextField(10);
        termField = new JTextField(10);
        resultArea = new JTextArea(5, 30);
        resultArea.setEditable(false);

        JButton calculateButton = new JButton("Calculate");
        calculateButton.addActionListener(e -> calculateLoan());

        panel.add(new JLabel("Loan Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("Interest Rate (%):"));
        panel.add(interestField);
        panel.add(new JLabel("Loan Term (Years):"));
        panel.add(termField);
        panel.add(calculateButton);
        panel.add(new JLabel());

        add(panel, BorderLayout.NORTH);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        setVisible(true);
    }

    private void calculateLoan() {
        try {
            double principal = Double.parseDouble(amountField.getText().trim());
            double annualInterestRate = Double.parseDouble(interestField.getText().trim());
            int termYears = Integer.parseInt(termField.getText().trim());

            double monthlyInterestRate = annualInterestRate / 100 / 12;
            int totalMonths = termYears * 12;

            double emi = (principal * monthlyInterestRate * Math.pow(1 + monthlyInterestRate, totalMonths))
                    / (Math.pow(1 + monthlyInterestRate, totalMonths) - 1);

            double totalPayment = emi * totalMonths;
            double totalInterest = totalPayment - principal;

            DecimalFormat df = new DecimalFormat("#,###.##");

            resultArea.setText("Monthly EMI: $" + df.format(emi) +
                    "\nTotal Payment: $" + df.format(totalPayment) +
                    "\nTotal Interest: $" + df.format(totalInterest));
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter valid numbers!", "Error", JOptionPane.ERROR_MESSAGE);
        }
    }

    private void searchTransactions() {
        JPanel searchPanel = new JPanel(new GridLayout(5, 2, 10, 10));

        JTextField fromAccountField = new JTextField();
        JTextField toAccountField = new JTextField();
        JTextField amountField = new JTextField();
        JTextField dateField = new JTextField();
        JTextField dateRangeField = new JTextField();

        searchPanel.add(new JLabel("From Account:"));
        searchPanel.add(fromAccountField);
        searchPanel.add(new JLabel("To Account:"));
        searchPanel.add(toAccountField);
        searchPanel.add(new JLabel("Amount:"));
        searchPanel.add(amountField);
        searchPanel.add(new JLabel("Transaction Date (YYYY-MM-DD):"));
        searchPanel.add(dateField);
        searchPanel.add(new JLabel("Date Range (e.g., 2025-03-01 to 2025-03-15):"));
        searchPanel.add(dateRangeField);

        int option = JOptionPane.showConfirmDialog(null, searchPanel, "Search Transactions",
                JOptionPane.OK_CANCEL_OPTION);

        if (option == JOptionPane.OK_OPTION) {
            String fromAccount = fromAccountField.getText().trim();
            String toAccount = toAccountField.getText().trim();
            String amount = amountField.getText().trim();
            String date = dateField.getText().trim();
            String dateRange = dateRangeField.getText().trim();

            StringBuilder query = new StringBuilder("SELECT * FROM Transactions WHERE 1=1 ");
            List<Object> params = new ArrayList<>();

            if (!fromAccount.isEmpty()) {
                query.append(" AND from_account = ?");
                params.add(fromAccount);
            }
            if (!toAccount.isEmpty()) {
                query.append(" AND to_account = ?");
                params.add(toAccount);
            }
            if (!amount.isEmpty()) {
                query.append(" AND amount = ?");
                params.add(Double.parseDouble(amount));
            }
            if (!date.isEmpty()) {
                query.append(" AND DATE(transaction_date) = ?");
                params.add(Date.valueOf(date));
            }
            if (!dateRange.isEmpty()) {
                String[] dates = dateRange.split(" to ");
                if (dates.length == 2) {
                    query.append(" AND DATE(transaction_date) BETWEEN ? AND ?");
                    params.add(Date.valueOf(dates[0].trim()));
                    params.add(Date.valueOf(dates[1].trim()));
                } else {
                    displayArea.append("❗ Invalid date range format. Use 'YYYY-MM-DD to YYYY-MM-DD'.\n");
                    return;
                }
            }

            try (Connection conn = DatabaseConnection.getConnection();
                    PreparedStatement stmt = conn.prepareStatement(query.toString())) {

                for (int i = 0; i < params.size(); i++) {
                    stmt.setObject(i + 1, params.get(i));
                }

                ResultSet rs = stmt.executeQuery();

                StringBuilder results = new StringBuilder("🔍 Search Results:\n");
                boolean found = false;

                while (rs.next()) {
                    found = true;
                    results.append("🆔 Transaction ID: ").append(rs.getInt("transaction_id"))
                            .append(" | 📤 From: ").append(rs.getString("from_account"))
                            .append(" | 📥 To: ").append(rs.getString("to_account"))
                            .append(" | 💰 Amount: $").append(rs.getDouble("amount"))
                            .append(" | 📅 Date: ").append(rs.getTimestamp("transaction_date"))
                            .append("\n");
                }

                if (!found) {
                    results.append("❗ No matching transactions found.\n");
                }

                displayArea.append(results.toString());

            } catch (SQLException | IllegalArgumentException e) {
                displayArea.append("❗ Error searching transactions: " + e.getMessage() + "\n");
            }
        }
    }

   

    public void initializeCurrencyConverter() {
        JFrame converterFrame = new JFrame("Currency Converter");
        converterFrame.setSize(400, 250);
        converterFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        converterFrame.setLayout(new BorderLayout());

        Map<String, Double> conversionRates = new HashMap<>();
        conversionRates.put("USD to EUR", 0.85);
        conversionRates.put("USD to GBP", 0.75);
        conversionRates.put("USD to INR", 75.0);
        conversionRates.put("EUR to USD", 1.18);
        conversionRates.put("GBP to USD", 1.33);
        conversionRates.put("INR to USD", 0.013);

        String[] currencies = { "USD", "EUR", "GBP", "INR" };
        JComboBox<String> fromCurrencyComboBox = new JComboBox<>(currencies);
        JComboBox<String> toCurrencyComboBox = new JComboBox<>(currencies);

        JTextField amountField = new JTextField(10);
        JTextField resultField = new JTextField(10);
        resultField.setEditable(false);

        JButton convertButton = new JButton("Convert");
        convertButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    String fromCurrency = fromCurrencyComboBox.getSelectedItem().toString();
                    String toCurrency = toCurrencyComboBox.getSelectedItem().toString();
                    double amount = Double.parseDouble(amountField.getText());

                    String key = fromCurrency + " to " + toCurrency;

                    if (conversionRates.containsKey(key)) {
                        double rate = conversionRates.get(key);
                        double convertedAmount = amount * rate;
                        resultField.setText(String.format("%.2f", convertedAmount));
                    } else {
                        JOptionPane.showMessageDialog(converterFrame, "Conversion rate not available!", "Error",
                                JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(converterFrame, "Please enter a valid number for the amount.",
                            "Input Error", JOptionPane.ERROR_MESSAGE);
                }
            }
        });

        JPanel panel = new JPanel();
        panel.setLayout(new GridLayout(5, 2, 10, 10));
        panel.add(new JLabel("Amount:"));
        panel.add(amountField);
        panel.add(new JLabel("From Currency:"));
        panel.add(fromCurrencyComboBox);
        panel.add(new JLabel("To Currency:"));
        panel.add(toCurrencyComboBox);
        panel.add(new JLabel("Converted Amount:"));
        panel.add(resultField);
        panel.add(new JLabel());
        panel.add(convertButton);

        converterFrame.add(panel, BorderLayout.CENTER);

        converterFrame.setLocationRelativeTo(null);
        converterFrame.setVisible(true);
    }

    private void clearMessages() {
        displayArea.setText("");
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            BankingApp app = new BankingApp();
            app.setSize(500, 400);
            app.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
            app.setVisible(true);
        });
    }
}
