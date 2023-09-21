# Google Chat plugin for Jenkins

[![Build Status][jenkins-status]][jenkins-builds]
[![Jenkins Plugin][plugin-version-badge]][plugin]
[![GitHub release][github-release-badge]][github-release]
[![Jenkins Plugin Installs][plugin-install-badge]][plugin]

Google Chat Notification Jenkins Plugin to send build status to [Google Chat][google-chat].

This Jenkins plugin allows you to send Google Chat notification as a post build action or as a pipeline script.
This plugin is supported for Jenkins version **2.361.4 or higher**.

![Screenshot][img-output-sample]

## Prerequisites

- You must [create a web hook in google][google-chat-create-webhook] chat group to send notification.

![Screenshot][img-configure-web-hook]

## How to configure it in post build action

- Click on Add post-build action button

![Screenshot][img-add-post-build-action]

- Click on Google Chat Notification

![Screenshot][img-post-build-action-google-chat]

- Configure URL (web hook URL configured in prerequisites), message (build message) and type of build result you want to send notification. You can configure multiple URLs separated by comma.

![Screenshot][img-post-build-action-google-chat-config]

## How to use it in pipeline script

Use below command
### googlechatnotification url: 'web hook(s) URL(s)', message: 'message to be sent', messageFormat: 'simple|card', sameThreadNotification: 'true', threadKey: '', notifyAborted: 'true', notifyFailure: 'true', notifyNotBuilt: 'true', notifySuccess: 'true', notifyUnstable: 'true', notifyBackToNormal: 'true', suppressInfoLoggers: 'true'

## Please find explanations for each fields as below, usage for all fields remains same for build job and pipeline:

1. **url**
   - This is a mandatory String parameter.
   - Single/multiple comma separated HTTP URLs or/and single/multiple comma separated Credential IDs.
     - To use Credential ID as URL identifier configure entire URL as secret in credential. Use *id:credential_id_for_room1* as value in URL.

     ![Screenshot][img-add-credential]

   - Different Sample Ways to define URL parameter:
     - https://chat.googleapis.com/v1/spaces/room_id/messages?key=key_id&token=token_id<br/>
     - https://chat.googleapis.com/v1/spaces/room_id/messages?key=key_id&token=token_id, https://chat.googleapis.com/v1/spaces/room_id2/messages?key=key_id2&token=token_id2<br/>
     - id:credential_id_for_room1<br/>
     - id:credential_id_for_room1, id:credential_id_for_room2<br/>
     - https://chat.googleapis.com/v1/spaces/room_id/messages?key=key_id&token=token_id, id:credential_id_for_room2<br/>

1. **message**
   - This is a mandatory String parameter.
   - Notification message to be sent.
   - Supports all token macro variables for pipeline as well as build jobs.

1. **messageFormat**
   - This is an optional String parameter.
   - The format of the message sent. Default value is `simple`.
   - If `card` is provided as value, the parameter `message` must be a [valid JSON configuration](https://developers.google.com/chat/reference/message-formats/cards) for card message.
   
1. **sameThreadNotification**
   - This is an optional boolean parameter. Default value is false.
   - This parameter is used to send notification in same thread for a particular job. If false, the default behavior is to create a new thread for each message.
   - If *messageFormat* is set to `card` and the provided JSON contains a 'thread' key, this parameter will be ignored.
   
1. **threadKey**
   - This is an optional String parameter. Default value is null.
   - The thread used to send all the generated notification messages for a particular job. If not defined, the default behavior is to use the `JOB_NAME` as *threadKey*.
   - Supports all token macro variables for pipeline as well as build jobs.
   - This parameter only applies if *sameThreadNotification* is set to true.
   - If *messageFormat* is set to `card` and the provided JSON contains a 'thread' key, this parameter will be ignored.

1. **notifyAborted**
   - This is an optional boolean parameter. Default value is false.
   - Notification message to be sent when build status is ABORTED.

1. **notifyFailure**
   - This is an optional boolean parameter. Default value is false.
   - Notification message to be sent when build status is FAILURE.

1. **notifyNotBuilt**
   - This is an optional boolean parameter. Default value is false.
   - Notification message to be sent when build status is NOT_BUILT.

1. **notifySuccess**
   - This is an optional boolean parameter. Default value is false.
   - Notification message to be sent when build status is SUCCESS.

1. **notifyUnstable**
   - This is an optional boolean parameter. Default value is false.
   - Notification message to be sent when build status is UNSTABLE.

1. **notifyBackToNormal**
   - This is an optional boolean parameter. Default value is false.
   - Notification message to be sent when build status is SUCCESS and previous build status was not SUCCESS.

1. **suppressInfoLoggers**
   - This is an optional boolean parameter. Default value is false.
   - Suppress all info loggers in Jenkins build.

### Default behaviour of plugin is to send notifications for all build status unless overridden with true value for above defined build statuses.

## Report an Issue

Please report issues and enhancements through the [Jenkins issue tracker](https://www.jenkins.io/participate/report-issue/redirect/#24023).

[jenkins-builds]: https://ci.jenkins.io/job/Plugins/job/google-chat-notification-plugin/job/master/

[jenkins-status]: https://ci.jenkins.io/buildStatus/icon?job=Plugins/google-chat-notification-plugin/master

[plugin-version-badge]: https://img.shields.io/jenkins/plugin/v/google-chat-notification.svg

[plugin-install-badge]: https://img.shields.io/jenkins/plugin/i/google-chat-notification.svg?color=blue

[plugin]: https://plugins.jenkins.io/google-chat-notification

[github-release-badge]: https://img.shields.io/github/release/jenkinsci/google-chat-notification-plugin.svg?label=release

[github-release]: https://github.com/jenkinsci/google-chat-notification-plugin/releases/latest

[google-chat]: https://chat.google.com

[google-chat-create-webhook]: https://developers.google.com/chat/how-tos/webhooks?hl=pt-br#step_1_register_the_incoming_webhook

[img-configure-web-hook]: docs/configure-web-hook.png

[img-add-post-build-action]: docs/add-post-build-action.png

[img-post-build-action-google-chat]: docs/click-google-chat-notification.png

[img-post-build-action-google-chat-config]: docs/details.png

[img-add-credential]: docs/add-credential.png

[img-output-sample]: docs/output-sample.png
