package cz.pfeffer.eventapi.service;

import cz.pfeffer.eventapi.domain.UserCountResponse;
import cz.pfeffer.eventapi.entity.EventEntity;
import cz.pfeffer.eventapi.repository.EventRepository;
import jakarta.transaction.Transactional;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVRecord;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.IOException;
import java.sql.Date;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Service
public class EventService {

    private static final Pattern pattern = Pattern.compile("^/legislativa/(CR[0-9]+).*");
    public static final String[] HEADERS = {"url", "user_id", "timestamp"};

    @Autowired
    private EventRepository eventRepository;

    @Transactional
    public void saveCsvData(BufferedReader br) throws IOException {
        List<EventEntity> events = new ArrayList<>();
        CSVFormat csvFormat = CSVFormat.DEFAULT.builder()
                .setHeader(HEADERS)
                .build();
        try (CSVParser csvParser = csvFormat.parse(br)) {
            for (CSVRecord record : csvParser) {
                // Process each CSV record
                EventEntity event = new EventEntity();
                String url = record.get("url");
                Matcher matcher = pattern.matcher(url);
                if (!matcher.matches()) {
                    continue;
                }
                event.setDocId(matcher.group(1));
                event.setUserId(record.get("user_id"));
                event.setTimestamp(record.get("timestamp"));
                events.add(event);

                // TODO: implement persistence by ActiveMq Queue instead of saving to the database directly.
                //  This will allow us to process the data asynchronously.
                //  The queue should be consumed by a separate service and not block the main thread.
                //  However, for the purpose of this exercise, we will save the data directly to the database.
                if (events.size() == 1000) {
                    this.saveEventsInBatch(events);
                    events.clear();
                }
            }
        }
        this.saveEventsInBatch(events);
    }

    public List<UserCountResponse> getDateAndUserCountByDocIdAndDateRange(String docId, LocalDate startDate, LocalDate endDate) {
        return eventRepository.findUniqueUsersByDocIdAndDateRange(docId, startDate, endDate).stream()
                .map(r -> {
                    Date sqlDate = (Date) r[0];
                    LocalDate localDate = sqlDate.toLocalDate();
                    return new UserCountResponse(localDate, (Long) r[1]);
                })
                .toList();
    }

    public List<UserCountResponse> getEventsByDocIdAndDateRange(String docId, LocalDate startDate, LocalDate endDate) {
        List<UserCountResponse> userCountResponses = new ArrayList<>();

        Set<EventEntity> eventEntities = eventRepository.findByDocIdAndTimestampBetween(
                docId,
                Timestamp.valueOf(startDate.atStartOfDay()).toString(),
                Timestamp.valueOf(endDate.atTime(23, 59, 59)).toString()
        );

        HashMap<LocalDate, Set<String>> userCountByDate = new HashMap<>();

        // TODO: get userCount by date
        eventEntities
            .forEach(e -> {
                LocalDate localDate = LocalDate.parse(e.getTimestamp().substring(0, 10));
                userCountByDate.putIfAbsent(localDate, new HashSet<>(Collections.singletonList(e.getUserId())));
                userCountByDate.get(localDate).add(e.getUserId());
            });
        return userCountByDate.entrySet().stream()
                .map(e -> new UserCountResponse(e.getKey(), (long) e.getValue().size()))
                .toList();
    }

    @Transactional
    public void saveEventsInBatch(List<EventEntity> events) {
        eventRepository.saveAll(events);
    }
}