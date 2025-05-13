package rh.Service;

import java.sql.SQLException;
import java.util.List;

public interface UserServiceInterface <entity>{

    public entity login(entity t) throws SQLException;
    public int addUser(entity t) throws SQLException;
    public List<entity> getAllusers() throws SQLException;
    public entity findById(int id) throws SQLException;
    public entity findByEmail(String email) throws SQLException;
    public entity modifierUser(entity t,String email) throws SQLException;
    public int supprimerUser(int id) throws SQLException;
    public int toggleUserStatus(int id) throws SQLException;
    public int changeUserRole(String role,int id) throws SQLException;
    public int admin() throws SQLException;

}
