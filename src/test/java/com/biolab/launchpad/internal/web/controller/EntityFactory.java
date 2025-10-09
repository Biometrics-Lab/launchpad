package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.*;
import com.biolab.launchpad.internal.repository.model.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

@RequiredArgsConstructor
@Component
public class EntityFactory {

    private final MeasurementRepository              measurementRepository;
    private final MetricRepository                   metricRepository;
    private final AgeGroupDictionaryRepository       ageGroupDictionaryRepository;
    private final DataSourceTypeDictionaryRepository dataSourceTypeDictionaryRepository;
    private final ResourceTypeDictionaryRepository   resourceTypeDictionaryRepository;
    private final SportDictionaryRepository          sportDictionaryRepository;
    private final UserRoleDictionaryRepository       userRoleDictionaryRepository;
    private final OrganisationRepository             organisationRepository;
    private final TeamRepository                     teamRepository;
    private final ModelRepository                    modelRepository;
    private final UserRepository                     userRepository;
    private final PlayerRepository                   playerRepository;
    private final AssessmentTemplateRepository       assessmentTemplateRepository;
    private final DataSourceRepository               dataSourceRepository;
    private final TemplateMetricRepository           templateMetricRepository;
    private final AssessmentRepository               assessmentRepository;
    private final Session1Repository                 session1Repository;
    private final RepRepository                      repRepository;
    private final RepMetricRepository                repMetricRepository;
    private final RepMetricSourceRepository          repMetricSourceRepository;

    public Measurement createMeasurement(String name) {

        return measurementRepository.save(
                Measurement.builder()
                        .name(name)
                        .build()
        );
    }

    public Metric createMetric(String name) {

        Measurement measurement = createMeasurement("AutoMeasurement");
        return metricRepository.save(
                Metric.builder()
                        .name(name)
                        .measurementId(measurement.getId())
                        .build()
        );
    }

    public AgeGroupDictionary createAgeGroupDictionary(String name) {

        Optional<AgeGroupDictionary> ageGroupDictionaryOptional = ageGroupDictionaryRepository.findById(name);
        if (ageGroupDictionaryOptional.isPresent()) {
            return ageGroupDictionaryOptional.get();
        }

        AgeGroupDictionary ageGroupDictionary = AgeGroupDictionary.builder()
                .name(name)
                .build();

        ageGroupDictionary.markAsNew(true);

        return ageGroupDictionaryRepository.save(ageGroupDictionary);

    }

    public DataSourceTypeDictionary createDataSourceTypeDictionary(String name) {

        Optional<DataSourceTypeDictionary> dataSourceTypeDictionaryOptional = dataSourceTypeDictionaryRepository.findById(name);
        if (dataSourceTypeDictionaryOptional.isPresent()) {
            return dataSourceTypeDictionaryOptional.get();
        }

        DataSourceTypeDictionary dataSourceTypeDictionary = DataSourceTypeDictionary.builder()
                .name(name)
                .build();

        dataSourceTypeDictionary.markAsNew(true);

        return dataSourceTypeDictionaryRepository.save(dataSourceTypeDictionary);

    }

    public ResourceTypeDictionary createResourceTypeDictionary(String name) {

        Optional<ResourceTypeDictionary> resourceTypeDictionaryOptional = resourceTypeDictionaryRepository.findById(name);
        if (resourceTypeDictionaryOptional.isPresent()) {
            return resourceTypeDictionaryOptional.get();
        }

        ResourceTypeDictionary resourceTypeDictionary = ResourceTypeDictionary.builder()
                .name(name)
                .build();

        resourceTypeDictionary.markAsNew(true);

        return resourceTypeDictionaryRepository.save(resourceTypeDictionary);

    }

    public SportDictionary createSportDictionary(String name) {

        Optional<SportDictionary> sportDictionaryOptional = sportDictionaryRepository.findById(name);
        if (sportDictionaryOptional.isPresent()) {
            return sportDictionaryOptional.get();
        }

        SportDictionary sportDictionary = SportDictionary.builder()
                .name(name)
                .build();

        sportDictionary.markAsNew(true);

        return sportDictionaryRepository.save(sportDictionary);

    }

    public UserRoleDictionary createUserRoleDictionary(String name) {

        Optional<UserRoleDictionary> userRoleDictionaryOptional = userRoleDictionaryRepository.findById(name);
        if (userRoleDictionaryOptional.isPresent()) {
            return userRoleDictionaryOptional.get();
        }

        UserRoleDictionary userRoleDictionary = UserRoleDictionary.builder()
                .name(name)
                .build();

        userRoleDictionary.markAsNew(true);

        return userRoleDictionaryRepository.save(userRoleDictionary);

    }

    public Organisation createOrganisation(String name) {
        return organisationRepository.save(
                Organisation.builder()
                        .name(name)
                        .build()
        );
    }

    public Team createTeam(String name) {
        Organisation organisation       = createOrganisation("AutoOrganisation");
        SportDictionary sportDictionary = createSportDictionary("AutoSport");
        return teamRepository.save(
                Team.builder()
                        .name(name)
                        .organisationId(organisation.getId())
                        .sport(sportDictionary.getId())
                        .description("AutoDescription_"+name)
                        .build()
        );
    }

    public Model createModel(String name) {
        AgeGroupDictionary ageGroupDictionary = createAgeGroupDictionary("AutoAgeGroup");
        SportDictionary sportDictionary       = createSportDictionary("AutoSport");
        return modelRepository.save(
                Model.builder()
                        .ageGroup(ageGroupDictionary.getId())
                        .sport(sportDictionary.getId())
                        .description("AutoDescription_"+name)
                        .build()
        );
    }

    public User createUser(String name) {
        UserRoleDictionary userRoleDictionary = createUserRoleDictionary("AutoRole");
        return userRepository.save(
                        User.builder()
                        .name(name)
                        .role(userRoleDictionary.getId())
                        .build()
        );
    }

    public Player createPlayer(String name) {
        Team team = createTeam("AutoTeam");
        return playerRepository.save(
                Player.builder()
                        .name(name)
                        .teamId(team.getId())
                        .build()
        );
    }

    public AssessmentTemplate createAssessmentTemplate(String name) {
        SportDictionary sportDictionary = createSportDictionary("AutoSport");
        return assessmentTemplateRepository.save(
                AssessmentTemplate.builder()
                        .name(name)
                        .sport(sportDictionary.getId())
                        .description("AutoDescription_"+name)
                        .build()
        );
    }

    public DataSource createDataSource(String name) {
        DataSourceTypeDictionary dataSourceTypeDictionary = createDataSourceTypeDictionary("AutoDataSource");
        return dataSourceRepository.save(
                DataSource.builder()
                        .name(name)
                        .type(dataSourceTypeDictionary.getId())
                        .description("AutoDescription_"+name)
                        .build()
        );
    }

    public TemplateMetric createTemplateMetric(String name) {
        AssessmentTemplate assessmentTemplate = createAssessmentTemplate("AutoAssessmentTemplate");
        Metric metric                         = createMetric("AutoMetric");
        DataSource dataSource                 = createDataSource("AutoDataSource");
        return templateMetricRepository.save(
                TemplateMetric.builder()
                        .templateId(assessmentTemplate.getId())
                        .metricId(metric.getId())
                        .sourceId(dataSource.getId())
                        .build()
        );
    }

    public Assessment createAssessment(String name) {
        SportDictionary sportDictionary       = createSportDictionary("AutoSportDictionary");
        Player player                         = createPlayer("AutoPlayer");
        AssessmentTemplate assessmentTemplate = createAssessmentTemplate("AutoAssessmentTemplate");
        return assessmentRepository.save(
                Assessment.builder()
                        .sport(sportDictionary.getId())
                        .playerId (player.getId())
                        .templateId(assessmentTemplate.getId())
                        .build()
        );
    }

    public Session1 createSession1(String name) {
        Assessment assessment = createAssessment("AutoAssessment");
        return session1Repository.save(
                Session1.builder()
                        .assessmentId(assessment.getId())
                        .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 1)))
                        .build()
        );
    }

    public Rep createRep(String name) {
        Session1 session1 = createSession1("AutoSession1");
        return repRepository.save(
                Rep.builder()
                        .session1Id(session1.getId())
                        .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 1)))
                        .build()
        );
    }

    public RepMetric createRepMetric(String name) {
        Rep rep       = createRep("AutoRep");
        Metric metric = createMetric("AutoMetric");
        return repMetricRepository.save(
                RepMetric.builder()
                        .repId(rep.getId())
                        .metricId(metric.getId())
                        .build()
        );
    }

    public RepMetricSource createRepMetricSource(String name) {
        Metric metric         = createMetric("AutoMetric");
        DataSource dataSource = createDataSource("AutoDataSource");
        return repMetricSourceRepository.save(
                RepMetricSource.builder()
                        .repMetricId(metric.getId())
                        .dataSourceId(dataSource.getId())
                        .description("AutoDescription_"+name)
                        .build()
        );
    }

    void cleanup() {
        repMetricSourceRepository.deleteAll();
        repMetricRepository.deleteAll();
        repRepository.deleteAll();
        session1Repository.deleteAll();
        assessmentRepository.deleteAll();
        templateMetricRepository.deleteAll();
        dataSourceRepository.deleteAll();
        assessmentTemplateRepository.deleteAll();
        playerRepository.deleteAll();
        userRepository.deleteAll();
        modelRepository.deleteAll();
        teamRepository.deleteAll();
        organisationRepository.deleteAll();
        metricRepository.deleteAll();
        measurementRepository.deleteAll();
        ageGroupDictionaryRepository.deleteAll();
        userRoleDictionaryRepository.deleteAll();
        sportDictionaryRepository.deleteAll();
        resourceTypeDictionaryRepository.deleteAll();
        dataSourceTypeDictionaryRepository.deleteAll();
    }
}