package pkovacs.util;

import java.util.List;
import java.util.regex.MatchResult;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

class RegexUtilsTest {

    @Test
    void test() {
        String s = "Hello World! I have 5 apples and 12 bananas. -42 is the opposite of 42.";
        String regex = "-?\\d+";

        assertFalse(RegexUtils.matches(regex, s));
        assertTrue(RegexUtils.matches(".* " + regex + " is .*", s));

        assertEquals("5", RegexUtils.findFirst(regex, s));
        assertEquals(List.of("5", "12", "-42", "42"), RegexUtils.findAll(regex, s));

        assertEquals(List.of("5", "12", "-42", "42"),
                RegexUtils.allMatches(regex, s).stream().map(MatchResult::group).toList());
        assertEquals(List.of(20, 33, 45, 68),
                RegexUtils.allMatches(regex, s).stream().map(MatchResult::start).toList());

        assertEquals("Hello World! I have [+5] apples and [+12] bananas. [-42] is the opposite of [+42].",
                RegexUtils.replaceAll(regex, s,
                        r -> "[" + (r.group().startsWith("-") ? "" : "+") + r.group() + "]"));
    }

}
