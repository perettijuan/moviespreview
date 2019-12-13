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
"""Command to analyze IAM policy in the specified root asset."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from googlecloudsdk.api_lib.asset import client_util
from googlecloudsdk.calliope import arg_parsers
from googlecloudsdk.calliope import base


def AddOrganizationArgs(parser):
  parser.add_argument(
      '--organization',
      metavar='ORGANIZATION_ID',
      required=True,
      help='The organization ID to perform the analysis.')


def AddResourceSelectorGroup(parser):
  resource_selector_group = parser.add_group(
      mutex=False,
      required=False,
      help='Specifies a resource for analysis. Leaving it empty means ANY.')
  AddFullResourceNameArgs(resource_selector_group)


def AddFullResourceNameArgs(parser):
  parser.add_argument('--full-resource-name', help='The full resource name.')


def AddIdentitySelectorGroup(parser):
  identity_selector_group = parser.add_group(
      mutex=False,
      required=False,
      help='Specifies an identity for analysis. Leaving it empty means ANY.')
  AddIdentityArgs(identity_selector_group)


def AddIdentityArgs(parser):
  parser.add_argument(
      '--identity',
      help=('The identity appearing in the form of members in the IAM policy '
            'binding.'))


def AddAccessSelectorGroup(parser):
  access_selector_group = parser.add_group(
      mutex=False,
      required=False,
      help=('Specifies roles or permissions for analysis. Leaving it empty '
            'means ANY.'))
  AddRolesArgs(access_selector_group)
  AddPermissionsArgs(access_selector_group)


def AddRolesArgs(parser):
  parser.add_argument(
      '--roles',
      metavar='ROLES',
      type=arg_parsers.ArgList(),
      help='The roles to appear in the result.')


def AddPermissionsArgs(parser):
  parser.add_argument(
      '--permissions',
      metavar='PERMISSIONS',
      type=arg_parsers.ArgList(),
      help='The permissions to appear in the result.')


def AddOptionsGroup(parser):
  options_group = parser.add_group(
      mutex=False, required=False, help='The analysis options.')
  AddExpandGroupsArgs(options_group)
  AddExpandRolesArgs(options_group)
  AddExpandResourcesArgs(options_group)
  AddOutputResourceEdgesArgs(options_group)
  AddOutputGroupEdgesArgs(options_group)
  AddOutputPartialResultBeforeTimeoutArgs(options_group)


def AddExpandGroupsArgs(parser):
  parser.add_argument(
      '--expand-groups',
      action='store_true',
      help=(
          'If true, the identities section of the result will expand any '
          'Google groups appearing in an IAM policy binding. Default is false.'
      ))
  parser.set_defaults(expand_groups=False)


def AddExpandRolesArgs(parser):
  parser.add_argument(
      '--expand-roles',
      action='store_true',
      help=(
          'If true, the access section of result will expand any roles '
          'appearing in IAM policy bindings to include their permissions. '
          'Default is false.'))
  parser.set_defaults(expand_roles=False)


def AddExpandResourcesArgs(parser):
  parser.add_argument(
      '--expand-resources',
      action='store_true',
      help=(
          'If true, the resource section of the result will expand any '
          'resource attached to an IAM policy to include resources lower in '
          'the resource hierarchy. Default is false.'))
  parser.set_defaults(expand_resources=False)


def AddOutputResourceEdgesArgs(parser):
  parser.add_argument(
      '--output-resource-edges',
      action='store_true',
      help=(
          'If true, the result will output resource edges, starting '
          'from the policy attached resource, to any expanded resources. '
          'Default is false.'))
  parser.set_defaults(output_resource_edges=False)


def AddOutputGroupEdgesArgs(parser):
  parser.add_argument(
      '--output-group-edges',
      action='store_true',
      help=(
          "If true, the result will output group identity edges, starting "
          "from the binding's group members, to any expanded identities. "
          "Default is false."))
  parser.set_defaults(output_group_edges=False)


def AddOutputPartialResultBeforeTimeoutArgs(parser):
  parser.add_argument(
      '--output-partial-result-before-timeout',
      action='store_true',
      help=(
          'If true, you will get a response with a partial result instead of '
          'a DEADLINE_EXCEEDED error when your request processing takes longer '
          'than the deadline. Default is false.'))
  parser.set_defaults(output_partial_result_before_timeout=False)


@base.ReleaseTracks(base.ReleaseTrack.ALPHA)
class AnalyzeIamPolicy(base.Command):
  """Analyzes accessible IAM policies that match a request."""

  detailed_help = {
      'DESCRIPTION':
          """\
      Analyzes accessible IAM policies that match a request.
      """,
      'EXAMPLES':
          """\
          To find out who has iam.serviceAccounts.actAs permission permission on a
          specified service account, run:

          $ {command} --organization=YOUR_ORG_ID
          --full-resource-name='//iam.googleapis.com/projects/YOUR_PROJ_ID/serviceAccounts/YOUR_SERVICE_ACCOUNT'
          --permissions='iam.serviceAccounts.actAs'
          --output-partial-result-before-timeout

          To find out which resources a specified user can access, run:

          $ {command} --organization=YOUR_ORG_ID
          --identity='user:u1@foo.com' --output-partial-result-before-timeout

          To find out which accesses (roles or permissions) a specified user has
          on a specified project, run:

          $ {command} --organization=YOUR_ORG_ID
          --full-resource-name='//cloudresourcemanager.googleapis.com/projects/YOUR_PROJ_ID'
          --identity='user:u1@foo.com' --output-partial-result-before-timeout
      """
  }

  @staticmethod
  def Args(parser):
    AddOrganizationArgs(parser)
    AddResourceSelectorGroup(parser)
    AddIdentitySelectorGroup(parser)
    AddAccessSelectorGroup(parser)
    AddOptionsGroup(parser)

  def Run(self, args):
    return client_util.MakeAnalyzeIamPolicyHttpRequests(args)
