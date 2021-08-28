package com.wgc.shelter.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.Max;
import javax.validation.constraints.Min;
import java.time.OffsetDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Document
public class Room {

    @Id
    String id;

    @Indexed(unique = true)
    Long ownerId;

    @Min(4)
    @Max(16)
    Integer playersQuantity;

    OffsetDateTime lastActionDate;

    RoomState state;

    List<User> players;
}
