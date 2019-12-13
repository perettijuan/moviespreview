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
"""Wrapper for user-visible error exceptions to raise in the CLI."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

from googlecloudsdk.api_lib.util import exceptions as api_lib_exceptions
from googlecloudsdk.core import exceptions as core_exceptions


class Error(core_exceptions.Error):
  """Exceptions for Services errors."""


class EnableServicePermissionDeniedException(Error):
  pass


class ListServicesPermissionDeniedException(Error):
  pass


class GetServicePermissionDeniedException(Error):
  pass


class CreateQuotaOverridePermissionDeniedException(Error):
  pass


class UpdateQuotaOverridePermissionDeniedException(Error):
  pass


class DeleteQuotaOverridePermissionDeniedException(Error):
  pass


class CreateConnectionsPermissionDeniedException(Error):
  pass


class ListConnectionsPermissionDeniedException(Error):
  pass


class GenerateServiceIdentityPermissionDeniedException(Error):
  pass


class OperationErrorException(Error):
  pass


class TimeoutError(Error):
  pass


def ReraiseError(err, klass):
  """Transform and re-raise error helper."""
  core_exceptions.reraise(klass(api_lib_exceptions.HttpException(err)))
