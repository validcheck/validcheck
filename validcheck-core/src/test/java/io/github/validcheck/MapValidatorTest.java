package io.github.validcheck;

import static io.github.validcheck.Check.batch;
import static io.github.validcheck.Check.check;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;
import org.junit.jupiter.api.Test;

class MapValidatorTest {

  @Test
  void notNull() {
    Check.check(Map.of("key", "value"), "map").notNull();

    assertThatThrownBy(() -> check((Map<String, String>) null, "map").notNull())
        .hasMessage("'map' must not be null");
  }

  @Test
  void isNull() {
    check((Map<String, String>) null, "map").isNull();

    assertThatThrownBy(() -> check(Map.of("key", "value"), "map").isNull())
        .hasMessage("'map' must be null, but it was {key=value}");
  }

  @Test
  void empty() {
    check(Collections.emptyMap(), "map").empty();
    check(Collections.emptyMap()).empty();

    assertThatThrownBy(() -> check(Map.of("key", "value"), "map").empty())
        .hasMessage("'map' must be empty, but it was {key=value}");
  }

  @Test
  void notEmpty() {
    check(Map.of("key", "value"), "map").notEmpty();

    assertThatThrownBy(() -> check(Collections.emptyMap(), "map").notEmpty())
        .hasMessage("'map' must not be empty");

    assertThatThrownBy(() -> check((Map<String, String>) null, "map").notEmpty())
        .hasMessage("'map' must not be empty");
  }

  @Test
  void size() {
    check(Map.of("key1", "value1", "key2", "value2"), "map").size(2);

    assertThatThrownBy(() -> check(Map.of("key", "value"), "map").size(2))
        .hasMessage("'map' must have size 2, but it was {key=value}");
  }

  @Test
  void minSize() {
    check(Map.of("key1", "value1", "key2", "value2", "key3", "value3"), "map").minSize(2);
    check(Map.of("key1", "value1", "key2", "value2"), "exact").minSize(2);

    assertThatThrownBy(() -> check(Map.of("key", "value"), "small").minSize(3))
        .hasMessage("'small' must have at least 3 entry(ies), but it was {key=value}");
    assertThatThrownBy(() -> check(Collections.emptyMap(), "empty").minSize(1))
        .hasMessage("'empty' must have at least 1 entry(ies), but it was {}");
  }

  @Test
  void maxSize() {
    check(Map.of("key", "value"), "map").maxSize(3);
    check(Map.of("key1", "value1", "key2", "value2"), "exact").maxSize(2);
    check(Collections.emptyMap(), "empty").maxSize(0);

    var largeMap = Map.of("key1", "value1", "key2", "value2", "key3", "value3", "key4", "value4");
    assertThatThrownBy(() -> check(largeMap, "large").maxSize(2))
        .hasMessageContaining("'large' must have at most 2 entry(ies)");
  }

  @Test
  void sizeBetween() {
    check(Map.of("key1", "value1", "key2", "value2", "key3", "value3"), "valid").sizeBetween(2, 5);
    check(Map.of("key1", "value1", "key2", "value2"), "minimum").sizeBetween(2, 5);
    check(
            Map.of(
                "key1", "value1", "key2", "value2", "key3", "value3", "key4", "value4", "key5",
                "value5"),
            "maximum")
        .sizeBetween(2, 5);
    check(Map.of("key1", "value1", "key2", "value2", "key3", "value3"), "exact").sizeBetween(3, 3);

    assertThatThrownBy(() -> check(Map.of("key", "value"), "small").sizeBetween(3, 5))
        .hasMessage("'small' must have between 3 and 5 entry(ies), but it was {key=value}");

    var largeMap = new TreeMap<String, String>();
    largeMap.put("key1", "value1");
    largeMap.put("key2", "value2");
    largeMap.put("key3", "value3");
    largeMap.put("key4", "value4");
    largeMap.put("key5", "value5");
    largeMap.put("key6", "value6");
    assertThatThrownBy(() -> check(largeMap, "large").sizeBetween(2, 4))
        .hasMessage(
            "'large' must have between 2 and 4 entry(ies), but it was {key1=value1, key2=value2, key3=value3, key4=value4, key5=value5, key6=value6}");
  }

  @Test
  void containsKey() {
    check(Map.of("key1", "value1", "key2", "value2"), "map").containsKey("key1");

    assertThatThrownBy(() -> check(Map.of("key1", "value1"), "map").containsKey("key2"))
        .hasMessage("'map' must contain key 'key2'");
  }

  @Test
  void doesNotContainKey() {
    check(Map.of("key1", "value1", "key2", "value2"), "map").doesNotContainKey("key3");

    assertThatThrownBy(() -> check(Map.of("key1", "value1"), "map").doesNotContainKey("key1"))
        .hasMessage("'map' must not contain key 'key1'");
  }

  @Test
  void containsValue() {
    check(Map.of("key1", "value1", "key2", "value2"), "map").containsValue("value1");

    assertThatThrownBy(() -> check(Map.of("key1", "value1"), "map").containsValue("value2"))
        .hasMessage("'map' must contain value 'value2'");
  }

  @Test
  void doesNotContainValue() {
    check(Map.of("key1", "value1", "key2", "value2"), "map").doesNotContainValue("value3");

    assertThatThrownBy(() -> check(Map.of("key1", "value1"), "map").doesNotContainValue("value1"))
        .hasMessage("'map' must not contain value 'value1'");
  }

  @Test
  void containsAllKeys() {
    check(Map.of("key1", "value1", "key2", "value2", "key3", "value3"), "map")
        .containsAllKeys("key1", "key2");

    assertThatThrownBy(() -> check(Map.of("key1", "value1"), "map").containsAllKeys("key1", "key2"))
        .hasMessage("'map' must contain all keys [key1, key2]");
  }

  @Test
  void whenThen() {
    check(Collections.emptyMap()).satisfies(Map::isEmpty, "OK");
    batch()
        .check(Map.of("key", "value"))
        .when(true, v -> ((MapValidator<Map<String, String>>) v).maxSize(1));
    assertThatThrownBy(() -> check(Collections.emptyMap()).whenMap(true, m -> m.minSize(1)))
        .hasMessage("parameter must have at least 1 entry(ies), but it was {}");
    check(Map.of("key", "value")).whenMap(true, v -> v.satisfies(m -> !m.isEmpty(), "Error"));
  }

  @Test
  void satisfies() {
    check(Map.of("key1", "value1", "key2", "value1"), "valid")
        .satisfies(m -> m.values().stream().allMatch(v -> v.equals("value1")), "Error 1");
    batch()
        .check(Map.of("key1", 1, "key2", 2), "valid")
        .satisfies(m -> m.containsValue(1), "Error 2");
    Check.withConfig(ValidationConfig.DEFAULT)
        .check(Map.of("key1", 1, "key2", 2), "valid")
        .satisfies(m -> m.containsValue(2), "Error 2");
  }

  @Test
  void withMessage() {
    assertThatThrownBy(
            () -> check(Collections.emptyMap(), "map").withMessage("Custom error").notEmpty())
        .hasMessage("Custom error");
  }
}
