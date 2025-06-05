package io.devchw.coderabbit

/**
 * @author DevCHW
 * @since 2025-06-05
 */
data class Comment(
    var id: Long? = null,
    var postId: Long,
    var content: String
)
