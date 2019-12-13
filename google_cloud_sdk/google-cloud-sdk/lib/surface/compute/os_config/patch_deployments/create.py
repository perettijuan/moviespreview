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
"""Implements command to create a new patch deployment."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from googlecloudsdk.api_lib.compute.os_config import utils as osconfig_api_utils
from googlecloudsdk.calliope import base
from googlecloudsdk.command_lib.compute.os_config import utils as osconfig_command_utils
from googlecloudsdk.core import properties


@base.ReleaseTracks(base.ReleaseTrack.BETA)
class Create(base.Command):
  """Create a patch deployment for a project."""

  detailed_help = {
      'DESCRIPTION':
          """\
      *{command}* creates a patch deployment in a project from a specified file.
      A patch deployment triggers a patch job to run at specific time(s)
      according to a schedule, and applies instance filters and other patch
      configurations to the patch job at run time. Alternatively, to run a patch
      job on-demand, see *$ gcloud*
      *beta compute os-config patch-jobs execute*.
        """,
      'EXAMPLES':
          """\
      To create a patch deployment `patch-deployment-1` in the current project,
      run:

          $ {command} patch-deployment-1 --file=path_to_config_file
      """,
  }

  @staticmethod
  def Args(parser):
    parser.add_argument(
        'PATCH_DEPLOYMENT_ID',
        type=str,
        help="""\
        ID of the patch deployment to create.

        This ID must contain only lowercase letters, numbers, and hyphens, start
        with a letter, end with a number or a letter, be between 1-63
        characters, and unique within the project.""",
    )
    parser.add_argument(
        '--file',
        required=True,
        help='The JSON or YAML file with the patch deployment to create.',
    )

  def Run(self, args):
    release_track = self.ReleaseTrack()
    client = osconfig_api_utils.GetClientInstance(release_track)
    messages = osconfig_api_utils.GetClientMessages(release_track)

    (patch_deployment,
     _) = osconfig_command_utils.GetResourceAndUpdateFieldsFromFile(
         args.file, messages.PatchDeployment)

    project = properties.VALUES.core.project.GetOrFail()
    parent_path = osconfig_command_utils.GetProjectUriPath(project)
    request = messages.OsconfigProjectsPatchDeploymentsCreateRequest(
        patchDeployment=patch_deployment,
        patchDeploymentId=args.PATCH_DEPLOYMENT_ID,
        parent=parent_path,
    )

    return client.projects_patchDeployments.Create(request)


@base.ReleaseTracks(base.ReleaseTrack.ALPHA)
class CreateAlpha(Create):
  """Create a patch deployment for a project."""

  detailed_help = {
      'DESCRIPTION':
          """\
      *{command}* creates a patch deployment in a project from a specified file.
      A patch deployment triggers a patch job to run at specific time(s)
      according to a schedule, and applies instance filters and other patch
      configurations to the patch job at run time. Alternatively, to run a patch
      job on-demand, see *$ gcloud*
      *alpha compute os-config patch-jobs execute*.
        """,
      'EXAMPLES':
          """\
      To create a patch deployment `patch-deployment-1` in the current project,
      run:

          $ {command} patch-deployment-1 --file=path_to_config_file
      """,
  }
