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
import org.springframework.beans.factory.annotation.Autowired

/**
 * @author DevCHW
 * @since 2025-06-05
 */
@RestController
@RequestMapping("/api/board")
class BoardController(
    @Autowired private val commentController: CommentController
) {

    private val posts = mutableMapOf<Long, Post>()
    private val idGenerator = AtomicLong(1)

    /**
     * Creates a new post, assigns it a unique ID, and stores it in memory.
     *
     * @param post The post data to create.
     * @return The created post with its assigned ID and HTTP status 201 (Created).
     */
    @PostMapping
    fun createPost(@RequestBody post: Post): ResponseEntity<Post> {
        val id = idGenerator.getAndIncrement()
        post.id = id
        posts[id] = post
        return ResponseEntity.status(HttpStatus.CREATED).body(post)
    }

    /**
     * Retrieves a list of all posts.
     *
     * @return HTTP 200 response containing all stored posts.
     */
    @GetMapping
    fun getPosts(): ResponseEntity<List<Post>> {
        return ResponseEntity.ok(posts.values.toList())
    }

    /**
     * Retrieves a post by its unique ID.
     *
     * Returns the post with HTTP 200 if found, or HTTP 404 if no post exists with the given ID.
     *
     * @param id The unique identifier of the post to retrieve.
     * @return A ResponseEntity containing the post if found, or a 404 status if not.
     */
    @GetMapping("/{id}")
    fun getPost(@PathVariable id: Long): ResponseEntity<Post> {
        val post = posts[id]
        return if (post != null) {
            ResponseEntity.ok(post)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    /**
     * Updates an existing post with new data by its ID.
     *
     * If the post with the specified ID does not exist, returns HTTP 404 (Not Found).
     * Otherwise, replaces the post's content and returns the updated post with HTTP 200 (OK).
     *
     * @param id The ID of the post to update.
     * @param updatedPost The new post data to replace the existing post.
     * @return The updated post with HTTP 200, or HTTP 404 if the post does not exist.
     */
    @PutMapping("/{id}")
    fun updatePost(@PathVariable id: Long, @RequestBody updatedPost: Post): ResponseEntity<Post> {
        if (!posts.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
        updatedPost.id = id
        posts[id] = updatedPost
        return ResponseEntity.ok(updatedPost)
    }

    /**
     * Deletes a post by its ID.
     *
     * Removes the post with the specified ID from the in-memory store. Returns HTTP 204 (No Content) if the post was deleted, or HTTP 404 (Not Found) if the post does not exist.
     *
     * @param id The unique identifier of the post to delete.
     * @return A ResponseEntity with HTTP status 204 if successful, or 404 if the post is not found.
     */
    @DeleteMapping("/{id}")
    fun deletePost(@PathVariable id: Long): ResponseEntity<Void> {
        if (!posts.containsKey(id)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

        // 게시물과 관련된 모든 댓글 삭제
        commentController.deleteAllCommentsForPost(id)

        posts.remove(id)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }
}