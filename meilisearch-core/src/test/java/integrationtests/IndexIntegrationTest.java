package integrationtests;

import io.github.niemannd.meilisearch.api.MeiliException;
import io.github.niemannd.meilisearch.api.index.Index;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class IndexIntegrationTest extends AbstractIT {

    @Test
    void basicUseCase() {
        String uid = UUID.randomUUID().toString();
        Index index = client.indexes().createIndex(uid);
        assertEquals(uid, index.getUid());
        assertNull(index.getPrimaryKey());

        index = client.indexes().getIndex(uid);
        assertEquals(uid, index.getUid());

        String primaryKey = "randomPrimaryKey";
        index = client.indexes().updateIndex(uid, primaryKey);
        assertEquals(uid, index.getUid());
        assertEquals(primaryKey, index.getPrimaryKey());
        assertTrue(client.indexes().deleteIndex(index.getUid()));

        Index[] allIndexes = client.indexes().getAllIndexes();
        assertEquals(0, allIndexes.length, "Still present: " + Arrays.stream(allIndexes).map(Index::getUid).collect(Collectors.joining(",")));

        index = client.indexes().createIndex(uid, primaryKey);
        assertEquals(uid, index.getUid());
        assertEquals(primaryKey, index.getPrimaryKey());
        assertTrue(client.indexes().deleteIndex(index.getUid()));
    }

    @Test
    void withoutPermissions() {
        key.setKey(null);
        assertThrows(MeiliException.class, () -> client.indexes().getAllIndexes());
        assertThrows(MeiliException.class, () -> client.indexes().createIndex(UUID.randomUUID().toString()));
        assertThrows(MeiliException.class, () -> client.indexes().createIndex(UUID.randomUUID().toString(), "test"));
        assertThrows(MeiliException.class, () -> client.indexes().updateIndex(UUID.randomUUID().toString(), "test"));
        assertThrows(MeiliException.class, () -> client.indexes().getIndex(UUID.randomUUID().toString()));
        assertThrows(MeiliException.class, () -> client.indexes().deleteIndex(UUID.randomUUID().toString()));
        key.setKey("3b3bf839485f90453acc6159ba18fbed673ca88523093def11a9b4f4320e44a5");
        assertThrows(MeiliException.class, () -> client.indexes().getAllIndexes());
        assertThrows(MeiliException.class, () -> client.indexes().createIndex(UUID.randomUUID().toString()));
        assertThrows(MeiliException.class, () -> client.indexes().createIndex(UUID.randomUUID().toString(), "test"));
        assertThrows(MeiliException.class, () -> client.indexes().updateIndex(UUID.randomUUID().toString(), "test"));
        assertThrows(MeiliException.class, () -> client.indexes().getIndex(UUID.randomUUID().toString()));
        assertThrows(MeiliException.class, () -> client.indexes().deleteIndex(UUID.randomUUID().toString()));
        key.setKey("8dcbb482663333d0280fa9fedf0e0c16d52185cb67db494ce4cd34da32ce2092");
        assertDoesNotThrow(() -> client.indexes().getAllIndexes());
        assertDoesNotThrow(() -> {
            String uid = UUID.randomUUID().toString();
            client.indexes().createIndex(uid);
            client.indexes().deleteIndex(uid);
        });
    }
}
