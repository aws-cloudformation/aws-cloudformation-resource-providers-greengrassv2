# AWS::GreengrassV2::ComponentVersion LambdaContainerParams

## Syntax

To declare this entity in your AWS CloudFormation template, use the following syntax:

### JSON

<pre>
{
    "<a href="#memorysizeinkb" title="MemorySizeInKB">MemorySizeInKB</a>" : <i>Integer</i>,
    "<a href="#mountrosysfs" title="MountROSysfs">MountROSysfs</a>" : <i>Boolean</i>,
    "<a href="#volumes" title="Volumes">Volumes</a>" : <i>[ <a href="lambdavolumemount.md">LambdaVolumeMount</a>, ... ]</i>,
    "<a href="#devices" title="Devices">Devices</a>" : <i>[ <a href="lambdadevicemount.md">LambdaDeviceMount</a>, ... ]</i>
}
</pre>

### YAML

<pre>
<a href="#memorysizeinkb" title="MemorySizeInKB">MemorySizeInKB</a>: <i>Integer</i>
<a href="#mountrosysfs" title="MountROSysfs">MountROSysfs</a>: <i>Boolean</i>
<a href="#volumes" title="Volumes">Volumes</a>: <i>
      - <a href="lambdavolumemount.md">LambdaVolumeMount</a></i>
<a href="#devices" title="Devices">Devices</a>: <i>
      - <a href="lambdadevicemount.md">LambdaDeviceMount</a></i>
</pre>

## Properties

#### MemorySizeInKB

_Required_: No

_Type_: Integer

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### MountROSysfs

_Required_: No

_Type_: Boolean

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Volumes

_Required_: No

_Type_: List of <a href="lambdavolumemount.md">LambdaVolumeMount</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)

#### Devices

_Required_: No

_Type_: List of <a href="lambdadevicemount.md">LambdaDeviceMount</a>

_Update requires_: [No interruption](https://docs.aws.amazon.com/AWSCloudFormation/latest/UserGuide/using-cfn-updating-stacks-update-behaviors.html#update-no-interrupt)
