# AWS::GreengrassV2::ComponentVersion LambdaDeviceMount

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#path" title="Path">Path</a>" : <i>String</i>,
    "<a href="#permission" title="Permission">Permission</a>" : <i>String</i>,
    "<a href="#addgroupowner" title="AddGroupOwner">AddGroupOwner</a>" : <i>Boolean</i>
}
</pre>

### YAML

<pre>
<a href="#path" title="Path">Path</a>: <i>String</i>
<a href="#permission" title="Permission">Permission</a>: <i>String</i>
<a href="#addgroupowner" title="AddGroupOwner">AddGroupOwner</a>: <i>Boolean</i>
</pre>

## Properties

#### Path

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Permission

_Required_: No

_Type_: String

_Allowed Values_: <code>ro</code> | <code>rw</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AddGroupOwner

_Required_: No

_Type_: Boolean

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
