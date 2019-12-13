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
"""Command for creating files for a local development environment."""
from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from googlecloudsdk.calliope import base
from googlecloudsdk.command_lib.local import flags
from googlecloudsdk.command_lib.local import local
from googlecloudsdk.command_lib.local import local_files
from googlecloudsdk.core.util import files
import six


@base.ReleaseTracks(base.ReleaseTrack.ALPHA)
class Setup(base.Command):
  """Command for creating a skaffold local development environment."""

  @classmethod
  def Args(cls, parser):
    flags.CommonFlags(parser)

    parser.add_argument(
        '--skaffold-file',
        default='skaffold.yaml',
        required=False,
        help='Location of the generated skaffold.yaml file.')

    parser.add_argument(
        '--kubernetes-file',
        default='pods_and_services.yaml',
        help='File containing yaml specifications for kubernetes resources.')

  def Run(self, args):
    settings = local.Settings.FromArgs(args)
    local_file_generator = local_files.LocalRuntimeFiles(settings)

    with files.FileWriter(args.kubernetes_file) as output:
      output.write(six.u(local_file_generator.KubernetesConfig()))

    with files.FileWriter(args.skaffold_file) as output:
      output.write(
          six.u(local_file_generator.SkaffoldConfig(args.kubernetes_file)))
