package server;

import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.logging.Logger;

public class ServiceRecordService {
    private static final Logger LOG = Logger.getLogger(ServiceRecordService.class.getName());
    private final ServiceRecordDAO dao = new ServiceRecordDAO();
    private final ServiceRequestDAO requestDAO = new ServiceRequestDAO();
    private final SpecialistDAO specialistDAO = new SpecialistDAO();

    public List<ServiceRecord> getAll() throws SQLException { return dao.getAll(); }

    public void add(ServiceRecord r) throws SQLException {
        validate(r);
        requireRelated(r);
        dao.add(r);
        LOG.info("Додано сервісний запис");
    }

    public void update(ServiceRecord r) throws SQLException {
        validate(r);
        requireRelated(r);
        dao.update(r);
        LOG.info("Оновлено сервісний запис");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено сервісний запис");
    }

    static void validate(ServiceRecord r) {
        if (r == null || r.getRequestId() <= 0 || r.getSpecialistId() <= 0 || r.getWorkDescription() == null || r.getWorkDescription().isBlank()) {
            throw new IllegalArgumentException("Некоректні дані сервісного запису");
        }
        if (!Double.isFinite(r.getLaborHours()) || r.getLaborHours() < 0 || !Double.isFinite(r.getCost()) || r.getCost() < 0) {
            throw new IllegalArgumentException("Трудовитрати і вартість не можуть бути від'ємними");
        }
        try {
            LocalDate.parse(r.getServiceDate() == null ? "" : r.getServiceDate());
        } catch (DateTimeParseException e) {
            throw new IllegalArgumentException("Дата має бути у форматі YYYY-MM-DD");
        }
    }

    private void requireRelated(ServiceRecord r) throws SQLException {
        if (requestDAO.getById(r.getRequestId()) == null) throw new IllegalArgumentException("Заявку не знайдено");
        if (specialistDAO.getById(r.getSpecialistId()) == null) throw new IllegalArgumentException("Фахівця не знайдено");
    }
}
