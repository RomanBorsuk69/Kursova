package server;

public class Equipment {
    private Long id;
    private String inventoryNumber;
    private String name;
    private String category;
    private String laboratory;
    private int criticalityLevel;
    private long responsibleSpecialistId;

    public Equipment() {}

    public Equipment(Long id, String inventoryNumber, String name, String category,
                     String laboratory, int criticalityLevel, long responsibleSpecialistId) {
        this.id = id;
        this.inventoryNumber = inventoryNumber;
        this.name = name;
        this.category = category;
        this.laboratory = laboratory;
        this.criticalityLevel = criticalityLevel;
        this.responsibleSpecialistId = responsibleSpecialistId;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getInventoryNumber() { return inventoryNumber; }
    public void setInventoryNumber(String inventoryNumber) { this.inventoryNumber = inventoryNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
    public String getLaboratory() { return laboratory; }
    public void setLaboratory(String laboratory) { this.laboratory = laboratory; }
    public int getCriticalityLevel() { return criticalityLevel; }
    public void setCriticalityLevel(int criticalityLevel) { this.criticalityLevel = criticalityLevel; }
    public long getResponsibleSpecialistId() { return responsibleSpecialistId; }
    public void setResponsibleSpecialistId(long responsibleSpecialistId) { this.responsibleSpecialistId = responsibleSpecialistId; }
}
