# AWS::GreengrassV2::Deployment DeploymentComponentUpdatePolicy

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#timeoutinseconds" title="TimeoutInSeconds">TimeoutInSeconds</a>" : <i>Integer</i>,
    "<a href="#action" title="Action">Action</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#timeoutinseconds" title="TimeoutInSeconds">TimeoutInSeconds</a>: <i>Integer</i>
<a href="#action" title="Action">Action</a>: <i>String</i>
</pre>

## Properties

#### TimeoutInSeconds

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Action

_Required_: No

_Type_: String

_Allowed Values_: <code>NOTIFY_COMPONENTS</code> | <code>SKIP_NOTIFY_COMPONENTS</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
