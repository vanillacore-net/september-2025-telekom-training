# Project Configuration

## Autonomy Settings
autonomy_level: L3
l3_settings:
  max_parallel: 5
  auto_discover: true
  continue_on_error: true

## Project Overview
This is a training architecture documentation project for Telekom Architecture training scheduled for 2025-09.

## System Nature
DOCUMENTATION PROJECT - Focus on training materials and architecture documentation.

## Workflow Settings
workflow_settings:
  nano:
    version_bump: false
    changelog_required: false
    pr_required: false
    merge_strategy: direct_commit
  tiny:
    version_bump: true
    version_type: patch
    changelog_required: true
    pr_required: false
    merge_strategy: direct_commit
  medium:
    version_bump: true
    version_type: minor
    changelog_required: true
    pr_required: true
    merge_strategy: feature_branch
  large:
    version_bump: true
    version_type: minor
    changelog_required: true
    pr_required: true
    merge_strategy: feature_branch
    coordination_required: true
  mega:
    version_bump: true
    version_type: major
    changelog_required: true
    pr_required: true
    merge_strategy: feature_branch
    coordination_required: true
    breaking_change_assessment: true