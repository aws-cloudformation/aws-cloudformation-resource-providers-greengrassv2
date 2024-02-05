# AWS::GreengrassV2::ComponentVersion LambdaFunctionRecipeSource

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#lambdaarn" title="LambdaArn">LambdaArn</a>" : <i>String</i>,
    "<a href="#componentname" title="ComponentName">ComponentName</a>" : <i>String</i>,
    "<a href="#componentversion" title="ComponentVersion">ComponentVersion</a>" : <i>String</i>,
    "<a href="#componentplatforms" title="ComponentPlatforms">ComponentPlatforms</a>" : <i>[ <a href="componentplatform.md">ComponentPlatform</a>, ... ]</i>,
    "<a href="#componentdependencies" title="ComponentDependencies">ComponentDependencies</a>" : <i><a href="lambdafunctionrecipesource-componentdependencies.md">ComponentDependencies</a></i>,
    "<a href="#componentlambdaparameters" title="ComponentLambdaParameters">ComponentLambdaParameters</a>" : <i><a href="lambdaexecutionparameters.md">LambdaExecutionParameters</a></i>
}
</pre>

### YAML

<pre>
<a href="#lambdaarn" title="LambdaArn">LambdaArn</a>: <i>String</i>
<a href="#componentname" title="ComponentName">ComponentName</a>: <i>String</i>
<a href="#componentversion" title="ComponentVersion">ComponentVersion</a>: <i>String</i>
<a href="#componentplatforms" title="ComponentPlatforms">ComponentPlatforms</a>: <i>
      - <a href="componentplatform.md">ComponentPlatform</a></i>
<a href="#componentdependencies" title="ComponentDependencies">ComponentDependencies</a>: <i><a href="lambdafunctionrecipesource-componentdependencies.md">ComponentDependencies</a></i>
<a href="#componentlambdaparameters" title="ComponentLambdaParameters">ComponentLambdaParameters</a>: <i><a href="lambdaexecutionparameters.md">LambdaExecutionParameters</a></i>
</pre>

## Properties

#### LambdaArn

_Required_: No

_Type_: String

_Pattern_: <code>^arn:aws(-(cn|us-gov))?:lambda:(([a-z]+-)+[0-9])?:([0-9]{12})?:[^.]+$</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ComponentName

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ComponentVersion

_Required_: No

_Type_: String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ComponentPlatforms

_Required_: No

_Type_: List of <a href="componentplatform.md">ComponentPlatform</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ComponentDependencies

_Required_: No

_Type_: <a href="lambdafunctionrecipesource-componentdependencies.md">ComponentDependencies</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ComponentLambdaParameters

_Required_: No

_Type_: <a href="lambdaexecutionparameters.md">LambdaExecutionParameters</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
