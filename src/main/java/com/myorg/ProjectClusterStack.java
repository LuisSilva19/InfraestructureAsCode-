package com.myorg;

import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
import software.amazon.awscdk.services.ec2.Vpc;
import software.amazon.awscdk.services.ecs.Cluster;
import software.constructs.Construct;

public class ProjectClusterStack extends Stack {
    private Cluster cluster;
    public ProjectClusterStack(final Construct scope, final String id, final Vpc vpc) {
        this(scope, id, null, vpc);
    }

    public ProjectClusterStack(final Construct scope, final String id, final StackProps props, final Vpc vpc) {
        super(scope, id, props);

        cluster = Cluster.Builder.create(this, "projectCLuster")
                .clusterName("project-cluster")
                .vpc(vpc)
                .build();
    }

    public Cluster getCluster(){
        return cluster;
    }
}
