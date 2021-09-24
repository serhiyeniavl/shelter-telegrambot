package com.wgc.shelter.config;

import com.wgc.shelter.spring.properties.YamlPropertySourceFactory;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;
import lombok.experimental.FieldDefaults;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.PropertySource;

import java.util.List;
import java.util.Objects;

@Data
@FieldDefaults(level = AccessLevel.PRIVATE)
@NoArgsConstructor
@AllArgsConstructor
@Configuration
@ConfigurationProperties(prefix = "languages")
@PropertySource(value = "classpath:/lang/languages.yml", factory = YamlPropertySourceFactory.class)
public class LanguagesConfiguration {

    List<Language> all;

    public boolean containsCode(String code) {
        return all.stream()
                .anyMatch(lang -> Objects.equals(lang.getCode(), code));
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @FieldDefaults(level = AccessLevel.PRIVATE)
    public static class Language {
        String name;
        String code;
        String unicode;
    }
}


