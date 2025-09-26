package org.gi.gICore.util;

import org.bukkit.plugin.java.JavaPlugin;
import org.gi.gICore.GICore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class QueryBuilder {
    private final String tableName;
    public QueryBuilder(String tableName) {
        ValidationUtil.requireNonEmpty(tableName, "tableName cannot be null or empty");
        
        this.tableName = tableName;
    }

    public String insert(String... columns){
        return insert(Arrays.asList(columns));
    }

    public String insert(List<String> columns){
        ValidationUtil.requireNonEmpty(columns, "columns cannot be null or empty");
        List<String> cols = normalizeColumns(columns);
        ValidationUtil.requireNonEmpty(cols, "columns cannot be empty");


        String columnList = String.join(", ", cols);
        String placeholders = columns.stream().map(c-> "?").collect(Collectors.joining(", "));
        return String.format("INSERT INTO %s (%s) VALUES (%s)", tableName, columnList, placeholders);
    }

    public SelectQuery select(String... columns){
        List<String> cols = (columns == null) ? List.of("*") : normalizeColumns(Arrays.asList(columns));
        if (cols.isEmpty()) cols = List.of("*");
        return new SelectQuery(tableName, cols);
    }


    public UpdateQuery update(){
        return new UpdateQuery(tableName);
    }

    public DeleteQuery delete(){
        return new DeleteQuery(tableName);
    }

    public static class SelectQuery{
        private final String tableName;
        private final List<String> columns;
        private final List<String> joins = new ArrayList<>();
        private final List<String> conditions = new ArrayList<>();
        private String orderBy;
        private String groupBy;
        private String having;
        private Integer limit;
        private Integer offset;

        public SelectQuery(String tableName,List<String> columns){
            this.tableName = tableName;
            List<String> cols = normalizeColumns(columns);              // ← 추가
            this.columns = cols.isEmpty() ? List.of("*") : cols;
        }

        public SelectQuery join(String column){
            joins.add("JOIN " +column);
            return this;
        }

        public SelectQuery leftJoin(String joinClause) {
            joins.add("LEFT JOIN " + joinClause);
            return this;
        }

        public SelectQuery where(String condition){
            String raw = condition.trim();

            boolean hasOp = raw.contains("?") || raw.matches(".*\\b(LIKE|IN|IS|BETWEEN)\\b.*") || raw.contains("<")
                    || raw.contains(">") || raw.contains("=");

            conditions.add(hasOp ? raw: (raw + " = ?"));
            return this;
        }

        public SelectQuery orderBy(String orderBy){
            this.orderBy = orderBy;
            return this;
        }

        public SelectQuery groupBy(String groupBy){
            this.groupBy = groupBy;
            return this;
        }

        public SelectQuery having(String having){
            this.having = having;
            return this;
        }

        public SelectQuery limit(Integer limit){
            this.limit = limit;
            return this;
        }

        public SelectQuery offset(Integer offset){
            this.offset = offset;
            return this;
        }

        public String build(){
            StringBuilder query = new StringBuilder();
            query.append("SELECT ").append(String.join(", ", columns))
                    .append(" FROM ").append(tableName);
            if (!joins.isEmpty()){
                joins.forEach(joinClause -> query.append(" ").append(joinClause));
            }

            if(!conditions.isEmpty()){
                query.append(" WHERE ").append(String.join(" AND ", conditions));
            }
            if(!StringUtil.isNotEmpty(groupBy)){
                query.append(" GROUP BY ").append(groupBy);
            }
            if(!StringUtil.isNotEmpty(having)){
                query.append(" HAVING ").append(having);
            }
            if(!StringUtil.isNotEmpty(orderBy)){
                query.append(" ORDER BY ").append(orderBy);
            }
            if(limit != null){
                query.append(" LIMIT ").append(limit);
                if (offset != null){
                    query.append(" OFFSET ").append(offset);
                }
            }

            return query.toString();
        }
    }

    public static class UpdateQuery{
        private final String tableName;
        private final List<String> sets = new ArrayList<>();
        private final List<String> conditions = new ArrayList<>();

        public UpdateQuery(String tableName) {
            this.tableName = tableName;
        }

        public UpdateQuery set(String column){
            sets.add(column + " = ?");
            return this;
        }

        public UpdateQuery where(String condition){
            String raw = condition.trim();
            boolean hasOp = raw.contains("?")
                    || raw.matches(".*\\b(LIKE|IN|IS|BETWEEN)\\b.*")
                    || raw.contains(">") || raw.contains("<") || raw.contains("=");

            conditions.add(hasOp ? raw : (raw + " = ?"));
            return this;
        }

        public String build(){
            ValidationUtil.requireNonEmpty(sets, "sets cannot be null or empty");

            StringBuilder query = new StringBuilder();
            query.append("UPDATE ").append(tableName).append(" SET ").append(String.join(", ", sets));

            if (!conditions.isEmpty()){
                query.append(" WHERE ").append(String.join(" AND ", conditions));
            }

            return query.toString();
        }
    }

    public static class DeleteQuery{
        private final String tableName;
        private final List<String> conditions = new ArrayList<>();
        public DeleteQuery(String tableName) {
            this.tableName = tableName;
        }

        public DeleteQuery where(String condition){
            conditions.add(condition);
            return this;
        }

        public String build(){
            StringBuilder query = new StringBuilder();
            query.append("DELETE FROM ").append(tableName);
            if (!conditions.isEmpty()){
                query.append(" WHERE ").append(String.join(" AND ", conditions));
            } else {
                throw new IllegalStateException("Refusing DELETE without WHERE. (의도적이면 where(\"1=1\")을 명시하세요)");
            }
            return query.toString();
        }
    }
    private static List<String> normalizeColumns(List<String> cols) {
        List<String> out = new ArrayList<>();
        for (String c : cols) {
            if (c == null) continue;
            if (c.contains(",")) {
                for (String s : c.split(",")) {
                    String t = s.trim();
                    if (!t.isEmpty()) out.add(t);
                }
            } else {
                String t = c.trim();
                if (!t.isEmpty()) out.add(t);
            }
        }
        return out;
    }
    public static QueryBuilder table(String tableName) {
        return new QueryBuilder(tableName);
    }
}