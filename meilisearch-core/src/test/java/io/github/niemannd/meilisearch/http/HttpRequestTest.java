package io.github.niemannd.meilisearch.http;


import com.openpojo.reflection.impl.PojoClassFactory;
import com.openpojo.validation.Validator;
import com.openpojo.validation.ValidatorBuilder;
import com.openpojo.validation.rule.impl.GetterMustExistRule;
import com.openpojo.validation.rule.impl.SetterMustExistRule;
import com.openpojo.validation.test.impl.GetterTester;
import com.openpojo.validation.test.impl.SetterTester;
import io.github.niemannd.meilisearch.http.request.BasicHttpRequest;
import org.junit.jupiter.api.Test;

public class HttpRequestTest {

    @Test
    void entityTest() {
        Validator validator = ValidatorBuilder.create()
                .with(new GetterMustExistRule())
                .with(new SetterMustExistRule())
                .with(new GetterTester())
                .with(new SetterTester())
                .build();
        validator.validate(PojoClassFactory.getPojoClass(BasicHttpRequest.class));
    }

}
