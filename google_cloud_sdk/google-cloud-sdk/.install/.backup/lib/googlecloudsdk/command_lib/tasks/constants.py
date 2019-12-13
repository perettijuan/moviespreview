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
"""Constants for `gcloud tasks` commands."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals


PROJECTS_COLLECTION = 'cloudtasks.projects'
LOCATIONS_COLLECTION = 'cloudtasks.projects.locations'
QUEUES_COLLECTION = 'cloudtasks.projects.locations.queues'
TASKS_COLLECTION = 'cloudtasks.projects.locations.queues.tasks'

PULL_QUEUE = 'pull'
PUSH_QUEUE = 'push'
VALID_QUEUE_TYPES = [PULL_QUEUE, PUSH_QUEUE]

PULL_TASK = 'pull'
APP_ENGINE_TASK = 'app-engine'
HTTP_TASK = 'http'

APP_ENGINE_ROUTING_KEYS = ['service', 'version', 'instance']

QUEUE_MANAGEMENT_WARNING = (
    'You are managing queues with gcloud, do not use queue.yaml or queue.xml '
    'in the future. More details at: '
    'https://cloud.google.com/tasks/docs/queue-yaml.')
