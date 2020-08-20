package io.github.niemannd.meilisearch.api.documents;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Test;

import java.util.Collections;

import static org.junit.jupiter.api.Assertions.*;

class DocumentEntityTest {

    @Test
    void structureTest() {
        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new GetterTester())
                .build();

        validator.validate(PojoClassFactory.getPojoClass(SearchRequest.class));
        validator.validate(PojoClassFactory.getPojoClass(SearchResponse.class));

        validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new GetterTester())
                .with(new SetterTester())
                .build();
        validator.validate(PojoClassFactory.getPojoClass(Type.class));
        validator.validate(PojoClassFactory.getPojoClass(Update.class));
    }

    @Test
    void searchRequestBuilder() {
        SearchRequest build = new SearchRequestBuilder()
                .setAttributesToRetrieve(Collections.singletonList("all"))
                .setAttributesToCrop(Collections.singletonList("overview"))
                .setAttributesToHighlight(Collections.singletonList("id"))
                .setCropLength(250)
                .setFilters("release_date > 50")
                .setLimit(20)
                .setOffset(5)
                .setMatches(true)
                .setQ("Shazam")
                .build();

        assertEquals("Shazam", build.getQ());
        assertEquals("release_date > 50", build.getFilters());
        assertNotNull(build.getAttributesToCrop());
        assertEquals(1, build.getAttributesToCrop().size());
        assertEquals("overview", build.getAttributesToCrop().get(0));
        assertNotNull(build.getAttributesToHighlight());
        assertEquals(1, build.getAttributesToHighlight().size());
        assertEquals("id", build.getAttributesToHighlight().get(0));
        assertNotNull(build.getAttributesToRetrieve());
        assertEquals(1, build.getAttributesToRetrieve().size());
        assertEquals("all", build.getAttributesToRetrieve().get(0));
        assertEquals(250, build.getCropLength());
        assertEquals(20, build.getLimit());
        assertEquals(5, build.getOffset());
    }
}