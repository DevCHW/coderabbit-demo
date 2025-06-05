package io.devchw.coderabbit

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController
import java.util.concurrent.atomic.AtomicLong

/**
 * @author DevCHW
 * @since 2025-06-05
 */
@RestController
@RequestMapping("/api/board")
class BoardController {

    private val posts = mutableMapOf<Long, Post>()
    private val idGenerator = AtomicLong(1)

    // 게시물 생성
    @PostMapping
    fun createPost(@RequestBody post: Post): ResponseEntity<Post> {
        val id = idGenerator.getAndIncrement()
        post.id = id
        posts[id] = post
        return ResponseEntity.status(HttpStatus.CREATED).body(post)
    }

    // 게시물 목록 조회
    @GetMapping
    fun getPosts(): ResponseEntity<List<Post>> {
        return ResponseEntity.ok(posts.values.toList())
    }

    // 게시물 상세 조회
    @GetMapping("/{id}")
    fun getPost(@PathVariable id: Long): ResponseEntity<Post> {
        val post = posts[id]
        return if (post != null) {
            ResponseEntity.ok(post)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    // 게시물 수정
    @PutMapping("/{id}")
    fun updatePost(@PathVariable id: Long, @RequestBody updatedPost: Post): ResponseEntity<Post> {
        if (!posts.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
        updatedPost.id = id
        posts[id] = updatedPost
        return ResponseEntity.ok(updatedPost)
    }

    // 게시물 삭제
    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable id: Long): ResponseEntity<Void> {
        if (!posts.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
        posts.remove(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}