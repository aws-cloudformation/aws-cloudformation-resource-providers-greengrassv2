# AWS::GreengrassV2::Deployment DeploymentIoTJobConfiguration

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#jobexecutionsrolloutconfig" title="JobExecutionsRolloutConfig">JobExecutionsRolloutConfig</a>" : <i><a href="iotjobexecutionsrolloutconfig.md">IoTJobExecutionsRolloutConfig</a></i>,
    "<a href="#abortconfig" title="AbortConfig">AbortConfig</a>" : <i><a href="iotjobabortconfig.md">IoTJobAbortConfig</a></i>,
    "<a href="#timeoutconfig" title="TimeoutConfig">TimeoutConfig</a>" : <i><a href="iotjobtimeoutconfig.md">IoTJobTimeoutConfig</a></i>
}
</pre>

### YAML

<pre>
<a href="#jobexecutionsrolloutconfig" title="JobExecutionsRolloutConfig">JobExecutionsRolloutConfig</a>: <i><a href="iotjobexecutionsrolloutconfig.md">IoTJobExecutionsRolloutConfig</a></i>
<a href="#abortconfig" title="AbortConfig">AbortConfig</a>: <i><a href="iotjobabortconfig.md">IoTJobAbortConfig</a></i>
<a href="#timeoutconfig" title="TimeoutConfig">TimeoutConfig</a>: <i><a href="iotjobtimeoutconfig.md">IoTJobTimeoutConfig</a></i>
</pre>

## Properties

#### JobExecutionsRolloutConfig

_Required_: No

_Type_: <a href="iotjobexecutionsrolloutconfig.md">IoTJobExecutionsRolloutConfig</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### AbortConfig

_Required_: No

_Type_: <a href="iotjobabortconfig.md">IoTJobAbortConfig</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TimeoutConfig

_Required_: No

_Type_: <a href="iotjobtimeoutconfig.md">IoTJobTimeoutConfig</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
