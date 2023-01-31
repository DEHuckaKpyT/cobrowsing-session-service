package com.example.cobrowsing.plugins

import com.example.cobrowsing.models.*
import com.zaxxer.hikari.HikariConfig
import com.zaxxer.hikari.HikariDataSource
import io.ktor.server.application.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jetbrains.exposed.sql.*
import org.jetbrains.exposed.sql.statements.BatchInsertStatement
import org.jetbrains.exposed.sql.statements.InsertStatement
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import java.sql.Connection.TRANSACTION_READ_COMMITTED
import java.sql.Connection.TRANSACTION_SERIALIZABLE


/**
 * Created on 29.11.2022.
 *<p>
 *
 * @author Denis Matytsin
 */
fun Application.configureDatabase() {

    val config = environment.config.config("database")

    fun connect() {
        val hikariConfig = HikariConfig().apply {
            driverClassName = "org.postgresql.Driver"
            jdbcUrl = config.property("url").getString()
            username = config.property("username").getString()
            password = config.property("password").getString()
            maximumPoolSize = 10
            isAutoCommit = false
            transactionIsolation = "TRANSACTION_READ_COMMITTED"
            validate()
        }

        Database.connect(HikariDataSource(hikariConfig))
    }

    fun createTables() {
        transaction {
            SchemaUtils.createMissingTablesAndColumns(
                Sessions,
                Chats,
                Messages,
                SessionEvents
            )
        }
    }

    connect()
    createTables()
}

suspend inline fun <T> read(crossinline block: () -> T): T =
    withContext(Dispatchers.IO) {
        transaction(TRANSACTION_READ_COMMITTED, repetitionAttempts = 0, readOnly = true) {
            block()
        }
    }

suspend inline fun <T> execute(crossinline block: () -> T): T =
    withContext(Dispatchers.IO) {
        transaction(TRANSACTION_READ_COMMITTED, repetitionAttempts = 0, readOnly = false) {
            block()
        }
    }

suspend inline fun <T> executeStrictly(crossinline block: () -> T): T =
    withContext(Dispatchers.IO) {
        transaction(TRANSACTION_SERIALIZABLE, repetitionAttempts = 0, readOnly = false) {
            block()
        }
    }

/**
 * Example:
 * val item = ...
 * MyTable.upsert {
 * 	it[id] = item.id
 *	it[value1] = item.value1
 * }
 */

fun <T : Table> T.upsert(
    vararg keys: Column<*> = (primaryKey ?: throw IllegalArgumentException("primary key is missing")).columns,
    body: T.(InsertStatement<Number>) -> Unit
) =
    InsertOrUpdate<Number>(this, keys = keys).apply {
        body(this)
        execute(TransactionManager.current())
    }

class InsertOrUpdate<Key : Any>(
    table: Table,
    isIgnore: Boolean = false,
    private vararg val keys: Column<*>
) : InsertStatement<Key>(table, isIgnore) {
    override fun prepareSQL(transaction: Transaction): String {
        val tm = TransactionManager.current()
        val updateSetter = (table.columns - keys).joinToString { "${tm.identity(it)} = EXCLUDED.${tm.identity(it)}" }
        val onConflict = "ON CONFLICT (${keys.joinToString { tm.identity(it) }}) DO UPDATE SET $updateSetter"
        return "${super.prepareSQL(transaction)} $onConflict"
    }
}

/**
 * Example:
 * val items = listOf(...)
 * MyTable.batchUpsert(items) { table, item  ->
 * 	table[id] = item.id
 *	table[value1] = item.value1
 * }
 */

fun <T : Table, E> T.batchUpsert(
    data: Collection<E>,
    vararg keys: Column<*> = (primaryKey ?: throw IllegalArgumentException("primary key is missing")).columns,
    body: T.(BatchInsertStatement, E) -> Unit
) =
    BatchInsertOrUpdate(this, keys = keys).apply {
        data.forEach {
            addBatch()
            body(this, it)
        }
        execute(TransactionManager.current())
    }

class BatchInsertOrUpdate(
    table: Table,
    isIgnore: Boolean = false,
    private vararg val keys: Column<*>
) : BatchInsertStatement(table, isIgnore, shouldReturnGeneratedValues = false) {
    override fun prepareSQL(transaction: Transaction): String {
        val tm = TransactionManager.current()
        val updateSetter = (table.columns - keys).joinToString { "${tm.identity(it)} = EXCLUDED.${tm.identity(it)}" }
        val onConflict = "ON CONFLICT (${keys.joinToString { tm.identity(it) }}) DO UPDATE SET $updateSetter"
        return "${super.prepareSQL(transaction)} $onConflict"
    }
}