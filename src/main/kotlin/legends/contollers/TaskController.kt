package legends.contollers

import legends.dto.HintDto
import legends.dto.TaskDto
import legends.services.TaskService
import legends.utils.getUserIdOrThrow
import legends.views.*
import org.slf4j.LoggerFactory
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.servlet.http.HttpSession

@RestController
@RequestMapping("/task")
class TaskController(private val taskService: TaskService) {

    private val logger = LoggerFactory.getLogger(TaskController::class.java)

    @PostMapping("/image")
    fun uploadImage(
            @RequestParam("content_type") contentType: String,
            @RequestParam("full_path") fullPath: String,
            @RequestParam("load_prefix") loadPrefix: String,
            httpSession: HttpSession
    ): ResponseEntity<String> {
        val userId = httpSession.getUserIdOrThrow()
        logger.warn("Upload image: userId=[$userId], path=[$fullPath]")
        val image = taskService.handleUploadedFile(
                userId = userId,
                fullPath = fullPath,
                loadPrefix = loadPrefix,
                contentType = contentType
        )
        return ResponseEntity(image.nginxPath, HttpStatus.OK)
    }

    @PostMapping
    fun createTask(
            @RequestBody dtoTask: TaskDto,
            httpSession: HttpSession
    ): ResponseEntity<TaskView> {
        val userId = httpSession.getUserIdOrThrow()
        logger.warn("Create task: userId=[$userId], task=[$dtoTask]")
        val task = taskService.createTask(userId, dtoTask)
        return ResponseEntity(TaskView(task), HttpStatus.CREATED)
    }

    @PutMapping
    fun updateTask(
            @RequestBody dtoTask: TaskDto,
            httpSession: HttpSession
    ): ResponseEntity<TaskView> {
        val userId = httpSession.getUserIdOrThrow()
        logger.warn("Update task: userId=[$userId], task=[$dtoTask]")
        val task = taskService.updateTask(userId, dtoTask)
        return ResponseEntity(TaskView(task), HttpStatus.OK)
    }

    @GetMapping
    fun getTask(
            @RequestParam(required = true, value = "task_id") taskId: Long,
            httpSession: HttpSession
    ): ResponseEntity<TaskView> {
        val userId = httpSession.getUserIdOrThrow()
        logger.info("Get task: userId=[$userId], taskId=[$taskId]")
        val task = taskService.getTask(userId, taskId)
        return ResponseEntity(TaskView(task), HttpStatus.OK)
    }

    @GetMapping("/all")
    fun getAllTasks(httpSession: HttpSession): ResponseEntity<List<TaskView>>  {
        val userId = httpSession.getUserIdOrThrow()
        val tasks = taskService.getAllTasks(userId)
        return ResponseEntity(tasks.toView(), HttpStatus.OK)
    }

    @DeleteMapping
    fun deleteTask(
            @RequestParam(required = true, value = "task_id") taskId: Long,
            httpSession: HttpSession
    ): ResponseEntity<Any> {
        val userId = httpSession.getUserIdOrThrow()
        logger.warn("Update task: userId=[$userId], taskId=[$taskId]")
        taskService.deleteTask(userId, taskId)
        return ResponseEntity(HttpStatus.OK)
    }
}
