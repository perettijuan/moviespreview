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
"""Command to create Cloud Firestore Database in Native mode."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from apitools.base.py import exceptions as apitools_exceptions
from googlecloudsdk.api_lib.app import appengine_api_client
from googlecloudsdk.calliope import base
from googlecloudsdk.command_lib.firestore import create_util
from googlecloudsdk.core import log
from googlecloudsdk.core import properties


@base.ReleaseTracks(base.ReleaseTrack.ALPHA)
class Create(base.Command):
  """Create a Google Cloud Firestore Native Database within the current Google Cloud Project."""

  detailed_help = {
      'DESCRIPTION':
          """\
          {description}
          """,
      'EXAMPLES':
          """\
          To create Google Cloud Firestore Native Database

              $ {command}

          To create an app in the us-central region, run:

              $ {command} --region=us-central

          """,
  }

  @staticmethod
  def Args(parser):
    parser.add_argument(
        '--region',
        help=('The region to create the Cloud Firestore Native Database within.'
              'Use `gcloud app regions list` to list available regions.'))

  def Run(self, args):
    api_client = appengine_api_client.GetApiClientForTrack(base.ReleaseTrack.GA)

    try:
      app = api_client.GetApplication()
      current_region = app.locationId

      if not args.region:
        raise create_util.RegionNotSpecified(
            'You must specify a region using the '
            '--region flag to use this command')
      if current_region != args.region:
        raise create_util.AppEngineAppRegionDoesNotMatch(
            'The app engine region is {app_region} which is not the same as '
            '{args_region}. Right now the Firestore region must match '
            'the App Engine region.\n'
            'Try running this command with --region={app_region}'.format(
                app_region=current_region, args_region=args.region))
      # Set the DB Type to Firestore Native (if needed)
      if (app.databaseType != api_client.messages.Application
          .DatabaseTypeValueValuesEnum.CLOUD_FIRESTORE):
        api_client.UpdateDatabaseType(
            api_client.messages.Application.DatabaseTypeValueValuesEnum
            .CLOUD_FIRESTORE)
      else:
        log.status.Print(
            'Success! The Cloud Firestore Native database was already created for'
            'created for {project}'.format(
                project=properties.VALUES.core.project.Get(required=True)))
        return

    except apitools_exceptions.HttpNotFoundError:
      if args.region is None:
        raise create_util.AppEngineAppDoesNotExist(
            'You must first create an'
            ' Google App Engine app first by running:\n'
            'gcloud app create\n'
            'The region you create the App Engine app in is '
            'the same region that the Firestore database will be created in. '
            'Once an App Engine region has been chosen it cannot be changed.')
      else:
        raise create_util.AppEngineAppDoesNotExist(
            'You must first create an'
            ' Google App Engine app in the corresponding region by running:\n'
            'gcloud app create --region={region}'.format(region=args.region))

    log.status.Print(
        'Success! The Cloud Firestore Native database has been '
        'created for {project}'.format(
            project=properties.VALUES.core.project.Get(required=True)))
