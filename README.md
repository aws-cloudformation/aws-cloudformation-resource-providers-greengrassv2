## aws-cloudformation-resource-providers-greengrassv2

TODO: Fill this README out!

Be sure to:

* Change the title in this README
* Edit your repository description on GitHub

## Security

See [CONTRIBUTING](CONTRIBUTING.md#security-issue-notifications) for more information.

## License

This project is licensed under the Apache-2.0 License.

## Steps to build, run UTs and test in the personal account.

1. Go to CFN resource directory(e.g. `aws-greengrassv2-deployment`).
2. Make some changes to resource definition json file(e.g. `aws-greengrassv2-deployment.json`).
3. Run `cfn validate` and `cfn generate` to generate the cfn resource files.
4. Make some changes to handlers (e.g. `aws-greengrassv2-deployment/src/main/java/software/amazon/greengrassv2/deployment/CreateHandler`)
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