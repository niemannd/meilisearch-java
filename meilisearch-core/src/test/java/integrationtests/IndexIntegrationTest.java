package integrationtests;

import com.github.niemannd.meilisearch.api.MeiliError;
import com.github.niemannd.meilisearch.api.MeiliErrorException;
import com.github.niemannd.meilisearch.api.index.Index;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.UUID;
import java.util.stream.Collectors;

import static org.junit.jupiter.api.Assertions.*;

public class IndexIntegrationTest extends AbstractIT {

    @Test
    void basicUseCase() {
        String uid = UUID.randomUUID().toString();
        Index index = client.index().createIndex(uid);
        assertEquals(uid, index.getUid());
        assertNull(index.getPrimaryKey());

        index = client.index().getIndex(uid);
        assertEquals(uid, index.getUid());

        String primaryKey = "randomPrimaryKey";
        index = client.index().updateIndex(uid, primaryKey);
        assertEquals(uid, index.getUid());
        assertEquals(primaryKey, index.getPrimaryKey());
        assertTrue(client.index().deleteIndex(index.getUid()));

        Index[] allIndexes = client.index().getAllIndexes();
        assertEquals(0, allIndexes.length, "Still present: " + Arrays.stream(allIndexes).map(Index::getUid).collect(Collectors.joining(",")));

        index = client.index().createIndex(uid, primaryKey);
        assertEquals(uid, index.getUid());
        assertEquals(primaryKey, index.getPrimaryKey());
        assertTrue(client.index().deleteIndex(index.getUid()));
    }

    @Test
    void withoutPermissions() {
        key.setKey(null);
        assertThrows(MeiliErrorException.class, () -> client.index().getAllIndexes());
        assertThrows(MeiliErrorException.class, () -> client.index().createIndex(UUID.randomUUID().toString()));
        assertThrows(MeiliErrorException.class, () -> client.index().createIndex(UUID.randomUUID().toString(), "test"));
        assertThrows(MeiliErrorException.class, () -> client.index().updateIndex(UUID.randomUUID().toString(), "test"));
        assertThrows(MeiliErrorException.class, () -> client.index().getIndex(UUID.randomUUID().toString()));
        assertFalse(client.index().deleteIndex(UUID.randomUUID().toString()));
        key.setKey("3b3bf839485f90453acc6159ba18fbed673ca88523093def11a9b4f4320e44a5");
        assertThrows(MeiliErrorException.class, () -> client.index().getAllIndexes());
        assertThrows(MeiliErrorException.class, () -> client.index().createIndex(UUID.randomUUID().toString()));
        assertThrows(MeiliErrorException.class, () -> client.index().createIndex(UUID.randomUUID().toString(), "test"));
        assertThrows(MeiliErrorException.class, () -> client.index().updateIndex(UUID.randomUUID().toString(), "test"));
        assertThrows(MeiliErrorException.class, () -> client.index().getIndex(UUID.randomUUID().toString()));
        assertFalse(client.index().deleteIndex(UUID.randomUUID().toString()));
        key.setKey("8dcbb482663333d0280fa9fedf0e0c16d52185cb67db494ce4cd34da32ce2092");
        assertDoesNotThrow(() -> client.index().getAllIndexes());
        assertDoesNotThrow(() -> {
            String uid = UUID.randomUUID().toString();
            client.index().createIndex(uid);
            client.index().deleteIndex(uid);
        });
    }
}
