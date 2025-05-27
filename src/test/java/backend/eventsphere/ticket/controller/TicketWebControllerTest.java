package backend.eventsphere.ticket.controller;

import org.junit.jupiter.api.Test;
import org.springframework.ui.ConcurrentModel;

import static org.junit.jupiter.api.Assertions.*;

class TicketWebControllerTest {

    @Test
    void testViewTicketsForEvent() {
        TicketWebController controller = new TicketWebController();
        String eventId = "21dc2c27-bb7f-463b-b837-3c2d49b4597c";
        ConcurrentModel model = new ConcurrentModel();

        String viewName = controller.viewTicketsForEvent(eventId, model);

        assertEquals("ticket/tickets", viewName);
        assertTrue(model.containsAttribute("eventId"));
        assertEquals(eventId, model.getAttribute("eventId"));
    }
}