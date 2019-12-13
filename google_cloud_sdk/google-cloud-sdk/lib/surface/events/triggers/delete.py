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
"""Command for deleting a service."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from googlecloudsdk.calliope import base
from googlecloudsdk.command_lib.events import eventflow_operations
from googlecloudsdk.command_lib.events import resource_args
from googlecloudsdk.command_lib.run import connection_context
from googlecloudsdk.command_lib.util.concepts import concept_parsers
from googlecloudsdk.command_lib.util.concepts import presentation_specs
from googlecloudsdk.core import log
from googlecloudsdk.core.console import console_io


class Delete(base.Command):
  """Delete a trigger."""

  detailed_help = {
      'DESCRIPTION': """\
          {description}
          """,
      'EXAMPLES': """\
          To delete a trigger:

              $ {command} TRIGGER
          """,
  }

  @staticmethod
  def CommonArgs(parser):
    """Defines arguments common to all release tracks."""
    trigger_presentation = presentation_specs.ResourcePresentationSpec(
        'trigger',
        resource_args.GetTriggerResourceSpec(),
        'Name of the trigger to delete',
        required=True)
    concept_parsers.ConceptParser([trigger_presentation]).AddToParser(parser)

  @staticmethod
  def Args(parser):
    Delete.CommonArgs(parser)

  def Run(self, args):
    """Executes when the user runs the delete command."""
    conn_context = connection_context.GetConnectionContext(
        args, product=connection_context.Product.EVENTS)

    trigger_ref = args.CONCEPTS.trigger.Parse()
    console_io.PromptContinue(
        message='Trigger [{}] will be deleted.'.format(trigger_ref.Name()),
        throw_if_unattended=True,
        cancel_on_no=True)

    with eventflow_operations.Connect(conn_context) as client:
      client.DeleteTrigger(trigger_ref)
    log.DeletedResource(trigger_ref.Name(), 'trigger')
