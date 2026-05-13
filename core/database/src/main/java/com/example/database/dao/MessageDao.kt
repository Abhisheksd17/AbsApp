package com.example.database.dao


import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.database.entity.MessageEntity
import com.example.model.message.MessageStatus
import com.example.model.message.MessageWithUser
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    // ─────────────────────────────────────────────
    // Insert
    // ─────────────────────────────────────────────

    /**
     * Bulk insert.  OnConflictStrategy.IGNORE is intentional:
     * we never want a blind REPLACE to wipe the clientId out of a row
     * that was already linked via mapClientToServer.
     * All status transitions go through explicit UPDATE queries below.
     */
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertMessages(messages: List<MessageEntity>)

    // ─────────────────────────────────────────────
    // Upsert helpers (used by MessageRepositoryImpl.upsertIncomingMessage)
    // ─────────────────────────────────────────────

    /**
     * Returns 1 if a row with this serverId already exists, 0 otherwise.
     * Used to decide insert vs. status-update for incoming network messages.
     */
    @Query("SELECT COUNT(*) FROM messages WHERE serverId = :serverId")
    suspend fun countByServerId(serverId: Long): Int

    // ─────────────────────────────────────────────
    // Status updates — always prefer the narrowest identifier
    // ─────────────────────────────────────────────

    /**
     * Called after a successful API send response.
     * Links the local optimistic row (known by clientId) to the server row
     * (known by serverId) in a single atomic UPDATE.
     * After this call the row can be found by both identifiers.
     */
    @Query("""
        UPDATE messages
        SET    serverId = :serverId,
               status   = :status
        WHERE  clientId = :clientId
    """)
    suspend fun mapClientToServer(
        clientId: String,
        serverId: Long,
        status: MessageStatus,
    )

    /**
     * Called for WebSocket receipts (delivered / read) and for the
     * upsertIncomingMessage path when the row already exists.
     * Identified by serverId because clientId is never present in
     * socket/REST payloads for incoming messages.
     */
    @Query("""
        UPDATE messages
        SET    status = :status
        WHERE  serverId = :serverId
    """)
    suspend fun updateStatusByServerId(serverId: Long, status: MessageStatus)

    /**
     * Called when the send API call fails.
     * Identified by clientId because serverId was never assigned.
     */
    @Query("""
        UPDATE messages
        SET    status = :status
        WHERE  clientId = :clientId
    """)
    suspend fun updateStatusByClientId(clientId: String, status: MessageStatus)

    // ─────────────────────────────────────────────
    // Queries
    // ─────────────────────────────────────────────

    /**
     * Primary observable query for the chat screen.
     * Ordered by createdAt first, then localId as a tiebreaker so
     * optimistic messages appear in insertion order within the same second.
     */
    @Query("""
    SELECT m.*,
           u.name       AS senderName,
           u.profileUrl AS senderProfile
    FROM   messages m
    LEFT JOIN users u ON m.senderId = u.id
    WHERE  m.chatId = :chatId
    ORDER  BY m.createdAt ASC, m.localId ASC
""")
    fun getMessagesWithUser(chatId: Int): Flow<List<MessageWithUser>>

    // ─────────────────────────────────────────────
    // Maintenance
    // ─────────────────────────────────────────────

    @Query("DELETE FROM messages WHERE chatId = :chatId")
    suspend fun clearChat(chatId: Int)
}