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
"""Additional hooks for Cloud Game Servers Scaling Policy."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from googlecloudsdk.calliope import arg_parsers
from googlecloudsdk.calliope import base
from googlecloudsdk.command_lib.game.servers import utils
from googlecloudsdk.command_lib.util.args import labels_util


def AdditionalUpdateArguments():
  return UpdateLabelsFlags() + [MatchClustersUpdateFlag(),
                                ClearMatchClustersFlag()]


def UpdateLabelsFlags():
  remove_group = base.ArgumentGroup(mutex=True)
  remove_group.AddArgument(labels_util.GetClearLabelsFlag())
  remove_group.AddArgument(labels_util.GetRemoveLabelsFlag(''))
  return [labels_util.GetUpdateLabelsFlag(''), remove_group]


def AdditionalScalingPolicyCreateArguments():
  return [MatchClustersFlag()]


def MatchClustersFlag():
  return base.Argument(
      '--match-clusters',
      action='append',
      metavar='KEY=VALUE',
      type=arg_parsers.ArgDict(),
      help="""\
      Labels to select clusters to which this Scaling Policy applies. This flag can be repeated.

      Example:
        $ {command} example-policy --match-clusters=label_a=value1,label_b=value2 --match-clusters=label_c=value3,label_d=value4

      With the above command, this policy is applicable to clusters that have
      either label_a=value1 and label_b=value2, or label_c=value3,label_d=value4.
      """)


def MatchClustersUpdateFlag():
  return base.Argument(
      '--match-clusters',
      action='append',
      metavar='KEY=VALUE',
      type=arg_parsers.ArgDict(),
      help="""\
      Labels to select clusters to which this Scaling Policy applies. This flag can be repeated.
      Specifying --match-clusters during update will fully replace existing cluster selection.

      Example:
        $ {command} example-policy --match-clusters=label_a=value1,label_b=value2 --match-clusters=label_c=value3,label_d=value4

      With the above command, this policy is applicable to clusters that have
      either label_a=value1 and label_b=value2, or label_c=value3,label_d=value4.
      """)


def ClearMatchClustersFlag():
  return base.Argument(
      '--clear-match-clusters',
      action='store_true',
      help="""\
      Remove all labels that select clusters. If `--match-clusters` is also
      specified then `--clear-match-cluster` is applied first.

      Example:
        $ {command} example-policy --clear-match-clusters
      """)


def MatchClusters(ref, args, req):
  if args.match_clusters:
    req.scalingPolicy.clusterSelectors = utils.ParseMatchClusters(
        ref, args.match_clusters)
  return req
