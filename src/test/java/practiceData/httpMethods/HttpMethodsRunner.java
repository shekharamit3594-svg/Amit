package practiceData.httpMethods;

import com.intuit.karate.junit5.Karate;

public class HttpMethodsRunner {

    @Karate.Test
    Karate patchOperation() {
        return Karate.run("PatchOperation.feature").relativeTo(getClass());
    }

    @Karate.Test
    Karate deleteOperation() {
        return Karate.run("DeleteOperation.feature").tags("@CreatePatchDelete").relativeTo(getClass());
    }

    @Karate.Test
    Karate putVsPatch() {
        return Karate.run("PutVsPatch.feature").relativeTo(getClass());
    }
}
