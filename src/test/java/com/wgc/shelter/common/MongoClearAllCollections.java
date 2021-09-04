package com.wgc.shelter.common;

import com.wgc.shelter.repository.RoomRepository;
import com.wgc.shelter.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("test")
public abstract class MongoClearAllCollections {

    @Autowired
    protected UserRepository userRepository;
    @Autowired
    protected RoomRepository roomRepository;

    @BeforeEach
    void clear() {
        userRepository.deleteAll();
        roomRepository.deleteAll();
    }
}
