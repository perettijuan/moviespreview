# -*- coding: utf-8 -*- #
# Copyright 2017 Google LLC. All Rights Reserved.
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#    http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.
"""Command for updating managed instance group."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from googlecloudsdk.api_lib.compute import base_classes
from googlecloudsdk.api_lib.compute import managed_instance_groups_utils
from googlecloudsdk.calliope import base
from googlecloudsdk.command_lib.compute import flags
from googlecloudsdk.command_lib.compute import scope as compute_scope
from googlecloudsdk.command_lib.compute.instance_groups import flags as instance_groups_flags
from googlecloudsdk.command_lib.compute.managed_instance_groups import auto_healing_utils
import six


@base.ReleaseTracks(base.ReleaseTrack.GA)
class UpdateGA(base.UpdateCommand):
  r"""Update Google Compute Engine managed instance groups.

  *{command}* allows you to specify or modify AutoHealingPolicy for an existing
  managed instance group.

  When updating the AutoHealingPolicy, you may specify the health check, initial
  delay, or both. If the field is unspecified, its value won't be modified. If
  `--health-check` is specified, the health check will be used to monitor the
  health of your application. Whenever the health check signal for the instance
  becomes `UNHEALTHY`, the autohealing action (`RECREATE`) on an instance will
  be performed.

  If no health check is specified, the instance autohealing will be triggered by
  the instance status only (i.e. the autohealing action (`RECREATE`) on an
  instance will be performed if `instance.status` is not `RUNNING`).
  """

  @staticmethod
  def Args(parser):
    instance_groups_flags.MULTISCOPE_INSTANCE_GROUP_MANAGER_ARG.AddArgument(
        parser, operation_type='update')

    autohealing_group = parser.add_mutually_exclusive_group()
    autohealing_group.add_argument(
        '--clear-autohealing',
        action='store_true',
        default=None,
        help="""\
        Clears all autohealing policy fields for the managed instance group.
        """)
    autohealing_params_group = autohealing_group.add_group()
    auto_healing_utils.AddAutohealingArgs(autohealing_params_group)

  def _GetValidatedAutohealingPolicies(self, holder, client, args,
                                       igm_resource):
    health_check = managed_instance_groups_utils.GetHealthCheckUri(
        holder.resources, args)
    auto_healing_policies = (
        managed_instance_groups_utils.ModifyAutohealingPolicies(
            igm_resource.autoHealingPolicies, client.messages, args,
            health_check))
    managed_instance_groups_utils.ValidateAutohealingPolicies(
        auto_healing_policies)
    return auto_healing_policies

  def _MakePatchRequest(self, client, igm_ref, igm_updated_resource):
    if igm_ref.Collection() == 'compute.instanceGroupManagers':
      service = client.apitools_client.instanceGroupManagers
      request = client.messages.ComputeInstanceGroupManagersPatchRequest(
          instanceGroupManager=igm_ref.Name(),
          instanceGroupManagerResource=igm_updated_resource,
          project=igm_ref.project,
          zone=igm_ref.zone)
    else:
      service = client.apitools_client.regionInstanceGroupManagers
      request = client.messages.ComputeRegionInstanceGroupManagersPatchRequest(
          instanceGroupManager=igm_ref.Name(),
          instanceGroupManagerResource=igm_updated_resource,
          project=igm_ref.project,
          region=igm_ref.region)
    return client.MakeRequests([(service, 'Patch', request)])

  def Run(self, args):
    holder = base_classes.ComputeApiHolder(self.ReleaseTrack())
    client = holder.client
    igm_ref = (instance_groups_flags.MULTISCOPE_INSTANCE_GROUP_MANAGER_ARG
               .ResolveAsResource)(
                   args,
                   holder.resources,
                   default_scope=compute_scope.ScopeEnum.ZONE,
                   scope_lister=flags.GetDefaultScopeLister(client))

    if igm_ref.Collection() not in [
        'compute.instanceGroupManagers', 'compute.regionInstanceGroupManagers'
    ]:
      raise ValueError('Unknown reference type {0}'.format(
          igm_ref.Collection()))

    igm_resource = managed_instance_groups_utils.GetInstanceGroupManagerOrThrow(
        igm_ref, client)

    auto_healing_policies = self._GetValidatedAutohealingPolicies(
        holder, client, args, igm_resource)

    if auto_healing_policies is not None:
      return self._MakePatchRequest(
          client, igm_ref,
          client.messages.InstanceGroupManager(
              autoHealingPolicies=auto_healing_policies))


@base.ReleaseTracks(base.ReleaseTrack.BETA)
class UpdateBeta(UpdateGA):
  r"""Update Google Compute Engine managed instance groups.

  *{command}* allows you to specify or modify AutoHealingPolicy for an existing
  managed instance group.

  When updating the AutoHealingPolicy, you may specify the health check, initial
  delay, or both. If the field is unspecified, its value won't be modified. If
  `--health-check` is specified, the health check will be used to monitor the
  health of your application. Whenever the health check signal for the instance
  becomes `UNHEALTHY`, the autohealing action (`RECREATE`) on an instance will
  be performed.

  If no health check is specified, the instance autohealing will be triggered by
  the instance status only (i.e. the autohealing action (`RECREATE`) on an
  instance will be performed if `instance.status` is not `RUNNING`).
  """

  @staticmethod
  def Args(parser):
    UpdateGA.Args(parser)
    instance_groups_flags.AddMigInstanceRedistributionTypeFlag(parser)

  def Run(self, args):
    holder = base_classes.ComputeApiHolder(self.ReleaseTrack())
    client = holder.client
    igm_ref = (instance_groups_flags.MULTISCOPE_INSTANCE_GROUP_MANAGER_ARG
               .ResolveAsResource)(
                   args,
                   holder.resources,
                   default_scope=compute_scope.ScopeEnum.ZONE,
                   scope_lister=flags.GetDefaultScopeLister(client))

    if igm_ref.Collection() not in [
        'compute.instanceGroupManagers', 'compute.regionInstanceGroupManagers'
    ]:
      raise ValueError('Unknown reference type {0}'.format(
          igm_ref.Collection()))

    instance_groups_flags.ValidateMigInstanceRedistributionTypeFlag(
        args.GetValue('instance_redistribution_type'), igm_ref)

    igm_resource = managed_instance_groups_utils.GetInstanceGroupManagerOrThrow(
        igm_ref, client)

    update_policy = (managed_instance_groups_utils
                     .ApplyInstanceRedistributionTypeToUpdatePolicy)(
                         client, args.GetValue('instance_redistribution_type'),
                         igm_resource.updatePolicy)

    auto_healing_policies = self._GetValidatedAutohealingPolicies(
        holder, client, args, igm_resource)

    igm_updated_resource = client.messages.InstanceGroupManager(
        updatePolicy=update_policy)
    if auto_healing_policies is not None:
      igm_updated_resource.autoHealingPolicies = auto_healing_policies
    return self._MakePatchRequest(client, igm_ref, igm_updated_resource)


@base.ReleaseTracks(base.ReleaseTrack.ALPHA)
class UpdateAlpha(UpdateBeta):
  r"""Update Google Compute Engine managed instance groups.

  *{command}* allows you to specify or modify the StatefulPolicy and
  AutoHealingPolicy for an existing managed instance group.

  Stateful Policy defines what stateful resources should be preserved for the
  group. When instances in the group are removed or recreated, those stateful
  properties are always applied to them. This command allows you to change the
  preserved resources by adding more disks or removing existing disks and allows
  you to turn on and off preserving instance names.

  When updating the AutoHealingPolicy, you may specify the health check, initial
  delay, or both. If the field is unspecified, its value won't be modified. If
  `--health-check` is specified, the health check will be used to monitor the
  health of your application. Whenever the health check signal for the instance
  becomes `UNHEALTHY`, the autohealing action (`RECREATE`) on an instance will
  be performed.

  If no health check is specified, the instance autohealing will be triggered by
  the instance status only (i.e. the autohealing action (`RECREATE`) on an
  instance will be performed if `instance.status` is not `RUNNING`).
  """

  @staticmethod
  def Args(parser):
    UpdateBeta.Args(parser)
    instance_groups_flags.AddMigUpdateStatefulFlags(parser)

  def _MakePreservedStateDiskEntry(self, client, stateful_disk_dict):
    """Create StatefulPolicyPreservedState from a list of device names."""
    disk_device = client.messages.StatefulPolicyPreservedStateDiskDevice()
    if stateful_disk_dict.get('auto-delete'):
      disk_device.autoDelete = (
          stateful_disk_dict.get('auto-delete').GetAutoDeleteEnumValue(
              client.messages.StatefulPolicyPreservedStateDiskDevice
              .AutoDeleteValueValuesEnum))
    # Add all disk_devices to map
    return client.messages.StatefulPolicyPreservedState.DisksValue \
        .AdditionalProperty(
            key=stateful_disk_dict.get('device-name'), value=disk_device)

  def _MakeStatefulPolicyFromDisks(self, client, stateful_disks):
    """Make stateful policy proto from a list of disk protos."""
    if stateful_disks:
      return client.messages.StatefulPolicy(
          preservedState=client.messages.StatefulPolicyPreservedState(
              disks=client.messages.StatefulPolicyPreservedState.DisksValue(
                  additionalProperties=stateful_disks)))
    else:
      return client.messages.StatefulPolicy()

  def _PatchPreservedState(self, preserved_state, patch):
    """Patch the preserved state proto."""
    if patch.value.autoDelete:
      preserved_state.value.autoDelete = patch.value.autoDelete

  def _GetUpdatedStatefulPolicy(self,
                                client,
                                current_stateful_policy,
                                update_disks=None,
                                remove_device_names=None):
    """Create an updated stateful policy with the updated disk data and removed disks as specified."""
    # Extract disk protos from current stateful policy proto
    if current_stateful_policy and current_stateful_policy.preservedState \
        and current_stateful_policy.preservedState.disks:
      current_disks = current_stateful_policy \
        .preservedState.disks.additionalProperties
    else:
      current_disks = []
    # Map of disks to have in the stateful policy, after updating and removing
    # the disks specified by the update and remove flags.
    final_disks_map = {
        disk_entry.key: disk_entry for disk_entry in current_disks
    }

    # Update the disks specified in --update-stateful-disk
    for update_disk in (update_disks or []):
      device_name = update_disk.get('device-name')
      updated_preserved_state = (
          self._MakePreservedStateDiskEntry(client, update_disk))
      # Patch semantics on the `--update-stateful-disk` flag
      if device_name in final_disks_map:
        self._PatchPreservedState(final_disks_map[device_name],
                                  updated_preserved_state)
      else:
        final_disks_map[device_name] = updated_preserved_state

    # Remove the disks specified in --remove-stateful-disks
    for device_name in remove_device_names or []:
      del final_disks_map[device_name]

    stateful_disks = sorted(
        [stateful_disk for _, stateful_disk in six.iteritems(final_disks_map)],
        key=lambda x: x.key)
    return self._MakeStatefulPolicyFromDisks(client, stateful_disks)

  def _MakeUpdateRequest(self, client, igm_ref, igm_updated_resource):
    if igm_ref.Collection() == 'compute.instanceGroupManagers':
      service = client.apitools_client.instanceGroupManagers
      request = client.messages.ComputeInstanceGroupManagersUpdateRequest(
          instanceGroupManager=igm_ref.Name(),
          instanceGroupManagerResource=igm_updated_resource,
          project=igm_ref.project,
          zone=igm_ref.zone)
    else:
      service = client.apitools_client.regionInstanceGroupManagers
      request = client.messages.ComputeRegionInstanceGroupManagersUpdateRequest(
          instanceGroupManager=igm_ref.Name(),
          instanceGroupManagerResource=igm_updated_resource,
          project=igm_ref.project,
          region=igm_ref.region)
    return client.MakeRequests([(service, 'Update', request)])

  def _StatefulArgsSet(self, args):
    return (args.IsSpecified('update_stateful_disk') or
            args.IsSpecified('remove_stateful_disks'))

  def _StatefulnessIntroduced(self, args):
    return args.IsSpecified('update_stateful_disk')

  def Run(self, args):
    holder = base_classes.ComputeApiHolder(self.ReleaseTrack())
    client = holder.client
    igm_ref = (instance_groups_flags.MULTISCOPE_INSTANCE_GROUP_MANAGER_ARG
               .ResolveAsResource)(
                   args,
                   holder.resources,
                   default_scope=compute_scope.ScopeEnum.ZONE,
                   scope_lister=flags.GetDefaultScopeLister(client))

    if igm_ref.Collection() not in [
        'compute.instanceGroupManagers', 'compute.regionInstanceGroupManagers'
    ]:
      raise ValueError('Unknown reference type {0}'.format(
          igm_ref.Collection()))

    instance_groups_flags.ValidateMigInstanceRedistributionTypeFlag(
        args.GetValue('instance_redistribution_type'), igm_ref)

    igm_resource = managed_instance_groups_utils.GetInstanceGroupManagerOrThrow(
        igm_ref, client)
    if self._StatefulnessIntroduced(args):
      managed_instance_groups_utils.ValidateIgmReadyForStatefulness(
          igm_resource, client)

    device_names = instance_groups_flags.ValidateUpdateStatefulPolicyParams(
        args, igm_resource.statefulPolicy)

    update_policy = (managed_instance_groups_utils
                     .ApplyInstanceRedistributionTypeToUpdatePolicy)(
                         client, args.GetValue('instance_redistribution_type'),
                         igm_resource.updatePolicy)

    auto_healing_policies = self._GetValidatedAutohealingPolicies(
        holder, client, args, igm_resource)

    if not self._StatefulArgsSet(args):
      igm_updated_resource = client.messages.InstanceGroupManager(
          updatePolicy=update_policy)
      if auto_healing_policies is not None:
        igm_updated_resource.autoHealingPolicies = auto_healing_policies
      return self._MakePatchRequest(client, igm_ref, igm_updated_resource)

    if not device_names:
      # TODO(b/70314588): Use Patch instead of manual Update.
      if igm_resource.statefulPolicy:
        igm_resource.statefulPolicy = self._GetUpdatedStatefulPolicy(
            client, igm_resource.statefulPolicy, args.update_stateful_disk,
            args.remove_stateful_disks)
      igm_resource.updatePolicy = update_policy
      if auto_healing_policies is not None:
        igm_resource.autoHealingPolicies = auto_healing_policies
      return self._MakeUpdateRequest(client, igm_ref, igm_resource)

    stateful_policy = self._GetUpdatedStatefulPolicy(
        client, igm_resource.statefulPolicy, args.update_stateful_disk,
        args.remove_stateful_disks)
    igm_updated_resource = client.messages.InstanceGroupManager(
        statefulPolicy=stateful_policy, updatePolicy=update_policy)
    if auto_healing_policies is not None:
      igm_updated_resource.autoHealingPolicies = auto_healing_policies

    return self._MakePatchRequest(client, igm_ref, igm_updated_resource)
