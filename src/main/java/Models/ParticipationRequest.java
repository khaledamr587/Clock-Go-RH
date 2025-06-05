package Models;

public class ParticipationRequest {
    private int id;
    private int employeeId;
    private int formationId;
    private String status;
    
    public ParticipationRequest() {
    }
    
    public ParticipationRequest(int id, int employeeId, int formationId, String status) {
        this.id = id;
        this.employeeId = employeeId;
        this.formationId = formationId;
        this.status = status;
    }
    
    public int getId() {
        return id;
    }
    
    public void setId(int id) {
        this.id = id;
    }
    
    public int getEmployeeId() {
        return employeeId;
    }
    
    public void setEmployeeId(int employeeId) {
        this.employeeId = employeeId;
    }
    
    public int getFormationId() {
        return formationId;
    }
    
    public void setFormationId(int formationId) {
        this.formationId = formationId;
    }
    
    public String getStatus() {
        return status;
    }
    
    public void setStatus(String status) {
        this.status = status;
    }
    
    @Override
    public String toString() {
        return "ParticipationRequest{" +
                "id=" + id +
                ", employeeId=" + employeeId +
                ", formationId=" + formationId +
                ", status='" + status + '\'' +
                '}';
    }
} 