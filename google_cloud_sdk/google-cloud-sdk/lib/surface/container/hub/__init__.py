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
"""Command group for GKE Hub."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from googlecloudsdk.calliope import base

# LINT.IfChange
DETAILED_HELP = {
    'brief': 'Registers a cluster with Google Cloud Platform.',
    'description': """\
    This command registers a cluster referenced from a
    kubeconfig file with Google Cloud Platform. It also installs the Connect
    agent into this cluster, or updates the Connect agent in a
    previously-registered cluster.""",
    'EXAMPLES': """\
    Register a cluster referenced from the default kubeconfig file, installing the
    Connect agent:

        $ {command} register-cluster my-cluster \
            --context=my-cluster-context \
            --service-account-key-file=/tmp/keyfile.json

    Upgrade the Connect agent in a cluster:

        $ {command} register-cluster my-cluster \
            --context=my-cluster-context \
            --service-account-key-file=/tmp/keyfile.json

    Register a cluster and output a manifest that can be used to install the
    Connect agent:

        $ {command} register-cluster my-cluster \
            --context=my-cluster-context \
            --manifest-output-file=/tmp/manifest.yaml \
            --service-account-key-file=/tmp/keyfile.json
    """,
}
# LINT.ThenChange(../memberships/__init__.py)


@base.ReleaseTracks(base.ReleaseTrack.ALPHA, base.ReleaseTrack.BETA)
class Hub(base.Group):
  """Manage clusters registered with Google Cloud Platform."""
  detailed_help = DETAILED_HELP
