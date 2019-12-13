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
"""This module manages the survey prompting."""

from __future__ import absolute_import
from __future__ import division
from __future__ import unicode_literals

import os
import time

from googlecloudsdk.core import config
from googlecloudsdk.core import log
from googlecloudsdk.core import yaml
from googlecloudsdk.core.util import files as file_utils

SURVEY_PROMPT_INTERVAL = 86400 * 14  # 14 days
SURVEY_PROMPT_INTERVAL_AFTER_ANSWERED = 86400 * 30 * 3  # 90 days


class PromptRecord(object):
  """The survey prompt record.

  Attributes:
    _cache_file_path: cache file path.
    last_answer_survey_time: the time user most recently answered the survey
      (epoch time).
    last_prompt_time: the time when user is most recently prompted (epoch time).
    dirty: bool, True if record in the cache file should be updated. Otherwise,
      False.
  """

  def __init__(self):
    self._cache_file_path = config.Paths().survey_prompting_cache_path
    self._last_prompt_time, self._last_answer_survey_time = (
        self.ReadPromptRecordFromFile())
    self._dirty = False

  def CacheFileExists(self):
    return os.path.isfile(self._cache_file_path)

  def ReadPromptRecordFromFile(self):
    """Loads the prompt record from the cache file.

    Returns:
       Two-value tuple (last_prompt_time, last_answer_survey_time)
    """
    if not self.CacheFileExists():
      return None, None

    try:
      with file_utils.FileReader(self._cache_file_path) as f:
        data = yaml.load(f)
      return (data.get('last_prompt_time', None),
              data.get('last_answer_survey_time', None))
    except Exception:  # pylint:disable=broad-except
      log.debug('Failed to parse survey prompt cache. '
                'Using empty cache instead.')
      return None, None

  def SavePromptRecordToFile(self):
    """Serializes data to the cache file."""
    if not self._dirty:
      return
    with file_utils.FileWriter(self._cache_file_path) as f:
      yaml.dump(self._ToDictionary(), stream=f)
    self._dirty = False

  def _ToDictionary(self):
    res = {}
    if self._last_prompt_time is not None:
      res['last_prompt_time'] = self._last_prompt_time
    if self._last_answer_survey_time is not None:
      res['last_answer_survey_time'] = self._last_answer_survey_time
    return res

  @property
  def last_answer_survey_time(self):
    return self._last_answer_survey_time

  @last_answer_survey_time.setter
  def last_answer_survey_time(self, value):
    self._last_answer_survey_time = value
    self._dirty = True

  @property
  def last_prompt_time(self):
    return self._last_prompt_time

  @last_prompt_time.setter
  def last_prompt_time(self, value):
    self._last_prompt_time = value
    self._dirty = True

  @property
  def dirty(self):
    return self._dirty

  def __enter__(self):
    return self

  def __exit__(self, exc_type, exc_val, exc_tb):
    self.SavePromptRecordToFile()


class SurveyPrompter(object):
  """Manages prompting user for survey.

  Attributes:
     _prompt_record: PromptRecord, the record of the survey prompt history.
     _prompt_message: str, the prompting message.
  """
  _DEFAULT_SURVEY_PROMPT_MSG = ('To take a quick anonymous survey, run:\n'
                                '  $ gcloud survey')

  def __init__(self, msg=_DEFAULT_SURVEY_PROMPT_MSG):
    self._prompt_record = PromptRecord()
    self._prompt_message = msg

  def PrintPromptMsg(self):
    log.status.write('\n\n' + self._prompt_message + '\n\n')

  def ShouldPrompt(self):
    """Check if the user should be prompted."""
    if not (log.out.isatty() and log.err.isatty()):
      return False

    last_prompt_time = self._prompt_record.last_prompt_time
    last_answer_survey_time = self._prompt_record.last_answer_survey_time
    now = time.time()
    if last_prompt_time and (now - last_prompt_time) < SURVEY_PROMPT_INTERVAL:
      return False
    if last_answer_survey_time and (now - last_answer_survey_time <
                                    SURVEY_PROMPT_INTERVAL_AFTER_ANSWERED):
      return False
    return True

  def PromptForSurvey(self):
    """Prompts user for survey if user should be prompted."""
    # Don't prompt users right after users install gcloud. Wait for 14 days.
    if not self._prompt_record.CacheFileExists():
      with self._prompt_record as pr:
        pr.last_prompt_time = time.time()
      return

    if self.ShouldPrompt():
      self.PrintPromptMsg()
      with self._prompt_record as pr:
        pr.last_prompt_time = time.time()
