# AWS::GreengrassV2::Deployment

Resource for Greengrass V2 deployment.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::GreengrassV2::Deployment",
    "Properties" : {
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
    <a href="#components" title="Components">Components</a>: <i><a href="components.md">Components</a></i>
    <a href="#iotjobconfigurations" title="IotJobConfigurations">IotJobConfigurations</a>: <i><a href="iotjobconfigurations.md">IoTJobConfigurations</a></i>
    <a href="#deploymentpolicies" title="DeploymentPolicies">DeploymentPolicies</a>: <i><a href="deploymentpolicies.md">DeploymentPolicies</a></i>
</pre>

## Properties

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

## Return Values

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### TargetName

The name of the target.

#### TargetType

The type of the target, should be either thing or thinggroup.

#### DeploymentName

The name of the deployment.

