package Services;

import Models.ParticipationRequest;
import java.util.ArrayList;
import java.util.List;

public class ParticipationService {
    public ParticipationService() {
    }
    
    public List<ParticipationRequest> getAllParticipationRequests() {
        return new ArrayList<>();
    }
    
    public ParticipationRequest getParticipationRequestById(int id) {
        return new ParticipationRequest();
    }
    
    public List<ParticipationRequest> getParticipationRequestsByEmployeeId(int employeeId) {
        return new ArrayList<>();
    }
    
    public boolean addParticipationRequest(ParticipationRequest request) {
        return true;
    }
    
    public boolean updateParticipationRequest(ParticipationRequest request) {
        return true;
    }
    
    public boolean deleteParticipationRequest(int id) {
        return true;
    }
} 