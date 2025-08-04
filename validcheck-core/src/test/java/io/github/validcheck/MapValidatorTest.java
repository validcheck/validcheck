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
    Check.check("map", Map.of("key", "value")).notNull();

    assertThatThrownBy(() -> check("map", (Map<String, String>) null).notNull())
        .hasMessage("'map' must not be null");
  }

  @Test
  void isNull() {
    check("map", (Map<String, String>) null).isNull();

    assertThatThrownBy(() -> check("map", Map.of("key", "value")).isNull())
        .hasMessage("'map' must be null, but it was {key=value}");
  }

  @Test
  void empty() {
    check("map", Collections.emptyMap()).empty();
    check(Collections.emptyMap()).empty();

    assertThatThrownBy(() -> check("map", Map.of("key", "value")).empty())
        .hasMessage("'map' must be empty, but it was {key=value}");
  }

  @Test
  void notEmpty() {
    check("map", Map.of("key", "value")).notEmpty();

    assertThatThrownBy(() -> check("map", Collections.emptyMap()).notEmpty())
        .hasMessage("'map' must not be empty");

    assertThatThrownBy(() -> check("map", (Map<String, String>) null).notEmpty())
        .hasMessage("'map' must not be empty");
  }

  @Test
  void size() {
    check("map", Map.of("key1", "value1", "key2", "value2")).size(2);

    assertThatThrownBy(() -> check("map", Map.of("key", "value")).size(2))
        .hasMessage("'map' must have size 2, but it was {key=value}");
  }

  @Test
  void minSize() {
    check("map", Map.of("key1", "value1", "key2", "value2", "key3", "value3")).minSize(2);
    check("exact", Map.of("key1", "value1", "key2", "value2")).minSize(2);

    assertThatThrownBy(() -> check("small", Map.of("key", "value")).minSize(3))
        .hasMessage("'small' must have at least 3 entry(ies), but it was {key=value}");
    assertThatThrownBy(() -> check("empty", Collections.emptyMap()).minSize(1))
        .hasMessage("'empty' must have at least 1 entry(ies), but it was {}");
  }

  @Test
  void maxSize() {
    check("map", Map.of("key", "value")).maxSize(3);
    check("exact", Map.of("key1", "value1", "key2", "value2")).maxSize(2);
    check("empty", Collections.emptyMap()).maxSize(0);

    var largeMap = Map.of("key1", "value1", "key2", "value2", "key3", "value3", "key4", "value4");
    assertThatThrownBy(() -> check("large", largeMap).maxSize(2))
        .hasMessageContaining("'large' must have at most 2 entry(ies)");
  }

  @Test
  void sizeBetween() {
    check("valid", Map.of("key1", "value1", "key2", "value2", "key3", "value3")).sizeBetween(2, 5);
    check("minimum", Map.of("key1", "value1", "key2", "value2")).sizeBetween(2, 5);
    check(
            "maximum",
            Map.of(
                "key1", "value1", "key2", "value2", "key3", "value3", "key4", "value4", "key5",
                "value5"))
        .sizeBetween(2, 5);
    check("exact", Map.of("key1", "value1", "key2", "value2", "key3", "value3")).sizeBetween(3, 3);

    assertThatThrownBy(() -> check("small", Map.of("key", "value")).sizeBetween(3, 5))
        .hasMessage("'small' must have between 3 and 5 entry(ies), but it was {key=value}");

    var largeMap = new TreeMap<String, String>();
    largeMap.put("key1", "value1");
    largeMap.put("key2", "value2");
    largeMap.put("key3", "value3");
    largeMap.put("key4", "value4");
    largeMap.put("key5", "value5");
    largeMap.put("key6", "value6");
    assertThatThrownBy(() -> check("large", largeMap).sizeBetween(2, 4))
        .hasMessage(
            "'large' must have between 2 and 4 entry(ies), but it was {key1=value1, key2=value2, key3=value3, key4=value4, key5=value5, key6=value6}");
  }

  @Test
  void containsKey() {
    check("map", Map.of("key1", "value1", "key2", "value2")).containsKey("key1");

    assertThatThrownBy(() -> check("map", Map.of("key1", "value1")).containsKey("key2"))
        .hasMessage("'map' must contain key 'key2'");
  }

  @Test
  void doesNotContainKey() {
    check("map", Map.of("key1", "value1", "key2", "value2")).doesNotContainKey("key3");

    assertThatThrownBy(() -> check("map", Map.of("key1", "value1")).doesNotContainKey("key1"))
        .hasMessage("'map' must not contain key 'key1'");
  }

  @Test
  void containsValue() {
    check("map", Map.of("key1", "value1", "key2", "value2")).containsValue("value1");

    assertThatThrownBy(() -> check("map", Map.of("key1", "value1")).containsValue("value2"))
        .hasMessage("'map' must contain value 'value2'");
  }

  @Test
  void doesNotContainValue() {
    check("map", Map.of("key1", "value1", "key2", "value2")).doesNotContainValue("value3");

    assertThatThrownBy(() -> check("map", Map.of("key1", "value1")).doesNotContainValue("value1"))
        .hasMessage("'map' must not contain value 'value1'");
  }

  @Test
  void containsAllKeys() {
    check("map", Map.of("key1", "value1", "key2", "value2", "key3", "value3"))
        .containsAllKeys("key1", "key2");

    assertThatThrownBy(() -> check("map", Map.of("key1", "value1")).containsAllKeys("key1", "key2"))
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
    check("valid", Map.of("key1", "value1", "key2", "value1"))
        .satisfies(m -> m.values().stream().allMatch(v -> v.equals("value1")), "Error 1");
    batch()
        .check("valid", Map.of("key1", 1, "key2", 2))
        .satisfies(m -> m.containsValue(1), "Error 2");
    Check.withConfig(ValidationConfig.DEFAULT)
        .check("valid", Map.of("key1", 1, "key2", 2))
        .satisfies(m -> m.containsValue(2), "Error 2");
  }

  @Test
  void withMessage() {
    assertThatThrownBy(
            () -> check("map", Collections.emptyMap()).withMessage("Custom error").notEmpty())
        .hasMessage("Custom error");
  }
}
