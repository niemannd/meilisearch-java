package com.github.niemannd.meilisearch.api.documents;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Test;

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
}