package dev.furkankavak.istemenu.model

import com.google.gson.annotations.SerializedName

data class ReactionResponse(
    @SerializedName("success") val success: Boolean,
    @SerializedName("message") val message: String,
    @SerializedName("data") val data: ReactionData
)

data class ReactionData(
    @SerializedName("action") val action: String = "",            // "created" or "removed"
    @SerializedName("user_reaction") val userReaction: String? = null,  // "like", "dislike" or null
    @SerializedName("reaction_type") val reactionType: String? = null, // Alternative name for user_reaction
    @SerializedName("likes_count") val likes: Int = 0,
    @SerializedName("dislikes_count") val dislikes: Int = 0,

    // Alternative field names that might be used in the API
    @SerializedName("likesCount") val likesAlt: Int = 0,
    @SerializedName("dislikesCount") val dislikesAlt: Int = 0
) {
    // Get the most appropriate likes count
    val effectiveLikes: Int
        get() = if (likes > 0) likes else likesAlt

    // Get the most appropriate dislikes count
    val effectiveDislikes: Int
        get() = if (dislikes > 0) dislikes else dislikesAlt

    // Get the most appropriate user reaction
    val effectiveUserReaction: String?
        get() = userReaction ?: reactionType
}