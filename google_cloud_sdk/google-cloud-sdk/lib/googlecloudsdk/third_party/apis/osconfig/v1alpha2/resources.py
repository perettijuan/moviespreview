# -*- coding: utf-8 -*- #
# Copyright 2015 Google LLC. All Rights Reserved.
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
"""Resource definitions for cloud platform apis."""

import enum


BASE_URL = 'https://osconfig.googleapis.com/v1alpha2/'
DOCS_URL = 'https://cloud.google.com/'


class Collections(enum.Enum):
  """Collections for all supported apis."""

  FOLDERS = (
      'folders',
      'folders/{foldersId}',
      {},
      [u'foldersId'],
      True
  )
  FOLDERS_GUESTPOLICIES = (
      'folders.guestPolicies',
      '{+name}',
      {
          '':
              'folders/{foldersId}/guestPolicies/{guestPoliciesId}',
      },
      [u'name'],
      True
  )
  ORGANIZATIONS = (
      'organizations',
      'organizations/{organizationsId}',
      {},
      [u'organizationsId'],
      True
  )
  ORGANIZATIONS_GUESTPOLICIES = (
      'organizations.guestPolicies',
      '{+name}',
      {
          '':
              'organizations/{organizationsId}/guestPolicies/'
              '{guestPoliciesId}',
      },
      [u'name'],
      True
  )
  PROJECTS = (
      'projects',
      'projects/{projectsId}',
      {},
      [u'projectsId'],
      True
  )
  PROJECTS_GUESTPOLICIES = (
      'projects.guestPolicies',
      '{+name}',
      {
          '':
              'projects/{projectsId}/guestPolicies/{guestPoliciesId}',
      },
      [u'name'],
      True
  )
  PROJECTS_PATCHDEPLOYMENTS = (
      'projects.patchDeployments',
      '{+name}',
      {
          '':
              'projects/{projectsId}/patchDeployments/{patchDeploymentsId}',
      },
      [u'name'],
      True
  )
  PROJECTS_PATCHJOBS = (
      'projects.patchJobs',
      '{+name}',
      {
          '':
              'projects/{projectsId}/patchJobs/{patchJobsId}',
      },
      [u'name'],
      True
  )

  def __init__(self, collection_name, path, flat_paths, params,
               enable_uri_parsing):
    self.collection_name = collection_name
    self.path = path
    self.flat_paths = flat_paths
    self.params = params
    self.enable_uri_parsing = enable_uri_parsing
