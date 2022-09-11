package com.myorg;

import software.amazon.awscdk.Duration;
import software.amazon.awscdk.Fn;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.applicationautoscaling.EnableScalingProps;
import software.amazon.awscdk.services.ecr.IRepository;
import software.amazon.awscdk.services.ecr.Repository;
import software.amazon.awscdk.services.ecs.*;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedFargateService;
import software.amazon.awscdk.services.ecs.patterns.ApplicationLoadBalancedTaskImageOptions;
import software.constructs.Construct;

import java.util.HashMap;
import java.util.Map;

public class ProjectServiceStack extends Stack {
    public ProjectServiceStack(final Construct scope, final String id, final Cluster cluster) {
        this(scope, id, null,  cluster);
    }

    public ProjectServiceStack(final Construct scope, final String id, final StackProps props, final Cluster cluster) {
        super(scope, id, props);

        Map<String, String> autenticacao= new HashMap<>();
        autenticacao.put("SPRING_DATASOURCE_URL", "jdbc:mysql://" + Fn.importValue("pedidos-db-endpoint") + ":3306/alurafood-pedidos?createDatabaseIfNotExist=true");
        autenticacao.put("SPRING_DATASOURCE_USERNAME", "admin");
        autenticacao.put("SPRING_DATASOURCE_PASSWORD", Fn.importValue("pedidos-db-senha"));

        IRepository iRepository =Repository.fromRepositoryName(this,"repositorio","img-aws-test");

        // Create a load-balanced Fargate service and make it public
        ApplicationLoadBalancedFargateService applicationLoadBalancedFargateService = ApplicationLoadBalancedFargateService.Builder.create(this, "ProjectService")
                .serviceName("projectService")
                .cluster(cluster)           // Required
                .cpu(512)                   // Default is 256
                .desiredCount(1)            // Default is 1
                .listenerPort(8080)
                .assignPublicIp(true)
                .taskImageOptions(
                        ApplicationLoadBalancedTaskImageOptions.builder()
                                .image(ContainerImage.fromEcrRepository(iRepository))
                                .containerPort(8080)
                                .environment(autenticacao)
                                .containerName("test")
                                .build())
                .memoryLimitMiB(2048)       // Default is 512
                .publicLoadBalancer(true)   // Default is false
                .build();

        ScalableTaskCount scalableTarget = applicationLoadBalancedFargateService
                .getService()
                .autoScaleTaskCount(
                        EnableScalingProps.builder()
                            .minCapacity(1)
                            .maxCapacity(3)
                            .build());
        scalableTarget.scaleOnCpuUtilization("CpuScaling", CpuUtilizationScalingProps.builder()
                .targetUtilizationPercent(70)
                .scaleInCooldown(Duration.minutes(3))
                .scaleOutCooldown(Duration.minutes(2))
                .build());
        scalableTarget.scaleOnMemoryUtilization("MemoryScaling", MemoryUtilizationScalingProps.builder()
                .targetUtilizationPercent(65)
                .scaleInCooldown(Duration.minutes(3))
                .scaleOutCooldown(Duration.minutes(2))
                .build());
    }
}
