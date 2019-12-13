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
"""Deploy a container to Cloud Run."""

from __future__ import absolute_import
from __future__ import division
from __future__ import print_function
from __future__ import unicode_literals

from googlecloudsdk.calliope import base
from googlecloudsdk.command_lib.run import config_changes as config_changes_mod
from googlecloudsdk.command_lib.run import connection_context
from googlecloudsdk.command_lib.run import flags
from googlecloudsdk.command_lib.run import pretty_print
from googlecloudsdk.command_lib.run import resource_args
from googlecloudsdk.command_lib.run import serverless_operations
from googlecloudsdk.command_lib.run import stages
from googlecloudsdk.command_lib.util.concepts import concept_parsers
from googlecloudsdk.command_lib.util.concepts import presentation_specs
from googlecloudsdk.core.console import progress_tracker


def GetAllowUnauth(args, operations, service_ref, service_exists):
  """Returns allow_unauth value for a service change.

  Args:
    args: argparse.Namespace, Command line arguments
    operations: serverless_operations.ServerlessOperations, Serverless client.
    service_ref: protorpc.messages.Message, A resource reference object
      for the service See googlecloudsdk.core.resources.Registry.ParseResourceId
      for details.
    service_exists: True if the service being changed already exists.
  Returns:
    allow_unauth value where
     True means to enable unauthenticated acess for the service.
     False means to disable unauthenticated access for the service.
     None means to retain the current value for the service.
  """
  allow_unauth = None
  if flags.GetPlatform() == flags.PLATFORM_MANAGED:
    allow_unauth = flags.GetAllowUnauthenticated(
        args, operations, service_ref, not service_exists)
    # Avoid failure removing a policy binding for a service that
    # doesn't exist.
    if not service_exists and not allow_unauth:
      allow_unauth = None
  return allow_unauth


def GetStartDeployMessage(conn_context, service_ref):
  """Returns a user mesage for starting a deploy.

  Args:
    conn_context: connection_context.ConnectionInfo, Metadata for the
      run API client.
    service_ref: protorpc.messages.Message, A resource reference object
      for the service See googlecloudsdk.core.resources.Registry.ParseResourceId
      for details.
  """
  msg = ('Deploying container to {operator} service '
         '[{{bold}}{service}{{reset}}] in {ns_label} [{{bold}}{ns}{{reset}}]')
  msg += conn_context.location_label

  return msg.format(
      operator=conn_context.operator,
      ns_label=conn_context.ns_label,
      service=service_ref.servicesId,
      ns=service_ref.namespacesId)


def GetSuccessMessageForSynchronousDeploy(operations, service_ref):
  """Returns a user message for a successful synchronous deploy.

  Args:
    operations: serverless_operations.ServerlessOperations, A
      ServerlessOperations instance for fetching the service.
    service_ref: protorpc.messages.Message, A resource reference object
      for the service See googlecloudsdk.core.resources.Registry.ParseResourceId
      for details.
  """
  service = operations.GetService(service_ref)
  latest_ready = service.status.latestReadyRevisionName
  latest_percent_traffic = sum(
      target.percent for target in service.status.traffic
      if target.latestRevision or (
          latest_ready and target.revisionName == latest_ready))
  msg = (
      'Service [{{bold}}{serv}{{reset}}] '
      'revision [{{bold}}{rev}{{reset}}] '
      'has been deployed')
  if latest_percent_traffic:
    msg += (
        ' and is serving {{bold}}{latest_percent_traffic}{{reset}} '
        'percent of traffic at {{bold}}{url}{{reset}}')
  return msg.format(
      serv=service_ref.servicesId,
      rev=latest_ready,
      url=service.domain,
      latest_percent_traffic=latest_percent_traffic)


@base.ReleaseTracks(base.ReleaseTrack.BETA, base.ReleaseTrack.GA)
class Deploy(base.Command):
  """Deploy a container to Cloud Run."""

  detailed_help = {
      'DESCRIPTION': """\
          Deploys container images to Google Cloud Run.
          """,
      'EXAMPLES': """\
          To deploy a container to the service `my-backend` on Cloud Run:

              $ {command} my-backend --image=gcr.io/my/image

          You may also omit the service name. Then a prompt will be displayed
          with a suggested default value:

              $ {command} --image=gcr.io/my/image

          To deploy to Cloud Run on Kubernetes Engine, you need to specify a cluster:

              $ {command} --image=gcr.io/my/image --cluster=my-cluster
          """,
  }

  @staticmethod
  def CommonArgs(parser):
    # Flags specific to managed CR
    managed_group = flags.GetManagedArgGroup(parser)
    flags.AddAllowUnauthenticatedFlag(managed_group)
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
        'Service to deploy to.',
        required=True,
        prefixes=False)
    flags.AddImageArg(parser)
    flags.AddFunctionArg(parser)
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
    Deploy.CommonArgs(parser)

  def Run(self, args):
    """Deploy a container to Cloud Run."""
    image = args.image

    conn_context = connection_context.GetConnectionContext(
        args, product=connection_context.Product.RUN)
    config_changes = flags.GetConfigurationChanges(args)

    service_ref = flags.GetService(args)

    with serverless_operations.Connect(conn_context) as operations:
      image_change = config_changes_mod.ImageChange(image)
      changes = [image_change]
      if config_changes:
        changes.extend(config_changes)
      exists = operations.GetService(service_ref)
      allow_unauth = GetAllowUnauth(args, operations, service_ref, exists)

      pretty_print.Info(GetStartDeployMessage(conn_context, service_ref))

      deployment_stages = stages.ServiceStages(allow_unauth is not None)
      header = 'Deploying...' if exists else 'Deploying new service...'
      with progress_tracker.StagedProgressTracker(
          header,
          deployment_stages,
          failure_message='Deployment failed',
          suppress_output=args.async_) as tracker:
        operations.ReleaseService(
            service_ref,
            changes,
            tracker,
            asyn=args.async_,
            allow_unauthenticated=allow_unauth)
      if args.async_:
        pretty_print.Success(
            'Service [{{bold}}{serv}{{reset}}] is deploying '
            'asynchronously.'.format(serv=service_ref.servicesId))
      else:
        pretty_print.Success(GetSuccessMessageForSynchronousDeploy(
            operations, service_ref))


@base.ReleaseTracks(base.ReleaseTrack.ALPHA)
class AlphaDeploy(Deploy):
  """Deploy a container to Cloud Run."""

  @staticmethod
  def Args(parser):
    Deploy.CommonArgs(parser)

    # Flags specific to managed CR
    managed_group = flags.GetManagedArgGroup(parser)
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
    flags.AddNoTrafficFlag(parser)
    flags.AddPortFlag(parser)

AlphaDeploy.__doc__ = Deploy.__doc__
