package client;

import com.fasterxml.jackson.databind.ObjectMapper;
import java.net.URI;
import java.net.URLEncoder;
import java.net.http.*;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;

public class HttpLabClient {
    private static final String BASE="http://localhost:8080/lab-maintenance-server";
    private final HttpClient http=HttpClient.newHttpClient();
    private final ObjectMapper mapper=new ObjectMapper();

    public List<Specialist> getSpecialists() { return Arrays.asList(read(get("/specialists"),Specialist[].class)); }
    public void addSpecialist(Specialist s) { post("/specialists",specialistForm("add",s)); }
    public void updateSpecialist(Specialist s) { post("/specialists",specialistForm("update",s)+"&id="+s.getId()); }
    public void deleteSpecialist(long id) { post("/specialists","action=delete&id="+id); }

    public List<Equipment> getEquipment() { return Arrays.asList(read(get("/equipment"),Equipment[].class)); }
    public void addEquipment(Equipment e) { post("/equipment",equipmentForm("add",e)); }
    public void updateEquipment(Equipment e) { post("/equipment",equipmentForm("update",e)+"&id="+e.getId()); }
    public void deleteEquipment(long id) { post("/equipment","action=delete&id="+id); }

    public List<ServiceRequest> getRequests() { return Arrays.asList(read(get("/requests"),ServiceRequest[].class)); }
    public void addRequest(ServiceRequest r) { post("/requests",requestForm("add",r)); }
    public void updateRequest(ServiceRequest r) { post("/requests",requestForm("update",r)+"&id="+r.getId()); }
    public void changeRequestState(long id,RequestState state) { post("/requests","action=transition&id="+id+"&state="+state.name()); }
    public void deleteRequest(long id) { post("/requests","action=delete&id="+id); }

    public List<ServiceRecord> getRecords() { return Arrays.asList(read(get("/records"),ServiceRecord[].class)); }
    public void addRecord(ServiceRecord r) { post("/records",recordForm("add",r)); }
    public void updateRecord(ServiceRecord r) { post("/records",recordForm("update",r)+"&id="+r.getId()); }
    public void deleteRecord(long id) { post("/records","action=delete&id="+id); }

    private String specialistForm(String action,Specialist s) {
        return "action="+action+"&fullName="+enc(s.getFullName())+"&specialization="+enc(s.getSpecialization())+"&phone="+enc(s.getPhone());
    }
    private String equipmentForm(String action,Equipment e) {
        return "action="+action+"&inventoryNumber="+enc(e.getInventoryNumber())+"&name="+enc(e.getName())+
            "&category="+enc(e.getCategory())+"&laboratory="+enc(e.getLaboratory())+
            "&criticalityLevel="+e.getCriticalityLevel()+"&responsibleSpecialistId="+e.getResponsibleSpecialistId();
    }
    private String requestForm(String action,ServiceRequest r) {
        return "action="+action+"&equipmentId="+r.getEquipmentId()+"&description="+enc(r.getDescription())+
            "&faultSeverity="+r.getFaultSeverity()+"&expectedDowntimeHours="+r.getExpectedDowntimeHours();
    }
    private String recordForm(String action,ServiceRecord r) {
        return "action="+action+"&requestId="+r.getRequestId()+"&specialistId="+r.getSpecialistId()+
            "&workDescription="+enc(r.getWorkDescription())+"&serviceDate="+enc(r.getServiceDate())+
            "&laborHours="+r.getLaborHours()+"&cost="+r.getCost();
    }
    private String get(String path) {
        HttpRequest req=HttpRequest.newBuilder(URI.create(BASE+path)).GET().build(); return send(req);
    }
    private String post(String path,String form) {
        HttpRequest req=HttpRequest.newBuilder(URI.create(BASE+path))
            .header("Content-Type","application/x-www-form-urlencoded; charset=UTF-8")
            .POST(HttpRequest.BodyPublishers.ofString(form)).build(); return send(req);
    }
    /** HTTP 400 означає, що сервер відхилив дані; інші помилки перериваюють операцію. */
    private String send(HttpRequest req) {
        HttpResponse<String> r;
        try {
            r=http.send(req,HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch(Exception e) { throw new RuntimeException("Помилка HTTP: сервер недоступний",e); }
        if(r.statusCode()==400) throw new IllegalArgumentException(errorText(r.body()));
        if(r.statusCode()>400) throw new RuntimeException("Помилка HTTP: код "+r.statusCode());
        return r.body();
    }
    private String errorText(String json) {
        try { return mapper.readTree(json).path("error").asText(json); } catch(Exception e) { return json; }
    }
    private <T> T read(String json,Class<T> type) {
        try { return mapper.readValue(json,type); } catch(Exception e) { throw new RuntimeException("Помилка JSON",e); }
    }
    private String enc(String s) { return URLEncoder.encode(s==null?"":s,StandardCharsets.UTF_8); }
}
