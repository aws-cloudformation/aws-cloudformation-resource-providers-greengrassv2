# AWS::GreengrassV2::ComponentVersion LambdaExecutionParameters

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#eventsources" title="EventSources">EventSources</a>" : <i>[ <a href="lambdaeventsource.md">LambdaEventSource</a>, ... ]</i>,
    "<a href="#maxqueuesize" title="MaxQueueSize">MaxQueueSize</a>" : <i>Integer</i>,
    "<a href="#maxinstancescount" title="MaxInstancesCount">MaxInstancesCount</a>" : <i>Integer</i>,
    "<a href="#maxidletimeinseconds" title="MaxIdleTimeInSeconds">MaxIdleTimeInSeconds</a>" : <i>Integer</i>,
    "<a href="#timeoutinseconds" title="TimeoutInSeconds">TimeoutInSeconds</a>" : <i>Integer</i>,
    "<a href="#statustimeoutinseconds" title="StatusTimeoutInSeconds">StatusTimeoutInSeconds</a>" : <i>Integer</i>,
    "<a href="#pinned" title="Pinned">Pinned</a>" : <i>Boolean</i>,
    "<a href="#inputpayloadencodingtype" title="InputPayloadEncodingType">InputPayloadEncodingType</a>" : <i>String</i>,
    "<a href="#execargs" title="ExecArgs">ExecArgs</a>" : <i>[ String, ... ]</i>,
    "<a href="#environmentvariables" title="EnvironmentVariables">EnvironmentVariables</a>" : <i><a href="lambdaexecutionparameters-environmentvariables.md">EnvironmentVariables</a></i>,
    "<a href="#linuxprocessparams" title="LinuxProcessParams">LinuxProcessParams</a>" : <i><a href="lambdalinuxprocessparams.md">LambdaLinuxProcessParams</a></i>
}
</pre>

### YAML

<pre>
<a href="#eventsources" title="EventSources">EventSources</a>: <i>
      - <a href="lambdaeventsource.md">LambdaEventSource</a></i>
<a href="#maxqueuesize" title="MaxQueueSize">MaxQueueSize</a>: <i>Integer</i>
<a href="#maxinstancescount" title="MaxInstancesCount">MaxInstancesCount</a>: <i>Integer</i>
<a href="#maxidletimeinseconds" title="MaxIdleTimeInSeconds">MaxIdleTimeInSeconds</a>: <i>Integer</i>
<a href="#timeoutinseconds" title="TimeoutInSeconds">TimeoutInSeconds</a>: <i>Integer</i>
<a href="#statustimeoutinseconds" title="StatusTimeoutInSeconds">StatusTimeoutInSeconds</a>: <i>Integer</i>
<a href="#pinned" title="Pinned">Pinned</a>: <i>Boolean</i>
<a href="#inputpayloadencodingtype" title="InputPayloadEncodingType">InputPayloadEncodingType</a>: <i>String</i>
<a href="#execargs" title="ExecArgs">ExecArgs</a>: <i>
      - String</i>
<a href="#environmentvariables" title="EnvironmentVariables">EnvironmentVariables</a>: <i><a href="lambdaexecutionparameters-environmentvariables.md">EnvironmentVariables</a></i>
<a href="#linuxprocessparams" title="LinuxProcessParams">LinuxProcessParams</a>: <i><a href="lambdalinuxprocessparams.md">LambdaLinuxProcessParams</a></i>
</pre>

## Properties

#### EventSources

_Required_: No

_Type_: List of <a href="lambdaeventsource.md">LambdaEventSource</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MaxQueueSize

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MaxInstancesCount

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MaxIdleTimeInSeconds

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### TimeoutInSeconds

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### StatusTimeoutInSeconds

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Pinned

_Required_: No

_Type_: Boolean

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### InputPayloadEncodingType

_Required_: No

_Type_: String

_Allowed Values_: <code>json</code> | <code>binary</code>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### ExecArgs

_Required_: No

_Type_: List of String

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### EnvironmentVariables

_Required_: No

_Type_: <a href="lambdaexecutionparameters-environmentvariables.md">EnvironmentVariables</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### LinuxProcessParams

_Required_: No

_Type_: <a href="lambdalinuxprocessparams.md">LambdaLinuxProcessParams</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
