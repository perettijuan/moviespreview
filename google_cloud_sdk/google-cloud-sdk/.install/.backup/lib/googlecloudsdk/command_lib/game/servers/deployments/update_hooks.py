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
"""Update hooks for Cloud Game Servers Config."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from googlecloudsdk.command_lib.game.servers import utils
from googlecloudsdk.core import exceptions
from googlecloudsdk.core import log
from googlecloudsdk.core import properties
from googlecloudsdk.core import resources


class NoFieldsSpecifiedError(exceptions.Error):
  """Error if no fields are specified for a patch request."""


class PreviewTimeFieldNotRelevantError(exceptions.Error):
  """Error if preview-time is specified with dry-run false."""


def ConvertOutput(response, args):
  if not args.dry_run:
    utils.WaitForOperation(response)
    log.status.Print('Updated rollout for: [{}]'.format(args.deployment))
    return GetExistingResource(args)

  return response


def GetResourceRef(args):
  project = properties.VALUES.core.project.Get(required=True)
  location = args.location
  ref = resources.REGISTRY.Create(
      'gameservices.projects.locations.gameServerDeployments',
      projectsId=project,
      locationsId=location, gameServerDeploymentsId=args.deployment)
  return ref


def GetExistingResource(args):
  resource_ref = GetResourceRef(args)
  api_version = resource_ref.GetCollectionInfo().api_version
  get_request_message = GetRequestMessage(resource_ref)
  orig_resource = utils.GetClient(
      api_version).projects_locations_gameServerDeployments.GetRollout(
          get_request_message)
  return orig_resource


def GetRequestMessage(resource_ref):
  return utils.GetApiMessage(
      resource_ref
  ).GameservicesProjectsLocationsGameServerDeploymentsGetRolloutRequest(
      name=resource_ref.RelativeName())


def ChooseUpdateOrPreviewMethod(unused_instance_ref, args):
  if args.dry_run:
    return 'previewRollout'

  if args.preview_time:
    raise PreviewTimeFieldNotRelevantError(
        '`--preview-time` is only relevant if `--dry-run` is set to true.')
  log.status.Print('Update rollout request issued for: [{}]'.format(
      args.deployment))
  return 'updateRollout'


def SetUpdateMask(ref, args, request):
  """Python hook that computes the update mask for a patch request.

  Args:
    ref: The rollout resource reference.
    args: The parsed args namespace.
    request: The update rollout request.

  Returns:
    Request with update mask set appropriately.
  Raises:
    NoFieldsSpecifiedError: If no fields were provided for updating.
  """
  del ref
  update_mask = []

  if args.default_config:
    update_mask.append('defaultGameServerConfig')
  if args.config_overrides_file or args.clear_config_overrides:
    update_mask.append('gameServerConfigOverrides')

  if not args.dry_run and not update_mask:
    raise NoFieldsSpecifiedError(
        'Must specify at least one parameter to update.')

  request.updateMask = ','.join(update_mask)
  return request


def ClearConfigOverrides(ref, args, request):
  del ref
  if args.clear_config_overrides:
    if not request.gameServerDeploymentRollout:
      request.gameServerDeploymentRollout = {}
    request.gameServerDeploymentRollout.gameServerConfigOverrides = []
  return request
