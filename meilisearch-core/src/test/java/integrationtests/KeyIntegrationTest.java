package integrationtests;

import com.github.niemannd.meilisearch.api.MeiliErrorException;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

public class KeyIntegrationTest extends AbstractIT {
    @Test
    void basicUseCase() {
        Map<String, String> keyMap = client.keys().get();
        assertNotNull(keyMap);
        assertEquals(2, keyMap.keySet().size());
        assertIterableEquals(new HashSet<String>() {{
            add("public");
            add("private");
        }}, keyMap.keySet());
        assertEquals("8dcbb482663333d0280fa9fedf0e0c16d52185cb67db494ce4cd34da32ce2092", keyMap.get("private"));
        assertEquals("3b3bf839485f90453acc6159ba18fbed673ca88523093def11a9b4f4320e44a5", keyMap.get("public"));
    }

    @Test
    void withoutPermission() {
        key.setKey("3b3bf839485f90453acc6159ba18fbed673ca88523093def11a9b4f4320e44a5");
        assertThrows(MeiliErrorException.class, () -> client.keys().get());
        key.setKey("8dcbb482663333d0280fa9fedf0e0c16d52185cb67db494ce4cd34da32ce2092");
        assertThrows(MeiliErrorException.class, () -> client.keys().get());
        key.setKey("masterKey");
        assertDoesNotThrow(() -> client.keys().get());
    }
}
