package io.github.niemannd.meilisearch.api.index;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

class IndexEntityTest {

    @Test
    void structureTest() {
        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new GetterTester())
                .build();

        validator.validate(PojoClassFactory.getPojoClass(Index.class));
        validator.validate(PojoClassFactory.getPojoClass(Settings.class));
    }

    @Test
    void settingsSynonyms() {
        Settings settings = new Settings();
        assertThat(settings.getSynonyms(), anEmptyMap());
        settings.addSynonym("wolverine","xmen", "logan");
        assertThat(settings.getSynonyms(), Matchers.aMapWithSize(1));
        assertThat(settings.getSynonyms().keySet(), containsInAnyOrder("wolverine"));
        assertThat(settings.getSynonyms().get("wolverine"), containsInAnyOrder("xmen", "logan"));
    }

    @Test
    void constructorTest() {
        Index index = new Index("justauid");
        assertEquals("justauid", index.getUid());
        assertNull(index.getPrimaryKey());
        assertNull(index.getName());
        assertNull(index.getCreatedAt());
        assertNull(index.getUpdatedAt());

        index = new Index("justauid", "justaprimarykey");
        assertEquals("justauid", index.getUid());
        assertEquals("justaprimarykey", index.getPrimaryKey());
        assertNull(index.getName());
        assertNull(index.getCreatedAt());
        assertNull(index.getUpdatedAt());

        index = new Index();
        assertNull(index.getUid());
        assertNull(index.getPrimaryKey());
        assertNull(index.getName());
        assertNull(index.getCreatedAt());
        assertNull(index.getUpdatedAt());
    }
}