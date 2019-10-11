function fn() {
    karate.configure('connectTimeout', 5000);
    karate.configure('readTimeout', 5000);

    // todo : document (see PersonCrudResourceUsingKarateIT)
    var testBaseUrl = karate.properties['baseUrl'];
    var supportFolderPath = karate.properties['supportFolderPath'];

    var config = { testBaseUrl: testBaseUrl, supportFolderPath: supportFolderPath };

    return config;
}
