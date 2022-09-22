# 1.5 release notes

### Plugin is supported for Jenkins version 2.60.3 or higher.

New Features:

- Add support for threaded messages with dynamic threadKey

# 1.4 release notes

### Plugin is supported for Jenkins version 2.60.3 or higher.

Issue:

- Encoding threadKey before appending to chat URL

# 1.3 release notes

### Plugin is supported for Jenkins version 2.60.3 or higher.

New Features:

- Add support for threading [#6](https://github.com/jenkinsci/google-chat-notification-plugin/issues/6).
  - New checkbox *Append Notification In Same Thread* added to enable or disable threading.
- Added support for timeout of 15 seconds


# 1.2 release notes

### From this version onwards, plugin is only supported for Jenkins version 2.60.3 or higher.

New Features:

- Add support to send notifications if proxy configuration defined [#1](https://github.com/jenkinsci/google-chat-notification-plugin/issues/1)
- Suppress Info logs [#3](https://github.com/jenkinsci/google-chat-notification-plugin/issues/3).
  - New checkbox *Suppress Info Logs* added to enable or disable logging.
- Removed deprecated libraries of Jenkins and replaced with newer versions. As a result of this version of plugin is only supported for Jenkins version 2.60.3 or higher.


# 1.1 release notes

### Plugin is supported for Jenkins version 2.7.3 or higher.

New Features:

- Add support to configure entire Google Chat Room URL in credentials and use it in URL parameter as *id:configured_credential_id*.
- Default behaviour of plugin is to send notifications for all build status unless overridden with true value for defined build statuses.