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
"""List the CryptoKeys within a KeyRing."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from apitools.base.py import list_pager
from googlecloudsdk.api_lib.cloudkms import base as cloudkms_base
from googlecloudsdk.calliope import base
from googlecloudsdk.command_lib.kms import flags
from googlecloudsdk.core import resources


class List(base.ListCommand):
  """List the CryptoKeys within a KeyRing.

  Lists all CryptoKeys within the given KeyRing.

  ## EXAMPLES

  The following command lists all CryptoKeys within the
  KeyRing `fellowship` and Location `global`:

    $ {command} --keyring=fellowship --location=global
  """

  def GetUriFunc(self):
    return cloudkms_base.MakeGetUriFunc(self)

  @staticmethod
  def Args(parser):
    flags.AddKeyRingFlag(parser, 'key')
    flags.AddLocationFlag(parser, 'key')

  def Run(self, args):
    client = cloudkms_base.GetClientInstance()
    messages = cloudkms_base.GetMessagesModule()

    key_ring_ref = resources.REGISTRY.Create(flags.KEY_RING_COLLECTION)

    request = messages.CloudkmsProjectsLocationsKeyRingsCryptoKeysListRequest(
        parent=key_ring_ref.RelativeName())

    return list_pager.YieldFromList(
        client.projects_locations_keyRings_cryptoKeys,
        request,
        field='cryptoKeys',
        limit=args.limit,
        batch_size_attribute='pageSize')
