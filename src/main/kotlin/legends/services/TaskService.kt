package legends.services

import legends.dao.TaskDao
import legends.dao.UserDao
import legends.dto.TaskDto
import legends.exceptions.BadRequestException
import legends.exceptions.LegendsException
import legends.models.ImageModel
import legends.models.TaskModel
import legends.utils.ValidationUtils.INVALID_ID
import legends.utils.ValidationUtils.validateAndGetReason
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.stereotype.Service
import java.io.File
import java.io.IOException
import java.nio.file.Files

@Service
class TaskService(
        private val taskDao: TaskDao,
        private val userDao: UserDao
) {
    companion object {
        private val IMAGE_PATTERN = Regex("image/(jpeg|png)", RegexOption.IGNORE_CASE)
        private val IMAGE_NAME_PATTERN = Regex("\\d{10}")   // nginx pattern file
    }

    private val logger = LoggerFactory.getLogger(TaskService::class.java)

    fun handleUploadedFile(
            userId: Long,
            fullPath: String,
            loadPrefix: String,
            contentType: String
    ): ImageModel {

        userDao.getUserOrThrow(userId).checkModerator()

        if (!IMAGE_PATTERN.matches(contentType)) {
            logger.warn("Content type doesn't match: [$contentType]")
            throw LegendsException(HttpStatus.UNSUPPORTED_MEDIA_TYPE)
            { "На сервер разрешено загружать только изображения форматов .jpg и .png" }
        }

        val imageName = fullPath.split('/').last()
        val nginxPath = loadPrefix + imageName

        return ImageModel(
                fullPath = fullPath,
                imageName = imageName,
                nginxPath = nginxPath
        )
    }

    fun createTask(userId: Long, dto: TaskDto): TaskModel {
        userDao.getUserOrThrow(userId).checkModerator()

        val reason = validateAndGetReason(dto)
        if (reason != null) {
            throw BadRequestException { reason }
        }

        val task = dto.convert(taskId = INVALID_ID)
        val taskId = taskDao.insertTask(task)

        return task.copy(taskId = taskId)
    }

    fun updateTask(userId: Long, updated: TaskDto): TaskModel {
        userDao.getUserOrThrow(userId).checkModerator()

        val reason = validateAndGetReason(updated)
        if (reason != null) {
            throw BadRequestException { reason }
        }
        if (updated.taskId == null) {
            throw BadRequestException { "Отсутсвует номер обновляемого задания [${updated.taskName}]" }
        }

        val task = updated.convert(taskId = updated.taskId)
        taskDao.updateTask(task)

        return task
    }

    fun getTask(userId: Long, taskId: Long): TaskModel {
        userDao.getUserOrThrow(userId).checkModerator()
        return taskDao.getTaskOrThrow(taskId)
    }

    fun getAllTasks(userId: Long): List<TaskModel> {
        userDao.getUserOrThrow(userId).checkModerator()
        return taskDao.getAllTasks()
    }

    fun deleteTask(userId: Long, taskId: Long) {
        userDao.getUserOrThrow(userId).checkModerator()
        taskDao.deleteTask(taskId)
    }

    @Deprecated(level = DeprecationLevel.HIDDEN, message = "It's not ready")
    fun deleteImage(userId: Long, imagePath: String) {
        userDao.getUserOrThrow(userId).checkModerator()
        try {
            val imageFile = File(imagePath)

            if (!imageFile.isFile) {
                throw BadRequestException { "Удалять можно лишь загруженные изображения." }
            }
            if (!IMAGE_NAME_PATTERN.matches(imageFile.name)) {
                throw BadRequestException { "Удаление файла [${imageFile.name}] запрещено." }
            }

            Files.deleteIfExists(imageFile.toPath())

        } catch (e: IOException) {
            logger.error("Failed to delete file", e)
            throw LegendsException(HttpStatus.BAD_REQUEST)
            { "Не удалось удалить указанный файл" }
        }
    }
}