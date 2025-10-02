package com.biolab.launchpad.internal.web.controller;

import com.biolab.launchpad.internal.repository.*;
import com.biolab.launchpad.internal.repository.model.*;
import org.junit.jupiter.api.AfterEach;
import org.springframework.stereotype.Component;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.Optional;

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

    public EntityFactory(MeasurementRepository measurementRepository, MetricRepository metricRepository, AgeGroupDictionaryRepository ageGroupDictionaryRepository, DataSourceTypeDictionaryRepository dataSourceTypeDictionaryRepository, ResourceTypeDictionaryRepository resourceTypeDictionaryRepository, SportDictionaryRepository sportDictionaryRepository, UserRoleDictionaryRepository userRoleDictionaryRepository, OrganisationRepository organisationRepository, TeamRepository teamRepository, ModelRepository modelRepository, UserRepository userRepository, PlayerRepository playerRepository, AssessmentTemplateRepository assessmentTemplateRepository, DataSourceRepository dataSourceRepository, TemplateMetricRepository templateMetricRepository, AssessmentRepository assessmentRepository, Session1Repository session1Repository, RepRepository repRepository, RepMetricRepository repMetricRepository, RepMetricSourceRepository repMetricSourceRepository) {
        this.measurementRepository = measurementRepository;
        this.metricRepository = metricRepository;
        this.ageGroupDictionaryRepository = ageGroupDictionaryRepository;
        this.dataSourceTypeDictionaryRepository = dataSourceTypeDictionaryRepository;
        this.resourceTypeDictionaryRepository = resourceTypeDictionaryRepository;
        this.sportDictionaryRepository = sportDictionaryRepository;
        this.userRoleDictionaryRepository = userRoleDictionaryRepository;
        this.organisationRepository = organisationRepository;
        this.teamRepository = teamRepository;
        this.modelRepository = modelRepository;
        this.userRepository = userRepository;
        this.playerRepository = playerRepository;
        this.assessmentTemplateRepository = assessmentTemplateRepository;
        this.dataSourceRepository = dataSourceRepository;
        this.templateMetricRepository = templateMetricRepository;
        this.assessmentRepository = assessmentRepository;
        this.session1Repository = session1Repository;
        this.repRepository = repRepository;
        this.repMetricRepository = repMetricRepository;
        this.repMetricSourceRepository = repMetricSourceRepository;
    }

    public Measurement createMeasurement(String name) {
        return measurementRepository.save(
                Measurement.builder()
                        .name(name)
                        .build()
        );
    }

    public Metric createMetric(String name) {
        Measurement measurement = createMeasurement("AutoMeasurement_" + name);
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
        Organisation organisation       = createOrganisation("AutoOrganisation_" + name);
        SportDictionary sportDictionary = createSportDictionary("AutoSport_" + name);
        return teamRepository.save(
                Team.builder()
                        .name(name)
                        .organisation_id(organisation.getId())
                        .sport(sportDictionary.getId())
                        .description("AutoDescription_"+name)
                        .build()
        );
    }

    public Model createModel(String name) {
        AgeGroupDictionary ageGroupDictionary = createAgeGroupDictionary("AutoAgeGroup_" + name);
        SportDictionary sportDictionary       = createSportDictionary("AutoSport_" + name);
        return modelRepository.save(
                Model.builder()
                        .age_group(ageGroupDictionary.getId())
                        .sport(sportDictionary.getId())
                        .description("AutoDescription_"+name)
                        .build()
        );
    }

    public User createUser(String name) {
        UserRoleDictionary userRoleDictionary       = createUserRoleDictionary("AutoRole_" + name);
        return userRepository.save(
                User.builder()
                        .role(userRoleDictionary.getId())
                        .build()
        );
    }

    public Player createPlayer(String name) {
        Team team = createTeam("AutoTeam_" + name);
        return playerRepository.save(
                Player.builder()
                        .name(name)
                        .team_id(team.getId())
                        .build()
        );
    }

    public AssessmentTemplate createAssessmentTemplate(String name) {
        SportDictionary sportDictionary       = createSportDictionary("AutoSport_" + name);
        return assessmentTemplateRepository.save(
                AssessmentTemplate.builder()
                        .name(name)
                        .sport(sportDictionary.getId())
                        .description("AutoDescription_"+name)
                        .build()
        );
    }

    public DataSource createDataSource(String name) {
        DataSourceTypeDictionary dataSourceTypeDictionary = createDataSourceTypeDictionary("AutoDataSours_" + name);
        return dataSourceRepository.save(
                DataSource.builder()
                        .name(name)
                        .type(dataSourceTypeDictionary.getId())
                        .description("AutoDescription_"+name)
                        .build()
        );
    }

    public TemplateMetric createTemplateMetric(String name) {
        AssessmentTemplate assessmentTemplate = createAssessmentTemplate("AutoAsTemp"+name);
        Metric metric                         = createMetric("AutoMetric"+name);
        DataSource dataSource                 = createDataSource("AutoDataSource"+name);
        return templateMetricRepository.save(
                TemplateMetric.builder()
                        .template_id(assessmentTemplate.getId())
                        .metric_id(metric.getId())
                        .source_id(dataSource.getId())
                        .build()
        );
    }

    public Assessment createAssessment(String name) {
        SportDictionary sportDictionary       = createSportDictionary("AutoSport"+name);
        Player player                         = createPlayer("AutoPlayer"+name);
        AssessmentTemplate assessmentTemplate = createAssessmentTemplate("AutoAsTemp"+name);
        return assessmentRepository.save(
                Assessment.builder()
                        .sport(sportDictionary.getId())
                        .player_id (player.getId())
                        .template_id(assessmentTemplate.getId())
                        .build()
        );
    }

    public Session1 createSession1(String name) {
        Assessment assessment = createAssessment("AutoAss_" + name);
        return session1Repository.save(
                Session1.builder()
                        .assessment_id(assessment.getId())
                        .start_time(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 1)))
                        .build()
        );
    }

    public Rep createRep(String name) {
        Session1 session1 = createSession1("AutoSes_" + name);
        return repRepository.save(
                Rep.builder()
                        .session1_id(session1.getId())
                        .start_time(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 1)))
                        .build()
        );
    }

    public RepMetric createRepMetric(String name) {
        Rep rep       = createRep("AutoRep_" + name);
        Metric metric = createMetric("AutoMetric_" + name);
        return repMetricRepository.save(
                RepMetric.builder()
                        .rep_id(rep.getId())
                        .metric_id(metric.getId())
                        .build()
        );
    }

    public RepMetricSource createRepMetricSource(String name) {
        Metric metric         = createMetric("AutoMetric_" + name);
        DataSource dataSource = createDataSource("AutoDataSource_" + name);
        return repMetricSourceRepository.save(
                RepMetricSource.builder()
                        .rep_metric_id(dataSource.getId())
                        .data_source_id(metric.getId())
                        .description("AutoDescription_"+name)
                        .build()
        );
    }

    @AfterEach
    void cleanup() {
//        metricRepository.deleteAll();
//        measurementRepository.deleteAll();
//        ageGroupDictionaryRepository.deleteAll();
//        dataSourceTypeDictionaryRepository.deleteAll();
//        resourceTypeDictionaryRepository.deleteAll();
//        sportDictionaryRepository.deleteAll();
//        userRoleDictionaryRepository.deleteAll();
//        organisationRepository.deleteAll();
//        teamRepository.deleteAll();
//        modelRepository.deleteAll();
//        userRepository.deleteAll();
//        playerRepository.deleteAll();
//        assessmentTemplateRepository.deleteAll();
//        dataSourceRepository.deleteAll();
//        templateMetricRepository.deleteAll();
//        assessmentRepository.deleteAll();
//        session1Repository.deleteAll();
//        repRepository.deleteAll();
//        repMetricRepository.deleteAll();
//        repMetricSourceRepository.deleteAll();
    }


}
