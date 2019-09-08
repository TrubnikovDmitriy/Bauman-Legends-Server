package legends.utils

import legends.exceptions.LegendsException
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import java.sql.PreparedStatement
import java.sql.SQLException
import java.sql.Types
import javax.sql.DataSource
import java.sql.Array as SqlArray

object SqlUtils {

    private val logger = LoggerFactory.getLogger(SqlUtils::class.java)

    fun convertToSqlArray(array: Collection<String>, dataSource: DataSource): SqlArray {
        try {
            dataSource.connection.use { connection ->
                return connection.createArrayOf("text" /*for `Int` use "integer"*/, array.toTypedArray())
            }
        } catch (e: SQLException) {
            logger.error("Failed to convert kotlin Collection to SQL array", e)
            throw LegendsException(HttpStatus.INTERNAL_SERVER_ERROR)
            { "Непредвиденная ошибка при создании SQL массива" }
        }
    }

    fun setNullable(position: Int, value: String?, preparedStatement: PreparedStatement) {
        if (value != null) {
            preparedStatement.setString(position, value)
        } else {
            preparedStatement.setNull(position, Types.VARCHAR)
        }
    }

    fun setNullable(position: Int, value: Long?, ps: PreparedStatement) {
        if (value != null) {
            ps.setLong(position, value)
        } else {
            ps.setNull(position, Types.NUMERIC)
        }
    }

    fun setNullable(position: Int, value: Int?, ps: PreparedStatement) {
        if (value != null) {
            ps.setInt(position, value)
        } else {
            ps.setNull(position, Types.INTEGER)
        }
    }
}