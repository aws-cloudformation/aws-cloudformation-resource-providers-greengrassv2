# AWS::GreengrassV2::Deployment

Resource for Greengrass V2 deployment.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::GreengrassV2::Deployment",
    "Properties" : {
        "<a href="#targettype" title="TargetType">TargetType</a>" : <i>String</i>,
        "<a href="#targetname" title="TargetName">TargetName</a>" : <i>String</i>,
        "<a href="#deploymentname" title="DeploymentName">DeploymentName</a>" : <i>String</i>,
        "<a href="#components" title="Components">Components</a>" : <i><a href="components.md">Components</a></i>,
        "<a href="#iotjobconfigurations" title="IotJobConfigurations">IotJobConfigurations</a>" : <i><a href="iotjobconfigurations.md">IoTJobConfigurations</a></i>,
        "<a href="#deploymentpolicies" title="DeploymentPolicies">DeploymentPolicies</a>" : <i><a href="deploymentpolicies.md">DeploymentPolicies</a></i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::GreengrassV2::Deployment
Properties:
    <a href="#targettype" title="TargetType">TargetType</a>: <i>String</i>
    <a href="#targetname" title="TargetName">TargetName</a>: <i>String</i>
    <a href="#deploymentname" title="DeploymentName">DeploymentName</a>: <i>String</i>
    <a href="#components" title="Components">Components</a>: <i><a href="components.md">Components</a></i>
    <a href="#iotjobconfigurations" title="IotJobConfigurations">IotJobConfigurations</a>: <i><a href="iotjobconfigurations.md">IoTJobConfigurations</a></i>
    <a href="#deploymentpolicies" title="DeploymentPolicies">DeploymentPolicies</a>: <i><a href="deploymentpolicies.md">DeploymentPolicies</a></i>
</pre>

## Properties

#### TargetType

The type of the target, should be either thing or thinggroup.

_Required_: Yes

_Type_: String

_Allowed Values_: <code>thing</code> | <code>thinggroup</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### TargetName

The name of the target.

_Required_: Yes

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>128</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### DeploymentName

The name of the deployment.

_Required_: Yes

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>128</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Components

_Required_: No

_Type_: <a href="components.md">Components</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### IotJobConfigurations

_Required_: No

_Type_: <a href="iotjobconfigurations.md">IoTJobConfigurations</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### DeploymentPolicies

_Required_: No

_Type_: <a href="deploymentpolicies.md">DeploymentPolicies</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
