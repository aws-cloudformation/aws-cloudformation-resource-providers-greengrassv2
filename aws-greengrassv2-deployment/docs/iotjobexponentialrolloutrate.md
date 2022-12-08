# AWS::GreengrassV2::Deployment IoTJobExponentialRolloutRate

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#baserateperminute" title="BaseRatePerMinute">BaseRatePerMinute</a>" : <i>Integer</i>,
    "<a href="#incrementfactor" title="IncrementFactor">IncrementFactor</a>" : <i>Double</i>,
    "<a href="#rateincreasecriteria" title="RateIncreaseCriteria">RateIncreaseCriteria</a>" : <i><a href="iotjobrateincreasecriteria.md">IoTJobRateIncreaseCriteria</a></i>
}
</pre>

### YAML

<pre>
<a href="#baserateperminute" title="BaseRatePerMinute">BaseRatePerMinute</a>: <i>Integer</i>
<a href="#incrementfactor" title="IncrementFactor">IncrementFactor</a>: <i>Double</i>
<a href="#rateincreasecriteria" title="RateIncreaseCriteria">RateIncreaseCriteria</a>: <i><a href="iotjobrateincreasecriteria.md">IoTJobRateIncreaseCriteria</a></i>
</pre>

## Properties

#### BaseRatePerMinute

_Required_: Yes

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### IncrementFactor

_Required_: Yes

_Type_: Double

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### RateIncreaseCriteria

_Required_: Yes

_Type_: <a href="iotjobrateincreasecriteria.md">IoTJobRateIncreaseCriteria</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

