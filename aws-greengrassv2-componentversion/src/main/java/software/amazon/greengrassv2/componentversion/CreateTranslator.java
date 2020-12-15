/*
 * Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
 * SPDX-License-Identifier: Apache-2.0
 */

package software.amazon.greengrassv2.componentversion;

import software.amazon.awssdk.services.greengrassv2.model.ComponentDependencyRequirement;
import software.amazon.awssdk.services.greengrassv2.model.ComponentPlatform;
import software.amazon.awssdk.services.greengrassv2.model.LambdaContainerParams;
import software.amazon.awssdk.services.greengrassv2.model.LambdaDeviceMount;
import software.amazon.awssdk.services.greengrassv2.model.LambdaEventSource;
import software.amazon.awssdk.services.greengrassv2.model.LambdaLinuxProcessParams;
import software.amazon.awssdk.services.greengrassv2.model.LambdaVolumeMount;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * A collection of translation methods just for Create, since there are many of them.
 */
public class CreateTranslator {

    @Nullable
    static software.amazon.awssdk.services.greengrassv2.model.LambdaFunctionRecipeSource
    translateToSDKLambdaFunctionRecipeSource(
            @Nonnull LambdaFunctionRecipeSource modelLambdaRecipeSource) {
      return software.amazon.awssdk.services.greengrassv2.model.LambdaFunctionRecipeSource.builder()
              .lambdaArn(modelLambdaRecipeSource.getLambdaArn())
              .componentName(modelLambdaRecipeSource.getComponentName())
              .componentVersion(modelLambdaRecipeSource.getComponentVersion())
              .componentDependencies(mapNullable(modelLambdaRecipeSource.getComponentDependencies(),
                      CreateTranslator::translateToSDKComponentVersionDependencyRequirements))
              .componentLambdaParameters(mapNullable(modelLambdaRecipeSource.getComponentLambdaParameters(),
                      CreateTranslator::translateToSDKComponentLambdaParameters))
              .componentPlatforms(mapNullable(modelLambdaRecipeSource.getComponentPlatforms(),
                      CreateTranslator::transformToSDKComponentPlatforms))
              .build();
    }

    private static Map<String, ComponentDependencyRequirement> translateToSDKComponentVersionDependencyRequirements(
            @Nonnull Map<String, software.amazon.greengrassv2.componentversion.ComponentDependencyRequirement> modelDependencies) {

      return modelDependencies.entrySet().stream()
              .collect(Collectors.toMap(
                      Map.Entry::getKey,
                      entry -> ComponentDependencyRequirement.builder()
                              .dependencyType(entry.getValue().getDependencyType())
                              .versionRequirement(entry.getValue().getVersionRequirement())
                              .build()
                      ));
    }

    private static software.amazon.awssdk.services.greengrassv2.model.LambdaExecutionParameters
    translateToSDKComponentLambdaParameters(@Nonnull LambdaExecutionParameters modelParams) {
      return software.amazon.awssdk.services.greengrassv2.model.LambdaExecutionParameters.builder()
              .environmentVariables(modelParams.getEnvironmentVariables())
              .eventSources(mapNullable(modelParams.getEventSources(), CreateTranslator::transformToSDKLambdaEventSources))
              .execArgs(modelParams.getExecArgs())
              .inputPayloadEncodingType(modelParams.getInputPayloadEncodingType())
              .linuxProcessParams(mapNullable(modelParams.getLinuxProcessParams(),
                      CreateTranslator::transformToSDKLambdaLinuxProcessParams))
              .maxIdleTimeInSeconds(modelParams.getMaxIdleTimeInSeconds())
              .maxInstancesCount(modelParams.getMaxInstancesCount())
              .maxQueueSize(modelParams.getMaxQueueSize())
              .pinned(modelParams.getPinned())
              .statusTimeoutInSeconds(modelParams.getStatusTimeoutInSeconds())
              .build();
    }

    private static List<LambdaEventSource> transformToSDKLambdaEventSources(
            @Nonnull List<software.amazon.greengrassv2.componentversion.LambdaEventSource> modelEventSources) {
      return modelEventSources.stream()
              .map(modelEventSource ->
                      LambdaEventSource.builder()
                              .topic(modelEventSource.getTopic())
                              .type(modelEventSource.getType())
                              .build())
              .collect(Collectors.toList());
    }

    private static List<ComponentPlatform> transformToSDKComponentPlatforms(
            @Nonnull List<software.amazon.greengrassv2.componentversion.ComponentPlatform> modelPlatforms) {
      return modelPlatforms.stream()
              .map(modelPlatform ->
                      ComponentPlatform.builder()
                              .name(modelPlatform.getName())
                              .attributes(modelPlatform.getAttributes())
                              .build())
              .collect(Collectors.toList());
    }

    private static LambdaLinuxProcessParams transformToSDKLambdaLinuxProcessParams(
            @Nonnull software.amazon.greengrassv2.componentversion.LambdaLinuxProcessParams linuxProcessParams) {
      return LambdaLinuxProcessParams.builder()
              .isolationMode(linuxProcessParams.getIsolationMode())
              .containerParams(mapNullable(linuxProcessParams.getContainerParams(),
                      CreateTranslator::translateToSDKLambdaContainerParams))
              .build();
    }

    private static LambdaContainerParams translateToSDKLambdaContainerParams(
            @Nonnull software.amazon.greengrassv2.componentversion.LambdaContainerParams modelContainerParams) {
      return LambdaContainerParams.builder()
              .memorySizeInKB(modelContainerParams.getMemorySizeInKB())
              .mountROSysfs(modelContainerParams.getMountROSysfs())
              .devices(mapNullable(modelContainerParams.getDevices(), CreateTranslator::translateToSDKLambdaDeviceMounts))
              .volumes(mapNullable(modelContainerParams.getVolumes(), CreateTranslator::translateToSDKLambdaVolumes))
              .build();
    }

    private static List<LambdaDeviceMount> translateToSDKLambdaDeviceMounts(
            @Nonnull List<software.amazon.greengrassv2.componentversion.LambdaDeviceMount> devices) {
      return devices.stream()
              .map(modelDeviceMount -> LambdaDeviceMount.builder()
                      .path(modelDeviceMount.getPath())
                      .permission(modelDeviceMount.getPermission())
                      .addGroupOwner(modelDeviceMount.getAddGroupOwner())
                      .build())
              .collect(Collectors.toList());
    }

    private static List<LambdaVolumeMount> translateToSDKLambdaVolumes(
            @Nonnull List<software.amazon.greengrassv2.componentversion.LambdaVolumeMount> devices) {
      return devices.stream()
              .map(modelVolumeMount -> LambdaVolumeMount.builder()
                      .addGroupOwner(modelVolumeMount.getAddGroupOwner())
                      .destinationPath(modelVolumeMount.getDestinationPath())
                      .permission(modelVolumeMount.getPermission())
                      .sourcePath(modelVolumeMount.getSourcePath())
                      .build())
              .collect(Collectors.toList());
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
