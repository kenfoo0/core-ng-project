package core.framework.db;

import java.util.List;
import java.util.Optional;

/**
 * @author neo
 */
public interface Query<T> {
    void where(String condition, Object... params);

    void orderBy(String sort);

    void groupBy(String groupBy);

    void skip(int skip);

    void limit(int limit);

    List<T> fetch();

    Optional<T> fetchOne();

    <P> Optional<P> project(String projection, Class<P> viewClass);

    // refer to https://dev.mysql.com/doc/refman/8.0/en/group-by-functions.html#function_count, count function return BIGINT
    default long count() {
        return project("count(1)", Long.class).orElseThrow();
    }
}
