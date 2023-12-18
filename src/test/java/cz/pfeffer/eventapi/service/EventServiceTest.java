package cz.pfeffer.eventapi.service;

import cz.pfeffer.eventapi.entity.EventEntity;
import cz.pfeffer.eventapi.repository.EventRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.HashSet;
import java.util.Set;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class EventServiceTest {

    @InjectMocks
    EventService eventService;

    @Mock
    EventRepository eventRepository;

    @Test
    void getEventsByDocIdAndDateRange() {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

        //convert String to LocalDate
        LocalDate startDate = LocalDate.parse("2023-12-20", formatter);
        LocalDate endDate = LocalDate.parse("2023-12-21", formatter);

        Set<EventEntity> data = new HashSet<>();
        // docId will be handled in DB, therefore is not relevant in this test
        data.add(createEvent("CR10", "user1", "2023-12-20 07:58:22.241947+00"));
        data.add(createEvent("CR10", "user2", "2023-12-20 07:58:22.241947+00"));
        data.add(createEvent("CR10", "user3", "2023-12-21 07:58:22.241947+00"));
        data.add(createEvent("CR10", "user1", "2023-12-20 07:58:22.241947+00"));

        when(eventRepository.findByDocIdAndTimestampBetween(any(), any(), any())).thenReturn(data);
        eventService.getEventsByDocIdAndDateRange("CR10", startDate, endDate);
    }

    private EventEntity createEvent(String docId, String userId, String timestamp) {
        EventEntity event = new EventEntity();
        event.setDocId(docId);
        event.setUserId(userId);
        event.setTimestamp(timestamp);
        return event;
    }
}