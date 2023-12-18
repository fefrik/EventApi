package cz.pfeffer.eventapi.rest;

import cz.pfeffer.eventapi.domain.ImplementationEnum;
import cz.pfeffer.eventapi.domain.UserCountResponse;
import cz.pfeffer.eventapi.service.EventService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/events")
public class EventController {

    @Autowired
    private EventService eventService;

    @PostMapping(value="/upload", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    public ResponseEntity<String> uploadCsv(@RequestParam("file") MultipartFile file) {
        try {
            // Parse and save CSV data
            eventService.saveCsvData(new BufferedReader(new InputStreamReader(file.getInputStream())));
            return ResponseEntity.status(HttpStatus.OK).body("CSV file successfully processed and saved.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Failed to process the CSV file.");
        }
    }

    @GetMapping("/uniqueUsers/{docId}/{startDate}/{endDate}")
    public List<UserCountResponse> getUniqueUsersByDocIdAndDateRange(
            @PathVariable String docId,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate startDate,
            @PathVariable @DateTimeFormat(pattern = "yyyy-MM-dd") LocalDate endDate,
            @RequestParam(required = false) ImplementationEnum impl) {
        if (impl == null || impl == ImplementationEnum.DB) {
            return eventService.getDateAndUserCountByDocIdAndDateRange(docId, startDate, endDate);
        }
        return eventService.getEventsByDocIdAndDateRange(docId, startDate, endDate);
    }
}