package server;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class EquipmentDAO {
    public void add(Equipment e) throws SQLException {
        String sql = "INSERT INTO equipment(inventory_number,name,category,laboratory,criticality_level,responsible_specialist_id) VALUES(?,?,?,?,?,?)";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, e);
            p.executeUpdate();
        }
    }

    public List<Equipment> getAll() throws SQLException {
        List<Equipment> list = new ArrayList<>();
        String sql = "SELECT * FROM equipment ORDER BY id";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql); ResultSet r = p.executeQuery()) {
            while (r.next()) list.add(map(r));
        }
        return list;
    }

    public Equipment getById(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM equipment WHERE id=?")) {
            p.setLong(1, id);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public Equipment getByInventoryNumber(String inventoryNumber) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement("SELECT * FROM equipment WHERE inventory_number=?")) {
            p.setString(1, inventoryNumber);
            try (ResultSet r = p.executeQuery()) {
                return r.next() ? map(r) : null;
            }
        }
    }

    public void update(Equipment e) throws SQLException {
        String sql = "UPDATE equipment SET inventory_number=?,name=?,category=?,laboratory=?,criticality_level=?,responsible_specialist_id=? WHERE id=?";
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement(sql)) {
            bind(p, e);
            p.setLong(7, e.getId());
            p.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        try (Connection c = DBUtil.getConnection(); PreparedStatement p = c.prepareStatement("DELETE FROM equipment WHERE id=?")) {
            p.setLong(1, id);
            p.executeUpdate();
        }
    }
    private void bind(PreparedStatement p, Equipment e) throws SQLException {
        p.setString(1, e.getInventoryNumber());
        p.setString(2, e.getName());
        p.setString(3, e.getCategory());
        p.setString(4, e.getLaboratory());
        p.setInt(5, e.getCriticalityLevel());
        p.setLong(6, e.getResponsibleSpecialistId());
    }
    private Equipment map(ResultSet r) throws SQLException {
        return new Equipment(r.getLong("id"), r.getString("inventory_number"), r.getString("name"), r.getString("category"),
                r.getString("laboratory"), r.getInt("criticality_level"), r.getLong("responsible_specialist_id"));
    }
}
