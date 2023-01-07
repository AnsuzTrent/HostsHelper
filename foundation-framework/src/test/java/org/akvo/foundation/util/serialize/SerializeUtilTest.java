package org.akvo.foundation.util.serialize;

import org.akvo.foundation.util.serialize.datatype.JsonSerializer;
import org.junit.jupiter.api.Test;

import java.time.*;
import java.util.*;
import java.util.stream.Stream;

class SerializeUtilTest {
    @Test
    void testTree() {
        //模拟从数据库查询出来
        List<Menu> menus = List.of(
            new Menu(1, "根节点", 0),
            new Menu(2, "子节点1", 1),
            new Menu(3, "子节点1.1", 2),
            new Menu(4, "子节点1.2", 2),
            new Menu(5, "根节点1.3", 2),
            new Menu(6, "根节点2", 1),
            new Menu(7, "根节点2.1", 6),
            new Menu(8, "根节点2.2", 6),
            new Menu(9, "根节点2.2.1", 7),
            new Menu(10, "根节点2.2.2", 7),
            new Menu(11, "根节点3", 1),
            new Menu(12, "根节点3.1", 11)
        );

        //获取父节点
        List<Menu> collect = menus.stream()
            .filter(m -> m.parentId() == 0)
            .peek(m -> m.setChildList(getChildren(m, menus)))
            .toList();
        System.out.println("-------转json输出结果-------");
        System.out.println(JsonSerializer.INSTANCE.serializePretty(collect));
    }

    /**
     * 递归查询子节点
     *
     * @param root 根节点
     * @param all  所有节点
     * @return 根节点信息
     */
    private List<Menu> getChildren(Menu root, List<Menu> all) {
        return all.stream()
            .filter(m -> Objects.equals(m.parentId(), root.id()))
            .peek(m -> m.setChildList(getChildren(m, all)))
            .toList();
    }

    @Test
    void serializeTest() {
        Stream.of(
                Year.now(),
                YearMonth.now(),
                MonthDay.now(),
                Clock.systemDefaultZone(),
                Clock.systemUTC(),
                Instant.now(),
                Duration.ofDays(1)
                    .plusHours(2)
                    .plusMinutes(15)
                    .plusSeconds(323)
                    .plusMillis(5132)
                    .plusNanos(23156),
                Period.ofYears(5)
                    .plusMonths(51)
                    .plusDays(451),
                LocalDate.now(),
                LocalTime.now(),
                LocalDateTime.now(),
                OffsetTime.now(),
                OffsetDateTime.now(),
                ZonedDateTime.now(),
                ZoneId.systemDefault(),
                ZoneOffset.MIN,
                ZoneOffset.UTC,
                ZoneOffset.systemDefault(),
                ZoneOffset.MAX,
                new Date(),
                Calendar.getInstance()
            )
            .map(this::test)
            .filter(Objects::nonNull)
            .toList()
            .forEach(System.err::println);

    }

    @SuppressWarnings("unchecked")
    private <T> String test(T o) {
        JsonSerializer instance = JsonSerializer.INSTANCE;
        Class<T> aClass = (Class<T>) o.getClass();
        String simpleName = aClass.getSimpleName();
        System.out.println(simpleName);
        System.out.println(o);
        String serialize = instance.serialize(o);
        System.out.println(serialize);
        T deserialize = instance.deserialize(serialize, aClass);
        System.out.println(deserialize);
        String serialize1 = instance.serialize(deserialize);
        System.out.println(serialize1);
        boolean equals = Objects.equals(o, deserialize);
        System.out.println(equals);
        System.out.println();
        return equals ? null : simpleName + "|" + serialize + "|" + serialize1;
    }

    /**
     * @param id        id
     * @param name      名称
     * @param parentId  父id ，根节点为0
     * @param childList 子节点信息
     */
    record Menu(Integer id, String name, Integer parentId, List<Menu> childList) {
        public Menu(Integer id, String name, Integer parentId) {
            this(id, name, parentId, new ArrayList<>());
        }

        public void setChildList(List<Menu> childList) {
            this.childList.clear();
            this.childList.addAll(childList);
        }
    }
}


