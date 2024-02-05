# AWS::GreengrassV2::ComponentVersion

Resource for Greengrass component version.

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "Type" : "AWS::GreengrassV2::ComponentVersion",
    "Properties" : {
        "<a href="#inlinerecipe" title="InlineRecipe">InlineRecipe</a>" : <i>String</i>,
        "<a href="#lambdafunction" title="LambdaFunction">LambdaFunction</a>" : <i><a href="lambdafunctionrecipesource.md">LambdaFunctionRecipeSource</a></i>,
        "<a href="#tags" title="Tags">Tags</a>" : <i><a href="tags.md">Tags</a></i>
    }
}
</pre>

### YAML

<pre>
Type: AWS::GreengrassV2::ComponentVersion
Properties:
    <a href="#inlinerecipe" title="InlineRecipe">InlineRecipe</a>: <i>String</i>
    <a href="#lambdafunction" title="LambdaFunction">LambdaFunction</a>: <i><a href="lambdafunctionrecipesource.md">LambdaFunctionRecipeSource</a></i>
    <a href="#tags" title="Tags">Tags</a>: <i><a href="tags.md">Tags</a></i>
</pre>

## Properties

#### InlineRecipe

_Required_: No

_Type_: String

_Update requires_: [Replacement](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-replacement)

#### LambdaFunction

_Required_: No

_Type_: <a href="lambdafunctionrecipesource.md">LambdaFunctionRecipeSource</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Tags

_Required_: No

_Type_: <a href="tags.md">Tags</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

## Return Values

### Ref

When you pass the logical ID of this resource to the intrinsic `Ref` function, Ref returns the Arn.

### Fn::GetAtt

The `Fn::GetAtt` intrinsic function returns a value for a specified attribute of this type. The following are the available attributes and sample return values.

For more information about using the `Fn::GetAtt` intrinsic function, see [Fn::GetAtt](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/intrinsic-function-reference-getatt.html).

#### Arn

Returns the <code>Arn</code> value.

#### ComponentName

Returns the <code>ComponentName</code> value.

#### ComponentVersion

Returns the <code>ComponentVersion</code> value.
