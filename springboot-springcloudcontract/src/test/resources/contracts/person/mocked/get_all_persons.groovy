package contracts.person.mocked

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should get a list of all persons"

    request {
        url "/api/person"
        method GET()
    }

    response {
        status OK()
        headers {
            contentType applicationJson()
        }
        body([  items: [
                [
                        id       : 1,
                        firstName: "Jan",
                        lastName : "Janssen"
                ], [
                        id       : 2,
                        firstName: "Pieter",
                        lastName : "Pietersen"
                ], [
                        id       : 3,
                        firstName: "Erik",
                        lastName : "Eriksen"
                ]
        ]])
    }
}
