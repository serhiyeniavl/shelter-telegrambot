package com.wgc.shelter.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@FieldDefaults(level = AccessLevel.PRIVATE)
@Accessors(chain = true)
@Document
public class Room {

    @Id
    String id;

    @Indexed(unique = true)
    Long ownerId;

    @Min(4)
    @Max(16)
    Integer playersQuantity;

    LocalDateTime lastActionDate;

    RoomState state;

    Set<Long> players;
}
