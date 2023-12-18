package cz.pfeffer.eventapi.domain;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDate;

@Data
@AllArgsConstructor
public class UserCountResponse {

    private LocalDate date;
    private Long userCount;

}