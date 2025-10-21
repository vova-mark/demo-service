# Jenkins to Azure DevOps Pipeline Conversion

This directory contains Azure DevOps pipeline definitions converted from the original Jenkins pipeline files.

## Pipeline Files

1. **unit-pipeline.yml**: Converted from `Jenkinsfile.unit`
   - Runs unit tests with Gradle
   - Publishes JUnit test results

2. **integration-pipeline.yml**: Converted from `Jenkinsfile.integration`
   - Runs integration tests with Gradle
   - Publishes JUnit test results

3. **api-pipeline.yml**: Converted from `Jenkinsfile.api`
   - Runs API isolated tests with Gradle
   - Publishes JUnit test results

4. **aggregate-pipeline.yml**: Converted from `Jenkinsfile.aggregate`
   - Orchestrates the other three pipelines in sequence
   - Uses Azure DevOps stages and dependencies to create the sequential flow

## Implementation Notes

1. **Agent Specification**: 
   - Jenkins used `agent any` which means "use any available agent"
   - In Azure DevOps, we specified `ubuntu-latest` as the agent pool, but you may need to adjust this based on your Azure DevOps setup

2. **Pipeline Triggering**:
   - The individual pipelines (unit, integration, api) are set with `trigger: none` as they will be called by the aggregate pipeline
   - The aggregate pipeline is set to trigger on changes to the main branch

3. **Pipeline Dependencies**:
   - Jenkins used the `build job` step to trigger other pipelines and wait for completion
   - In Azure DevOps, we use stages with dependencies to create the same sequential flow

4. **Test Results Publishing**:
   - Jenkins used the `junit` step in the `post` section
   - Azure DevOps uses the `PublishTestResults@2` task with `condition: always()` to ensure it runs regardless of test success/failure

5. **Templates vs. Inline Steps**:
   - The aggregate pipeline example uses templates for modularity
   - You could also inline all steps if you prefer a single file approach

## Usage

To use these pipelines in Azure DevOps:

1. Create a new pipeline in Azure DevOps
2. Select "Existing Azure Pipelines YAML file"
3. Select the `aggregate-pipeline.yml` file
4. Run the pipeline

The aggregate pipeline will automatically run the unit, integration, and API tests in sequence.