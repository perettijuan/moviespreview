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
"""Flags and helpers for the compute instance groups commands."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from googlecloudsdk.calliope import arg_parsers
from googlecloudsdk.calliope import exceptions
from googlecloudsdk.command_lib.compute import completers
from googlecloudsdk.command_lib.compute import flags
from googlecloudsdk.command_lib.compute.instance_templates import mesh_mode_aux_data

DEFAULT_LIST_FORMAT = """\
    table(
      name,
      properties.machineType.machine_type(),
      properties.scheduling.preemptible.yesno(yes=true, no=''),
      creationTimestamp
    )"""


def MakeInstanceTemplateArg(plural=False):
  return flags.ResourceArgument(
      resource_name='instance template',
      completer=completers.InstanceTemplatesCompleter,
      plural=plural,
      global_collection='compute.instanceTemplates')


def MakeSourceInstanceArg():
  return flags.ResourceArgument(
      name='--source-instance',
      resource_name='instance',
      completer=completers.InstancesCompleter,
      required=False,
      zonal_collection='compute.instances',
      short_help=('The name of the source instance that the instance template '
                  'will be created from.\n\nYou can override machine type and '
                  'labels. Values of other flags will be ignored and values '
                  'from the source instance will be used instead.')
  )


def AddMeshModeConfigArgs(parser):
  """Adds mesh mode configuration arguments for instance templates."""
  mesh_group = parser.add_group(hidden=True)
  mesh_group.add_argument(
      '--mesh',
      hidden=True,
      type=arg_parsers.ArgDict(
          spec={
              'mode': mesh_mode_aux_data.MeshModes,
              'workload-ports': str,
          },
          required_keys=['mode']),
      help="""\
      Enables mesh and specifies mesh-level configuration.

      *mode*::: If ON, the mesh software will be installed on the instance when created.
      It will be configured to work with TrafficDirector. Allowed values of the flags are:
      ON and OFF for Alpha.

      *workload-ports*::: List of the ports inside quotes ("), separated by ';', on which the customer's workload is running.
      Used to intercept the incoming traffic to the workload. If not provided, no
      incoming traffic is intercepted.
      """)


def ValidateMeshModeFlags(args):
  """Validates the values of all mesh-mode related flags."""
  if 'startup-script' in args.metadata:
    # Extending startup-script is a temporary solution.
    # After b/143457772 is implemented,
    # we will switch to separate 'google-software-declaration' metadata key.
    if args.metadata[
        'startup-script'][:mesh_mode_aux_data
                          .shebang_len] != mesh_mode_aux_data.shebang:
      raise exceptions.InvalidArgumentException(
          'startup-script',
          'Only a bash startup-script can be used with mesh mode.')

  if 'workload-ports' in args.mesh:
    try:
      workload_ports = list(map(int, args.mesh['workload-ports'].split(';')))
      for port in workload_ports:
        if port < 1 or port > 65535:
          # valid port range is 1 - 65535
          raise ValueError
    except ValueError:
      # an invalid port is present in the list of workload ports.
      raise exceptions.InvalidArgumentException(
          'workload-ports',
          'List of ports can only contain numbers between 1 and 65535.')
