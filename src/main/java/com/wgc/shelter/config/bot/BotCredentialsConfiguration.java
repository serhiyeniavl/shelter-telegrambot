package com.wgc.shelter.config.bot;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.ConstructorBinding;

@ConfigurationProperties("telegram.bot")
@ConstructorBinding
public record BotCredentialsConfiguration(String botUserName, String token) { }

