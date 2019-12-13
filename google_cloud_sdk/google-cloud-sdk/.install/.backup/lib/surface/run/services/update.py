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
"""Command for updating env vars and other configuration info."""

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
from __future__ import unicode_literals

from googlecloudsdk.calliope import base
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


@base.ReleaseTracks(base.ReleaseTrack.BETA, base.ReleaseTrack.GA)
class Update(base.Command):
  """Update Cloud Run environment variables and other configuration settings.
  """

  detailed_help = {
      'DESCRIPTION': """\
          {description}
          """,
      'EXAMPLES': """\
          To update one or more env vars:

              $ {command} myservice --update-env-vars=KEY1=VALUE1,KEY2=VALUE2
         """,
  }

  @staticmethod
  def CommonArgs(parser):
    # Flags specific to managed CR
    managed_group = flags.GetManagedArgGroup(parser)
    flags.AddServiceAccountFlag(managed_group)
    flags.AddCloudSQLFlags(managed_group)

    # Flags specific to connecting to a cluster
    cluster_group = flags.GetClusterArgGroup(parser)
    flags.AddEndpointVisibilityEnum(cluster_group)
    flags.AddCpuFlag(cluster_group)

    # Flags not specific to any platform
    service_presentation = presentation_specs.ResourcePresentationSpec(
        'SERVICE',
        resource_args.GetServiceResourceSpec(prompt=True),
        'Service to update the configuration of.',
        required=True,
        prefixes=False)
    flags.AddMutexEnvVarsFlags(parser)
    flags.AddMemoryFlag(parser)
    flags.AddConcurrencyFlag(parser)
    flags.AddTimeoutFlag(parser)
    flags.AddAsyncFlag(parser)
    flags.AddLabelsFlags(parser)
    flags.AddMaxInstancesFlag(parser)
    concept_parsers.ConceptParser([service_presentation]).AddToParser(parser)

  @staticmethod
  def Args(parser):
    Update.CommonArgs(parser)

  def Run(self, args):
    """Update configuration information about the service.

    Does not change the running code.

    Args:
      args: Args!
    """
    changes = flags.GetConfigurationChanges(args)
    if not changes:
      raise exceptions.NoConfigurationChangeError(
          'No configuration change requested. '
          'Did you mean to include the flags `--update-env-vars`, '
          '`--memory`, `--concurrency`, `--timeout`, `--connectivity`?')

    conn_context = connection_context.GetConnectionContext(
        args, product=connection_context.Product.RUN)
    service_ref = flags.GetService(args)

    with serverless_operations.Connect(conn_context) as client:
      deployment_stages = stages.ServiceStages()
      with progress_tracker.StagedProgressTracker(
          'Deploying...',
          deployment_stages,
          failure_message='Deployment failed',
          suppress_output=args.async_) as tracker:
        client.ReleaseService(service_ref, changes, tracker, asyn=args.async_)
      if args.async_:
        pretty_print.Success(
            'Deploying asynchronously.')
      else:
        service = client.GetService(service_ref)
        active_revs = client.GetActiveRevisions(service_ref)

        msg = ('Service [{{bold}}{serv}{{reset}}] revision{plural} {rev_msg} '
               'is active and serving traffic at {{bold}}{url}{{reset}}')

        rev_msg = ' '.join(
            ['[{{bold}}{}{{reset}}]'.format(rev) for rev in active_revs])

        msg = msg.format(
            serv=service_ref.servicesId,
            plural='s' if len(active_revs) > 1 else '',
            rev_msg=rev_msg,
            url=service.domain if 'domain' in dir(service) else service.url)

        pretty_print.Success(msg)


@base.ReleaseTracks(base.ReleaseTrack.ALPHA)
class AlphaUpdate(Update):
  """Update Cloud Run environment variables and other configuration settings.
  """

  @staticmethod
  def Args(parser):
    Update.CommonArgs(parser)

    # Flags specific to managed CR
    managed_group = flags.GetManagedArgGroup(parser)
    flags.AddVpcConnectorArg(managed_group)
    flags.AddRevisionSuffixArg(managed_group)

    # Flags specific to connecting to a cluster
    cluster_group = flags.GetClusterArgGroup(parser)
    flags.AddSecretsFlags(cluster_group)
    flags.AddConfigMapsFlags(cluster_group)
    flags.AddHttp2Flag(cluster_group)

    # Flags not specific to any platform
    flags.AddMinInstancesFlag(parser)
    flags.AddCommandFlag(parser)
    flags.AddArgsFlag(parser)
    flags.AddPortFlag(parser)

AlphaUpdate.__doc__ = Update.__doc__
