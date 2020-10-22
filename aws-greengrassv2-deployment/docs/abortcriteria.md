# AWS::GreengrassV2::Deployment AbortCriteria

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#failuretype" title="FailureType">FailureType</a>" : <i>String</i>,
    "<a href="#action" title="Action">Action</a>" : <i>String</i>,
    "<a href="#thresholdpercentage" title="ThresholdPercentage">ThresholdPercentage</a>" : <i>Double</i>,
    "<a href="#minnumberofexecutedthings" title="MinNumberOfExecutedThings">MinNumberOfExecutedThings</a>" : <i>Integer</i>
}
</pre>

### YAML

<pre>
<a href="#failuretype" title="FailureType">FailureType</a>: <i>String</i>
<a href="#action" title="Action">Action</a>: <i>String</i>
<a href="#thresholdpercentage" title="ThresholdPercentage">ThresholdPercentage</a>: <i>Double</i>
<a href="#minnumberofexecutedthings" title="MinNumberOfExecutedThings">MinNumberOfExecutedThings</a>: <i>Integer</i>
</pre>

## Properties

#### FailureType

_Required_: Yes

_Type_: String

_Allowed Values_: <code>FAILED</code> | <code>REJECTED</code> | <code>TIMED_OUT</code> | <code>ALL</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Action

_Required_: Yes

_Type_: String

_Allowed Values_: <code>CANCEL</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ThresholdPercentage

_Required_: Yes

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MinNumberOfExecutedThings

_Required_: Yes

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

