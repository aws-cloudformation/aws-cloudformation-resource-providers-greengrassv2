# AWS::GreengrassV2::Deployment DeploymentPolicies

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#failurehandlingpolicy" title="FailureHandlingPolicy">FailureHandlingPolicy</a>" : <i>String</i>,
    "<a href="#componentupdatepolicy" title="ComponentUpdatePolicy">ComponentUpdatePolicy</a>" : <i><a href="deploymentcomponentupdatepolicy.md">DeploymentComponentUpdatePolicy</a></i>,
    "<a href="#configurationvalidationpolicy" title="ConfigurationValidationPolicy">ConfigurationValidationPolicy</a>" : <i><a href="deploymentconfigurationvalidationpolicy.md">DeploymentConfigurationValidationPolicy</a></i>
}
</pre>

### YAML

<pre>
<a href="#failurehandlingpolicy" title="FailureHandlingPolicy">FailureHandlingPolicy</a>: <i>String</i>
<a href="#componentupdatepolicy" title="ComponentUpdatePolicy">ComponentUpdatePolicy</a>: <i><a href="deploymentcomponentupdatepolicy.md">DeploymentComponentUpdatePolicy</a></i>
<a href="#configurationvalidationpolicy" title="ConfigurationValidationPolicy">ConfigurationValidationPolicy</a>: <i><a href="deploymentconfigurationvalidationpolicy.md">DeploymentConfigurationValidationPolicy</a></i>
</pre>

## Properties

#### FailureHandlingPolicy

_Required_: No

_Type_: String

_Allowed Values_: <code>ROLLBACK</code> | <code>DO_NOTHING</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ComponentUpdatePolicy

_Required_: No

_Type_: <a href="deploymentcomponentupdatepolicy.md">DeploymentComponentUpdatePolicy</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ConfigurationValidationPolicy

_Required_: No

_Type_: <a href="deploymentconfigurationvalidationpolicy.md">DeploymentConfigurationValidationPolicy</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

