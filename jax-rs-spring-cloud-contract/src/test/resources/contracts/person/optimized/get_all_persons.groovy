package contracts.person.optimized

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should get a list of all persons"

    request {
        url "/person/all"
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
