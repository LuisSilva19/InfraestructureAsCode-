package com.myorg;

import software.amazon.awscdk.App;

public class ProjectInfraApp {
    public static void main(final String[] args) {
        App app = new App();

        ProjectVpcStack vpc = new ProjectVpcStack(app, "vpc");

        ProjectClusterStack cLuster = new ProjectClusterStack(app, "CLuster", vpc.getVpc());
        cLuster.addDependency(vpc);

        ProjectRdsStack rds = new ProjectRdsStack(app, "Rds", vpc.getVpc());
        rds.addDependency(vpc);

        ProjectServiceStack service = new ProjectServiceStack(app, "Service", cLuster.getCluster());
        service.addDependency(cLuster);

        app.synth();
    }
}


