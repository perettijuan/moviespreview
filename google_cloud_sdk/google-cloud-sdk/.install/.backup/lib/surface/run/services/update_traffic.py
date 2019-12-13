# -*- coding: utf-8 -*- #
# Copyright 2019 Google LLC. All Rights Reserved.
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
"""Command for updating env vars and other configuration info."""

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
from __future__ import unicode_literals

from googlecloudsdk.api_lib.run import traffic
from googlecloudsdk.calliope import base
from googlecloudsdk.calliope import display
from googlecloudsdk.command_lib.run import connection_context
from googlecloudsdk.command_lib.run import exceptions
from googlecloudsdk.command_lib.run import flags
from googlecloudsdk.command_lib.run import pretty_print
from googlecloudsdk.command_lib.run import resource_args
from googlecloudsdk.command_lib.run import serverless_operations
from googlecloudsdk.command_lib.run import stages
from googlecloudsdk.command_lib.util.concepts import concept_parsers
from googlecloudsdk.command_lib.util.concepts import presentation_specs
from googlecloudsdk.core.console import progress_tracker


@base.ReleaseTracks(base.ReleaseTrack.ALPHA)
class AdjustTraffic(base.Command):
  """Adjust the trafic assignments for a Cloud Run service."""

  detailed_help = {
      'DESCRIPTION':
          """\
          {description}
          """,
      'EXAMPLES':
          """\
          To assign 10% of traffic to revision myservice-s5sxn and
          90% of traffic to revision myservice-cp9kw run:

              $ {command} myservice --to-revisions=myservice-s5sxn=10,myservice-cp9kw=90

          To increase the traffic to revision myservice-s5sxn to 20% and
          by reducing the traffic to revision myservice-cp9kw to 80% run:

              $ {command} myservice --to-revisions=myservice-s5sxn=20

          To rollback to revision myservice-cp9kw run:

              $ {command} myservice --to-revisions=myservice-cp9kw=100

          To assign 100% of traffic to the current or future LATEST revision
          run:

              $ {command} myservice --to-latest

          You can also refer to the current or future LATEST revision in
          --to-revisions by the string "LATEST". For example, to set 10% of
          traffic to always float to the latest revision:

              $ {command} myservice --to-revisions=LATEST=10

         """,
  }

  @staticmethod
  def Args(parser):
    service_presentation = presentation_specs.ResourcePresentationSpec(
        'SERVICE',
        resource_args.GetServiceResourceSpec(prompt=True),
        'Service to update the configuration of.',
        required=True,
        prefixes=False)
    flags.AddAsyncFlag(parser)
    flags.AddUpdateTrafficFlags(parser)
    concept_parsers.ConceptParser([service_presentation]).AddToParser(parser)

  def _SetFormat(self, args):
    """Set display format for output.

    Args:
      args: Namespace, the args namespace
    """
    columns = [
        'displayPercent:label=TRAFFIC',
        'displayRevisionId:label=REVISION',
    ]
    args.GetDisplayInfo().AddFormat(
        'table({})'.format(','.join(columns)))

  def Run(self, args):
    """Update the traffic split for the service.

    Args:
      args: Args!

    Returns:
      List of traffic.TrafficTargetStatus instances reflecting the change.
    """
    conn_context = connection_context.GetConnectionContext(
        args, product=connection_context.Product.RUN)
    service_ref = flags.GetService(args)

    changes = flags.GetConfigurationChanges(args)
    if not changes:
      raise exceptions.NoConfigurationChangeError(
          'No traffic configuration change requested.')

    self._SetFormat(args)

    is_managed = flags.GetPlatform() == flags.PLATFORM_MANAGED
    with serverless_operations.Connect(conn_context) as client:
      deployment_stages = stages.UpdateTrafficStages()
      try:
        with progress_tracker.StagedProgressTracker(
            'Updating traffic...',
            deployment_stages,
            failure_message='Updating traffic failed',
            suppress_output=args.async_) as tracker:
          client.UpdateTraffic(service_ref, changes, tracker, args.async_)
      except:
        serv = client.GetService(service_ref)
        if serv:
          resources = traffic.GetTrafficTargetPairs(
              serv.spec.traffic,
              serv.status.traffic,
              is_managed,
              serv.status.latestReadyRevisionName)
          display.Displayer(
              self, args, resources,
              display_info=args.GetDisplayInfo()).Display()
        raise

    if args.async_:
      pretty_print.Success('Updating traffic asynchronously.')
    else:
      serv = client.GetService(service_ref)
      resources = traffic.GetTrafficTargetPairs(
          serv.spec.traffic,
          serv.status.traffic,
          is_managed,
          serv.status.latestReadyRevisionName)
      return resources
