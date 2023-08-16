# AWS::GreengrassV2::Cluster

Resource for Greengrass V2 cluster.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::GreengrassV2::Cluster",
    "Properties" : {
        "<a href="#clustername" title="ClusterName">ClusterName</a>" : <i>String</i>,
        "<a href="#clusterconfiguration" title="ClusterConfiguration">ClusterConfiguration</a>" : <i><a href="clusterconfiguration.md">ClusterConfiguration</a></i>,
        "<a href="#associatedmembers" title="AssociatedMembers">AssociatedMembers</a>" : <i>[ String, ... ]</i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i><a href="tags.md">Tags</a></i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::GreengrassV2::Cluster
Properties:
    <a href="#clustername" title="ClusterName">ClusterName</a>: <i>String</i>
    <a href="#clusterconfiguration" title="ClusterConfiguration">ClusterConfiguration</a>: <i><a href="clusterconfiguration.md">ClusterConfiguration</a></i>
    <a href="#associatedmembers" title="AssociatedMembers">AssociatedMembers</a>: <i>
      - String</i>
    <a href="#tags" title="Tags">Tags</a>: <i><a href="tags.md">Tags</a></i>
</pre>

## Properties

#### ClusterName

_Required_: Yes

_Type_: String

_Minimum Length_: <code>1</code>

_Maximum Length_: <code>128</code>

_Pattern_: <code>^[a-zA-Z0-9_-]+$</code>

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### ClusterConfiguration

_Required_: No

_Type_: <a href="clusterconfiguration.md">ClusterConfiguration</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AssociatedMembers

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: <a href="tags.md">Tags</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the ClusterArn.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### ClusterArn

Returns the <code>ClusterArn</code> value.

