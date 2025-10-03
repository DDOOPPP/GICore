package org.gi.gICore.builder;

import org.gi.gICore.util.StringUtil;
import org.gi.gICore.util.ValidationUtil;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryBuilder {
    private final String tableName;

    public QueryBuilder(String tableName) {
        ValidationUtil.requireNonEmpty(tableName, "Table name cannot be empty");
        this.tableName = tableName;
    }

    /**
     * SELECT 쿼리 생성
     */
    public SelectBuilder select(String... columns) {
        return new SelectBuilder(tableName, Arrays.asList(columns));
    }

    public SelectBuilder selectAll() {
        return new SelectBuilder(tableName, List.of("*"));
    }

    /**
     * INSERT 쿼리 생성
     */
    public String insert(String... columns) {
        return insert(Arrays.asList(columns));
    }

    public String insert(List<String> columns) {
        ValidationUtil.requireNonEmpty(columns, "Columns cannot be empty");

        String columnList = String.join(", ", columns);
        String placeholders = columns.stream().map(c -> "?").collect(Collectors.joining(", "));

        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columnList, placeholders);
    }

    public String insert(List<String> columns,List<String> sets,boolean isUpsert) {
        ValidationUtil.requireNonEmpty(columns, "Columns cannot be empty");

        String columnList = String.join(", ", columns);
        String placeholders = columns.stream().map(c -> "?").collect(Collectors.joining(", "));

        if (!isUpsert){
            return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columnList, placeholders);
        }
        ValidationUtil.requireNonEmpty(sets, "Sets cannot be empty");
        String updateList = sets.stream()
               .map(c -> c + " = VALUES(" + c + ")")
               .collect(Collectors.joining(", "));
        return String.format(
                    "INSERT INTO %s (%s) VALUES (%s) ON DUPLICATE KEY UPDATE %s",
                    tableName, columnList, placeholders, updateList
        );
    }

    /**
     * UPDATE 쿼리 생성
     */
    public UpdateBuilder update() {
        return new UpdateBuilder(tableName);
    }

    /**
     * DELETE 쿼리 생성
     */
    public DeleteBuilder delete() {
        return new DeleteBuilder(tableName);
    }
    public static class SelectBuilder{
        private final String tableName;
        private final List<String> columns;
        private final List<String> joins = new ArrayList<>();
        private final List<String> conditions = new ArrayList<>();
        private String orderBy;
        private String groupBy;
        private String having;
        private Integer limit;
        private Integer offset;

        public SelectBuilder(String tableName, List<String> columns) {
            this.tableName = tableName;
            this.columns = columns;
        }

        public SelectBuilder join(String joinClause) {
            joins.add("JOIN " + joinClause);
            return this;
        }

        public SelectBuilder leftJoin(String joinClause) {
            joins.add("LEFT JOIN " + joinClause);
            return this;
        }

        public SelectBuilder where(String condition) {
            conditions.add(condition);
            return this;
        }

        public SelectBuilder orderBy(String orderBy) {
            this.orderBy = orderBy;
            return this;
        }

        public SelectBuilder groupBy(String groupBy) {
            this.groupBy = groupBy;
            return this;
        }

        public SelectBuilder having(String having) {
            this.having = having;
            return this;
        }

        public SelectBuilder limit(int limit) {
            this.limit = limit;
            return this;
        }

        public SelectBuilder offset(int offset) {
            this.offset = offset;
            return this;
        }

        public String build() {
            StringBuilder query = new StringBuilder();
            query.append("SELECT ").append(String.join(", ", columns))
                    .append(" FROM ").append(tableName);

            joins.forEach(join -> query.append(" ").append(join));

            if (!conditions.isEmpty()) {
                query.append(" WHERE ").append(String.join(" AND ", conditions));
            }

            if (StringUtil.isNotEmpty(groupBy)) {
                query.append(" GROUP BY ").append(groupBy);
            }

            if (StringUtil.isNotEmpty(having)) {
                query.append(" HAVING ").append(having);
            }

            if (StringUtil.isNotEmpty(orderBy)) {
                query.append(" ORDER BY ").append(orderBy);
            }

            if (limit != null) {
                query.append(" LIMIT ").append(limit);
                if (offset != null) {
                    query.append(" OFFSET ").append(offset);
                }
            }

            return query.toString();
        }
    }

    public static class UpdateBuilder {
        private final String tableName;
        private final List<String> sets = new ArrayList<>();
        private final List<String> conditions = new ArrayList<>();

        public UpdateBuilder(String tableName) {
            this.tableName = tableName;
        }

        public UpdateBuilder set(String column) {
            sets.add(column + " = ?");
            return this;
        }

        public UpdateBuilder set(String column, String value) {
            sets.add(column + " = " + value);
            return this;
        }

        public UpdateBuilder where(String condition) {
            conditions.add(condition);
            return this;
        }

        public String build() {
            ValidationUtil.requireNonEmpty(sets, "SET clause cannot be empty");

            StringBuilder query = new StringBuilder();
            query.append("UPDATE ").append(tableName)
                    .append(" SET ").append(String.join(", ", sets));

            if (!conditions.isEmpty()) {
                query.append(" WHERE ").append(String.join(" AND ", conditions));
            }

            return query.toString();
        }
    }

    public static class DeleteBuilder {
        private final String tableName;
        private final List<String> conditions = new ArrayList<>();

        public DeleteBuilder(String tableName) {
            this.tableName = tableName;
        }

        public DeleteBuilder where(String condition) {
            conditions.add(condition);
            return this;
        }

        public String build() {
            StringBuilder query = new StringBuilder();
            query.append("DELETE FROM ").append(tableName);

            if (!conditions.isEmpty()) {
                query.append(" WHERE ").append(String.join(" AND ", conditions));
            }

            return query.toString();
        }
    }

    public static QueryBuilder table(String tableName) {
        return new QueryBuilder(tableName);
    }
}