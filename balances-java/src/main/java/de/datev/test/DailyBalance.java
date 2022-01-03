package de.datev.test;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;

import static java.util.Comparator.comparing;

@Data
class DailyBalance {
    @JsonProperty("iBAN")
    String iban;
    LocalDate day;
    BigDecimal total;

    public static final Comparator<DailyBalance> DayComparator = comparing(db -> db.day);
}
