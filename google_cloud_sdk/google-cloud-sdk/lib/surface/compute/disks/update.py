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
"""Command for labels update to disks."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from googlecloudsdk.api_lib.compute import base_classes
from googlecloudsdk.api_lib.compute import disks_util as api_util
from googlecloudsdk.api_lib.compute.operations import poller
from googlecloudsdk.api_lib.util import waiter
from googlecloudsdk.calliope import base
from googlecloudsdk.command_lib.compute import flags
from googlecloudsdk.command_lib.compute.disks import flags as disks_flags
from googlecloudsdk.command_lib.util.args import labels_util


@base.ReleaseTracks(
    base.ReleaseTrack.GA, base.ReleaseTrack.BETA, base.ReleaseTrack.ALPHA)
class Update(base.UpdateCommand):
  r"""Update a Google Compute Engine persistent disk."""

  DISK_ARG = None

  @classmethod
  def Args(cls, parser):
    cls.DISK_ARG = disks_flags.MakeDiskArg(plural=False)
    cls.DISK_ARG.AddArgument(parser, operation_type='update')
    labels_util.AddUpdateLabelsFlags(parser)

  def Run(self, args):
    holder = base_classes.ComputeApiHolder(self.ReleaseTrack())
    client = holder.client.apitools_client
    messages = holder.client.messages

    disk_ref = self.DISK_ARG.ResolveAsResource(
        args, holder.resources,
        scope_lister=flags.GetDefaultScopeLister(holder.client))

    labels_diff = labels_util.GetAndValidateOpsFromArgs(args)

    disk_info = api_util.GetDiskInfo(disk_ref, client, messages)
    disk = disk_info.GetDiskResource()

    set_label_req = disk_info.GetSetLabelsRequestMessage()
    labels_update = labels_diff.Apply(set_label_req.LabelsValue, disk.labels)
    request = disk_info.GetSetDiskLabelsRequestMessage(
        disk, labels_update.GetOrNone())

    if not labels_update.needs_update:
      return disk
    service = disk_info.GetService()
    operation = service.SetLabels(request)
    operation_ref = holder.resources.Parse(
        operation.selfLink, collection=disk_info.GetOperationCollection())

    operation_poller = poller.Poller(service)
    return waiter.WaitFor(
        operation_poller, operation_ref,
        'Updating labels of disk [{0}]'.format(
            disk_ref.Name()))


Update.detailed_help = {
    'DESCRIPTION':
        '*{command}* updates a Google Compute Engine persistent disk.',
    'EXAMPLES':
        """\
        To update labels 'k0' and 'k1' and remove label 'k3' of a disk, run:

            $ {command} example-disk --zone=us-central1-a --update-labels=k0=value1,k1=value2 --remove-labels=k3

            ``k0'' and ``k1'' are added as new labels if not already present.

        Labels can be used to identify the disk. To list disks with the 'k1:value2' label, run:

            $ {parent_command} list --filter='labels.k1:value2'

        To list only the labels when describing a resource, use --format to filter the result:

            $ {parent_command} describe example-disk --format='default(labels)'
        """,
}
