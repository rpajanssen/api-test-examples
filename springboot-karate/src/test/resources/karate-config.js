function fn() {
    karate.configure('connectTimeout', 5000);
    karate.configure('readTimeout', 5000);

    // todo : document
    var testBaseUrl = karate.properties['baseUrl'];

    var config = { testBaseUrl: testBaseUrl };

    return config;
}
