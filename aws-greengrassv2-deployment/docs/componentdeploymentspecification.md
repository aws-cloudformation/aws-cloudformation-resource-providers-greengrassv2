# AWS::GreengrassV2::Deployment ComponentDeploymentSpecification

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#componentversion" title="ComponentVersion">ComponentVersion</a>" : <i>String</i>,
    "<a href="#configurationupdate" title="ConfigurationUpdate">ConfigurationUpdate</a>" : <i><a href="componentconfigurationupdate.md">ComponentConfigurationUpdate</a></i>,
    "<a href="#runwith" title="RunWith">RunWith</a>" : <i><a href="componentrunwith.md">ComponentRunWith</a></i>
}
</pre>

### YAML

<pre>
<a href="#componentversion" title="ComponentVersion">ComponentVersion</a>: <i>String</i>
<a href="#configurationupdate" title="ConfigurationUpdate">ConfigurationUpdate</a>: <i><a href="componentconfigurationupdate.md">ComponentConfigurationUpdate</a></i>
<a href="#runwith" title="RunWith">RunWith</a>: <i><a href="componentrunwith.md">ComponentRunWith</a></i>
</pre>

## Properties

#### ComponentVersion

_Required_: No

_Type_: String

_Minimum_: <code>1</code>

_Maximum_: <code>64</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ConfigurationUpdate

_Required_: No

_Type_: <a href="componentconfigurationupdate.md">ComponentConfigurationUpdate</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### RunWith

_Required_: No

_Type_: <a href="componentrunwith.md">ComponentRunWith</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
