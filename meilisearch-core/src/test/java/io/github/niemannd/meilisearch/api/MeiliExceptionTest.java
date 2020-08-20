package io.github.niemannd.meilisearch.api;

import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertFalse;

class MeiliExceptionTest {

    @Test
    void structureTest() {
        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new GetterTester())
                .build();

        validator.validate(PojoClassFactory.getPojoClass(MeiliAPIException.class));
        validator.validate(PojoClassFactory.getPojoClass(MeiliException.class));
        validator.validate(PojoClassFactory.getPojoClass(MeiliJSONException.class));

        validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new GetterTester())
                .with(new SetterTester())
                .build();
        validator.validate(PojoClassFactory.getPojoClass(MeiliError.class));

        MeiliAPIException meiliAPIException = new MeiliAPIException("Test",null);
        assertFalse(meiliAPIException.hasError());
    }

}