import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.sql.*;
import java.util.Random;

public class EntrevestApp20 extends JFrame {
    private JTextField searchDomainField, searchLocationField, minInvestmentField, usernameField, emailField,
            domainField, projectNameField, addressField, locationField, otpField, resetUsernameField, nameField,
            minInvestmentEntrepreneurField;
    private JTextArea projectDescriptionArea;
    private JPasswordField passwordField, resetPasswordField;
    private JTable resultTable, projectsTable;
    private JButton searchButton, uploadProjectButton, viewProjectsButton, logoutButton;
    private DefaultTableModel tableModel, projectsTableModel;
    private Connection connection;
    private String currentUserType;
    private int currentUserId, generatedOtp;
    private JPanel mainPanel, registerPanel, loginPanel, searchPanel, projectUploadPanel, manageProjectsPanel, resetPanel, deletePanel;

    public EntrevestApp20() {
        setSize(800, 600);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        getContentPane().setBackground(new Color(230, 230, 250));
        setLayout(new BorderLayout());

        connectToDatabase();

        JMenuBar menuBar = new JMenuBar();
        JMenu menu = new JMenu("Menu");
        JMenuItem registerItem = new JMenuItem("Register");
        JMenuItem loginItem = new JMenuItem("Login");
        JMenuItem resetItem = new JMenuItem("Reset Password");
        JMenuItem deleteItem = new JMenuItem("Delete Account");

        registerItem.addActionListener(e -> showRegisterPage());
        loginItem.addActionListener(e -> showLoginPage());
        resetItem.addActionListener(e -> showResetPasswordPage());
        deleteItem.addActionListener(e -> showDeleteAccountPage());

        menu.add(registerItem);
        menu.add(loginItem);
        menu.add(resetItem);
        menu.add(deleteItem);
        menuBar.add(menu);
        setJMenuBar(menuBar);

        mainPanel = new JPanel();
        mainPanel.setLayout(new BoxLayout(mainPanel, BoxLayout.Y_AXIS));
        JLabel welcomeLabel = new JLabel("Welcome to our EntreVest Platform!!");
        welcomeLabel.setFont(new Font("Arial", Font.BOLD, 28));
        welcomeLabel.setForeground(new Color(72, 61, 139));
        welcomeLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
        welcomeLabel.setBorder(BorderFactory.createEmptyBorder(50, 0, 20, 0));

        JButton exitButton = new JButton("Exit");
        exitButton.setAlignmentX(Component.CENTER_ALIGNMENT);
        exitButton.addActionListener(e -> System.exit(0));

        mainPanel.add(welcomeLabel);
        mainPanel.add(Box.createVerticalStrut(20));
        mainPanel.add(exitButton);

        add(mainPanel, BorderLayout.CENTER);
        setVisible(true);
    }

    private void connectToDatabase() {
        try {
            connection = DriverManager.getConnection("jdbc:postgresql://localhost:5432/project", "postgres", "Malesh2006#");
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Database connection failed!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showRegisterPage() {
        remove(mainPanel);
        registerPanel = new JPanel();
        registerPanel.setLayout(null);
        createRegisterForm(registerPanel);
        add(registerPanel);
        revalidate();
        repaint();
    }

    private void createRegisterForm(JPanel registerPanel) {
        JLabel nameLabel = new JLabel("Name:");
        nameLabel.setBounds(20, 20, 100, 30);
        registerPanel.add(nameLabel);

        nameField = new JTextField();
        nameField.setBounds(140, 20, 200, 30);
        registerPanel.add(nameField);

        JLabel emailLabel = new JLabel("Email:");
        emailLabel.setBounds(20, 70, 100, 30);
        registerPanel.add(emailLabel);

        emailField = new JTextField();
        emailField.setBounds(140, 70, 200, 30);
        registerPanel.add(emailField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(20, 120, 100, 30);
        registerPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(140, 120, 200, 30);
        registerPanel.add(passwordField);

        JLabel domainLabel = new JLabel("Domain:");
        domainLabel.setBounds(20, 170, 100, 30);
        registerPanel.add(domainLabel);

        domainField = new JTextField();
        domainField.setBounds(140, 170, 200, 30);
        registerPanel.add(domainField);

        JLabel typeLabel = new JLabel("Type:");
        typeLabel.setBounds(20, 220, 100, 30);
        registerPanel.add(typeLabel);

        JComboBox<String> typeComboBox = new JComboBox<>(new String[]{"Investor", "Entrepreneur"});
        typeComboBox.setBounds(140, 220, 200, 30);
        registerPanel.add(typeComboBox);

        JLabel addressLabel = new JLabel("Address:");
        addressLabel.setBounds(20, 270, 100, 30);
        registerPanel.add(addressLabel);

        addressField = new JTextField();
        addressField.setBounds(140, 270, 200, 30);
        registerPanel.add(addressField);

        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setBounds(20, 320, 100, 30);
        registerPanel.add(locationLabel);

        locationField = new JTextField();
        locationField.setBounds(140, 320, 200, 30);
        registerPanel.add(locationField);

        JLabel minInvestmentLabel = new JLabel("Min Investment:");
        minInvestmentLabel.setBounds(20, 370, 130, 30);
        registerPanel.add(minInvestmentLabel);

        minInvestmentField = new JTextField();
        minInvestmentField.setBounds(140, 370, 200, 30);
        registerPanel.add(minInvestmentField);

        otpField = new JTextField();
        otpField.setBounds(140, 420, 100, 30);
        registerPanel.add(otpField);

        JButton verifyOtpButton = new JButton("Verify OTP");
        verifyOtpButton.setBounds(260, 420, 120, 30);
        registerPanel.add(verifyOtpButton);

        JButton registerButton = new JButton("Register");
        registerButton.setBounds(140, 470, 100, 30);
        registerPanel.add(registerButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(260, 470, 100, 30);
        registerPanel.add(backButton);

        registerButton.addActionListener(e -> {
            String userType = String.valueOf(typeComboBox.getSelectedItem());
            registerUser(userType);
        });

        verifyOtpButton.addActionListener(e -> {
            int enteredOtp;
            try {
                enteredOtp = Integer.parseInt(otpField.getText());
            } catch (NumberFormatException ex) {
                JOptionPane.showMessageDialog(registerPanel, "Invalid OTP format!", "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }

            if (enteredOtp == generatedOtp) {
                JOptionPane.showMessageDialog(registerPanel, "OTP Verified!");
                proceedWithRegistration(String.valueOf(typeComboBox.getSelectedItem()));
            } else {
                JOptionPane.showMessageDialog(registerPanel, "Invalid OTP!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        });

        backButton.addActionListener(e -> {
            remove(registerPanel);
            add(mainPanel);
            revalidate();
            repaint();
        });
    }

    private void registerUser(String userType) {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String domain = domainField.getText();
        String address = addressField.getText();
        String location = locationField.getText();
        String minInvestment = minInvestmentField.getText();

        if (name.isEmpty() || email.isEmpty() || password.isEmpty() || domain.isEmpty() || address.isEmpty() || location.isEmpty() || (userType.equals("Entrepreneur") && minInvestment.isEmpty())) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        generatedOtp = generateOtp();
        JOptionPane.showMessageDialog(this, "Your OTP: " + generatedOtp + "\nPlease enter the OTP in the text field provided.", "OTP", JOptionPane.INFORMATION_MESSAGE);
    }

    private void proceedWithRegistration(String userType) {
        String name = nameField.getText();
        String email = emailField.getText();
        String password = new String(passwordField.getPassword());
        String domain = domainField.getText();
        String address = addressField.getText();
        String location = locationField.getText();
        String minInvestment = minInvestmentField.getText();
        String username = generateUniqueUsername(userType);

        String userSql = "INSERT INTO users (name, email, password, domain, type, address, location) VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement userPs = connection.prepareStatement(userSql, Statement.RETURN_GENERATED_KEYS)) {
            userPs.setString(1, username);
            userPs.setString(2, email);
            userPs.setString(3, password);
            userPs.setString(4, domain);
            userPs.setString(5, userType);
            userPs.setString(6, address);
            userPs.setString(7, location);
            userPs.executeUpdate();

            ResultSet generatedKeys = userPs.getGeneratedKeys();
            if (generatedKeys.next()) {
                int userId = generatedKeys.getInt(1);
                if (userType.equals("Entrepreneur")) {
                    String entrepreneurSql = "INSERT INTO entrepreneurs (user_id, min_investment) VALUES (?, ?)";
                    try (PreparedStatement entrepreneurPs = connection.prepareStatement(entrepreneurSql)) {
                        entrepreneurPs.setInt(1, userId);
                        entrepreneurPs.setDouble(2, Double.parseDouble(minInvestment));
                        entrepreneurPs.executeUpdate();
                    }
                } else if (userType.equals("Investor")) {
                    String investorSql = "INSERT INTO investors (user_id) VALUES (?)";
                    try (PreparedStatement investorPs = connection.prepareStatement(investorSql)) {
                        investorPs.setInt(1, userId);
                        investorPs.executeUpdate();
                    }
                }
            }

            JOptionPane.showMessageDialog(this, "Registration successful! Your username is: " + username);
            remove(registerPanel);
            add(mainPanel);
            revalidate();
            repaint();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Registration failed!", "Error", JOptionPane.ERROR_MESSAGE);
            ex.printStackTrace();
        }
    }

    private String generateUniqueUsername(String userType) {
        Random random = new Random();
        String userTypeString = userType.equals("Investor") ? "INV" : "ENT";
        int randomNum = 1000 + random.nextInt(9000);
        return userTypeString + randomNum + random.nextInt(100);
    }

    private int generateOtp() {
        Random random = new Random();
        return 100000 + random.nextInt(900000);
    }

    private void showLoginPage() {
        remove(mainPanel);
        loginPanel = new JPanel();
        loginPanel.setLayout(null);
        createLoginForm(loginPanel);
        add(loginPanel);
        revalidate();
        repaint();
    }

    private void createLoginForm(JPanel loginPanel) {
        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(20, 20, 100, 30);
        loginPanel.add(usernameLabel);

        usernameField = new JTextField();
        usernameField.setBounds(140, 20, 200, 30);
        loginPanel.add(usernameField);

        JLabel passwordLabel = new JLabel("Password:");
        passwordLabel.setBounds(20, 70, 100, 30);
        loginPanel.add(passwordLabel);

        passwordField = new JPasswordField();
        passwordField.setBounds(140, 70, 200, 30);
        loginPanel.add(passwordField);

        JButton loginButton = new JButton("Login");
        loginButton.setBounds(140, 130, 100, 30);
        loginPanel.add(loginButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(260, 130, 100, 30);
        loginPanel.add(backButton);

        loginButton.addActionListener(e -> loginUser());

        backButton.addActionListener(e -> {
            remove(loginPanel);
            add(mainPanel);
            revalidate();
            repaint();
        });
    }

    private void loginUser() {
        String username = usernameField.getText();
        String password = new String(passwordField.getPassword());

        if (username.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "SELECT id, type FROM users WHERE name = ? AND password = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            ps.setString(2, password);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                currentUserId = rs.getInt("id");
                currentUserType = rs.getString("type");
                JOptionPane.showMessageDialog(this, "Login successful as " + currentUserType + "!");

                checkForNotifications(currentUserId);

                remove(loginPanel);
                showMainInterface();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid username or password.", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Login failed!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void checkForNotifications(int userId) {
        String sql = "SELECT message FROM notifications WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            StringBuilder notifications = new StringBuilder();
            while (rs.next()) {
                notifications.append(rs.getString("message")).append("\n");
            }

            if (notifications.length() > 0) {
                JOptionPane.showMessageDialog(this, "Notifications:\n" + notifications.toString(), "Notifications", JOptionPane.INFORMATION_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve notifications!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showMainInterface() {
        searchPanel = new JPanel();
        searchPanel.setLayout(null);

        JLabel searchLabel = new JLabel("Search Investors/Entrepreneurs:");
        searchLabel.setBounds(20, 20, 250, 30);
        searchPanel.add(searchLabel);

        JLabel domainLabel = new JLabel("Domain:");
        domainLabel.setBounds(20, 60, 100, 30);
        searchPanel.add(domainLabel);

        searchDomainField = new JTextField();
        searchDomainField.setBounds(120, 60, 200, 30);
        searchPanel.add(searchDomainField);

        JLabel locationLabel = new JLabel("Location:");
        locationLabel.setBounds(340, 60, 100, 30);
        searchPanel.add(locationLabel);

        searchLocationField = new JTextField();
        searchLocationField.setBounds(440, 60, 200, 30);
        searchPanel.add(searchLocationField);

        JLabel minInvestmentLabel = new JLabel("Min Investment:");
        minInvestmentLabel.setBounds(660, 60, 120, 30);
        searchPanel.add(minInvestmentLabel);

        minInvestmentField = new JTextField();
        minInvestmentField.setBounds(780, 60, 80, 30);
        searchPanel.add(minInvestmentField);

        searchButton = new JButton("Search");
        searchButton.setBounds(670, 20, 100, 30);
        searchPanel.add(searchButton);

        String[] columnNames = {"ID", "Name", "Type", "Domain", "Location", "Min Investment"};
        tableModel = new DefaultTableModel(columnNames, 0);
        resultTable = new JTable(tableModel);
        JScrollPane scrollPane = new JScrollPane(resultTable);
        scrollPane.setBounds(20, 100, 740, 200);
        searchPanel.add(scrollPane);

        searchButton.addActionListener(e -> searchUsers());

        resultTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                if (currentUserType.equals("Entrepreneur")) {
                    showInvestorDetails(evt);
                }
            }
        });

        if (currentUserType.equals("Investor")) {
            viewProjectsButton = new JButton("View Projects");
            viewProjectsButton.setBounds(20, 320, 150, 30);
            viewProjectsButton.addActionListener(e -> viewProjectsForSelectedEntrepreneur());
            searchPanel.add(viewProjectsButton);
        }

        if (currentUserType.equals("Entrepreneur")) {
            uploadProjectButton = new JButton("Upload Project");
            uploadProjectButton.setBounds(200, 320, 150, 30);
            uploadProjectButton.addActionListener(e -> showUploadProjectPage());
            searchPanel.add(uploadProjectButton);
        }

        if (currentUserType.equals("Investor")) {
            JButton manageProjectsButton = new JButton("Manage Projects");
            manageProjectsButton.setBounds(400, 320, 150, 30);
            manageProjectsButton.addActionListener(e -> showManageProjectsPage());
            searchPanel.add(manageProjectsButton);
        }

        logoutButton = new JButton("Logout");
        logoutButton.setBounds(600, 320, 150, 30);
        logoutButton.addActionListener(e -> logout());
        searchPanel.add(logoutButton);

        add(searchPanel);
        revalidate();
        repaint();
    }

    private void logout() {
        currentUserId = -1;
        currentUserType = null;
        remove(searchPanel);
        add(mainPanel);
        revalidate();
        repaint();
        JOptionPane.showMessageDialog(this, "You have logged out successfully.", "Logout", JOptionPane.INFORMATION_MESSAGE);
    }

    private void searchUsers() {
        String domain = searchDomainField.getText();
        String location = searchLocationField.getText();
        Double minInvestment = null;

        String minInvestmentText = minInvestmentField.getText();
        if (!minInvestmentText.isEmpty()) {
            minInvestment = Double.parseDouble(minInvestmentText);
        }

        StringBuilder sqlBuilder = new StringBuilder("SELECT u.id, u.name, u.type, u.domain, u.location, e.min_investment FROM users u LEFT JOIN entrepreneurs e ON u.id = e.user_id WHERE 1=1");
        if (!domain.isEmpty()) {
            sqlBuilder.append(" AND u.domain ILIKE ?");
        }
        if (!location.isEmpty()) {
            sqlBuilder.append(" AND u.location ILIKE ?");
        }
        if (minInvestment != null) {
            sqlBuilder.append(" AND e.min_investment >= ?");
        }

        try (PreparedStatement ps = connection.prepareStatement(sqlBuilder.toString())) {
            int index = 1;
            if (!domain.isEmpty()) {
                ps.setString(index++, "%" + domain + "%");
            }
            if (!location.isEmpty()) {
                ps.setString(index++, "%" + location + "%");
            }
            if (minInvestment != null) {
                ps.setDouble(index++, minInvestment);
            }

            ResultSet rs = ps.executeQuery();
            tableModel.setRowCount(0);
            while (rs.next()) {
                int id = rs.getInt("id");
                String name = rs.getString("name");
                String type = rs.getString("type");
                String domainResult = rs.getString("domain");
                String locationResult = rs.getString("location");
                double minInvestmentResult = rs.getDouble("min_investment");
                tableModel.addRow(new Object[]{id, name, type, domainResult, locationResult, minInvestmentResult});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Search failed!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showInvestorDetails(java.awt.event.MouseEvent evt) {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow == -1) {
            return;
        }

        int investorId = (int) tableModel.getValueAt(selectedRow, 0);
        String sql = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, investorId);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                JFrame detailFrame = new JFrame("Investor Details");
                detailFrame.setSize(400, 400);
                detailFrame.setLayout(new GridLayout(8, 2));

                detailFrame.add(new JLabel("Username:"));
                detailFrame.add(new JLabel(rs.getString("name")));

                detailFrame.add(new JLabel("Email:"));
                detailFrame.add(new JLabel(rs.getString("email")));

                detailFrame.add(new JLabel("Domain:"));
                detailFrame.add(new JLabel(rs.getString("domain")));

                detailFrame.add(new JLabel("Type:"));
                detailFrame.add(new JLabel(rs.getString("type")));

                detailFrame.add(new JLabel("Address:"));
                detailFrame.add(new JLabel(rs.getString("address")));

                detailFrame.add(new JLabel("Location:"));
                detailFrame.add(new JLabel(rs.getString("location")));

                if (rs.getString("type").equals("Entrepreneur")) {
                    detailFrame.add(new JLabel("Min Investment:"));
                    detailFrame.add(new JLabel(String.valueOf(rs.getDouble("min_investment"))));
                }

                JButton backButton = new JButton("Back");
                backButton.addActionListener(e -> detailFrame.dispose());
                detailFrame.add(backButton, BorderLayout.SOUTH);

                detailFrame.setVisible(true);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve investor details!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showUploadProjectPage() {
        remove(searchPanel);
        projectUploadPanel = new JPanel();
        projectUploadPanel.setLayout(null);

        JLabel projectNameLabel = new JLabel("Project Name:");
        projectNameLabel.setBounds(20, 20, 100, 30);
        projectUploadPanel.add(projectNameLabel);

        projectNameField = new JTextField();
        projectNameField.setBounds(140, 20, 200, 30);
        projectUploadPanel.add(projectNameField);

        JLabel projectDescriptionLabel = new JLabel("Description:");
        projectDescriptionLabel.setBounds(20, 70, 100, 30);
        projectUploadPanel.add(projectDescriptionLabel);

        projectDescriptionArea = new JTextArea();
        projectDescriptionArea.setBounds(140, 70, 200, 100);
        projectUploadPanel.add(projectDescriptionArea);

        JButton uploadButton = new JButton("Upload");
        uploadButton.setBounds(140, 190, 100, 30);
        projectUploadPanel.add(uploadButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(260, 190, 100, 30);
        projectUploadPanel.add(backButton);

        uploadButton.addActionListener(e -> uploadProject());

        backButton.addActionListener(e -> {
            remove(projectUploadPanel);
            showMainInterface();
        });

        add(projectUploadPanel);
        revalidate();
        repaint();
    }

    private void uploadProject() {
        String projectName = projectNameField.getText();
        String projectDescription = projectDescriptionArea.getText();

        if (projectName.isEmpty() || projectDescription.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill all fields.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String sql = "INSERT INTO projects (user_id, name, description, status) VALUES (?, ?, ?, 'Pending')";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, currentUserId);
            ps.setString(2, projectName);
            ps.setString(3, projectDescription);
            ps.executeUpdate();
            JOptionPane.showMessageDialog(this, "Project uploaded successfully!");
            remove(projectUploadPanel);
            showMainInterface();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Upload failed!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showResetPasswordPage() {
        remove(mainPanel);
        resetPanel = new JPanel();
        resetPanel.setLayout(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(20, 20, 100, 30);
        resetPanel.add(usernameLabel);

        resetUsernameField = new JTextField();
        resetUsernameField.setBounds(140, 20, 200, 30);
        resetPanel.add(resetUsernameField);

        JLabel newPasswordLabel = new JLabel("New Password:");
        newPasswordLabel.setBounds(20, 70, 100, 30);
        resetPanel.add(newPasswordLabel);

        resetPasswordField = new JPasswordField();
        resetPasswordField.setBounds(140, 70, 200, 30);
        resetPanel.add(resetPasswordField);

        JButton resetButton = new JButton("Reset");
        resetButton.setBounds(140, 120, 100, 30);
        resetPanel.add(resetButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(260, 120, 100, 30);
        resetPanel.add(backButton);

        resetButton.addActionListener(e -> resetPassword(resetUsernameField.getText()));

        backButton.addActionListener(e -> {
            remove(resetPanel);
            add(mainPanel);
            revalidate();
            repaint();
        });

        add(resetPanel);
        revalidate();
        repaint();
    }

    private void resetPassword(String username) {
        String newPassword = new String(resetPasswordField.getPassword());
        if (newPassword.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter a new password.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        String sql = "UPDATE users SET password = ? WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, newPassword);
            ps.setString(2, username);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Password reset successfully!");
            } else {
                JOptionPane.showMessageDialog(this, "Username not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Reset failed!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showDeleteAccountPage() {
        remove(mainPanel);
        deletePanel = new JPanel();
        deletePanel.setLayout(null);

        JLabel usernameLabel = new JLabel("Username:");
        usernameLabel.setBounds(20, 20, 100, 30);
        deletePanel.add(usernameLabel);

        JTextField deleteUsernameField = new JTextField();
        deleteUsernameField.setBounds(140, 20, 200, 30);
        deletePanel.add(deleteUsernameField);

        JButton deleteButton = new JButton("Delete");
        deleteButton.setBounds(140, 70, 100, 30);
        deletePanel.add(deleteButton);

        JButton backButton = new JButton("Back");
        backButton.setBounds(260, 70, 100, 30);
        deletePanel.add(backButton);

        deleteButton.addActionListener(e -> deleteAccount(deleteUsernameField.getText()));

        backButton.addActionListener(e -> {
            remove(deletePanel);
            add(mainPanel);
            revalidate();
            repaint();
        });

        add(deletePanel);
        revalidate();
        repaint();
    }

    private void deleteAccount(String username) {
        String sql = "DELETE FROM users WHERE name = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, username);
            int rowsAffected = ps.executeUpdate();
            if (rowsAffected > 0) {
                JOptionPane.showMessageDialog(this, "Account deleted successfully.");
            } else {
                JOptionPane.showMessageDialog(this, "Username not found!", "Error", JOptionPane.ERROR_MESSAGE);
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to delete account.", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void viewProjectsForSelectedEntrepreneur() {
        int selectedRow = resultTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select an entrepreneur from the list.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        int entrepreneurId = (int) tableModel.getValueAt(selectedRow, 0);
        String sql = "SELECT name, description, status FROM projects WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, entrepreneurId);
            ResultSet rs = ps.executeQuery();

            JFrame projectsFrame = new JFrame("Projects");
            projectsFrame.setSize(400, 400);
            projectsFrame.setLayout(new BorderLayout());

            DefaultTableModel projectsTableModel = new DefaultTableModel(new String[]{"Project Name", "Description", "Status"}, 0);
            JTable projectsTable = new JTable(projectsTableModel);
            JScrollPane scrollPane = new JScrollPane(projectsTable);
            projectsFrame.add(scrollPane, BorderLayout.CENTER);

            while (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                String status = rs.getString("status");
                projectsTableModel.addRow(new Object[]{name, description, status});
            }

            JButton backButton = new JButton("Back");
            backButton.addActionListener(e -> projectsFrame.dispose());
            projectsFrame.add(backButton, BorderLayout.SOUTH);

            projectsFrame.setVisible(true);
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve projects!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void showManageProjectsPage() {
        remove(searchPanel);
        manageProjectsPanel = new JPanel();
        manageProjectsPanel.setLayout(new BorderLayout());

        projectsTableModel = new DefaultTableModel(new String[]{"Project Name", "Description", "Status", "User ID"}, 0);
        projectsTable = new JTable(projectsTableModel);
        JScrollPane scrollPane = new JScrollPane(projectsTable);
        manageProjectsPanel.add(scrollPane, BorderLayout.CENTER);

        JButton refreshButton = new JButton("Refresh");
        JButton approveProjectButton = new JButton("Approve Project");
        JButton disapproveProjectButton = new JButton("Disapprove Project");
        JButton backButton = new JButton("Back");

        approveProjectButton.addActionListener(e -> updateProjectStatus("Approved"));
        disapproveProjectButton.addActionListener(e -> updateProjectStatus("Disapproved"));
        backButton.addActionListener(e -> {
            remove(manageProjectsPanel);
            add(searchPanel);
            revalidate();
            repaint();
        });

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(refreshButton);
        buttonPanel.add(approveProjectButton);
        buttonPanel.add(disapproveProjectButton);
        buttonPanel.add(backButton);
        manageProjectsPanel.add(buttonPanel, BorderLayout.SOUTH);

        refreshButton.addActionListener(e -> reloadProjects());

        reloadProjects();

        add(manageProjectsPanel);
        revalidate();
        repaint();
    }

    private void reloadProjects() {
        String sql = "SELECT p.name, p.description, p.status, p.user_id FROM projects p JOIN users u ON p.user_id = u.id WHERE u.type = 'Entrepreneur'";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ResultSet rs = ps.executeQuery();
            projectsTableModel.setRowCount(0);
            while (rs.next()) {
                String name = rs.getString("name");
                String description = rs.getString("description");
                String status = rs.getString("status");
                int entrepreneurId = rs.getInt("user_id");
                projectsTableModel.addRow(new Object[]{name, description, status, entrepreneurId});
            }
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to retrieve projects!", "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    private void updateProjectStatus(String status) {
        int selectedRow = projectsTable.getSelectedRow();
        if (selectedRow == -1) {
            JOptionPane.showMessageDialog(this, "Please select a project to update.", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String projectName = (String) projectsTableModel.getValueAt(selectedRow, 0);
        int entrepreneurId = (int) projectsTableModel.getValueAt(selectedRow, 3);
        String investorName = usernameField.getText();
        String sql = "UPDATE projects SET status = ? WHERE name = ? AND user_id = ?";

        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, status);
            ps.setString(2, projectName);
            ps.setInt(3, entrepreneurId);
            int rowsUpdated = ps.executeUpdate();

            if (rowsUpdated > 0) {
                String notificationSql = "INSERT INTO notifications (user_id, message) VALUES (?, ?)";
                try (PreparedStatement notificationPs = connection.prepareStatement(notificationSql)) {
                    notificationPs.setInt(1, entrepreneurId);
                    if (status.equals("Approved")) {
                        notificationPs.setString(2, "Your project \"" + projectName + "\" was approved by investor \"" + investorName + "\".");
                    } else {
                        notificationPs.setString(2, "Your project \"" + projectName + "\" was disapproved by investor \"" + investorName + "\".");
                    }
                    notificationPs.executeUpdate();
                }

                JOptionPane.showMessageDialog(this, "Project status updated successfully.", "Project Status Update", JOptionPane.INFORMATION_MESSAGE);
            } else {
                JOptionPane.showMessageDialog(this, "Project status update failed. No matching project found.", "Error", JOptionPane.ERROR_MESSAGE);
            }
            reloadProjects();
        } catch (SQLException e) {
            JOptionPane.showMessageDialog(this, "Failed to update project status: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            EntrevestApp20 app = new EntrevestApp20();
            app.setLocationRelativeTo(null);
            app.setVisible(true);
        });
    }
}
