# AWS::GreengrassV2::Deployment

Resource for Greengrass V2 deployment.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::GreengrassV2::Deployment",
    "Properties" : {
        "<a href="#targetarn" title="TargetArn">TargetArn</a>" : <i>String</i>,
        "<a href="#parenttargetarn" title="ParentTargetArn">ParentTargetArn</a>" : <i>String</i>,
        "<a href="#deploymentname" title="DeploymentName">DeploymentName</a>" : <i>String</i>,
        "<a href="#components" title="Components">Components</a>" : <i><a href="components.md">Components</a></i>,
        "<a href="#iotjobconfiguration" title="IotJobConfiguration">IotJobConfiguration</a>" : <i><a href="deploymentiotjobconfiguration.md">DeploymentIoTJobConfiguration</a></i>,
        "<a href="#deploymentpolicies" title="DeploymentPolicies">DeploymentPolicies</a>" : <i><a href="deploymentpolicies.md">DeploymentPolicies</a></i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i><a href="tags.md">Tags</a></i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::GreengrassV2::Deployment
Properties:
    <a href="#targetarn" title="TargetArn">TargetArn</a>: <i>String</i>
    <a href="#parenttargetarn" title="ParentTargetArn">ParentTargetArn</a>: <i>String</i>
    <a href="#deploymentname" title="DeploymentName">DeploymentName</a>: <i>String</i>
    <a href="#components" title="Components">Components</a>: <i><a href="components.md">Components</a></i>
    <a href="#iotjobconfiguration" title="IotJobConfiguration">IotJobConfiguration</a>: <i><a href="deploymentiotjobconfiguration.md">DeploymentIoTJobConfiguration</a></i>
    <a href="#deploymentpolicies" title="DeploymentPolicies">DeploymentPolicies</a>: <i><a href="deploymentpolicies.md">DeploymentPolicies</a></i>
    <a href="#tags" title="Tags">Tags</a>: <i><a href="tags.md">Tags</a></i>
</pre>

## Properties

#### TargetArn

_Required_: Yes

_Type_: String

_Pattern_: <code>arn:[^:]*:iot:[^:]*:[0-9]+:(thing|thinggroup)/.+</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### ParentTargetArn

_Required_: No

_Type_: String

_Pattern_: <code>arn:[^:]*:iot:[^:]*:[0-9]+:thinggroup/.+</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### DeploymentName

_Required_: No

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>256</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Components

_Required_: No

_Type_: <a href="components.md">Components</a>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### IotJobConfiguration

_Required_: No

_Type_: <a href="deploymentiotjobconfiguration.md">DeploymentIoTJobConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### DeploymentPolicies

_Required_: No

_Type_: <a href="deploymentpolicies.md">DeploymentPolicies</a>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### Tags

_Required_: No

_Type_: <a href="tags.md">Tags</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the DeploymentId.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### DeploymentId

Returns the <code>DeploymentId</code> value.
