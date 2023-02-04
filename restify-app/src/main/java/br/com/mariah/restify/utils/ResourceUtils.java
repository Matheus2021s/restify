package br.com.mariah.restify.utils;

public class ResourceUtils {

    public static final String SRC_MAIN_JAVA = "src/main/java";
    public static final String SRC_TEST_JAVA = "src/test/java";
    public static final String USER_DIR = "user.dir";
    public static final String $_RESOURCES_BASE_PACKAGE = "${resources.base-package}";

    private String basePackage = "br.com.mariah.restapi";


    public String getBasePackage() {
        return this.basePackage;
    }

    public String getBasePackageFolder() {
        return this.basePackage.replaceAll("\\.", "/");
    }

    public String getJavaResourcesPath() {
        return SRC_MAIN_JAVA;
    }

    public String getTestResourcesPath() {
        return SRC_TEST_JAVA;
    }

    public String getApplicationBasePath() {
        return System.getProperty(USER_DIR);
    }


}
