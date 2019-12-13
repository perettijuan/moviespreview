# -*- coding: utf-8 -*- #
# Copyright 2018 Google LLC. All Rights Reserved.
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
"""Describe network endpoint groups command."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from googlecloudsdk.api_lib.compute import base_classes
from googlecloudsdk.calliope import base
from googlecloudsdk.command_lib.compute import flags as compute_flags
from googlecloudsdk.command_lib.compute import scope as compute_scope
from googlecloudsdk.command_lib.compute.network_endpoint_groups import flags

DETAILED_HELP = {
    'EXAMPLES': """
To describe a network endpoint group:

  $ {command} my-neg --zone=us-central1-a
""",
}


@base.ReleaseTracks(base.ReleaseTrack.BETA, base.ReleaseTrack.GA)
class Describe(base.DescribeCommand):
  """Describe a Google Compute Engine network endpoint group."""

  detailed_help = DETAILED_HELP

  @staticmethod
  def Args(parser):
    flags.MakeNetworkEndpointGroupsArg().AddArgument(parser)

  def _Run(self, args, support_global_scope=False):
    holder = base_classes.ComputeApiHolder(self.ReleaseTrack())
    client = holder.client

    neg_ref = flags.MakeNetworkEndpointGroupsArg(
        support_global_scope=support_global_scope).ResolveAsResource(
            args,
            holder.resources,
            default_scope=compute_scope.ScopeEnum.ZONE,
            scope_lister=compute_flags.GetDefaultScopeLister(holder.client))

    messages = holder.client.messages
    if hasattr(neg_ref, 'zone'):
      request = messages.ComputeNetworkEndpointGroupsGetRequest(
          networkEndpointGroup=neg_ref.Name(),
          project=neg_ref.project,
          zone=neg_ref.zone)

      service = holder.client.apitools_client.networkEndpointGroups
    else:
      request = messages.ComputeGlobalNetworkEndpointGroupsGetRequest(
          networkEndpointGroup=neg_ref.Name(), project=neg_ref.project)
      service = holder.client.apitools_client.globalNetworkEndpointGroups

    return client.MakeRequests([(service, 'Get', request)])[0]

  def Run(self, args):
    return self._Run(args)


@base.ReleaseTracks(base.ReleaseTrack.ALPHA)
class DescribeAlpha(Describe):
  """Describe a Google Compute Engine network endpoint group."""

  @staticmethod
  def Args(parser):
    flags.MakeNetworkEndpointGroupsArg(
        support_global_scope=True).AddArgument(parser)

  def Run(self, args):
    return self._Run(args, support_global_scope=True)
