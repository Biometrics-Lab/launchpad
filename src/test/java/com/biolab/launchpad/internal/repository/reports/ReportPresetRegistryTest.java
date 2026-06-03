package com.biolab.launchpad.internal.repository.reports;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@DisplayName("ReportPresetRegistry")
class ReportPresetRegistryTest {

    private ReportPresetRegistry registry;

    @BeforeEach
    void setUp() {
        registry = new ReportPresetRegistry(new ObjectMapper());
        registry.load();
    }

    @Test
    @DisplayName("loads all 3 player-assessment presets")
    void loadsAllPresets() {
        List<ReportPresetRegistry.ReportPreset> presets = registry.findAll("player-assessment");
        assertThat(presets).hasSize(3);
    }

    @Test
    @DisplayName("findDefault returns Column Chart preset")
    void findsDefault() {
        assertThat(registry.findDefault("player-assessment"))
            .isPresent()
            .get()
            .extracting(ReportPresetRegistry.ReportPreset::name)
            .isEqualTo("Column Chart");
    }

    @Test
    @DisplayName("findByName returns present for known preset")
    void findByNameKnown() {
        assertThat(registry.findByName("Line Chart")).isPresent();
    }

    @Test
    @DisplayName("findByName returns empty for unknown name")
    void findByNameUnknown() {
        assertThat(registry.findByName("Does Not Exist")).isEmpty();
    }

    @Test
    @DisplayName("exists returns true for preset name")
    void existsPreset() {
        assertThat(registry.exists("Column Chart")).isTrue();
        assertThat(registry.exists("Unknown")).isFalse();
    }

    @Test
    @DisplayName("findAll with null returns all presets")
    void findAllWithNullReturnsAll() {
        assertThat(registry.findAll(null)).hasSize(3);
    }
}
