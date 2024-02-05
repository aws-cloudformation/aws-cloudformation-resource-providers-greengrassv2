# AWS::GreengrassV2::ComponentVersion ComponentDependencyRequirement

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#versionrequirement" title="VersionRequirement">VersionRequirement</a>" : <i>String</i>,
    "<a href="#dependencytype" title="DependencyType">DependencyType</a>" : <i>String</i>
}
</pre>

### YAML

<pre>
<a href="#versionrequirement" title="VersionRequirement">VersionRequirement</a>: <i>String</i>
<a href="#dependencytype" title="DependencyType">DependencyType</a>: <i>String</i>
</pre>

## Properties

#### VersionRequirement

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### DependencyType

_Required_: No

_Type_: String

_Allowed Values_: <code>SOFT</code> | <code>HARD</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
