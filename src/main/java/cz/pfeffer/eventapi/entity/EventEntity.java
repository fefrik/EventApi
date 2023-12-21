package cz.pfeffer.eventapi.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.apache.commons.lang3.builder.HashCodeBuilder;
import org.apache.commons.lang3.builder.ReflectionToStringBuilder;
import org.apache.commons.lang3.builder.ToStringStyle;

@Entity
@Table(name = "EVENT", indexes = {
        @Index(name = "idx_docId", columnList = "docId"),
        @Index(name = "idx_userId", columnList = "userId"),
        @Index(name = "idx_timestamp", columnList = "timestamp")
})
@NoArgsConstructor
@Setter
@Getter
public class EventEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "seqGen")
    @SequenceGenerator(name = "seqGen", sequenceName = "event_seq", allocationSize = 1000)
    @Column(columnDefinition = "serial")
    private Long id;

    private String docId;
    private String userId;
    private String timestamp;

    @Override
    public int hashCode() {
        return new HashCodeBuilder().append(id)
                .hashCode();
    }

    @Override
    public String toString() {
        return ReflectionToStringBuilder.toString(this, ToStringStyle.JSON_STYLE, false, false, true, null);
    }

}
