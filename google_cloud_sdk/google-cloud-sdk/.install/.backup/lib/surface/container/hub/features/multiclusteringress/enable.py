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
"""The command to enable MultiClusterIngress Feature."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

import os
import textwrap

from googlecloudsdk.command_lib.container.hub.features import base
from googlecloudsdk.core import exceptions
from googlecloudsdk.core import properties
from googlecloudsdk.core.console import console_io


CONFIG_MEMBERSHIP_FLAG = '--config-membership'


class Enable(base.EnableCommand):
  r"""Enable MultiClusterIngress Feature.

  This command enables MultiClusterIngress Feature in Hub.

  ## Examples

  Enable MultiClusterIngress Feature

      $ {command} --config-membership=CONFIG_MEMBERSHIP
  """

  FEATURE_NAME = 'multiclusteringress'

  @classmethod
  def Args(cls, parser):
    parser.add_argument(
        CONFIG_MEMBERSHIP_FLAG,
        type=str,
        help=textwrap.dedent("""\
            Membership resource representing the Kubernetes cluster which hosts
            the MultiClusterIngress and MultiClusterService CustomResourceDefinitions.
            """),
    )

  def Run(self, args):
    project = properties.VALUES.core.project.GetOrFail()
    if not args.config_membership:
      memberships = base.ListMemberships(project)
      if not memberships:
        raise exceptions.Error('No Memberships available in GKE Hub.')
      index = console_io.PromptChoice(
          options=memberships,
          message='Please specify a config membership:\n')
      config_membership = memberships[index]
    else:
      config_membership = args.config_membership
    config_membership = ('projects/{0}/locations/global/memberships/{1}'
                         .format(project,
                                 os.path.basename(config_membership)))
    self.RunCommand(args, multiclusteringressFeatureSpec=(
        base.CreateMultiClusterIngressFeatureSpec(
            config_membership)))
