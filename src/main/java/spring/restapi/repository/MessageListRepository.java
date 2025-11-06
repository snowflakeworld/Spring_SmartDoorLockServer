package spring.restapi.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import spring.restapi.model.MessageList;
import spring.restapi.model.MessageListProjection;

public interface MessageListRepository extends JpaRepository<MessageList, Long> {
    @Query(
            value = "SELECT\n" +
                    "  * \n" +
                    "FROM\n" +
                    "  (\n" +
                    "    SELECT\n" +
                    "      id,\n" +
                    "    CASE\n" +
                    "        \n" +
                    "        WHEN from_cid = 0 THEN\n" +
                    "        0 ELSE 1 \n" +
                    "      END AS isUser,\n" +
                    "      content AS msg,\n" +
                    "      date_format( create_at, '%Y-%m-%d %H:%i:%s' ) AS createAt \n" +
                    "    FROM\n" +
                    "      message_list \n" +
                    "    WHERE\n" +
                    "      ( from_cid = :cid OR to_cid = :cid ) \n" +
                    "      AND state = 3 \n" +
                    "  ) T1 \n" +
                    "ORDER BY\n" +
                    "  id DESC",
            countQuery = "SELECT\n" +
                    "  count( * ) \n" +
                    "FROM\n" +
                    "  message_list \n" +
                    "WHERE\n" +
                    "  ( from_cid = :cid OR to_cid = :cid ) \n" +
                    "  AND state = 3",
            nativeQuery = true
    )
    Page<MessageListProjection> ntGetMessageList(long cid, Pageable pageable);
}
