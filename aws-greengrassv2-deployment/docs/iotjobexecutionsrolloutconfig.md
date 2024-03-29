# AWS::GreengrassV2::Deployment IoTJobExecutionsRolloutConfig

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#exponentialrate" title="ExponentialRate">ExponentialRate</a>" : <i><a href="iotjobexponentialrolloutrate.md">IoTJobExponentialRolloutRate</a></i>,
    "<a href="#maximumperminute" title="MaximumPerMinute">MaximumPerMinute</a>" : <i>Integer</i>
}
</pre>

### YAML

<pre>
<a href="#exponentialrate" title="ExponentialRate">ExponentialRate</a>: <i><a href="iotjobexponentialrolloutrate.md">IoTJobExponentialRolloutRate</a></i>
<a href="#maximumperminute" title="MaximumPerMinute">MaximumPerMinute</a>: <i>Integer</i>
</pre>

## Properties

#### ExponentialRate

_Required_: No

_Type_: <a href="iotjobexponentialrolloutrate.md">IoTJobExponentialRolloutRate</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MaximumPerMinute

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
