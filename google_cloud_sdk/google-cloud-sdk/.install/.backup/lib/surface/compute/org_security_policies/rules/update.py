# -*- coding: utf-8 -*- #
# Copyright 2019 Google Inc. All Rights Reserved.
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
"""Command for updating security policies rules."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from googlecloudsdk.api_lib.compute import base_classes
from googlecloudsdk.api_lib.compute import org_security_policy_rule_utils as rule_utils
from googlecloudsdk.api_lib.compute.org_security_policies import client
from googlecloudsdk.calliope import base
from googlecloudsdk.command_lib.compute.org_security_policies import flags


@base.ReleaseTracks(base.ReleaseTrack.ALPHA)
class Update(base.UpdateCommand):
  r"""Update a Google Compute Engine security policy rule.

  *{command}* is used to update organization security policy rules.
  """

  ORG_SECURITY_POLICY_ARG = None

  @classmethod
  def Args(cls, parser):
    cls.ORG_SECURITY_POLICY_ARG = flags.OrgSecurityPolicyRuleArgument(
        required=True, operation='update')
    cls.ORG_SECURITY_POLICY_ARG.AddArgument(parser)
    flags.AddAction(parser, required=False)
    flags.AddSecurityPolicyId(parser, operation='updated')
    flags.AddSrcIpRanges(parser)
    flags.AddDestIpRanges(parser)
    flags.AddDestPorts(parser)
    flags.AddDirection(parser)
    flags.AddEnableLogging(parser)
    flags.AddTargetResources(parser)
    flags.AddDescription(parser)
    flags.AddNewPriority(parser, operation='update')

  def Run(self, args):
    holder = base_classes.ComputeApiHolder(self.ReleaseTrack())
    ref = self.ORG_SECURITY_POLICY_ARG.ResolveAsResource(
        args, holder.resources, with_project=False)
    security_policy_rule_client = client.OrgSecurityPolicyRule(
        ref=ref, compute_client=holder.client)
    priority = rule_utils.ConvertPriorityToInt(ref.Name())
    src_ip_ranges = []
    dest_ip_ranges = []
    dest_port_list = []
    target_resources = []
    enable_logging = False
    should_setup_match = False
    traffic_direct = None
    matcher = None
    if args.IsSpecified('src_ip_ranges'):
      src_ip_ranges = args.src_ip_ranges
      should_setup_match = True
    if args.IsSpecified('dest_ip_ranges'):
      dest_ip_ranges = args.dest_ip_ranges
      should_setup_match = True
    if args.IsSpecified('dest_ports'):
      dest_port_list = rule_utils.ParseDestPorts(args.dest_ports,
                                                 holder.client.messages)
      should_setup_match = True
    if args.IsSpecified('target_resources'):
      target_resources = args.target_resources
    if args.IsSpecified('enable_logging'):
      enable_logging = True
    if args.IsSpecified('new_priority'):
      new_priority = rule_utils.ConvertPriorityToInt(args.new_priority)
    else:
      new_priority = priority

    # If need to construct a new matcher.
    if should_setup_match:
      matcher = holder.client.messages.SecurityPolicyRuleMatcher(
          versionedExpr=holder.client.messages.SecurityPolicyRuleMatcher
          .VersionedExprValueValuesEnum.FIREWALL,
          config=holder.client.messages.SecurityPolicyRuleMatcherConfig(
              srcIpRanges=src_ip_ranges,
              destIpRanges=dest_ip_ranges,
              destPorts=dest_port_list))
    if args.IsSpecified('direction'):
      if args.direction == 'INGRESS':
        traffic_direct = holder.client.messages.SecurityPolicyRule.DirectionValueValuesEnum.INGRESS
      else:
        traffic_direct = holder.client.messages.SecurityPolicyRule.DirectionValueValuesEnum.EGRESS

    security_policy_rule = holder.client.messages.SecurityPolicyRule(
        priority=new_priority,
        action=args.action,
        match=matcher,
        direction=traffic_direct,
        targetResources=target_resources,
        description=args.description,
        enableLogging=enable_logging)

    return security_policy_rule_client.Update(
        priority=priority,
        security_policy=args.security_policy,
        security_policy_rule=security_policy_rule)
