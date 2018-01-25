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
package org.jooq;

import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.util.List;

/**
 * A parameter object that is passed to {@link DiagnosticsListener} methods.
 *
 * @author Lukas Eder
 */
public interface DiagnosticsContext {

    /**
     * The {@link ResultSet} available in this context, or <code>null</code>, if
     * there was no result set.
     */
    ResultSet resultSet();

    /**
     * The number of rows that were fetched from {@link #resultSet()}, or
     * <code>-1</code> if there was no result set.
     */
    int resultSetFetchedRows();

    /**
     * The number of rows that were actually available from
     * {@link #resultSet()}, or <code>-1</code> if there was no result set.
     * <p>
     * Calling this method will try to scroll to the end of the
     * {@link #resultSet()}, in order to count the number of rows, which incurs
     * overhead!
     * <p>
     * If the result set is still being consumed (i.e. prior to the
     * {@link ResultSet#close()} call), and scrolling back to the current row
     * after scrolling to the end of {@link #resultSet()} is not possible (e.g.
     * because the driver supports only {@link ResultSet#TYPE_FORWARD_ONLY}),
     * then this will return the same value as {@link #resultSetFetchedRows()}.
     */
    int resultSetActualRows();

    /**
     * The number of columns that were fetched from the {@link #resultSet()}, or
     * <code>-1</code> if there was no result set.
     * <p>
     * If the result set is still being consumed (i.e. prior to the
     * {@link ResultSet#close()} call), then this will return the number of
     * columns that were retrieved from the {@link #resultSet()} set <em>thus
     * far</em>.
     */
    int resultSetFetchedColumns();

    /**
     * The number of columns that were actually available from
     * {@link #resultSet()}, or <code>-1</code> if there was no result set.
     */
    int resultSetActualColumns();

    /**
     * There had been an unnecessary {@link ResultSet#wasNull()} call to check
     * the a non-primitive type fetched previously was null, or the call was
     * made more than once.
     * <p>
     * {@link #resultSetColumnIndex()} will return the relevant column index for
     * which the {@link ResultSet#wasNull()} call was missing.
     *
     * @see #resultSetColumnIndex()
     */
    boolean resultSetUnnecessaryWasNullCall();

    /**
     * There had been a missing {@link ResultSet#wasNull()} call on a previously
     * fetched primitive type, which is reported to be
     * {@link ResultSetMetaData#isNullable(int)}.
     * <p>
     * {@link #resultSetColumnIndex()} will return the relevant column index for
     * which the {@link ResultSet#wasNull()} call was missing.
     *
     * @see #resultSetColumnIndex()
     */
    boolean resultSetMissingWasNullCall();

    /**
     * The relevant column index (1 based) in the {@link ResultSet} if
     * applicable, or <code>0</code> if there was no result set.
     */
    int resultSetColumnIndex();

    /**
     * The normalised statement that all duplicates correspond to.
     */
    String normalisedStatement();

    /**
     * The duplicate statements that all correspond to a single normalised
     * statement.
     */
    List<String> duplicateStatements();
}