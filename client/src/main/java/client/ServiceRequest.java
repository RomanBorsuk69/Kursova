package client;

public class ServiceRequest {
    private Long id;
    private long equipmentId;
    private String description;
    private int faultSeverity;
    private double expectedDowntimeHours;
    private RequestState state;
    private double f1;
    private double f2;
    private double f3;
    private double priorityIndex;
    private String createdDate;

    public ServiceRequest() {}

    public ServiceRequest(Long id, long equipmentId, String description, int faultSeverity,
                          double expectedDowntimeHours, RequestState state, double f1, double f2,
                          double f3, double priorityIndex, String createdDate) {
        this.id = id;
        this.equipmentId = equipmentId;
        this.description = description;
        this.faultSeverity = faultSeverity;
        this.expectedDowntimeHours = expectedDowntimeHours;
        this.state = state;
        this.f1 = f1;
        this.f2 = f2;
        this.f3 = f3;
        this.priorityIndex = priorityIndex;
        this.createdDate = createdDate;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getEquipmentId() { return equipmentId; }
    public void setEquipmentId(long equipmentId) { this.equipmentId = equipmentId; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public int getFaultSeverity() { return faultSeverity; }
    public void setFaultSeverity(int faultSeverity) { this.faultSeverity = faultSeverity; }
    public double getExpectedDowntimeHours() { return expectedDowntimeHours; }
    public void setExpectedDowntimeHours(double expectedDowntimeHours) { this.expectedDowntimeHours = expectedDowntimeHours; }
    public RequestState getState() { return state; }
    public void setState(RequestState state) { this.state = state; }
    public double getF1() { return f1; }
    public void setF1(double f1) { this.f1 = f1; }
    public double getF2() { return f2; }
    public void setF2(double f2) { this.f2 = f2; }
    public double getF3() { return f3; }
    public void setF3(double f3) { this.f3 = f3; }
    public double getPriorityIndex() { return priorityIndex; }
    public void setPriorityIndex(double priorityIndex) { this.priorityIndex = priorityIndex; }
    public String getCreatedDate() { return createdDate; }
    public void setCreatedDate(String createdDate) { this.createdDate = createdDate; }
}
