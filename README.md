# BUSINESS MESSAGES: Kitchen Sink Bot

This sample demonstrates how to receive a message from the [Business Messages](https://developers.google.com/business-communications/business-messages/reference/rest)
platform and echo the same message back to the user using the [Business Messages Java client library](https://github.com/google-business-communications/java-businessmessages). The bot also includes commands to generate all message types supported
in Business Messages and receive all user generated message types.

Enter `help` to see a  list of supported commands.

This sample runs on the Google App Engine.

See the Google App Engine (https://cloud.google.com/appengine/docs/java/) standard environment
documentation for more detailed instructions.

## Documentation

The documentation for the Business Messages API can be found [here](https://developers.google.com/business-communications/business-messages/reference/rest).

## Prerequisite

You must have the following software installed on your machine:

* [Apache Maven](http://maven.apache.org) 3.3.9 or greater
* [Google Cloud SDK](https://cloud.google.com/sdk/) (aka gcloud)
* [Java 8](http://www.oracle.com/technetwork/java/javase/downloads/index.html)
* [Google App Engine SDK for Java](https://cloud.google.com/appengine/docs/standard/java/download)

## Before you begin

1.  [Register with Business Messages](https://developers.google.com/business-communications/business-messages/guides/set-up/register).
1.  Once registered, follow the instructions to [enable the APIs for your project](https://developers.google.com/business-communications/business-messages/guides/set-up/register#enable-api).
1. Open the [Create an agent](https://developers.google.com/business-communications/business-messages/guides/set-up/agent)
guide and follow the instructions to create a Business Messages agent.

## Deploy the sample

1.  In a terminal, navigate to this sample's root directory.

1.  Run the following commands:

    ```bash
    gcloud config set project PROJECT_ID
    ```

    Where PROJECT_ID is the project ID for the project you created when you registered for
    Business Messages.

    ```bash
    mvn appengine:deploy
    ```

1.  On your mobile device, use the test business URL associated with the
    Business Messages agent you created. Open a conversation with your agent
    and type in "Hello". Once delivered, you should receive "Hello" back
    from the agent. Type "help" to tap the Help suggestion to explore other functionality.

    See the [Test an agent](https://developers.google.com/business-communications/business-messages/guides/set-up/agent#test-agent) guide if you need help retrieving your test business URL.