# -*- coding: utf-8 -*- #
# Copyright 2016 Google LLC. All Rights Reserved.
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
"""Command for spanner operations list."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

import textwrap

from googlecloudsdk.api_lib.spanner import database_operations
from googlecloudsdk.api_lib.spanner import instance_operations
from googlecloudsdk.calliope import base
from googlecloudsdk.command_lib.spanner import flags


class List(base.ListCommand):
  """List the Cloud Spanner operations on the given instance or database."""

  detailed_help = {
      'EXAMPLES':
          textwrap.dedent("""\
        To list Cloud Spanner operations for an instance, run:

          $ {command} --instance=my-instance-id

        To list Cloud Spanner operations for a database, run:

          $ {command} --instance=my-instance-id --database=my-database-id
        """),
  }

  @staticmethod
  def Args(parser):
    """Args is called by calliope to gather arguments for this command.

    Please add arguments in alphabetical order except for no- or a clear-
    pair for that argument which can follow the argument itself.
    Args:
      parser: An argparse parser that you can use to add arguments that go
          on the command line after this command. Positional arguments are
          allowed.
    """
    flags.Instance(
        positional=False,
        text='The ID of the instance the operations are executing on.'
        ).AddToParser(parser)
    flags.Database(positional=False, required=False,
                   text='For database operations, the name of the database '
                   'the operations are executing on.').AddToParser(parser)
    parser.display_info.AddFormat("""
          table(
            name.basename():label=OPERATION_ID,
            metadata.statements.join(sep="\n"),
            done,
            metadata.'@type'.split('.').slice(-1:).join()
          )
        """)
    parser.display_info.AddCacheUpdater(None)

  def Run(self, args):
    """This is what gets called when the user runs this command.

    Args:
      args: an argparse namespace. All the arguments that were provided to this
        command invocation.

    Returns:
      Some value that we want to have printed later.
    """
    if args.database:
      return database_operations.List(args.instance, args.database)
    else:
      return instance_operations.List(args.instance)
