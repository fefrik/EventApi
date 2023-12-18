package cz.pfeffer.eventapi.repository;

import cz.pfeffer.eventapi.entity.EventEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Set;

public interface EventRepository extends JpaRepository<EventEntity, Long> {

    // FIXME:??  There is mapping to a DTO class in the query, so we need to use a constructor result, but it is working only with native queries.
    @Query("SELECT date(e.timestamp) as date, COUNT(DISTINCT e.userId) " +
            "FROM EventEntity e " +
            "WHERE e.docId = :docId AND date(e.timestamp) BETWEEN :startDate AND :endDate " +
            "GROUP BY date(e.timestamp)")
    List<Object[]> findUniqueUsersByDocIdAndDateRange(
            @Param("docId") String docId,
            @Param("startDate") LocalDate  startDate,
            @Param("endDate") LocalDate  endDate);

    Set<EventEntity> findByDocIdAndTimestampBetween(String docId, String startDate, String endDate);
}