package com.sawoo.pipeline.api.service.todo;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.context.annotation.Profile;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@TestMethodOrder(MethodOrderer.Alphanumeric.class)
@Tag(value = "service")
@Profile(value = {"unit-tests", "unit-tests-embedded"})
class TodoMessagePatternTest {

    private static final Pattern MESSAGE_PATTERN = Pattern.compile(".*?\\{\\{(.*?)\\}\\}.*?", Pattern.DOTALL);

    @Test
    @DisplayName("matches: message with parentheses")
    void matchesWhenTextHasParenthesesReturnsInvalid() {
        String message1 = "This a message with variables. {{prospect_name}} So it should be an invalid message";
        Matcher matcher1 = MESSAGE_PATTERN.matcher(message1);
        Assertions.assertTrue(matcher1.matches(), String.format("Message: [%s] must match", message1));

        String message2 = "My first message for an analytical prospect.\n\nHello {{ctx:prospect_name}}. Nice to e-meet you.\n\nSaludos.";
        Matcher matcher2 = MESSAGE_PATTERN.matcher(message2);
        Assertions.assertTrue(matcher2.matches(), String.format("Message: [%s] must match", message2));
    }

    @Test
    @DisplayName("matches: message with parentheses")
    void matchesWhenTextHasParenthesesTwiceReturnsInvalid() {
        String message = "This a message with variables. {{ctx:prospect_name}} So it should be an invalid message. More parentheses {{company_name}}";
        Matcher matcher = MESSAGE_PATTERN.matcher(message);
        Assertions.assertTrue(matcher.matches(), String.format("Message: [%s] must match", message));
    }

    @Test
    @DisplayName("matches: message with no parentheses")
    void matchesWhenTextHasNoParenthesesReturnsValid() {
        String message = "This a message without variables. So it should be an invalid message";
        Matcher matcher = MESSAGE_PATTERN.matcher(message);
        Assertions.assertFalse(matcher.matches(), String.format("Message: [%s] can not match", message));
    }

    @Test
    @DisplayName("results: message with 2 matches - Success")
    void matchesWhenTextHasTwoMatchesReturnsSuccess() {
        String PROSPECT_NAME_VARIABLE = "prospect_name";
        String COMPANY_NAME_VARIABLE = "company_name";
        String message = "This a message with variables. {{prospect_name}} So it should be an invalid message. More parentheses {{company_name}}";
        int LIST_MATCHES = 2;
        Matcher matcher = MESSAGE_PATTERN.matcher(message);
        List<String> matches = new ArrayList<>();
        while(matcher.find()) {
            matches.add(matcher.group(1));
        }
        Assertions.assertEquals(LIST_MATCHES, matches.size(), String.format("Matches must be: [%d]. Message: [%s]", LIST_MATCHES, message));
        Assertions.assertEquals(PROSPECT_NAME_VARIABLE, matches.get(0), String.format("First match variable must be [%s]", PROSPECT_NAME_VARIABLE));
        Assertions.assertEquals(COMPANY_NAME_VARIABLE, matches.get(1), String.format("Second match variable must be [%s]", COMPANY_NAME_VARIABLE));
    }

    @Test
    @DisplayName("results: message with 1 matches and text html- Success")
    void matchesWhenTextHasOneMatchAndTextIsHTMLReturnsSuccess() {
        String PROSPECT_NAME_VARIABLE = "prospect_name";
        String message = "<p>This is a message with variables {{prospect_name}}. Testing.</p><ul><li>sdasdas</li><li>asdasdas</li><li>asdasd</li></ul><p>asdasdasdas asdaThis is a message with variables {{prospect_name}}. Testing.</p><ul><li>sdasdas</li><li>asdasdas</li><li>asdasd</li></ul> <p>asdasdasdas asda</p>";
        int LIST_MATCHES = 2;
        Matcher matcher = MESSAGE_PATTERN.matcher(message);
        List<String> matches = new ArrayList<>();
        while (matcher.find()) {
            matches.add(matcher.group(1));
        }
        Assertions.assertEquals(LIST_MATCHES, matches.size(), String.format("Matches must be: [%d]. Message: [%s]", LIST_MATCHES, message));
        Assertions.assertEquals(PROSPECT_NAME_VARIABLE, matches.get(0), String.format("First match variable must be [%s]", PROSPECT_NAME_VARIABLE));
    }
}
