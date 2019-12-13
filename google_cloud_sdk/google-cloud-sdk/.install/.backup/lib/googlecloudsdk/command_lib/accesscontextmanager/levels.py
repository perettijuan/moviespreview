# -*- coding: utf-8 -*- #
# Copyright 2017 Google LLC. All Rights Reserved.
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
"""Command line processing utilities for access levels."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from apitools.base.py import encoding

from googlecloudsdk.api_lib.accesscontextmanager import util
from googlecloudsdk.calliope import base
from googlecloudsdk.calliope.concepts import concepts
from googlecloudsdk.command_lib.accesscontextmanager import common
from googlecloudsdk.command_lib.accesscontextmanager import policies
from googlecloudsdk.command_lib.util.apis import arg_utils
from googlecloudsdk.command_lib.util.concepts import concept_parsers
from googlecloudsdk.core import exceptions
from googlecloudsdk.core import yaml
import six

COLLECTION = 'accesscontextmanager.accessPolicies.accessLevels'


class ParseResponseError(exceptions.Error):

  def __init__(self, reason):
    super(ParseResponseError,
          self).__init__('Issue parsing response: {}'.format(reason))


class ParseError(exceptions.Error):

  def __init__(self, path, reason):
    super(ParseError, self).__init__('Issue parsing file [{}]: {}'.format(
        path, reason))


class InvalidFormatError(ParseError):

  def __init__(self, path, reason, message_class):
    valid_fields = [f.name for f in message_class.all_fields()]
    super(InvalidFormatError, self).__init__(
        path,
        ('Invalid format: {}\n\n'
         'The valid fields for the YAML objects in this file type are '
         '[{}].\n'
         'For an access level condition file, an example of the '
         'YAML-formatted list of conditions will look like:\n\n'
         ' - ipSubnetworks:\n'
         '   - 162.222.181.197/24\n'
         '   - 2001:db8::/48\n'
         ' - members:\n'
         '   - user:user@example.com\n\n'
         'For access levels file, an example of the YAML-formatted list '
         'of access levels will look like:\n\n'
         ' - name: accessPolicies/my_policy/accessLevels/my_level\n'
         '   title: My Level\n'
         '   description: Level for foo.\n'
         '   basic:\n'
         '     combiningFunction: AND\n'
         '     conditions:\n'
         '     - ipSubnetworks:\n'
         '       - 192.168.100.14/24\n'
         '       - 2001:db8::/48\n'
         '     - members\n'
         '       - user1:user1@example.com').format(reason,
                                                    ', '.join(valid_fields)))


def _LoadData(path):
  try:
    return yaml.load_path(path)
  except yaml.FileLoadError as err:
    raise ParseError(path, 'Problem loading file: {}'.format(err))
  except yaml.YAMLParseError as err:
    raise ParseError(path, 'Problem parsing data as YAML: {}'.format(err))


def _ValidateAllFieldsRecognized(path, conditions):
  unrecognized_fields = set()
  for condition in conditions:
    if condition.all_unrecognized_fields():
      unrecognized_fields.update(condition.all_unrecognized_fields())
  if unrecognized_fields:
    raise InvalidFormatError(
        path,
        'Unrecognized fields: [{}]'.format(', '.join(unrecognized_fields)),
        type(conditions[0]))


def ParseBasicLevelConditions(path):
  return ParseBasicLevelConditionsBase(path, version='v1')


def ParseBasicLevelConditionsAlpha(path):
  return ParseBasicLevelConditionsBase(path, version='v1alpha')


def ParseBasicLevelConditionsBeta(path):
  return ParseBasicLevelConditionsBase(path, version='v1beta')


def ParseBasicAccessLevelsBeta(path):
  return ParseBasicAccessLevelsBase(path, version='v1beta')


def ParseReplaceAccessLevelsResponseBeta(lro, unused_args):
  return ParseReplaceAccessLevelsResponseBase(lro, version='v1beta')


def ParseReplaceAccessLevelsResponseBase(lro, version):
  """Parse the Long Running Operation response of the ReplaceAccessLevels call.

  Args:
    lro: Long Running Operation response of ReplaceAccessLevels.
    version: version of the API. e.g. 'v1beta', 'v1'.

  Returns:
    The replacement Access Levels created by the ReplaceAccessLevels call.

  Raises:
    ParseResponseError: if the response could not be parsed into the proper
    object.
  """
  messages = util.GetMessages(version)
  message_class = messages.ReplaceAccessLevelsResponse
  try:
    return encoding.DictToMessage(
        encoding.MessageToDict(lro.response), message_class).accessLevels
  except Exception as err:
    raise ParseResponseError(six.text_type(err))


def ParseBasicLevelConditionsBase(path, version=None):
  """Parse a YAML representation of basic level conditions.

  Args:
    path: str, path to file containing basic level conditions
    version: str, api version of ACM to use for proto messages

  Returns:
    list of Condition objects.

  Raises:
    ParseError: if the file could not be read into the proper object
  """

  data = yaml.load_path(path)
  if not data:
    raise ParseError(path, 'File is empty')

  messages = util.GetMessages(version=version)
  message_class = messages.Condition
  try:
    conditions = [encoding.DictToMessage(c, message_class) for c in data]
  except Exception as err:
    raise InvalidFormatError(path, six.text_type(err), message_class)

  _ValidateAllFieldsRecognized(path, conditions)
  return conditions


def ParseBasicAccessLevelsBase(path, version=None):
  """Parse a YAML representation of a list of Access Levels with basic level conditions.

  Args:
    path: str, path to file containing basic access levels
    version: str, api version of ACM to use for proto messages

  Returns:
    list of Access Level objects.

  Raises:
    ParseError: if the file could not be read into the proper object
  """

  data = yaml.load_path(path)
  if not data:
    raise ParseError(path, 'File is empty')

  messages = util.GetMessages(version=version)
  message_class = messages.AccessLevel
  try:
    conditions = [encoding.DictToMessage(c, message_class) for c in data]
  except Exception as err:
    raise InvalidFormatError(path, six.text_type(err), message_class)

  _ValidateAllFieldsRecognized(path, conditions)
  return conditions


def GetAttributeConfig():
  return concepts.ResourceParameterAttributeConfig(
      name='level', help_text='The ID of the access level.')


def GetResourceSpec():
  return concepts.ResourceSpec(
      'accesscontextmanager.accessPolicies.accessLevels',
      resource_name='level',
      accessPoliciesId=policies.GetAttributeConfig(),
      accessLevelsId=GetAttributeConfig())


def AddResourceArg(parser, verb):
  """Add a resource argument for an access level.

  NOTE: Must be used only if it's the only resource arg in the command.

  Args:
    parser: the parser for the command.
    verb: str, the verb to describe the resource, such as 'to update'.
  """
  concept_parsers.ConceptParser.ForResource(
      'level',
      GetResourceSpec(),
      'The access level {}.'.format(verb),
      required=True).AddToParser(parser)


def GetCombineFunctionEnumMapper(version=None):
  return arg_utils.ChoiceEnumMapper(
      '--combine-function',
      util.GetMessages(
          version=version).BasicLevel.CombiningFunctionValueValuesEnum,
      custom_mappings={
          'AND': 'and',
          'OR': 'or'
      },
      required=False,
      help_str='For a basic level, determines how conditions are combined.',
  )


def AddLevelArgs(parser, version=None):
  """Add common args for level create/update commands."""
  args = [
      common.GetDescriptionArg('access level'),
      common.GetTitleArg('access level'),
      GetCombineFunctionEnumMapper(version=version).choice_arg
  ]
  for arg in args:
    arg.AddToParser(parser)


def AddLevelSpecArgs(parser, version=None):
  """Add arguments for in-file level specifications."""
  basic_level_help_text = (
      'Path to a file containing a list of basic access level conditions.\n\n'
      'An access level condition file is a YAML-formatted list of conditions, '
      'which are YAML objects representing a Condition as described in the API '
      'reference. For example:\n\n'
      '    ```\n'
      '     - ipSubnetworks:\n'
      '       - 162.222.181.197/24\n'
      '       - 2001:db8::/48\n'
      '     - members\n'
      '       - user:user@example.com\n'
      '    ```')
  basic_map = {
      'v1': ParseBasicLevelConditions,
      'v1beta': ParseBasicLevelConditionsBeta,
      'v1alpha': ParseBasicLevelConditionsAlpha
  }
  args = [
      base.Argument(
          '--basic-level-spec',
          help=basic_level_help_text,
          type=basic_map.get(version, ParseBasicLevelConditions))
  ]
  for arg in args:
    arg.AddToParser(parser)
