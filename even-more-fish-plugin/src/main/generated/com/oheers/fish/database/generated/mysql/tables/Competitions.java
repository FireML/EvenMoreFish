/*
 * This file is generated by jOOQ.
 */
package com.oheers.fish.database.generated.mysql.tables;


import com.oheers.fish.database.generated.mysql.DefaultSchema;
import com.oheers.fish.database.generated.mysql.Keys;
import com.oheers.fish.database.generated.mysql.tables.records.CompetitionsRecord;
import org.jooq.*;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;

import java.util.Collection;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Competitions extends TableImpl<CompetitionsRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>${table.prefix}competitions</code>
     */
    public static final Competitions COMPETITIONS = new Competitions();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<CompetitionsRecord> getRecordType() {
        return CompetitionsRecord.class;
    }

    /**
     * The column <code>${table.prefix}competitions.ID</code>.
     */
    public final TableField<CompetitionsRecord, Integer> ID = createField(DSL.name("ID"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>${table.prefix}competitions.COMPETITION_NAME</code>.
     */
    public final TableField<CompetitionsRecord, String> COMPETITION_NAME = createField(DSL.name("COMPETITION_NAME"), SQLDataType.VARCHAR(256).nullable(false), this, "");

    /**
     * The column <code>${table.prefix}competitions.WINNER_UUID</code>.
     */
    public final TableField<CompetitionsRecord, String> WINNER_UUID = createField(DSL.name("WINNER_UUID"), SQLDataType.VARCHAR(128).nullable(false), this, "");

    /**
     * The column <code>${table.prefix}competitions.WINNER_FISH</code>.
     */
    public final TableField<CompetitionsRecord, String> WINNER_FISH = createField(DSL.name("WINNER_FISH"), SQLDataType.VARCHAR(256).nullable(false), this, "");

    /**
     * The column <code>${table.prefix}competitions.WINNER_SCORE</code>.
     */
    public final TableField<CompetitionsRecord, Float> WINNER_SCORE = createField(DSL.name("WINNER_SCORE"), SQLDataType.REAL.nullable(false), this, "");

    /**
     * The column <code>${table.prefix}competitions.CONTESTANTS</code>.
     */
    public final TableField<CompetitionsRecord, String> CONTESTANTS = createField(DSL.name("CONTESTANTS"), SQLDataType.CLOB.nullable(false), this, "");

    private Competitions(Name alias, Table<CompetitionsRecord> aliased) {
        this(alias, aliased, (Field<?>[]) null, null);
    }

    private Competitions(Name alias, Table<CompetitionsRecord> aliased, Field<?>[] parameters, Condition where) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table(), where);
    }

    /**
     * Create an aliased <code>${table.prefix}competitions</code> table
     * reference
     */
    public Competitions(String alias) {
        this(DSL.name(alias), COMPETITIONS);
    }

    /**
     * Create an aliased <code>${table.prefix}competitions</code> table
     * reference
     */
    public Competitions(Name alias) {
        this(alias, COMPETITIONS);
    }

    /**
     * Create a <code>${table.prefix}competitions</code> table reference
     */
    public Competitions() {
        this(DSL.name("${table.prefix}competitions"), null);
    }

    @Override
    public Schema getSchema() {
        return aliased() ? null : DefaultSchema.DEFAULT_SCHEMA;
    }

    @Override
    public Identity<CompetitionsRecord, Integer> getIdentity() {
        return (Identity<CompetitionsRecord, Integer>) super.getIdentity();
    }

    @Override
    public UniqueKey<CompetitionsRecord> getPrimaryKey() {
        return Keys.CONSTRAINT_9;
    }

    @Override
    public Competitions as(String alias) {
        return new Competitions(DSL.name(alias), this);
    }

    @Override
    public Competitions as(Name alias) {
        return new Competitions(alias, this);
    }

    @Override
    public Competitions as(Table<?> alias) {
        return new Competitions(alias.getQualifiedName(), this);
    }

    /**
     * Rename this table
     */
    @Override
    public Competitions rename(String name) {
        return new Competitions(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public Competitions rename(Name name) {
        return new Competitions(name, null);
    }

    /**
     * Rename this table
     */
    @Override
    public Competitions rename(Table<?> name) {
        return new Competitions(name.getQualifiedName(), null);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Competitions where(Condition condition) {
        return new Competitions(getQualifiedName(), aliased() ? this : null, null, condition);
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Competitions where(Collection<? extends Condition> conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Competitions where(Condition... conditions) {
        return where(DSL.and(conditions));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Competitions where(Field<Boolean> condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Competitions where(SQL condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Competitions where(@Stringly.SQL String condition) {
        return where(DSL.condition(condition));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Competitions where(@Stringly.SQL String condition, Object... binds) {
        return where(DSL.condition(condition, binds));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    @PlainSQL
    public Competitions where(@Stringly.SQL String condition, QueryPart... parts) {
        return where(DSL.condition(condition, parts));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Competitions whereExists(Select<?> select) {
        return where(DSL.exists(select));
    }

    /**
     * Create an inline derived table from this table
     */
    @Override
    public Competitions whereNotExists(Select<?> select) {
        return where(DSL.notExists(select));
    }
}
