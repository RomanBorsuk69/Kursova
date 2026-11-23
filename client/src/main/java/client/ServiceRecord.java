package client;

public class ServiceRecord {
    private Long id;
    private long requestId;
    private long specialistId;
    private String workDescription;
    private String serviceDate;
    private double laborHours;
    private double cost;

    public ServiceRecord() {}

    public ServiceRecord(Long id, long requestId, long specialistId, String workDescription,
                         String serviceDate, double laborHours, double cost) {
        this.id = id;
        this.requestId = requestId;
        this.specialistId = specialistId;
        this.workDescription = workDescription;
        this.serviceDate = serviceDate;
        this.laborHours = laborHours;
        this.cost = cost;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public long getRequestId() { return requestId; }
    public void setRequestId(long requestId) { this.requestId = requestId; }
    public long getSpecialistId() { return specialistId; }
    public void setSpecialistId(long specialistId) { this.specialistId = specialistId; }
    public String getWorkDescription() { return workDescription; }
    public void setWorkDescription(String workDescription) { this.workDescription = workDescription; }
    public String getServiceDate() { return serviceDate; }
    public void setServiceDate(String serviceDate) { this.serviceDate = serviceDate; }
    public double getLaborHours() { return laborHours; }
    public void setLaborHours(double laborHours) { this.laborHours = laborHours; }
    public double getCost() { return cost; }
    public void setCost(double cost) { this.cost = cost; }
}
