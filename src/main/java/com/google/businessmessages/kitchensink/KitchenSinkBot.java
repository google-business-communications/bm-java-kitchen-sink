/*
 * Copyright (C) 2020 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not use this file except
 * in compliance with the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License
 * is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express
 * or implied. See the License for the specific language governing permissions and limitations under
 * the License.
 */
package com.google.businessmessages.kitchensink;

// [START import_libraries]

import com.google.api.client.googleapis.auth.oauth2.GoogleCredential;
import com.google.api.client.googleapis.javanet.GoogleNetHttpTransport;
import com.google.api.client.http.HttpTransport;
import com.google.api.client.json.jackson2.JacksonFactory;
import com.google.api.services.businessmessages.v1.Businessmessages;
import com.google.api.services.businessmessages.v1.model.BusinessMessagesCardContent;
import com.google.api.services.businessmessages.v1.model.BusinessMessagesCarouselCard;
import com.google.api.services.businessmessages.v1.model.BusinessMessagesContentInfo;
import com.google.api.services.businessmessages.v1.model.BusinessMessagesDialAction;
import com.google.api.services.businessmessages.v1.model.BusinessMessagesEvent;
import com.google.api.services.businessmessages.v1.model.BusinessMessagesLiveAgentRequest;
import com.google.api.services.businessmessages.v1.model.BusinessMessagesMedia;
import com.google.api.services.businessmessages.v1.model.BusinessMessagesMessage;
import com.google.api.services.businessmessages.v1.model.BusinessMessagesOpenUrlAction;
import com.google.api.services.businessmessages.v1.model.BusinessMessagesRepresentative;
import com.google.api.services.businessmessages.v1.model.BusinessMessagesRichCard;
import com.google.api.services.businessmessages.v1.model.BusinessMessagesStandaloneCard;
import com.google.api.services.businessmessages.v1.model.BusinessMessagesSuggestedAction;
import com.google.api.services.businessmessages.v1.model.BusinessMessagesSuggestedReply;
import com.google.api.services.businessmessages.v1.model.BusinessMessagesSuggestion;
import com.google.api.services.businessmessages.v1.model.BusinessMessagesSurvey;
import com.google.cloud.translate.Translate;
import com.google.cloud.translate.TranslateOptions;
import com.google.cloud.translate.Translation;
import com.google.communications.businessmessages.v1.CardWidth;
import com.google.communications.businessmessages.v1.EventType;
import com.google.communications.businessmessages.v1.MediaHeight;
import com.google.communications.businessmessages.v1.RepresentativeType;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.logging.Level;
import java.util.logging.Logger;
// [END import_libraries]

/**
 * Main bot logic. Most messages are passed through the routing function to map the user's response
 * to business logic to generate a message in return.
 */
public class KitchenSinkBot {
  private static final Logger logger = Logger.getLogger(KitchenSinkBot.class.getName());

  private static final String EXCEPTION_WAS_THROWN = "exception";

  // Object to maintain OAuth2 credentials to call the BM API
  private GoogleCredential credential;

  // Reference to the BM api builder
  private Businessmessages.Builder builder;

  // The current representative
  private BusinessMessagesRepresentative representative;

  public KitchenSinkBot(BusinessMessagesRepresentative representative) {
    this.representative = representative;

    initBmApi();
  }

  /**
   * Routes the message to produce a response based on the incoming message if it matches an
   * existing supported command. Otherwise, the inbound message is echoed back to the user.
   *
   * @param message The received message from a user.
   * @param conversationId The conversation ID that uniquely maps to the user and agent.
   */
  public void routeMessage(String message, String conversationId) {
    String normalizedMessage = message.toLowerCase().trim();

    if (normalizedMessage.equals(BotConstants.CMD_LOREM_IPSUM)) {
      sendResponse(BotConstants.RSP_LOREM_IPSUM, conversationId);
    } else if (normalizedMessage.equals(BotConstants.CMD_MEDIUM_TEXT)) {
      sendResponse(BotConstants.RSP_MEDIUM_TEXT, conversationId);
    } else if (normalizedMessage.equals(BotConstants.CMD_LONG_TEXT)) {
      sendResponse(BotConstants.RSP_LONG_TEXT, conversationId);
    } else if (normalizedMessage.matches(BotConstants.CMD_SPEAK)) {
      attemptTranslation(normalizedMessage, conversationId);
    } else if (normalizedMessage.matches(BotConstants.CMD_LINK)) {
      sendLinkAction(conversationId);
    } else if (normalizedMessage.matches(BotConstants.CMD_DIAL)) {
      sendDialAction(conversationId);
    } else if (normalizedMessage.matches(BotConstants.CMD_CARD)) {
      sendRichCard(conversationId);
    } else if (normalizedMessage.matches(BotConstants.CMD_CAROURSEL)) {
      sendCarouselRichCard(conversationId);
    } else if (normalizedMessage.matches(BotConstants.CMD_WHO)) {
      sendResponse(BotConstants.RSP_WHO_TEXT, conversationId);
    } else if (normalizedMessage.matches(BotConstants.CMD_CSAT_TRIGGER)) {
      showCSAT(conversationId);
    } else if (normalizedMessage.matches(BotConstants.CMD_HELP)) {
      sendResponse(BotConstants.RSP_HELP_TEXT, conversationId);
    } else if (normalizedMessage.matches(BotConstants.CMD_LIVE_AGENT)) {
      sendLiveAgentAction(conversationId);
    } else if (normalizedMessage.matches(BotConstants.CMD_CHIPS)) {
      sendChipExamples(conversationId);
    } else if (normalizedMessage.matches(BotConstants.CMD_BOLD)) {
      sendRichResponse("**" + BotConstants.RSP_LOREM_IPSUM + "**", conversationId);
    } else if (normalizedMessage.matches(BotConstants.CMD_ITALICS)) {
      sendRichResponse("*" + BotConstants.RSP_LOREM_IPSUM + "*", conversationId);
    }  else if (normalizedMessage.matches(BotConstants.CMD_HYPERLINK)) {
      sendRichResponse(BotConstants.RSP_HYPERLINK_TEXT, conversationId);
    } else { // Echo received message
      sendResponse(message, conversationId);
    }
  }

  /**
   * Transfers the chat to a live agent. Creating the representative as a HUMAN will show a
   * tombstone to the user indicating that they have been transferred to a live agent.
   *
   * @param conversationId The conversation ID that uniquely maps to the user and agent.
   */
  public void transferToLiveAgent(String conversationId) {
    transferToAnAgent(conversationId, new BusinessMessagesRepresentative()
        .setRepresentativeType(RepresentativeType.HUMAN.toString())
        .setDisplayName(BotConstants.LIVE_AGENT_NAME)
        .setAvatarImage(BotConstants.LIVE_AGENT_AVATAR));
  }

  /**
   * Transfers the chat back to a bot.
   *
   * @param conversationId The conversation ID that uniquely maps to the user and agent.
   */
  public void transferToBot(String conversationId) {
    try {
      BusinessMessagesEvent event =
          new BusinessMessagesEvent()
              .setEventType(EventType.REPRESENTATIVE_LEFT.toString())
              .setRepresentative(
                  new BusinessMessagesRepresentative()
                      .setRepresentativeType(RepresentativeType.HUMAN.toString())
                      .setDisplayName(BotConstants.LIVE_AGENT_NAME)
                      .setAvatarImage(BotConstants.LIVE_AGENT_AVATAR));

      Businessmessages.Conversations.Events.Create request
          = builder.build().conversations().events()
          .create("conversations/" + conversationId, event);

      request.setEventId(UUID.randomUUID().toString());

      request.execute();

      transferToAnAgent(conversationId, new BusinessMessagesRepresentative()
          .setRepresentativeType(RepresentativeType.BOT.toString())
          .setDisplayName(BotConstants.BOT_AGENT_NAME)
          .setAvatarImage(BotConstants.BOT_AGENT_AVATAR));

      sendResponse(BotConstants.RSP_BOT_TRANSFER, conversationId);
    } catch (Exception e) {
      logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
    }
  }

  /**
   * Generic method to transfer in a representative into the conversation.
   *
   * @param conversationId The conversation ID that uniquely maps to the user and agent.
   * @param representative The representative joining the conversation.
   */
  private void transferToAnAgent(String conversationId,
      BusinessMessagesRepresentative representative) {
    try {
      BusinessMessagesEvent event =
          new BusinessMessagesEvent()
              .setEventType(EventType.REPRESENTATIVE_JOINED.toString())
              .setRepresentative(representative);

      Businessmessages.Conversations.Events.Create request
          = builder.build().conversations().events()
          .create("conversations/" + conversationId, event);

      request.setEventId(UUID.randomUUID().toString());

      request.execute();

      sendResponse(BotConstants.RSP_LIVE_AGENT_TRANSFER, conversationId);
    } catch (Exception e) {
      logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
    }
  }

  /**
   * Sends the user a CSAT survey.
   *
   * @param conversationId The conversation ID that uniquely maps to the user and agent.
   */
  private void showCSAT(String conversationId) {
    try {
      Businessmessages.Conversations.Surveys.Create request
          = builder.build().conversations().surveys()
          .create("conversations/" + conversationId,
              new BusinessMessagesSurvey());

      request.setSurveyId(UUID.randomUUID().toString());

      request.execute();
    } catch (Exception e) {
      logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
    }
  }

  /**
   * Sends a text message along with an open url action to the user.
   *
   * @param conversationId The conversation ID that uniquely maps to the user and agent.
   */
  private void sendLinkAction(String conversationId) {
    try {
      List<BusinessMessagesSuggestion> suggestions = new ArrayList<>();

      // Add the open url action
      suggestions.add(new BusinessMessagesSuggestion()
          .setAction(new BusinessMessagesSuggestedAction()
              .setOpenUrlAction(
                  new BusinessMessagesOpenUrlAction()
                      .setUrl("https://www.google.com"))
              .setText("Open Google").setPostbackData("open_url")));

      suggestions.addAll(getDefaultMenu());

      // Send the text message and suggestions to the user
      // Use a fallback text of the actual URL
      sendResponse(new BusinessMessagesMessage()
          .setMessageId(UUID.randomUUID().toString())
          .setText(BotConstants.RSP_LINK_TEXT)
          .setRepresentative(representative)
          .setFallback(BotConstants.RSP_LINK_TEXT + " https://www.google.com")
          .setSuggestions(suggestions), conversationId);
    } catch (Exception e) {
      logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
    }
  }

  /**
   * Sends a text message along with a Request a live agent action to the user.
   *
   * @param conversationId The conversation ID that uniquely maps to the user and agent.
   */
  private void sendChipExamples(String conversationId) {
    try {
      List<BusinessMessagesSuggestion> suggestions = new ArrayList<>();

      // Add the request a live agent action
      suggestions.add(new BusinessMessagesSuggestion()
          .setReply(new BusinessMessagesSuggestedReply()
              .setText("Example suggestion").setPostbackData("example_postback")));

      // Add the request a live agent action
      suggestions.add(new BusinessMessagesSuggestion()
          .setAction(new BusinessMessagesSuggestedAction()
              .setOpenUrlAction(
                  new BusinessMessagesOpenUrlAction()
                      .setUrl("https://www.google.com"))
              .setText("Open Google").setPostbackData("open_url")));

      // Add the request a live agent action
      suggestions.add(new BusinessMessagesSuggestion()
          .setLiveAgentRequest(new BusinessMessagesLiveAgentRequest()));

      // Add the dial action
      suggestions.add(new BusinessMessagesSuggestion()
          .setAction(new BusinessMessagesSuggestedAction()
              .setDialAction(
                  new BusinessMessagesDialAction()
                      .setPhoneNumber("+12223334444"))
              .setText("Call example").setPostbackData("call_example")));

      suggestions.add(getHelpMenuItem());

      // Send the text message and suggestions to the user
      sendResponse(new BusinessMessagesMessage()
          .setMessageId(UUID.randomUUID().toString())
          .setText(BotConstants.RSP_CHIP_TEXT)
          .setRepresentative(representative)
          .setSuggestions(suggestions), conversationId);
    } catch (Exception e) {
      logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
    }
  }

  /**
   * Sends a text message along with a Request a live agent action to the user.
   *
   * @param conversationId The conversation ID that uniquely maps to the user and agent.
   */
  private void sendLiveAgentAction(String conversationId) {
    try {
      List<BusinessMessagesSuggestion> suggestions = new ArrayList<>();

      // Add the request a live agent action
      suggestions.add(new BusinessMessagesSuggestion()
          .setLiveAgentRequest(new BusinessMessagesLiveAgentRequest()));

      suggestions.add(getHelpMenuItem());

      // Send the text message and suggestions to the user
      sendResponse(new BusinessMessagesMessage()
          .setMessageId(UUID.randomUUID().toString())
          .setText(BotConstants.RSP_LIVE_AGENT_TEXT)
          .setRepresentative(representative)
          .setSuggestions(suggestions), conversationId);
    } catch (Exception e) {
      logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
    }
  }

  /**
   * Sends a text message along with a dial action to the user.
   *
   * @param conversationId The conversation ID that uniquely maps to the user and agent.
   */
  private void sendDialAction(String conversationId) {
    try {
      List<BusinessMessagesSuggestion> suggestions = new ArrayList<>();

      // Add the dial action
      suggestions.add(new BusinessMessagesSuggestion()
          .setAction(new BusinessMessagesSuggestedAction()
              .setDialAction(
                  new BusinessMessagesDialAction()
                      .setPhoneNumber("+12223334444"))
              .setText("Call example").setPostbackData("call_example")));

      suggestions.add(getHelpMenuItem());

      // Send the text message and suggestions to the user
      sendResponse(new BusinessMessagesMessage()
          .setMessageId(UUID.randomUUID().toString())
          .setText(BotConstants.RSP_DIAL_TEXT)
          .setRepresentative(representative)
          .setSuggestions(suggestions), conversationId);
    } catch (Exception e) {
      logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
    }
  }

  /**
   * Sends a sample rich card to the user.
   *
   * @param conversationId The conversation ID that uniquely maps to the user and agent.
   */
  private void sendRichCard(String conversationId) {
    try {
      List<BusinessMessagesSuggestion> suggestions = new ArrayList<>();
      suggestions.add(getHelpMenuItem());

      BusinessMessagesStandaloneCard standaloneCard = getSampleCard();

      // Construct a fallback text for devices that do not support rich cards
      StringBuilder fallbackText = new StringBuilder();
      fallbackText.append(standaloneCard.getCardContent().getTitle()).append("\n\n")
          .append(standaloneCard.getCardContent().getDescription()).append("\n\n")
          .append(standaloneCard.getCardContent().getMedia().getContentInfo().getFileUrl());

      // Send the rich card message and suggestions to the user
      sendResponse(new BusinessMessagesMessage()
          .setMessageId(UUID.randomUUID().toString())
          .setRichCard(new BusinessMessagesRichCard()
              .setStandaloneCard(standaloneCard))
          .setRepresentative(representative)
          .setFallback(fallbackText.toString())
          .setSuggestions(suggestions), conversationId);
    } catch (Exception e) {
      logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
    }
  }

  /**
   * Creates a sample standalone rich card.
   *
   * @return A standalone rich card.
   */
  private BusinessMessagesStandaloneCard getSampleCard() {
    return new BusinessMessagesStandaloneCard()
        .setCardContent(
            new BusinessMessagesCardContent()
                .setTitle("Business Messages!!!")
                .setDescription("The future of business communication")
                .setSuggestions(getCardSuggestions())
                .setMedia(new BusinessMessagesMedia()
                    .setHeight(MediaHeight.MEDIUM.toString())
                    .setContentInfo(
                        new BusinessMessagesContentInfo()
                            .setFileUrl(BotConstants.SAMPLE_IMAGES[0])
                    ))
        );
  }

  /**
   * Sends a sample carousel rich card to the user.
   *
   * @param conversationId The conversation ID that uniquely maps to the user and agent.
   */
  private void sendCarouselRichCard(String conversationId) {
    try {
      List<BusinessMessagesSuggestion> suggestions = new ArrayList<>();
      suggestions.add(getHelpMenuItem());

      BusinessMessagesCarouselCard carouselCard = getSampleCarousel();

      // Construct a fallback text for devices that do not support carousels
      StringBuilder fallbackText = new StringBuilder();
      for (BusinessMessagesCardContent cardContent : carouselCard.getCardContents()) {
        fallbackText.append(cardContent.getTitle()).append("\n\n");
        fallbackText.append(cardContent.getDescription()).append("\n\n");
        fallbackText.append(cardContent.getMedia().getContentInfo().getFileUrl()).append("\n");
        fallbackText.append("---------------------------------------------\n\n");
      }

      // Send the carousel card message and suggestions to the user
      sendResponse(new BusinessMessagesMessage()
          .setMessageId(UUID.randomUUID().toString())
          .setRichCard(new BusinessMessagesRichCard()
              .setCarouselCard(carouselCard))
          .setRepresentative(representative)
          .setFallback(fallbackText.toString())
          .setSuggestions(suggestions), conversationId);
    } catch (Exception e) {
      logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
    }
  }

  /**
   * Creates a sample carousel rich card.
   *
   * @return A carousel rich card.
   */
  private BusinessMessagesCarouselCard getSampleCarousel() {
    List<BusinessMessagesCardContent> cardContents = new ArrayList<>();

    // Create individual cards for the carousel
    for (int i = 0; i < BotConstants.SAMPLE_IMAGES.length; i++) {
      cardContents.add(new BusinessMessagesCardContent()
          .setTitle("Card #" + (i + 1))
          .setDescription("What do you think?")
          .setSuggestions(getCardSuggestions())
          .setMedia(new BusinessMessagesMedia()
              .setHeight(MediaHeight.MEDIUM.toString())
              .setContentInfo(new BusinessMessagesContentInfo()
                  .setFileUrl(BotConstants.SAMPLE_IMAGES[i]))));
    }

    return new BusinessMessagesCarouselCard()
        .setCardContents(cardContents)
        .setCardWidth(CardWidth.MEDIUM.toString());
  }

  /**
   * Suggestions to add to sample cards.
   *
   * @return List of suggestions.
   */
  private List<BusinessMessagesSuggestion> getCardSuggestions() {
    List<BusinessMessagesSuggestion> suggestions = new ArrayList<>();

    suggestions.add(
        new BusinessMessagesSuggestion()
            .setReply(new BusinessMessagesSuggestedReply()
                .setText("\uD83D\uDC4D Like").setPostbackData("like-item")));

    suggestions.add(
        new BusinessMessagesSuggestion()
            .setReply(new BusinessMessagesSuggestedReply()
                .setText("\uD83D\uDC4E Dislike").setPostbackData("dislike-item")));

    return suggestions;
  }

  /**
   * The normalizedMessage should be formatted as "speak french", "speak chinese", etc. the
   * specified langauge is parsed and mapped to a supported language. If no supported language is
   * found, an error response is shown.
   *
   * @param normalizedMessage The inbound request from the user.
   * @param conversationId The conversation ID that uniquely maps to the user and agent.
   */
  private void attemptTranslation(String normalizedMessage, String conversationId) {
    String language = normalizedMessage.replace("speak ", "").trim();

    // Trim any extra text
    if (language.indexOf(" ") > 0) {
      language = language.substring(0, language.indexOf(" "));
    }

    logger.info("Trying to translate to language: " + language);

    // Attempt to match input language to language map
    if (BotConstants.LANGUAGE_MAP.containsKey(language)) {
      String languageCode = BotConstants.LANGUAGE_MAP.get(language);

      Translate translate = TranslateOptions.getDefaultInstance().getService();
      Translation translation =
          translate.translate(
              BotConstants.RSP_TO_TRANSLATION,
              Translate.TranslateOption.sourceLanguage("en"),
              Translate.TranslateOption.targetLanguage(languageCode),
              Translate.TranslateOption.format("text"),
              Translate.TranslateOption.model("base"));

      sendResponse(translation.getTranslatedText(), conversationId);
    } else { // No matching language found, show default response
      String noLanguageMatch = "Sorry, but " + language + " is not a supported language.\n\n" +
          "Here is the list of supported languages: ";

      StringBuilder sb = new StringBuilder();
      for (String languageName : BotConstants.LANGUAGE_MAP.keySet()) {
        if (sb.length() != 0) {
          sb.append(", ");
        }
        // Upper case the first letter of the language
        sb.append(languageName.substring(0, 1).toUpperCase());
        sb.append(languageName.substring(1));
      }

      noLanguageMatch += sb.toString();

      sendResponse(noLanguageMatch, conversationId);
    }
  }

  /**
   * Posts a message to the Business Messages API with the contains rich text
   * flag set to true. This will make it so the markdown expressions for bold,
   * italics, and linkified text will render correctly.
   *
   * @param message The message text to send the user.
   * @param conversationId The conversation ID that uniquely maps to the user and agent.
   */
  private void sendRichResponse(String message, String conversationId) {
    try {
      // Send plaintext message with default menu to user
      sendResponse(new BusinessMessagesMessage()
          .setMessageId(UUID.randomUUID().toString())
          .setText(message)
          .setContainsRichText(true)
          .setRepresentative(representative)
          .setFallback(message)
          .setSuggestions(getDefaultMenu()), conversationId);
    } catch (Exception e) {
      logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
    }
  }

  /**
   * Posts a message to the Business Messages API.
   *
   * @param message The message text to send the user.
   * @param conversationId The conversation ID that uniquely maps to the user and agent.
   */
  private void sendResponse(String message, String conversationId) {
    try {
      // Send plaintext message with default menu to user
      sendResponse(new BusinessMessagesMessage()
          .setMessageId(UUID.randomUUID().toString())
          .setText(message)
          .setRepresentative(representative)
          .setFallback(message)
          .setSuggestions(getDefaultMenu()), conversationId);
    } catch (Exception e) {
      logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
    }
  }

  /**
   * Posts a message to the Business Messages API, first sending a typing indicator event and
   * sending a stop typing event after the message has been sent.
   *
   * @param message The message object to send the user.
   * @param conversationId The conversation ID that uniquely maps to the user and agent.
   */
  private void sendResponse(BusinessMessagesMessage message,
      String conversationId) {
    try {
      // Send typing indicator
      BusinessMessagesEvent event =
          new BusinessMessagesEvent()
              .setEventType(EventType.TYPING_STARTED.toString());

      Businessmessages.Conversations.Events.Create request
          = builder.build().conversations().events()
          .create("conversations/" + conversationId, event);

      request.setEventId(UUID.randomUUID().toString());
      request.execute();

      logger.info("message id: " + message.getMessageId());
      logger.info("message body: " + message.toPrettyString());

      // Send the message
      Businessmessages.Conversations.Messages.Create messageRequest
          = builder.build().conversations().messages()
          .create("conversations/" + conversationId, message);

      messageRequest.execute();

      // Stop typing indicator
      event =
          new BusinessMessagesEvent()
              .setEventType(EventType.TYPING_STOPPED.toString());

      request
          = builder.build().conversations().events()
          .create("conversations/" + conversationId, event);

      request.setEventId(UUID.randomUUID().toString());
      request.execute();
    } catch (Exception e) {
      logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
    }
  }

  /**
   * Creates the default menu items for responses.
   *
   * @return List of suggestions to form a menu.
   */
  private List<BusinessMessagesSuggestion> getDefaultMenu() {
    List<BusinessMessagesSuggestion> suggestions = new ArrayList<>();
    suggestions.add(getHelpMenuItem());

    suggestions.add(new BusinessMessagesSuggestion()
        .setReply(new BusinessMessagesSuggestedReply()
            .setText("Rich card").setPostbackData("card")
        ));

    suggestions.add(new BusinessMessagesSuggestion()
        .setReply(new BusinessMessagesSuggestedReply()
            .setText("Carousel").setPostbackData("carousel")
        ));

    if (representative.getRepresentativeType().equals(RepresentativeType.HUMAN.toString())) {
      suggestions.add(new BusinessMessagesSuggestion()
          .setReply(new BusinessMessagesSuggestedReply()
              .setText("Back to bot").setPostbackData("back_to_bot")
          ));
    }

    return suggestions;
  }

  /**
   * Get the help menu suggested reply.
   *
   * @return A help suggested reply.
   */
  private BusinessMessagesSuggestion getHelpMenuItem() {
    return new BusinessMessagesSuggestion()
        .setReply(new BusinessMessagesSuggestedReply()
            .setText("Help").setPostbackData("help")
        );
  }

  /**
   * Initializes credentials used by the Business Messages API.
   */
  private void initCredentials() {
    logger.info("Initializing credentials for Business Messages.");

    try {
      this.credential = GoogleCredential.getApplicationDefault();
      this.credential = credential.createScoped(Arrays.asList(
          "https://www.googleapis.com/auth/businessmessages"));

      this.credential.refreshToken();
    } catch (Exception e) {
      logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
    }
  }

  /**
   * Initializes the BM API object.
   */
  private void initBmApi() {
    try {
      if (this.credential == null) {
        initCredentials();
      }

      HttpTransport httpTransport = GoogleNetHttpTransport.newTrustedTransport();
      JacksonFactory jsonFactory = JacksonFactory.getDefaultInstance();

      // create instance of the BM API
      builder = new Businessmessages
          .Builder(httpTransport, jsonFactory, null)
          .setApplicationName("BM Kitchen Sink");

      // set the API credentials and endpoint
      builder.setHttpRequestInitializer(credential);
      builder.setRootUrl(BotConstants.BM_API_URL);
    } catch (Exception e) {
      logger.log(Level.SEVERE, EXCEPTION_WAS_THROWN, e);
    }
  }
}
