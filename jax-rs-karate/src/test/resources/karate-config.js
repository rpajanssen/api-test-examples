function fn() {
    karate.configure('connectTimeout', 5000);
    karate.configure('readTimeout', 5000);

    // we make some environment settings available in the context of the feature files by adding them as properties
    // to the config json object
    // fetch the baseUrl set in the integration test class
    var testBaseUrl = karate.properties['baseUrl'];
    // fetch the support folder path that will hold all supporting feature files
    var supportFolderPath = karate.properties['supportFolderPath'];

    return { testBaseUrl: testBaseUrl, supportFolderPath: supportFolderPath };
}
