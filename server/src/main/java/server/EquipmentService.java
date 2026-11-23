package server;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;
import java.util.logging.Logger;

public class EquipmentService {
    private static final Logger LOG = Logger.getLogger(EquipmentService.class.getName());
    private final EquipmentDAO dao = new EquipmentDAO();
    private final SpecialistDAO specialistDAO = new SpecialistDAO();

    public List<Equipment> getAll() throws SQLException { return dao.getAll(); }

    public void add(Equipment e) throws SQLException {
        check(e);
        dao.add(e);
        LOG.info("Додано обладнання");
    }

    public void update(Equipment e) throws SQLException {
        check(e);
        dao.update(e);
        LOG.info("Оновлено обладнання");
    }

    public void delete(long id) throws SQLException {
        try {
            dao.delete(id);
        } catch (SQLIntegrityConstraintViolationException e) {
            LOG.warning("Відхилено видалення обладнання " + id + ": існують пов'язані заявки");
            throw new IllegalArgumentException("Неможливо видалити обладнання: існують пов'язані заявки");
        }
        LOG.info("Видалено обладнання");
    }
    static void validate(Equipment e) {
        if (e == null || blank(e.getInventoryNumber()) || blank(e.getName()) || blank(e.getCategory()) || blank(e.getLaboratory())) {
            throw new IllegalArgumentException("Усі текстові поля обладнання є обов'язковими");
        }
        if (e.getCriticalityLevel() < 1 || e.getCriticalityLevel() > 5) throw new IllegalArgumentException("Рівень критичності має бути в діапазоні 1..5");
        if (e.getResponsibleSpecialistId() <= 0) throw new IllegalArgumentException("Некоректний ID фахівця");
    }

    private void check(Equipment e) throws SQLException {
        validate(e);
        if (specialistDAO.getById(e.getResponsibleSpecialistId()) == null) throw new IllegalArgumentException("Фахівця не знайдено");
        Equipment same = dao.getByInventoryNumber(e.getInventoryNumber());
        if (same != null && !same.getId().equals(e.getId())) throw new IllegalArgumentException("Інвентарний номер уже існує");
    }
    private static boolean blank(String v) { return v == null || v.isBlank(); }
}
