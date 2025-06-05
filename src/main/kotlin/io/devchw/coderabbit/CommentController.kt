package io.devchw.coderabbit

import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import java.util.concurrent.atomic.AtomicLong

/**
 * @author DevCHW
 * @since 2025-06-05
 */
@RestController
@RequestMapping("/api/board")
class CommentController {

    private val comments = mutableMapOf<Long, Comment>()
    private val idGenerator = AtomicLong(1)

    // 댓글 생성
    @PostMapping("/{postId}/comments")
    fun createComment(
        @PathVariable postId: Long, 
        @RequestBody comment: Comment
    ): ResponseEntity<Comment> {
        val id = idGenerator.getAndIncrement()
        comment.id = id
        comment.postId = postId
        comments[id] = comment
        return ResponseEntity.status(HttpStatus.CREATED).body(comment)
    }

    // 게시물의 모든 댓글 조회
    @GetMapping("/{postId}/comments")
    fun getCommentsByPost(@PathVariable postId: Long): ResponseEntity<List<Comment>> {
        val postComments = comments.values.filter { it.postId == postId }
        return ResponseEntity.ok(postComments)
    }

    // 댓글 상세 조회
    @GetMapping("/comments/{commentId}")
    fun getComment(@PathVariable commentId: Long): ResponseEntity<Comment> {
        val comment = comments[commentId]
        return if (comment != null) {
            ResponseEntity.ok(comment)
        } else {
            ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
    }

    // 댓글 수정
    @PutMapping("/comments/{commentId}")
    fun updateComment(
        @PathVariable commentId: Long, 
        @RequestBody updatedComment: Comment
    ): ResponseEntity<Comment> {
        val existingComment = comments[commentId]
        if (existingComment == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }

        // 기존 postId 유지
        updatedComment.id = commentId
        updatedComment.postId = existingComment.postId
        comments[commentId] = updatedComment
        return ResponseEntity.ok(updatedComment)
    }

    // 댓글 삭제
    @DeleteMapping("/comments/{commentId}")
    fun deleteComment(@PathVariable commentId: Long): ResponseEntity<Void> {
        if (!comments.containsKey(commentId)) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).build()
        }
        comments.remove(commentId)
        return ResponseEntity.status(HttpStatus.NO_CONTENT).build()
    }

    // 게시물의 모든 댓글 삭제 (게시물이 삭제될 때 호출)
    fun deleteAllCommentsForPost(postId: Long) {
        val commentIdsToRemove = comments.values
            .filter { it.postId == postId }
            .mapNotNull { it.id }

        commentIdsToRemove.forEach { comments.remove(it) }
    }
}
