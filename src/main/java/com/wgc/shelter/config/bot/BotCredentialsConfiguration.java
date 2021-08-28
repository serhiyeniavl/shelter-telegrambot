package com.wgc.shelter.config.bot;

import lombok.AccessLevel;
import lombok.Data;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties("telegram.bot")
@FieldDefaults(level = AccessLevel.PRIVATE)
public class BotCredentialsConfiguration {
    String botUserName;
    String token;
}

