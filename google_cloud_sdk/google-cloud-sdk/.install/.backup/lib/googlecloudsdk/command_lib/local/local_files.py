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
"""Library for generating the files for local development environment."""
from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

import os.path

from googlecloudsdk.command_lib.local import local
from googlecloudsdk.command_lib.local import yaml_helper
from googlecloudsdk.core import yaml

_SKAFFOLD_TEMPLATE = """
apiVersion: skaffold/v1
kind: Config
build:
  artifacts:
  - image: {image_name}
    context: {context_path}
deploy:
  kubectl:
    manifests: []
"""


class LocalRuntimeFiles(object):
  """Generates the developement environment files for a project."""

  def __init__(self, settings):
    """Initialize LocalRuntimeFiles.

    Args:
      settings: Local development settings.
    """
    self._settings = settings

  def KubernetesConfig(self):
    """Create a kubernetes config file.

    Returns:
      Text of a kubernetes config file.
    """
    kubernetes_configs = local.CreatePodAndService(self._settings.service_name,
                                                   self._settings.image_name)
    if self._settings.service_account:
      service_account = local.CreateDevelopmentServiceAccount(
          self._settings.service_account)
      private_key_json = local.CreateServiceAccountKey(service_account)
      secret_yaml = local.LocalDevelopmentSecretSpec(private_key_json)
      kubernetes_configs.append(secret_yaml)
      local.AddServiceAccountSecret(kubernetes_configs)

    return yaml.dump_all(kubernetes_configs)

  def SkaffoldConfig(self, kubernetes_file_path):
    """Create a skaffold yaml file.

    Args:
      kubernetes_file_path: Path to the kubernetes config file.

    Returns:
      Text of the skaffold yaml file.
    """
    skaffold_yaml_text = _SKAFFOLD_TEMPLATE.format(
        image_name=self._settings.image_name,
        context_path=self._settings.build_context_directory or
        os.path.dirname(self._settings.dockerfile) or '.')
    skaffold_yaml = yaml.load(skaffold_yaml_text)
    manifests = yaml_helper.GetOrCreate(
        skaffold_yaml, ('deploy', 'kubectl', 'manifests'), constructor=list)
    manifests.append(kubernetes_file_path)

    return yaml.dump(skaffold_yaml)
