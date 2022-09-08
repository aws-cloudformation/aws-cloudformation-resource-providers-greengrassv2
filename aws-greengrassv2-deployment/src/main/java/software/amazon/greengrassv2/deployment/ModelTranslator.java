/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.deployment;

import lombok.NonNull;
import org.apache.commons.collections.MapUtils;
import software.amazon.awssdk.services.greengrassv2.model.ComponentConfigurationUpdate;
import software.amazon.awssdk.services.greengrassv2.model.ComponentRunWith;
import software.amazon.awssdk.services.greengrassv2.model.DeploymentComponentUpdatePolicy;
import software.amazon.awssdk.services.greengrassv2.model.DeploymentConfigurationValidationPolicy;
import software.amazon.awssdk.services.greengrassv2.model.DeploymentFailureHandlingPolicy;
import software.amazon.awssdk.services.greengrassv2.model.DeploymentIoTJobConfiguration;
import software.amazon.awssdk.services.greengrassv2.model.DeploymentPolicies;
import software.amazon.awssdk.services.greengrassv2.model.IoTJobAbortConfig;
import software.amazon.awssdk.services.greengrassv2.model.IoTJobAbortCriteria;
import software.amazon.awssdk.services.greengrassv2.model.IoTJobExecutionsRolloutConfig;
import software.amazon.awssdk.services.greengrassv2.model.IoTJobExponentialRolloutRate;
import software.amazon.awssdk.services.greengrassv2.model.IoTJobRateIncreaseCriteria;
import software.amazon.awssdk.services.greengrassv2.model.IoTJobTimeoutConfig;
import software.amazon.awssdk.services.greengrassv2.model.SystemResourceLimits;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

public class ModelTranslator {

    static Map<String, software.amazon.awssdk.services.greengrassv2.model.ComponentDeploymentSpecification> translateToSdkComponentDeploymentSpecs(
            Map<String, software.amazon.greengrassv2.deployment.ComponentDeploymentSpecification> componentDeploymentSpec) {
        Map<String, software.amazon.awssdk.services.greengrassv2.model.ComponentDeploymentSpecification> sdkComponentDeploymentSepc = new LinkedHashMap<>();

        if (MapUtils.isEmpty(componentDeploymentSpec)) {
            return sdkComponentDeploymentSepc;
        }

        componentDeploymentSpec.forEach(((componentName, componentInfo) -> {
            sdkComponentDeploymentSepc.put(componentName, software.amazon.awssdk.services.greengrassv2.model.ComponentDeploymentSpecification.builder()
                    .componentVersion(componentInfo.getComponentVersion())
                    .configurationUpdate(mapNullable(componentInfo.getConfigurationUpdate(), ModelTranslator::translateToSdkComponentConfigurationUpdate))
                    .runWith(mapNullable(componentInfo.getRunWith(), ModelTranslator::translateToSdkRunWith))
                    .build());
        }));

        return sdkComponentDeploymentSepc;
    }

    static DeploymentPolicies translateToSdkDeploymentPolicies(
            software.amazon.greengrassv2.deployment.DeploymentPolicies deploymentPolicies) {
        return DeploymentPolicies.builder()
                .componentUpdatePolicy(mapNullable(deploymentPolicies.getComponentUpdatePolicy(), ModelTranslator::translateToSdkDeploymentComponentUpdatePolicy))
                .failureHandlingPolicy(mapNullable(deploymentPolicies.getFailureHandlingPolicy(), ModelTranslator::translateToSdkDeploymentFailureHandlingPolicy))
                .configurationValidationPolicy(mapNullable(deploymentPolicies.getConfigurationValidationPolicy(), ModelTranslator::translateToSdkDeploymentConfigurationValidationPolicy))
                .build();
    }

    static software.amazon.greengrassv2.deployment.DeploymentPolicies translateToCfnDeploymentPolicies(
            DeploymentPolicies deploymentPolicies) {
        return software.amazon.greengrassv2.deployment.DeploymentPolicies .builder()
                .componentUpdatePolicy(mapNullable(deploymentPolicies.componentUpdatePolicy(), ModelTranslator::translateToCfnDeploymentComponentUpdatePolicy))
                .failureHandlingPolicy(mapNullable(deploymentPolicies.failureHandlingPolicy(), ModelTranslator::translateToCfnDeploymentFailureHandlingPolicy))
                .configurationValidationPolicy(mapNullable(deploymentPolicies.configurationValidationPolicy(), ModelTranslator::translateToCfnDeploymentConfigurationValidationPolicy))
                .build();
    }

    private static ComponentRunWith translateToSdkRunWith(software.amazon.greengrassv2.deployment.ComponentRunWith runWith) {
        return ComponentRunWith.builder()
                .posixUser(runWith.getPosixUser())
                .systemResourceLimits(mapNullable(runWith.getSystemResourceLimits(), ModelTranslator::translateToSdkSystemResourceLimits))
                .windowsUser(runWith.getWindowsUser())
                .build();
    }

    private static software.amazon.greengrassv2.deployment.ComponentRunWith translateToCfnRunWith(ComponentRunWith runWith) {
        return software.amazon.greengrassv2.deployment.ComponentRunWith.builder()
                .posixUser(runWith.posixUser())
                .systemResourceLimits(mapNullable(runWith.systemResourceLimits(), ModelTranslator::translateToCfnSystemResourceLimits))
                .windowsUser(runWith.windowsUser())
                .build();
    }

    private static ComponentConfigurationUpdate translateToSdkComponentConfigurationUpdate(software.amazon.greengrassv2.deployment.ComponentConfigurationUpdate update) {
        return ComponentConfigurationUpdate.builder()
                .merge(update.getMerge())
                .reset(update.getReset())
                .build();
    }

    private static software.amazon.greengrassv2.deployment.ComponentConfigurationUpdate translateToCfnComponentConfigurationUpdate(ComponentConfigurationUpdate update) {
        return software.amazon.greengrassv2.deployment.ComponentConfigurationUpdate.builder()
                .merge(update.merge())
                .reset(update.hasReset() ? update.reset() : null)
                .build();
    }

    private static DeploymentConfigurationValidationPolicy translateToSdkDeploymentConfigurationValidationPolicy(software.amazon.greengrassv2.deployment.DeploymentConfigurationValidationPolicy policy) {
        return DeploymentConfigurationValidationPolicy.builder()
                .timeoutInSeconds(policy.getTimeoutInSeconds())
                .build();
    }

    private static software.amazon.greengrassv2.deployment.DeploymentConfigurationValidationPolicy translateToCfnDeploymentConfigurationValidationPolicy(DeploymentConfigurationValidationPolicy policy) {
        return software.amazon.greengrassv2.deployment.DeploymentConfigurationValidationPolicy.builder()
                .timeoutInSeconds(policy.timeoutInSeconds())
                .build();
    }

    private static DeploymentFailureHandlingPolicy translateToSdkDeploymentFailureHandlingPolicy(String policy) {
        return DeploymentFailureHandlingPolicy.fromValue(policy);
    }

    private static String translateToCfnDeploymentFailureHandlingPolicy(DeploymentFailureHandlingPolicy policy) {
        return policy.toString();
    }

    private static SystemResourceLimits translateToSdkSystemResourceLimits(software.amazon.greengrassv2.deployment.SystemResourceLimits systemResourceLimits) {
        return SystemResourceLimits.builder()
                .cpus(systemResourceLimits.getCpus())
                .memory(systemResourceLimits.getMemory())
                .build();
    }

    private static software.amazon.greengrassv2.deployment.SystemResourceLimits translateToCfnSystemResourceLimits(SystemResourceLimits systemResourceLimits) {
        return software.amazon.greengrassv2.deployment.SystemResourceLimits.builder()
                .cpus(systemResourceLimits.cpus())
                .memory(systemResourceLimits.memory())
                .build();
    }

    private static DeploymentComponentUpdatePolicy translateToSdkDeploymentComponentUpdatePolicy(software.amazon.greengrassv2.deployment.DeploymentComponentUpdatePolicy policy) {
        return DeploymentComponentUpdatePolicy.builder()
                .action(policy.getAction())
                .timeoutInSeconds(policy.getTimeoutInSeconds())
                .build();
    }

    private static software.amazon.greengrassv2.deployment.DeploymentComponentUpdatePolicy translateToCfnDeploymentComponentUpdatePolicy(DeploymentComponentUpdatePolicy policy) {
        return software.amazon.greengrassv2.deployment.DeploymentComponentUpdatePolicy.builder()
                .action(policy.actionAsString())
                .timeoutInSeconds(policy.timeoutInSeconds())
                .build();
    }

    private static List<IoTJobAbortCriteria> translateToSdkIoTJobAbortCriteria(@NonNull List<software.amazon.greengrassv2.deployment.IoTJobAbortCriteria> criteria) {
        return criteria.stream().map(
                criterion -> software.amazon.awssdk.services.greengrassv2.model.IoTJobAbortCriteria.builder()
                        .action(criterion.getAction())
                        .failureType(criterion.getFailureType())
                        .minNumberOfExecutedThings(criterion.getMinNumberOfExecutedThings())
                        .thresholdPercentage(criterion.getThresholdPercentage())
                        .build()
                ).collect(Collectors.toList());
    }

    private static List<software.amazon.greengrassv2.deployment.IoTJobAbortCriteria> translateToCfnIoTJobAbortCriteria(@NonNull List<IoTJobAbortCriteria> criteria) {
        return criteria.stream().map(
                criterion -> software.amazon.greengrassv2.deployment.IoTJobAbortCriteria.builder()
                        .action(criterion.actionAsString())
                        .failureType(criterion.failureTypeAsString())
                        .minNumberOfExecutedThings(criterion.minNumberOfExecutedThings())
                        .thresholdPercentage(criterion.thresholdPercentage())
                        .build()
        ).collect(Collectors.toList());
    }

    private static IoTJobAbortConfig translateToSdkIoTJobAbortConfig(@NonNull software.amazon.greengrassv2.deployment.IoTJobAbortConfig config) {
        return IoTJobAbortConfig.builder()
                .criteriaList(mapNullable(config.getCriteriaList(), ModelTranslator::translateToSdkIoTJobAbortCriteria))
                .build();
    }

    private static software.amazon.greengrassv2.deployment.IoTJobAbortConfig translateToCfnIoTJobAbortConfig(@NonNull IoTJobAbortConfig config) {
        return software.amazon.greengrassv2.deployment.IoTJobAbortConfig.builder()
                .criteriaList(mapNullable(config.criteriaList(), ModelTranslator::translateToCfnIoTJobAbortCriteria))
                .build();
    }

    private static IoTJobTimeoutConfig translateToSdkTimeoutConfig(@NonNull software.amazon.greengrassv2.deployment.IoTJobTimeoutConfig config) {
        return IoTJobTimeoutConfig.builder()
                .inProgressTimeoutInMinutes(Long.valueOf(config.getInProgressTimeoutInMinutes()))
                .build();
    }

    private static software.amazon.greengrassv2.deployment.IoTJobTimeoutConfig translateToCfnTimeoutConfig(@NonNull IoTJobTimeoutConfig config) {
        return software.amazon.greengrassv2.deployment.IoTJobTimeoutConfig.builder()
                .inProgressTimeoutInMinutes(Math.toIntExact(config.inProgressTimeoutInMinutes()))
                .build();
    }

    private static IoTJobExecutionsRolloutConfig translateToSdkIoTJobExponentialRolloutConfig(@NonNull software.amazon.greengrassv2.deployment.IoTJobExecutionsRolloutConfig config) {
        return IoTJobExecutionsRolloutConfig.builder()
                .maximumPerMinute(config.getMaximumPerMinute())
                .exponentialRate(mapNullable(config.getExponentialRate(), ModelTranslator::translateToSdkIoTJobExponentialRolloutRate))
                .build();
    }

    private static software.amazon.greengrassv2.deployment.IoTJobExecutionsRolloutConfig translateToCfnIoTJobExponentialRolloutConfig(@NonNull IoTJobExecutionsRolloutConfig config) {
        return software.amazon.greengrassv2.deployment.IoTJobExecutionsRolloutConfig.builder()
                .maximumPerMinute(config.maximumPerMinute())
                .exponentialRate(mapNullable(config.exponentialRate(), ModelTranslator::translateToCfnIoTJobExponentialRolloutRate))
                .build();
    }

    private static IoTJobExponentialRolloutRate translateToSdkIoTJobExponentialRolloutRate(@NonNull software.amazon.greengrassv2.deployment.IoTJobExponentialRolloutRate rolloutRate) {
        return IoTJobExponentialRolloutRate.builder()
                .rateIncreaseCriteria(mapNullable(rolloutRate.getRateIncreaseCriteria(), ModelTranslator::translateToSdkIoTJobRateIncreaseCriteria))
                .incrementFactor(rolloutRate.getIncrementFactor())
                .baseRatePerMinute(rolloutRate.getBaseRatePerMinute())
                .build();
    }

    private static software.amazon.greengrassv2.deployment.IoTJobExponentialRolloutRate translateToCfnIoTJobExponentialRolloutRate(@NonNull IoTJobExponentialRolloutRate rolloutRate) {
        return software.amazon.greengrassv2.deployment.IoTJobExponentialRolloutRate.builder()
                .rateIncreaseCriteria(mapNullable(rolloutRate.rateIncreaseCriteria(), ModelTranslator::translateToCfnIoTJobRateIncreaseCriteria))
                .incrementFactor(rolloutRate.incrementFactor())
                .baseRatePerMinute(rolloutRate.baseRatePerMinute())
                .build();
    }

    private static IoTJobRateIncreaseCriteria translateToSdkIoTJobRateIncreaseCriteria(@NonNull software.amazon.greengrassv2.deployment.IoTJobRateIncreaseCriteria criteria) {
        return IoTJobRateIncreaseCriteria.builder()
                .numberOfSucceededThings(criteria.getNumberOfSucceededThings())
                .numberOfNotifiedThings(criteria.getNumberOfNotifiedThings())
                .build();
    }

    private static software.amazon.greengrassv2.deployment.IoTJobRateIncreaseCriteria translateToCfnIoTJobRateIncreaseCriteria(@NonNull IoTJobRateIncreaseCriteria criteria) {
        return software.amazon.greengrassv2.deployment.IoTJobRateIncreaseCriteria.builder()
                .numberOfSucceededThings(criteria.numberOfSucceededThings())
                .numberOfNotifiedThings(criteria.numberOfNotifiedThings())
                .build();
    }

    static DeploymentIoTJobConfiguration translateToSdkDeploymentIoTJobConfiguration(
            software.amazon.greengrassv2.deployment.DeploymentIoTJobConfiguration configuration) {

        return DeploymentIoTJobConfiguration.builder()
                .abortConfig(mapNullable(configuration.getAbortConfig(), ModelTranslator::translateToSdkIoTJobAbortConfig))
                .timeoutConfig(mapNullable(configuration.getTimeoutConfig(), ModelTranslator::translateToSdkTimeoutConfig))
                .jobExecutionsRolloutConfig(mapNullable(configuration.getJobExecutionsRolloutConfig(), ModelTranslator::translateToSdkIoTJobExponentialRolloutConfig))
                .build();
    }

    static software.amazon.greengrassv2.deployment.DeploymentIoTJobConfiguration translateToCfnDeploymentIoTJobConfiguration(
            DeploymentIoTJobConfiguration configuration) {
        return software.amazon.greengrassv2.deployment.DeploymentIoTJobConfiguration.builder()
                .abortConfig(mapNullable(configuration.abortConfig(), ModelTranslator::translateToCfnIoTJobAbortConfig))
                .timeoutConfig(mapNullable(configuration.timeoutConfig(), ModelTranslator::translateToCfnTimeoutConfig))
                .jobExecutionsRolloutConfig(mapNullable(configuration.jobExecutionsRolloutConfig(), ModelTranslator::translateToCfnIoTJobExponentialRolloutConfig))
                .build();
    }

    static Map<String, ComponentDeploymentSpecification> translateToCfnComponentDeploymentSpecs(
            Map<String, software.amazon.awssdk.services.greengrassv2.model.ComponentDeploymentSpecification> componentDeploymentSpec) {
        Map<String, ComponentDeploymentSpecification> cfnComponentDeploymentSpec = new LinkedHashMap<>();

        if (MapUtils.isEmpty(componentDeploymentSpec)) {
            return cfnComponentDeploymentSpec;
        }

        componentDeploymentSpec.forEach(((componentName, componentInfo) -> {
            cfnComponentDeploymentSpec.put(componentName, ComponentDeploymentSpecification.builder()
                    .componentVersion(componentInfo.componentVersion())
                    .configurationUpdate(mapNullable(componentInfo.configurationUpdate(), ModelTranslator::translateToCfnComponentConfigurationUpdate))
                    .runWith(mapNullable(componentInfo.runWith(), ModelTranslator::translateToCfnRunWith))
                    .build());
        }));

        return cfnComponentDeploymentSpec;
    }

    /**
     * Maps the given input using the given mapper function if it's non-null. Otherwise,
     * returns null.
     */
    @Nullable
    static <T, V> V mapNullable(@Nullable T input, @Nonnull Function<T, V> mapper) {
        return Optional.ofNullable(input)
                .map(mapper)
                .orElse(null);
    }
}
