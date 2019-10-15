package contracts.person.mocked

import org.springframework.cloud.contract.spec.Contract

Contract.make {
    description "should add a person"

    request {
        url "/api/person"
        method POST()
        headers {
            contentType applicationJson()
        }
        body (
                id: null,
                firstName: "Katy",
                lastName: "Perry"
        )
    }

    response {
        status CREATED()
        headers {
            contentType applicationJson()
        }
        body (
                id: 1001,
                firstName: "Katy",
                lastName: "Perry"
        )
    }
}
