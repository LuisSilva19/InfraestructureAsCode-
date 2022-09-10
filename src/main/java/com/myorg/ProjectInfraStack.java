package com.myorg;

import software.constructs.Construct;
import software.amazon.awscdk.Stack;
import software.amazon.awscdk.StackProps;
// import software.amazon.awscdk.Duration;
// import software.amazon.awscdk.services.sqs.Queue;

public class ProjectInfraStack extends Stack {
    public ProjectInfraStack(final Construct scope, final String id) {
        this(scope, id, null);
    }

    public ProjectInfraStack(final Construct scope, final String id, final StackProps props) {
        super(scope, id, props);

    }
}
