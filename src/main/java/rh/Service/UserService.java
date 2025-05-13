package rh.Service;

import rh.Models.User;
import rh.Utils.MyDataBase;
import org.mindrot.jbcrypt.BCrypt;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

/**
 * UserService class provides methods for user management, including login, registration,
 * password reset, and Google OAuth2 authentication.
 */
public class UserService implements UserServiceInterface<User> {
    private final Connection cnx;

    /**
     * Constructor to initialize the database connection.
     */
    public UserService() {
        cnx = MyDataBase.getInstance().getConnection();
    }

    @Override
    public User login(User t) throws SQLException {
        User u = this.findByEmail(t.getEmail());
        if (u == null)
            return null;
        else {
            if (BCrypt.checkpw(t.getPassword(), u.getPassword())) {
                return u;
            } else {
                u.setId(0); // Indicates wrong password
                return u;
            }
        }
    }

    public User loginWithGoogle(String email, String googleId) throws SQLException {
        String req = "SELECT * FROM users WHERE email = ? OR google_id = ?";
        try (PreparedStatement preparedStatement = cnx.prepareStatement(req)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, googleId);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setNom(resultSet.getString("nom"));
                    user.setPrenom(resultSet.getString("prenom"));
                    user.setPassword(resultSet.getString("password"));
                    user.setRole(User.Role.valueOf(resultSet.getString("role")));
                    user.setStatus(resultSet.getInt("status") == 1);
                    user.setEmail(resultSet.getString("email"));
                    return user;
                }
            }
        }
        return null;
    }

    public User registerWithGoogle(String email, String googleId, String nom, String prenom) throws SQLException {
        User existingUser = loginWithGoogle(email, googleId);
        if (existingUser != null) {
            return existingUser; // User already exists
        }

        String req = "INSERT INTO users (email, nom, prenom, role, status, google_id) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement preparedStatement = cnx.prepareStatement(req, Statement.RETURN_GENERATED_KEYS)) {
            preparedStatement.setString(1, email);
            preparedStatement.setString(2, nom);
            preparedStatement.setString(3, prenom);
            preparedStatement.setString(4, User.Role.Employe.name());
            preparedStatement.setInt(5, 0); // Status inactive by default
            preparedStatement.setString(6, googleId);
            int rows = preparedStatement.executeUpdate();
            if (rows > 0) {
                try (ResultSet generatedKeys = preparedStatement.getGeneratedKeys()) {
                    if (generatedKeys.next()) {
                        User user = new User();
                        user.setId(generatedKeys.getInt(1));
                        user.setEmail(email);
                        user.setNom(nom);
                        user.setPrenom(prenom);
                        user.setRole(User.Role.Employe);
                        user.setStatus(false);
                        return user;
                    }
                }
            }
        }
        return null;
    }

    @Override
    public int addUser(User t) throws SQLException {
        int statusValue = t.isStatus() ? 1 : 0;
        String hashedPassword = BCrypt.hashpw(t.getPassword(), BCrypt.gensalt());
        String req = "INSERT INTO users (email, password, nom, prenom, role, status) " +
                "VALUES (?, ?, ?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = cnx.prepareStatement(req)) {
            preparedStatement.setString(1, t.getEmail());
            preparedStatement.setString(2, hashedPassword);
            preparedStatement.setString(3, t.getNom());
            preparedStatement.setString(4, t.getPrenom());
            preparedStatement.setString(5, t.getRole().toString());
            preparedStatement.setInt(6, statusValue);
            int rows = preparedStatement.executeUpdate();
            return (rows > 0) ? 1 : 0;
        }
    }

    @Override
    public List<User> getAllusers() throws SQLException {
        List<User> users = new ArrayList<>();
        String req = "SELECT * FROM users WHERE role != ?";
        try (PreparedStatement preparedStatement = cnx.prepareStatement(req)) {
            preparedStatement.setString(1, "Admin");
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                while (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setNom(resultSet.getString("nom"));
                    user.setPrenom(resultSet.getString("prenom"));
                    user.setRole(User.Role.valueOf(resultSet.getString("role")));
                    user.setStatus(resultSet.getInt("status") == 1);
                    user.setEmail(resultSet.getString("email"));
                    users.add(user);
                }
            }
        }
        return users;
    }

    @Override
    public User findById(int id) throws SQLException {
        String req = "SELECT * FROM users WHERE id = ?";
        try (PreparedStatement preparedStatement = cnx.prepareStatement(req)) {
            preparedStatement.setInt(1, id);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setNom(resultSet.getString("nom"));
                    user.setPrenom(resultSet.getString("prenom"));
                    user.setRole(User.Role.valueOf(resultSet.getString("role")));
                    user.setStatus(resultSet.getInt("status") == 1);
                    user.setEmail(resultSet.getString("email"));
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public User findByEmail(String email) throws SQLException {
        String req = "SELECT * FROM users WHERE email = ?";
        try (PreparedStatement preparedStatement = cnx.prepareStatement(req)) {
            preparedStatement.setString(1, email);
            try (ResultSet resultSet = preparedStatement.executeQuery()) {
                if (resultSet.next()) {
                    User user = new User();
                    user.setId(resultSet.getInt("id"));
                    user.setNom(resultSet.getString("nom"));
                    user.setPrenom(resultSet.getString("prenom"));
                    user.setPassword(resultSet.getString("password"));
                    user.setRole(User.Role.valueOf(resultSet.getString("role")));
                    user.setStatus(resultSet.getInt("status") == 1);
                    user.setEmail(resultSet.getString("email"));
                    return user;
                }
            }
        }
        return null;
    }

    @Override
    public User modifierUser(User t, String originalEmail) throws SQLException {
        User existingUser = this.findByEmail(originalEmail);
        if (existingUser == null) {
            return null;
        }

        String passwordToUpdate = existingUser.getPassword();
        if (t.getPassword() != null && !t.getPassword().isEmpty() && !BCrypt.checkpw(t.getPassword(), existingUser.getPassword())) {
            passwordToUpdate = BCrypt.hashpw(t.getPassword(), BCrypt.gensalt());
        }

        String req = "UPDATE users SET nom = ?, prenom = ?, email = ?, password = ? WHERE email = ?";
        try (PreparedStatement preparedStatement = cnx.prepareStatement(req)) {
            preparedStatement.setString(1, t.getNom());
            preparedStatement.setString(2, t.getPrenom());
            preparedStatement.setString(3, t.getEmail());
            preparedStatement.setString(4, passwordToUpdate);
            preparedStatement.setString(5, originalEmail);
            int rowsUpdated = preparedStatement.executeUpdate();
            if (rowsUpdated > 0) {
                return findByEmail(t.getEmail());
            } else {
                return existingUser;
            }
        }
    }

    @Override
    public int supprimerUser(int id) throws SQLException {
        User u = this.findById(id);
        if (u == null) {
            return 0;
        }
        String reqDelete = "DELETE FROM users WHERE id = ?";
        try (PreparedStatement statement = cnx.prepareStatement(reqDelete)) {
            statement.setInt(1, id);
            int rowsDeleted = statement.executeUpdate();
            return (rowsDeleted > 0) ? 1 : 0;
        }
    }

    @Override
    public int toggleUserStatus(int id) throws SQLException {
        User u = this.findById(id);
        if (u == null) return 0;

        int newStatus = u.isStatus() ? 0 : 1;
        String req = "UPDATE users SET status = ? WHERE id = ?";
        try (PreparedStatement preparedStatement = cnx.prepareStatement(req)) {
            preparedStatement.setInt(1, newStatus);
            preparedStatement.setInt(2, id);
            int rowsUpdated = preparedStatement.executeUpdate();
            return (rowsUpdated > 0) ? 1 : 0;
        }
    }

    @Override
    public int changeUserRole(String role, int id) throws SQLException {
        User u = this.findById(id);
        if (u == null) {
            return 0;
        }
        try {
            User.Role newRole = User.Role.valueOf(role);
            String req = "UPDATE users SET role = ? WHERE id = ?";
            try (PreparedStatement preparedStatement = cnx.prepareStatement(req)) {
                preparedStatement.setString(1, newRole.name());
                preparedStatement.setInt(2, id);
                int rowsUpdated = preparedStatement.executeUpdate();
                return rowsUpdated > 0 ? 1 : 0;
            }
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid role: " + role + " for user ID: " + id);
            return 0;
        }
    }

    @Override
    public int admin() throws SQLException {
        String req = "SELECT COUNT(*) AS count FROM users WHERE role = ?";
        try (PreparedStatement statement = cnx.prepareStatement(req)) {
            statement.setString(1, "Admin");
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    return resultSet.getInt("count");
                }
            }
        }
        return 0;
    }

    public String generatePasswordResetToken(String email) throws SQLException {
        User user = findByEmail(email);
        if (user == null) {
            System.out.println("No user found with email: " + email);
            return null;
        }

        Random random = new Random();
        String token = String.format("%06d", random.nextInt(1000000));

        String deleteOldTokensSql = "DELETE FROM password_reset_tokens WHERE user_id = ?";
        try (PreparedStatement pstDelete = cnx.prepareStatement(deleteOldTokensSql)) {
            pstDelete.setInt(1, user.getId());
            pstDelete.executeUpdate();
        }

        String sql = "INSERT INTO password_reset_tokens (user_id, token, expiry_date) VALUES (?, ?, ?)";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setInt(1, user.getId());
            pst.setString(2, token);
            Timestamp expiryDate = new Timestamp(System.currentTimeMillis() + TimeUnit.HOURS.toMillis(1));
            pst.setTimestamp(3, expiryDate);

            int affectedRows = pst.executeUpdate();
            if (affectedRows > 0) {
                System.out.println("Token generated for user " + user.getId() + ": " + token);
                return token;
            } else {
                System.out.println("Failed to store token for user " + user.getId());
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception during token generation: " + e.getMessage());
            throw e;
        }
        return null;
    }

    public int verifyPasswordResetToken(String token) throws SQLException {
        String sql = "SELECT user_id, expiry_date FROM password_reset_tokens WHERE token = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, token);
            try (ResultSet rs = pst.executeQuery()) {
                if (rs.next()) {
                    Timestamp expiryDate = rs.getTimestamp("expiry_date");
                    if (expiryDate.after(new Timestamp(System.currentTimeMillis()))) {
                        return rs.getInt("user_id");
                    } else {
                        deleteToken(token);
                        return -2;
                    }
                }
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception during token verification: " + e.getMessage());
            throw e;
        }
        return -1;
    }

    public boolean resetPassword(int userId, String newPassword, String token) throws SQLException {
        String hashedPassword = BCrypt.hashpw(newPassword, BCrypt.gensalt());
        String sql = "UPDATE users SET password = ? WHERE id = ?";

        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, hashedPassword);
            pst.setInt(2, userId);
            int affectedRows = pst.executeUpdate();

            if (affectedRows > 0) {
                deleteToken(token);
                return true;
            }
        } catch (SQLException e) {
            System.err.println("SQL Exception during password reset: " + e.getMessage());
            throw e;
        }
        return false;
    }

    private void deleteToken(String token) throws SQLException {
        String sql = "DELETE FROM password_reset_tokens WHERE token = ?";
        try (PreparedStatement pst = cnx.prepareStatement(sql)) {
            pst.setString(1, token);
            pst.executeUpdate();
        } catch (SQLException e) {
            System.err.println("SQL Exception during token deletion: " + e.getMessage());
        }
    }
}