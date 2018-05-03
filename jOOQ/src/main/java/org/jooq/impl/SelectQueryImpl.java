/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *  http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Other licenses:
 * -----------------------------------------------------------------------------
 * Commercial licenses for this work are available. These replace the above
 * ASL 2.0 and offer limited warranties, support, maintenance, and commercial
 * database integrations.
 *
 * For more information, please visit: http://www.jooq.org/licenses
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 *
 */
package org.jooq.impl;

import static java.lang.Boolean.TRUE;
import static org.jooq.Clause.SELECT;
import static org.jooq.Clause.SELECT_CONNECT_BY;
import static org.jooq.Clause.SELECT_EXCEPT;
import static org.jooq.Clause.SELECT_EXCEPT_ALL;
import static org.jooq.Clause.SELECT_FROM;
import static org.jooq.Clause.SELECT_GROUP_BY;
import static org.jooq.Clause.SELECT_HAVING;
import static org.jooq.Clause.SELECT_INTERSECT;
import static org.jooq.Clause.SELECT_INTERSECT_ALL;
import static org.jooq.Clause.SELECT_INTO;
import static org.jooq.Clause.SELECT_ORDER_BY;
import static org.jooq.Clause.SELECT_SELECT;
import static org.jooq.Clause.SELECT_START_WITH;
import static org.jooq.Clause.SELECT_UNION;
import static org.jooq.Clause.SELECT_UNION_ALL;
import static org.jooq.Clause.SELECT_WHERE;
import static org.jooq.Clause.SELECT_WINDOW;
import static org.jooq.Operator.OR;
// ...
// ...
// ...
// ...
import static org.jooq.SQLDialect.CUBRID;
// ...
// ...
import static org.jooq.SQLDialect.DERBY;
import static org.jooq.SQLDialect.FIREBIRD;
import static org.jooq.SQLDialect.H2;
// ...
import static org.jooq.SQLDialect.HSQLDB;
// ...
// ...
import static org.jooq.SQLDialect.MARIADB;
import static org.jooq.SQLDialect.MYSQL;
// ...
// ...
import static org.jooq.SQLDialect.POSTGRES;
// ...
import static org.jooq.SQLDialect.SQLITE;
// ...
// ...
// ...
// ...
// ...
// ...
import static org.jooq.SortOrder.DESC;
import static org.jooq.impl.CombineOperator.EXCEPT;
import static org.jooq.impl.CombineOperator.EXCEPT_ALL;
import static org.jooq.impl.CombineOperator.INTERSECT;
import static org.jooq.impl.CombineOperator.INTERSECT_ALL;
import static org.jooq.impl.CombineOperator.UNION;
import static org.jooq.impl.CombineOperator.UNION_ALL;
import static org.jooq.impl.DSL.falseCondition;
import static org.jooq.impl.DSL.inline;
import static org.jooq.impl.DSL.name;
import static org.jooq.impl.DSL.one;
import static org.jooq.impl.DSL.orderBy;
import static org.jooq.impl.DSL.row;
import static org.jooq.impl.Keywords.K_AND;
import static org.jooq.impl.Keywords.K_BY;
import static org.jooq.impl.Keywords.K_CONNECT_BY;
import static org.jooq.impl.Keywords.K_DISTINCT;
import static org.jooq.impl.Keywords.K_DISTINCT_ON;
import static org.jooq.impl.Keywords.K_FOR_SHARE;
import static org.jooq.impl.Keywords.K_FOR_UPDATE;
import static org.jooq.impl.Keywords.K_FROM;
import static org.jooq.impl.Keywords.K_GROUP_BY;
import static org.jooq.impl.Keywords.K_HAVING;
import static org.jooq.impl.Keywords.K_INTO;
import static org.jooq.impl.Keywords.K_LOCK_IN_SHARE_MODE;
import static org.jooq.impl.Keywords.K_NOCYCLE;
import static org.jooq.impl.Keywords.K_OF;
import static org.jooq.impl.Keywords.K_ORDER;
import static org.jooq.impl.Keywords.K_ORDER_BY;
import static org.jooq.impl.Keywords.K_PERCENT;
import static org.jooq.impl.Keywords.K_SELECT;
import static org.jooq.impl.Keywords.K_SIBLINGS;
import static org.jooq.impl.Keywords.K_START_WITH;
import static org.jooq.impl.Keywords.K_TOP;
import static org.jooq.impl.Keywords.K_WHERE;
import static org.jooq.impl.Keywords.K_WINDOW;
import static org.jooq.impl.Keywords.K_WITH_CHECK_OPTION;
import static org.jooq.impl.Keywords.K_WITH_LOCK;
import static org.jooq.impl.Keywords.K_WITH_READ_ONLY;
import static org.jooq.impl.Tools.EMPTY_FIELD;
import static org.jooq.impl.Tools.fieldArray;
import static org.jooq.impl.Tools.hasAmbiguousNames;
import static org.jooq.impl.Tools.DataKey.DATA_COLLECTED_SEMI_ANTI_JOIN;
import static org.jooq.impl.Tools.DataKey.DATA_COLLECT_SEMI_ANTI_JOIN;
import static org.jooq.impl.Tools.DataKey.DATA_INSERT_SELECT_WITHOUT_INSERT_COLUMN_LIST;
import static org.jooq.impl.Tools.DataKey.DATA_NESTED_SET_OPERATIONS;
import static org.jooq.impl.Tools.DataKey.DATA_OMIT_INTO_CLAUSE;
import static org.jooq.impl.Tools.DataKey.DATA_OVERRIDE_ALIASES_IN_ORDER_BY;
import static org.jooq.impl.Tools.DataKey.DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE;
import static org.jooq.impl.Tools.DataKey.DATA_ROW_VALUE_EXPRESSION_PREDICATE_SUBQUERY;
import static org.jooq.impl.Tools.DataKey.DATA_SELECT_INTO_TABLE;
import static org.jooq.impl.Tools.DataKey.DATA_UNALIAS_ALIASES_IN_ORDER_BY;
import static org.jooq.impl.Tools.DataKey.DATA_WINDOW_DEFINITIONS;
import static org.jooq.impl.Tools.DataKey.DATA_WRAP_DERIVED_TABLES_IN_PARENTHESES;

import java.sql.ResultSetMetaData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.EnumSet;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.stream.Stream;

import org.jooq.Asterisk;
import org.jooq.Clause;
import org.jooq.Condition;
import org.jooq.Configuration;
import org.jooq.Context;
import org.jooq.DataType;
import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.GroupField;
import org.jooq.JoinType;
import org.jooq.Keyword;
import org.jooq.Name;
import org.jooq.Operator;
import org.jooq.OrderField;
import org.jooq.Param;
import org.jooq.QualifiedAsterisk;
import org.jooq.Record;
import org.jooq.Row;
import org.jooq.SQLDialect;
import org.jooq.Select;
import org.jooq.SelectFieldOrAsterisk;
import org.jooq.SelectQuery;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableLike;
import org.jooq.TableOnStep;
import org.jooq.TableOptionalOnStep;
import org.jooq.TablePartitionByStep;
import org.jooq.WindowDefinition;
import org.jooq.exception.DataAccessException;
import org.jooq.impl.Tools.DataKey;
import org.jooq.tools.StringUtils;

/**
 * A sub-select is a <code>SELECT</code> statement that can be combined with
 * other <code>SELECT</code> statement in <code>UNION</code>s and similar
 * operations.
 *
 * @author Lukas Eder
 */
final class SelectQueryImpl<R extends Record> extends AbstractResultQuery<R> implements SelectQuery<R> {

    /**
     * Generated UID
     */
    private static final long                    serialVersionUID                = 1646393178384872967L;
    private static final Clause[]                CLAUSES                         = { SELECT };
    private static final EnumSet<SQLDialect>     EMULATE_SELECT_INTO_AS_CTAS     = EnumSet.of(CUBRID, DERBY, FIREBIRD, H2, HSQLDB, MARIADB, MYSQL, POSTGRES, SQLITE);
    private static final EnumSet<SQLDialect>     NO_SUPPORT_FOR_UPDATE           = EnumSet.of(CUBRID);
    private static final EnumSet<SQLDialect>     NO_SUPPORT_FOR_UPDATE_QUALIFIED = EnumSet.of(DERBY, FIREBIRD, H2, HSQLDB);
    private static final EnumSet<SQLDialect>     SUPPORT_SELECT_INTO             = EnumSet.of(HSQLDB, POSTGRES);
    private static final EnumSet<SQLDialect>     SUPPORT_WINDOW_CLAUSE           = EnumSet.of(MYSQL, POSTGRES);
    private static final EnumSet<SQLDialect>     REQUIRES_FROM_CLAUSE            = EnumSet.of(CUBRID, DERBY, FIREBIRD, HSQLDB, MARIADB, MYSQL);
    private static final EnumSet<SQLDialect>     EMULATE_EMPTY_GROUP_BY_OTHER    = EnumSet.of(FIREBIRD, HSQLDB, MARIADB, MYSQL, POSTGRES, SQLITE);










    private final WithImpl                               with;
    private final SelectFieldList<SelectFieldOrAsterisk> select;
    private Table<?>                                     into;
    private String                                       hint;
    private String                                       option;
    private boolean                                      distinct;
    private QueryPartList<SelectFieldOrAsterisk>         distinctOn;
    private boolean                                      forUpdate;
    private QueryPartList<Field<?>>                      forUpdateOf;
    private TableList                                    forUpdateOfTables;
    private ForUpdateMode                                forUpdateMode;
    private int                                          forUpdateWait;
    private boolean                                      forShare;




    private final TableList                              from;
    private final ConditionProviderImpl                  condition;
    private final ConditionProviderImpl                  connectBy;
    private boolean                                      connectByNoCycle;
    private final ConditionProviderImpl                  connectByStartWith;
    private boolean                                      grouping;
    private QueryPartList<GroupField>                    groupBy;
    private final ConditionProviderImpl                  having;
    private WindowList                                   window;
    private final SortFieldList                          orderBy;
    private boolean                                      orderBySiblings;
    private final QueryPartList<Field<?>>                seek;
    private boolean                                      seekBefore;
    private final Limit                                  limit;
    private final List<CombineOperator>                  unionOp;
    private final List<QueryPartList<Select<?>>>         union;
    private final SortFieldList                          unionOrderBy;
    private boolean                                      unionOrderBySiblings; // [#3579] TODO
    private final QueryPartList<Field<?>>                unionSeek;
    private boolean                                      unionSeekBefore;      // [#3579] TODO
    private final Limit                                  unionLimit;

    SelectQueryImpl(Configuration configuration, WithImpl with) {
        this(configuration, with, null);
    }

    SelectQueryImpl(Configuration configuration, WithImpl with, boolean distinct) {
        this(configuration, with, null, distinct);
    }

    SelectQueryImpl(Configuration configuration, WithImpl with, TableLike<? extends R> from) {
        this(configuration, with, from, false);
    }

    SelectQueryImpl(Configuration configuration, WithImpl with, TableLike<? extends R> from, boolean distinct) {
        super(configuration);

        this.with = with;
        this.distinct = distinct;
        this.select = new SelectFieldList<SelectFieldOrAsterisk>();
        this.from = new TableList();
        this.condition = new ConditionProviderImpl();
        this.connectBy = new ConditionProviderImpl();
        this.connectByStartWith = new ConditionProviderImpl();
        this.having = new ConditionProviderImpl();
        this.orderBy = new SortFieldList();
        this.seek = new QueryPartList<Field<?>>();
        this.limit = new Limit();
        this.unionOp = new ArrayList<CombineOperator>();
        this.union = new ArrayList<QueryPartList<Select<?>>>();
        this.unionOrderBy = new SortFieldList();
        this.unionSeek = new QueryPartList<Field<?>>();
        this.unionLimit = new Limit();

        if (from != null)
            this.from.add(from.asTable());
    }

    @Override
    public final int fetchCount() throws DataAccessException {
        return DSL.using(configuration()).fetchCount(this);
    }

    @SuppressWarnings("unchecked")
    @Override
    public final <T> Field<T> asField() {
        List<Field<?>> s = getSelect();

        if (s.size() != 1) {
            throw new IllegalStateException("Can only use single-column ResultProviderQuery as a field");
        }

        return new ScalarSubquery<T>(this, (DataType<T>) s.get(0).getDataType());
    }

    @Override
    public final <T> Field<T> asField(String alias) {
        return this.<T> asField().as(alias);
    }


    @Override
    public <T> Field<T> asField(Function<? super Field<T>, ? extends String> aliasFunction) {
        return this.<T> asField().as(aliasFunction);
    }


    @Override
    public final Row fieldsRow() {
        return asTable().fieldsRow();
    }


    @Override
    public final Stream<Field<?>> fieldStream() {
        return Stream.of(fields());
    }


    @Override
    public final <T> Field<T> field(Field<T> field) {
        return asTable().field(field);
    }

    @Override
    public final Field<?> field(String string) {
        return asTable().field(string);
    }

    @Override
    public final <T> Field<T> field(String name, Class<T> type) {
        return asTable().field(name, type);
    }

    @Override
    public final <T> Field<T> field(String name, DataType<T> dataType) {
        return asTable().field(name, dataType);
    }

    @Override
    public final Field<?> field(Name string) {
        return asTable().field(string);
    }

    @Override
    public final <T> Field<T> field(Name name, Class<T> type) {
        return asTable().field(name, type);
    }

    @Override
    public final <T> Field<T> field(Name name, DataType<T> dataType) {
        return asTable().field(name, dataType);
    }

    @Override
    public final Field<?> field(int index) {
        return asTable().field(index);
    }

    @Override
    public final <T> Field<T> field(int index, Class<T> type) {
        return asTable().field(index, type);
    }

    @Override
    public final <T> Field<T> field(int index, DataType<T> dataType) {
        return asTable().field(index, dataType);
    }

    @Override
    public final Field<?>[] fields() {
        return asTable().fields();
    }

    @Override
    public final Field<?>[] fields(Field<?>... fields) {
        return asTable().fields(fields);
    }

    @Override
    public final Field<?>[] fields(String... fieldNames) {
        return asTable().fields(fieldNames);
    }

    @Override
    public final Field<?>[] fields(Name... fieldNames) {
        return asTable().fields(fieldNames);
    }

    @Override
    public final Field<?>[] fields(int... fieldIndexes) {
        return asTable().fields(fieldIndexes);
    }

    @Override
    public final Table<R> asTable() {
        // Its usually better to alias nested selects that are used in
        // the FROM clause of a query
        return new DerivedTable<R>(this).as("alias_" + Tools.hash(this));
    }

    @Override
    public final Table<R> asTable(String alias) {
        return new DerivedTable<R>(this).as(alias);
    }

    @Override
    public final Table<R> asTable(String alias, String... fieldAliases) {
        return new DerivedTable<R>(this).as(alias, fieldAliases);
    }


    @Override
    public final Table<R> asTable(String alias, Function<? super Field<?>, ? extends String> aliasFunction) {
        return new DerivedTable<R>(this).as(alias, aliasFunction);
    }

    @Override
    public final Table<R> asTable(String alias, BiFunction<? super Field<?>, ? super Integer, ? extends String> aliasFunction) {
        return new DerivedTable<R>(this).as(alias, aliasFunction);
    }


    @Override
    protected final Field<?>[] getFields(ResultSetMetaData meta) {

        // [#1808] TODO: Restrict this field list, in case a restricting fetch()
        // method was called to get here
        List<Field<?>> fields = getSelect();

        // If no projection was specified explicitly, create fields from result
        // set meta data instead. This is typically the case for SELECT * ...
        if (fields.isEmpty()) {
            Configuration configuration = configuration();
            return new MetaDataFieldProvider(configuration, meta).getFields();
        }

        return fieldArray(fields);
    }

    @Override
    public final Clause[] clauses(Context<?> ctx) {
        return CLAUSES;
    }

    @Override
    public final void accept(Context<?> context) {
        context.scopeStart();
        for (Table<?> table : getFrom())
            registerTable(context, table);

        SQLDialect dialect = context.dialect();
        SQLDialect family = context.family();

        // [#2791] TODO: Instead of explicitly manipulating these data() objects, future versions
        // of jOOQ should implement a push / pop semantics to clearly delimit such scope.
        Object renderTrailingLimit = context.data(DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE);
        Object localWindowDefinitions = context.data(DATA_WINDOW_DEFINITIONS);
        try {
            if (renderTrailingLimit != null)
                context.data().remove(DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE);

            // [#5127] Lazy initialise this map
            if (localWindowDefinitions != null)
                context.data(DATA_WINDOW_DEFINITIONS, null);

            if (into != null
                    && context.data(DATA_OMIT_INTO_CLAUSE) == null
                    && EMULATE_SELECT_INTO_AS_CTAS.contains(family)) {

                context.data(DATA_OMIT_INTO_CLAUSE, true);
                context.visit(DSL.createTable(into).as(this));
                context.data().remove(DATA_OMIT_INTO_CLAUSE);

                return;
            }

            if (with != null)
                context.visit(with).formatSeparator();

            pushWindow(context);

            Boolean wrapDerivedTables = (Boolean) context.data(DATA_WRAP_DERIVED_TABLES_IN_PARENTHESES);
            if (TRUE.equals(wrapDerivedTables)) {
                context.sql('(')
                       .formatIndentStart()
                       .formatNewLine()
                       .data(DATA_WRAP_DERIVED_TABLES_IN_PARENTHESES, null);
            }

            switch (dialect) {


















































































































                case CUBRID:
                case FIREBIRD_3_0:
                case MYSQL:
                case MYSQL_8_0:
                case POSTGRES:
                case POSTGRES_9_3:
                case POSTGRES_9_4:
                case POSTGRES_9_5:
                case POSTGRES_10: {
                    if (getLimit().isApplicable() && getLimit().withTies())
                        toSQLReferenceLimitWithWindowFunctions(context);
                    else
                        toSQLReferenceLimitDefault(context);

                    break;
                }

                /* [/pro] */
                // By default, render the dialect's limit clause
                default: {
                    toSQLReferenceLimitDefault(context);

                    break;
                }
            }

            // [#1296] FOR UPDATE is emulated in some dialects using ResultSet.CONCUR_UPDATABLE
            if (forUpdate && !NO_SUPPORT_FOR_UPDATE.contains(family)) {
                context.formatSeparator()
                       .visit(K_FOR_UPDATE);

                if (Tools.isNotEmpty(forUpdateOf)) {

                    // [#4151] [#6117] Some databases don't allow for qualifying column
                    // names here. Copy also to TableList
                    boolean unqualified = NO_SUPPORT_FOR_UPDATE_QUALIFIED.contains(context.family());
                    boolean qualify = context.qualify();

                    if (unqualified)
                        context.qualify(false);

                    context.sql(' ').visit(K_OF)
                           .sql(' ').visit(forUpdateOf);

                    if (unqualified)
                        context.qualify(qualify);
                }
                else if (Tools.isNotEmpty(forUpdateOfTables)) {
                    context.sql(' ').visit(K_OF).sql(' ');

                    switch (family) {

                        // Some dialects don't allow for an OF [table-names] clause
                        // It can be emulated by listing the table's fields, though







                        case DERBY: {
                            forUpdateOfTables.toSQLFields(context);
                            break;
                        }

                        // Render the OF [table-names] clause
                        default:
                            Tools.tableNames(context, forUpdateOfTables);
                            break;
                    }
                }

                // [#3186] Firebird's FOR UPDATE clause has a different semantics. To achieve "regular"
                // FOR UPDATE semantics, we should use FOR UPDATE WITH LOCK
                if (family == FIREBIRD) {
                    context.sql(' ').visit(K_WITH_LOCK);
                }

                if (forUpdateMode != null) {
                    context.sql(' ');
                    context.visit(forUpdateMode.toKeyword());

                    if (forUpdateMode == ForUpdateMode.WAIT) {
                        context.sql(' ');
                        context.sql(forUpdateWait);
                    }
                }
            }
            else if (forShare) {
                switch (family) {

                    // MySQL has a non-standard implementation for the "FOR SHARE" clause




                    case MARIADB:
                    case MYSQL:
                        context.formatSeparator()
                               .visit(K_LOCK_IN_SHARE_MODE);
                        break;

                    // Postgres is known to implement the "FOR SHARE" clause like this
                    default:
                        context.formatSeparator()
                               .visit(K_FOR_SHARE);
                        break;
                }
            }













            // [#1952] SQL Server OPTION() clauses as well as many other optional
            // end-of-query clauses are appended to the end of a query
            if (!StringUtils.isBlank(option)) {
                context.formatSeparator()
                       .sql(option);
            }

            if (TRUE.equals(wrapDerivedTables)) {
                context.formatIndentEnd()
                       .formatNewLine()
                       .sql(')')
                       .data(DATA_WRAP_DERIVED_TABLES_IN_PARENTHESES, true);
            }
























        }
        finally {
            context.data(DATA_WINDOW_DEFINITIONS, localWindowDefinitions);
            if (renderTrailingLimit != null)
                context.data(DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE, renderTrailingLimit);
        }

        context.scopeEnd();
    }

    private final void registerTable(Context<?> context, Table<?> table) {
        if (table instanceof JoinTable) {
            registerTable(context, ((JoinTable) table).lhs);
            registerTable(context, ((JoinTable) table).rhs);
        }
        else if (table instanceof TableImpl) {
            context.scopeRegister(table);
        }
    }

    private final void pushWindow(Context<?> context) {
        // [#531] [#2790] Make the WINDOW clause available to the SELECT clause
        // to be able to inline window definitions if the WINDOW clause is not
        // supported.
        if (Tools.isNotEmpty(window))
            context.data(DATA_WINDOW_DEFINITIONS, window);
    }

    /**
     * The default LIMIT / OFFSET clause in most dialects
     */
    private void toSQLReferenceLimitDefault(Context<?> context) {
        Object data = context.data(DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE);

        context.data(DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE, true);
        toSQLReference0(context);

        if (data == null)
            context.data().remove(DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE);
        else
            context.data(DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE, data);
    }

    /**
     * Emulate the LIMIT / OFFSET clause using window functions, specifically
     * when the WITH TIES clause is specified.
     */
    @SuppressWarnings("serial")
    private final void toSQLReferenceLimitWithWindowFunctions(Context<?> ctx) {

        // AUTHOR.ID, BOOK.ID, BOOK.TITLE
        final Field<?>[] originalFields = Tools.fieldArray(getSelect());

        // ID, ID, TITLE
        final Name[] originalNames = Tools.fieldNames(originalFields);

        // v1, v2, v3
        final Name[] alternativeNames = Tools.fieldNames(originalFields.length);

        // AUTHOR.ID as v1, BOOK.ID as v2, BOOK.TITLE as v3
        // Enforce x.* or just * if we have no known field names (e.g. when plain SQL tables are involved)
        final Field<?>[] alternativeFields = Tools.combine(
            alternativeNames.length == 0
                ? new Field[] { DSL.field("*") }
                : Tools.aliasedFields(originalFields, alternativeNames),

            null
        );

        alternativeFields[alternativeFields.length - 1] =
            new CustomField<Integer>("rn", SQLDataType.INTEGER) {
                @Override
                public void accept(Context<?> c) {
                    boolean wrapQueryExpressionBodyInDerivedTable = wrapQueryExpressionBodyInDerivedTable(c);

                    // [#3575] Ensure that no column aliases from the surrounding SELECT clause
                    // are referenced from the below ranking functions' ORDER BY clause.
                    c.data(DATA_UNALIAS_ALIASES_IN_ORDER_BY, !wrapQueryExpressionBodyInDerivedTable);

                    boolean qualify = c.qualify();

                    c.data(DATA_OVERRIDE_ALIASES_IN_ORDER_BY, new Object[] { originalFields, alternativeFields });
                    if (wrapQueryExpressionBodyInDerivedTable)
                        c.qualify(false);

                    // [#2580] FETCH NEXT n ROWS ONLY emulation:
                    // -----------------------------------------
                    // When DISTINCT is applied, we mustn't use ROW_NUMBER() OVER(),
                    // which changes the DISTINCT semantics. Instead, use DENSE_RANK() OVER(),
                    // ordering by the SELECT's ORDER BY clause AND all the expressions from
                    // the projection
                    //
                    // [#6197] FETCH NEXT n ROWS WITH TIES emulation:
                    // ----------------------------------------------
                    // DISTINCT seems irrelevant here (to be proven)

                    c.visit(getLimit().withTies()
                        ? DSL.rank().over(orderBy(getNonEmptyOrderBy(c.configuration())))
                        : distinct
                        ? DSL.denseRank().over(orderBy(getNonEmptyOrderByForDistinct(c.configuration())))
                        : DSL.rowNumber().over(orderBy(getNonEmptyOrderBy(c.configuration())))
                    );

                    c.data().remove(DATA_UNALIAS_ALIASES_IN_ORDER_BY);
                    c.data().remove(DATA_OVERRIDE_ALIASES_IN_ORDER_BY);
                    if (wrapQueryExpressionBodyInDerivedTable)
                        c.qualify(qualify);

                }
            }.as("rn");

        // v1 as ID, v2 as ID, v3 as TITLE
        final Field<?>[] unaliasedFields = Tools.aliasedFields(Tools.fields(originalFields.length), originalNames);

        ctx.visit(K_SELECT).sql(' ')
           .declareFields(true)
           .visit(new SelectFieldList<Field<?>>(unaliasedFields))
           .declareFields(false)
           .formatSeparator()
           .visit(K_FROM).sql(" (")
           .formatIndentStart()
           .formatNewLine()
           .subquery(true);

        toSQLReference0(ctx, originalFields, alternativeFields);

        ctx.subquery(false)
           .formatIndentEnd()
           .formatNewLine()
           .sql(") ")
           .visit(name("x"))
           .formatSeparator()
           .visit(K_WHERE).sql(' ')
           .visit(name("rn"))
           .sql(" > ")
           .visit(getLimit().getLowerRownum());

        if (!getLimit().limitZero())
            ctx.formatSeparator()
               .visit(K_AND).sql(' ')
               .visit(name("rn"))
               .sql(" <= ")
               .visit(getLimit().getUpperRownum());

        // [#5068] Don't rely on nested query's ordering in case an operation
        //         like DISTINCT or JOIN produces hashing.
        if (!ctx.subquery())
            ctx.formatSeparator()
               .visit(K_ORDER_BY)
               .sql(' ')
               .visit(name("rn"));
    }


















































































    /**
     * This method renders the main part of a query without the LIMIT clause.
     * This part is common to any type of limited query
     */
    private final void toSQLReference0(Context<?> context) {
        toSQLReference0(context, null, null);
    }

    /**
     * This method renders the main part of a query without the LIMIT clause.
     * This part is common to any type of limited query
     */
    @SuppressWarnings("unchecked")
    private final void toSQLReference0(Context<?> context, Field<?>[] originalFields, Field<?>[] alternativeFields) {
        SQLDialect dialect = context.dialect();
        SQLDialect family = dialect.family();

        int unionOpSize = unionOp.size();
        boolean unionOpNesting = false;

        // The SQL standard specifies:
        //
        // <query expression> ::=
        //    [ <with clause> ] <query expression body>
        //    [ <order by clause> ] [ <result offset clause> ] [ <fetch first clause> ]
        //
        // Depending on the dialect and on various syntax elements, parts of the above must be wrapped in
        // synthetic parentheses
        boolean wrapQueryExpressionInDerivedTable;
        boolean wrapQueryExpressionBodyInDerivedTable = false;
        boolean applySeekOnDerivedTable = applySeekOnDerivedTable(context);


        wrapQueryExpressionInDerivedTable = false







//        // [#2995] Prevent the generation of wrapping parentheses around the
//        //         INSERT .. SELECT statement's SELECT because they would be
//        //         interpreted as the (missing) INSERT column list's parens.
//         || (context.data(DATA_INSERT_SELECT_WITHOUT_INSERT_COLUMN_LIST) != null && unionOpSize > 0)
         ;

        if (wrapQueryExpressionInDerivedTable)
            context.visit(K_SELECT).sql(" *")
                   .formatSeparator()
                   .visit(K_FROM).sql(" (")
                   .formatIndentStart()
                   .formatNewLine();












        // [#7459] In the presence of UNIONs and other set operations, the SEEK
        //         predicate must be applied on a derived table, not on the individual subqueries
        wrapQueryExpressionBodyInDerivedTable |= applySeekOnDerivedTable;

        if (wrapQueryExpressionBodyInDerivedTable) {
            context.visit(K_SELECT).sql(' ');





            context.formatIndentStart()
                   .formatNewLine()
                   .sql("t.*");

            if (alternativeFields != null && originalFields.length < alternativeFields.length)
                context.sql(", ")
                       .formatSeparator()
                       .declareFields(true)
                       .visit(alternativeFields[alternativeFields.length - 1])
                       .declareFields(false);

            context.formatIndentEnd()
                   .formatSeparator()
                   .visit(K_FROM).sql(" (")
                   .formatIndentStart()
                   .formatNewLine();
        }

        // [#1658] jOOQ applies left-associativity to set operators. In order to enforce that across
        // all databases, we need to wrap relevant subqueries in parentheses.
        if (unionOpSize > 0) {
            if (!TRUE.equals(context.data(DATA_NESTED_SET_OPERATIONS)))
                context.data(DATA_NESTED_SET_OPERATIONS, unionOpNesting = unionOpNesting());

            for (int i = unionOpSize - 1; i >= 0; i--) {
                switch (unionOp.get(i)) {
                    case EXCEPT:        context.start(SELECT_EXCEPT);        break;
                    case EXCEPT_ALL:    context.start(SELECT_EXCEPT_ALL);    break;
                    case INTERSECT:     context.start(SELECT_INTERSECT);     break;
                    case INTERSECT_ALL: context.start(SELECT_INTERSECT_ALL); break;
                    case UNION:         context.start(SELECT_UNION);         break;
                    case UNION_ALL:     context.start(SELECT_UNION_ALL);     break;
                }

                unionParenthesis(context, '(', getSelect().toArray(EMPTY_FIELD));
            }
        }

        // SELECT clause
        // -------------
        context.start(SELECT_SELECT)
               .visit(K_SELECT)
               .sql(' ');

        // [#1493] Oracle hints come directly after the SELECT keyword
        if (!StringUtils.isBlank(hint)) {
            context.sql(hint).sql(' ');
        }








        if (Tools.isNotEmpty(distinctOn))
            context.visit(K_DISTINCT_ON).sql(" (").visit(distinctOn).sql(") ");
        else if (distinct)
            context.visit(K_DISTINCT).sql(' ');






        context.declareFields(true);

        // [#2335] When emulating LIMIT .. OFFSET, the SELECT clause needs to generate
        // non-ambiguous column names as ambiguous column names are not allowed in subqueries
        if (alternativeFields != null) {
            if (wrapQueryExpressionBodyInDerivedTable && originalFields.length < alternativeFields.length)
                context.visit(new SelectFieldList<Field<?>>(Arrays.copyOf(alternativeFields, alternativeFields.length - 1)));
            else
                context.visit(new SelectFieldList<Field<?>>(alternativeFields));
        }

        // [#1905] H2 only knows arrays, no row value expressions. Subqueries
        // in the context of a row value expression predicate have to render
        // arrays explicitly, as the subquery doesn't form an implicit RVE
        else if (context.subquery() && dialect == H2 && context.data(DATA_ROW_VALUE_EXPRESSION_PREDICATE_SUBQUERY) != null) {
            Object data = context.data(DATA_ROW_VALUE_EXPRESSION_PREDICATE_SUBQUERY);

            try {
                context.data(DATA_ROW_VALUE_EXPRESSION_PREDICATE_SUBQUERY, null);
                context.sql('(')
                       .visit(getSelect1())
                       .sql(')');
            }
            finally {
                context.data(DATA_ROW_VALUE_EXPRESSION_PREDICATE_SUBQUERY, data);
            }
        }

        // The default behaviour
        else {
            context.visit(getSelect1());
        }


        context.declareFields(false)
               .end(SELECT_SELECT);

        // INTO clauses
        // ------------
        // [#4910] This clause (and the Clause.SELECT_INTO signal) must be emitted
        //         only in top level SELECTs
        if (!context.subquery()



        ) {
            context.start(SELECT_INTO);

            Table<?> actualInto = (Table<?>) context.data(DATA_SELECT_INTO_TABLE);
            if (actualInto == null)
                actualInto = into;

            if (actualInto != null
                    && context.data(DATA_OMIT_INTO_CLAUSE) == null
                    && SUPPORT_SELECT_INTO.contains(family)) {

                context.formatSeparator()
                       .visit(K_INTO)
                       .sql(' ')
                       .visit(actualInto);
            }

            context.end(SELECT_INTO);
        }

        // FROM and JOIN clauses
        // ---------------------
        context.start(SELECT_FROM)
               .declareTables(true);

        // [#....] Some SQL dialects do not require a FROM clause. Others do and
        //         jOOQ generates a "DUAL" table or something equivalent.
        //         See also org.jooq.impl.Dual for details.
        boolean hasFrom = !getFrom().isEmpty()



            || REQUIRES_FROM_CLAUSE.contains(family)
        ;

        List<Condition> semiAntiJoinPredicates = null;

        if (hasFrom) {
            Object previousCollect = context.data(DATA_COLLECT_SEMI_ANTI_JOIN, true);
            Object previousCollected = context.data(DATA_COLLECTED_SEMI_ANTI_JOIN, null);

            context.formatSeparator()
                   .visit(K_FROM)
                   .sql(' ')
                   .visit(getFrom());













            semiAntiJoinPredicates = (List<Condition>) context.data(DATA_COLLECTED_SEMI_ANTI_JOIN, previousCollected);
            context.data(DATA_COLLECT_SEMI_ANTI_JOIN, previousCollect);
        }

        context.declareTables(false)
               .end(SELECT_FROM);

        // WHERE clause
        // ------------
        context.start(SELECT_WHERE);
        ConditionProviderImpl where = getWhere();

        if (TRUE.equals(context.data().get(DataKey.DATA_SELECT_NO_DATA)))
            context.formatSeparator()
                   .visit(K_WHERE)
                   .sql(' ')
                   .visit(falseCondition());
        else if (!where.hasWhere() && semiAntiJoinPredicates == null)
            ;
        else {
            ConditionProviderImpl actual = new ConditionProviderImpl();

            if (semiAntiJoinPredicates != null)
                actual.addConditions(semiAntiJoinPredicates);

            if (where.hasWhere())
                actual.addConditions(where);

            context.formatSeparator()
                   .visit(K_WHERE)
                   .sql(' ')
                   .visit(actual);
        }

        context.end(SELECT_WHERE);

        // CONNECT BY clause
        // -----------------

        // CUBRID supports this clause only as [ START WITH .. ] CONNECT BY
        // Oracle also knows the CONNECT BY .. [ START WITH ] alternative
        // syntax
        context.start(SELECT_START_WITH);

        if (getConnectByStartWith().hasWhere())
            context.formatSeparator()
                   .visit(K_START_WITH)
                   .sql(' ')
                   .visit(getConnectByStartWith());

        context.end(SELECT_START_WITH);
        context.start(SELECT_CONNECT_BY);

        if (getConnectBy().hasWhere()) {
            context.formatSeparator()
                   .visit(K_CONNECT_BY);

            if (connectByNoCycle)
                context.sql(' ').visit(K_NOCYCLE);

            context.sql(' ').visit(getConnectBy());
        }

        context.end(SELECT_CONNECT_BY);

        // GROUP BY and HAVING clause
        // --------------------------
        context.start(SELECT_GROUP_BY);

        if (grouping) {
            context.formatSeparator()
                   .visit(K_GROUP_BY)
                   .sql(' ');

            // [#1665] Empty GROUP BY () clauses need parentheses
            if (Tools.isEmpty(groupBy))

                // [#4292] Some dialects accept constant expressions in GROUP BY
                // Note that dialects may consider constants as indexed field
                // references, as in the ORDER BY clause!
                if (family == DERBY)
                    context.sql('0');

                // [#4447] CUBRID can't handle subqueries in GROUP BY
                else if (family == CUBRID)
                    context.sql("1 + 0");







                // [#4292] Some dialects don't support empty GROUP BY () clauses
                else if (EMULATE_EMPTY_GROUP_BY_OTHER.contains(family))
                    context.sql('(').visit(DSL.select(one())).sql(')');

                // Few dialects support the SQL standard "grand total" (i.e. empty grouping set)
                else
                    context.sql("()");
            else
                context.visit(groupBy);
        }

        context.end(SELECT_GROUP_BY);

        // HAVING clause
        // -------------
        context.start(SELECT_HAVING);

        if (getHaving().hasWhere())
            context.formatSeparator()
                   .visit(K_HAVING)
                   .sql(' ')
                   .visit(getHaving());

        context.end(SELECT_HAVING);

        // WINDOW clause
        // -------------
        context.start(SELECT_WINDOW);

        if (Tools.isNotEmpty(window) && SUPPORT_WINDOW_CLAUSE.contains(family)) {
            context.formatSeparator()
                   .visit(K_WINDOW)
                   .sql(' ')
                   .declareWindows(true)
                   .visit(window)
                   .declareWindows(false);
        }

        context.end(SELECT_WINDOW);

        // ORDER BY clause for local subselect
        // -----------------------------------
        toSQLOrderBy(
            context,
            originalFields, alternativeFields,
            false, wrapQueryExpressionBodyInDerivedTable,
            orderBy, limit
        );

        // SET operations like UNION, EXCEPT, INTERSECT
        // --------------------------------------------
        if (unionOpSize > 0) {
            unionParenthesis(context, ')', null);

            for (int i = 0; i < unionOpSize; i++) {
                CombineOperator op = unionOp.get(i);

                for (Select<?> other : union.get(i)) {
                    context.formatSeparator()
                           .visit(op.toKeyword(dialect))
                           .sql(' ');

                    unionParenthesis(context, '(', other.getSelect().toArray(EMPTY_FIELD));
                    context.visit(other);
                    unionParenthesis(context, ')', null);
                }

                // [#1658] Close parentheses opened previously
                if (i < unionOpSize - 1)
                    unionParenthesis(context, ')', null);

                switch (unionOp.get(i)) {
                    case EXCEPT:        context.end(SELECT_EXCEPT);        break;
                    case EXCEPT_ALL:    context.end(SELECT_EXCEPT_ALL);    break;
                    case INTERSECT:     context.end(SELECT_INTERSECT);     break;
                    case INTERSECT_ALL: context.end(SELECT_INTERSECT_ALL); break;
                    case UNION:         context.end(SELECT_UNION);         break;
                    case UNION_ALL:     context.end(SELECT_UNION_ALL);     break;
                }
            }

            if (unionOpNesting)
                context.data().remove(DATA_NESTED_SET_OPERATIONS);
        }

        if (wrapQueryExpressionBodyInDerivedTable) {
            context.formatIndentEnd()
                   .formatNewLine()
                   .sql(") t");

            if (applySeekOnDerivedTable) {
                boolean qualify = context.qualify();

                context.formatSeparator()
                       .visit(K_WHERE)
                       .sql(' ')
                       .qualify(false)
                       .visit(getSeekCondition())
                       .qualify(qualify);
            }
        }

        // ORDER BY clause for UNION
        // -------------------------
        boolean qualify = context.qualify();
        try {
            context.qualify(false);
            toSQLOrderBy(
                context,
                originalFields, alternativeFields,
                wrapQueryExpressionInDerivedTable, wrapQueryExpressionBodyInDerivedTable,
                unionOrderBy, unionLimit
            );
        }
        finally {
            context.qualify(qualify);
        }
    }

    private final void toSQLOrderBy(
            Context<?> ctx,
            Field<?>[] originalFields, Field<?>[] alternativeFields,
            boolean wrapQueryExpressionInDerivedTable, boolean wrapQueryExpressionBodyInDerivedTable,
            SortFieldList actualOrderBy,
            Limit actualLimit
    ) {

        ctx.start(SELECT_ORDER_BY);

        // [#6197] When emulating WITH TIES using RANK() in a subquery, we must avoid rendering the
        //         subquery's ORDER BY clause
        if (!getLimit().withTies()







        ) {
            if (!actualOrderBy.isEmpty()) {
                ctx.formatSeparator()
                   .visit(K_ORDER);

                if (orderBySiblings)
                    ctx.sql(' ').visit(K_SIBLINGS);

                ctx.sql(' ').visit(K_BY)
                   .sql(' ');




















                {
                    ctx.visit(actualOrderBy);
                }
            }












        }

        ctx.end(SELECT_ORDER_BY);

        if (wrapQueryExpressionInDerivedTable)
            ctx.formatIndentEnd()
               .formatNewLine()
               .sql(") x");

        if (ctx.data().containsKey(DATA_RENDER_TRAILING_LIMIT_IF_APPLICABLE) && actualLimit.isApplicable())
            ctx.visit(actualLimit);
    }

    private final boolean applySeekOnDerivedTable(Context<?> ctx) {
        return !getSeek().isEmpty() && !getOrderBy().isEmpty() && !unionOp.isEmpty();
    }

    private final boolean wrapQueryExpressionBodyInDerivedTable(Context<?> ctx) {
        return true





        ;
    }





















































    private static final EnumSet<SQLDialect> NO_SUPPORT_UNION_PARENTHESES = EnumSet.of(SQLITE);
    private static final EnumSet<SQLDialect> UNION_PARENTHESIS            = EnumSet.of(DERBY, MARIADB, MYSQL);

    private final boolean unionOpNesting() {
        if (unionOp.size() > 1)
            return true;

        for (QueryPartList<Select<?>> s1 : union)
            for (Select<?> s2 : s1)
                if (s2 instanceof SelectQueryImpl
                        && ((SelectQueryImpl<?>) s2).unionOp.size() > 0)
                    return true;
                else if (s2 instanceof SelectImpl
                        && ((SelectImpl) s2).getDelegate() instanceof SelectQueryImpl
                        && ((SelectQueryImpl<?>) ((SelectImpl) s2).getDelegate()).unionOp.size() > 0)
                    return true;

        return false;
    }

    private final void unionParenthesis(Context<?> ctx, char parenthesis, Field<?>[] fields) {
        boolean derivedTable =

            // [#3579] [#6431] [#7222] Some databases don't support nested set operations at all
            //                         because they do not allow wrapping set op subqueries in parentheses
            NO_SUPPORT_UNION_PARENTHESES.contains(ctx.family())

            // [#3579] [#6431] [#7222] Nested set operations aren't supported, but parenthesised
            //                         set op subqueries are.
            || (TRUE.equals(ctx.data(DATA_NESTED_SET_OPERATIONS)) && UNION_PARENTHESIS.contains(ctx.family()))

            // [#2995] Ambiguity may need to be resolved when parentheses could mean both:
            //         Set op subqueries or insert column lists
            || ctx.data(DATA_INSERT_SELECT_WITHOUT_INSERT_COLUMN_LIST) != null

            // [#7222] Workaround for https://issues.apache.org/jira/browse/DERBY-6984
            || (ctx.family() == DERBY && ctx.subquery());

        if (')' == parenthesis) {
            ctx.formatIndentEnd()
               .formatNewLine();
        }

        // [#3579] Nested set operators aren't supported in some databases. Emulate them via derived tables...
        // [#7222] Do this only in the presence of actual nested set operators
        else if ('(' == parenthesis) {
            if (derivedTable) {
                ctx.formatNewLine()
                   .visit(K_SELECT).sql(' ');

                // [#7222] Workaround for https://issues.apache.org/jira/browse/DERBY-6983
                if (ctx.family() == DERBY)
                    ctx.visit(new SelectFieldList<Field<?>>(Tools.unqualified(fields)));
                else
                    ctx.sql('*');


                ctx.formatSeparator()
                   .visit(K_FROM)
                   .sql(' ');
            }
        }

        // [#3579] ... but don't use derived tables to emulate nested set operators for Firebird, as that
        // only causes many more issues in various contexts where they are not allowed:
        // - Recursive CTE
        // - INSERT SELECT
        // - Derived tables with undefined column names (see also [#3679])

        switch (ctx.family()) {
            case FIREBIRD:
                break;

            default:
                ctx.sql(parenthesis);
                break;
        }

        if ('(' == parenthesis) {
            ctx.formatIndentStart()
               .formatNewLine();
        }

        else if (')'== parenthesis) {
            if (derivedTable)
                ctx.sql(" x");
        }
    }

    @Override
    public final void addSelect(Collection<? extends SelectFieldOrAsterisk> fields) {
        getSelect0().addAll(fields);
    }

    @Override
    public final void addSelect(SelectFieldOrAsterisk... fields) {
        addSelect(Arrays.asList(fields));
    }

    @Override
    public final void setDistinct(boolean distinct) {
        this.distinct = distinct;
    }

    @Override
    public final void addDistinctOn(SelectFieldOrAsterisk... fields) {
        addDistinctOn(Arrays.asList(fields));
    }

    @Override
    public final void addDistinctOn(Collection<? extends SelectFieldOrAsterisk> fields) {
        if (distinctOn == null)
            distinctOn = new QueryPartList<SelectFieldOrAsterisk>();

        distinctOn.addAll(fields);
    }

    @Override
    public final void setInto(Table<?> into) {
        this.into = into;
    }

    @Override
    public final void addOffset(int offset) {
        getLimit().setOffset(offset);
    }

    @Override
    public final void addOffset(Param<Integer> offset) {
        getLimit().setOffset(offset);
    }

    @Override
    public final void addLimit(int numberOfRows) {
        getLimit().setNumberOfRows(numberOfRows);
    }

    @Override
    public final void addLimit(Param<Integer> numberOfRows) {
        getLimit().setNumberOfRows(numberOfRows);
    }

    @Override
    public final void addLimit(int offset, int numberOfRows) {
        getLimit().setOffset(offset);
        getLimit().setNumberOfRows(numberOfRows);
    }

    @Override
    public final void addLimit(int offset, Param<Integer> numberOfRows) {
        getLimit().setOffset(offset);
        getLimit().setNumberOfRows(numberOfRows);
    }

    @Override
    public final void addLimit(Param<Integer> offset, int numberOfRows) {
        getLimit().setOffset(offset);
        getLimit().setNumberOfRows(numberOfRows);
    }

    @Override
    public final void addLimit(Param<Integer> offset, Param<Integer> numberOfRows) {
        getLimit().setOffset(offset);
        getLimit().setNumberOfRows(numberOfRows);
    }










    @Override
    public final void setWithTies(boolean withTies) {
        getLimit().setWithTies(withTies);
    }

    @Override
    public final void setForUpdate(boolean forUpdate) {
        this.forUpdate = forUpdate;
        this.forShare = false;
    }

    @Override
    public final void setForUpdateOf(Field<?>... fields) {
        setForUpdateOf(Arrays.asList(fields));
    }

    @Override
    public final void setForUpdateOf(Collection<? extends Field<?>> fields) {
        setForUpdate(true);
        forUpdateOf = new QueryPartList<Field<?>>(fields);
        forUpdateOfTables = null;
    }

    @Override
    public final void setForUpdateOf(Table<?>... tables) {
        setForUpdate(true);
        forUpdateOf = null;
        forUpdateOfTables = new TableList(Arrays.asList(tables));
    }










    @Override
    public final void setForUpdateNoWait() {
        setForUpdate(true);
        forUpdateMode = ForUpdateMode.NOWAIT;
        forUpdateWait = 0;
    }

    @Override
    public final void setForUpdateSkipLocked() {
        setForUpdate(true);
        forUpdateMode = ForUpdateMode.SKIP_LOCKED;
        forUpdateWait = 0;
    }

    @Override
    public final void setForShare(boolean forShare) {
        this.forUpdate = false;
        this.forShare = forShare;
        this.forUpdateOf = null;
        this.forUpdateOfTables = null;
        this.forUpdateMode = null;
        this.forUpdateWait = 0;
    }















    @Override
    public final List<Field<?>> getSelect() {
        List<Field<?>> result = new ArrayList<Field<?>>();

        for (SelectFieldOrAsterisk f : getSelect1())
            if (f instanceof Field<?>)
                result.add((Field<?>) f);
            else if (f instanceof QualifiedAsterisk)
                result.addAll(Arrays.asList(((QualifiedAsterisk) f).qualifier().fields()));
            else if (f instanceof Asterisk)
                result.addAll(resolveAsterisk(new QueryPartList<Field<?>>()));

        return result;
    }

    final SelectFieldList<SelectFieldOrAsterisk> getSelect0() {
        return select;
    }

    final SelectFieldList<SelectFieldOrAsterisk> getSelect1() {
        if (getSelect0().isEmpty())
            return resolveAsterisk(new SelectFieldList<SelectFieldOrAsterisk>());

        return getSelect0();
    }

    private final <Q extends QueryPartList<? super Field<?>>> Q resolveAsterisk(Q result) {

        // [#109] [#489] [#7231]: SELECT * is only applied when at least one
        // table from the table source is "unknown", i.e. not generated from a
        // physical table. Otherwise, the fields are selected explicitly
        if (knownTableSource())
            for (TableLike<?> table : getFrom())
                for (Field<?> field : table.asTable().fields())
                    result.add(field);

        // The default is SELECT 1, when projections and table sources are
        // both empty
        if (getFrom().isEmpty())
            result.add(one());

        return result;
    }

    private final boolean knownTableSource() {
        for (Table<?> table : getFrom())
            if (!knownTable(table))
                return false;

        return true;
    }

    private final boolean knownTable(Table<?> table) {
        if (table instanceof JoinTable)
            return knownTable(((JoinTable) table).lhs) && knownTable(((JoinTable) table).rhs);
        else
            return table.fieldsRow().size() > 0;
    }

    @SuppressWarnings("unchecked")
    @Override
    public final Class<? extends R> getRecordType() {
        // Generated record classes only come into play, when the select is
        // - on a single table
        // - a select *

        if (getFrom().size() == 1 && getSelect0().isEmpty()) {
            return (Class<? extends R>) getFrom().get(0).asTable().getRecordType();
        }
        else {
            return (Class<? extends R>) RecordImpl.class;
        }
    }

    final TableList getFrom() {
        return from;
    }

    final void setGrouping() {
        grouping = true;
    }

    final ConditionProviderImpl getWhere() {

        // Do not apply SEEK predicates in the WHERE clause, if:
        // - There is no ORDER BY clause (SEEK is non-deterministic)
        // - There is no SEEK clause (obvious case)
        // - There are unions (union is nested in derived table
        //   and SEEK predicate is applied outside). See [#7459]
        if (getOrderBy().isEmpty() || getSeek().isEmpty() || unionOp.size() > 0)
            return condition;

        ConditionProviderImpl result = new ConditionProviderImpl();

        if (condition.hasWhere())
            result.addConditions(condition.getWhere());

        result.addConditions(getSeekCondition());
        return result;
    }

    @SuppressWarnings({ "rawtypes", "unchecked" })
    final Condition getSeekCondition() {
        SortFieldList o = getOrderBy();
        Condition c = null;

        // [#2786] TODO: Check if NULLS FIRST | NULLS LAST clauses are
        // contained in the SortFieldList, in case of which, the below
        // predicates will become a lot more complicated.
        if (o.nulls()) {}

        // If we have uniform sorting, more efficient row value expression
        // predicates can be applied, which can be heavily optimised on some
        // databases.
        if (o.size() > 1 && o.uniform()) {
            if (o.get(0).getOrder() != DESC ^ seekBefore)
                c = row(o.fields()).gt(row(getSeek()));
            else
                c = row(o.fields()).lt(row(getSeek()));
        }

        // With alternating sorting, the SEEK clause has to be explicitly
        // phrased for each ORDER BY field.
        else {
            ConditionProviderImpl or = new ConditionProviderImpl();

            for (int i = 0; i < o.size(); i++) {
                ConditionProviderImpl and = new ConditionProviderImpl();

                for (int j = 0; j < i; j++)
                    and.addConditions(((Field) ((SortFieldImpl<?>) o.get(j)).getField()).eq(getSeek().get(j)));

                SortFieldImpl<?> s = (SortFieldImpl<?>) o.get(i);
                if (s.getOrder() != DESC ^ seekBefore)
                    and.addConditions(((Field) s.getField()).gt(getSeek().get(i)));
                else
                    and.addConditions(((Field) s.getField()).lt(getSeek().get(i)));

                or.addConditions(OR, and);
            }

            c = or;
        }

        return c;
    }

    final ConditionProviderImpl getConnectBy() {
        return connectBy;
    }

    final ConditionProviderImpl getConnectByStartWith() {
        return connectByStartWith;
    }

    final ConditionProviderImpl getHaving() {
        return having;
    }

    final SortFieldList getOrderBy() {
        return (unionOp.size() == 0) ? orderBy : unionOrderBy;
    }

    final QueryPartList<Field<?>> getSeek() {
        return (unionOp.size() == 0) ? seek : unionSeek;
    }

    final Limit getLimit() {
        return (unionOp.size() == 0) ? limit : unionLimit;
    }

    final SortFieldList getNonEmptyOrderBy(Configuration configuration) {
        if (getOrderBy().isEmpty()) {
            SortFieldList result = new SortFieldList();

            switch (configuration.family()) {











                default:
                    result.add(DSL.field("({select} 0)").asc());
                    break;
            }
            return result;
        }

        return getOrderBy();
    }

    final SortFieldList getNonEmptyOrderByForDistinct(Configuration configuration) {
        SortFieldList order = new SortFieldList();
        order.addAll(getNonEmptyOrderBy(configuration));

        for (Field<?> field : getSelect())
            order.add(field.asc());

        return order;
    }

    @Override
    public final void addOrderBy(Collection<? extends OrderField<?>> fields) {
        getOrderBy().addAll(Tools.sortFields(fields));
    }

    @Override
    public final void addOrderBy(OrderField<?>... fields) {
        addOrderBy(Arrays.asList(fields));
    }

    @Override
    public final void addOrderBy(int... fieldIndexes) {
        Field<?>[] fields = new Field[fieldIndexes.length];

        for (int i = 0; i < fieldIndexes.length; i++) {
            fields[i] = inline(fieldIndexes[i]);
        }

        addOrderBy(fields);
    }

    @Override
    public final void setOrderBySiblings(boolean orderBySiblings) {
        if (unionOp.size() == 0)
            this.orderBySiblings = orderBySiblings;
        else
            this.unionOrderBySiblings = orderBySiblings;
    }

    @Override
    public final void addSeekAfter(Field<?>... fields) {
        addSeekAfter(Arrays.asList(fields));
    }

    @Override
    public final void addSeekAfter(Collection<? extends Field<?>> fields) {
        if (unionOp.size() == 0)
            seekBefore = false;
        else
            unionSeekBefore = false;

        getSeek().addAll(fields);
    }

    @Override
    public final void addSeekBefore(Field<?>... fields) {
        addSeekBefore(Arrays.asList(fields));
    }

    @Override
    public final void addSeekBefore(Collection<? extends Field<?>> fields) {
        if (unionOp.size() == 0)
            seekBefore = true;
        else
            unionSeekBefore = true;

        getSeek().addAll(fields);
    }

    @Override
    public final void addConditions(Condition conditions) {
        condition.addConditions(conditions);
    }

    @Override
    public final void addConditions(Condition... conditions) {
        condition.addConditions(conditions);
    }

    @Override
    public final void addConditions(Collection<? extends Condition> conditions) {
        condition.addConditions(conditions);
    }

    @Override
    public final void addConditions(Operator operator, Condition conditions) {
        condition.addConditions(operator, conditions);
    }

    @Override
    public final void addConditions(Operator operator, Condition... conditions) {
        condition.addConditions(operator, conditions);
    }

    @Override
    public final void addConditions(Operator operator, Collection<? extends Condition> conditions) {
        condition.addConditions(operator, conditions);
    }

    final void setConnectByNoCycle(boolean connectByNoCycle) {
        this.connectByNoCycle = connectByNoCycle;
    }

    final void setStartWith(Condition condition) {
        connectByStartWith.addConditions(condition);
    }

    final void setHint(String hint) {
        this.hint = hint;
    }

    final void setOption(String option) {
        this.option = option;
    }

    @Override
    final boolean isForUpdate() {
        return forUpdate;
    }

    @Override
    public final void addFrom(Collection<? extends TableLike<?>> f) {
        for (TableLike<?> provider : f)
            getFrom().add(provider.asTable());
    }

    @Override
    public final void addFrom(TableLike<?> f) {
        getFrom().add(f.asTable());
    }

    @Override
    public final void addFrom(TableLike<?>... f) {
        for (TableLike<?> provider : f)
            getFrom().add(provider.asTable());
    }

    @Override
    public final void addConnectBy(Condition c) {
        getConnectBy().addConditions(c);
    }

    @Override
    public final void addConnectByNoCycle(Condition c) {
        getConnectBy().addConditions(c);
        setConnectByNoCycle(true);
    }

    @Override
    public final void setConnectByStartWith(Condition c) {
        setStartWith(c);
    }

    @Override
    public final void addGroupBy(Collection<? extends GroupField> fields) {
        setGrouping();

        if (groupBy == null)
            groupBy = new QueryPartList<GroupField>();

        groupBy.addAll(fields);
    }

    @Override
    public final void addGroupBy(GroupField... fields) {
        addGroupBy(Arrays.asList(fields));
    }

    @Override
    public final void addHaving(Condition conditions) {
        getHaving().addConditions(conditions);
    }

    @Override
    public final void addHaving(Condition... conditions) {
        getHaving().addConditions(conditions);
    }

    @Override
    public final void addHaving(Collection<? extends Condition> conditions) {
        getHaving().addConditions(conditions);
    }

    @Override
    public final void addHaving(Operator operator, Condition conditions) {
        getHaving().addConditions(operator, conditions);
    }

    @Override
    public final void addHaving(Operator operator, Condition... conditions) {
        getHaving().addConditions(operator, conditions);
    }

    @Override
    public final void addHaving(Operator operator, Collection<? extends Condition> conditions) {
        getHaving().addConditions(operator, conditions);
    }

    @Override
    public final void addWindow(WindowDefinition... definitions) {
        addWindow(Arrays.asList(definitions));
    }

    @Override
    public final void addWindow(Collection<? extends WindowDefinition> definitions) {
        if (window == null)
            window = new WindowList();

        window.addAll(definitions);
    }

    private final Select<R> combine(CombineOperator op, Select<? extends R> other) {
        int index = unionOp.size() - 1;

        if (index == -1 || unionOp.get(index) != op || op == EXCEPT || op == EXCEPT_ALL) {
            unionOp.add(op);
            union.add(new QueryPartList<Select<?>>());

            index++;
        }

        union.get(index).add(other);
        return this;
    }

    @Override
    public final Select<R> union(Select<? extends R> other) {
        return combine(UNION, other);
    }

    @Override
    public final Select<R> unionAll(Select<? extends R> other) {
        return combine(UNION_ALL, other);
    }

    @Override
    public final Select<R> except(Select<? extends R> other) {
        return combine(EXCEPT, other);
    }

    @Override
    public final Select<R> exceptAll(Select<? extends R> other) {
        return combine(EXCEPT_ALL, other);
    }

    @Override
    public final Select<R> intersect(Select<? extends R> other) {
        return combine(INTERSECT, other);
    }

    @Override
    public final Select<R> intersectAll(Select<? extends R> other) {
        return combine(INTERSECT_ALL, other);
    }

    @Override
    public final void addJoin(TableLike<?> table, Condition conditions) {
        addJoin(table, JoinType.JOIN, conditions);
    }

    @Override
    public final void addJoin(TableLike<?> table, Condition... conditions) {
        addJoin(table, JoinType.JOIN, conditions);
    }

    @Override
    public final void addJoin(TableLike<?> table, JoinType type, Condition conditions) {
        addJoin0(table, type, conditions, null);
    }

    @Override
    public final void addJoin(TableLike<?> table, JoinType type, Condition... conditions) {
        addJoin0(table, type, conditions, null);
    }













    private final void addJoin0(TableLike<?> table, JoinType type, Object conditions, Field<?>[] partitionBy) {

        // TODO: This and similar methods should be refactored, patterns extracted...
        int index = getFrom().size() - 1;
        Table<?> joined = null;

        switch (type) {
            case JOIN:
            case STRAIGHT_JOIN:
            case LEFT_SEMI_JOIN:
            case LEFT_ANTI_JOIN:
            case FULL_OUTER_JOIN: {
                TableOptionalOnStep<Record> o = getFrom().get(index).join(table, type);

                if (conditions instanceof Condition)
                    joined = o.on((Condition) conditions);
                else
                    joined = o.on((Condition[]) conditions);

                break;
            }

            case LEFT_OUTER_JOIN:
            case RIGHT_OUTER_JOIN: {
                TablePartitionByStep<?> p = (TablePartitionByStep<?>) getFrom().get(index).join(table, type);
                TableOnStep<?> o = p;




                if (conditions instanceof Condition)
                    joined = o.on((Condition) conditions);
                else
                    joined = o.on((Condition[]) conditions);

                break;
            }

            // These join types don't take any ON clause. Ignore conditions.
            case CROSS_JOIN:
            case NATURAL_JOIN:
            case NATURAL_LEFT_OUTER_JOIN:
            case NATURAL_RIGHT_OUTER_JOIN:




                joined = getFrom().get(index).join(table, type);
                break;

            default: throw new IllegalArgumentException("Bad join type: " + type);
        }

        getFrom().set(index, joined);
    }

    @Override
    public final void addJoinOnKey(TableLike<?> table, JoinType type) throws DataAccessException {
        // TODO: This and similar methods should be refactored, patterns extracted...

        int index = getFrom().size() - 1;
        Table<?> joined = null;

        switch (type) {
            case JOIN:
            case LEFT_OUTER_JOIN:
            case RIGHT_OUTER_JOIN:
            case FULL_OUTER_JOIN:
            case LEFT_SEMI_JOIN:
            case LEFT_ANTI_JOIN:
                joined = getFrom().get(index).join(table, type).onKey();
                break;

            default:
                throw new IllegalArgumentException("JoinType " + type + " is not supported with the addJoinOnKey() method. Use INNER or OUTER JOINs only");
        }

        getFrom().set(index, joined);
    }

    @Override
    public final void addJoinOnKey(TableLike<?> table, JoinType type, TableField<?, ?>... keyFields) throws DataAccessException {
        // TODO: This and similar methods should be refactored, patterns extracted...

        int index = getFrom().size() - 1;
        Table<?> joined = null;

        switch (type) {
            case JOIN:
            case LEFT_OUTER_JOIN:
            case RIGHT_OUTER_JOIN:
            case FULL_OUTER_JOIN:
            case LEFT_SEMI_JOIN:
            case LEFT_ANTI_JOIN:
                joined = getFrom().get(index).join(table, type).onKey(keyFields);
                break;

            default:
                throw new IllegalArgumentException("JoinType " + type + " is not supported with the addJoinOnKey() method. Use INNER or OUTER JOINs only");
        }

        getFrom().set(index, joined);
    }

    @Override
    public final void addJoinOnKey(TableLike<?> table, JoinType type, ForeignKey<?, ?> key) {
        // TODO: This and similar methods should be refactored, patterns extracted...

        int index = getFrom().size() - 1;
        Table<?> joined = null;

        switch (type) {
            case JOIN:
            case LEFT_OUTER_JOIN:
            case RIGHT_OUTER_JOIN:
            case FULL_OUTER_JOIN:
            case LEFT_SEMI_JOIN:
            case LEFT_ANTI_JOIN:
                joined = getFrom().get(index).join(table, type).onKey(key);
                break;

            default:
                throw new IllegalArgumentException("JoinType " + type + " is not supported with the addJoinOnKey() method. Use INNER or OUTER JOINs only");
        }

        getFrom().set(index, joined);
    }

    @Override
    public final void addJoinUsing(TableLike<?> table, Collection<? extends Field<?>> fields) {
        addJoinUsing(table, JoinType.JOIN, fields);
    }

    @Override
    public final void addJoinUsing(TableLike<?> table, JoinType type, Collection<? extends Field<?>> fields) {
        // TODO: This and similar methods should be refactored, patterns extracted...

        int index = getFrom().size() - 1;
        Table<?> joined = null;

        switch (type) {
            case JOIN:
            case LEFT_OUTER_JOIN:
            case RIGHT_OUTER_JOIN:
            case FULL_OUTER_JOIN:
            case LEFT_SEMI_JOIN:
            case LEFT_ANTI_JOIN:
                joined = getFrom().get(index).join(table, type).using(fields);
                break;

            default:
                throw new IllegalArgumentException("JoinType " + type + " is not supported with the addJoinUsing() method. Use INNER or OUTER JOINs only");
        }

        getFrom().set(index, joined);
    }

    @Override
    public final void addHint(String h) {
        setHint(h);
    }

    @Override
    public final void addOption(String o) {
        setOption(o);
    }

    // -------------------------------------------------------------------------
    // Utility classes
    // -------------------------------------------------------------------------

    /**
     * The lock mode for the <code>FOR UPDATE</code> clause, if set.
     */
    private static enum ForUpdateMode {
        WAIT("wait"),
        NOWAIT("nowait"),
        SKIP_LOCKED("skip locked"),

        ;

        private final Keyword keyword;

        private ForUpdateMode(String sql) {
            this.keyword = DSL.keyword(sql);
        }

        public final Keyword toKeyword() {
            return keyword;
        }
    }
}
