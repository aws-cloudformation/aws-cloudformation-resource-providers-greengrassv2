# AWS::GreengrassV2::ComponentVersion LambdaEventSource

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#topic" title="Topic">Topic</a>" : <i>String</i>,
    "<a href="#type" title="Type">Type</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#topic" title="Topic">Topic</a>: <i>String</i>
<a href="#type" title="Type">Type</a>: <i>String</i>
</pre>

## Properties

#### Topic

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Type

_Required_: No

_Type_: String

_Allowed Values_: <code>PUB_SUB</code> | <code>IOT_CORE</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
