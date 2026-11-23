package server;

import java.sql.SQLException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.time.LocalDate;
import java.util.List;
import java.util.logging.Logger;

public class ServiceRequestService {
    private static final Logger LOG = Logger.getLogger(ServiceRequestService.class.getName());
    private final ServiceRequestDAO dao = new ServiceRequestDAO();
    private final EquipmentDAO equipmentDAO = new EquipmentDAO();

    public List<ServiceRequest> getAll() throws SQLException { return dao.getAll(); }

    public void add(ServiceRequest r) throws SQLException {
        validate(r);
        calculate(r);
        r.setState(RequestState.NEW);
        r.setCreatedDate(LocalDate.now().toString());
        dao.add(r);
        LOG.info("Створено заявку, I = " + r.getPriorityIndex());
    }

    public void update(ServiceRequest r) throws SQLException {
        validate(r);
        ServiceRequest old = dao.getById(r.getId());
        if (old == null) throw new IllegalArgumentException("Заявку не знайдено");
        calculate(r);
        r.setState(old.getState());
        r.setCreatedDate(old.getCreatedDate());
        dao.update(r);
        LOG.info("Оновлено заявку");
    }

    public void changeState(long id, RequestState next) throws SQLException {
        ServiceRequest current = dao.getById(id);
        if (current == null) throw new IllegalArgumentException("Заявку не знайдено");
        if (!isAllowedTransition(current.getState(), next)) {
            LOG.warning("Недопустимий перехід стану заявки " + id + ": " + current.getState() + " -> " + next);
            throw new IllegalArgumentException("Недопустимий перехід стану: " + current.getState() + " → " + next);
        }
        dao.updateState(id, next);
        LOG.info("Змінено стан заявки " + id + ": " + current.getState() + " -> " + next);
    }
    public void delete(long id) throws SQLException {
        try {
            dao.delete(id);
        } catch (SQLIntegrityConstraintViolationException e) {
            LOG.warning("Відхилено видалення заявки " + id + ": існують сервісні записи");
            throw new IllegalArgumentException("Неможливо видалити заявку: існують сервісні записи");
        }
        LOG.info("Видалено заявку");
    }
    public static boolean isAllowedTransition(RequestState from, RequestState to) {
        return (from == RequestState.NEW && to == RequestState.DIAGNOSED)
            || (from == RequestState.DIAGNOSED && to == RequestState.IN_SERVICE)
            || (from == RequestState.IN_SERVICE && to == RequestState.COMPLETED);
    }

    static void validate(ServiceRequest r) {
        if (r == null || r.getEquipmentId() <= 0 || r.getDescription() == null || r.getDescription().isBlank()) {
            throw new IllegalArgumentException("Некоректні основні дані заявки");
        }
        if (r.getFaultSeverity() < 1 || r.getFaultSeverity() > 5) throw new IllegalArgumentException("Тяжкість несправності має бути в діапазоні 1..5");
        if (!Double.isFinite(r.getExpectedDowntimeHours()) || r.getExpectedDowntimeHours() < 0) throw new IllegalArgumentException("Некоректна тривалість простою");
    }

    private void calculate(ServiceRequest r) throws SQLException {
        Equipment e = equipmentDAO.getById(r.getEquipmentId());
        if (e == null) throw new IllegalArgumentException("Обладнання не знайдено");
        double f1 = PriorityCalculator.factor1(e.getCriticalityLevel());
        double f2 = PriorityCalculator.factor2(r.getFaultSeverity());
        double f3 = PriorityCalculator.factor3(r.getExpectedDowntimeHours());
        r.setF1(f1);
        r.setF2(f2);
        r.setF3(f3);
        r.setPriorityIndex(PriorityCalculator.calculate(f1, f2, f3));
    }
}
