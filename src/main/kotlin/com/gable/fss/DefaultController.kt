package com.gable.fss

import org.apache.tomcat.util.http.fileupload.IOUtils
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.core.io.FileSystemResource
import org.springframework.http.HttpHeaders
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile
import java.io.File

@RestController
@RequestMapping("/")
open class DefaultController {
    @Autowired
    lateinit var fssConfig: FssConfig

    @PostMapping("/")
    fun upload(@RequestParam("path") path: String, @RequestParam("file") file: MultipartFile): ResponseEntity<ResponseDetails> {
        return try {
            val dest = File(fssConfig.rootLocation + path)
            IOUtils.copy(file.inputStream, dest.outputStream())
            ResponseEntity.ok().body(
                ResponseDetails().also {
                it.successful = true
                }
            )
        } catch (t: Throwable) {
            ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(
                    ResponseDetails().also {
                    it.successful = false
                    it.errorMessage = t.message ?: t.toString()
                }
            )
        }
    }

    @GetMapping("/")
    fun download(@RequestParam("path") path: String): ResponseEntity<Any> {
        val src = File(fssConfig.rootLocation + path)
        if (!src.exists())
            return ResponseEntity.notFound().build()
        return ResponseEntity.ok().header(HttpHeaders.CONTENT_DISPOSITION,
            "attachment; filename=\"" + src.name + "\"").body(FileSystemResource(src))
    }
}
