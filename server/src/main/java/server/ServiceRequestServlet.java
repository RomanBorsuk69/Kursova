package server;
import com.fasterxml.jackson.databind.ObjectMapper;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.Map;
public class ServiceRequestServlet extends HttpServlet {
    private final ServiceRequestService service = new ServiceRequestService();
    private final ObjectMapper mapper = new ObjectMapper();
    @Override
    protected void doGet(HttpServletRequest req,
                         HttpServletResponse resp) throws IOException {
        try {
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }
    @Override
    protected void doPost(HttpServletRequest req,
                          HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String action = req.getParameter("action");
            if ("add".equals(action)) {
                service.add(read(null, req));
            } else if ("update".equals(action)) {
                service.update(read(id(req), req));
            } else if ("transition".equals(action)) {
                String stateValue = req.getParameter("state");
                if (stateValue == null || stateValue.isBlank()) {
                    throw new IllegalArgumentException(
                        "Не задано стан заявки"
                    );
                }
                service.changeState(
                    id(req),
                    RequestState.valueOf(stateValue)
                );
            } else if ("delete".equals(action)) {
                service.delete(id(req));
            } else {
                throw new IllegalArgumentException("Невідома операція");
            }
            writeJson(resp, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }
    private ServiceRequest read(Long id, HttpServletRequest req) {
        return new ServiceRequest(
            id,
            Long.parseLong(req.getParameter("equipmentId")),
            req.getParameter("description"),
            Integer.parseInt(req.getParameter("faultSeverity")),
            Double.parseDouble(req.getParameter("expectedDowntimeHours")),
            null, 0, 0, 0, 0, null
        );
    }
    private void writeJson(HttpServletResponse resp,
                           Object data) throws IOException {
        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }
    private void writeError(HttpServletResponse resp,
                            Exception e) throws IOException {
        boolean clientError = e instanceof IllegalArgumentException;
        int status = clientError
            ? HttpServletResponse.SC_BAD_REQUEST
            : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String message = clientError
            ? (e.getMessage() == null
                ? "Некоректний запит"
                : e.getMessage())
            : "Внутрішня помилка сервера";
        resp.setStatus(status);
        writeJson(resp, Map.of("error", message));
    }
    private long id(HttpServletRequest req) {
        return Long.parseLong(req.getParameter("id"));
    }
}
