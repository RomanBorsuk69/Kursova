package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRequestDAO {
    public void add(ServiceRequest s) throws SQLException {
        String sql = "INSERT INTO service_request(equipment_id,description,fault_severity,expected_downtime_hours,state,f1,f2,f3,priority_index,created_date) VALUES(?,?,?,?,?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, s);
            p.executeUpdate();
        }
    }

    public List<ServiceRequest> getAll() throws SQLException {
        List<ServiceRequest> list = new ArrayList<>();
        String sql = "SELECT * FROM service_request ORDER BY id";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) list.add(map(r));
        }
        return list;
    }
    public ServiceRequest getById(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM service_request WHERE id=?")) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }
    public void update(ServiceRequest s) throws SQLException {
        String sql = "UPDATE service_request SET equipment_id=?,description=?,fault_severity=?,expected_downtime_hours=?,state=?,f1=?,f2=?,f3=?,priority_index=?,created_date=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, s);
            p.setLong(11, s.getId());
            p.executeUpdate();
        }
    }

    public void updateState(long id, RequestState state) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement("UPDATE service_request SET state=? WHERE id=?")) {
            p.setString(1, state.name());
            p.setLong(2, id);
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM service_request WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, ServiceRequest s) throws SQLException {
        p.setLong(1, s.getEquipmentId());
        p.setString(2, s.getDescription());
        p.setInt(3, s.getFaultSeverity());
        p.setDouble(4, s.getExpectedDowntimeHours());
        p.setString(5, s.getState().name());
        p.setDouble(6, s.getF1());
        p.setDouble(7, s.getF2());
        p.setDouble(8, s.getF3());
        p.setDouble(9, s.getPriorityIndex());
        p.setString(10, s.getCreatedDate());
    }

    private ServiceRequest map(ResultSet r) throws SQLException {
        return new ServiceRequest(r.getLong("id"), r.getLong("equipment_id"), r.getString("description"), r.getInt("fault_severity"),
                r.getDouble("expected_downtime_hours"), RequestState.valueOf(r.getString("state")),
                r.getDouble("f1"), r.getDouble("f2"), r.getDouble("f3"), r.getDouble("priority_index"), r.getString("created_date"));
    }
}
