package com.biolab.launchpad;

import com.biolab.TestcontainersConfiguration;
import com.biolab.common.*;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.jdbc.core.simple.JdbcClient;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ExtendWith(SpringExtension.class)
@Import(TestcontainersConfiguration.class)
@DisplayName("Dictionary tables match their Java enums")
class DictionarySyncTest {

    @Autowired
    private JdbcClient jdbcClient;

    @Test
    @DisplayName("sport_dictionary contains all Sport enum values")
    void sportDictionaryMatchesEnum() {
        List<String> db = jdbcClient.sql("SELECT name FROM sport_dictionary").query(String.class).list();
        List<String> expected = Arrays.stream(Sport.values()).map(s -> s.getValue()).toList();
        assertThat(db).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("age_group_dictionary contains all AgeGroup enum values")
    void ageGroupDictionaryMatchesEnum() {
        List<String> db = jdbcClient.sql("SELECT name FROM age_group_dictionary").query(String.class).list();
        List<String> expected = Arrays.stream(AgeGroup.values()).map(ag -> ag.getValue()).toList();
        assertThat(db).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("data_source_type_dictionary contains all DataSourceType enum values")
    void dataSourceTypeDictionaryMatchesEnum() {
        List<String> db = jdbcClient.sql("SELECT name FROM data_source_type_dictionary").query(String.class).list();
        List<String> expected = Arrays.stream(DataSourceType.values()).map(dst -> dst.getValue()).toList();
        assertThat(db).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("resource_type_dictionary contains all ResourceContentType enum values")
    void resourceContentTypeDictionaryMatchesEnum() {
        List<String> db = jdbcClient.sql("SELECT name FROM resource_type_dictionary").query(String.class).list();
        List<String> expected = Arrays.stream(ResourceContentType.values()).map(rct -> rct.getValue()).toList();
        assertThat(db).containsExactlyInAnyOrderElementsOf(expected);
    }

    @Test
    @DisplayName("user_roles_dictionary contains all UserRole enum values")
    void userRoleDictionaryMatchesEnum() {
        List<String> db = jdbcClient.sql("SELECT name FROM user_roles_dictionary").query(String.class).list();
        List<String> expected = Arrays.stream(UserRole.values()).map(ur -> ur.getValue()).toList();
        assertThat(db).containsExactlyInAnyOrderElementsOf(expected);
    }
}
