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
"""Print credential settings to add to the .npmrc file."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from googlecloudsdk.calliope import base
from googlecloudsdk.command_lib.build_artifacts import flags
from googlecloudsdk.command_lib.build_artifacts import settings_util


class Npm(base.Command):
  """Print credential settings to add to the .npmrc file.

  Print credential settings to add to the .npmrc file for connecting to an npm
  repository.
  """

  @staticmethod
  def Args(parser):
    flags.GetRepoFlag().AddToParser(parser)
    flags.GetScopeFlag().AddToParser(parser)
    parser.display_info.AddFormat('value(npm)')

  def Run(self, args):
    """This is what gets called when the user runs this command.

    Args:
      args: an argparse namespace. All the arguments that were provided to this
        command invocation.

    Returns:
      An npm settings snippet.
    """

    return {'npm': settings_util.GetNpmSettingsSnippet(args)}
