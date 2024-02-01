# AWS::GreengrassV2::ComponentVersion LambdaLinuxProcessParams

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#isolationmode" title="IsolationMode">IsolationMode</a>" : <i>String</i>,
    "<a href="#containerparams" title="ContainerParams">ContainerParams</a>" : <i><a href="lambdacontainerparams.md">LambdaContainerParams</a></i>
}
</pre>

### YAML

<pre>
<a href="#isolationmode" title="IsolationMode">IsolationMode</a>: <i>String</i>
<a href="#containerparams" title="ContainerParams">ContainerParams</a>: <i><a href="lambdacontainerparams.md">LambdaContainerParams</a></i>
</pre>

## Properties

#### IsolationMode

_Required_: No

_Type_: String

_Allowed Values_: <code>GreengrassContainer</code> | <code>NoContainer</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ContainerParams

_Required_: No

_Type_: <a href="lambdacontainerparams.md">LambdaContainerParams</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
