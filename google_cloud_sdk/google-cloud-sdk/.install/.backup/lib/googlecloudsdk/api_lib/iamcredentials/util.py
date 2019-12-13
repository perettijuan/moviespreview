# -*- coding: utf-8 -*- #
# Copyright 2018 Google LLC. All Rights Reserved.
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

"""Utilities for the iamcredentials API."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

import datetime

from googlecloudsdk.api_lib.util import apis_internal
from googlecloudsdk.core import resources
from googlecloudsdk.core.credentials import http as http_creds

from oauth2client import client


def GenerateAccessToken(service_account_id, scopes):
  """Generates an access token for the given service account."""
  service_account_ref = resources.REGISTRY.Parse(
      service_account_id, collection='iamcredentials.serviceAccounts',
      params={'projectsId': '-', 'serviceAccountsId': service_account_id})

  # pylint: disable=protected-access
  http_client = http_creds.Http(
      response_encoding=http_creds.ENCODING,
      allow_account_impersonation=False, force_resource_quota=True)
  iam_client = apis_internal._GetClientInstance(
      'iamcredentials', 'v1', http_client=http_client)
  response = iam_client.projects_serviceAccounts.GenerateAccessToken(
      iam_client.MESSAGES_MODULE
      .IamcredentialsProjectsServiceAccountsGenerateAccessTokenRequest(
          name=service_account_ref.RelativeName(),
          generateAccessTokenRequest=iam_client.MESSAGES_MODULE
          .GenerateAccessTokenRequest(scope=scopes)
      )
  )
  return response


def GenerateIdToken(service_account_id, audience, include_email=False):
  """Generates an id token for the given service account."""
  service_account_ref = resources.REGISTRY.Parse(
      service_account_id, collection='iamcredentials.serviceAccounts',
      params={'projectsId': '-', 'serviceAccountsId': service_account_id})

  # pylint: disable=protected-access
  http_client = http_creds.Http(
      response_encoding=http_creds.ENCODING,
      allow_account_impersonation=False, force_resource_quota=True)
  iam_client = apis_internal._GetClientInstance(
      'iamcredentials', 'v1', http_client=http_client)
  response = iam_client.projects_serviceAccounts.GenerateIdToken(
      iam_client.MESSAGES_MODULE
      .IamcredentialsProjectsServiceAccountsGenerateIdTokenRequest(
          name=service_account_ref.RelativeName(),
          generateIdTokenRequest=iam_client.MESSAGES_MODULE
          .GenerateIdTokenRequest(audience=audience, includeEmail=include_email)
      )
  )
  return response.token


class ImpersonationAccessTokenProvider(object):
  """A token provider for service account elevation.

  This supports the interface required by the core/credentials module.
  """

  def GetElevationAccessToken(self, service_account_id, scopes):
    response = GenerateAccessToken(service_account_id, scopes)
    return ImpersonationCredentials(
        service_account_id, response.accessToken, response.expireTime, scopes)

  def GetElevationIdToken(self, service_account_id, audience, include_email):
    return GenerateIdToken(service_account_id, audience, include_email)

  @classmethod
  def IsImpersonationCredential(cls, cred):
    return isinstance(cred, ImpersonationCredentials)


class ImpersonationCredentials(client.OAuth2Credentials):
  """Implementation of a credential that refreshes using the iamcredentials API.
  """
  _EXPIRY_FORMAT = '%Y-%m-%dT%H:%M:%SZ'

  def __init__(self, service_account_id, access_token, token_expiry, scopes):
    self._service_account_id = service_account_id
    token_expiry = self._ConvertExpiryTime(token_expiry)
    super(ImpersonationCredentials, self).__init__(
        access_token, None, None, None, token_expiry, None, None, scopes=scopes)

  def _refresh(self, http):
    # client.Oauth2Credentials converts scopes into a set, so we need to convert
    # back to a list before making the API request.
    response = GenerateAccessToken(self._service_account_id, list(self.scopes))
    self.access_token = response.accessToken
    self.token_expiry = self._ConvertExpiryTime(response.expireTime)

  def _ConvertExpiryTime(self, value):
    return datetime.datetime.strptime(value,
                                      ImpersonationCredentials._EXPIRY_FORMAT)
