package listeners;

import org.testng.IAnnotationTransformer;
import org.testng.annotations.ITestAnnotation;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.Arrays;

public class AnnotationTransformers implements IAnnotationTransformer {

    public void transform(ITestAnnotation annotation, Class testClass, Constructor testConstructor, Method testMethod) {

        annotation.setRetryAnalyzer(RetryAnalyser.class);

        // Merge "Regression" into any existing groups instead of replacing them
        String[] existingGroups = annotation.getGroups(); //Getting the array of groups present for that particular test case
        String[] mergedGroups = Arrays.copyOf(existingGroups, existingGroups.length + 1); //Creating a new Array with a bigger size
        mergedGroups[existingGroups.length] = "Regression"; //Will be adding the "Regression" to the existing array
        annotation.setGroups(mergedGroups); //For every test annotation, the groups will be updated
    }

}
