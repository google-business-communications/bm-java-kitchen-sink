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

import java.util.HashMap;
import java.util.Map;

public interface BotConstants {

  // the URL for the API endpoint
  String BM_API_URL = "https://businessmessages.googleapis.com/";

  // Map of all supported languages for translation
  Map<String, String> LANGUAGE_MAP = new HashMap<String, String>() {{
    put("afrikaans", "af");
    put("albanian", "sq");
    put("amharic", "am");
    put("arabic", "ar");
    put("armenian", "hy");
    put("azerbaijani", "az");
    put("basque", "eu");
    put("belarusian", "be");
    put("bengali", "bn");
    put("bosnian", "bs");
    put("bulgarian", "bg");
    put("catalan", "ca");
    put("cebuano", "ceb");
    put("chinese", "zh-CN");
    put("corsican", "co");
    put("croatian", "hr");
    put("czech", "cs");
    put("danish", "da");
    put("dutch", "nl");
    put("english", "en");
    put("esperanto", "eo");
    put("estonian", "et");
    put("finnish", "fi");
    put("french", "fr");
    put("frisian", "fy");
    put("galician", "gl");
    put("georgian", "ka");
    put("german", "de");
    put("greek", "el");
    put("gujarati", "gu");
    put("haitian", "ht");
    put("hausa", "ha");
    put("hawaiian", "haw");
    put("hebrew", "he");
    put("hindi", "hi");
    put("hmong", "hmn");
    put("hungarian", "hu");
    put("icelandic", "is");
    put("igbo", "ig");
    put("indonesian", "id");
    put("irish", "ga");
    put("italian", "it");
    put("japanese", "ja");
    put("javanese", "jw");
    put("kannada", "kn");
    put("kazakh", "kk");
    put("khmer", "km");
    put("korean", "ko");
    put("kurdish", "ku");
    put("kyrgyz", "ky");
    put("lao", "lo");
    put("latin", "la");
    put("latvian", "lv");
    put("lithuanian", "lt");
    put("luxembourgish", "lb");
    put("macedonian", "mk");
    put("malagasy", "mg");
    put("malay", "ms");
    put("malayalam", "ml");
    put("maltese", "mt");
    put("maori", "mi");
    put("marathi", "mr");
    put("mongolian", "mn");
    put("myanmar", "my");
    put("burmemes", "my");
    put("nepali", "ne");
    put("norwegian", "no");
    put("nyanja", "ny");
    put("chichewa", "ny");
    put("pashto", "ps");
    put("persian", "fa");
    put("polish", "pl");
    put("portuguese", "pt");
    put("punjabi", "pa");
    put("romanian", "ro");
    put("russian", "ru");
    put("samoan", "sm");
    put("scots gaelic", "gd");
    put("scottish", "gd");
    put("serbian", "sr");
    put("sesotho", "st");
    put("shona", "sn");
    put("sindhi", "sd");
    put("sinhala", "si");
    put("sinhalese", "si");
    put("slovak", "sk");
    put("slovenian", "sl");
    put("somali", "so");
    put("spanish", "es");
    put("sundanese", "su");
    put("swahili", "sw");
    put("swedish", "sv");
    put("tagalog", "tl");
    put("filipino", "tl");
    put("tajik", "tg");
    put("tamil", "ta");
    put("telugu", "te");
    put("thai", "th");
    put("turkish", "tr");
    put("ukrainian", "uk");
    put("urdu", "ur");
    put("uzbek", "uz");
    put("vietnamese", "vi");
    put("welsh", "cy");
    put("xhosa", "xh");
    put("yiddish", "yi");
    put("yoruba", "yo");
    put("zulu", "zu");
  }};

  String LIVE_AGENT_NAME = "Sally";
  String BOT_AGENT_NAME = "BM Welcome Bot";

  String LIVE_AGENT_AVATAR = "https://storage.googleapis.com/sample-avatars-for-bm/live-avatar.jpg";
  String BOT_AGENT_AVATAR = "https://storage.googleapis.com/sample-avatars-for-bm/bot-avatar.jpg";

  // List of recognized commands to produce certain responses
  String CMD_LOREM_IPSUM = "lorem ipsum";
  String CMD_MEDIUM_TEXT = "medium text";
  String CMD_LONG_TEXT = "long text";
  String CMD_SPEAK = "^speak.*";
  String CMD_LINK = "link";
  String CMD_DIAL = "dial";
  String CMD_CARD = "card";
  String CMD_CAROURSEL = "carousel";
  String CMD_WHO = "^who.*";
  String CMD_HELP = "^help.*|^commands\\s.*|see the help menu";
  String CMD_BACK_TO_BOT = "back_to_bot";
  String CMD_CSAT_TRIGGER = "csat.*";
  String CMD_LIVE_AGENT = "live agent";
  String CMD_CHIPS = "chips";

  // List of pre-programmed responses
  String RSP_LOREM_IPSUM = "Lorem ipsum dolor sit amet, consectetur " +
      "adipiscing elit, sed do eiusmod tempor incididunt ut labore et dolore magna aliqua. Ut " +
      "enim ad minim veniam, quis nostrud exercitation ullamco laboris nisi ut aliquip ex ea " +
      "commodo consequat. Duis aute irure dolor in reprehenderit in voluptate velit esse " +
      "cillum dolore eu fugiat nulla pariatur. Excepteur sint occaecat cupidatat non proident, " +
      "sunt in culpa qui officia deserunt mollit anim id est laborum.";

  String RSP_MEDIUM_TEXT = "Google was founded in September 1998 by Larry " +
      "Page and Sergey Brin while they were Ph.D. students at Stanford University in " +
      "California. They incorporated Google as a California publicly held company on " +
      "September 4, 1998, in California. Google was then reincorporated in Delaware on October " +
      "22, 2002. An initial public offering (IPO) took place on August 19, 2004, and Google " +
      "moved to its headquarters in Mountain View, California, nicknamed the Googleplex. In " +
      "August 2015, Google announced plans to reorganize its various interests as a " +
      "conglomerate called Alphabet Inc. Google is Alphabet's leading subsidiary and will " +
      "continue to be the umbrella company for Alphabet's Internet interests. Sundar Pichai " +
      "was appointed CEO of Google, replacing Larry Page who became the CEO of Alphabet.";

  String RSP_LONG_TEXT = "Google was founded in September 1998 by Larry " +
      "Page and Sergey Brin while they were Ph.D. students at Stanford University in " +
      "California. They incorporated Google as a California publicly held company on " +
      "September 4, 1998, in California. Google was then reincorporated in Delaware on October " +
      "22, 2002. An initial public offering (IPO) took place on August 19, 2004, and Google " +
      "moved to its headquarters in Mountain View, California, nicknamed the Googleplex. In " +
      "August 2015, Google announced plans to reorganize its various interests as a " +
      "conglomerate called Alphabet Inc. Google is Alphabet's leading subsidiary and will " +
      "continue to be the umbrella company for Alphabet's Internet interests. Sundar Pichai " +
      "was appointed CEO of Google, replacing Larry Page who became the CEO of Alphabet.\n\n" +
      "The company's rapid growth since incorporation has triggered a chain of products, " +
      "acquisitions, and partnerships beyond Google's core search engine (Google Search). " +
      "It offers services designed for work and productivity (Google Docs, Google Sheets, " +
      "and Google Slides), email (Gmail), scheduling and time management (Google Calendar), " +
      "cloud storage (Google Drive), instant messaging and video chat (Duo, Hangouts," +
      " Messages!!!!!!!), language translation (Google Translate), mapping and navigation " +
      "(Google Maps, Waze, Google Earth, Street View), video sharing (YouTube), " +
      "note-taking (Google Keep), and photo organizing and editing (Google Photos). The " +
      "company leads the development of the Android mobile operating system, the Google " +
      "Chrome web browser, and Chrome OS, a lightweight operating system based on the " +
      "Chrome browser. Google has moved increasingly into hardware; from 2010 to 2015, it " +
      "partnered with major electronics manufacturers in the production of its Nexus " +
      "devices, and it released multiple hardware products in October 2016, including the " +
      "Google Pixel smartphone, Google Home smart speaker, Google Wifi mesh wireless " +
      "router, and Google Daydream virtual reality headset. Google has also experimented " +
      "with becoming an Internet carrier (Google Fiber, Google Fi, and Google Station).\n\n" +
      "Google.com is the most visited website in the world.[14] Several other Google " +
      "services also figure in the top 100 most visited websites, including YouTube and " +
      "Blogger. Google's mission statement is \"to organize the world's information and " +
      "make it universally accessible and useful\". The company's unofficial slogan " +
      "\"Don't be evil\" was removed from the company's code of conduct around May 2018, " +
      "but reinstated by July 31, 2018.";

  String RSP_TO_TRANSLATION = "Hello there. How are you doing? I hope you " +
      "are having a great day!";

  String RSP_LINK_TEXT = "Go to Google!";

  String RSP_CHIP_TEXT = "Example suggested replies";

  String RSP_LIVE_AGENT_TEXT = "Example a live agent request action";

  String RSP_DIAL_TEXT = "Give me a call!";

  String RSP_WHO_TEXT = "This program was created to help demonstrate the capabilities of " +
      "the Business Messages platform.";

  String RSP_HELP_TEXT = "Welcome to the help :-). This program will echo " +
      "any text that you enter that is not part of a supported command. The supported " +
      "commands are: \n\n" +
      "Help - Shows the list of supported commands and functions\n\n" +
      "Lorem ipsum - Will respond with Lorem Ipsum text\n\n" +
      "Medium text - Will respond with a medium length paragraph\n\n" +
      "Long text - Will create a long multi-paragraph response\n\n" +
      "Speak XYZ - Will respond in the language of choice where XYZ corresponds " +
      "to a recognized language\n\n" +
      "Who - Shows a description this product\n\n" +
      "CSAT - Triggers the CSAT survey\n\n" +
      "Link - Shows an open url action to https://www.google.com\n\n" +
      "Dial - Shows a dial action to +12223334444\n\n" +
      "Live agent - Shows an Request a live agent chip\n\n" +
      "Chips - Shows an example chip list\n\n" +
      "Card - Shows a sample rich card\n\n" +
      "Carousel - Shows a sample carousel rich card";

  String RSP_LIVE_AGENT_TRANSFER = "Hey there, you are now chatting with a live agent " +
      "(not really, but let's pretend).";

  String RSP_BOT_TRANSFER = "Hey there, you are now chatting with a bot.";

  // Images used in cards and carousel examples
  String SAMPLE_IMAGES[] = new String[]{
      "https://storage.googleapis.com/kitchen-sink-sample-images/cute-dog.jpg",
      "https://storage.googleapis.com/kitchen-sink-sample-images/elephant.jpg",
      "https://storage.googleapis.com/kitchen-sink-sample-images/adventure-cliff.jpg",
      "https://storage.googleapis.com/kitchen-sink-sample-images/sheep.jpg",
      "https://storage.googleapis.com/kitchen-sink-sample-images/golden-gate-bridge.jpg"
  };
}
