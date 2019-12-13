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
"""`gcloud domains registrations operations list` command."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from googlecloudsdk.api_lib.domains import operations
from googlecloudsdk.calliope import base
from googlecloudsdk.command_lib.domains import resource_args

_FORMAT = """\
table(
    name.basename():label=OPERATION_NAME,
    metadata.verb:label=TYPE,
    metadata.target.basename(),
    done,
    metadata.createTime.date():reverse,
    duration(start=metadata.createTime,end=metadata.endTime,precision=0,calendar=false).slice(2:).join("").yesno(no="<1S"):label=DURATION
)
"""


@base.ReleaseTracks(base.ReleaseTrack.ALPHA)
class List(base.ListCommand):
  """List Cloud Domains operations.

  Lists Cloud Domains operations in the project.

  ## EXAMPLES

  To list all Cloud Domains operations in the project, run:

    $ {command}
  """

  @staticmethod
  def Args(parser):
    resource_args.AddLocationResourceArg(parser, 'in which to list operations')
    parser.display_info.AddFormat(_FORMAT)

  def Run(self, args):
    """Run the list command."""
    client = operations.Client.FromApiVersion('v1alpha1')

    location_ref = args.CONCEPTS.location.Parse()

    # TODO(b/110077203): Add server-side filtering.
    return client.List(location_ref, args.limit, args.page_size)
