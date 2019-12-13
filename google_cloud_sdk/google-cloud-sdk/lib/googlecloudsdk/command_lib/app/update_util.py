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

"""Utilities for `gcloud app update` command."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from googlecloudsdk.api_lib.app.api import appengine_app_update_api_client
from googlecloudsdk.calliope import actions
from googlecloudsdk.calliope import arg_parsers
from googlecloudsdk.core import log
from googlecloudsdk.core.console import progress_tracker


_APP_UPDATE_COS_WARNING = """\
Starting 2019-11-01, Container-Optimized OS is the default VM image type of App Engine Flex deployments.
The flag `--no-use-container-optimized-os` is deprecated and will not have an effect.
"""


def AddAppUpdateFlags(parser, enable_use_container_optimized_os=False):
  """Add the common flags to a app update command."""

  parser.add_argument('--split-health-checks',
                      action=arg_parsers.StoreTrueFalseAction,
                      help='Enables/disables split health checks by default '
                           'on new deployments.')
  if enable_use_container_optimized_os:
    parser.add_argument('--use-container-optimized-os',
                        action=actions.DeprecationAction(
                            '--use-container-optimized-os',
                            warn=_APP_UPDATE_COS_WARNING,
                            action=arg_parsers.StoreTrueFalseAction),
                        help='Enables/disables Container-Optimized OS as Flex '
                             'base VM image by default on new deployments.')


def PatchApplication(
    release_track, split_health_checks=None, use_container_optimized_os=None):
  """Updates an App Engine application via API client.

  Args:
    release_track: The release track of the app update command to run.
    split_health_checks: Boolean, whether to enable split health checks by
      default.
    use_container_optimized_os: Boolean, whether to enable Container-Opimized
      OS as Flex base VM image by default.
  """
  api_client = appengine_app_update_api_client.GetApiClientForTrack(
      release_track)

  if use_container_optimized_os is not None:
    log.warning(_APP_UPDATE_COS_WARNING)
  if (split_health_checks is not None
      or use_container_optimized_os is not None):
    with progress_tracker.ProgressTracker(
        'Updating the app [{0}]'.format(api_client.project)):
      api_client.PatchApplication(
          split_health_checks=split_health_checks,
          use_container_optimized_os=use_container_optimized_os)
  else:
    log.status.Print('Nothing to update.')
