package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ServiceRecordDAO {
    public void add(ServiceRecord s) throws SQLException {
        String sql = "INSERT INTO service_record(request_id,specialist_id,work_description,service_date,labor_hours,cost) VALUES(?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, s);
            p.executeUpdate();
        }
    }

    public List<ServiceRecord> getAll() throws SQLException {
        List<ServiceRecord> list = new ArrayList<>();
        String sql = "SELECT * FROM service_record ORDER BY id";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) list.add(map(r));
        }
        return list;
    }

    public ServiceRecord getById(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM service_record WHERE id=?")) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(ServiceRecord s) throws SQLException {
        String sql = "UPDATE service_record SET request_id=?,specialist_id=?,work_description=?,service_date=?,labor_hours=?,cost=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, s);
            p.setLong(7, s.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM service_record WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }

    private void bind(PreparedStatement p, ServiceRecord s) throws SQLException {
        p.setLong(1, s.getRequestId());
        p.setLong(2, s.getSpecialistId());
        p.setString(3, s.getWorkDescription());
        p.setString(4, s.getServiceDate());
        p.setDouble(5, s.getLaborHours());
        p.setDouble(6, s.getCost());
    }

    private ServiceRecord map(ResultSet r) throws SQLException {
        return new ServiceRecord(r.getLong("id"), r.getLong("request_id"), r.getLong("specialist_id"), r.getString("work_description"),
                r.getString("service_date"), r.getDouble("labor_hours"), r.getDouble("cost"));
    }
}
