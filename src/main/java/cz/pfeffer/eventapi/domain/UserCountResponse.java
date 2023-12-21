package cz.pfeffer.eventapi.domain;

import java.time.LocalDate;

public record UserCountResponse(LocalDate date, Long userCount) {
}