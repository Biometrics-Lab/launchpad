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
    private final AssessmentRepository               assessmentRepository;
    private final SessionRepository                  sessionRepository;
    private final RepRepository                      repRepository;
    private final RepMetricRepository                repMetricRepository;
    private final ConditionRepository                conditionRepository;
    private final ConditionalMetricRepository        conditionalMetricRepository;
    private final TemplateMetricRepository           templateMetricRepository;
    private final AssessmentMetricRepository         assessmentMetricRepository;
    private final SessionMetricRepository            sessionMetricRepository;

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

    public DataSource createDataSource() {
        Metric metric = createMetric("AutoMetric");
        return dataSourceRepository.save(
                DataSource.builder()
                        .metricId(metric.getId())
                        .type("JSON_CONFIG")
                        .build()
        );
    }

    public DataSource createDataSource(Integer metricId) {
        return dataSourceRepository.save(
                DataSource.builder()
                        .metricId(metricId)
                        .name("AutoDataSource-" + System.nanoTime())
                        .type("JSON_CONFIG")
                        .content("{}")
                        .build()
        );
    }

    public DataSource createBlastMotionDataSource(Integer metricId) {
        createDataSourceTypeDictionary("BAT_SENSOR");
        return dataSourceRepository.save(DataSource.builder()
                .name("AutoBlastBatSpeed-" + System.nanoTime())
                .metricId(metricId)
                .type("BAT_SENSOR")
                .content("{\"integrationId\":\"blast-motion-api-demo\",\"metric\":\"BAT_SPEED\"}")
                .build());
    }

    public ConditionalMetric createConditionalMetric(Integer metricId) {
        Condition condition = createCondition("AutoCondition-" + System.nanoTime());
        return conditionalMetricRepository.save(ConditionalMetric.builder()
                .name("AutoConditionalMetric-" + System.nanoTime())
                .conditionId(condition.getId())
                .metricId(metricId)
                .build());
    }

    public AssessmentMetric createAssessmentMetric(Integer assessmentId, Integer conditionalMetricId) {
        ConditionalMetric cm = conditionalMetricRepository.findById(conditionalMetricId).orElseThrow();
        DataSource dataSource = createDataSource(cm.getMetricId());
        return assessmentMetricRepository.save(
                AssessmentMetric.builder()
                        .assessmentId(assessmentId)
                        .conditionalMetricId(conditionalMetricId)
                        .dataSourceId(dataSource.getId())
                        .build()
        );
    }

    public AssessmentMetric createAssessmentMetric(Integer assessmentId, Integer conditionalMetricId, Integer dataSourceId) {
        return assessmentMetricRepository.save(AssessmentMetric.builder()
                .assessmentId(assessmentId)
                .conditionalMetricId(conditionalMetricId)
                .dataSourceId(dataSourceId)
                .build());
    }

    public Session createBlastMotionSession() {
        createResourceTypeDictionary("Video");
        Metric metric = createMetric("AutoBatSpeedMetric");
        DataSource dataSource = createBlastMotionDataSource(metric.getId());
        ConditionalMetric cm = createConditionalMetric(metric.getId());
        Assessment assessment = createAssessment();
        createAssessmentMetric(assessment.getId(), cm.getId(), dataSource.getId());
        return sessionRepository.save(Session.builder()
                .assessmentId(assessment.getId())
                .startTime(Timestamp.valueOf(LocalDateTime.now()))
                .build());
    }

    public Assessment createAssessment() {
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

    public Assessment createAssessment(Integer playerId) {
        SportDictionary sportDictionary = createSportDictionary("AutoSportDictionary");
        AssessmentTemplate assessmentTemplate = createAssessmentTemplate("AutoAssessmentTemplate");
        return assessmentRepository.save(
                Assessment.builder()
                        .sport(sportDictionary.getId())
                        .playerId(playerId)
                        .templateId(assessmentTemplate.getId())
                        .build()
        );
    }

    public Session createSession() {
        Assessment assessment = createAssessment();
        return sessionRepository.save(
                Session.builder()
                        .assessmentId(assessment.getId())
                        .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 1)))
                        .build()
        );
    }

    public Session createActiveSession() {
        Session session = createSession();
        session.setStatus("ACTIVE");
        return sessionRepository.save(session);
    }

    public Rep createRep() {
        Session session = createSession();
        return repRepository.save(
                Rep.builder()
                        .sessionId(session.getId())
                        .startTime(Timestamp.valueOf(LocalDateTime.of(2025, 10, 2, 14, 45, 1)))
                        .build()
        );
    }

    public Condition createCondition(String name) {
        SportDictionary sportDictionary = createSportDictionary("AutoSport");
        return conditionRepository.save(
                Condition.builder()
                        .name(name)
                        .sport(sportDictionary.getId())
                        .build()
        );
    }

    public ConditionalMetric createConditionalMetric() {
        Condition condition = createCondition("AutoCondition");
        Metric metric       = createMetric("AutoMetric");
        return conditionalMetricRepository.save(
                ConditionalMetric.builder()
                        .name("AutoConditionalMetric")
                        .conditionId(condition.getId())
                        .metricId(metric.getId())
                        .build()
        );
    }

    private Metric createNegatedMetric(String name) {
        Measurement measurement = createMeasurement("AutoMeasurement");
        return metricRepository.save(
            Metric.builder()
                .name(name)
                .measurementId(measurement.getId())
                .negate(true)
                .build()
        );
    }

    public ConditionalMetric createNegatedConditionalMetric() {
        Condition condition = createCondition("AutoCondition");
        Metric metric       = createNegatedMetric("AutoNegatedMetric");
        return conditionalMetricRepository.save(
            ConditionalMetric.builder()
                .name("AutoNegatedConditionalMetric")
                .conditionId(condition.getId())
                .metricId(metric.getId())
                .build()
        );
    }

    public RepMetric createRepMetric() {
        Rep rep                               = createRep();
        ConditionalMetric conditionalMetric   = createConditionalMetric();
        return repMetricRepository.save(
                RepMetric.builder()
                        .repId(rep.getId())
                        .conditionalMetricId(conditionalMetric.getId())
                        .build()
        );
    }

    public void cleanup() {
        templateMetricRepository.deleteAll();
        assessmentMetricRepository.deleteAll();
        repMetricRepository.deleteAll();
        sessionMetricRepository.deleteAll();
        conditionalMetricRepository.deleteAll();
        repRepository.deleteAll();
        sessionRepository.deleteAll();
        assessmentRepository.deleteAll();
        assessmentTemplateRepository.deleteAll();
        conditionRepository.deleteAll();
        dataSourceRepository.deleteAll();
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
