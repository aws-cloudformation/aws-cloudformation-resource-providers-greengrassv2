## aws-cloudformation-resource-providers-greengrassv2

TODO: Fill this README out!

Be sure to:

* Change the title in this README
* Edit your repository description on GitHub

## Security

See [CONTRIBUTING](CONTRIBUTING.md#security-issue-notifications) for more information.

## License

This project is licensed under the Apache-2.0 License.

## Steps to build, run UTs and test in the personal account (Brazil).

https://w.amazon.com/bin/view/AWS/CloudFormation/Teams/ProviderEx/RP-Framework/Projects/UluruContractTests/
```
# Configure the region and dev bucket 
export DEV_BUCKET=robewei-backups
export AWS_REGION=us-west-2

# Paste dev account credentials

# Download the contract test container
docker pull public.ecr.aws/j9c3o4f9/contract-tests:latest

# Start the contract test container. This needs to be done in its own session.
docker run --env AWS_ACCESS_KEY_ID=$AWS_ACCESS_KEY_ID \
--env AWS_SECRET_ACCESS_KEY=$AWS_SECRET_ACCESS_KEY \
--env AWS_SESSION_TOKEN=$AWS_SESSION_TOKEN \
--env AWS_DEFAULT_REGION=$AWS_REGION \
-p 9000:8080 public.ecr.aws/j9c3o4f9/contract-tests:latest

# Start a new session.
# Configure the region, dev bucket, and admin account credentials the same way as the test container session

# Build the contract tests
bb release

# Upload to the dev bucket
aws s3 cp --recursive build/packaging_additional_published_artifacts s3://$DEV_BUCKET/uluru

# Run contract tests for AWS::GreengrassV2::ComponentVersion
curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d '{"TypeName": "AWS::GreengrassV2::ComponentVersion", "Bucket": "$DEV_BUCKET", "Key": "uluru/aws-greengrassv2-componentversion.zip"}'

# Run contract tests for AWS::GreengrassV2::Deployment
curl -XPOST "http://localhost:9000/2015-03-31/functions/function/invocations" -d '{"TypeName": "AWS::GreengrassV2::Deployment", "Bucket": "$DEV_BUCKET", "Key": "uluru/aws-greengrassv2-deployment.zip"}'
```
### Troubleshooting (Brazil)

> I am unable to download the contract test container.

1. Check to make sure Docker is installed.
2. 
## Steps to build, run UTs and test in the personal account (Maven).

1. Go to CFN resource directory(e.g. `aws-greengrassv2-componentversion`).
2. Make some changes to resource definition json file(e.g. `aws-greengrassv2-componentversion.json`).
3. Run `cfn validate` and `cfn generate` to generate the cfn resource files.
4. Make some changes to handlers (e.g. `aws-greengrassv2-componentversion/src/main/java/software/amazon/greengrassv2/componentversion/CreateHandler`)
5. Run `mvn clean install -U` to build the packages and run the tests.
6. Run `mvn package` to generate the artifacts.
7. Paste credentials of the personal account to the terminal.
8. Run `cfn submit -v --region <REGION> --set-default` to register this resource type to your personal account.
Make sure replace the <REGION> with the region you want to deploy to.
In the response it may complain about the contract tests failing but we could ignore it since the resource is still under construction.
9. Prepare a cfn template which contains this new resource type.
10. Run `aws cloudformation create-stack --region <REGION> --template-body "file://<PATH_TO_TEMPLATE_FILE>" --stack-name <STACK_NAME>` to create a stack with the template.
11. After making sure it works in the personal account, we are almost ready to commit the changes.
Before that, run `pip3 install pre-commit cloudformation-cli-java-plugin` to install the pre-commit and then run `pre-commit run --all-files`.
12. If some of tests fail, the command will fix the files itself. Then, we are good to commit the changes!

### Troubleshooting (Maven)

> My GitHub Pull Request failed the automated check.

1. Make sure to run the pre-commit hook (step 11 in Steps to build).
2. If you have run it and are still getting the error, you can log into the shared `cfn-uluru+ci` account `485432771924` to view the results.

> I get the error: Fatal error compiling: java.lang.ExceptionInInitializerError: Unable to make field private com.sun.tools.javac.processing.JavacProcessingEnvironment$DiscoveredProcessors

1. Try updating the Lombok version in the `pom.xml` file.
