package Services;

import Models.Formation;
import java.util.ArrayList;
import java.util.List;

public class FormationService {
    public FormationService() {
    }
    
    public List<Formation> getAllFormations() {
        return new ArrayList<>();
    }
    
    public Formation getFormationById(int id) {
        return new Formation();
    }
    
    public boolean addFormation(Formation formation) {
        return true;
    }
    
    public boolean updateFormation(Formation formation) {
        return true;
    }
    
    public boolean deleteFormation(int id) {
        return true;
    }
    
    public List<Formation> searchFormations(String query) {
        return new ArrayList<>();
    }
} 